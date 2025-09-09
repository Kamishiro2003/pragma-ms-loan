package co.com.loan.sqs.sender;

import co.com.loan.model.gateways.MessageBrokerGateway;
import co.com.loan.model.message.MessageBody;
import co.com.loan.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender implements MessageBrokerGateway {

  private final SQSSenderProperties properties;
  private final SqsAsyncClient client;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> sendMessage(MessageBody messageBody) {
    return Mono
        .fromCallable(() -> buildRequest(toJson(messageBody)))
        .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
        .doOnNext(response -> log.debug("Message sent to SQS with ID {}", response.messageId()))
        .onErrorResume(error -> {
          log.error("Recovering from error while sending message: {}", error.getMessage(), error);
          return Mono.empty();
        })
        .then();
  }

  private SendMessageRequest buildRequest(String message) {
    return SendMessageRequest
        .builder()
        .queueUrl(properties.queueUrl())
        .messageBody(message)
        .build();
  }

  private String toJson(MessageBody messageBody) {
    try {
      return objectMapper.writeValueAsString(messageBody);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error serializing MessageBody", e);
    }
  }
}

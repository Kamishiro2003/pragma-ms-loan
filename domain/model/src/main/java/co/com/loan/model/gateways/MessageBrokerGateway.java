package co.com.loan.model.gateways;

import co.com.loan.model.message.MessageBody;
import reactor.core.publisher.Mono;

/**
 * Gateway interface for message broker operations.
 */
public interface MessageBrokerGateway {

  /**
   * Sends a message through the message broker.
   *
   * @param messageBody the message content to be sent
   * @return a Mono that completes when the message is sent successfully
   */
  Mono<Void> sendMessage(MessageBody messageBody);
}

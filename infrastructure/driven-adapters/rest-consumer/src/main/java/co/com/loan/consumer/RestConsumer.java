package co.com.loan.consumer;

import co.com.loan.model.error.ErrorCode;
import co.com.loan.model.exception.ObjectNotFoundException;
import co.com.loan.model.gateways.UserGateway;
import co.com.loan.model.user.User;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {

  private final WebClient client;
  private final UserMapper mapper;

  @Override
  @CircuitBreaker(name = "userService")
  public Mono<User> getUserByDocumentId(String documentId, String token) {
    return client.get()
        .uri(uriBuilder -> uriBuilder.path("/api/v1/usuarios")
            .queryParam("documentId", documentId)
            .build())
        .header(HttpHeaders.AUTHORIZATION, "bearer " + token)
        .retrieve()
        .bodyToMono(UserResponse.class)
        .map(mapper::toDomain)
        .onErrorResume(WebClientResponseException.NotFound.class, ex -> {
          log.warn("User not found with documentId={}", documentId);
          return Mono.error(new ObjectNotFoundException(ErrorCode.USER_NOT_FOUND_BY_DOCUMENT_ID,
              documentId));
        });
  }

}

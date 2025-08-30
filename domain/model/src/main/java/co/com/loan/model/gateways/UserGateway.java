package co.com.loan.model.gateways;

import co.com.loan.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {

  Mono<User> getUserByDocumentId(String documentId);
}

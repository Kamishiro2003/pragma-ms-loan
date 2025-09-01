package co.com.loan.model.gateways;

import co.com.loan.model.user.User;
import reactor.core.publisher.Mono;

/**
 * Gateway interface for accessing user data from external sources or services.
 */
public interface UserGateway {

  /**
   * Retrieves a user by their document ID, using the provided authentication token.
   *
   * @param documentId the document ID of the user
   * @param token      the authentication token for authorization
   * @return a Mono emitting the User if found, or an error if not
   */
  Mono<User> getUserByDocumentId(String documentId, String token);
}

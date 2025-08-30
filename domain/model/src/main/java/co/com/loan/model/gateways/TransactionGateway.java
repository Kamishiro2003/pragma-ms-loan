package co.com.loan.model.gateways;

import reactor.core.publisher.Mono;

/**
 * Gateway interface for managing transactions.
 */
public interface TransactionGateway {

  /**
   * Executes a given action within a transaction.
   *
   * @param action the action to execute
   * @param <T>    the type of the result
   * @return a Mono containing the result of the action
   */
  <T> Mono<T> execute(Mono<T> action);
}

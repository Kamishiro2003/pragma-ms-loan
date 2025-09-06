package co.com.loan.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import co.com.loan.model.gateways.LoanRepository;
import co.com.loan.model.gateways.LoanTypeRepository;
import co.com.loan.model.gateways.TransactionGateway;
import co.com.loan.model.gateways.UserGateway;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

class UseCasesConfigTest {

  @Test
  void testUseCaseBeansExist() {
    try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
        TestConfig.class)) {
      String[] beanNames = context.getBeanDefinitionNames();

      boolean useCaseBeanFound = false;
      for (String beanName : beanNames) {
        if (beanName.endsWith("UseCase")) {
          useCaseBeanFound = true;
          break;
        }
      }

      assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
    }
  }

  @Configuration
  @Import(UseCasesConfig.class)
  static class TestConfig {

    @Bean
    public MyUseCase myUseCase() {
      return new MyUseCase();
    }

    @Bean
    public LoanRepository loanRepository() {
      return Mockito.mock(LoanRepository.class);
    }

    @Bean
    public LoanTypeRepository loanTypeRepository() {
      return Mockito.mock(LoanTypeRepository.class);
    }

    @Bean
    public UserGateway userGateway() {
      return Mockito.mock(UserGateway.class);
    }

    @Bean
    public TransactionGateway transactionGateway() {
      return Mockito.mock(TransactionGateway.class);
    }
  }

  static class MyUseCase {

    public String execute() {
      return "MyUseCase Test";
    }
  }
}
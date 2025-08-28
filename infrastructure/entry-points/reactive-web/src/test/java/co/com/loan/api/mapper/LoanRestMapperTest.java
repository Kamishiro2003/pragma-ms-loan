package co.com.loan.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.api.model.response.LoanRestResponse;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.StatusEnum;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class LoanRestMapperTest {

  private final LoanRestMapper mapper = new LoanRestMapper();

  @Test
  void toResponse_mapsLoanToLoanRestResponse() {
    // Arrange
    Loan loan = Loan.builder()
        .id("loanId")
        .amount(BigDecimal.valueOf(1000))
        .term(12)
        .email("test@mail.com")
        .idStatus(StatusEnum.PENDING.getId())
        .typeId("typeId")
        .build();

    // Act
    Mono<LoanRestResponse> result = mapper.toResponse(loan);

    // Assert
    StepVerifier.create(result).assertNext(response -> {
      assertThat(response.getId()).isEqualTo("loanId");
      assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
      assertThat(response.getTerm()).isEqualTo(12);
      assertThat(response.getEmail()).isEqualTo("test@mail.com");
      assertThat(response.getStatus()).isEqualTo(StatusEnum.PENDING.getDisplayName());
      assertThat(response.getTypeId()).isEqualTo("typeId");
    }).verifyComplete();
  }

  @Test
  void toLoanCreate_mapsRequestToLoanCreate() {
    // Arrange
    LoanCreateRequest request = LoanCreateRequest.builder()
        .amount(BigDecimal.valueOf(2000))
        .term(24)
        .documentId("doc123")
        .typeId("typeABC")
        .build();

    // Act
    LoanCreate loanCreate = mapper.toLoanCreate(request);

    // Assert
    assertThat(loanCreate.amount()).isEqualTo(BigDecimal.valueOf(2000));
    assertThat(loanCreate.term()).isEqualTo(24);
    assertThat(loanCreate.documentId()).isEqualTo("doc123");
    assertThat(loanCreate.typeId()).isEqualTo("typeABC");
  }
}
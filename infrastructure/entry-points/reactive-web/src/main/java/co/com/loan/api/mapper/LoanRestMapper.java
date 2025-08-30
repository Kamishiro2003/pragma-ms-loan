package co.com.loan.api.mapper;

import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.api.model.response.LoanRestResponse;
import co.com.loan.model.loan.Loan;
import co.com.loan.model.loan.LoanCreate;
import co.com.loan.model.loan.StatusEnum;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LoanRestMapper {

  public Mono<LoanRestResponse> toResponse(Loan data) {
    return StatusEnum.fromId(data.getIdStatus())
        .map(StatusEnum::getDisplayName)
        .map(displayName -> LoanRestResponse.builder()
            .id(data.getId())
            .amount(data.getAmount())
            .term(data.getTerm())
            .email(data.getEmail())
            .status(displayName)
            .typeId(data.getTypeId())
            .build());
  }

  public LoanCreate toLoanCreate(LoanCreateRequest request) {
    return new LoanCreate(request.getAmount(),
        request.getTerm(),
        request.getDocumentId(),
        request.getTypeId());
  }
}

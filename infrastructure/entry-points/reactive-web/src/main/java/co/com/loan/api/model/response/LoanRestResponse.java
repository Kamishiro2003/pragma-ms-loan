package co.com.loan.api.model.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the response model for loan information in the REST API.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRestResponse {

  private String id;
  private BigDecimal amount;
  private Integer term;
  private String email;
  private String status;
  private String typeId;
}

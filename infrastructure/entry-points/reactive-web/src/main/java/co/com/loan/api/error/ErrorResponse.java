package co.com.loan.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a standardized error response structure for API responses.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class ErrorResponse {

  private String timestamp;
  private String path;
  private int status;
  private String error;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String requestId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> details;
}


package co.com.loan.api.model.request;

import co.com.loan.model.loan.StatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanRequestList {

  @NotNull(message = "El estado es obligatorio")
  private StatusEnum status;

  @NotNull(message = "El número de la pagina es obligatorio")
  private Integer page;

  @NotNull(message = "El tamaño de la pagina es obligatorio")
  private Integer size;
}

package co.com.loan.api.model.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request model for creating a new loan.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanCreateRequest {

  @NotNull(message = "El monto es obligatorio")
  @Digits(integer = 8,
      fraction = 2,
      message = "El monto debe tener como máximo 8 dígitos enteros y 2 decimales"
  )
  private BigDecimal amount;

  @NotNull(message = "El plazo es obligatorio")
  private Integer term;

  @NotBlank(message = "El documento de identidad es obligatorio")
  @Size(max = 30, message = "El documento de identidad debe tener menos de 30 caracteres")
  private String documentId;

  @NotBlank(message = "El identificador del tipo de préstamo es obligatorio")
  @Size(max = 50,
      message = "El identificador del tipo de préstamo debe tener menos de 50 caracteres"
  )
  private String typeId;
}

package co.com.loan.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageEnum {
  DEFAULT(
      "Hola,%nTu solicitud de préstamo por un monto de $%,.2f ha sido %s.%n¡Gracias por confiar en nosotros!"),
  DEFAULT_SUBJECT("Estado de tu solicitud de préstamo");
  private final String message;

}

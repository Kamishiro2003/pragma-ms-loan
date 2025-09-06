package co.com.loan.r2dbc.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("tipo_prestamo")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanTypeEntity {

  @Id
  @Column("id_tipo_prestamo")
  private String id;

  @Column("nombre")
  private String name;

  @Column("monto_minimo")
  private BigDecimal minAmount;

  @Column("monto_maximo")
  private BigDecimal maxAmount;

  @Column("tasa_interes")
  private BigDecimal interestRate;

  @Column("validacion_automatica")
  private Boolean automaticValidation;
}

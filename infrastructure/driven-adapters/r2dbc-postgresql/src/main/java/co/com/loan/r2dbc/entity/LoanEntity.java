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
@Table("solicitud")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanEntity {

  @Id
  @Column("id_solicitud")
  private String id;

  @Column("monto")
  private BigDecimal amount;

  @Column("plazo")
  private Integer term;

  @Column("email")
  private String email;

  @Column("id_estado")
  private Integer idStatus;

  @Column("id_tipo_prestamo")
  private String typeId;
}

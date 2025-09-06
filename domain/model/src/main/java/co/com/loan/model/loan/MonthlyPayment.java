package co.com.loan.model.loan;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class MonthlyPayment {

  private MonthlyPayment() {
  }

  public static BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal annualRate,
      int months) {

    BigDecimal monthlyRate = annualRate
        .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP)
        .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

    // (1 + i)
    BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);

    // (1 + i)^(-n)
    BigDecimal pow = BigDecimal.ONE.divide(
        onePlusRate.pow(months, MathContext.DECIMAL64), 10,
        RoundingMode.HALF_UP);

    // 1 - (1 + i)^(-n)
    BigDecimal denominator = BigDecimal.ONE.subtract(pow);

    // C0 * i / (1 - (1+i)^(-n))
    return amount
        .multiply(monthlyRate)
        .divide(denominator, 2, RoundingMode.HALF_UP);
  }

}

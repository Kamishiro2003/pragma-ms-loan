package co.com.loan.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for loan-related paths in the application.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "loan.paths")
public class LoanPath {

  private String loan;
}

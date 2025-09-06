package co.com.loan.api.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Pragma MS-Solicitud API",
    version = "1.0.0",
    description = "API for loan management.",
    license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
    )
)
)
public class OpenApiConfig {

}

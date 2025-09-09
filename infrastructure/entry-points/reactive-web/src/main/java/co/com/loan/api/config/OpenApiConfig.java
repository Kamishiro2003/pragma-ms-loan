package co.com.loan.api.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Pragma MS-Solicitud API",
    version = "1.0.0",
    description = "API for loan management.",
    license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
    )),
    security = @SecurityRequirement(
        name = "Security Token"
    )
)
@SecurityScheme(
    name = "Security Token",
    description = "Access Token",
    type = SecuritySchemeType.HTTP,
    paramName = HttpHeaders.AUTHORIZATION,
    in = SecuritySchemeIn.HEADER,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class OpenApiConfig {

}

package co.com.loan.api.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import co.com.loan.api.config.LoanPath;
import co.com.loan.api.error.ErrorResponse;
import co.com.loan.api.error.GlobalErrorWebFilter;
import co.com.loan.api.handler.LoanHandler;
import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.api.model.response.LoanRestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class LoanRouterRest {

  private final LoanPath loanPath;
  private final LoanHandler loanHandler;
  private final GlobalErrorWebFilter globalErrorWebFilter;

  @Bean
  @RouterOperation(method = RequestMethod.POST,
      beanClass = LoanHandler.class,
      beanMethod = "listenCreateLoan",
      operation = @Operation(operationId = "createLoan",
          summary = "Crea una nueva solicitud",
          description = "Recibe datos de la solicitud y devuelve la solicitud creada",
          requestBody = @RequestBody(required = true,
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = LoanCreateRequest.class)
              )
          ),
          responses = {@ApiResponse(responseCode = "201",
              description = "Solicitud creado correctamente",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = LoanRestResponse.class)
              )
          ), @ApiResponse(responseCode = "400",
              description = "Parámetros inválidos o faltantes",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          )}
      )
  )
  public RouterFunction<ServerResponse> loanRouterFunction() {
    return route(POST(loanPath.getLoan()), loanHandler::listenCreateLoan).filter(
        globalErrorWebFilter);
  }
}

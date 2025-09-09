package co.com.loan.api.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import co.com.loan.api.error.ErrorResponse;
import co.com.loan.api.error.GlobalErrorWebFilter;
import co.com.loan.api.handler.LoanHandler;
import co.com.loan.api.model.request.LoanCreateRequest;
import co.com.loan.api.model.response.LoanRestResponse;
import co.com.loan.model.page.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class LoanRouterRest {

  private static final String PATH = "/api/v1/solicitud";

  private final LoanHandler loanHandler;
  private final GlobalErrorWebFilter globalErrorWebFilter;

  @Bean
  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      beanClass = LoanHandler.class,
      path = PATH,
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
  ), @RouterOperation(method = RequestMethod.GET,
      beanClass = LoanHandler.class,
      path = PATH,
      beanMethod = "listenFindLoansByIdStatus",
      operation = @Operation(operationId = "findLoansByIdStatus",
          summary = "Recupera la lista de solicitudes por estado",
          description = "Recibe datos para filtrar y devuelve la lista de solicitudes",
          parameters = {@Parameter(name = "idStatus",
              in = ParameterIn.QUERY,
              required = true,
              description = "Estado de las solicitudes a recuperar",
              schema = @Schema(type = "integer")
          ), @Parameter(name = "page",
              in = ParameterIn.QUERY,
              required = false,
              description = "Número de página a recuperar (por defecto 0)",
              schema = @Schema(type = "integer", defaultValue = "0")
          ), @Parameter(name = "size",
              in = ParameterIn.QUERY,
              required = false,
              description = "Cantidad de registros por página (por defecto 10)",
              schema = @Schema(type = "integer", defaultValue = "10")
          )},
          responses = {@ApiResponse(responseCode = "200",
              description = "Lista de solicitudes recuperada",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = PageResponse.class)
              )
          ), @ApiResponse(responseCode = "400",
              description = "Formato invalido",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          )}
      )
  ), @RouterOperation(method = RequestMethod.PUT,
      beanClass = LoanHandler.class,
      path = PATH,
      beanMethod = "listenUpdateLoanStatusById",
      operation = @Operation(operationId = "updateLoanStatusById",
          summary = "Actualiza el estado de una solicitud",
          description = "Permite actualizar el estado de un préstamo por su idLoan",
          parameters = {@Parameter(name = "idLoan",
              in = ParameterIn.QUERY,
              required = true,
              description = "ID de la solicitud a actualizar",
              schema = @Schema(type = "string")
          ), @Parameter(name = "idStatus",
              in = ParameterIn.QUERY,
              required = true,
              description = "Nuevo estado de la solicitud",
              schema = @Schema(type = "integer")
          )},
          responses = {
              @ApiResponse(responseCode = "204", description = "Estado actualizado correctamente"
              ), @ApiResponse(responseCode = "400",
              description = "Parámetros inválidos o faltantes",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          ), @ApiResponse(responseCode = "404",
              description = "Solicitud no encontrada",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = ErrorResponse.class)
              )
          )}
      )
  )}
  )
  public RouterFunction<ServerResponse> loanRouterFunction() {
    return route(POST(PATH), loanHandler::listenCreateLoan)
        .andRoute(GET(PATH), loanHandler::listenFindLoansByIdStatus)
        .andRoute(PUT(PATH), loanHandler::listenUpdateLoanStatusById)
        .filter(globalErrorWebFilter);
  }
}

package com.acme.zulieferer.graphql;

import com.acme.zulieferer.service.NotFoundException;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import static org.springframework.graphql.execution.ErrorType.NOT_FOUND;

@ControllerAdvice
public class ExceptionHandler {
    @GraphQlExceptionHandler
    GraphQLError onNotFound(final NotFoundException ex) {
        final var id = ex.getId();
        final var message = id == null
            ? STR."Kein Zulieferer gefunden: suchkriterien=\{ex.getQueryParams()}"
            : STR."Kein Zulieferer mit der ID \{id} gefunden";
        return GraphQLError.newError()
            .errorType(NOT_FOUND)
            .message(message)
            .build();
    }


}

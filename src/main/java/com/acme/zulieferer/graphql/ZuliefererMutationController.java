package com.acme.zulieferer.graphql;

import com.acme.zulieferer.entity.Zulieferer;
import com.acme.zulieferer.service.ConstraintViolationsException;
import com.acme.zulieferer.service.EmailExistsException;
import com.acme.zulieferer.service.ZuliefererWriteService;
import graphql.GraphQLError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.StringTemplate.STR;
import static org.springframework.graphql.execution.ErrorType.BAD_REQUEST;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ZuliefererMutationController {
    private final ZuliefererWriteService service;
    private final ZuliefererInputMapper mapper;

    /**
     * Einen neuen Zulieferer anlegen.
     *
     * @param input Die Eingabedaten für einen neuen Zulieferer
     * @return Die generierte ID für den neuen Zulieferer als Payload
     */
    @MutationMapping
    CreatePayload create(@Argument final ZuliefererInput input) {
        log.debug("create: input={}", input);
        final var zuliefererNew = mapper.toZulieferer(input);
        final var id = service.create(zuliefererNew).getId();
        log.debug("create: id={}", id);
        return new CreatePayload(id);
    }

    @GraphQlExceptionHandler
    GraphQLError onEmailExists(final EmailExistsException ex) {
        final List<Object> path = List.of("input", "email"); // NOSONAR
        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message(STR."Die Emailadresse \{ex.getEmail()} existiert bereits.")
            .path(path)
            .build();
    }

    @GraphQlExceptionHandler
    GraphQLError onDateTimeParseException(final DateTimeParseException ex) {
        final List<Object> path = List.of("input", "geburtsdatum");
        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message(STR."Das Datum \{ex.getParsedString()} ist nicht korrekt.")
            .path(path)
            .build();
    }

    @GraphQlExceptionHandler
    Collection<GraphQLError> onConstraintViolations(final ConstraintViolationsException ex) {
        return ex.getViolations()
            .stream()
            .map(this::violationToGraphQLError)
            .collect(Collectors.toList());
    }

    private GraphQLError violationToGraphQLError(final ConstraintViolation<Zulieferer> violation) {
        final List<Object> path = new ArrayList<>(5);
        path.add("input");
        for (final Path.Node node: violation.getPropertyPath()) {
            path.add(node.toString());
        }
        return GraphQLError.newError()
            .errorType(BAD_REQUEST)
            .message(violation.getMessage())
            .path(path)
            .build();
    }
}

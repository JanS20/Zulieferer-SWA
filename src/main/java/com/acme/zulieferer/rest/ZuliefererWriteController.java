package com.acme.zulieferer.rest;

import com.acme.zulieferer.service.ConstraintViolationsException;
import com.acme.zulieferer.service.EmailExistsException;
import com.acme.zulieferer.service.VersionOutdatedException;
import com.acme.zulieferer.service.ZuliefererWriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import static java.lang.StringTemplate.STR;
import static com.acme.zulieferer.rest.ZuliefererGetController.ID_PATTERN;
import static com.acme.zulieferer.rest.ZuliefererGetController.REST_PATH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;

@Controller
@RequestMapping(REST_PATH)
@RequiredArgsConstructor
@Slf4j
public class ZuliefererWriteController {
    @SuppressWarnings("TrailingComment")
    public static final String PROBLEM_PATH = "/problem/";
    private static final String VERSIONSNUMMER_FEHLT = "Versionsnummer fehlt";
    private final ZuliefererWriteService service;
    private final UriHelper uriHelper;
    private final ZuliefererMapper mapper;

    /**
     * Einen neuen Zulieferer-Datensatz anlegen.
     *
     * @param zuliefererDTO Das Zuliefererobjekt aus dem eingegangenen Request-Body.
     * @param request Das Request-Objekt, um `Location` im Response-Header zu erstellen.
     * @return Response mit Statuscode 201 einschließlich Location-Header oder Statuscode 422 falls Constraints verletzt
     *      sind oder die Emailadresse bereits existiert oder Statuscode 400 falls syntaktische Fehler im Request-Body
     *      vorliegen.
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neuen Zulieferer anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Zulieferer neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    ResponseEntity<Void> post(@RequestBody final ZuliefererDTO zuliefererDTO,
                              final HttpServletRequest request) {
        log.debug("post: {}", zuliefererDTO);
        final var zuliefererInput = mapper.toZulieferer(zuliefererDTO);
        final var zulieferer = service.create(zuliefererInput);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var location = URI.create(STR."\{baseUri}/\{zulieferer.getId()}");
        return created(location).build();
    }

    /**
     * Einen vorhandenen Zulieferer-Datensatz überschreiben.
     *
     * @param id ID des zu aktualisierenden Zulieferers.
     * @param zuliefererDTO Das Zuliefererobjekt aus dem eingegangenen Request-Body.
     */
    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Einen Zulieferer mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Zulieferer nicht vorhanden")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte oder Email vorhanden")
    void put(@PathVariable final UUID id, @RequestBody final ZuliefererDTO zuliefererDTO,
             @RequestHeader("If-Match") final Optional<String> version,
             final HttpServletRequest request) {
        log.debug("update: id={}, {}", id, zuliefererDTO);
        final int versionInt = getVersion(version, request);
        final var zuliefererInput = mapper.toZulieferer(zuliefererDTO);
        service.update(zuliefererInput, id, versionInt);
    }

    private int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
        log.trace("getVersion: {}", versionOpt);
        if (versionOpt.isEmpty()) {
            throw new VersionInvalidException(
                PRECONDITION_REQUIRED,
                VERSIONSNUMMER_FEHLT,
                URI.create(request.getRequestURL().toString()));
        }

        final var versionStr = versionOpt.get();
        if (versionStr.length() < 3 ||
            versionStr.charAt(0) != '"' ||
            versionStr.charAt(versionStr.length() - 1) != '"') {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                STR."Ungueltiges ETag \{versionStr}",
                URI.create(request.getRequestURL().toString())
            );
        }

        final int version;
        try {
            version = Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
        } catch (final NumberFormatException ex) {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                STR."Ungueltiges ETag \{versionStr}",
                URI.create(request.getRequestURL().toString()),
                ex
            );
        }

        log.trace("getVersion: version={}", version);
        return version;
    }

    @ExceptionHandler
    ProblemDetail onConstraintViolations(final ConstraintViolationsException ex, final HttpServletRequest request) {
        log.debug("onConstraintViolations: {}", ex.getMessage());

        final var zuliefererViolations = ex.getViolations()
            .stream()
            .map(violation -> STR."\{violation.getPropertyPath()}: " +
                STR."\{violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()} " +
                violation.getMessage())
            .toList();
        log.trace("onConstraintViolations: {}", zuliefererViolations);
        final String detail;
        if (zuliefererViolations.isEmpty()) {
            detail = "N/A";
        } else {
            final var violationsStr = zuliefererViolations.toString();
            detail = violationsStr.substring(1, violationsStr.length() - 2);
        }

        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create("/problem/unprocessable"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onMessageNotReadable(final HttpMessageNotReadableException ex,
        final HttpServletRequest request
    ) {
        log.debug("onMessageNotReadable: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.BAD_REQUEST.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onEmailExists(final EmailExistsException ex, final HttpServletRequest request) {
        log.debug("onEmailExists: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setType(URI.create("/problem/unprocessable"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onVersionOutdated(
        final VersionOutdatedException ex, final HttpServletRequest request) {
        log.debug("onVersionOutdated: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(PRECONDITION_FAILED, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }
}

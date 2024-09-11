package com.acme.zulieferer.rest;

import com.acme.zulieferer.service.NotFoundException;
import com.acme.zulieferer.service.ZuliefererReadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Objects;
import java.util.UUID;
import java.util.Optional;
import static com.acme.zulieferer.rest.ZuliefererGetController.REST_PATH;
import static java.lang.StringTemplate.STR;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;


/**
 * Eine Controller-Klasse welche die REST-Schnittstelle bilded, wobei die HTTP-Methoden, Pfade und MIME-Typen auf die
 * Methoden der Klasse abgebildet werden.
 */
@RestController
@RequestMapping(REST_PATH)
@RequiredArgsConstructor
@Slf4j
public class ZuliefererGetController {
    /**
     * Basispfad für die REST-Schnittstelle.
     */
    public static final String REST_PATH = "/rest";
    /**
     * Muster für eine UUID.
     */
    public static final String ID_PATTERN =
        "[\\dA-Fa-f]{8}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{4}-[\\dA-Fa-f]{12}";
    /**
     * Deklaration Service.
     */
    private final ZuliefererReadService service;
    private final UriHelper uriHelper;

    /**
     * Suche alle der Zulieferer mit den richtigen Parametern in der DB.
     *
     * @return Gefundene Zulieferer nach Parametern.
     */
    @GetMapping(produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit Query Parameter", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Zulieferer gefunden")
    @ApiResponse(responseCode = "404", description = "Zulieferer nicht gefunden")
    CollectionModel<ZuliefererModel> get(@RequestParam @NonNull final MultiValueMap<String, String> queryParams,
                                         final HttpServletRequest request) {
        log.debug("get: queryParams={}", queryParams);

        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var models = service.find(queryParams)
            .stream()
            .map(zulieferer -> {
                final var model = new ZuliefererModel(zulieferer);
                model.add(Link.of(STR."\{baseUri}/\{zulieferer.getId()}"));
                return model;
            })
            .toList();

        log.debug("get: {}", models);
        return CollectionModel.of(models);
    }

    /**
     * Suche anhand der Zulieferer-ID als Pfad-Parameter.
     *
     * @param id ID des zu suchenden Zulieferers
     * @return Gefundener Zulieferer.
     */
    @GetMapping(path = "{id:" + ID_PATTERN + "}", produces = HAL_JSON_VALUE)
    @Operation(summary = "Suche mit der Zulieferer-ID", tags = "Suchen")
    @ApiResponse(responseCode = "200", description = "Zulieferer gefunden")
    @ApiResponse(responseCode = "404", description = "Zulieferer nicht gefunden")
    ResponseEntity<ZuliefererModel> getById(@PathVariable final UUID id,
                                            @RequestHeader("If-None-Match") final Optional<String> version,
                                            final HttpServletRequest request) {
        log.debug("getById: id={}", id);

        final var zulieferer = service.findById(id);
        final var currentVersion = STR."\"\{zulieferer.getVersion()}\"";
        if (Objects.equals(version.orElse(null), currentVersion)) {
            return status(NOT_MODIFIED).build();
        }

        final var model = new ZuliefererModel(zulieferer);
        final var baseUri = uriHelper.getBaseUri(request).toString();
        final var idUri = STR."\{baseUri}/\{zulieferer.getId()}";
        final var selfLink = Link.of(idUri);
        final var listLink = Link.of(baseUri, LinkRelation.of("list"));
        final var addLink = Link.of(baseUri, LinkRelation.of("add"));
        final var updateLink = Link.of(idUri, LinkRelation.of("update"));
        final var removeLink = Link.of(idUri, LinkRelation.of("remove"));
        model.add(selfLink, listLink, addLink, updateLink, removeLink);

        log.debug("getById: {}", model);
        return ok().eTag(currentVersion).body(model);
    }

    /**
     * ExceptionHandler für Error
     */
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    void onNotFound(final NotFoundException ex) {
        log.debug(ex.getMessage());
    }
}

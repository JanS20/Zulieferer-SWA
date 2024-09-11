package com.acme.zulieferer.rest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import static com.acme.zulieferer.rest.ZuliefererGetController.REST_PATH;

@Component
@Slf4j
public class UriHelper {
    private static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    private static final String X_FORWARDED_HOST = "x-forwarded-host";
    private static final String X_FORWARDED_PREFIX = "x-forwarded-prefix";
    private static final String ZULIEFERER_PREFIX = "/zulieferer";

    URI getBaseUri(final HttpServletRequest request) {
        final var forwardedHost = request.getHeader(X_FORWARDED_HOST);
        if (forwardedHost != null) {
            return getBaseUriForwarded(request, forwardedHost);
        }

        final var uriComponents = ServletUriComponentsBuilder.fromRequestUri(request).build();
        final var baseUri = STR."\{uriComponents.getScheme()}://\{uriComponents
            .getHost()}:\{uriComponents.getPort()}\{REST_PATH}";
        log.debug("getBaseUri (ohne Forwarding): baseUri={}", baseUri);
        return URI.create(baseUri);
    }

    private URI getBaseUriForwarded(final HttpServletRequest request, final String forwardedHost) {

        final var forwardedProto = request.getHeader(X_FORWARDED_PROTO);
        if (forwardedProto == null) {
            throw new IllegalStateException(STR."Kein '\{X_FORWARDED_PROTO}' im Header");
        }

        var forwardedPrefix = request.getHeader(X_FORWARDED_PREFIX);
        if (forwardedPrefix == null) {
            log.trace("getBaseUriForwarded: Kein '{}' im Header", X_FORWARDED_PREFIX);
            forwardedPrefix = ZULIEFERER_PREFIX;
        }
        final var baseUri = STR."\{forwardedProto}://\{forwardedHost}\{forwardedPrefix}\{REST_PATH}";
        log.debug("getBaseUriForwarded: baseUri={}", baseUri);
        return URI.create(baseUri);
    }
}

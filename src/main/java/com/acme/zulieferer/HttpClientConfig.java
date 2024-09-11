package com.acme.zulieferer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Beans für die REST-Schnittstelle zu "supermarkt" (SupermarktRestRepository) und für die GraphQL-Schnittstelle zu "supermarkt"
 */
public interface HttpClientConfig {
    int KUNDE_DEFAULT_PORT = 8080;
    String GRAPHQL_PATH = "/graphql";
    Logger LOGGER = LoggerFactory.getLogger(HttpClientConfig.class);

    @Bean
    @SuppressWarnings("CallToSystemGetenv")
    default UriComponentsBuilder uriComponentsBuilder() {
        // Umgebungsvariable in Kubernetes, sonst: null
        final var kundeSchemaEnv = System.getenv("KUNDE_SERVICE_SCHEMA");
        final var kundeHostEnv = System.getenv("KUNDE_SERVICE_HOST");
        final var kundePortEnv = System.getenv("KUNDE_SERVICE_PORT");

        // TODO URI bei Docker Compose
        final var schema = kundeSchemaEnv == null ? "https" : kundeSchemaEnv;
        final var host = kundeHostEnv == null ? "localhost" : kundeHostEnv;
        final int port = kundePortEnv == null ? KUNDE_DEFAULT_PORT : Integer.parseInt(kundePortEnv);

        LOGGER.debug("kunde: host={}, port={}", host, port);
        return UriComponentsBuilder.newInstance()
            .scheme(schema)
            .host(host)
            .port(port);
    }
}

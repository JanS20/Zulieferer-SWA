package com.acme.zulieferer.graphql;

import com.acme.zulieferer.entity.Zulieferer;
import com.acme.zulieferer.service.ZuliefererReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import static java.util.Collections.emptyMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ZuliefererQueryController {
    private final ZuliefererReadService service;

    /**
     * Suche anhand der Zulieferer-ID.
     *
     * @param id ID des zu suchenden Zulieferers
     * @return Der gefundene Zulieferer
     */
    @QueryMapping("zulieferer")
    Zulieferer findById(@Argument final UUID id) {
        log.debug("findById: id={}", id);
        final var zulieferer = service.findById(id);
        log.debug("findById: zulieferer={}", zulieferer);
        return zulieferer;
    }

    /**
     * Suche mit diversen Suchkriterien.
     *
     * @param input Suchkriterien und ihre Werte, z.B. `nachname` und `Alpha`
     * @return Die gefundenen Zulieferer als Collection
     */
    @QueryMapping("dieZulieferer")
    Collection<Zulieferer> find(@Argument final Optional<Suchkriterien> input) {
        log.debug("find: suchkriterien={}", input);
        final var suchkriterien = input.map(Suchkriterien::toMap).orElse(emptyMap());
        final var dieZulieferer = service.find(suchkriterien);
        log.debug("find: dieZulieferer={}", dieZulieferer);
        return dieZulieferer;
    }
}

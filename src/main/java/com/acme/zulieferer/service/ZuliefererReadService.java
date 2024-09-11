package com.acme.zulieferer.service;

import com.acme.zulieferer.entity.Zulieferer;
import com.acme.zulieferer.repository.SpecificationBuilder;
import com.acme.zulieferer.repository.ZuliefererRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Anwendungslogik für Zulieferer.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ZuliefererReadService {
    /**
     * Deklaration Repository.
     */
    private final ZuliefererRepository repo;
    private final SpecificationBuilder specificationBuilder;

    /** Zulieferer anhand von Query Parametern als Collection suchen.
     *
     * @param queryParams Die Query Parameter
     * @return Die gefundenen Zulieferer oder eine leere Liste
     * @throws NotFoundException Falls keine Zulieferer gefunden wurden
     */
    @Transactional(readOnly = true)
    public @NonNull Collection<Zulieferer> find(@NonNull final Map<String, List<String>> queryParams) {
        log.debug("find: queryParams={}", queryParams);

        if (queryParams.isEmpty()) {
            return repo.findAll();
        }

        if (queryParams.size() == 1) {
            final var names = queryParams.get("name");
            if (names != null && names.size() == 1) {
                final var dieZulieferer = repo.findByName(names.getFirst());
                if (dieZulieferer.isEmpty()) {
                    throw new NotFoundException(queryParams);
                }
                log.debug("find (name): {}", dieZulieferer);
                return dieZulieferer;
            }
            final var emails = queryParams.get("email");
            if (emails != null && emails.size() == 1) {
                final var zulieferer = repo.findByEmail(emails.getFirst());
                if (zulieferer.isEmpty()) {
                    throw new NotFoundException(queryParams);
                }
                final var dieZulieferer = List.of(zulieferer.get());
                log.debug("find (email): {}", dieZulieferer);
                return dieZulieferer;
            }
        }

        final var spec = specificationBuilder
            .build(queryParams)
            .orElseThrow(() -> new NotFoundException(queryParams));
        final var zulieferers = repo.findAll(spec);
        if (zulieferers.isEmpty()) {
            throw new NotFoundException(queryParams);
        }
        log.debug("find: {}", zulieferers);
        return zulieferers;
    }

    /**
     * Einen Zulieferer anhand seiner ID suchen.
     *
     * @param id Die Id des gesuchten Zulieferers
     * @return Der gefundene Zulieferer
     * @throws NotFoundException Falls kein Zulieferer gefunden wurde
     */
    public Zulieferer findById(final UUID id) {
        log.debug("findById: id={}", id);
        final var zulieferer = repo.findById(id)
            .orElseThrow(() -> new NotFoundException(id));
        log.debug("findById: {}", zulieferer);
        return zulieferer;
    }
}

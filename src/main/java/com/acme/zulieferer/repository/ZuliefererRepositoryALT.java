package com.acme.zulieferer.repository;

import com.acme.zulieferer.entity.Zulieferer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.IntStream;
import static com.acme.zulieferer.repository.DB.ZULIEFERER;
import static java.util.Collections.emptyList;
import static java.util.UUID.randomUUID;

/**
 * Repository für den DB-Zugriff bei Zulieferern.
 *
 */
@Repository
@Slf4j
public class ZuliefererRepositoryALT {

    /**
     * Einen Zulieferer anhand seiner ID suchen.
     *
     * @param id Die Id des gesuchten Zulieferers
     * @return Optional mit dem gefundenen Zulieferer oder leeres Optional
     */
    public Optional<Zulieferer> findById(final UUID id) {
        log.debug("findById: id={}", id);
        final var result = ZULIEFERER.stream()
            .filter(zulieferer -> Objects.equals(zulieferer.getId(), id))
            .findFirst();
        log.debug("findById: {}", result);
        return result;
    }

    /**
     * Zulieferer anhand von Query Parametern ermitteln.
     * Z.B. mit GET <a href="https://localhost:8080/api?nachname=A&amp;plz=7">...</a>
     *
     * @param queryParams Query Parameter.
     * @return Gefundene Zulieferer oder leere Collection.
     */
    public @NonNull Collection<Zulieferer> find(final Map<String, ? extends List<String>> queryParams) {
        log.debug("find: suchkriterien={}", queryParams);

        if (queryParams.isEmpty()) {
            return findAll();
        }

        for (final var entry : queryParams.entrySet()) {
            switch (entry.getKey()) {
                case "email" -> {
                    final var zuliefererOpt = findByEmail(entry.getValue().getFirst());
                    //noinspection OptionalIsPresent
                    return zuliefererOpt.isPresent() ? List.of(zuliefererOpt.get()) : emptyList();
                }
                case "name" -> {
                    return findByName(entry.getValue().getFirst());
                }
                default -> {
                    log.debug("find: ungueltiges Suchkriterium={}", entry.getKey());
                    return emptyList();
                }
            }
        }

        return findAll();
    }

    /**
     * Alle Zulieferer zurückgeben
     *
     * @return Alle Zulieferer in der DB
     */
    public @NonNull Collection<Zulieferer> findAll() {
        return ZULIEFERER;
    }

    /**
     * Zulieferer zu gegebener Emailadresse aus der DB ermitteln.
     *
     * @param email Emailadresse für die Suche
     * @return Gefundener Zulieferer oder leeres Optional
     */
    public Optional<Zulieferer> findByEmail(final String email) {
        log.debug("findByEmail: {}", email);
        final var result = ZULIEFERER.stream()
            .filter(zulieferer -> Objects.equals(zulieferer.getEmail(), email))
            .findFirst();
        log.debug("findByEmail: {}", result);
        return result;
    }

    /**
     * Zulieferer zu gegebenem Namen aus der DB ermitteln.
     *
     * @param name Name für die Suche
     * @return Gefundener Zulieferer oder leeres Optional
     */
    public @NonNull Collection<Zulieferer> findByName(final String name) {
        log.debug("findByName: name={}", name);
        final var result = ZULIEFERER.stream()
            .filter(zulieferer -> zulieferer.getName().contains(name))
            .toList();
        log.debug("findByName: name={}", result);
        return result;
    }

    /**
     * Abfrage, ob es einen Zulieferer mit gegebener Emailadresse gibt.
     *
     * @param email Emailadresse für die Suche
     * @return true, falls es einen solchen Zulieferer gibt, sonst false
     */
    public boolean isEmailExisting(final String email) {
        log.debug("isEmailExisting: email={}", email);
        final var count = ZULIEFERER.stream()
            .filter(zulieferer -> Objects.equals(zulieferer.getEmail(), email))
            .count();
        log.debug("isEmailExisting: count={}", count);
        return count > 0L;
    }

    /**
     * Einen neuen Zulieferer anlegen.
     *
     * @param zulieferer Das Objekt des neu anzulegenden Zulieferers.
     * @return Der neu angelegte Zulieferer mit generierter ID
     */
    public @NonNull Zulieferer create(final @NonNull Zulieferer zulieferer) {
        log.debug("create: {}", zulieferer);
        zulieferer.setId(randomUUID());
        ZULIEFERER.add(zulieferer);
        log.debug("create: {}", zulieferer);
        return zulieferer;
    }

    /**
     * Einen vorhandenen Zulieferer aktualisieren.
     *
     * @param zulieferer Das Objekt mit den neuen Daten
     */
    public void update(final @NonNull Zulieferer zulieferer) {
        log.debug("update: {}", zulieferer);
        final OptionalInt index = IntStream
            .range(0, ZULIEFERER.size())
            .filter(i -> Objects.equals(ZULIEFERER.get(i).getId(), zulieferer.getId()))
            .findFirst();
        log.trace("update: index={}", index);
        if (index.isEmpty()) {
            return;
        }
        ZULIEFERER.set(index.getAsInt(), zulieferer);
        log.debug("update: {}", zulieferer);
    }
}


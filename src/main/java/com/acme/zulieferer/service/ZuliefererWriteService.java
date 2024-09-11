package com.acme.zulieferer.service;

import com.acme.zulieferer.entity.Zulieferer;
import com.acme.zulieferer.repository.ZuliefererRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ZuliefererWriteService {
    private final ZuliefererRepository repo;

    private final Validator validator;

    /**
     * Einen neuen Zulieferer anlegen.
     *
     * @param zulieferer Das Objekt des neu anzulegenden Zulieferers.
     * @return Der neu angelegte Zulieferer mit generierter ID
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws EmailExistsException Es gibt bereits einen Zulieferer mit der Emailadresse.
     */
    @Transactional
    public Zulieferer create(final Zulieferer zulieferer) {
        log.debug("create: {}", zulieferer);

        final var violations = validator.validate(zulieferer);
        if (!violations.isEmpty()) {
            log.debug("create: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }

        if (repo.existsByEmail(zulieferer.getEmail())) {
            throw new EmailExistsException(zulieferer.getEmail());
        }

        final var zuliefererDB = repo.save(zulieferer);
        log.debug("create: {}", zuliefererDB);
        return zuliefererDB;
    }

    /**
     * Einen vorhandenen Zulieferer aktualisieren.
     *
     * @param zulieferer Das Objekt mit den neuen Daten (ohne ID)
     * @param id ID des zu aktualisierenden Zulieferers
     * @throws ConstraintViolationsException Falls mindestens ein Constraint verletzt ist.
     * @throws NotFoundException Kein Zulieferer zur ID vorhanden.
     * @throws EmailExistsException Es gibt bereits einen ZuliefererF mit der Emailadresse.
     */
    @Transactional
    public Zulieferer update(final Zulieferer zulieferer, final UUID id, final int version) {
        log.debug("update: {}", zulieferer);
        log.debug("update: id={}", id);

        final var violations = validator.validate(zulieferer);
        if (!violations.isEmpty()) {
            log.debug("update: violations={}", violations);
            throw new ConstraintViolationsException(violations);
        }

        final var zuliefererDbOptional = repo.findById(id);
        if (zuliefererDbOptional.isEmpty()) {
            throw new NotFoundException(id);
        }

        var zuliefererDb = zuliefererDbOptional.get();
        log.trace("update: version={}, kundeDb={}", version, zuliefererDb);
        if (version != zuliefererDb.getVersion()) {
            throw new VersionOutdatedException(version);
        }

        final var email = zulieferer.getEmail();
        if (!Objects.equals(email, zuliefererDb.getEmail()) && repo.existsByEmail(email)) {
            log.debug("update: email {} existiert", email);
            throw new EmailExistsException(email);
        }

        zuliefererDb.set(zulieferer);
        repo.save(zuliefererDb);
        return zuliefererDb;
    }

    @Transactional
    public void deleteById(final UUID id) {
        log.debug("deleteById: id={}", id);
        final var zuliefererOptional = repo.findById(id);
        if (zuliefererOptional.isEmpty()) {
            log.debug("deleteById: id={} nicht vorhanden", id);
            return;
        }
        repo.delete(zuliefererOptional.get());
    }
}

package com.acme.zulieferer.repository;

import com.acme.zulieferer.entity.Zulieferer;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import static com.acme.zulieferer.entity.Zulieferer.GESCHAEFTSFUEHRER_GRAPH;
import static com.acme.zulieferer.entity.Zulieferer.GESCHAEFTSFUEHRER_LIEFERUNG_GRAPH;

@Repository
public interface ZuliefererRepository extends JpaRepository<Zulieferer, UUID>, JpaSpecificationExecutor<Zulieferer> {
    @EntityGraph(GESCHAEFTSFUEHRER_GRAPH)
    @NonNull
    @Override
    List<Zulieferer> findAll();

    @EntityGraph(GESCHAEFTSFUEHRER_GRAPH)
    @NonNull
    @Override
    List<Zulieferer> findAll(@NonNull Specification<Zulieferer> spec);

    @EntityGraph(GESCHAEFTSFUEHRER_GRAPH)
    @NonNull
    @Override
    Optional<Zulieferer> findById(@NonNull UUID id);

    /**
     * Kunde einschließlich Umsätze anhand der ID suchen.
     *
     * @param id Kunde ID
     * @return Gefundener Kunde
     */
    @Query("""
        SELECT DISTINCT z
        FROM     Zulieferer z
        WHERE    z.id = :id
        """)
    @EntityGraph(GESCHAEFTSFUEHRER_LIEFERUNG_GRAPH)
    @NonNull
    Optional<Zulieferer> findByIdFetchLieferung(UUID id);

    /**
     * Kunde zu gegebener Emailadresse aus der DB ermitteln.
     *
     * @param email Emailadresse für die Suche
     * @return Optional mit dem gefundenen Kunde oder leeres Optional
     */
    @Query("""
        SELECT z
        FROM   Zulieferer z
        WHERE  lower(z.email) LIKE concat(lower(:email), '%')
        """)
    @EntityGraph(GESCHAEFTSFUEHRER_GRAPH)
    Optional<Zulieferer> findByEmail(String email);

    /**
     * Abfrage, ob es einen Kunden mit gegebener Emailadresse gibt.
     *
     * @param email Emailadresse für die Suche
     * @return true, falls es einen solchen Kunden gibt, sonst false
     */
    @SuppressWarnings("BooleanMethodNameMustStartWithQuestion")
    boolean existsByEmail(String email);

    /**
     * Kunden anhand des Nachnamens suchen.
     *
     * @param name Der (Teil-) Nachname der gesuchten Kunden
     * @return Die gefundenen Kunden oder eine leere Collection
     */
    @Query("""
        SELECT   z
        FROM     Zulieferer z
        WHERE    lower(z.name) LIKE concat('%', lower(:name), '%')
        ORDER BY z.id
        """)
    @EntityGraph(GESCHAEFTSFUEHRER_GRAPH)
    Collection<Zulieferer> findByName(CharSequence name);

    /**
     * Abfrage, welche Nachnamen es zu einem Präfix gibt.
     *
     * @param prefix Nachname-Präfix.
     * @return Die passenden Nachnamen oder eine leere Collection.
     */
    @Query("""
        SELECT DISTINCT z.name
        FROM     Zulieferer z
        WHERE    lower(z.name) LIKE concat(lower(:prefix), '%')
        ORDER BY z.name
        """)
    Collection<String> findNamenByPrefix(String prefix);
}

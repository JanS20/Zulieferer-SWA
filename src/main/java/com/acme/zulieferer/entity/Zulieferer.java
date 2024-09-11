package com.acme.zulieferer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

/**
 * Daten eines Kunden.
 */
@Entity
@Table(name = "zulieferer")
@NamedEntityGraph(name = Zulieferer.GESCHAEFTSFUEHRER_GRAPH, attributeNodes = @NamedAttributeNode("geschaeftsfuehrer"))
@NamedEntityGraph(name = Zulieferer.GESCHAEFTSFUEHRER_LIEFERUNG_GRAPH, attributeNodes = {
    @NamedAttributeNode("geschaeftsfuehrer"), @NamedAttributeNode("lieferungen")
})
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@Builder
@SuppressWarnings({
    "ClassFanOutComplexity",
    "RequireEmptyLineBeforeBlockTagGroup",
    "DeclarationOrder",
    "JavadocDeclaration",
    "MissingSummary",
    "RedundantSuppression"})
public class Zulieferer {
    public interface NeuValidation {
    }

    public static final String GESCHAEFTSFUEHRER_GRAPH = "Zulieferer.geschaeftsfuehrer";

    public static final String GESCHAEFTSFUEHRER_LIEFERUNG_GRAPH = "Zulieferer.geschaeftsfuehrerLieferung";

    /**
     * Muster für einen gültigen Namen.
     */
    private static final String NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+";

    private static final int USERNAME_MAX_LENGTH = 20;

    /**
     * Die ID des Zulieferers.
     *
     * @param id Die ID.
     * @return Die ID.
     */
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Versionsnummer für optimistische Synchronisation.
     */
    @Version
    private int version;

    /**
     * Der Name des Zulieferers.
     *
     * @param name Der Name.
     * @return Der Name.
     */
    @Pattern(regexp = NAME_PATTERN)
    @NotNull
    private String name;

    /**
     * Die Emailadresse des Zulieferers.
     *
     * @param email Die Emailadresse.
     * @return Die Emailadresse.
     */
    @Email
    @NotNull
    private String email;

    /**
     * Der Geschaeftsfuehrer des Zulieferers.
     *
     * @param geschaeftsfuehrer Der Geschaeftsfuehrer.s
     * @return Der Geschaeftsfuehrer.
     */
    @OneToOne(optional = false, cascade = {PERSIST, REMOVE}, fetch = LAZY, orphanRemoval = true)
    @Valid
    @NotNull(groups = Zulieferer.NeuValidation.class)
    private Geschaeftsfuehrer geschaeftsfuehrer;

    /**
     * Die Lieferung des Zulieferers.
     *
     * @param lieferungen Die Lieferung.
     * @return Die Lieferung.
     */
    @OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "zulieferer_id")
    @OrderColumn(name = "idx", nullable = false)
    @NotNull
    private List<Lieferung> lieferungen;

    @Size(max = USERNAME_MAX_LENGTH)
    private String username;

    @CreationTimestamp
    private LocalDateTime erzeugt;

    @UpdateTimestamp
    private LocalDateTime aktualisiert;

    public void set(final Zulieferer zulieferer) {
        name = zulieferer.name;
        email = zulieferer.email;
    }
}

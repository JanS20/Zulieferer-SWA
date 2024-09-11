package com.acme.zulieferer.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.util.UUID;

/**
 * Daten einer Lieferung.
 * 1:N-Beziehung
 */
@Entity
@Table(name = "lieferung")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@Builder
@SuppressWarnings({"JavadocDeclaration", "RequireEmptyLineBeforeBlockTagGroup", "MissingSummary"})
public class Lieferung {
    /**
     * Muster für einen gültigen Artikel.
     */
    public static final String ARTIKEL_PATTERN =
        "[A-ZÄÖÜ][a-zäöüß]+";

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Die Artikel für die Lieferung.
     *
     * @param artikel Die Artikel als String
     * @return Die Artikel als String
     */
    @Pattern(regexp = ARTIKEL_PATTERN)
    private String artikel;

    @JsonCreator
    public Lieferung(@JsonProperty("artikel") String artikel) {
        this.artikel = artikel;
    }
}

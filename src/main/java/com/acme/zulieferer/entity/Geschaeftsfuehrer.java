package com.acme.zulieferer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
 * Daten eines Geschaeftsfuehrers.
 * 1:1-Beziehung
 */
@Entity
@Table(name = "geschaeftsfuehrer")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString
@Builder
@SuppressWarnings({"JavadocDeclaration", "RequireEmptyLineBeforeBlockTagGroup", "MissingSummary"})
public class Geschaeftsfuehrer {
    /**
     * Kleinster Wert für eine Kategorie.
     */
    public static final long MIN_KATEGORIE = 0;
    /**
     * Maximaler Wert für eine Kategorie.
     */
    public static final long MAX_KATEGORIE = 10000000;
    /**
     * Muster für einen gültigen Namen.
     */
    private static final String NAME_PATTERN = "[A-ZÄÖÜ][a-zäöüß]+";

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Der Name des Geschaeftsfuehrer.
     *
     * @param name Der Name.
     * @return Der Name.
     */
    @Pattern(regexp = NAME_PATTERN)
    @NotNull
    private String name;

    /**
     * Die Emailadresse des Geschaeftsfuehrer.
     *
     * @param email Die Emailadresse.
     * @return Die Emailadresse.
     */
    @Email
    @NotNull
    private String email;

    /**
     * Das Gehalt des Geschaeftsfuehrer.
     *
     * @param gehalt Das Gehalt.
     * @return Das Gehalt.
     */
    @Min(MIN_KATEGORIE)
    @Max(MAX_KATEGORIE)
    private int gehalt;
}

package com.acme.zulieferer.service;

import lombok.Getter;

@Getter
public class EmailExistsException extends RuntimeException {
    /**
     * Bereits vorhandene Emailadresse.
     */
    private final String email;

    EmailExistsException(@SuppressWarnings("ParameterHidesMemberVariable") final String email) {
        super(STR."Die Emailadresse \{email} existiert bereits");
        this.email = email;
    }
}

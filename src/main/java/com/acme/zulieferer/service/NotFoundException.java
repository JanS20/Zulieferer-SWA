package com.acme.zulieferer.service;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RuntimeException, falls kein Zulieferer gefunden wurde.
 */
@Getter
public class NotFoundException extends RuntimeException{

    /**
     * Nicht-vorhandene ID.
     */
    private final UUID id;

    /**
     * Query Parameter, zu denen nichts gefunden wurde.
     */
    private final Map<String, List<String>> queryParams;

    NotFoundException(final UUID id) {
        super(STR."Kein Zulieferer mit der ID \{id} gefunden.");
        this.id = id;
        queryParams = null;
    }

    NotFoundException(final Map<String, List<String>> queryParams) {
        super(STR."Kein Zulieferer mit den Parametern: \{queryParams} gefunden");
        id = null;
        this.queryParams = queryParams;
    }

}

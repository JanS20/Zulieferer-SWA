package com.acme.zulieferer.service;

import lombok.Getter;

import static java.lang.StringTemplate.STR;

@Getter
public class VersionOutdatedException extends RuntimeException {
    private final int version;

    VersionOutdatedException(final int version) {
        super(STR."Die Versionsnummer \{version} ist veraltet.");
        this.version = version;
    }
}

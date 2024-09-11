/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.acme.zulieferer;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * Konfigurationsklasse für die Anwendung bzw. den Microservice.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
final class ApplicationConfig implements SecurityConfig {
    ApplicationConfig() {
    }

    // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#native-image.advanced.custom-hints
    // https://stackoverflow.com/questions/76287163/...
    // ...how-to-specify-the-location-of-a-keystore-file-with-spring-aot-processing
    /**
     * Keystores f&uuml;r TLS und SQL-Skripte f&uuml;r GraalVM registrieren.
     */
    static class CertificateResourcesRegistrar implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(final RuntimeHints hints, final ClassLoader classLoader) {
            hints.resources().registerPattern("*.p12");
            // https://github.com/spring-projects/spring-boot/issues/31999
            // https://github.com/flyway/flyway/issues/2927
            hints.resources().registerPattern("*.sql");
        }
    }
}

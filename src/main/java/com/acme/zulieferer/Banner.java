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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.zulieferer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Objects;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * Banner als String-Konstante für den Start des Servers.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@SuppressWarnings({
    "AccessOfSystemProperties",
    "CallToSystemGetenv",
    "UtilityClassCanBeEnum",
    "UtilityClass",
    "ClassUnconnectedToPackage"
})
@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
final class Banner {

    // http://patorjk.com/software/taag/#p=display&f=Slant&t=zulieferer%20v1
    private static final String FIGLET = """
                      ___      ____                            ___
         ____  __  __/ (_)__  / __/__  ________  _____   _   _<  /
        /_  / / / / / / / _ \\/ /_/ _ \\/ ___/ _ \\/ ___/  | | / / /\s
         / /_/ /_/ / / /  __/ __/  __/ /  /  __/ /      | |/ / / \s
        /___/\\__,_/_/_/\\___/_/  \\___/_/   \\___/_/       |___/_/  \s
                                                                 \s
        """;
    private static final String JAVA = STR."\{Runtime.version()} - \{System.getProperty("java.vendor")}";
    private static final String OS_VERSION = System.getProperty("os.name");
    private static final InetAddress LOCALHOST = getLocalhost();
    private static final long MEGABYTE = 1024L * 1024L;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final String SERVICE_HOST = System.getenv("ZULIEFERER_SERVICE_HOST");
    private static final String SERVICE_PORT = System.getenv("ZULIEFERER_SERVICE_PORT");
    private static final String KUBERNETES = SERVICE_HOST == null
        ? "N/A"
        : STR."ZULIEFERER_SERVICE_HOST=\{SERVICE_HOST}, ZULIEFERER_SERVICE_PORT=\{SERVICE_PORT}";
    private static final String USERNAME = System.getProperty("user.name");

    /**
     * Banner für den Server-Start.
     */
    static final String TEXT = """

        $figlet
        (C) Juergen Zimmermann, Hochschule Karlsruhe
        Version             2023.10.0
        Spring Boot         $springBoot
        Spring Security     $springSecurity
        Spring Framework    $spring
        Java                $java
        Betriebssystem      $os
        Rechnername         $rechnername
        IP-Adresse          $ip
        Heap: Size          $heapSize MiB
        Heap: Free          $heapFree MiB
        Kubernetes          $kubernetes
        Username            $username
        JVM Locale          $locale
        GraphiQL            /graphiql
        OpenAPI             /swagger-ui.html /v3/api-docs.yaml
        H2 Console          /h2-console (JDBC URL: "jdbc:h2:mem:testdb" mit User "sa" und Passwort "")
        """
        .replace("$figlet", FIGLET)
        .replace("$springBoot", SpringBootVersion.getVersion())
        .replace("$springSecurity", SpringSecurityCoreVersion.getVersion())
        .replace("$spring", Objects.requireNonNull(SpringVersion.getVersion()))
        .replace("$java", JAVA)
        .replace("$os", OS_VERSION)
        .replace("$rechnername", LOCALHOST.getHostName())
        .replace("$ip", LOCALHOST.getHostAddress())
        .replace("$heapSize", String.valueOf(RUNTIME.totalMemory() / MEGABYTE))
        .replace("$heapFree", String.valueOf(RUNTIME.freeMemory() / MEGABYTE))
        .replace("$kubernetes", KUBERNETES)
        .replace("$username", USERNAME)
        .replace("$locale", Locale.getDefault().toString());

    @SuppressWarnings("ImplicitCallToSuper")
    private Banner() {
    }

    private static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (final UnknownHostException ex) {
            throw new IllegalStateException(ex);
        }
    }
}

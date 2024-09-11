package com.acme.zulieferer.graphql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Suchkriterien(String name, String email) {
        /**
         * Konvertierung in eine Map.
         *
         * @return Das konvertierte Map-Objekt
         */
        Map<String, List<String>> toMap() {
            final Map<String, List<String>> map = new HashMap<>(2, 1);
            if (name != null) {
                map.put("nachname", List.of(name));
            }
            if (email != null) {
                map.put("email", List.of(email));
            }
            return map;
        }
}

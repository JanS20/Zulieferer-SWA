package com.acme.zulieferer.repository;

import com.acme.zulieferer.entity.Geschaeftsfuehrer;
import com.acme.zulieferer.entity.Lieferung;
import com.acme.zulieferer.entity.Zulieferer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Emulation der Datenbasis für persistente Zulieferer.
 */
public class DB {

    /**
     * Liste der Zulieferer zur Emulation der DB.
     */
    static final List<Zulieferer> ZULIEFERER = getZulieferer();

    private static List<Zulieferer> getZulieferer() {
        return Stream.of(
                Zulieferer.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                    .name("Admin")
                    .email("admin@acme.con")
                    .lieferungen(List.of(
                        Lieferung.builder().artikel("Gold").build(),
                        Lieferung.builder().artikel("Silber").build()))
                    .geschaeftsfuehrer(Geschaeftsfuehrer.builder()
                        .name("Admin")
                        .email("admin@acme.cn")
                        .gehalt(1000000).build())
                    .build(),
                // HTTP GET
                Zulieferer.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                    .name("Paprikafarm")
                    .email("paprikafarm@acme.cn")
                    .lieferungen(List.of(
                        Lieferung.builder().artikel("RotePaprika").build(),
                        Lieferung.builder().artikel("GruenePaprika").build()))
                    .geschaeftsfuehrer(Geschaeftsfuehrer.builder()
                        .name("Peter")
                        .email("peter@acme.cn")
                        .gehalt(200000).build())
                    .build(),
                Zulieferer.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000002"))
                    .name("Wurstfabrik")
                    .email("wurstfabrik@acme.cn")
                    .lieferungen(List.of(
                        Lieferung.builder().artikel("Bratwurst").build(),
                        Lieferung.builder().artikel("Salami").build()))
                    .geschaeftsfuehrer(Geschaeftsfuehrer.builder()
                        .name("Hans")
                        .email("hans@acme.cn")
                        .gehalt(300000).build())
                    .build(),
                Zulieferer.builder()
                    .id(UUID.fromString("00000000-0000-0000-0000-000000000030"))
                    .name("Apfelhof")
                    .email("apfelhof@acme.cn")
                    .lieferungen(List.of(
                        Lieferung.builder().artikel("Apfel").build(),
                        Lieferung.builder().artikel("Granatapfel").build()))
                    .geschaeftsfuehrer(Geschaeftsfuehrer.builder()
                        .name("Angelika")
                        .email("angelika@acme.cn")
                        .gehalt(250000).build())
                    .build()
        )
            .collect(Collectors.toList());
    }
}

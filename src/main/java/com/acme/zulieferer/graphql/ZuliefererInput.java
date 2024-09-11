package com.acme.zulieferer.graphql;

import com.acme.zulieferer.entity.Geschaeftsfuehrer;
import com.acme.zulieferer.entity.Lieferung;

import java.util.List;

public record ZuliefererInput(
    String name,
    String email,
    List<Lieferung> lieferungen,
    Geschaeftsfuehrer geschaeftsfuehrer
) {
}

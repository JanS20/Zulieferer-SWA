package com.acme.zulieferer.rest;

import com.acme.zulieferer.entity.Geschaeftsfuehrer;
import com.acme.zulieferer.entity.Lieferung;

import java.util.List;

public record ZuliefererDTO(
    String name,
    String email,
    List<Lieferung> lieferungen,
    Geschaeftsfuehrer geschaeftsfuehrer
) {
}

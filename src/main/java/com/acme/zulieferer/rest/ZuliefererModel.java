package com.acme.zulieferer.rest;

import com.acme.zulieferer.entity.Geschaeftsfuehrer;
import com.acme.zulieferer.entity.Lieferung;
import com.acme.zulieferer.entity.Zulieferer;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import java.util.List;

@JsonPropertyOrder({
    "nachname", "email", "lieferungen", "geschaeftsfuehrer"
})
@Relation(collectionRelation = "dieZulieferer", itemRelation = "zulieferer")
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Getter
@Setter
@ToString(callSuper = true)
public class ZuliefererModel extends RepresentationModel<ZuliefererModel> {
    private final String name;
    private final String email;
    private final List<Lieferung> lieferungen;
    private final Geschaeftsfuehrer geschaeftsfuehrer;

    ZuliefererModel(final Zulieferer zulieferer) {
        name = zulieferer.getName();
        email = zulieferer.getEmail();
        lieferungen = zulieferer.getLieferungen();
        geschaeftsfuehrer = zulieferer.getGeschaeftsfuehrer();
    }
}

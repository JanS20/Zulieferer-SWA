package com.acme.zulieferer.rest;

import com.acme.zulieferer.entity.Lieferung;
import com.acme.zulieferer.entity.Zulieferer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = "spring", nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface ZuliefererMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "erzeugt", ignore = true)
    @Mapping(target = "aktualisiert", ignore = true)
    @Mapping(target = "username", ignore = true)
    Zulieferer toZulieferer(ZuliefererDTO dto);

    @Mapping(target = "id", ignore = true)
    Lieferung toLieferung(LieferungDTO dto);
}

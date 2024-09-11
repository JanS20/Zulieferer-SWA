package com.acme.zulieferer.repository;

import com.acme.zulieferer.entity.Zulieferer;
import com.acme.zulieferer.entity.Zulieferer_;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.lang.StringTemplate.STR;

@Component
@Slf4j
public class SpecificationBuilder {

    public Optional<Specification<Zulieferer>> build(Map<String, List<String>> queryParams) {
        log.debug("build: queryParams={}", queryParams);

        if (queryParams.isEmpty()) {
            return Optional.empty();
        }

        final var specs = queryParams
            .entrySet()
            .stream()
            .map(this::toSpecification)
            .toList();

        if (specs.isEmpty() || specs.contains(null)) {
            return Optional.empty();
        }

        return Optional.of(Specification.allOf(specs));
    }

    private Specification<Zulieferer> toSpecification(Map.Entry<String, List<String>> entry) {
        log.trace("toSpec: entry={}", entry);
        final var key = entry.getKey();
        final var values = entry.getValue();

        if (values == null || values.size() != 1) {
            return null;
        }

        final var value = values.getFirst();
        return switch (key) {
            case "name" -> name(value);
            case "email" ->  email(value);
            default -> null;
        };
    }

    private Specification<Zulieferer> name(final String teil) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Zulieferer_.name)),
            builder.lower(builder.literal(STR."%\{teil}%"))
        );
    }

    private Specification<Zulieferer> email(final String teil) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(Zulieferer_.email)),
            builder.lower(builder.literal(STR."%\{teil}%"))
        );
    }
}

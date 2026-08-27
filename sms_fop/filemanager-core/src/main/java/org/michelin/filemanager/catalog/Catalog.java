package org.michelin.filemanager.catalog;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Immutable in-memory catalog of all loaded InterfaceDefinitions, keyed by
 * (interfaceName, variant). The loader builds one of these at startup; the
 * rest of the application reads from it.
 */
public final class Catalog {

    private final Map<InterfaceDefinition.Key, InterfaceDefinition> defs;

    public Catalog(Collection<InterfaceDefinition> definitions) {
        Objects.requireNonNull(definitions, "definitions");
        Map<InterfaceDefinition.Key, InterfaceDefinition> map = new LinkedHashMap<>();
        for (InterfaceDefinition d : definitions) {
            InterfaceDefinition prior = map.put(d.key(), d);
            if (prior != null) {
                throw new CatalogValidationException(
                    "Duplicate catalog entry for interface '" + d.interfaceName() +
                    "' variant '" + d.variant() + "'");
            }
        }
        this.defs = Map.copyOf(map);
    }

    public Optional<InterfaceDefinition> find(String interfaceName, String variant) {
        return Optional.ofNullable(defs.get(new InterfaceDefinition.Key(interfaceName, variant)));
    }

    public Optional<InterfaceDefinition> find(InterfaceDefinition.Key key) {
        return Optional.ofNullable(defs.get(key));
    }

    public Set<InterfaceDefinition.Key> keys() { return defs.keySet(); }

    public Collection<InterfaceDefinition> all() { return defs.values(); }

    public int size() { return defs.size(); }
}

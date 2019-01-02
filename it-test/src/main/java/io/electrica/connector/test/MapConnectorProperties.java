package io.electrica.connector.test;

import io.electrica.connector.spi.ConnectorProperties;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Implementation of {@link ConnectorProperties} interface based on {@code Map<String, String>}.
 *
 * @see Builder
 */
public class MapConnectorProperties implements ConnectorProperties {

    public static final ConnectorProperties EMPTY = new MapConnectorProperties(Collections.emptyMap());

    private final Map<String, String> properties;

    public MapConnectorProperties(Map<String, String> properties) {
        this.properties = Objects.requireNonNull(properties, "properties map");
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean contains(String key) {
        return properties.containsKey(key);
    }

    @Nullable
    @Override
    public String getString(String key) {
        return properties.get(key);
    }


    public static class Builder {

        private final Map<String, String> properties = new HashMap<>();

        public Builder() {
        }

        public Builder addString(String key, String value) {
            this.properties.put(key, value);
            return this;
        }

        public Builder addBoolean(String key, boolean value) {
            return addString(key, String.valueOf(value));
        }

        public Builder addInteger(String key, int value) {
            return addString(key, String.valueOf(value));
        }

        public Builder addLong(String key, long value) {
            return addString(key, String.valueOf(value));
        }

        public Builder addDouble(String key, double value) {
            return addString(key, String.valueOf(value));
        }

        public Builder addAll(Map<String, String> properties) {
            this.properties.putAll(properties);
            return this;
        }

        public MapConnectorProperties build() {
            return new MapConnectorProperties(properties);
        }
    }
}

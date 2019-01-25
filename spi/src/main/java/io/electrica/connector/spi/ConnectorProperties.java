package io.electrica.connector.spi;

import javax.annotation.Nullable;

public interface ConnectorProperties extends ObjectProperties {

    ConnectorProperties EMPTY = new ConnectorProperties() {
        @Override
        public boolean contains(String key) {
            return false;
        }

        @Nullable
        @Override
        public String getString(String key) {
            return null;
        }
    };

}

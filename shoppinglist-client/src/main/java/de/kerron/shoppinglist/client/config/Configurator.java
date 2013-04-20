package de.kerron.shoppinglist.client.config;

import javax.naming.ConfigurationException;

/**
 * Zentrale Konfigurationsklasse.
 * 
 * @author Ronny Friedland
 */
public class Configurator {
    public final static CompositeConfiguration CONFIG = new CompositeConfiguration();
    static {
        try {
            CONFIG.addConfiguration(new SystemConfiguration());
            CONFIG.addConfiguration(new PropertiesConfiguration(Thread.currentThread().getContextClassLoader()
                    .getResource("client.properties")));
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Konfigurationsparameter
     */
    public enum ConfiguratorKeys {
        /** Read Timeout */
        READ_TIMEOUT("shoppinglist.timeout.read"),
        /** Connection Timeout */
        CONNECTION_TIMEOUT("shoppinglist.timeout.connect"),
        /** Target Host */
        HOST("shoppinglist.server.host"),
        /** Target Port */
        PORT("shoppinglist.server.port"),
        /** Target Path */
        PATH("shoppinglist.server.path"),
        /** Flag if communication is encrypted */
        SECURE("shoppinglist.server.https");

        private final String key;

        private ConfiguratorKeys(final String aKey) {
            this.key = aKey;
        }

        /**
         * Liefert den Schlüssel des Konfigurationsparameters
         * 
         * @return Konfigurationsparameter
         */
        public String getKey() {
            return key;
        }
    }

    private Configurator() {
        // empty
    }
}

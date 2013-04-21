package de.kerron.shoppinglist.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import de.kerron.shoppinglist.client.config.Configurator;

/**
 * @author Ronny Friedland
 */
public final class ShoppingListRestClient {

    private Client client;

    private void configure() {
        // read configuration
        Integer readTimeout = Configurator.CONFIG.getInteger(Configurator.ConfiguratorKeys.READ_TIMEOUT.getKey(), 1000);
        Integer connectionTimeout = Configurator.CONFIG.getInteger(
                Configurator.ConfiguratorKeys.CONNECTION_TIMEOUT.getKey(), 1000);
        // configure client
        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        client.setConnectTimeout(connectionTimeout);
        client.setReadTimeout(readTimeout);
    }

    /**
     * Erzeugt eine neue {@link ShoppingListRestClient} Instanz.
     */
    public ShoppingListRestClient() {
        configure();
    }

    /**
     * Sendet neue Daten an den Server
     * 
     * @param entry
     *            die Daten
     * @return success Status
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean addData(final String id, final String description, final String quantityValue,
            final String quantityUnit, final String list) {
        if (null == id) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (null == description) {
            throw new IllegalArgumentException("description must not be null");
        }
        if (null == quantityValue) {
            throw new IllegalArgumentException("quantityValue must not be null");
        }
        if (null == quantityUnit) {
            throw new IllegalArgumentException("quantityUnit must not be null");
        }
        if (null == list) {
            throw new IllegalArgumentException("list must not be null");
        }
        // prepare post data
        MultivaluedMap formData = new MultivaluedMapImpl();
        formData.add("id", id);
        // doPost
        WebResource service = client.resource(getBaseURI());
        ClientResponse response = service.path("add").queryParams(formData).accept(MediaType.TEXT_PLAIN)
                .post(ClientResponse.class);
        return 200 == response.getStatus();
    }

    /**
     * Löscht die angegebenen Daten vom Server
     * 
     * @param entry
     *            die zu löschenden Daten
     * @return success Status
     */
    public boolean deleteData(final String id) {
        if (null == id) {
            throw new IllegalArgumentException("id must not be null");
        }
        // doDelete
        WebResource service = client.resource(getBaseURI());
        ClientResponse response = service.path("delete").queryParam("id", id).accept(MediaType.TEXT_PLAIN)
                .delete(ClientResponse.class);
        return 200 == response.getStatus();
    }

    private static URI getBaseURI() {
        Boolean isSecure = Configurator.CONFIG.getBoolean(Configurator.ConfiguratorKeys.SECURE.getKey(), Boolean.FALSE);
        String host = Configurator.CONFIG.getString(Configurator.ConfiguratorKeys.HOST.getKey(), "localhost");
        Integer port = Configurator.CONFIG.getInteger(Configurator.ConfiguratorKeys.PORT.getKey(), 8080);
        String path = Configurator.CONFIG.getString(Configurator.ConfiguratorKeys.PATH.getKey(), "");

        StringBuilder sbuild = new StringBuilder(255);
        if (isSecure) {
            sbuild.append("https://");
        } else {
            sbuild.append("http://");
        }
        sbuild.append(host).append(":").append(port).append("/").append(path);

        return UriBuilder.fromUri(sbuild.toString()).build();
    }
}

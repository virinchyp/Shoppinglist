package de.ronnyfriedland.shoppinglist.service;

import de.kerron.shoppinglist.client.ShoppingListRestClient;

/**
 * @author Ronny Friedland
 */
public class SynchronizationService {

    private static ShoppingListRestClient client;

    static {
        client = new ShoppingListRestClient();
    }
}

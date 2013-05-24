package de.ronnyfriedland.shoppinglist.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import de.kerron.shoppinglist.client.ShoppingListRestClient;
import de.ronnyfriedland.shoppinglist.exception.ShopplinglistLogicException;
import de.ronnyfriedland.shoppinglist.security.PasswordEncoder;

/**
 * @author Ronny Friedland
 */
public class SynchronizationService {

    public static String register(final String username, final char[] password) throws ShopplinglistLogicException {
        try {
            return new ShoppingListRestClient().register(username, PasswordEncoder.encodePassword(password));
        } catch (NoSuchAlgorithmException e) {
            throw new ShopplinglistLogicException("Unknown algorithm used to encode password.", e);
        } catch (UnsupportedEncodingException e) {
            throw new ShopplinglistLogicException("Unknown encoding used to create password hash.", e);
        }
    }
}

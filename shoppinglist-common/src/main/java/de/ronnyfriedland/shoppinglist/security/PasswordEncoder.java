package de.ronnyfriedland.shoppinglist.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Encodes a given password.
 * 
 * @author Ronny Friedland
 */
public final class PasswordEncoder {

    public static String encodePassword(final char[] password) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(String.valueOf(password).getBytes());
        return String.format("%x", new BigInteger(1, md.digest()));
    }
}

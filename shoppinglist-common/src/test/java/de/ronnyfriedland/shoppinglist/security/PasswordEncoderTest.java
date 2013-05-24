package de.ronnyfriedland.shoppinglist.security;

import org.junit.Assert;
import org.junit.Test;

public class PasswordEncoderTest {

    @Test
    public void testPasswordEncoder() throws Exception {
        String enc1 = PasswordEncoder.encodePassword("123".toCharArray());
        String enc2 = PasswordEncoder.encodePassword("123".toCharArray());

        Assert.assertEquals(enc1, enc2);
    }

}

package de.ronnyfriedland.shoppinglist.entity;

import java.util.UUID;

/**
 * Entity to represent a shopping list. Every {@link Entry} is associated to a
 * {@link Shoppinglist}.
 * 
 * @author Ronnyf Friedland
 */
public class Shoppinglist extends AbstractEntity {

    public static final String TABLE = "List";
    public static final String COL_ID = "id";

    /**
     * Creates an new {@link Shoppinglist} instance.
     * 
     * @param uuid
     *            the initial {@link #uuid}
     */
    public Shoppinglist(final String uuid) {
        super(uuid);
    }

    /**
     * Creates a new {@link Shoppinglist}.
     */
    public Shoppinglist() {
        this(UUID.randomUUID().toString());
    }
}

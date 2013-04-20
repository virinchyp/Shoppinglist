package de.ronnyfriedland.shoppinglist.entity;

import java.util.UUID;

/**
 * @author ronnyfriedland
 * 
 */
public class Shoppinglist extends AbstractEntity {

	public static final String TABLE = "List";
	public static final String COL_ID = "id";

	public Shoppinglist(final String uuid) {
		super(uuid);
	}

	public Shoppinglist() {
		this(UUID.randomUUID().toString());
	}
}

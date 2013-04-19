package de.ronnyfriedland.shoppinglist.entity;

import java.util.UUID;

/**
 * @author ronnyfriedland
 *
 */
public class Shoppinglist {

	public static final String TABLE = "List";
	public static final String COL_ID = "id";
	
	private String uuid;

	public Shoppinglist(final String uuid) {
		this.uuid = uuid;
	}
	
	public Shoppinglist() {
		this(UUID.randomUUID().toString());
	}
	
	public String getUuid() {
		return uuid;
	}
}

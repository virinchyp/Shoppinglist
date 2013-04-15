package de.ronnyfriedland.shoppinglist.entity;

import java.util.UUID;

/**
 * @author ronnyfriedland
 *
 */
public class Entry {

	public static final String COL_ID = "id";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_QUANTITYVALUE = "quantityvalue";
	public static final String COL_QUANTITY = "quantity";
	
	private String uuid;
	private String description;
	private String quantity;
	private String quantityValue;
	
	public Entry() {
		uuid = UUID.randomUUID().toString();
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public String getQuantityValue() {
		return quantityValue;
	}
	public void setQuantityValue(String quantityValue) {
		this.quantityValue = quantityValue;
	}
	public String getUuid() {
		return uuid;
	}
	
	@Override
	public String toString() {
		StringBuilder sbuild = new StringBuilder();
		sbuild.append(getQuantityValue()).append(" ");
		sbuild.append(getQuantity()).append(" ");
		sbuild.append(getDescription());
		return sbuild.toString();
	}
}

package de.ronnyfriedland.shoppinglist.entity;

import java.util.UUID;

/**
 * @author ronnyfriedland
 *
 */
public class Entry {

	public static final String TABLE = "Entry";
	public static final String COL_ID = "id";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_QUANTITYVALUE = "quantityvalue";
	public static final String COL_QUANTITY = "quantity";
	public static final String COL_STATUS = "status";
	public static final String COL_LIST = "list";
	
	private String uuid;
	private String description;
	private Quantity quantity;
	private Status status;
	private Shoppinglist list;
	
	public Entry() {
		this(UUID.randomUUID().toString());
	}

	public Entry(final String uuid) {
		this.uuid = uuid;
		this.status = Status.OPEN;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Quantity getQuantity() {
		return quantity;
	}
	public void setQuantity(Quantity quantity) {
		this.quantity = quantity;
	}
	public String getUuid() {
		return uuid;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setStatus(String status) {
		this.status = Status.valueOf(status);
	}
	
	public Shoppinglist getList() {
		return list;
	}

	public void setList(Shoppinglist list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean equals = false;
		if(null != o && o instanceof Entry) {
			if(((Entry)o).getUuid().equals(getUuid())) {
				equals = true;
			}
		}
		return equals;
	}
	
	@Override
	public String toString() {
		StringBuilder sbuild = new StringBuilder();
		sbuild.append(getQuantity().getValue()).append(" ");
		sbuild.append(getQuantity().getUnit()).append(" ");
		sbuild.append(getDescription());
		return sbuild.toString();
	}
}

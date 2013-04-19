package de.ronnyfriedland.shoppinglist.entity;

/**
 * @author Ronny Friedland
 *
 */
public class Quantity {

	private final Integer value;
	private final String unit;
	
	public Quantity(final Integer value, final String unit) {
		this.value = value;
		this.unit = unit;
	}

	public Integer getValue() {
		return value;
	}

	public String getUnit() {
		return unit;
	}
	
}

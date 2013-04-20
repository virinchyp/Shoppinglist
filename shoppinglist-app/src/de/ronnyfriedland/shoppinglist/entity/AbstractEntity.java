package de.ronnyfriedland.shoppinglist.entity;

public abstract class AbstractEntity {

	private String uuid;

	public AbstractEntity(final String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		boolean equals = false;
		if (null != o && o instanceof Entry) {
			if (((AbstractEntity) o).getUuid().equals(getUuid())) {
				equals = true;
			}
		}
		return equals;
	}

}

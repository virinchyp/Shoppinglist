package de.ronnyfriedland.shoppinglist.entity;

/**
 * The abstract base entity
 * 
 * @author Ronny Friedland
 */
public abstract class AbstractEntity {

    private final String uuid;

    /**
     * Creates a new {@link AbstractEntity}.
     * 
     * @param uuid
     *            the initial uuid
     */
    public AbstractEntity(final String uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the {@link #uuid} of the entity
     * 
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getUuid().hashCode();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (null != o && o instanceof AbstractEntity) {
            if (((AbstractEntity) o).getUuid().equals(getUuid())) {
                equals = true;
            }
        }
        return equals;
    }

}

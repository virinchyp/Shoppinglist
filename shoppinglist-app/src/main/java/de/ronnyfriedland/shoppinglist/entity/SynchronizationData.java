package de.ronnyfriedland.shoppinglist.entity;

import java.util.UUID;

/**
 * @author Ronny Friedland
 */
public class SynchronizationData extends AbstractEntity {

    public static final String TABLE = "Syncdata";
    public static final String COL_ID = "id";
    public static final String COL_USERNAME = "username";
    public static final String COL_CODE = "code";

    private String username;
    private String code;

    public SynchronizationData(String uuid) {
        super(uuid);
    }

    public SynchronizationData() {
        this(UUID.randomUUID().toString());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
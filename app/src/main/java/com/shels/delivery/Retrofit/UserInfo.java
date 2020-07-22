package com.shels.delivery.Retrofit;

public class UserInfo {
    private String username;
    private String name;
    private String group;
    private String id;

    public UserInfo(String username, String name, String group, String id) {
        this.username = username;
        this.name = name;
        this.group = group;
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setId(String id) {
        this.id = id;
    }
}

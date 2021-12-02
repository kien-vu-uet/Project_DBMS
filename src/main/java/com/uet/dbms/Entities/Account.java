package com.uet.dbms.Entities;


public class Account {
    private int id;
    private String username;
    private String password;
    private String avatarPath;

    public Account(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.avatarPath = "";
    }

    public Account(int id, String username, String password, String path) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.avatarPath = path;
    }

    public void setAvatarPath(String path) {
        this.avatarPath = path;
    }

    public String getAvatarPath() {
        return this.avatarPath;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void changeAccount(Account other) {
        if (other == null) return;
        this.id = other.getId();
        this.username = other.getUsername();
        this.password = other.getPassword();
    }
}

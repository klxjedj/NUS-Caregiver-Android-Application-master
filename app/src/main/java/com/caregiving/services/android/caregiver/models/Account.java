package com.caregiving.services.android.caregiver.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PC1 on 26/9/2016.
 */
public class Account {

    @SerializedName("account_id")
    private int accountId;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("role")
    public Role role;

    public Account() {}

    public Account(int accountId, Role role) {
        this.accountId = accountId;
        this.role = role;
    }

    public Account(int accountId, String username, String password, Role role) {
        this.accountId = accountId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getAccountId() {
        return accountId;
    }

    public Account setAccountId(int accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public Account setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public Role getRole() {
        return role;
    }

    public Account setRole(Role role) {
        this.role = role;
        return this;
    }
}

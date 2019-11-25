package com.hey.model;

import lombok.Data;

@Data
public class AddressBookItem {
    private String name;
    private String userId;
    private String status;
    private boolean online;
}

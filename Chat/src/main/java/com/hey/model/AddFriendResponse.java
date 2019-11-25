package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddFriendResponse implements Serializable {
    private AddressBookItem item;
}

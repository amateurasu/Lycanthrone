package com.hey.model;

import lombok.Data;

import java.util.List;

@Data
public class AddressBookResponse {
    private List<AddressBookItem> items;
}

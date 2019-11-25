package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddFriendRequest implements Serializable {
    private String username;
}

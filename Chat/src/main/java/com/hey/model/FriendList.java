package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class FriendList implements Serializable {
    private UserHash currentUserHashes;
    private UserHash friendUserHashes;
}

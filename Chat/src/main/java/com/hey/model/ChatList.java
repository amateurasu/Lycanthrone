package com.hey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ChatList implements Serializable {
    private List<UserHash> userHashes;
    private String sessionId;
    private Date updatedDate;
    private String lastMessage;
}

package com.hey.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatListItem {
    private String name;
    private String sessionId;
    private String lastMessage;
    private int unread;
    private boolean groupChat;
    private Date updatedDate;
}

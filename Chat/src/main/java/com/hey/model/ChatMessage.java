package com.hey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChatMessage implements Serializable {
    private String sessionId;
    private UserHash userHash;
    private String message;
    private Date createdDate;
}

package com.hey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ChatMessageResponse extends WsMessage implements Serializable {
    private String sessionId;
    private String userId;
    private String name;
    private String message;
    private Date createdDate;
}

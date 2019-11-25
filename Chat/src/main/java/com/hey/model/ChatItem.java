package com.hey.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChatItem implements Serializable {
    private String userId;
    private String message;
    private String name;
    private Date createdDate;
}

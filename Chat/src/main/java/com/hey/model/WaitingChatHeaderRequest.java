package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class WaitingChatHeaderRequest implements Serializable {
    private String[] usernames;
}

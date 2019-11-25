package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsernameExistedResponse implements Serializable {
    private boolean existed;
    private String username;
}

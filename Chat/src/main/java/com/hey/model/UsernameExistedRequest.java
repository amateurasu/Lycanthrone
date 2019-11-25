package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsernameExistedRequest implements Serializable {
    private String username;
}

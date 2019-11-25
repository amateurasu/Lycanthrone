package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserStatus implements Serializable {
    private String userId;
    private String status;
}

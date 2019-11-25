package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetSessionIdRequest implements Serializable {
    private String userId;
}

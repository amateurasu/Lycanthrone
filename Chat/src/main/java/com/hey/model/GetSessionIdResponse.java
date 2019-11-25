package com.hey.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetSessionIdResponse implements Serializable {
    private String sessionId;
}

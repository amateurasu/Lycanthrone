package com.hey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserOfflineResponse extends WsMessage implements Serializable {
    private String userId;
    private String fullName;
}

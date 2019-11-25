package com.hey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserOnlineResponse extends WsMessage implements Serializable {
    private String userId;
    private String fullName;
}

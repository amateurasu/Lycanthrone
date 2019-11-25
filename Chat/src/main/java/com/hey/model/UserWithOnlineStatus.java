package com.hey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithOnlineStatus extends UserLite {
    private boolean isOnline;
}

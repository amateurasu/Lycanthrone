package com.hey.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAuth {
    private String userName;
    private String userId;
    private String hashedPassword;
}

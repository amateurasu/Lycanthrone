package org.baeldung.web.dto;

import lombok.*;
import org.baeldung.validation.ValidPassword;

@Data
public class PasswordDto {

    @ValidPassword
    private String newPassword;
    private String oldPassword;
}

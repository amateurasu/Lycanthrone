package performance.model;

import lombok.*;

@Data
public class UserRequest {
    private String userId;
    private String userName;
    private String fullName;
    private String password;
}

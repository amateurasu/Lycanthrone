package performance.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChangeStatusRequest implements Serializable {
    private String status;
}

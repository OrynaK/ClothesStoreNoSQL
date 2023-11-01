package nure.ua.clothesstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrder {
    private long userId;
    private String description;
    private LocalDateTime dateTime;
    public UserOrder(long userId) {
        this.userId = userId;
    }

}

package nure.ua.clothesstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.clothesstore.entity.enums.Role;
import nure.ua.clothesstore.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class Order {
    private long id;
    private LocalDateTime dateTime;
    private Status status;
    private List<ClothingOrder> clothesInOrder;
    private Map<Role, UserOrder> usersInOrder;

    public Order() {
        clothesInOrder = new ArrayList<>();
        usersInOrder = new EnumMap<>(Role.class);
    }

    public Order(long userId, List<ClothingOrder> clothesInOrder) {
        this.clothesInOrder = clothesInOrder;
        this.usersInOrder = new EnumMap<>(Role.class);
        usersInOrder.put(Role.USER, new UserOrder(userId));
    }

    public void addClothing(ClothingOrder clothingOrder) {
        clothesInOrder.add(clothingOrder);
    }

    public void putUser(Role role, UserOrder userOrder) {
        usersInOrder.put(role, userOrder);
    }
}

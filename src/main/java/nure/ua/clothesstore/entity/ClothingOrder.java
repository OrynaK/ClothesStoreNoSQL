package nure.ua.clothesstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.clothesstore.entity.enums.Size;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClothingOrder {
    private long clothingId;
    private BigDecimal currentPrice;
    private int amount;
    private String name;
    private Size size;
    private String color;

}

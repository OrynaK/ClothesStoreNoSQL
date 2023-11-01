package nure.ua.clothesstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nure.ua.clothesstore.entity.enums.Season;
import nure.ua.clothesstore.entity.enums.Sex;
import nure.ua.clothesstore.entity.enums.Size;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clothing {
    private long id;
    private String name;
    private Size size;
    private String color;
    private Season season;
    private int amount;
    private BigDecimal actualPrice;
    private Sex sex;

    public Clothing(String name, Size size, String color, Season season, int amount, BigDecimal actualPrice, Sex sex) {
        this.name = name;
        this.size = size;
        this.color = color;
        this.season = season;
        this.amount = amount;
        this.actualPrice = actualPrice;
        this.sex = sex;
    }
}

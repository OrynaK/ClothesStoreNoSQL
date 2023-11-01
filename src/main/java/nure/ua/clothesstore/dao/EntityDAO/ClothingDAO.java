package nure.ua.clothesstore.dao.EntityDAO;

import nure.ua.clothesstore.dao.CRUDRepository;
import nure.ua.clothesstore.entity.Clothing;
import nure.ua.clothesstore.entity.enums.Size;

import java.util.List;

public interface ClothingDAO extends CRUDRepository<Clothing> {
    List<Clothing> getClothingBySize(Size size);
    void updateClothingAmount(long shoeId, int amount);

}

package nure.ua.clothesstore.dao.EntityDAO;

import nure.ua.clothesstore.dao.CRUDRepository;
import nure.ua.clothesstore.dao.DBException;
import nure.ua.clothesstore.entity.Order;
import nure.ua.clothesstore.entity.enums.Status;

import java.util.List;

public interface OrderDAO extends CRUDRepository<Order> {
    List<Order> getOrdersByUserId(Long userId);
    void updateStatus(Long orderId, Status status) throws DBException;

    void placeOrder(long clothingId, int amount, long userId,String city, String  street, String houseNumber, int entrance, int apartmentNumber);

}

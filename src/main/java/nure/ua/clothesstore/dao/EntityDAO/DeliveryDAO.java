package nure.ua.clothesstore.dao.EntityDAO;

import nure.ua.clothesstore.dao.CRUDRepository;
import nure.ua.clothesstore.entity.Delivery;

public interface DeliveryDAO extends CRUDRepository<Delivery> {

    Delivery updateByOrderId(Delivery delivery, long order_id);
}

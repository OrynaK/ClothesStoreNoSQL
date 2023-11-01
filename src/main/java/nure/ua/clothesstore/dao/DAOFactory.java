package nure.ua.clothesstore.dao;

import nure.ua.clothesstore.dao.EntityDAO.*;
import nure.ua.clothesstore.dao.EntityDAOImpl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DAOFactory {
    DAOConfig config;

    @Bean
    public static UserDAO getUserDAOInstance(DAOConfig config) {
        return new UserDAOImpl(config);
    }

    @Bean
    public DeliveryDAO getDeliveryDAOInstance(DAOConfig config) {
        return new DeliveryDAOIml(config);
    }

    @Bean
    public ClothingDAO getClothingDAOInstance(DAOConfig config) {
        return new ClothingDAOImpl(config);
    }

    @Bean
    public OrderDAO getOrderDAOInstance(DAOConfig config) {
        return new OrderDAOImpl(config);
    }

}

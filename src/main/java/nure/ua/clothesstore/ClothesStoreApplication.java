package nure.ua.clothesstore;

import nure.ua.clothesstore.dao.DAOConfig;
import nure.ua.clothesstore.dao.DBException;
import nure.ua.clothesstore.dao.EntityDAO.*;
import nure.ua.clothesstore.entity.*;
import nure.ua.clothesstore.entity.enums.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(DAOConfig.class)
public class ClothesStoreApplication {
    public static void main(String[] args) throws DBException {
        ApplicationContext context = SpringApplication.run(ClothesStoreApplication.class, args);

        UserDAO userDAO = context.getBean(UserDAO.class);
        ClothingDAO clothingDAO = context.getBean(ClothingDAO.class);
        OrderDAO orderDAO = context.getBean(OrderDAO.class);
        DeliveryDAO deliveryDAO = context.getBean(DeliveryDAO.class);

        System.out.println("---TEST FIND BY ID---");
        System.out.println("User with id=1: "+userDAO.findById(1));
        System.out.println("Clothing with id=2: "+clothingDAO.findById(2));
        System.out.println("Order with id=1: "+orderDAO.findById(1));
        System.out.println("Delivery with order_id=1: "+deliveryDAO.findById(1));

        System.out.println("---TEST ADD---");
        User newUser = new User("тест", "тест", "тест", "тест", "+тест", null);
        System.out.println("Вставлений користувач: " + userDAO.findById(userDAO.add(newUser)));
        Clothing newClothing = new Clothing("тест", Size.valueOf("XS"), "тест", Season.valueOf("WINTER"), 1, new BigDecimal(17), Sex.valueOf("MALE"));
        System.out.println("Вставлена одежа: " + clothingDAO.findById(clothingDAO.add(newClothing)));
        ClothingOrder clothingOrder = new ClothingOrder(newClothing.getId(), newClothing.getActualPrice(), 1, newClothing.getName(), newClothing.getSize(), newClothing.getColor());
        UserOrder userOrder=new UserOrder(1);
        Order newOrder = new Order();
        newOrder.addClothing(clothingOrder);
        newOrder.putUser(Role.USER, userOrder);
        long orderId= orderDAO.add(newOrder);
        System.out.println("Вставлене замовлення: " + orderDAO.findById(orderId));
        Delivery newDelivery = new Delivery(orderId,"тест", "тест", "тест", 1, 1);
        System.out.println("Вставлена доставка: " + deliveryDAO.findById(deliveryDAO.add(newDelivery)));


        System.out.println("---TEST DELETE---");
        System.out.println("-----------------");
        List<User> users = userDAO.findAll();
        System.out.println("Users before delete:");
        for (User user : users) {
            System.out.println(user);
        }
        userDAO.delete(3);
        users = userDAO.findAll();
        System.out.println("Users after delete:");
        for (User user : users) {
            System.out.println(user);
        }
        System.out.println("-----------------");
        List<Clothing> clothes = clothingDAO.findAll();
        System.out.println("Clothing before delete:");
        for (Clothing clothing : clothes) {
            System.out.println(clothing);
        }
        clothingDAO.delete(5);
        clothes = clothingDAO.findAll();
        System.out.println("Clothing after delete:");
        for (Clothing clothing : clothes) {
            System.out.println(clothing);
        }
        List<Order> orders = orderDAO.findAll();
        System.out.println("-----------------");
        System.out.println("Orders before delete:");
        for (Order order : orders) {
            System.out.println(order);
        }
        orderDAO.delete(3);
        orders = orderDAO.findAll();
        System.out.println("-----------------");
        System.out.println("Orders after delete:");
        for (Order order : orders) {
            System.out.println(order);
        }
        System.out.println("---TEST UPDATE---");
        System.out.println("User before update: ");
        System.out.println(userDAO.findById(1));
        User updatedUser = new User(1, "апдейт", "Петров", "ivan@example.com", "пароль123", "+380123456789", Role.valueOf("USER"));
        userDAO.update(updatedUser);
        System.out.println(userDAO.findById(1));
        System.out.println("Clothing before update: ");
        System.out.println(clothingDAO.findById(1));
        clothingDAO.updateClothingAmount(clothingDAO.findById(1).getId(), 2365);
        System.out.println("Clothing after update: ");
        System.out.println(clothingDAO.findById(1));
        System.out.println("Order before update: ");
        System.out.println(orderDAO.findById(1));
        orderDAO.updateStatus(orderDAO.findById(1).getId(), Status.DELIVERED);
        System.out.println("Order after update: ");
        System.out.println(orderDAO.findById(1));
        System.out.println("Delivery before update: ");
        System.out.println(deliveryDAO.findById(1));
        Delivery updatedDelivery = new Delivery("тест", "тест", "тест");
        deliveryDAO.updateByOrderId(updatedDelivery, 1);
        System.out.println("Delivery after update: ");
        System.out.println(deliveryDAO.findById(1));

        System.out.println("---TEST PROCEDURE---");
        List<Order> orders1 = orderDAO.findAll();
        System.out.println("Orders before insert:");
        for (Order order : orders1) {
            System.out.println(order);
        }
        orderDAO.placeOrder(1, 1, 1, "тест", "тест", "тест", 1, 1);
        orders1 = orderDAO.findAll();
        System.out.println("Orders after insert:");
        for (Order order : orders1) {
            System.out.println(order);
        }
    }
}

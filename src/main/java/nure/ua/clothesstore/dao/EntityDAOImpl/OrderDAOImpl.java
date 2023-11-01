package nure.ua.clothesstore.dao.EntityDAOImpl;

import nure.ua.clothesstore.dao.ConnectionManager;
import nure.ua.clothesstore.dao.DAOConfig;
import nure.ua.clothesstore.dao.DBException;
import nure.ua.clothesstore.dao.EntityDAO.OrderDAO;
import nure.ua.clothesstore.entity.ClothingOrder;
import nure.ua.clothesstore.entity.Order;
import nure.ua.clothesstore.entity.UserOrder;
import nure.ua.clothesstore.entity.enums.Role;
import nure.ua.clothesstore.entity.enums.Size;
import nure.ua.clothesstore.entity.enums.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    ConnectionManager connectionManager;

    public OrderDAOImpl(DAOConfig config) {
        connectionManager = new ConnectionManager(config);
    }

    private static final String INSERT_ORDER = "INSERT INTO `order` (datetime, status) VALUES (DEFAULT,DEFAULT)";
    private static final String UPDATE_STATUS = "UPDATE `order` SET status=? WHERE id=?";
    private static final String INSERT_CLOTHING_ORDER = "INSERT INTO `clothing_order` (clothing_id, order_id, amount, current_price) VALUES (?, ?, ?, ?)";
    private static final String INSERT_ORDER_USER = "INSERT INTO `user_order` (order_id, user_id, description, datetime) VALUES (?, ?, ?, DEFAULT)";
    private static final String GET_ORDERS = "SELECT * from `order`";
    private static final String GET_ORDER_BY_ID = "SELECT * from `order` WHERE id=?";
    private static final String GET_CLOTHING_ORDER = "SELECT * FROM `clothing_order` WHERE order_id=?";
    private static final String GET_USER_ORDER = "SELECT * FROM `user_order` WHERE order_id=?";
    private static final String GET_ORDER_FROM_USER_ORDER = "SELECT order_id FROM `user_order` WHERE user_id=? ORDER BY order_id DESC";
    private static final String GET_ROLE = "SELECT role FROM `user` WHERE id=?";
    private static final String DELETE = "DELETE FROM `order` WHERE id=?";
    private static final String DELETE_DELIVERY = "DELETE FROM delivery WHERE order_id=?";
    private static final String PLACE_ORDER = "CALL PlaceOrder(?, ?, ?, ?, ?, ?, ?, ?)";


    @Override
    public long add(Order order) {
        try (Connection con = connectionManager.getConnection(false)) {
            try (PreparedStatement ps = con.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                try (PreparedStatement s = con.prepareStatement(INSERT_CLOTHING_ORDER)) {
                    if (generatedKeys.next()) {
                        con.commit();
                        order.setId(generatedKeys.getLong(1));
                        for (ClothingOrder clothingOrder : order.getClothesInOrder()) {
                            int k = 0;
                            s.setLong(++k, clothingOrder.getClothingId());
                            s.setLong(++k, order.getId());
                            s.setInt(++k, clothingOrder.getAmount());
                            s.setBigDecimal(++k, clothingOrder.getCurrentPrice());
                            s.addBatch();
                        }
                        s.executeBatch();

                        try (PreparedStatement prs = con.prepareStatement(INSERT_ORDER_USER)) {
                            prs.setLong(1, order.getId());
                            prs.setLong(2, order.getUsersInOrder().get(Role.USER).getUserId());
                            prs.setString(3, "");
                            if(prs.executeUpdate()!=0){
                                con.commit();
                            }
                            else {
                                con.rollback();
                            }
                        }
                    }
                    else{
                        con.rollback();
                    }
                }
            }
            return order.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Order update(Order entity) {
        return null;
    }

    @Override
    public void delete(long orderId) {
        try {
            try (Connection con = connectionManager.getConnection(false)) {
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM clothing_order WHERE order_id=?")) {
                    ps.setLong(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new SQLException("delete failed." +
                                    "To delete order, please, firstly delete clothing_order with this order");
                        } else {
                            try (PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user_order WHERE order_id=?")) {
                                preparedStatement.setLong(1, orderId);
                                try (ResultSet rst = ps.executeQuery()) {
                                    if (rst.next()) {
                                        throw new SQLException("delete failed." +
                                                "To delete order, please, firstly delete user_order with this order");
                                    } else {
                                        try (PreparedStatement statement = con.prepareStatement(DELETE)) {
                                            statement.setLong(1, orderId);
                                            if (statement.executeUpdate() > 0) {
                                                try (PreparedStatement pst = con.prepareStatement(DELETE_DELIVERY)) {
                                                    pst.setLong(1, orderId);
                                                    pst.executeUpdate();
                                                }
                                                con.commit();
                                            } else {
                                                con.rollback();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Order findById(long orderId) {
        Order order = new Order();
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_ORDER_BY_ID)) {
                int k = 0;
                ps.setLong(++k, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        order = mapOrder(rs);
                        try (PreparedStatement prs = con.prepareStatement(GET_USER_ORDER)) {
                            int l = 0;
                            prs.setLong(++l, order.getId());
                            UserOrder userOrder;
                            try (ResultSet resultSet = prs.executeQuery()) {
                                while (resultSet.next()) {
                                    userOrder = mapUserOrder(resultSet);
                                    Role role1 = null;
                                    try (PreparedStatement preparedStatement = con.prepareStatement(GET_ROLE)) {
                                        int m = 0;
                                        preparedStatement.setLong(++m, userOrder.getUserId());
                                        try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                                            while (resultSet1.next()) {
                                                role1 = getRole(userOrder.getUserId());
                                            }
                                        }
                                    }
                                    order.putUser(role1, userOrder);
                                }
                            }
                        }
                        addClothingOrder(order, con);
                    }
                    return order;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> findAll() throws DBException {
        List<Order> orders = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            try (Statement ps = con.createStatement()) {
                try (ResultSet rs = ps.executeQuery(GET_ORDERS)) {
                    while (rs.next()) {
                        Order order = mapOrder(rs);
                        try (PreparedStatement prs = con.prepareStatement(GET_USER_ORDER)) {
                            int l = 0;
                            prs.setLong(++l, order.getId());
                            UserOrder userOrder;
                            try (ResultSet resultSet = prs.executeQuery()) {
                                while (resultSet.next()) {
                                    userOrder = mapUserOrder(resultSet);
                                    Role role1 = null;
                                    try (PreparedStatement preparedStatement = con.prepareStatement(GET_ROLE)) {
                                        int m = 0;
                                        preparedStatement.setLong(++m, userOrder.getUserId());
                                        try (ResultSet resultSet1 = preparedStatement.executeQuery()) {
                                            while (resultSet1.next()) {
                                                role1 = getRole(userOrder.getUserId());
                                            }
                                        }
                                    }
                                    order.putUser(role1, userOrder);
                                }
                            }
                        }
                        addClothingOrder(order, con);
                        orders.add(order);
                    }

                    return orders;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setDateTime(rs.getTimestamp("datetime").toLocalDateTime());
        o.setStatus(Status.valueOf(rs.getString("status").toUpperCase()));
        return o;
    }

    private UserOrder mapUserOrder(ResultSet rs) throws SQLException {
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(rs.getLong("user_id"));
        userOrder.setDescription(rs.getString("description"));
        userOrder.setDateTime(rs.getTimestamp("datetime").toLocalDateTime());
        return userOrder;
    }

    private void addClothingOrder(Order order, Connection con) throws SQLException {
        try (PreparedStatement prs = con.prepareStatement(GET_CLOTHING_ORDER)) {
            int l = 0;
            prs.setLong(++l, order.getId());
            try (ResultSet resultSet = prs.executeQuery()) {
                while (resultSet.next()) {
                    try (PreparedStatement prst = con.prepareStatement("SELECT name, size, color FROM clothing WHERE id=?")) {
                        int b = 0;
                        prst.setLong(++b, resultSet.getLong("clothing_id"));
                        try (ResultSet rs = prst.executeQuery()) {
                            while (rs.next()) {
                                order.addClothing(mapClothingOrder(resultSet, rs));
                            }
                        }
                    }
                }
            }
        }
    }

    private ClothingOrder mapClothingOrder(ResultSet resultSet, ResultSet rs) throws SQLException {
        ClothingOrder clothingOrder = new ClothingOrder();
        clothingOrder.setClothingId(resultSet.getLong("clothing_id"));
        clothingOrder.setCurrentPrice(resultSet.getBigDecimal("current_price"));
        clothingOrder.setAmount(resultSet.getInt("amount"));
        clothingOrder.setName(rs.getString("name"));
        clothingOrder.setSize(Size.valueOf(rs.getString("size")));
        clothingOrder.setColor(rs.getString("color"));
        return clothingOrder;
    }

    private Role getRole(Long id) {
        Role role = null;
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_ROLE)) {
                int k = 0;
                ps.setLong(++k, id);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        role = Role.valueOf(resultSet.getString("role").toUpperCase());
                    }
                    return role;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_ORDER_FROM_USER_ORDER)) {
                int k = 0;
                ps.setLong(++k, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        orders.add(findById(rs.getLong(1)));
                    }
                    return orders;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStatus(Long orderId, Status status) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE_STATUS)) {
                int k = 0;
                ps.setString(++k, String.valueOf(status));
                ps.setLong(++k, orderId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void placeOrder(long clothingId, int amount, long userId, String city, String street, String houseNumber, int entrance, int apartmentNumber) {
        try (Connection con = connectionManager.getConnection()) {
            ResultSet rs;
            try (CallableStatement ps = con.prepareCall(PLACE_ORDER)) {
                int k = 0;
                ps.setLong(++k, clothingId);
                ps.setInt(++k, amount);
                ps.setLong(++k, userId);
                ps.setString(++k, city);
                ps.setString(++k, street);
                ps.setString(++k, houseNumber);
                ps.setInt(++k, entrance);
                ps.setInt(++k, apartmentNumber);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

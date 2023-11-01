package nure.ua.clothesstore.dao.EntityDAOImpl;

import nure.ua.clothesstore.dao.ConnectionManager;
import nure.ua.clothesstore.dao.DAOConfig;
import nure.ua.clothesstore.dao.DBException;
import nure.ua.clothesstore.dao.EntityDAO.DeliveryDAO;
import nure.ua.clothesstore.entity.Delivery;

import java.sql.*;
import java.util.List;

public class DeliveryDAOIml implements DeliveryDAO {
    private static final String ADD_DELIVERY = "INSERT INTO delivery (order_id, city, street, house_number, entrance, apartment_number) values (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE delivery SET city=?, street=?, house_number=?, entrance=?, apartment_number=? WHERE order_id=?";
    private static final String GET_DELIVERY_BY_ORDER = "SELECT * FROM delivery WHERE order_id=?";
    ConnectionManager connectionManager;

    public DeliveryDAOIml(DAOConfig config) {
        connectionManager = new ConnectionManager(config);
    }

    @Override
    public long add(Delivery delivery) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = connectionManager.getConnection(false);
            ps = con.prepareStatement(ADD_DELIVERY);
            int k = 0;
            ps.setLong(++k, delivery.getOrder_id());
            ps.setString(++k, delivery.getCity());
            ps.setString(++k, delivery.getStreet());
            ps.setString(++k, delivery.getHouseNumber());
            ps.setInt(++k, delivery.getEntrance());
            ps.setInt(++k, delivery.getApartmentNumber());
            ps.executeUpdate();
            con.commit();
            return delivery.getOrder_id();
        } catch (Exception e) {
            ConnectionManager.rollback(con);
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.close(ps, con);
        }
    }

    @Override
    public Delivery update(Delivery entity) throws DBException {
        return null;
    }

    @Override
    public Delivery updateByOrderId(Delivery delivery, long order_id) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                int k = 0;
                ps.setString(++k, delivery.getCity());
                ps.setString(++k, delivery.getStreet());
                ps.setString(++k, delivery.getHouseNumber());
                ps.setInt(++k, delivery.getEntrance());
                ps.setInt(++k, delivery.getApartmentNumber());
                ps.setLong(++k, order_id);

                ps.executeUpdate();
            }
            return findById(delivery.getOrder_id());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public Delivery findById(long orderId) {
        Delivery delivery = new Delivery();
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_DELIVERY_BY_ORDER)) {
                int k = 0;
                ps.setLong(++k, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        delivery = mapDelivery(rs);
                    }
                    return delivery;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Delivery> findAll() throws DBException {
        return null;
    }


    public Delivery mapDelivery(ResultSet rs) throws SQLException {
        Delivery delivery = new Delivery();
        delivery.setOrder_id(rs.getLong("order_id"));
        delivery.setCity(rs.getString("city"));
        delivery.setStreet(rs.getString("street"));
        delivery.setHouseNumber(rs.getString("house_number"));
        delivery.setEntrance(rs.getInt("entrance"));
        delivery.setApartmentNumber(rs.getInt("apartment_number"));
        return delivery;
    }
}

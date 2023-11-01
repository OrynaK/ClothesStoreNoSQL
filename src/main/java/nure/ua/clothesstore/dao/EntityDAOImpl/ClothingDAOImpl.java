package nure.ua.clothesstore.dao.EntityDAOImpl;

import nure.ua.clothesstore.dao.ConnectionManager;
import nure.ua.clothesstore.dao.DAOConfig;
import nure.ua.clothesstore.dao.DBException;
import nure.ua.clothesstore.dao.EntityDAO.ClothingDAO;
import nure.ua.clothesstore.entity.Clothing;
import nure.ua.clothesstore.entity.enums.Season;
import nure.ua.clothesstore.entity.enums.Sex;
import nure.ua.clothesstore.entity.enums.Size;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClothingDAOImpl implements ClothingDAO {
    private static final String GET_ALL_CLOTHES = "SELECT * FROM clothing";
    private static final String UPDATE = "UPDATE clothing SET name=?, size=?, color=?, season=?, amount=?, actual_price=?, sex=? WHERE id=?";
    private static final String DELETE = "DELETE FROM clothing WHERE id=?";
    private static final String ADD_CLOTHING = "INSERT INTO clothing (name, size, color, season, amount, actual_price, sex) VALUES(?,?,?,?,?,?,?)";
    private static final String FIND_CLOTHING_IN_ORDER = "SELECT * FROM clothing_order WHERE clothing_id=?";
    private static final String FIND_BY_ID = "SELECT * FROM clothing WHERE id=?";
    private static final String GET_CLOTHING_BY_SIZE = "SELECT * FROM clothing WHERE size=?";
    private static final String UPDATE_AMOUNT = "UPDATE clothing SET amount=? WHERE id=?";
    ConnectionManager connectionManager;
    public ClothingDAOImpl(DAOConfig config) {
        connectionManager = new ConnectionManager(config);
    }

    @Override
    public long add(Clothing clothing) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(ADD_CLOTHING, Statement.RETURN_GENERATED_KEYS)) {
                int k = 0;
                ps.setString(++k, clothing.getName());
                ps.setString(++k, clothing.getSize().toString().toUpperCase());
                ps.setString(++k, clothing.getColor());
                ps.setString(++k, clothing.getSeason().toString().toUpperCase());
                ps.setInt(++k, clothing.getAmount());
                ps.setBigDecimal(++k, clothing.getActualPrice());
                ps.setString(++k, clothing.getSex().toString().toUpperCase());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        clothing.setId(keys.getLong(1));
                    }
                }
            }
            return clothing.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Clothing update(Clothing clothing) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                int k = 0;
                ps.setString(++k, clothing.getName());
                ps.setString(++k,clothing.getSize().toString().toUpperCase());
                ps.setString(++k,clothing.getColor());
                ps.setString(++k,clothing.getSeason().toString().toUpperCase());
                ps.setInt(++k,clothing.getAmount());
                ps.setBigDecimal(++k,clothing.getActualPrice());
                ps.setString(++k,clothing.getSex().toString().toUpperCase());
                ps.setLong(++k, clothing.getId());
                ps.executeUpdate();
            }
            return findById(clothing.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        try {
            try (Connection con = connectionManager.getConnection()) {
                try (PreparedStatement st = con.prepareStatement(FIND_CLOTHING_IN_ORDER)) {
                    st.setLong(1, id);
                    try (ResultSet resultSet = st.executeQuery()) {
                        if (resultSet.next())
                            throw new SQLException("delete failed. " +
                                    "To delete clothing, please, firstly delete order with this clothing");
                        try (PreparedStatement statement = con.prepareStatement(DELETE)) {
                            statement.setLong(1, id);
                            statement.executeUpdate();
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Clothing findById(long id) {
        try (Connection connection = connectionManager.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(FIND_BY_ID)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapClothing(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Clothing> findAll() throws DBException {
        List<Clothing> clothingList = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            try (Statement st = con.createStatement()) {
                try (ResultSet rs = st.executeQuery(GET_ALL_CLOTHES)) {
                    while (rs.next()) {
                        clothingList.add(mapClothing(rs));
                    }
                    return clothingList;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Clothing mapClothing(ResultSet rs) throws SQLException {
        Clothing c = new Clothing();
        c.setId(rs.getLong("id"));
        c.setName(rs.getString("name"));
        c.setSize(Size.valueOf(rs.getString("size")));
        c.setColor(rs.getString("color"));
        c.setSeason(Season.valueOf(rs.getString("season").toUpperCase()));
        c.setAmount(rs.getInt("amount"));
        c.setActualPrice(rs.getBigDecimal("actual_price"));
        c.setSex(Sex.valueOf(rs.getString("sex").toUpperCase()));
        return c;
    }

    @Override
    public List<Clothing> getClothingBySize(Size size) {
        List<Clothing> clothingList = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_CLOTHING_BY_SIZE)) {
                int k = 0;
                ps.setString(++k, size.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        clothingList.add(mapClothing(rs));
                    }
                    return clothingList;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public void updateClothingAmount(long clothingId, int amount) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE_AMOUNT)) {
                int k = 0;
                ps.setInt(++k, amount);
                ps.setLong(++k, clothingId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

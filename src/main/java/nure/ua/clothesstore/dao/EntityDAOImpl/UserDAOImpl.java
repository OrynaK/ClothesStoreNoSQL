package nure.ua.clothesstore.dao.EntityDAOImpl;

import nure.ua.clothesstore.dao.ConnectionManager;
import nure.ua.clothesstore.dao.DAOConfig;
import nure.ua.clothesstore.dao.DBException;
import nure.ua.clothesstore.dao.EntityDAO.UserDAO;
import nure.ua.clothesstore.entity.User;
import nure.ua.clothesstore.entity.enums.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    ConnectionManager connectionManager;
    public UserDAOImpl(DAOConfig config) {
        connectionManager = new ConnectionManager(config);
    }

    private static final String GET_USER_BY_ID = "SELECT * from user WHERE id=?";
    private static final String UPDATE = "UPDATE user SET name=?, surname=?, email=?, password=?, phone = ? WHERE id=?";
    private static final String GET_ALL_USERS = "SELECT * from user";
    private static final String ADD_USER = "INSERT INTO user (name, surname, password, email, phone) VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_USER_IN_ORDER = "SELECT * FROM user_order WHERE user_id=?";
    private static final String DELETE = "DELETE FROM user WHERE id=?";

    @Override
    public long add(User user) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(ADD_USER, Statement.RETURN_GENERATED_KEYS)) {
                int k = 0;
                ps.setString(++k, user.getName());
                ps.setString(++k, user.getSurname());
                ps.setString(++k, user.getPassword());
                ps.setString(++k, user.getEmail());
                ps.setString(++k, user.getPhone());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        user.setId(keys.getLong(1));
                    }
                }
            }
            return user.getId();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(User user) {
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(UPDATE)) {
                int k = 0;
                ps.setString(++k, user.getName());
                ps.setString(++k, user.getSurname());
                ps.setString(++k, user.getEmail());
                ps.setString(++k, user.getPassword());
                ps.setString(++k, user.getPhone());
                ps.setLong(++k, user.getId());
                ps.executeUpdate();
            }
            return findById(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void delete(long id) {
        try {
            try (Connection con = connectionManager.getConnection()) {
                try (PreparedStatement st = con.prepareStatement(FIND_USER_IN_ORDER)) {
                    st.setLong(1, id);
                    try (ResultSet resultSet = st.executeQuery()) {
                        if (resultSet.next())
                            throw new SQLException("delete failed. " +
                                    "To delete user, please, firstly delete order with this user");
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
    public User findById(long id) {
        User user = new User();
        try (Connection con = connectionManager.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(GET_USER_BY_ID)) {
                int k = 0;
                ps.setLong(++k, id);
                try (ResultSet resultSet = ps.executeQuery()) {
                    while (resultSet.next()) {
                        user = mapUsers(resultSet);
                    }
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll(){
        List<User> userList = new ArrayList<>();
        try (Connection con = connectionManager.getConnection()) {
            try (Statement st = con.createStatement()) {
                try (ResultSet rs = st.executeQuery(GET_ALL_USERS)) {
                    while (rs.next()) {
                        userList.add(mapUsers(rs));
                    }
                    return userList;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User mapUsers(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setSurname(rs.getString("surname"));
        u.setPassword(rs.getString("password"));
        u.setEmail(rs.getString("email"));
        u.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
        u.setPhone(rs.getString("phone"));
        return u;
    }
}

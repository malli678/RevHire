package com.revhire.service;

import com.revhire.dao.UserDAO;
import com.revhire.model.User;
import com.revhire.util.PasswordUtil;
import java.sql.SQLException;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    
    public User register(String email, String password, String role, String name, String phone, String location) throws SQLException {
        if (userDAO.emailExists(email)) {
            throw new SQLException("Email already registered");
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setRole(role);
        user.setName(name);
        user.setPhone(phone);
        user.setLocation(location);
        
        int userId = userDAO.registerUser(user);
        if (userId > 0) {
            user.setId(userId);
            return user;
        }
        return null;
    }
    
    public User login(String email, String password) throws SQLException {
        String hashedPassword = PasswordUtil.hashPassword(password);
        return userDAO.loginUser(email, hashedPassword);
    }
    
    public boolean updateProfile(int userId, String name, String phone, String location) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            user.setLocation(location);
            return userDAO.updateUser(user);
        }
        return false;
    }
}
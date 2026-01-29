package com.revhire.service;

import com.revhire.dao.UserDAO;
import com.revhire.model.User;
import com.revhire.util.PasswordUtil;
import java.sql.SQLException;
import java.util.Random;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    
    // Existing methods
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
    
    // NEW: Change Password Feature
    public boolean changePassword(int userId, String currentPassword, String newPassword) throws SQLException {
        User user = userDAO.getUserById(userId);
        if (user != null) {
            String currentHashed = PasswordUtil.hashPassword(currentPassword);
            if (user.getPassword().equals(currentHashed)) {
                String newHashed = PasswordUtil.hashPassword(newPassword);
                return userDAO.updatePassword(userId, newHashed);
            }
        }
        return false;
    }
    
    // NEW: Get user by email (for forgot password)
    public User getUserByEmail(String email) throws SQLException {
        return userDAO.getUserByEmail(email);
    }
    
    // NEW: Forgot Password - Security Question (simplified for demo)
    public String generateSecurityQuestion(int userId) throws SQLException {
        // In real app, you'd store/retrieve from database
        // For demo, return a generic question
        return "What is your favorite color?";
    }
    
    // NEW: Verify security answer (simplified for demo)
    public boolean verifySecurityAnswer(int userId, String answer) throws SQLException {
        // In real app, verify against stored answer
        // For demo, accept any non-empty answer
        return answer != null && !answer.trim().isEmpty();
    }
    
    // NEW: Reset password
    public boolean resetPassword(int userId, String newPassword) throws SQLException {
        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(userId, hashedPassword);
    }
    
    // NEW: Generate temporary password
    public String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
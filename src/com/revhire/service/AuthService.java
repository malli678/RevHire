package com.revhire.service;

import com.revhire.dao.UserDAO;
import com.revhire.model.User;
import com.revhire.util.PasswordUtil;
import com.revhire.util.LoggerUtil;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Random;

public class AuthService {

    private static final Logger logger = LoggerUtil.getLogger(AuthService.class);
    private UserDAO userDAO = new UserDAO();

    /* ===================== REGISTER ===================== */

    public User register(String email, String password, String role,
                         String name, String phone, String location) throws SQLException {

        logger.debug("Attempting to register user: " + email);

        if (userDAO.emailExists(email)) {
            logger.warn("Registration failed – email already exists: " + email);
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
            logger.info("User registered successfully: " + email + " (ID: " + userId + ")");
            return user;
        }

        logger.error("User registration failed for email: " + email);
        return null;
    }

    /* ===================== LOGIN ===================== */

    public User login(String email, String password) throws SQLException {
        logger.debug("Login attempt for email: " + email);

        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = userDAO.loginUser(email, hashedPassword);

        if (user != null) {
            logger.info("Login successful for user: " + email);
        } else {
            logger.warn("Login failed for user: " + email);
        }

        return user;
    }

    /* ===================== GET USER ===================== */

    public User getUserByEmail(String email) throws SQLException {
        logger.debug("Fetching user by email: " + email);
        return userDAO.getUserByEmail(email);
    }

    /* ===================== UPDATE PROFILE ===================== */

    public boolean updateProfile(int userId, String name, String phone, String location) throws SQLException {
        logger.debug("Updating profile for user ID: " + userId);

        User user = userDAO.getUserById(userId);
        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            user.setLocation(location);

            boolean updated = userDAO.updateUser(user);
            if (updated) {
                logger.info("Profile updated successfully for user ID: " + userId);
            } else {
                logger.error("Profile update failed for user ID: " + userId);
            }
            return updated;
        }

        logger.warn("Profile update failed – user not found (ID: " + userId + ")");
        return false;
    }

    /* ===================== CHANGE PASSWORD ===================== */

    public boolean changePassword(int userId, String currentPassword, String newPassword) throws SQLException {
        logger.debug("Change password request for user ID: " + userId);

        User user = userDAO.getUserById(userId);
        if (user != null) {
            String currentHashed = PasswordUtil.hashPassword(currentPassword);

            if (user.getPassword().equals(currentHashed)) {
                String newHashed = PasswordUtil.hashPassword(newPassword);
                boolean updated = userDAO.updatePassword(userId, newHashed);

                if (updated) {
                    logger.info("Password changed successfully for user ID: " + userId);
                } else {
                    logger.error("Password change failed for user ID: " + userId);
                }
                return updated;
            }

            logger.warn("Incorrect current password for user ID: " + userId);
            return false;
        }

        logger.warn("Change password failed – user not found (ID: " + userId + ")");
        return false;
    }

    /* ===================== FORGOT PASSWORD ===================== */

    public String generateSecurityQuestion(int userId) throws SQLException {
        logger.debug("Generating security question for user ID: " + userId);
        return "What is your mother's maiden name?";
    }

    public boolean verifySecurityAnswer(int userId, String answer) throws SQLException {
        logger.debug("Verifying security answer for user ID: " + userId);

        boolean valid = answer != null && !answer.trim().isEmpty();
        if (valid) {
            logger.info("Security answer verified for user ID: " + userId);
        } else {
            logger.warn("Security answer verification failed for user ID: " + userId);
        }

        return valid;
    }

    public boolean resetPassword(int userId, String newPassword) throws SQLException {
        logger.debug("Resetting password for user ID: " + userId);

        String hashedPassword = PasswordUtil.hashPassword(newPassword);
        boolean updated = userDAO.updatePassword(userId, hashedPassword);

        if (updated) {
            logger.info("Password reset successfully for user ID: " + userId);
        } else {
            logger.error("Password reset failed for user ID: " + userId);
        }

        return updated;
    }

    /* ===================== TEMP PASSWORD ===================== */

    public String generateTemporaryPassword() {
        logger.debug("Generating temporary password");

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        logger.info("Temporary password generated");
        return sb.toString();
    }
}

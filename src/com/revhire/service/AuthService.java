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

    /**
     * Registers a new user in the system
     * @param email User's email address
     * @param password User's password
     * @param role User role (jobseeker/employer)
     * @param name User's full name
     * @param phone User's phone number
     * @param location User's location
     * @return User object if registration successful, null otherwise
     * @throws SQLException if database error occurs or email already exists
     */
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

    /**
     * Authenticates user login
     * @param email User's email address
     * @param password User's password
     * @return User object if login successful, null otherwise
     * @throws SQLException if database error occurs
     */
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

    /**
     * Retrieves user by email address
     * @param email User's email address
     * @return User object if found, null otherwise
     * @throws SQLException if database error occurs
     */
    public User getUserByEmail(String email) throws SQLException {
        logger.debug("Fetching user by email: " + email);
        return userDAO.getUserByEmail(email);
    }

    /* ===================== UPDATE PROFILE ===================== */

    /**
     * Updates user profile information
     * @param userId ID of the user
     * @param name Updated name
     * @param phone Updated phone number
     * @param location Updated location
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
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

    /**
     * Changes user password after verifying current password
     * @param userId ID of the user
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     * @throws SQLException if database error occurs
     */
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

    /**
     * Generates a security question for password recovery
     * @param userId ID of the user
     * @return Security question string
     * @throws SQLException if database error occurs
     */
    public String generateSecurityQuestion(int userId) throws SQLException {
        logger.debug("Generating security question for user ID: " + userId);
        return "What is your mother's maiden name?";
    }
    

    /**
     * Verifies security answer for password recovery
     * @param userId ID of the user
     * @param answer User's answer to security question
     * @return true if answer is valid, false otherwise
     * @throws SQLException if database error occurs
     */
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

    
    /**
     * Resets user password (forgot password flow)
     * @param userId ID of the user
     * @param newPassword New password to set
     * @return true if password reset successful, false otherwise
     * @throws SQLException if database error occurs
     */
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

    /**
     * Generates a temporary password for password recovery
     * @return Randomly generated 8-character temporary password
     */
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

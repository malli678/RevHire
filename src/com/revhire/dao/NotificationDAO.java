package com.revhire.dao;

import com.revhire.model.Notification;
import com.revhire.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    /**
     * Creates a new notification in the database
     * @param notification The notification object to create
     * @return The generated notification ID, or -1 if creation failed
     * @throws SQLException if database error occurs
     */
    public int createNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (id, user_id, message, is_read) VALUES (notif_seq.NEXTVAL, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, new String[]{"id"});
            
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getMessage());
            // Directly use the int value - no null check needed for primitive
            ps.setInt(3, notification.getIsRead());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } finally {
            closeResources(rs, ps, conn);
        }
    }
    
    /**
     * Retrieves all notifications for a specific user
     * @param userId The ID of the user
     * @return List of notifications for the user
     * @throws SQLException if database error occurs
     */
    public List<Notification> getNotificationsByUser(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<Notification>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setMessage(rs.getString("message"));
                notification.setIsRead(rs.getInt("is_read"));
                notification.setCreatedAt(rs.getDate("created_at"));
                notifications.add(notification);
            }
        } finally {
            closeResources(rs, ps, conn);
        }
        return notifications;
    }
    
    /**
     * Retrieves a notification by its ID
     * @param notificationId The ID of the notification
     * @return The Notification object, or null if not found
     * @throws SQLException if database error occurs
     */
    public Notification getNotificationById(int notificationId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setMessage(rs.getString("message"));
                notification.setIsRead(rs.getInt("is_read"));
                notification.setCreatedAt(rs.getDate("created_at"));
                return notification;
            }
            return null;
        } finally {
            closeResources(rs, ps, conn);
        }
    }
    
    /**
     * Marks a specific notification as read
     * @param notificationId The ID of the notification to mark as read
     * @return true if the operation was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Marks all unread notifications for a user as read
     * @param userId The ID of the user
     * @return true if any notifications were updated, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Gets the count of unread notifications for a user
     * @param userId The ID of the user
     * @return The number of unread notifications
     * @throws SQLException if database error occurs
     */
    public int getUnreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(rs, ps, conn);
        }
    }
    
    /**
     * Deletes a specific notification
     * @param notificationId The ID of the notification to delete
     * @return true if the notification was deleted, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean deleteNotification(int notificationId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Deletes all notifications for a user
     * @param userId The ID of the user
     * @return The number of notifications deleted
     * @throws SQLException if database error occurs
     */
    public int clearAllNotifications(int userId) throws SQLException {
        String sql = "DELETE FROM notifications WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeUpdate();
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Retrieves recent notifications for a user (with limit)
     * @param userId The ID of the user
     * @param limit Maximum number of notifications to retrieve
     * @return List of recent notifications
     * @throws SQLException if database error occurs
     */
    public List<Notification> getRecentNotifications(int userId, int limit) throws SQLException {
        List<Notification> notifications = new ArrayList<Notification>();
        // For Oracle older versions, use ROWNUM instead of FETCH FIRST
        String sql = "SELECT * FROM (SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC) WHERE ROWNUM <= ?";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Notification notification = new Notification();
                notification.setId(rs.getInt("id"));
                notification.setUserId(rs.getInt("user_id"));
                notification.setMessage(rs.getString("message"));
                notification.setIsRead(rs.getInt("is_read"));
                notification.setCreatedAt(rs.getDate("created_at"));
                notifications.add(notification);
            }
        } finally {
            closeResources(rs, ps, conn);
        }
        return notifications;
    }
    
    /**
     * Updates a notification message
     * @param notificationId The ID of the notification to update
     * @param newMessage The new message content
     * @return true if the update was successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateNotificationMessage(int notificationId, String newMessage) throws SQLException {
        String sql = "UPDATE notifications SET message = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, newMessage);
            ps.setInt(2, notificationId);
            return ps.executeUpdate() > 0;
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Checks if a notification exists for a user
     * @param userId The ID of the user
     * @param message The message to check
     * @return true if a similar notification exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean notificationExists(int userId, String message) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND message = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setString(2, message);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }
    
    /**
     * Gets total count of notifications for a user
     * @param userId The ID of the user
     * @return Total number of notifications
     * @throws SQLException if database error occurs
     */
    public int getTotalNotificationCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            closeResources(rs, ps, conn);
        }
    }
    
    /**
     * Deletes old notifications (for cleanup purposes)
     * @param daysOld Delete notifications older than this many days
     * @return Number of notifications deleted
     * @throws SQLException if database error occurs
     */
    public int deleteOldNotifications(int daysOld) throws SQLException {
        String sql = "DELETE FROM notifications WHERE created_at < SYSDATE - ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, daysOld);
            return ps.executeUpdate();
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Marks multiple notifications as read in batch
     * @param notificationIds List of notification IDs to mark as read
     * @return Number of notifications marked as read
     * @throws SQLException if database error occurs
     */
    public int markMultipleAsRead(List<Integer> notificationIds) throws SQLException {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        
        StringBuilder sql = new StringBuilder("UPDATE notifications SET is_read = 1 WHERE id IN (");
        for (int i = 0; i < notificationIds.size(); i++) {
            sql.append("?");
            if (i < notificationIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < notificationIds.size(); i++) {
                ps.setInt(i + 1, notificationIds.get(i));
            }
            
            return ps.executeUpdate();
        } finally {
            closeResources(null, ps, conn);
        }
    }
    
    /**
     * Helper method to close database resources
     */
    private void closeResources(ResultSet rs, PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (ps != null) {
            try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
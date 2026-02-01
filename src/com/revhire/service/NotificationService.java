package com.revhire.service;

import com.revhire.dao.NotificationDAO;
import com.revhire.model.Notification;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class NotificationService {
    
    private NotificationDAO notificationDAO;
    private Scanner scanner;
    
    /**
     * Constructor - initializes NotificationDAO and Scanner
     */
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Constructor with Scanner injection
     */
    public NotificationService(Scanner scanner) {
        this.notificationDAO = new NotificationDAO();
        this.scanner = scanner;
    }
    
    /**
     * Displays notification menu and handles user interaction
     * @param userId ID of the logged-in user
     * @throws SQLException if database error occurs
     */
    public void showNotificationMenu(int userId) throws SQLException {
        while (true) {
            int unreadCount = getUnreadCount(userId);
            System.out.println("\n============================================================");
            System.out.println("                       Notifications");
            System.out.println("============================================================");
            System.out.println("Total Unread: " + unreadCount);
            System.out.println("============================================================");
            System.out.println("1. View All Notifications");
            System.out.println("2. View Unread Notifications");
            System.out.println("3. View Recent Notifications (Last 10)");
            System.out.println("4. Mark All as Read");
            System.out.println("5. Clear All Notifications");
            System.out.println("6. Mark Specific Notification as Read");
            System.out.println("7. Delete Specific Notification");
            System.out.println("8. Back to Dashboard");
            System.out.println("============================================================");
            System.out.print("Enter your choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                
                switch (choice) {
                    case 1:
                        viewAllNotifications(userId);
                        break;
                    case 2:
                        viewUnreadNotifications(userId);
                        break;
                    case 3:
                        viewRecentNotifications(userId, 10);
                        break;
                    case 4:
                        markAllAsRead(userId);
                        System.out.println("\nAll notifications marked as read!");
                        pause();
                        break;
                    case 5:
                        clearAllNotificationsConfirmation(userId);
                        break;
                    case 6:
                        markSpecificAsRead(userId);
                        break;
                    case 7:
                        deleteSpecificNotification(userId);
                        break;
                    case 8:
                        return;
                    default:
                        System.out.println("\nInvalid choice. Please try again.");
                        pause();
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number.");
                pause();
            } catch (SQLException e) {
                System.out.println("\nDatabase error: " + e.getMessage());
                pause();
            }
        }
    }
    
    /**
     * View all notifications with pagination
     */
    private void viewAllNotifications(int userId) throws SQLException {
        List<Notification> notifications = getNotificationsByUser(userId);
        displayNotifications(notifications, "All Notifications");
        
        if (!notifications.isEmpty()) {
            showNotificationActions(userId);
        }
    }
    
    /**
     * View only unread notifications
     */
    private void viewUnreadNotifications(int userId) throws SQLException {
        List<Notification> notifications = getUnreadNotifications(userId);
        displayNotifications(notifications, "Unread Notifications");
        
        if (!notifications.isEmpty()) {
            System.out.println("\nOptions:");
            System.out.println("1. Mark all displayed as read");
            System.out.println("2. Mark specific as read");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        for (Notification n : notifications) {
                            markAsRead(n.getId());
                        }
                        System.out.println("\nAll displayed notifications marked as read!");
                        pause();
                        break;
                    case 2:
                        markSpecificAsRead(userId);
                        break;
                    case 3:
                        break;
                    default:
                        System.out.println("\nInvalid choice.");
                        pause();
                }
            } catch (NumberFormatException e) {
                System.out.println("\nPlease enter a valid number.");
                pause();
            }
        }
    }
    
    /**
     * View recent notifications
     */
    private void viewRecentNotifications(int userId, int limit) throws SQLException {
        List<Notification> notifications = getRecentNotifications(userId, limit);
        displayNotifications(notifications, "Recent Notifications (Last " + limit + ")");
        
        if (!notifications.isEmpty()) {
            showNotificationActions(userId);
        }
    }
    
    /**
     * Display notifications in a formatted way
     */
    private void displayNotifications(List<Notification> notifications, String title) {
        System.out.println("\n============================================================");
        System.out.println("                     " + title);
        System.out.println("============================================================");
        
        if (notifications.isEmpty()) {
            System.out.println("\nNo notifications found.");
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            return;
        }
        
        System.out.println("Total: " + notifications.size());
        System.out.println("================================================================================\n");
        
        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            String status = (n.getIsRead() == 0) ? "[NEW]" : "[READ]";
            System.out.println((i + 1) + ". " + status + " " + n.getCreatedAt() + ": " + n.getMessage());
        }
        
        System.out.println("\n================================================================================\n");
    }
    
    /**
     * Show notification actions after viewing
     */
    private void showNotificationActions(int userId) throws SQLException {
        System.out.println("\nOptions:");
        System.out.println("1. Mark all as read");
        System.out.println("2. Mark specific notification as read");
        System.out.println("3. Delete specific notification");
        System.out.println("4. Back to menu");
        System.out.print("Enter choice: ");
        
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    markAllAsRead(userId);
                    System.out.println("\nAll notifications marked as read!");
                    pause();
                    break;
                case 2:
                    markSpecificAsRead(userId);
                    break;
                case 3:
                    deleteSpecificNotification(userId);
                    break;
                case 4:
                    break;
                default:
                    System.out.println("\nInvalid choice.");
                    pause();
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number.");
            pause();
        }
    }
    
    /**
     * Mark a specific notification as read
     */
    private void markSpecificAsRead(int userId) throws SQLException {
        List<Notification> notifications = getNotificationsByUser(userId);
        if (notifications.isEmpty()) {
            System.out.println("\nNo notifications to mark as read.");
            pause();
            return;
        }
        
        System.out.print("\nEnter notification number to mark as read (1-" + notifications.size() + "): ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < notifications.size()) {
                boolean success = markAsRead(notifications.get(index).getId());
                if (success) {
                    System.out.println("\nNotification marked as read!");
                } else {
                    System.out.println("\nFailed to mark notification as read.");
                }
            } else {
                System.out.println("\nInvalid notification number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number.");
        }
        pause();
    }
    
    /**
     * Delete a specific notification
     */
    private void deleteSpecificNotification(int userId) throws SQLException {
        List<Notification> notifications = getNotificationsByUser(userId);
        if (notifications.isEmpty()) {
            System.out.println("\nNo notifications to delete.");
            pause();
            return;
        }
        
        System.out.print("\nEnter notification number to delete (1-" + notifications.size() + "): ");
        try {
            int index = Integer.parseInt(scanner.nextLine()) - 1;
            if (index >= 0 && index < notifications.size()) {
                System.out.print("Are you sure you want to delete this notification? (y/n): ");
                String confirm = scanner.nextLine().toLowerCase();
                if (confirm.equals("y") || confirm.equals("yes")) {
                    boolean success = deleteNotification(notifications.get(index).getId());
                    if (success) {
                        System.out.println("\nNotification deleted successfully!");
                    } else {
                        System.out.println("\nFailed to delete notification.");
                    }
                }
            } else {
                System.out.println("\nInvalid notification number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a valid number.");
        }
        pause();
    }
    
    /**
     * Confirm before clearing all notifications
     */
    private void clearAllNotificationsConfirmation(int userId) throws SQLException {
        System.out.print("\nAre you sure you want to clear ALL notifications? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        if (confirm.equals("y") || confirm.equals("yes")) {
            int deletedCount = clearAllNotifications(userId);
            System.out.println("\n" + deletedCount + " notifications cleared.");
        } else {
            System.out.println("\nOperation cancelled.");
        }
        pause();
    }
    
    /**
     * Pause and wait for user to press Enter
     */
    private void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    // ================ Core DAO Wrapper Methods ================
    
    /**
     * Sends a notification to a user
     * @param userId ID of the user to notify
     * @param message Notification message
     * @return notification ID if sent successfully, -1 otherwise
     * @throws SQLException if database error occurs
     */
    public int sendNotification(int userId, String message) throws SQLException {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setIsRead(0); // Default to unread
        return notificationDAO.createNotification(notification);
    }
    
    /**
     * Sends a notification to a user with status return
     * @param userId ID of the user to notify
     * @param message Notification message
     * @return true if notification sent successfully, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean sendNotificationWithStatus(int userId, String message) throws SQLException {
        int result = sendNotification(userId, message);
        return result > 0;
    }
    
    /**
     * Retrieves all notifications for a user
     * @param userId ID of the user
     * @return List of Notification objects
     * @throws SQLException if database error occurs
     */
    public List<Notification> getNotificationsByUser(int userId) throws SQLException {
        return notificationDAO.getNotificationsByUser(userId);
    }
    
    /**
     * Retrieves a notification by its ID
     * @param notificationId ID of the notification
     * @return Notification object or null if not found
     * @throws SQLException if database error occurs
     */
    public Notification getNotificationById(int notificationId) throws SQLException {
        return notificationDAO.getNotificationById(notificationId);
    }
    
    /**
     * Marks a notification as read
     * @param notificationId ID of the notification to mark as read
     * @return true if marked successfully, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean markAsRead(int notificationId) throws SQLException {
        return notificationDAO.markAsRead(notificationId);
    }
    
    /**
     * Marks all notifications for a user as read
     * @param userId ID of the user
     * @return true if marked successfully, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean markAllAsRead(int userId) throws SQLException {
        return notificationDAO.markAllAsRead(userId);
    }
    
    /**
     * Deletes a notification
     * @param notificationId ID of the notification to delete
     * @return true if deleted successfully, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean deleteNotification(int notificationId) throws SQLException {
        return notificationDAO.deleteNotification(notificationId);
    }
    
    /**
     * Gets count of unread notifications for a user
     * @param userId ID of the user
     * @return Number of unread notifications
     * @throws SQLException if database error occurs
     */
    public int getUnreadCount(int userId) throws SQLException {
        return notificationDAO.getUnreadCount(userId);
    }
    
    /**
     * Sends batch notifications to multiple users
     * @param userIds List of user IDs to notify
     * @param message Notification message
     * @return Number of notifications sent successfully
     * @throws SQLException if database error occurs
     */
    public int sendBatchNotifications(List<Integer> userIds, String message) throws SQLException {
        int successCount = 0;
        for (int userId : userIds) {
            int result = sendNotification(userId, message);
            if (result > 0) {
                successCount++;
            }
        }
        return successCount;
    }
    
    /**
     * Clears all notifications for a user
     * @param userId ID of the user
     * @return Number of notifications cleared
     * @throws SQLException if database error occurs
     */
    public int clearAllNotifications(int userId) throws SQLException {
        return notificationDAO.clearAllNotifications(userId);
    }
    
    /**
     * Retrieves recent notifications for a user
     * @param userId ID of the user
     * @param limit Maximum number of notifications to retrieve
     * @return List of recent notifications
     * @throws SQLException if database error occurs
     */
    public List<Notification> getRecentNotifications(int userId, int limit) throws SQLException {
        return notificationDAO.getRecentNotifications(userId, limit);
    }
    
    /**
     * Updates a notification message
     * @param notificationId ID of the notification to update
     * @param newMessage New message content
     * @return true if updated successfully, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateNotificationMessage(int notificationId, String newMessage) throws SQLException {
        return notificationDAO.updateNotificationMessage(notificationId, newMessage);
    }
    
    /**
     * Checks if a similar notification already exists for a user
     * @param userId ID of the user
     * @param message Message to check
     * @return true if notification exists, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean notificationExists(int userId, String message) throws SQLException {
        return notificationDAO.notificationExists(userId, message);
    }
    
    /**
     * Gets total count of notifications for a user
     * @param userId ID of the user
     * @return Total number of notifications
     * @throws SQLException if database error occurs
     */
    public int getTotalNotificationCount(int userId) throws SQLException {
        return notificationDAO.getTotalNotificationCount(userId);
    }
    
    /**
     * Deletes old notifications (cleanup)
     * @param daysOld Delete notifications older than this many days
     * @return Number of notifications deleted
     * @throws SQLException if database error occurs
     */
    public int deleteOldNotifications(int daysOld) throws SQLException {
        return notificationDAO.deleteOldNotifications(daysOld);
    }
    
    /**
     * Sends notification and marks it as read immediately
     * @param userId ID of the user
     * @param message Notification message
     * @return notification ID if sent successfully, -1 otherwise
     * @throws SQLException if database error occurs
     */
    public int sendAndMarkAsRead(int userId, String message) throws SQLException {
        int notificationId = sendNotification(userId, message);
        if (notificationId > 0) {
            markAsRead(notificationId);
        }
        return notificationId;
    }
    
    /**
     * Gets unread notifications for a user
     * @param userId ID of the user
     * @return List of unread notifications
     * @throws SQLException if database error occurs
     */
    public List<Notification> getUnreadNotifications(int userId) throws SQLException {
        List<Notification> allNotifications = getNotificationsByUser(userId);
        List<Notification> unreadNotifications = new java.util.ArrayList<Notification>();
        
        for (Notification notification : allNotifications) {
            if (notification.getIsRead() == 0) {
                unreadNotifications.add(notification);
            }
        }
        return unreadNotifications;
    }
    
    /**
     * Gets read notifications for a user
     * @param userId ID of the user
     * @return List of read notifications
     * @throws SQLException if database error occurs
     */
    public List<Notification> getReadNotifications(int userId) throws SQLException {
        List<Notification> allNotifications = getNotificationsByUser(userId);
        List<Notification> readNotifications = new java.util.ArrayList<Notification>();
        
        for (Notification notification : allNotifications) {
            if (notification.getIsRead() == 1) {
                readNotifications.add(notification);
            }
        }
        return readNotifications;
    }
    
    /**
     * Main method for testing
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        NotificationService service = new NotificationService();
        
        try {
            // Test sending a notification
            System.out.println("Testing Notification Service...");
            
            // Example: Send a notification
            int notificationId = service.sendNotification(1, "Welcome to RevHire!");
            if (notificationId > 0) {
                System.out.println("Notification sent successfully with ID: " + notificationId);
                
                // Mark it as read
                boolean marked = service.markAsRead(notificationId);
                if (marked) {
                    System.out.println("Notification marked as read");
                }
                
                // Get unread count
                int unreadCount = service.getUnreadCount(1);
                System.out.println("Unread notifications: " + unreadCount);
                
                // Get all notifications
                List<Notification> notifications = service.getNotificationsByUser(1);
                System.out.println("Total notifications: " + notifications.size());
            } else {
                System.out.println("Failed to send notification");
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.revhire.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.revhire.dao.NotificationDAO;
import com.revhire.model.Notification;
import com.revhire.service.NotificationService;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {
    
    @Mock
    private NotificationDAO notificationDAO;
    
    private NotificationService notificationService;
    
    private List<Notification> testNotifications;
    
    @Before
    public void setUp() throws Exception {
        // Create test notification list
        Notification notif1 = new Notification();
        notif1.setId(1);
        notif1.setUserId(101);
        notif1.setMessage("Notification 1");
        notif1.setIsRead(0);
        
        Notification notif2 = new Notification();
        notif2.setId(2);
        notif2.setUserId(101);
        notif2.setMessage("Notification 2");
        notif2.setIsRead(1);
        
        testNotifications = Arrays.asList(notif1, notif2);
        
        // Create NotificationService manually using reflection
        notificationService = new NotificationService();
        
        // Inject mock DAO using reflection
        java.lang.reflect.Field daoField = NotificationService.class.getDeclaredField("notificationDAO");
        daoField.setAccessible(true);
        daoField.set(notificationService, notificationDAO);
        
        // Mock behaviors
        when(notificationDAO.getNotificationsByUser(101)).thenReturn(testNotifications);
        when(notificationDAO.getUnreadCount(101)).thenReturn(1);
        when(notificationDAO.markAsRead(1)).thenReturn(true);
        when(notificationDAO.markAllAsRead(101)).thenReturn(true);
        when(notificationDAO.deleteNotification(1)).thenReturn(true);
        when(notificationDAO.clearAllNotifications(101)).thenReturn(2);
        when(notificationDAO.getRecentNotifications(101, 10)).thenReturn(testNotifications);
        when(notificationDAO.getNotificationById(1)).thenReturn(notif1);
        when(notificationDAO.getTotalNotificationCount(101)).thenReturn(2);
        when(notificationDAO.notificationExists(101, "Test message")).thenReturn(false);
        when(notificationDAO.updateNotificationMessage(1, "Updated")).thenReturn(true);
        when(notificationDAO.deleteOldNotifications(30)).thenReturn(5);
    }
    
    @Test
    public void testGetNotificationsByUser() throws SQLException {
        List<Notification> result = notificationService.getNotificationsByUser(101);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationDAO).getNotificationsByUser(101);
    }
    
    @Test
    public void testGetNotificationById() throws SQLException {
        Notification result = notificationService.getNotificationById(1);
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(notificationDAO).getNotificationById(1);
    }
    
    @Test
    public void testGetUnreadCount() throws SQLException {
        int result = notificationService.getUnreadCount(101);
        assertEquals(1, result);
        verify(notificationDAO).getUnreadCount(101);
    }
    
    @Test
    public void testMarkAsReadSuccess() throws SQLException {
        boolean result = notificationService.markAsRead(1);
        assertTrue(result);
        verify(notificationDAO).markAsRead(1);
    }
    
    @Test
    public void testMarkAllAsRead() throws SQLException {
        boolean result = notificationService.markAllAsRead(101);
        assertTrue(result);
        verify(notificationDAO).markAllAsRead(101);
    }
    
    @Test
    public void testClearAllNotifications() throws SQLException {
        int result = notificationService.clearAllNotifications(101);
        assertEquals(2, result);
        verify(notificationDAO).clearAllNotifications(101);
    }
    
    @Test
    public void testDeleteNotification() throws SQLException {
        boolean result = notificationService.deleteNotification(1);
        assertTrue(result);
        verify(notificationDAO).deleteNotification(1);
    }
    
    @Test
    public void testGetUnreadNotifications() throws SQLException {
        List<Notification> result = notificationService.getUnreadNotifications(101);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }
    
    @Test
    public void testGetReadNotifications() throws SQLException {
        List<Notification> result = notificationService.getReadNotifications(101);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
    }
    
    @Test
    public void testGetRecentNotifications() throws SQLException {
        List<Notification> result = notificationService.getRecentNotifications(101, 10);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(notificationDAO).getRecentNotifications(101, 10);
    }
    
    @Test
    public void testUpdateNotificationMessage() throws SQLException {
        boolean result = notificationService.updateNotificationMessage(1, "Updated");
        assertTrue(result);
        verify(notificationDAO).updateNotificationMessage(1, "Updated");
    }
    
    @Test
    public void testNotificationExists() throws SQLException {
        boolean result = notificationService.notificationExists(101, "Test message");
        assertFalse(result);
        verify(notificationDAO).notificationExists(101, "Test message");
    }
    
    @Test
    public void testGetTotalNotificationCount() throws SQLException {
        int result = notificationService.getTotalNotificationCount(101);
        assertEquals(2, result);
        verify(notificationDAO).getTotalNotificationCount(101);
    }
    
    @Test
    public void testDeleteOldNotifications() throws SQLException {
        int result = notificationService.deleteOldNotifications(30);
        assertEquals(5, result);
        verify(notificationDAO).deleteOldNotifications(30);
    }
}
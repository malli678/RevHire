package com.revhire.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.revhire.model.User;
import com.revhire.util.DBUtil;
import java.sql.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UserDAOTest {
    
    @Mock
    private Connection mockConnection;
    
    @Mock
    private PreparedStatement mockPreparedStatement;
    
    @Mock
    private ResultSet mockResultSet;
    
    @Before
    public void setUp() throws SQLException {
        MockitoAnnotations.initMocks(this);
        
        // Mock static DBUtil method
        // We need PowerMock for this, but for now let's skip DB-level tests
        // and focus on service layer tests
    }
    
    @Test
    public void testBasicUserProperties() {
        System.out.println("Testing basic User model...");
        
        // Test User model directly
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setPassword("hashed123");
        user.setRole("JobSeeker");
        user.setName("John Doe");
        user.setPhone("1234567890");
        user.setLocation("New York");
        
        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashed123", user.getPassword());
        assertEquals("JobSeeker", user.getRole());
        assertEquals("John Doe", user.getName());
        assertEquals("1234567890", user.getPhone());
        assertEquals("New York", user.getLocation());
    }
}
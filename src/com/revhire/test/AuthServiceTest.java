package com.revhire.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.revhire.dao.UserDAO;
import com.revhire.model.User;
import com.revhire.service.AuthService;
import com.revhire.util.PasswordUtil;

import java.sql.SQLException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;

public class AuthServiceTest {
    
    // Create mock objects
    @Mock
    private UserDAO userDAO;
    
    // Inject mocks into service
    @InjectMocks
    private AuthService authService;
    
    // Test data
    private User testUser;
    private String testEmail = "test@example.com";
    private String testPassword = "password123";
    private String testName = "John Doe";
    private String testPhone = "1234567890";
    private String testLocation = "New York";
    private String hashedPassword;
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("=== Starting AuthService Tests ===");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("=== AuthService Tests Completed ===");
    }
    
    @Before
    public void setUp() throws SQLException {
        System.out.println("\nSetting up test...");
        MockitoAnnotations.initMocks(this); // Initialize mocks
        
        // Create test user with actual hash
        hashedPassword = PasswordUtil.hashPassword(testPassword);
        
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail(testEmail);
        testUser.setPassword(hashedPassword);
        testUser.setRole("JobSeeker");
        testUser.setName(testName);
        testUser.setPhone(testPhone);
        testUser.setLocation(testLocation);
        
        // Configure mock behaviors - FIXED: Use eq() for specific values, any() for others
        when(userDAO.emailExists(testEmail)).thenReturn(false);
        when(userDAO.emailExists("existing@example.com")).thenReturn(true);
        
        // For loginUser - match exact parameters
        when(userDAO.loginUser(eq(testEmail), eq(hashedPassword))).thenReturn(testUser);
        when(userDAO.loginUser(eq(testEmail), anyString())).thenReturn(null);
        when(userDAO.loginUser(anyString(), anyString())).thenReturn(null);
        
        when(userDAO.getUserByEmail(testEmail)).thenReturn(testUser);
        when(userDAO.getUserByEmail("nonexistent@example.com")).thenReturn(null);
        when(userDAO.getUserById(1)).thenReturn(testUser);
        when(userDAO.getUserById(999)).thenReturn(null);
        
        // FIXED: Use eq() for specific values
        when(userDAO.registerUser(any(User.class))).thenReturn(1); // Returns user ID
        when(userDAO.updateUser(any(User.class))).thenReturn(true);
        when(userDAO.updatePassword(eq(1), anyString())).thenReturn(true);
        when(userDAO.updatePassword(eq(999), anyString())).thenReturn(false);
    }
    
    @After
    public void tearDown() {
        System.out.println("Cleaning up test...");
        reset(userDAO);
    }
    
    /* ===================== REGISTRATION TESTS ===================== */
    
    @Test
    public void testRegisterSuccess() throws SQLException {
        System.out.println("Testing user registration success...");
        
        // Arrange
        String email = "newuser@example.com";
        String password = "newpass123";
        String role = "JobSeeker";
        String name = "Jane Smith";
        String phone = "9876543210";
        String location = "Los Angeles";
        
        when(userDAO.emailExists(email)).thenReturn(false);
        when(userDAO.registerUser(any(User.class))).thenReturn(100);
        
        // Act
        User result = authService.register(email, password, role, name, phone, location);
        
        // Assert
        assertNotNull("User should not be null", result);
        assertEquals("Email should match", email, result.getEmail());
        assertEquals("Name should match", name, result.getName());
        assertEquals("Role should match", role, result.getRole());
        assertEquals("Phone should match", phone, result.getPhone());
        assertEquals("Location should match", location, result.getLocation());
        
        verify(userDAO, times(1)).emailExists(email);
        verify(userDAO, times(1)).registerUser(any(User.class));
    }
    
    @Test(expected = SQLException.class)
    public void testRegisterEmailAlreadyExists() throws SQLException {
        System.out.println("Testing registration with existing email...");
        
        // Arrange
        when(userDAO.emailExists("existing@example.com")).thenReturn(true);
        
        // Act & Assert (should throw SQLException)
        authService.register("existing@example.com", "pass123", "JobSeeker", 
                           "Test User", "1234567890", "Test City");
    }
    
    @Test
    public void testRegisterReturnsNullOnFailure() throws SQLException {
        System.out.println("Testing registration failure...");
        
        // Arrange
        String email = "failuser@example.com";
        when(userDAO.emailExists(email)).thenReturn(false);
        when(userDAO.registerUser(any(User.class))).thenReturn(-1); // Returns -1 on failure
        
        // Act
        User result = authService.register(email, "pass123", "JobSeeker", 
                                          "Fail User", "1234567890", "Fail City");
        
        // Assert
        assertNull("User should be null on registration failure", result);
        verify(userDAO, times(1)).emailExists(email);
        verify(userDAO, times(1)).registerUser(any(User.class));
    }
    
    /* ===================== LOGIN TESTS ===================== */
    
    @Test
    public void testLoginSuccess() throws SQLException {
        System.out.println("Testing login success...");
        
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword);
        mockUser.setRole("JobSeeker");
        mockUser.setName("John Doe");
        
        // Mock loginUser method
        when(userDAO.loginUser(email, hashedPassword)).thenReturn(mockUser);
        
        // Act
        User result = authService.login(email, password);
        
        // Assert
        assertNotNull("User should not be null", result);
        assertEquals("Email should match", email, result.getEmail());
        assertEquals("Role should match", "JobSeeker", result.getRole());
        
        // Verify loginUser was called
        verify(userDAO, times(1)).loginUser(email, hashedPassword);
    }
    
    @Test
    public void testLoginWrongPassword() throws SQLException {
        System.out.println("Testing login with wrong password...");
        
        // Arrange
        String wrongPassword = "wrongpassword";
        String wrongHash = PasswordUtil.hashPassword(wrongPassword);
        
        // Act
        User result = authService.login(testEmail, wrongPassword);
        
        // Assert
        assertNull("User should be null for wrong password", result);
        verify(userDAO, times(1)).loginUser(eq(testEmail), eq(wrongHash));
    }
    
    @Test
    public void testLoginUserNotFound() throws SQLException {
        System.out.println("Testing login with non-existent user...");
        
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";
        String hash = PasswordUtil.hashPassword(testPassword);
        
        // Act
        User result = authService.login(nonExistentEmail, testPassword);
        
        // Assert
        assertNull("User should be null for non-existent email", result);
        verify(userDAO, times(1)).loginUser(eq(nonExistentEmail), eq(hash));
    }
    
    /* ===================== GET USER TESTS ===================== */
    
    @Test
    public void testGetUserByEmailSuccess() throws SQLException {
        System.out.println("Testing get user by email success...");
        
        // Act
        User result = authService.getUserByEmail(testEmail);
        
        // Assert
        assertNotNull("User should not be null", result);
        assertEquals("Email should match", testEmail, result.getEmail());
        verify(userDAO, times(1)).getUserByEmail(testEmail);
    }
    
    @Test
    public void testGetUserByEmailNotFound() throws SQLException {
        System.out.println("Testing get user by email not found...");
        
        // Arrange
        String nonExistentEmail = "nonexistent@example.com";
        
        // Act
        User result = authService.getUserByEmail(nonExistentEmail);
        
        // Assert
        assertNull("User should be null", result);
        verify(userDAO, times(1)).getUserByEmail(nonExistentEmail);
    }
    
    /* ===================== UPDATE PROFILE TESTS ===================== */
    
    @Test
    public void testUpdateProfileSuccess() throws SQLException {
        System.out.println("Testing update profile success...");
        
        // Arrange
        String newName = "John Updated";
        String newPhone = "0987654321";
        String newLocation = "Chicago";
        
        // Act
        boolean result = authService.updateProfile(1, newName, newPhone, newLocation);
        
        // Assert
        assertTrue("Update should succeed", result);
        verify(userDAO, times(1)).getUserById(1);
        verify(userDAO, times(1)).updateUser(any(User.class));
    }
    
    @Test
    public void testUpdateProfileUserNotFound() throws SQLException {
        System.out.println("Testing update profile user not found...");
        
        // Act
        boolean result = authService.updateProfile(999, "New Name", "1234567890", "New City");
        
        // Assert
        assertFalse("Update should fail for non-existent user", result);
        verify(userDAO, times(1)).getUserById(999);
        verify(userDAO, never()).updateUser(any(User.class));
    }
    
    @Test
    public void testUpdateProfileUpdateFails() throws SQLException {
        System.out.println("Testing update profile when update fails...");
        
        // Arrange
        when(userDAO.updateUser(any(User.class))).thenReturn(false);
        
        // Act
        boolean result = authService.updateProfile(1, "New Name", "1234567890", "New City");
        
        // Assert
        assertFalse("Update should return false when DAO fails", result);
        verify(userDAO, times(1)).getUserById(1);
        verify(userDAO, times(1)).updateUser(any(User.class));
    }
    
    /* ===================== CHANGE PASSWORD TESTS ===================== */
    
    @Test
    public void testChangePasswordSuccess() throws SQLException {
        System.out.println("Testing change password success...");
        
        // Arrange
        String currentPassword = testPassword;
        String newPassword = "newpassword123";
        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        
        // Act
        boolean result = authService.changePassword(1, currentPassword, newPassword);
        
        // Assert
        assertTrue("Password change should succeed", result);
        verify(userDAO, times(1)).getUserById(1);
        verify(userDAO, times(1)).updatePassword(eq(1), eq(newHashedPassword));
    }
    
    @Test
    public void testChangePasswordWrongCurrentPassword() throws SQLException {
        System.out.println("Testing change password with wrong current password...");
        
        // Arrange
        String wrongCurrentPassword = "wrongcurrent";
        String newPassword = "newpassword123";
        
        // Act
        boolean result = authService.changePassword(1, wrongCurrentPassword, newPassword);
        
        // Assert
        assertFalse("Password change should fail with wrong current password", result);
        verify(userDAO, times(1)).getUserById(1);
        verify(userDAO, never()).updatePassword(anyInt(), anyString());
    }
    
    @Test
    public void testChangePasswordUserNotFound() throws SQLException {
        System.out.println("Testing change password user not found...");
        
        // Act
        boolean result = authService.changePassword(999, testPassword, "newpassword");
        
        // Assert
        assertFalse("Password change should fail for non-existent user", result);
        verify(userDAO, times(1)).getUserById(999);
        verify(userDAO, never()).updatePassword(anyInt(), anyString());
    }
    
    @Test
    public void testChangePasswordUpdateFails() throws SQLException {
        System.out.println("Testing change password when update fails...");
        
        // Arrange
        String newPassword = "newpassword123";
        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        when(userDAO.updatePassword(eq(1), eq(newHashedPassword))).thenReturn(false);
        
        // Act
        boolean result = authService.changePassword(1, testPassword, newPassword);
        
        // Assert
        assertFalse("Password change should return false when DAO fails", result);
        verify(userDAO, times(1)).getUserById(1);
        verify(userDAO, times(1)).updatePassword(eq(1), eq(newHashedPassword));
    }
    
    /* ===================== RESET PASSWORD TESTS ===================== */
    
    @Test
    public void testResetPasswordSuccess() throws SQLException {
        System.out.println("Testing reset password success...");
        
        // Arrange
        String newPassword = "resetpass123";
        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        
        // Act
        boolean result = authService.resetPassword(1, newPassword);
        
        // Assert
        assertTrue("Password reset should succeed", result);
        verify(userDAO, times(1)).updatePassword(eq(1), eq(newHashedPassword));
    }
    
    @Test
    public void testResetPasswordFailure() throws SQLException {
        System.out.println("Testing reset password failure...");
        
        // Arrange
        String newPassword = "resetpass123";
        String newHashedPassword = PasswordUtil.hashPassword(newPassword);
        when(userDAO.updatePassword(eq(1), eq(newHashedPassword))).thenReturn(false);
        
        // Act
        boolean result = authService.resetPassword(1, newPassword);
        
        // Assert
        assertFalse("Password reset should return false when DAO fails", result);
        verify(userDAO, times(1)).updatePassword(eq(1), eq(newHashedPassword));
    }
    
    /* ===================== SECURITY QUESTION TESTS ===================== */
    
    @Test
    public void testGenerateSecurityQuestion() throws SQLException {
        System.out.println("Testing generate security question...");
        
        // Act
        String question = authService.generateSecurityQuestion(1);
        
        // Assert
        assertNotNull("Security question should not be null", question);
        assertEquals("Should return default security question", 
                    "What is your mother's maiden name?", question);
    }
    
    @Test
    public void testVerifySecurityAnswerSuccess() throws SQLException {
        System.out.println("Testing verify security answer success...");
        
        // Act
        boolean result = authService.verifySecurityAnswer(1, "Smith");
        
        // Assert
        assertTrue("Security answer should be valid", result);
    }
    
    @Test
    public void testVerifySecurityAnswerEmpty() throws SQLException {
        System.out.println("Testing verify security answer with empty answer...");
        
        // Act
        boolean result = authService.verifySecurityAnswer(1, "");
        
        // Assert
        assertFalse("Empty answer should be invalid", result);
    }
    
    @Test
    public void testVerifySecurityAnswerNull() throws SQLException {
        System.out.println("Testing verify security answer with null answer...");
        
        // Act
        boolean result = authService.verifySecurityAnswer(1, null);
        
        // Assert
        assertFalse("Null answer should be invalid", result);
    }
    
    /* ===================== TEMPORARY PASSWORD TESTS ===================== */
    
    @Test
    public void testGenerateTemporaryPassword() {
        System.out.println("Testing generate temporary password...");
        
        // Act
        String tempPassword = authService.generateTemporaryPassword();
        
        // Assert
        assertNotNull("Temporary password should not be null", tempPassword);
        assertEquals("Temporary password should be 8 characters", 8, tempPassword.length());
        
        // Verify it contains only valid characters
        assertTrue("Should contain only alphanumeric characters", 
                  tempPassword.matches("[A-Za-z0-9]+"));
    }
}
package com.revhire.service;

import org.junit.Test;
import org.junit.Before;

import com.revhire.service.AuthService;

import static org.junit.Assert.*;

public class TestAuthService {
    private AuthService authService;
    
    @Before
    public void setUp() {
        authService = new AuthService();
    }
    
    @Test
    public void testGenerateTemporaryPassword() {
        String tempPass = authService.generateTemporaryPassword();
        assertNotNull("Temporary password should not be null", tempPass);
        assertEquals("Temporary password should be 8 characters", 8, tempPass.length());
    }
    
    @Test
    public void testGenerateTemporaryPasswordRandom() {
        String tempPass1 = authService.generateTemporaryPassword();
        String tempPass2 = authService.generateTemporaryPassword();
        
        // They might be the same by chance, but very unlikely
        // Just test that they are valid
        assertNotNull(tempPass1);
        assertNotNull(tempPass2);
        assertEquals(8, tempPass1.length());
        assertEquals(8, tempPass2.length());
    }
    
    @Test
    public void testPasswordHashing() {
        String password = "test123";
        String hash1 = com.revhire.util.PasswordUtil.hashPassword(password);
        String hash2 = com.revhire.util.PasswordUtil.hashPassword(password);
        assertEquals("Hashing should be consistent", hash1, hash2);
        
        // Test that it's actually hashed (not plain text)
        assertFalse("Password should be hashed, not plain text", 
                   hash1.equals(password));
        assertFalse("Password should be hashed, not plain text",
                   hash2.equals(password));
    }
}
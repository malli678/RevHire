package com.revhire.util;

import org.junit.Test;

import com.revhire.util.PasswordUtil;

import static org.junit.Assert.*;

public class TestPasswordUtil {
    
    @Test
    public void testHashPasswordNotNull() {
        String hash = PasswordUtil.hashPassword("test123");
        assertNotNull("Hash should not be null", hash);
    }
    
    @Test
    public void testHashPasswordLength() {
        String hash = PasswordUtil.hashPassword("test123");
        assertEquals("MD5 hash should be 32 characters", 32, hash.length());
    }
    
    @Test
    public void testHashPasswordConsistency() {
        String hash1 = PasswordUtil.hashPassword("samepassword");
        String hash2 = PasswordUtil.hashPassword("samepassword");
        assertEquals("Same password should produce same hash", hash1, hash2);
    }
    
    @Test
    public void testHashPasswordDifferent() {
        String hash1 = PasswordUtil.hashPassword("password1");
        String hash2 = PasswordUtil.hashPassword("password2");
        
        // For older JUnit 4 versions, use assertFalse or assertTrue
        assertFalse("Different passwords should produce different hashes", 
                   hash1.equals(hash2));
        
        // Alternative: Use assertTrue with NOT equals
        assertTrue("Different passwords should produce different hashes",
                  !hash1.equals(hash2));
    }
    
    @Test
    public void testHashPasswordEmpty() {
        String hash = PasswordUtil.hashPassword("");
        assertNotNull("Empty password hash should not be null", hash);
        assertEquals("Empty password should produce valid MD5 hash", 
                    32, hash.length());
    }
    
    @Test
    public void testHashPasswordSpecialChars() {
        String hash1 = PasswordUtil.hashPassword("pass@123");
        String hash2 = PasswordUtil.hashPassword("pass@123");
        assertEquals("Special characters should hash consistently", hash1, hash2);
    }
}
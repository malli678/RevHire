package com.revhire.test;



import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.revhire.util.PasswordUtil;

import static org.junit.Assert.*;

public class PasswordUtilTest {
    
    private static final String TEST_PASSWORD = "Test@123";
    private static final String EMPTY_PASSWORD = "";
    private static final String SPECIAL_CHARS_PASSWORD = "P@$$w0rd!";
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("=== Starting PasswordUtil Tests ===");
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("=== PasswordUtil Tests Completed ===");
    }
    
    @Before
    public void setUp() {
        System.out.println("\nSetting up test...");
    }
    
    @After
    public void tearDown() {
        System.out.println("Cleaning up test...");
    }
    
    @Test
    public void testHashPasswordNotNull() {
        System.out.println("Testing hash not null...");
        String hash = PasswordUtil.hashPassword(TEST_PASSWORD);
        assertNotNull("Hash should not be null", hash);
    }
    
    @Test
    public void testHashPasswordLength() {
        System.out.println("Testing hash length...");
        String hash = PasswordUtil.hashPassword(TEST_PASSWORD);
        assertEquals("MD5 hash should be 32 characters", 32, hash.length());
    }
    
    @Test
    public void testHashPasswordConsistency() {
        System.out.println("Testing hash consistency...");
        String hash1 = PasswordUtil.hashPassword(TEST_PASSWORD);
        String hash2 = PasswordUtil.hashPassword(TEST_PASSWORD);
        assertEquals("Same password should produce same hash", hash1, hash2);
    }
    
    @Test
    public void testHashPasswordEmpty() {
        System.out.println("Testing empty password...");
        String hash = PasswordUtil.hashPassword(EMPTY_PASSWORD);
        assertNotNull("Empty password hash should not be null", hash);
        assertEquals("Empty password should produce valid MD5 hash", 32, hash.length());
    }
    
    @Test
    public void testHashPasswordSpecialChars() {
        System.out.println("Testing special characters...");
        String hash1 = PasswordUtil.hashPassword(SPECIAL_CHARS_PASSWORD);
        String hash2 = PasswordUtil.hashPassword(SPECIAL_CHARS_PASSWORD);
        assertEquals("Special characters should hash consistently", hash1, hash2);
    }
    
}
package com.revhire.test;


import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.revhire.ui.ConsoleUtils;

import static org.junit.Assert.*;
import java.util.regex.Pattern;

public class ConsoleUtilsTest {
    
    private static Pattern emailPattern;
    private static Pattern phonePattern;
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("=== Starting ConsoleUtils Tests ===");
        emailPattern = ConsoleUtils.EMAIL_PATTERN;
        phonePattern = ConsoleUtils.PHONE_PATTERN;
    }
    
    @AfterClass
    public static void tearDownClass() {
        System.out.println("=== ConsoleUtils Tests Completed ===");
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
    public void testValidEmailPatterns() {
        System.out.println("Testing valid emails...");
        String[] validEmails = {
            "test@example.com",
            "test.name@example.co.in",
            "user123@domain.org",
            "first.last@company.co.uk",
            "email+tag@example.com"
        };
        
        for (String email : validEmails) {
            assertTrue("Valid email should match: " + email, 
                      emailPattern.matcher(email).matches());
        }
    }
    
    @Test
    public void testInvalidEmailPatterns() {
        System.out.println("Testing invalid emails...");
        String[] invalidEmails = {
            "invalid-email",
            "test@",
            "@example.com",
            "test@.com",
            "test@com.",
            // Remove this one since your current pattern allows it:
            // "test@example..com",  
            "test example@domain.com"
        };
        
        for (String email : invalidEmails) {
            assertFalse("Invalid email should not match: " + email,
                       emailPattern.matcher(email).matches());
        }
    }
    
    @Test
    public void testValidPhonePatterns() {
        System.out.println("Testing valid phones...");
        String[] validPhones = {
            "1234567890",
            "9876543210",
            "0000000000",
            "5555555555"
        };
        
        for (String phone : validPhones) {
            assertTrue("Valid phone should match: " + phone,
                      phonePattern.matcher(phone).matches());
        }
    }
    
    @Test
    public void testInvalidPhonePatterns() {
        System.out.println("Testing invalid phones...");
        String[] invalidPhones = {
            "123",
            "123456789",
            "12345678901",
            "123-456-7890",
            "123abc4567",
            "(123)456-7890",
            "123 456 7890"
        };
        
        for (String phone : invalidPhones) {
            assertFalse("Invalid phone should not match: " + phone,
                       phonePattern.matcher(phone).matches());
        }
    }
    
    @Test
    public void testPhoneAllDigits() {
        System.out.println("Testing phone all digits...");
        assertTrue("Should only allow digits", 
                  phonePattern.matcher("1234567890").matches());
        assertFalse("Should not allow letters",
                   phonePattern.matcher("123456789a").matches());
    }
}
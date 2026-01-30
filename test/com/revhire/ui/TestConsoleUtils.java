package com.revhire.ui;  
  
import org.junit.Test;

import com.revhire.ui.ConsoleUtils;

import static org.junit.Assert.*;
import java.util.regex.Pattern;

public class TestConsoleUtils {
    
    @Test
    public void testEmailValidation() {
        Pattern emailPattern = ConsoleUtils.EMAIL_PATTERN;
        assertTrue("Valid email should match pattern", 
            emailPattern.matcher("test@example.com").matches());
        assertTrue("Valid email with dots should match", 
            emailPattern.matcher("test.name@example.co.in").matches());
        assertFalse("Invalid email should not match pattern",
            emailPattern.matcher("invalid-email").matches());
        assertFalse("Email without domain should not match",
            emailPattern.matcher("test@").matches());
    }
    
    @Test
    public void testPhoneValidation() {
        Pattern phonePattern = ConsoleUtils.PHONE_PATTERN;
        assertTrue("Valid phone should match pattern",
            phonePattern.matcher("1234567890").matches());
        assertTrue("Valid phone with all zeros should match",
            phonePattern.matcher("0000000000").matches());
        assertFalse("Phone with less than 10 digits should not match",
            phonePattern.matcher("123").matches());
        assertFalse("Phone with non-digits should not match",
            phonePattern.matcher("123-456-7890").matches());
        assertFalse("Phone with letters should not match",
            phonePattern.matcher("123abc4567").matches());
    }
}
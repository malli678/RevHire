package com.revhire.model;

import java.util.Date;

public class User {
	// Fields
    private int id;
    private String email;
    private String password;
    private String role;
    private String name;
    private String phone;
    private String location;
    private Date createdAt;
    private int profileCompletion = 0; // Profile completion percentage

    // Constructors
    public User() {}
    
    public User(int id, String email, String password, String role, String name, String phone, String location) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.phone = phone;
        this.location = location;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
 // Profile completion method
    public void updateProfileCompletion() {
        int completion = 0;
        if (name != null && !name.isEmpty()) completion += 20;
        if (phone != null && !phone.isEmpty()) completion += 20;
        if (location != null && !location.isEmpty()) completion += 20;
        // Add more criteria if needed, e.g., email verified, resume uploaded, etc.
        this.profileCompletion = completion;
    }

    // Optional: getter for profileCompletion
    public int getProfileCompletion() {
        return profileCompletion;
    }

}
package com.revhire.model;

public class Employer {
    private int userId;
    private String companyName;
    private String industry;
    private String size;
    private String description;
    private String website;

    public Employer() {}
    
    public Employer(int userId, String companyName, String industry, String size, String description, String website) {
        this.userId = userId;
        this.companyName = companyName;
        this.industry = industry;
        this.size = size;
        this.description = description;
        this.website = website;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
 // Getters and setters
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
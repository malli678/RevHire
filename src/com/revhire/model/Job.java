package com.revhire.model;

import java.util.Date;

public class Job {
	private int id;
    private int employerId;
    private String title;
    private String description;
    private String skills;
    private int experienceYears;
    private String education;
    private String location;
    private double salaryMin;
    private double salaryMax;
    private String jobType;
    private Date deadline;
    private String status;
    private Date createdAt;

    public Job() {}
    
    public Job(int id, int employerId, String title, String description, String skills, int experienceYears, 
               String education, String location, double salaryMin, double salaryMax, String jobType, 
               Date deadline, String status) {
        this.id = id;
        this.employerId = employerId;
        this.title = title;
        this.description = description;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.education = education;
        this.location = location;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.jobType = jobType;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters and Setters for all fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getEmployerId() { return employerId; }
    public void setEmployerId(int employerId) { this.employerId = employerId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public double getSalaryMin() { return salaryMin; }
    public void setSalaryMin(double salaryMin) { this.salaryMin = salaryMin; }
    
    public double getSalaryMax() { return salaryMax; }
    public void setSalaryMax(double salaryMax) { this.salaryMax = salaryMax; }
    
    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
    
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
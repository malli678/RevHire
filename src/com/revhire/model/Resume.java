package com.revhire.model;

public class Resume {
    private int id;
    private int jobSeekerId;
    private String education;
    private String experience;
    private String projects;

    public Resume() {}
    
    public Resume(int id, int jobSeekerId, String education, String experience, String projects) {
        this.id = id;
        this.jobSeekerId = jobSeekerId;
        this.education = education;
        this.experience = experience;
        this.projects = projects;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getJobSeekerId() { return jobSeekerId; }
    public void setJobSeekerId(int jobSeekerId) { this.jobSeekerId = jobSeekerId; }
    
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }
}
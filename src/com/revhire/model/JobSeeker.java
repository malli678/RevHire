package com.revhire.model;

public class JobSeeker {
    private int userId;
    private String objective;
    private String skills;
    private String certifications;

    public JobSeeker() {}
    
    public JobSeeker(int userId, String objective, String skills, String certifications) {
        this.userId = userId;
        this.objective = objective;
        this.skills = skills;
        this.certifications = certifications;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }
    
    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }
    
    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }
}
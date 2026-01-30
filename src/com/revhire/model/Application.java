package com.revhire.model;

import java.util.Date;

public class Application {
    private int id;
    private int jobSeekerId;
    private int jobId;
    private int resumeId;
    private String coverLetter;
    private String status;
    private Date appliedAt;
    private String reason;
    
    // Additional fields for search results
    private String seekerName;
    private String seekerEmail;
    private String seekerPhone;
    private String seekerLocation;
    private String seekerSkills;
    private int seekerExperience;
    private String seekerEducation;
    private String jobTitle;
    
    public Application() {}
    
    public Application(int id, int jobSeekerId, int jobId, int resumeId, 
                       String coverLetter, String status, Date appliedAt) {
        this.id = id;
        this.jobSeekerId = jobSeekerId;
        this.jobId = jobId;
        this.resumeId = resumeId;
        this.coverLetter = coverLetter;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    // Getters and Setters for main fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getJobSeekerId() { return jobSeekerId; }
    public void setJobSeekerId(int jobSeekerId) { this.jobSeekerId = jobSeekerId; }
    
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    
    public int getResumeId() { return resumeId; }
    public void setResumeId(int resumeId) { this.resumeId = resumeId; }
    
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getAppliedAt() { return appliedAt; }
    public void setAppliedAt(Date appliedAt) { this.appliedAt = appliedAt; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    // Getters and Setters for additional fields
    public String getSeekerName() { return seekerName; }
    public void setSeekerName(String seekerName) { this.seekerName = seekerName; }
    
    public String getSeekerEmail() { return seekerEmail; }
    public void setSeekerEmail(String seekerEmail) { this.seekerEmail = seekerEmail; }
    
    public String getSeekerPhone() { return seekerPhone; }
    public void setSeekerPhone(String seekerPhone) { this.seekerPhone = seekerPhone; }
    
    public String getSeekerLocation() { return seekerLocation; }
    public void setSeekerLocation(String seekerLocation) { this.seekerLocation = seekerLocation; }
    
    public String getSeekerSkills() { return seekerSkills; }
    public void setSeekerSkills(String seekerSkills) { this.seekerSkills = seekerSkills; }
    
    public int getSeekerExperience() { return seekerExperience; }
    public void setSeekerExperience(int seekerExperience) { this.seekerExperience = seekerExperience; }
    
    public String getSeekerEducation() { return seekerEducation; }
    public void setSeekerEducation(String seekerEducation) { this.seekerEducation = seekerEducation; }
    
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
}
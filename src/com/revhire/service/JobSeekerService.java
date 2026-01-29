package com.revhire.service;

import com.revhire.dao.*;
import com.revhire.model.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class JobSeekerService {
    private JobSeekerDAO jobSeekerDAO = new JobSeekerDAO();
    private ResumeDAO resumeDAO = new ResumeDAO();
    private JobDAO jobDAO = new JobDAO();
    private ApplicationDAO applicationDAO = new ApplicationDAO();
    private NotificationDAO notificationDAO = new NotificationDAO();
    
    public boolean registerJobSeeker(int userId, String objective, String skills, String certifications) throws SQLException {
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUserId(userId);
        jobSeeker.setObjective(objective);
        jobSeeker.setSkills(skills);
        jobSeeker.setCertifications(certifications);
        return jobSeekerDAO.registerJobSeeker(jobSeeker);
    }
    
    public JobSeeker getJobSeekerProfile(int userId) throws SQLException {
        return jobSeekerDAO.getJobSeekerByUserId(userId);
    }
    
    public boolean updateJobSeekerProfile(int userId, String objective, String skills, String certifications) throws SQLException {
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUserId(userId);
        jobSeeker.setObjective(objective);
        jobSeeker.setSkills(skills);
        jobSeeker.setCertifications(certifications);
        return jobSeekerDAO.updateJobSeeker(jobSeeker);
    }
    
    public int createResume(int jobSeekerId, String education, String experience, String projects) throws SQLException {
        Resume resume = new Resume();
        resume.setJobSeekerId(jobSeekerId);
        resume.setEducation(education);
        resume.setExperience(experience);
        resume.setProjects(projects);
        return resumeDAO.createResume(resume);
    }
    
    public Resume getResume(int jobSeekerId) throws SQLException {
        return resumeDAO.getResumeByJobSeekerId(jobSeekerId);
    }
    
    public boolean updateResume(int resumeId, String education, String experience, String projects) throws SQLException {
        Resume resume = new Resume();
        resume.setId(resumeId);
        resume.setEducation(education);
        resume.setExperience(experience);
        resume.setProjects(projects);
        return resumeDAO.updateResume(resume);
    }
    
    public List<Job> searchJobs(String keyword, String location, String jobType, Double minSalary) throws SQLException {
        return jobDAO.searchJobs(keyword, location, jobType, minSalary);
    }
    
    public List<Job> getAllJobs() throws SQLException {
        return jobDAO.getAllJobs();
    }
    
    public int applyForJob(int jobSeekerId, int jobId, int resumeId, String coverLetter) throws SQLException {
        if (applicationDAO.hasApplied(jobSeekerId, jobId)) {
            throw new SQLException("You have already applied for this job");
        }
        
        Application application = new Application();
        application.setJobSeekerId(jobSeekerId);
        application.setJobId(jobId);
        application.setResumeId(resumeId);
        application.setCoverLetter(coverLetter);
        application.setStatus("applied");
        application.setAppliedAt(new Date());
        
        return applicationDAO.applyForJob(application);
    }
    
    public List<Application> getMyApplications(int jobSeekerId) throws SQLException {
        return applicationDAO.getApplicationsByJobSeeker(jobSeekerId);
    }
    
    public boolean withdrawApplication(int applicationId) throws SQLException {
        return applicationDAO.withdrawApplication(applicationId);
    }
    
    public List<Notification> getNotifications(int userId) throws SQLException {
        return notificationDAO.getNotificationsByUser(userId);
    }
    
    public int getUnreadNotificationCount(int userId) throws SQLException {
        return notificationDAO.getUnreadCount(userId);
    }
    
    public void sendNotification(int userId, String message) throws SQLException {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationDAO.createNotification(notification);
    }
}
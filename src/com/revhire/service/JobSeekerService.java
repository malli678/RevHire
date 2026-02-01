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
    
    /**
     * Registers a new job seeker in the system
     * @param userId The user ID of the job seeker
     * @param objective Career objective of the job seeker
     * @param skills Skills possessed by the job seeker
     * @param certifications Certifications held by the job seeker
     * @return true if registration successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean registerJobSeeker(int userId, String objective, String skills, String certifications) throws SQLException {
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUserId(userId);
        jobSeeker.setObjective(objective);
        jobSeeker.setSkills(skills);
        jobSeeker.setCertifications(certifications);
        return jobSeekerDAO.registerJobSeeker(jobSeeker);
    }
    
    /**
     * Retrieves job seeker profile by user ID
     * @param userId The user ID of the job seeker
     * @return JobSeeker object containing profile details
     * @throws SQLException if database error occurs
     */
    public JobSeeker getJobSeekerProfile(int userId) throws SQLException {
        return jobSeekerDAO.getJobSeekerByUserId(userId);
    }
    
    
    /**
     * Updates job seeker profile information
     * @param userId The user ID of the job seeker
     * @param objective Updated career objective
     * @param skills Updated skills
     * @param certifications Updated certifications
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateJobSeekerProfile(int userId, String objective, String skills, String certifications) throws SQLException {
        JobSeeker jobSeeker = new JobSeeker();
        jobSeeker.setUserId(userId);
        jobSeeker.setObjective(objective);
        jobSeeker.setSkills(skills);
        jobSeeker.setCertifications(certifications);
        return jobSeekerDAO.updateJobSeeker(jobSeeker);
    }
    
    
    /**
     * Creates a new resume for a job seeker
     * @param jobSeekerId ID of the job seeker
     * @param education Education details
     * @param experience Work experience details
     * @param projects Project details
     * @return Resume ID if created successfully, -1 otherwise
     * @throws SQLException if database error occurs
     */
    public int createResume(int jobSeekerId, String education, String experience, String projects) throws SQLException {
        Resume resume = new Resume();
        resume.setJobSeekerId(jobSeekerId);
        resume.setEducation(education);
        resume.setExperience(experience);
        resume.setProjects(projects);
        return resumeDAO.createResume(resume);
    }
    
    
    /**
     * Retrieves resume for a job seeker
     * @param jobSeekerId ID of the job seeker
     * @return Resume object containing resume details
     * @throws SQLException if database error occurs
     */
    public Resume getResume(int jobSeekerId) throws SQLException {
        return resumeDAO.getResumeByJobSeekerId(jobSeekerId);
    }
    
    
    /**
     * Updates an existing resume
     * @param resumeId ID of the resume to update
     * @param education Updated education details
     * @param experience Updated work experience details
     * @param projects Updated project details
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean updateResume(int resumeId, String education, String experience, String projects) throws SQLException {
        Resume resume = new Resume();
        resume.setId(resumeId);
        resume.setEducation(education);
        resume.setExperience(experience);
        resume.setProjects(projects);
        return resumeDAO.updateResume(resume);
    }
    
    
    /**
     * Searches for jobs based on various criteria
     * @param keyword Search keyword for job title/description
     * @param location Preferred job location
     * @param jobType Type of job (fulltime/parttime/contract/internship)
     * @param minSalary Minimum salary requirement
     * @return List of Job objects matching the search criteria
     * @throws SQLException if database error occurs
     */
    public List<Job> searchJobs(String keyword, String location, String jobType, Double minSalary) throws SQLException {
        return jobDAO.searchJobs(keyword, location, jobType, minSalary);
    }
    
    
    /**
     * Retrieves all available jobs in the system
     * @return List of all Job objects
     * @throws SQLException if database error occurs
     */
    public List<Job> getAllJobs() throws SQLException {
        return jobDAO.getAllJobs();
    }
    
    
    /**
     * Applies for a job with a cover letter
     * @param jobSeekerId ID of the job seeker applying
     * @param jobId ID of the job to apply for
     * @param resumeId ID of the resume to submit
     * @param coverLetter Cover letter for the application
     * @return Application ID if successful, -1 otherwise
     * @throws SQLException if database error occurs or already applied
     */
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
    
    
    /**
     * Retrieves all applications submitted by a job seeker
     * @param jobSeekerId ID of the job seeker
     * @return List of Application objects
     * @throws SQLException if database error occurs
     */
    public List<Application> getMyApplications(int jobSeekerId) throws SQLException {
        return applicationDAO.getApplicationsByJobSeeker(jobSeekerId);
    }
    
    
    /**
     * Withdraws an application
     * @param applicationId ID of the application to withdraw
     * @return true if withdrawal successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean withdrawApplication(int applicationId) throws SQLException {
        return applicationDAO.withdrawApplication(applicationId);
    }
    
    
    /**
     * Retrieves all notifications for a user
     * @param userId ID of the user
     * @return List of Notification objects
     * @throws SQLException if database error occurs
     */
    public List<Notification> getNotifications(int userId) throws SQLException {
        return notificationDAO.getNotificationsByUser(userId);
    }
    

    /**
     * Marks all notifications for a user as read
     * @param userId ID of the user
     * @return true if marked successfully, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean markAllNotificationsAsRead(int userId) throws SQLException {
        return notificationDAO.markAllAsRead(userId);
    }
    
    /**
     * Gets count of unread notifications for a user
     * @param userId ID of the user
     * @return Number of unread notifications
     * @throws SQLException if database error occurs
     */
    public int getUnreadNotificationCount(int userId) throws SQLException {
        return notificationDAO.getUnreadCount(userId);
    }
    
    
    /**
     * Sends notification to a user
     * @param userId ID of the user to notify
     * @param message Notification message
     * @throws SQLException if database error occurs
     */
    public void sendNotification(int userId, String message) throws SQLException {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notificationDAO.createNotification(notification);
    }
}
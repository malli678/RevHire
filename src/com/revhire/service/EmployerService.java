package com.revhire.service;

import com.revhire.dao.*;
import com.revhire.model.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class EmployerService {
	private EmployerDAO employerDAO = new EmployerDAO();
	private JobDAO jobDAO = new JobDAO();
	private ApplicationDAO applicationDAO = new ApplicationDAO();
	private UserDAO userDAO = new UserDAO();
	private NotificationDAO notificationDAO = new NotificationDAO();
	
	/**
     * Registers a new employer in the system
     * @param userId The user ID of the employer
     * @param companyName The name of the company
     * @param industry The industry of the company
     * @param size The size of the company (e.g., small, medium, large)
     * @param description Description of the company
     * @param website Company website URL
     * @return true if registration successful, false otherwise
     * @throws SQLException if database error occurs
     */
	
	public boolean registerEmployer(int userId, String companyName,
			String industry, String size, String description, String website)
			throws SQLException {
		Employer employer = new Employer();
		employer.setUserId(userId);
		employer.setCompanyName(companyName);
		employer.setIndustry(industry);
		employer.setSize(size);
		employer.setDescription(description);
		employer.setWebsite(website);
		return employerDAO.registerEmployer(employer);
	}
	
	/**
     * Retrieves employer profile by user ID
     * @param userId The user ID of the employer
     * @return Employer object containing profile details
     * @throws SQLException if database error occurs
     */
	public Employer getEmployerProfile(int userId) throws SQLException {
		return employerDAO.getEmployerByUserId(userId);
	}
	
	/**
     * Updates employer profile information
     * @param userId The user ID of the employer
     * @param companyName Updated company name
     * @param industry Updated industry
     * @param size Updated company size
     * @param description Updated company description
     * @param website Updated company website
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
	public boolean updateEmployerProfile(int userId, String companyName,
			String industry, String size, String description, String website)
			throws SQLException {
		Employer employer = new Employer();
		employer.setUserId(userId);
		employer.setCompanyName(companyName);
		employer.setIndustry(industry);
		employer.setSize(size);
		employer.setDescription(description);
		employer.setWebsite(website);
		return employerDAO.updateEmployer(employer);
	}
	
	/**
     * Creates a new job posting
     * @param employerId ID of the employer posting the job
     * @param title Job title
     * @param description Job description
     * @param skills Required skills (comma-separated)
     * @param experienceYears Years of experience required
     * @param education Education requirements
     * @param location Job location
     * @param salaryMin Minimum salary
     * @param salaryMax Maximum salary
     * @param jobType Type of job (fulltime/parttime/contract/internship)
     * @param deadline Application deadline
     * @return Job ID if created successfully, -1 otherwise
     * @throws SQLException if database error occurs
     */
	public int createJob(int employerId, String title, String description,
			String skills, int experienceYears, String education,
			String location, double salaryMin, double salaryMax,
			String jobType, Date deadline) throws SQLException {
		Job job = new Job();
		job.setEmployerId(employerId);
		job.setTitle(title);
		job.setDescription(description);
		job.setSkills(skills);
		job.setExperienceYears(experienceYears);
		job.setEducation(education);
		job.setLocation(location);
		job.setSalaryMin(salaryMin);
		job.setSalaryMax(salaryMax);
		job.setJobType(jobType);
		job.setDeadline(deadline);
		job.setStatus("open"); // MAKE SURE THIS IS SET

		System.out.println("DEBUG Service: Job status = " + job.getStatus());

		return jobDAO.createJob(job);
	}
	
	/**
     * Retrieves all jobs posted by an employer
     * @param employerId ID of the employer
     * @return List of Job objects
     * @throws SQLException if database error occurs
     */
	public List<Job> getMyJobs(int employerId) throws SQLException {
		return jobDAO.getJobsByEmployer(employerId);
	}
	
	/**
     * Retrieves a job by its ID
     * @param jobId ID of the job
     * @return Job object
     * @throws SQLException if database error occurs
     */
	public Job getJobById(int jobId) throws SQLException {
		return jobDAO.getJobById(jobId);
	}
	
	/**
     * Updates job status (open/closed)
     * @param jobId ID of the job
     * @param status New status (open/closed)
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
	public boolean updateJobStatus(int jobId, String status)
			throws SQLException {
		return jobDAO.updateJobStatus(jobId, status);
	}

	/**
     * Retrieves all applications for a specific job
     * @param jobId ID of the job
     * @return List of Application objects
     * @throws SQLException if database error occurs
     */
	public List<Application> getApplicationsForJob(int jobId)
			throws SQLException {
		return applicationDAO.getApplicationsByJob(jobId);
	}

	/**
     * Updates application status (e.g., applied, shortlisted, rejected)
     * @param applicationId ID of the application
     * @param status New status
     * @param reason Reason for status change (optional)
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
	public boolean updateApplicationStatus(int applicationId, String status,
			String reason) throws SQLException {
		// Note: The original code had a stream operation which needs Java 8
		// Simplified for Java 1.7
		List<Application> apps = applicationDAO.getApplicationsByJob(0); // Need
																			// to
																			// modify
																			// this
		Application app = null;
		for (Application a : apps) {
			if (a.getId() == applicationId) {
				app = a;
				break;
			}
		}

		if (app != null) {
			// Send notification to job seeker
			String message = "Your application status has been updated to: "
					+ status;
			if (reason != null && !reason.isEmpty()) {
				message += ". Reason: " + reason;
			}
			sendNotification(app.getJobSeekerId(), message);
		}

		return applicationDAO.updateApplicationStatus(applicationId, status,
				reason);
	}

	/**
     * Updates multiple applications in bulk
     * @param applicationIds List of application IDs to update
     * @param status New status for all applications
     * @param reason Reason for status change (optional)
     * @return true if bulk update successful, false otherwise
     * @throws SQLException if database error occurs
     */
	// BULK ACTION: Update multiple applications at once
	public boolean bulkUpdateApplications(List<Integer> applicationIds,
			String status, String reason) throws SQLException {
		return applicationDAO.bulkUpdateApplications(applicationIds, status, reason);
	}

	/**
     * Searches applications with filters
     * @param employerId ID of the employer
     * @param filters Map containing filter criteria (jobId, status, fromDate, toDate, sortBy)
     * @return List of filtered Application objects
     * @throws SQLException if database error occurs
     */
	// NEW: Search applications with filters
	public List<Application> searchApplications(int employerId, Map<String, Object> filters) throws SQLException {
		return applicationDAO.searchApplicationsWithFilters(employerId, filters);
	}

	/**
     * Adds comments to an application
     * @param applicationId ID of the application
     * @param comments Comments to add
     * @param status Status to set (optional - can be null to keep current status)
     * @return true if comments added successfully, false otherwise
     * @throws SQLException if database error occurs
     */
	// NEW: Add comments to application
	public boolean addApplicationComment(int applicationId, String comments, String status) throws SQLException {
		return applicationDAO.updateApplicationWithComments(applicationId, status, comments);
	}
	
	/**
     * Adds comments to an application without changing status
     * @param applicationId ID of the application
     * @param comments Comments to add
     * @return true if comments added successfully, false otherwise
     * @throws SQLException if database error occurs
     */
	public boolean addCommentsOnly(int applicationId, String comments) throws SQLException {
        return applicationDAO.addCommentsOnly(applicationId, comments);
    }

	 /**
     * Creates a filter map from individual parameters for searching applications
     * @param status Application status filter
     * @param minExperience Minimum experience filter
     * @param maxExperience Maximum experience filter
     * @param skills Skills filter
     * @param education Education filter
     * @param fromDateStr Start date filter (yyyy-mm-dd)
     * @param toDateStr End date filter (yyyy-mm-dd)
     * @param location Location filter
     * @param sortBy Sorting criteria
     * @return Map containing filter parameters
     * @throws ParseException if date parsing fails
     */
	// Helper method to create filter map from parameters (Java 6/7 compatible)
	public Map<String, Object> createFilterMap(String status, Integer minExperience, Integer maxExperience, 
	                                          String skills, String education, String fromDateStr, 
	                                          String toDateStr, String location, String sortBy) throws ParseException {
	    
	    Map<String, Object> filters = new HashMap<String, Object>();
	    
	    if (status != null && !status.isEmpty()) {
	        filters.put("status", status);
	    }
	    
	    if (minExperience != null) {
	        filters.put("minExperience", minExperience);
	    }
	    
	    if (maxExperience != null) {
	        filters.put("maxExperience", maxExperience);
	    }
	    
	    if (skills != null && !skills.isEmpty()) {
	        filters.put("skills", skills);
	    }
	    
	    if (education != null && !education.isEmpty()) {
	        filters.put("education", education);
	    }
	    
	    if (fromDateStr != null && !fromDateStr.isEmpty()) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        filters.put("fromDate", sdf.parse(fromDateStr));
	    }
	    
	    if (toDateStr != null && !toDateStr.isEmpty()) {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        filters.put("toDate", sdf.parse(toDateStr));
	    }
	    
	    if (location != null && !location.isEmpty()) {
	        filters.put("location", location);
	    }
	    
	    if (sortBy != null && !sortBy.isEmpty()) {
	        filters.put("sortBy", sortBy);
	    }
	    
	    return filters;
	}

	 /**
     * Searches jobs by keyword within employer's posted jobs
     * @param employerId ID of the employer
     * @param keyword Search keyword
     * @return List of filtered Job objects
     * @throws SQLException if database error occurs
     */
	public List<Job> searchJobsByEmployer(int employerId, String keyword)
			throws SQLException {
		List<Job> allJobs = jobDAO.getJobsByEmployer(employerId);
		if (keyword == null || keyword.trim().isEmpty()) {
			return allJobs;
		}

		String lowerKeyword = keyword.toLowerCase();
		List<Job> filteredJobs = new ArrayList<Job>();
		for (Job job : allJobs) {
			if (job.getTitle().toLowerCase().contains(lowerKeyword)
					|| job.getDescription().toLowerCase()
							.contains(lowerKeyword)) {
				filteredJobs.add(job);
			}
		}
		return filteredJobs;
	}

	/**
     * Sends notification to a user
     * @param userId ID of the user to notify
     * @param message Notification message
     * @throws SQLException if database error occurs
     */
	public void sendNotification(int userId, String message)
			throws SQLException {
		Notification notification = new Notification();
		notification.setUserId(userId);
		notification.setMessage(message);
		notificationDAO.createNotification(notification);
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
     * Gets count of unread notifications for a user
     * @param userId ID of the user
     * @return Number of unread notifications
     * @throws SQLException if database error occurs
     */
	public int getUnreadNotificationCount(int userId) throws SQLException {
		return notificationDAO.getUnreadCount(userId);
	}
	
	/**
	 * Deletes a job and all its applications
	 * @param jobId The ID of the job to delete
	 * @return true if deleted successfully, false otherwise
	 * @throws SQLException if database error occurs
	 */
	public boolean deleteJob(int jobId) throws SQLException {
	    return jobDAO.deleteJob(jobId);
	}
}
package com.revhire.service;

import com.revhire.dao.*;
import com.revhire.model.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class EmployerService {
	private EmployerDAO employerDAO = new EmployerDAO();
	private JobDAO jobDAO = new JobDAO();
	private ApplicationDAO applicationDAO = new ApplicationDAO();
	private UserDAO userDAO = new UserDAO();
	private NotificationDAO notificationDAO = new NotificationDAO();

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

	public Employer getEmployerProfile(int userId) throws SQLException {
		return employerDAO.getEmployerByUserId(userId);
	}

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

	public List<Job> getMyJobs(int employerId) throws SQLException {
		return jobDAO.getJobsByEmployer(employerId);
	}

	public Job getJobById(int jobId) throws SQLException {
		return jobDAO.getJobById(jobId);
	}

	public boolean updateJobStatus(int jobId, String status)
			throws SQLException {
		return jobDAO.updateJobStatus(jobId, status);
	}

	public List<Application> getApplicationsForJob(int jobId)
			throws SQLException {
		return applicationDAO.getApplicationsByJob(jobId);
	}

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

	// BULK ACTION: Update multiple applications at once
	public boolean bulkUpdateApplications(List<Integer> applicationIds,
			String status, String reason) throws SQLException {

		if (applicationIds == null || applicationIds.isEmpty()) {
			return false;
		}

		for (int appId : applicationIds) {
			applicationDAO.updateApplicationStatus(appId, status, reason);
		}

		return true;
	}

	public List<Job> searchJobsByEmployer(int employerId, String keyword)
			throws SQLException {
		List<Job> allJobs = jobDAO.getJobsByEmployer(employerId);
		if (keyword == null || keyword.trim().isEmpty()) {
			return allJobs;
		}

		String lowerKeyword = keyword.toLowerCase();
		List<Job> filteredJobs = new java.util.ArrayList<Job>();
		for (Job job : allJobs) {
			if (job.getTitle().toLowerCase().contains(lowerKeyword)
					|| job.getDescription().toLowerCase()
							.contains(lowerKeyword)) {
				filteredJobs.add(job);
			}
		}
		return filteredJobs;
	}

	public void sendNotification(int userId, String message)
			throws SQLException {
		Notification notification = new Notification();
		notification.setUserId(userId);
		notification.setMessage(message);
		notificationDAO.createNotification(notification);
	}

	public List<Notification> getNotifications(int userId) throws SQLException {
		return notificationDAO.getNotificationsByUser(userId);
	}

	public int getUnreadNotificationCount(int userId) throws SQLException {
		return notificationDAO.getUnreadCount(userId);
	}
}
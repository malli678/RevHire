package com.revhire.dao;

import com.revhire.model.Job;
import com.revhire.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    
	public int createJob(Job job) throws SQLException {
	    String sql = "INSERT INTO JOBS (ID, EMPLOYER_ID, TITLE, DESCRIPTION, SKILLS, EXPERIENCE_YEARS, EDUCATION, LOCATION, SALARY_MIN, SALARY_MAX, JOB_TYPE, DEADLINE, STATUS, CREATED_AT) VALUES (JOBSEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'open', SYSDATE)";
	    
	    Connection conn = null;
	    PreparedStatement ps = null;
	    
	    try {
	        conn = DBUtil.getConnection();
	        ps = conn.prepareStatement(sql);
	        
	        ps.setInt(1, job.getEmployerId());
	        ps.setString(2, job.getTitle());
	        ps.setString(3, job.getDescription());
	        ps.setString(4, job.getSkills());
	        ps.setInt(5, job.getExperienceYears());
	        ps.setString(6, job.getEducation());
	        ps.setString(7, job.getLocation());
	        ps.setDouble(8, job.getSalaryMin());
	        ps.setDouble(9, job.getSalaryMax());
	        ps.setString(10, job.getJobType());
	        ps.setDate(11, new java.sql.Date(job.getDeadline().getTime()));
	        
	        int result = ps.executeUpdate();
	        
	        // Return 1 for success, -1 for failure
	        return result > 0 ? 1 : -1;
	        
	    } finally {
	        if (ps != null) {
	            try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
	        }
	        if (conn != null) {
	            try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	        }
	    }
	}
    
    public List<Job> getAllJobs() throws SQLException {
        List<Job> jobs = new ArrayList<Job>();
        String sql = "SELECT * FROM jobs WHERE status = 'open' ORDER BY created_at DESC";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setEmployerId(rs.getInt("employer_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setSkills(rs.getString("skills"));
                job.setExperienceYears(rs.getInt("experience_years"));
                job.setEducation(rs.getString("education"));
                job.setLocation(rs.getString("location"));
                job.setSalaryMin(rs.getDouble("salary_min"));
                job.setSalaryMax(rs.getDouble("salary_max"));
                job.setJobType(rs.getString("job_type"));
                job.setDeadline(rs.getDate("deadline"));
                job.setStatus(rs.getString("status"));
                job.setCreatedAt(rs.getDate("created_at"));
                jobs.add(job);
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return jobs;
    }
    
    public List<Job> searchJobs(String keyword, String location, String jobType, Double minSalary) throws SQLException {
        List<Job> jobs = new ArrayList<Job>();
        StringBuilder sql = new StringBuilder("SELECT * FROM jobs WHERE status = 'open'");
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (title LIKE ? OR description LIKE ? OR skills LIKE ?)");
        }
        if (location != null && !location.trim().isEmpty()) {
            sql.append(" AND location LIKE ?");
        }
        if (jobType != null && !jobType.trim().isEmpty()) {
            sql.append(" AND job_type = ?");
        }
        if (minSalary != null) {
            sql.append(" AND salary_max >= ?");
        }
        
        sql.append(" ORDER BY created_at DESC");
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String likeKeyword = "%" + keyword + "%";
                ps.setString(paramIndex++, likeKeyword);
                ps.setString(paramIndex++, likeKeyword);
                ps.setString(paramIndex++, likeKeyword);
            }
            if (location != null && !location.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + location + "%");
            }
            if (jobType != null && !jobType.trim().isEmpty()) {
                ps.setString(paramIndex++, jobType);
            }
            if (minSalary != null) {
                ps.setDouble(paramIndex++, minSalary);
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setEmployerId(rs.getInt("employer_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setSkills(rs.getString("skills"));
                job.setExperienceYears(rs.getInt("experience_years"));
                job.setEducation(rs.getString("education"));
                job.setLocation(rs.getString("location"));
                job.setSalaryMin(rs.getDouble("salary_min"));
                job.setSalaryMax(rs.getDouble("salary_max"));
                job.setJobType(rs.getString("job_type"));
                job.setDeadline(rs.getDate("deadline"));
                job.setStatus(rs.getString("status"));
                job.setCreatedAt(rs.getDate("created_at"));
                jobs.add(job);
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return jobs;
    }
    
    public Job getJobById(int jobId) throws SQLException {
        String sql = "SELECT * FROM jobs WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, jobId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setEmployerId(rs.getInt("employer_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setSkills(rs.getString("skills"));
                job.setExperienceYears(rs.getInt("experience_years"));
                job.setEducation(rs.getString("education"));
                job.setLocation(rs.getString("location"));
                job.setSalaryMin(rs.getDouble("salary_min"));
                job.setSalaryMax(rs.getDouble("salary_max"));
                job.setJobType(rs.getString("job_type"));
                job.setDeadline(rs.getDate("deadline"));
                job.setStatus(rs.getString("status"));
                job.setCreatedAt(rs.getDate("created_at"));
                return job;
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return null;
    }
    
    public List<Job> getJobsByEmployer(int employerId) throws SQLException {
        List<Job> jobs = new ArrayList<Job>();
        String sql = "SELECT * FROM jobs WHERE employer_id = ? ORDER BY created_at DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, employerId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Job job = new Job();
                job.setId(rs.getInt("id"));
                job.setEmployerId(rs.getInt("employer_id"));
                job.setTitle(rs.getString("title"));
                job.setDescription(rs.getString("description"));
                job.setSkills(rs.getString("skills"));
                job.setExperienceYears(rs.getInt("experience_years"));
                job.setEducation(rs.getString("education"));
                job.setLocation(rs.getString("location"));
                job.setSalaryMin(rs.getDouble("salary_min"));
                job.setSalaryMax(rs.getDouble("salary_max"));
                job.setJobType(rs.getString("job_type"));
                job.setDeadline(rs.getDate("deadline"));
                job.setStatus(rs.getString("status"));
                job.setCreatedAt(rs.getDate("created_at"));
                jobs.add(job);
            }
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return jobs;
    }
    
    public boolean updateJobStatus(int jobId, String status) throws SQLException {
        String sql = "UPDATE jobs SET status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, jobId);
            
            return ps.executeUpdate() > 0;
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
 
}
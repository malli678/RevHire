package com.revhire.dao;

import com.revhire.model.JobSeeker;
import com.revhire.util.DBUtil;
import java.sql.*;

public class JobSeekerDAO {
    
	/**
     * Registers a new job seeker in the database
     * @param jobSeeker JobSeeker object containing registration details
     * @return true if registration successful, false otherwise
     * @throws SQLException if database error occurs
     */
	public boolean registerJobSeeker(JobSeeker jobSeeker) throws SQLException {
        String sql = "INSERT INTO jobseekers (user_id, objective, skills, certifications) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, jobSeeker.getUserId());
            ps.setString(2, jobSeeker.getObjective());
            ps.setString(3, jobSeeker.getSkills());
            ps.setString(4, jobSeeker.getCertifications());
            
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
    
	/**
     * Retrieves job seeker by user ID
     * @param userId The user ID of the job seeker
     * @return JobSeeker object if found, null otherwise
     * @throws SQLException if database error occurs
     */
	public JobSeeker getJobSeekerByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM jobseekers WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                JobSeeker jobSeeker = new JobSeeker();
                jobSeeker.setUserId(rs.getInt("user_id"));
                jobSeeker.setObjective(rs.getString("objective"));
                jobSeeker.setSkills(rs.getString("skills"));
                jobSeeker.setCertifications(rs.getString("certifications"));
                return jobSeeker;
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
    
	/**
     * Updates job seeker profile information
     * @param jobSeeker JobSeeker object with updated information
     * @return true if update successful, false otherwise
     * @throws SQLException if database error occurs
     */
	public boolean updateJobSeeker(JobSeeker jobSeeker) throws SQLException {
        String sql = "UPDATE jobseekers SET objective = ?, skills = ?, certifications = ? WHERE user_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, jobSeeker.getObjective());
            ps.setString(2, jobSeeker.getSkills());
            ps.setString(3, jobSeeker.getCertifications());
            ps.setInt(4, jobSeeker.getUserId());
            
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
package com.revhire.dao;

import com.revhire.model.Application;
import com.revhire.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {
    
    public int applyForJob(Application application) throws SQLException {
        String sql = "INSERT INTO applications (id, jobseeker_id, job_id, resume_id, cover_letter, status) VALUES (app_seq.NEXTVAL, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, new String[]{"id"});
            
            ps.setInt(1, application.getJobSeekerId());
            ps.setInt(2, application.getJobId());
            ps.setInt(3, application.getResumeId());
            ps.setString(4, application.getCoverLetter());
            ps.setString(5, application.getStatus());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return -1;
        } finally {
            // Close resources in reverse order
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
    }
    
    public List<Application> getApplicationsByJobSeeker(int jobSeekerId) throws SQLException {
        List<Application> applications = new ArrayList<Application>();
        String sql = "SELECT a.*, j.title as job_title FROM applications a JOIN jobs j ON a.job_id = j.id WHERE a.jobseeker_id = ? ORDER BY a.applied_at DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, jobSeekerId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setJobSeekerId(rs.getInt("jobseeker_id"));
                app.setJobId(rs.getInt("job_id"));
                app.setResumeId(rs.getInt("resume_id"));
                app.setCoverLetter(rs.getString("cover_letter"));
                app.setStatus(rs.getString("status"));
                app.setAppliedAt(rs.getDate("applied_at"));
                app.setReason(rs.getString("reason"));
                applications.add(app);
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
        return applications;
    }
    
    public List<Application> getApplicationsByJob(int jobId) throws SQLException {
        List<Application> applications = new ArrayList<Application>();
        String sql = "SELECT a.*, u.name as seeker_name FROM applications a JOIN jobseekers js ON a.jobseeker_id = js.user_id JOIN users u ON js.user_id = u.id WHERE a.job_id = ? ORDER BY a.applied_at DESC";
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, jobId);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setJobSeekerId(rs.getInt("jobseeker_id"));
                app.setJobId(rs.getInt("job_id"));
                app.setResumeId(rs.getInt("resume_id"));
                app.setCoverLetter(rs.getString("cover_letter"));
                app.setStatus(rs.getString("status"));
                app.setAppliedAt(rs.getDate("applied_at"));
                app.setReason(rs.getString("reason"));
                applications.add(app);
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
        return applications;
    }
    
    public boolean updateApplicationStatus(int applicationId, String status, String reason) throws SQLException {
        String sql = "UPDATE applications SET status = ?, reason = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, reason);
            ps.setInt(3, applicationId);
            
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
    
    public boolean withdrawApplication(int applicationId) throws SQLException {
        return updateApplicationStatus(applicationId, "withdrawn", "Withdrawn by applicant");
    }
    
    public boolean hasApplied(int jobSeekerId, int jobId) throws SQLException {
        String sql = "SELECT 1 FROM applications WHERE jobseeker_id = ? AND job_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, jobSeekerId);
            ps.setInt(2, jobId);
            rs = ps.executeQuery();
            
            return rs.next();
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
    }
}
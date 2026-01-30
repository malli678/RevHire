package com.revhire.dao;

import com.revhire.model.Application;
import com.revhire.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
    
    // BULK ACTION: Update multiple applications at once - FIXED FOR ORACLE
    public boolean bulkUpdateApplications(List<Integer> applicationIds, String status, String reason) throws SQLException {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return false;
        }
        
        // Build the SQL with dynamic number of placeholders
        // Use NVL for Oracle instead of IFNULL
        StringBuilder sql = new StringBuilder("UPDATE applications SET status = ?, reason = ? WHERE id IN (");
        
        // Add placeholders
        for (int i = 0; i < applicationIds.size(); i++) {
            sql.append("?");
            if (i < applicationIds.size() - 1) {
                sql.append(",");
            }
        }
        sql.append(")");
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            
            // Set status and reason
            ps.setString(1, status);
            ps.setString(2, reason);
            
            // Set application IDs
            for (int i = 0; i < applicationIds.size(); i++) {
                ps.setInt(i + 3, applicationIds.get(i));
            }
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } finally {
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
    
    // SIMPLIFIED: Search applications with basic filters - FIXED FOR ORACLE
    public List<Application> searchApplicationsWithFilters(int employerId, Map<String, Object> filters) throws SQLException {
        List<Application> applications = new ArrayList<Application>();
        
        // SIMPLIFIED SQL - only use columns that exist
        StringBuilder sql = new StringBuilder(
            "SELECT a.*, u.name as seeker_name, u.email, u.location, u.phone, " +
            "j.title as job_title " +
            "FROM applications a " +
            "JOIN jobs j ON a.job_id = j.id " +
            "JOIN users u ON a.jobseeker_id = u.id " +  // Direct join to users table
            "WHERE j.employer_id = ?"
        );
        
        List<Object> params = new ArrayList<Object>();
        params.add(employerId);
        
        // Add only basic filters (remove skills/education filters)
        if (filters != null) {
            if (filters.containsKey("jobId") && (Integer)filters.get("jobId") > 0) {
                sql.append(" AND a.job_id = ?");
                params.add(filters.get("jobId"));
            }
            
            if (filters.containsKey("status") && filters.get("status") != null && !((String)filters.get("status")).isEmpty()) {
                sql.append(" AND a.status = ?");
                params.add(filters.get("status"));
            }
            
            // Simple date filter
            if (filters.containsKey("fromDate")) {
                sql.append(" AND a.applied_at >= ?");
                params.add(filters.get("fromDate"));
            }
            
            if (filters.containsKey("toDate")) {
                sql.append(" AND a.applied_at <= ?");
                params.add(filters.get("toDate"));
            }
            
            // Sorting
            sql.append(" ORDER BY ");
            if (filters.containsKey("sortBy")) {
                String sortBy = (String) filters.get("sortBy");
                if ("date".equals(sortBy)) {
                    sql.append("a.applied_at DESC");
                } else if ("name".equals(sortBy)) {
                    sql.append("u.name ASC");
                } else if ("status".equals(sortBy)) {
                    sql.append("a.status ASC");
                } else {
                    sql.append("a.applied_at DESC");
                }
            } else {
                sql.append("a.applied_at DESC");
            }
        } else {
            sql.append(" ORDER BY a.applied_at DESC");
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql.toString());
            
            // Set all parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
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
                
                // Set only available fields
                app.setSeekerName(rs.getString("seeker_name"));
                app.setSeekerEmail(rs.getString("email"));
                app.setSeekerPhone(rs.getString("phone"));
                app.setSeekerLocation(rs.getString("location"));
                app.setJobTitle(rs.getString("job_title"));
                
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
    
    // NEW: Update application with comments - FIXED FOR ORACLE (SIMPLIFIED)
    public boolean updateApplicationWithComments(int applicationId, String status, String comments) throws SQLException {
        // Get current reason first, then append new comments
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            
            // Get current reason
            String getSql = "SELECT reason FROM applications WHERE id = ?";
            ps = conn.prepareStatement(getSql);
            ps.setInt(1, applicationId);
            rs = ps.executeQuery();
            
            String currentReason = "";
            if (rs.next()) {
                currentReason = rs.getString("reason");
                if (currentReason == null) {
                    currentReason = "";
                }
            }
            
            // Close result set and statement
            rs.close();
            ps.close();
            
            // Append new comments
            String newReason = currentReason + comments;
            
            // Update with new reason
            String updateSql = "UPDATE applications SET status = ?, reason = ? WHERE id = ?";
            ps = conn.prepareStatement(updateSql);
            ps.setString(1, status);
            ps.setString(2, newReason);
            ps.setInt(3, applicationId);
            
            return ps.executeUpdate() > 0;
            
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
 // SIMPLE: Add comments without changing status - FIXED VERSION
    public boolean addCommentsOnly(int applicationId, String comments) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            
            // Get current reason
            String getSql = "SELECT reason FROM applications WHERE id = ?";
            ps = conn.prepareStatement(getSql);
            ps.setInt(1, applicationId);
            rs = ps.executeQuery();
            
            String currentReason = "";
            if (rs.next()) {
                currentReason = rs.getString("reason");
                if (currentReason == null) {
                    currentReason = "";
                }
            }
            
            // Close result set and statement
            rs.close();
            ps.close();
            
            // Append new comments
            String newReason = currentReason + comments;
            
            // Update ONLY reason, NOT status
            String updateSql = "UPDATE applications SET reason = ? WHERE id = ?";
            ps = conn.prepareStatement(updateSql);
            ps.setString(1, newReason);
            ps.setInt(2, applicationId);
            
            return ps.executeUpdate() > 0;
            
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
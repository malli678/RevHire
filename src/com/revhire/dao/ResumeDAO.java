package com.revhire.dao;

import com.revhire.model.Resume;
import com.revhire.util.DBUtil;
import java.sql.*;

public class ResumeDAO {
    
    public int createResume(Resume resume) throws SQLException {
        String sql = "INSERT INTO resumes (id, jobseeker_id, education, experience, projects) VALUES (resume_seq.NEXTVAL, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql, new String[]{"id"});
            
            ps.setInt(1, resume.getJobSeekerId());
            ps.setString(2, resume.getEducation());
            ps.setString(3, resume.getExperience());
            ps.setString(4, resume.getProjects());
            
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
    
    public Resume getResumeByJobSeekerId(int jobSeekerId) throws SQLException {
        String sql = "SELECT * FROM resumes WHERE jobseeker_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, jobSeekerId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Resume resume = new Resume();
                resume.setId(rs.getInt("id"));
                resume.setJobSeekerId(rs.getInt("jobseeker_id"));
                resume.setEducation(rs.getString("education"));
                resume.setExperience(rs.getString("experience"));
                resume.setProjects(rs.getString("projects"));
                return resume;
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
    
    public boolean updateResume(Resume resume) throws SQLException {
        String sql = "UPDATE resumes SET education = ?, experience = ?, projects = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, resume.getEducation());
            ps.setString(2, resume.getExperience());
            ps.setString(3, resume.getProjects());
            ps.setInt(4, resume.getId());
            
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
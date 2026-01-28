package com.revhire.dao;

import com.revhire.model.Employer;
import com.revhire.util.DBUtil;
import java.sql.*;

public class EmployerDAO {
    
    public boolean registerEmployer(Employer employer) throws SQLException {
        String sql = "INSERT INTO company_employers (employer_id, company_name, industry, company_size, description, website) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setInt(1, employer.getUserId());
            ps.setString(2, employer.getCompanyName());
            ps.setString(3, employer.getIndustry());
            ps.setString(4, employer.getSize());
            ps.setString(5, employer.getDescription());
            ps.setString(6, employer.getWebsite());
            
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
    
    public Employer getEmployerByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM company_employers WHERE employer_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Employer employer = new Employer();
                employer.setUserId(rs.getInt("employer_id"));
                employer.setCompanyName(rs.getString("company_name"));
                employer.setIndustry(rs.getString("industry"));
                employer.setSize(rs.getString("company_size"));
                employer.setDescription(rs.getString("description"));
                employer.setWebsite(rs.getString("website"));
                return employer;
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
    
    public boolean updateEmployer(Employer employer) throws SQLException {
        String sql = "UPDATE company_employers SET company_name = ?, industry = ?, company_size = ?, description = ?, website = ? WHERE employer_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(sql);
            
            ps.setString(1, employer.getCompanyName());
            ps.setString(2, employer.getIndustry());
            ps.setString(3, employer.getSize());
            ps.setString(4, employer.getDescription());
            ps.setString(5, employer.getWebsite());
            ps.setInt(6, employer.getUserId());
            
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
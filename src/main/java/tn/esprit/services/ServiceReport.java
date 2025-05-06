package tn.esprit.services;

import tn.esprit.entities.Report;
import tn.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ServiceReport {
    private Connection connection;

    public ServiceReport(){
        connection = MyDataBase.getInstance().getMyConnection();
    }

    public void addReport(Report report) throws SQLException {
        String query = "INSERT INTO report (comment_id, user_id, report_reason, report_date) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, report.getCommentId());
        ps.setInt(2, report.getUserId());
        ps.setString(3, report.getReportReason());
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(report.getReportDate()));
        ps.executeUpdate();
    }
    // Count reports for a comment
    public int countReportsForComment(int commentId) throws SQLException {
        String query = "SELECT COUNT(*) FROM report WHERE comment_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, commentId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }

    // Delete reports related to a comment
    public void deleteReportsByCommentId(int commentId) throws SQLException {
        String query = "DELETE FROM report WHERE comment_id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, commentId);
        ps.executeUpdate();
    }

}

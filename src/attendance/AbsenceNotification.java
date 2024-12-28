package attendance;

import dao.ConnectionProvider;
import java.sql.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class AbsenceNotification {

    public static void main(String[] args) {
        sendAbsenceNotifications();
    }

    public static void sendAbsenceNotifications() {
        try (Connection con = ConnectionProvider.getCon()) {
            // Query to find users marked as "Absent" for the current date
            String query = "SELECT u.id, u.email, u.name " +
                           "FROM users u " +
                           "JOIN attendance a ON u.id = a.user_id " +
                           "WHERE a.log_date = CURDATE() AND a.status = 'Absent'";

            try (PreparedStatement ps = con.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("id");
                    String email = rs.getString("email");
                    String name = rs.getString("name");

                    // Compose notification message
                    String message = "Dear " + name + ",\n\nYou were marked absent today. Please contact your supervisor if this is an error.\n\nBest regards,\nAttendance Management System";

                    // Send email notification
                    if (sendEmail(email, "Absence Notification", message)) {
                        // Log notification in the database
                        logNotification(con, userId, message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean sendEmail(String recipient, String subject, String body) {
        // Configure mail server
        String host = "smtp.gmail.com"; // Replace with your SMTP server
        String from = "menelikgete1@gmail.com"; // Replace with your email
        String password = "your-password"; // Replace with your email password

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");

        // Create session
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject(subject);
            message.setText(body);

            // Send email
            Transport.send(message);
            System.out.println("Email sent to: " + recipient);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void logNotification(Connection con, int userId, String message) {
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO notifications (user_id, message) VALUES (?, ?)")) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

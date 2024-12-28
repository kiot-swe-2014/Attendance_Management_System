package attendance;

import dao.ConnectionProvider;
import java.sql.*;
import javax.swing.*;
import java.awt.Font;

public class MarkAttendance extends JFrame {

    // Constructor to initialize the GUI
    public MarkAttendance() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Mark Attendance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);  // Increased window size
        setLayout(null);

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Font settings for text size
        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        // Add title at the top center
        JLabel lblTitle = new JLabel("Mark Attendance", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(0, 10, getWidth(), 40);  // Positioned at the top center
        add(lblTitle);

        // GUI Components
        JLabel lblUser = new JLabel("User:");
        lblUser.setFont(labelFont);  // Set font size for the label
        lblUser.setBounds(50, 60, 100, 30);
        add(lblUser);

        JComboBox<String> cbUser = new JComboBox<>();
        cbUser.setFont(labelFont);  // Set font size for the combo box
        cbUser.setBounds(150, 60, 350, 30);
        populateUserDropdown(cbUser);  // Populate the combo box with user data
        add(cbUser);

        JLabel lblDate = new JLabel("Date:");
        lblDate.setFont(labelFont);  // Set font size for the label
        lblDate.setBounds(50, 120, 100, 30);
        add(lblDate);

        JTextField txtDate = new JTextField();
        txtDate.setFont(labelFont);  // Set font size for the text field
        txtDate.setBounds(150, 120, 350, 30);
        add(txtDate);

        JLabel lblTime = new JLabel("Time:");
        lblTime.setFont(labelFont);  // Set font size for the label
        lblTime.setBounds(50, 180, 100, 30);
        add(lblTime);

        JTextField txtTime = new JTextField();
        txtTime.setFont(labelFont);  // Set font size for the text field
        txtTime.setBounds(150, 180, 350, 30);
        add(txtTime);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(labelFont);  // Set font size for the label
        lblStatus.setBounds(50, 240, 100, 30);
        add(lblStatus);

        JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Present", "Absent"});
        cbStatus.setFont(labelFont);  // Set font size for the combo box
        cbStatus.setBounds(150, 240, 350, 30);
        add(cbStatus);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setFont(buttonFont);  // Set font size for the button
        btnSubmit.setBounds(50, 320, 150, 40);
        add(btnSubmit);

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(buttonFont);  // Set font size for the button
        btnReset.setBounds(220, 320, 150, 40);
        add(btnReset);

        // Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setFont(buttonFont);  // Set font size for the button
        btnBack.setBounds(10, 10, 100, 30);
        add(btnBack);

        // Button Actions
        btnSubmit.addActionListener(e -> {
            String user = (String) cbUser.getSelectedItem();
            String date = txtDate.getText();
            String time = txtTime.getText();
            String status = (String) cbStatus.getSelectedItem();

            if (user.isEmpty() || date.isEmpty() || time.isEmpty() || status.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            // Save attendance data to database
            try (Connection con = ConnectionProvider.getCon();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO attendance (user_id, log_date, log_time, status) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, Integer.parseInt(user.split(" - ")[0]));  // Extract user ID from the selected user
                ps.setString(2, date);
                ps.setString(3, time);
                ps.setString(4, status);

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Attendance marked successfully!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error marking attendance: " + ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            cbUser.setSelectedIndex(0);
            txtDate.setText("");
            txtTime.setText("");
            cbStatus.setSelectedIndex(0);
        });

        // Back button action
        btnBack.addActionListener(e -> {
            new Dashboard("Test User", "Admin").setVisible(true); // Pass actual user details
            this.dispose(); // Close current frame
        });
    }

    private void populateUserDropdown(JComboBox<String> cbUser) {
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM users")) {
            while (rs.next()) {
                cbUser.addItem(rs.getString("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MarkAttendance().setVisible(true));
    }
}

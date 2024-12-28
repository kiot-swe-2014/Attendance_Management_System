package attendance;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Dashboard extends JFrame {

    private String userName;
    private String userRole;
    private Image backgroundImage;

    // Constructor to initialize the dashboard with user details
    public Dashboard(String userName, String userRole) {
        this.userName = userName;
        this.userRole = userRole;
        initComponents();
    }

    private void initComponents() {
        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);  // Enlarged size
        setLayout(null);

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Load the background image
        try {
            backgroundImage = ImageIO.read(new File("src/Images/Attendance.jpg")); // Correct path for background image
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Font settings for text size
        Font labelFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);

        // Title Text with semi-transparent background
        JLabel lblTitle = new JLabel("Attendance Management System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 30));  // Larger font size for title
        lblTitle.setBounds(200, 50, 600, 40); // Adjusted position for title
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);  // Center the text horizontally
        lblTitle.setForeground(Color.WHITE);  // Set text color to white for visibility

        // Adding a semi-transparent background behind the title to improve visibility
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(0, 0, 0, 100));  // Semi-transparent black background

        add(lblTitle);

        // Welcome Message
        JLabel lblWelcome = new JLabel("Welcome, " + userName + " (" + userRole + ")");
        lblWelcome.setFont(labelFont);  // Set font size for the label
        lblWelcome.setBounds(20, 120, 400, 30); // Adjusted size and position
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);  // Center the text horizontally
        lblWelcome.setForeground(Color.WHITE);  // Set text color to white for visibility
        add(lblWelcome);

        // Buttons for modules
        JButton btnUserRegistration = new JButton("User Registration");
        JButton btnMarkAttendance = new JButton("Mark Attendance");
        JButton btnAbsenceNotification = new JButton("Absence Notification");
        JButton btnReporting = new JButton("Generate Reports");
        JButton btnLogout = new JButton("Logout");

        // Set the bounds dynamically to center components
        int width = getWidth();
        int height = getHeight();

        // Calculate the positions for buttons to center them
        int buttonWidth = 250;
        int buttonHeight = 50;
        int gap = 30; // Vertical gap between buttons

        int xPosition = (width - buttonWidth) / 2; // Center horizontally

        // Buttons' Y positions based on vertical alignment
        int yPosition1 = (height / 5) - (buttonHeight / 2);
        int yPosition2 = yPosition1 + buttonHeight + gap;
        int yPosition3 = yPosition2 + buttonHeight + gap;
        int yPosition4 = yPosition3 + buttonHeight + gap;
        int yPosition5 = yPosition4 + buttonHeight + gap;

        // Setting bounds for buttons
        btnUserRegistration.setBounds(xPosition, yPosition1, buttonWidth, buttonHeight);
        btnMarkAttendance.setBounds(xPosition, yPosition2, buttonWidth, buttonHeight);
        btnAbsenceNotification.setBounds(xPosition, yPosition3, buttonWidth, buttonHeight);
        btnReporting.setBounds(xPosition, yPosition4, buttonWidth, buttonHeight);
        btnLogout.setBounds(xPosition, yPosition5, buttonWidth, buttonHeight);

        // Set font for buttons
        btnUserRegistration.setFont(buttonFont);
        btnMarkAttendance.setFont(buttonFont);
        btnAbsenceNotification.setFont(buttonFont);
        btnReporting.setFont(buttonFont);
        btnLogout.setFont(buttonFont);

        // Set button text color to black for better contrast
        btnUserRegistration.setForeground(Color.BLACK);
        btnMarkAttendance.setForeground(Color.BLACK);
        btnAbsenceNotification.setForeground(Color.BLACK);
        btnReporting.setForeground(Color.BLACK);
        btnLogout.setForeground(Color.BLACK);

        // Add buttons to frame
        add(btnUserRegistration);
        add(btnMarkAttendance);
        add(btnAbsenceNotification);
        add(btnReporting);
        add(btnLogout);

        // Button Actions
        btnUserRegistration.addActionListener(e -> {
            new UserRegistration().setVisible(true); // Redirect to User Registration module
            this.dispose();
        });

        btnMarkAttendance.addActionListener(e -> {
            new MarkAttendance().setVisible(true); // Redirect to Mark Attendance module
            this.dispose();
        });

        btnAbsenceNotification.addActionListener(e -> {
            new AbsenceNotification().setVisible(true); // Redirect to Absence Notification module
            this.dispose();
        });

        btnReporting.addActionListener(e -> {
            new Reporting().setVisible(true); // Redirect to Reporting module
            this.dispose();
        });

        btnLogout.addActionListener(e -> {
            new Login().setVisible(true); // Redirect back to Login module
            this.dispose();
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);  // Draw the image
        }
    }

    public static void main(String[] args) {
        // For testing, pass dummy data
        SwingUtilities.invokeLater(() -> new Dashboard("Test User", "Admin").setVisible(true));
    }
}

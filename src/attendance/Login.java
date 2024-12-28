package attendance;

import dao.ConnectionProvider;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Login extends JFrame {

    private Image backgroundImage;

    // Constructor to initialize the GUI
    public Login() {
        initComponents();
        try {
            // Load the background image using relative path
            backgroundImage = ImageIO.read(new File("src/Images/free.jpeg")); // Correct the path to free.jpeg
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initComponents() {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);  // Increased JFrame size
        setLayout(new BorderLayout());  // Use BorderLayout for proper component positioning

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Add the custom JPanel that will paint the background image
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null); // Use null layout to manually position components
        add(backgroundPanel, BorderLayout.CENTER);

        // Font settings for text size
        Font labelFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Font titleFont = new Font("Arial", Font.BOLD, 24);  // Font for the title

        // Add title at the center
        JLabel lblTitle = new JLabel("Attendance Management System", JLabel.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setBounds(0, 10, getWidth(), 40);  // Positioned at the top center
        backgroundPanel.add(lblTitle);

        // GUI Components
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);  // Set font size for the label
        lblEmail.setBounds(50, 80, 100, 30);
        backgroundPanel.add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setFont(labelFont);  // Set font size for the text field
        txtEmail.setBounds(150, 80, 200, 30);
        backgroundPanel.add(txtEmail);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);  // Set font size for the label
        lblPassword.setBounds(50, 130, 100, 30);
        backgroundPanel.add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(labelFont);  // Set font size for the password field
        txtPassword.setBounds(150, 130, 200, 30);
        backgroundPanel.add(txtPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(labelFont);  // Set font size for the label
        lblRole.setBounds(50, 180, 100, 30);
        backgroundPanel.add(lblRole);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        cbRole.setFont(labelFont);  // Set font size for the combo box
        cbRole.setBounds(150, 180, 200, 30);
        backgroundPanel.add(cbRole);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(buttonFont);  // Set font size for the button
        btnLogin.setBounds(50, 230, 100, 40);
        backgroundPanel.add(btnLogin);

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(buttonFont);  // Set font size for the button
        btnReset.setBounds(200, 230, 100, 40);
        backgroundPanel.add(btnReset);

        // Button Actions
        btnLogin.addActionListener(e -> {
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String role = cbRole.getSelectedItem().toString();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and Password are required!");
                return;
            }

            // Authenticate user
            try (Connection con = ConnectionProvider.getCon();
                 PreparedStatement ps = con.prepareStatement(
                         "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?")) {
                ps.setString(1, email);
                ps.setString(2, password); // Use hashed passwords in production
                ps.setString(3, role);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String userName = rs.getString("name");
                    JOptionPane.showMessageDialog(this, "Login successful! Welcome " + userName);

                    // Navigate to Dashboard with user details
                    new Dashboard(userName, role).setVisible(true);
                    this.dispose(); // Close the Login window
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error during login: " + ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            txtEmail.setText("");
            txtPassword.setText("");
            cbRole.setSelectedIndex(0);
        });
    }

    // Custom JPanel class to paint the background image
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);  // Draw the image
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}

package attendance;

import dao.ConnectionProvider;
import java.sql.*;
import javax.swing.*;
import java.awt.Font;

public class UserRegistration extends JFrame {

    // Constructor to initialize the GUI
    public UserRegistration() {
        initComponents();
    }

    private void initComponents() {
        setTitle("User Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);  // Increased JFrame size for a bigger interface
        setLayout(null);

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Font settings for text size
        Font labelFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Font titleFont = new Font("Arial", Font.BOLD, 24);  // Font for the title

        // Add title at the center
        JLabel lblTitle = new JLabel("User Registration", JLabel.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setBounds(0, 10, getWidth(), 40);  // Positioned at the top center
        add(lblTitle);

        // GUI Components
        JLabel lblName = new JLabel("Name:");
        lblName.setFont(labelFont);  // Set font size for the label
        lblName.setBounds(50, 60, 100, 30);
        add(lblName);

        JTextField txtName = new JTextField();
        txtName.setFont(labelFont);  // Set font size for the text field
        txtName.setBounds(150, 60, 350, 30);
        add(txtName);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);  // Set font size for the label
        lblEmail.setBounds(50, 120, 100, 30);
        add(lblEmail);

        JTextField txtEmail = new JTextField();
        txtEmail.setFont(labelFont);  // Set font size for the text field
        txtEmail.setBounds(150, 120, 350, 30);
        add(txtEmail);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);  // Set font size for the label
        lblPassword.setBounds(50, 180, 100, 30);
        add(lblPassword);

        JPasswordField txtPassword = new JPasswordField();
        txtPassword.setFont(labelFont);  // Set font size for the password field
        txtPassword.setBounds(150, 180, 350, 30);
        add(txtPassword);

        JLabel lblConfirmPassword = new JLabel("Confirm Password:");
        lblConfirmPassword.setFont(labelFont);  // Set font size for the label
        lblConfirmPassword.setBounds(50, 240, 200, 30);
        add(lblConfirmPassword);

        JPasswordField txtConfirmPassword = new JPasswordField();
        txtConfirmPassword.setFont(labelFont);  // Set font size for the password field
        txtConfirmPassword.setBounds(250, 240, 250, 30);
        add(txtConfirmPassword);

        JLabel lblRole = new JLabel("Role:");
        lblRole.setFont(labelFont);  // Set font size for the label
        lblRole.setBounds(50, 300, 100, 30);
        add(lblRole);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        cbRole.setFont(labelFont);  // Set font size for the combo box
        cbRole.setBounds(150, 300, 350, 30);
        add(cbRole);

        JLabel lblContact = new JLabel("Contact:");
        lblContact.setFont(labelFont);  // Set font size for the label
        lblContact.setBounds(50, 360, 100, 30);
        add(lblContact);

        JTextField txtContact = new JTextField();
        txtContact.setFont(labelFont);  // Set font size for the text field
        txtContact.setBounds(150, 360, 350, 30);
        add(txtContact);

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(buttonFont);  // Set font size for the button
        btnRegister.setBounds(50, 430, 150, 40);
        add(btnRegister);

        JButton btnReset = new JButton("Reset");
        btnReset.setFont(buttonFont);  // Set font size for the button
        btnReset.setBounds(220, 430, 150, 40);
        add(btnReset);

        // Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setFont(buttonFont);  // Set font size for the button
        btnBack.setBounds(10, 10, 100, 30);
        add(btnBack);

        // Button Actions
        btnRegister.addActionListener(e -> {
            String name = txtName.getText();
            String email = txtEmail.getText();
            String password = new String(txtPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());
            String role = cbRole.getSelectedItem().toString();
            String contact = txtContact.getText();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            // Save to database
            try (Connection con = ConnectionProvider.getCon();
                 PreparedStatement ps = con.prepareStatement(
                         "INSERT INTO users (name, email, password, role, contact) VALUES (?, ?, ?, ?, ?)")) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, password); // Use hashing for security in production
                ps.setString(4, role);
                ps.setString(5, contact);

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "User registered successfully!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error registering user: " + ex.getMessage());
            }
        });

        btnReset.addActionListener(e -> {
            txtName.setText("");
            txtEmail.setText("");
            txtPassword.setText("");
            txtConfirmPassword.setText("");
            cbRole.setSelectedIndex(0);
            txtContact.setText("");
        });

        // Back button action
        btnBack.addActionListener(e -> {
            new Dashboard("Test User", "Admin").setVisible(true); // Pass actual user details
            this.dispose(); // Close current frame
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserRegistration().setVisible(true));
    }
}

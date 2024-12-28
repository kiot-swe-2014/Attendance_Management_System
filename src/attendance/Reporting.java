package attendance;

import dao.ConnectionProvider;
import java.awt.Font;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.IOException;

public class Reporting extends JFrame {

    private JTextField txtStartDate, txtEndDate;
    private JComboBox<String> cbUsers, cbStatus;
    private JTable table;
    private DefaultTableModel tableModel;

    public Reporting() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Attendance Reporting");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);  // Increased JFrame size for a bigger interface
        setLayout(null);

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Font settings for text size
        Font labelFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);
        Font titleFont = new Font("Arial", Font.BOLD, 24);  // Font for the title

        // Add title at the center
        JLabel lblTitle = new JLabel("Attendance Reporting", JLabel.CENTER);
        lblTitle.setFont(titleFont);
        lblTitle.setBounds(0, 10, getWidth(), 40);  // Positioned at the top center
        add(lblTitle);

        // Filters
        JLabel lblStartDate = new JLabel("Start Date:");
        lblStartDate.setFont(labelFont);  // Set font size for the label
        lblStartDate.setBounds(20, 60, 120, 30);
        add(lblStartDate);

        txtStartDate = new JTextField();
        txtStartDate.setFont(labelFont);  // Set font size for the text field
        txtStartDate.setBounds(140, 60, 180, 30);
        add(txtStartDate);

        JLabel lblEndDate = new JLabel("End Date:");
        lblEndDate.setFont(labelFont);  // Set font size for the label
        lblEndDate.setBounds(330, 60, 120, 30);
        add(lblEndDate);

        txtEndDate = new JTextField();
        txtEndDate.setFont(labelFont);  // Set font size for the text field
        txtEndDate.setBounds(450, 60, 180, 30);
        add(txtEndDate);

        JLabel lblUsers = new JLabel("User:");
        lblUsers.setFont(labelFont);  // Set font size for the label
        lblUsers.setBounds(20, 100, 120, 30);
        add(lblUsers);

        cbUsers = new JComboBox<>();
        cbUsers.setFont(labelFont);  // Set font size for the combo box
        cbUsers.setBounds(140, 100, 180, 30);
        populateUserDropdown();
        add(cbUsers);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(labelFont);  // Set font size for the label
        lblStatus.setBounds(330, 100, 120, 30);
        add(lblStatus);

        cbStatus = new JComboBox<>(new String[]{"All", "Present", "Absent"});
        cbStatus.setFont(labelFont);  // Set font size for the combo box
        cbStatus.setBounds(450, 100, 180, 30);
        add(cbStatus);

        JButton btnGenerate = new JButton("Generate Report");
        btnGenerate.setFont(buttonFont);  // Set font size for the button
        btnGenerate.setBounds(20, 140, 220, 40);
        add(btnGenerate);

        JButton btnExport = new JButton("Export Report");
        btnExport.setFont(buttonFont);  // Set font size for the button
        btnExport.setBounds(260, 140, 220, 40);
        add(btnExport);

        // Table to display report data
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 200, 740, 220);  // Adjusted scrollPane size
        add(scrollPane);

        // Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setFont(buttonFont);  // Set font size for the button
        btnBack.setBounds(5, 5, 120, 30);
        add(btnBack);

        // Button Actions
        btnGenerate.addActionListener(e -> generateReport());
        btnExport.addActionListener(e -> exportReport());

        // Back button action
        btnBack.addActionListener(e -> {
            new Dashboard("Test User", "Admin").setVisible(true); // Pass actual user details
            this.dispose(); // Close current frame
        });
    }

    private void populateUserDropdown() {
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name FROM users")) {
            cbUsers.addItem("All");
            while (rs.next()) {
                cbUsers.addItem(rs.getString("id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }
    }

    private void generateReport() {
        String startDate = txtStartDate.getText();
        String endDate = txtEndDate.getText();
        String selectedUser = (String) cbUsers.getSelectedItem();
        String status = (String) cbStatus.getSelectedItem();

        String userCondition = selectedUser.equals("All") ? "" : " AND a.user_id = " + selectedUser.split(" - ")[0];
        String statusCondition = status.equals("All") ? "" : " AND a.status = '" + status + "'";

        String query = "SELECT u.name, a.log_date, a.log_time, a.status " +
                       "FROM attendance a JOIN users u ON a.user_id = u.id " +
                       "WHERE a.log_date BETWEEN ? AND ?" + userCondition + statusCondition;

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage());
        }
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(tableModel.getColumnName(i) + "\t");
                }
                writer.write("\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        writer.write(tableModel.getValueAt(i, j).toString() + "\t");
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Reporting().setVisible(true));
    }
}

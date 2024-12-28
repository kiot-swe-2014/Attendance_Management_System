package attendance;

import dao.ConnectionProvider;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.imageio.ImageIO;

public class DataManagement extends JFrame {

    private JComboBox<String> cbTables;
    private JTable table;
    private DefaultTableModel tableModel;
    private Image backgroundImage;  // Declare image variable

    public DataManagement() {
        initComponents();
        loadBackgroundImage();  // Load the background image
    }

    private void initComponents() {
        setTitle("Data Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);  // Enlarged size
        setLayout(null);

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Font settings for text size
        Font labelFont = new Font("Arial", Font.PLAIN, 20);
        Font buttonFont = new Font("Arial", Font.BOLD, 18);

        // Table Selection Dropdown
        JLabel lblTable = new JLabel("Select Table:");
        lblTable.setFont(labelFont);  // Set font size for the label
        lblTable.setBounds(20, 20, 200, 30);
        lblTable.setForeground(Color.WHITE);  // Set label text color to white
        add(lblTable);

        cbTables = new JComboBox<>(new String[]{"users", "attendance", "notifications"});
        cbTables.setFont(labelFont);  // Set font size for the combo box
        cbTables.setBounds(220, 20, 300, 30);
        add(cbTables);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(buttonFont);  // Set font size for the button
        btnRefresh.setBounds(540, 20, 150, 30);
        btnRefresh.setBackground(Color.BLUE);  // Set button background color
        btnRefresh.setForeground(Color.WHITE);  // Set button text color
        add(btnRefresh);

        // Table to display data
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 70, 740, 300);  // Adjusted size
        add(scrollPane);

        JButton btnDelete = new JButton("Delete Selected");
        btnDelete.setFont(buttonFont);
        btnDelete.setBounds(20, 380, 200, 40);
        btnDelete.setBackground(Color.RED);  // Set button background color
        btnDelete.setForeground(Color.WHITE);  // Set button text color
        add(btnDelete);

        JButton btnBackup = new JButton("Backup Database");
        btnBackup.setFont(buttonFont);
        btnBackup.setBounds(240, 380, 200, 40);
        btnBackup.setBackground(Color.GREEN);  // Set button background color
        btnBackup.setForeground(Color.WHITE);  // Set button text color
        add(btnBackup);

        JButton btnRestore = new JButton("Restore Database");
        btnRestore.setFont(buttonFont);
        btnRestore.setBounds(460, 380, 200, 40);
        btnRestore.setBackground(Color.ORANGE);  // Set button background color
        btnRestore.setForeground(Color.WHITE);  // Set button text color
        add(btnRestore);

        // Button Actions
        btnRefresh.addActionListener(e -> refreshTable());
        btnDelete.addActionListener(e -> deleteSelectedRecord());
        btnBackup.addActionListener(e -> backupDatabase());
        btnRestore.addActionListener(e -> restoreDatabase());

        // Back Button (moved to the bottom)
        JButton btnBack = new JButton("Back");
        btnBack.setFont(buttonFont);
        int bottomY = getHeight() - 70; // 70px from the bottom of the frame
        btnBack.setBounds((getWidth() - 100) / 2, bottomY, 100, 30); // Center horizontally at the bottom
        btnBack.setBackground(Color.GRAY);  // Set button background color
        btnBack.setForeground(Color.WHITE);  // Set button text color
        add(btnBack);

        btnBack.addActionListener(e -> {
            new Dashboard("Test User", "Admin").setVisible(true); // Pass actual user details
            this.dispose();
        });
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("src/Images/")); // Correct path for background image
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        String tableName = (String) cbTables.getSelectedItem();
        try (Connection con = ConnectionProvider.getCon();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            tableModel.setRowCount(0); // Clear existing data
            tableModel.setColumnCount(0);

            // Add columns to the table
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(metaData.getColumnName(i));
            }

            // Add rows to the table
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading table: " + ex.getMessage());
        }
    }

    private void deleteSelectedRecord() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "No record selected!");
            return;
        }

        String tableName = (String) cbTables.getSelectedItem();
        Object id = tableModel.getValueAt(selectedRow, 0); // Assuming ID is the first column

        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement ps = con.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?")) {
            ps.setObject(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Record deleted successfully!");
                refreshTable();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting record: " + ex.getMessage());
        }
    }

    private void backupDatabase() {
        try {
            Process process = Runtime.getRuntime().exec(
                    "mysqldump -u root -p --databases attendancejframebd -r backup.sql");
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                JOptionPane.showMessageDialog(this, "Database backup created successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Database backup failed!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating backup: " + ex.getMessage());
        }
    }

    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Process process = Runtime.getRuntime().exec(
                        "mysql -u root -p attendancejframebd < " + file.getAbsolutePath());
                int processComplete = process.waitFor();
                if (processComplete == 0) {
                    JOptionPane.showMessageDialog(this, "Database restored successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Database restoration failed!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error restoring database: " + ex.getMessage());
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);  // Draw the image
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataManagement().setVisible(true));
    }
}

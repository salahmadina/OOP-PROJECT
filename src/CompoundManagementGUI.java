import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.time.LocalDate;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.util.Date;

public class CompoundManagementGUI extends JFrame {

    // --- Backend Systems ---
    private static SignUp signUpSystem = new SignUp();
    private static BusSchedules busScheduleSystem = new BusSchedules();
    private static QRGateSystem qrGateSystem = new QRGateSystem();
    private static ArrayList<Gate> compoundGates = new ArrayList<>();
    // Shared in-memory request state so Residents and Staff see the same requests
    private static final java.util.List<request> sharedRequests = new java.util.ArrayList<>();
    private static final java.util.Map<request, String> requestStatuses = new java.util.HashMap<>();
    private static final java.util.Map<request, Resident> requestOwners = new java.util.HashMap<>();
    private static final java.util.Map<request, String> requestServices = new java.util.HashMap<>();
    private static final java.util.Map<request, String> requestDetails = new java.util.HashMap<>();
    // Simple view refresher registry
    private static final java.util.Map<String, java.util.List<Runnable>> viewRefreshers = new java.util.HashMap<>();

    private static void registerRefresher(String view, Runnable r) {
        viewRefreshers.computeIfAbsent(view, k -> new java.util.ArrayList<>()).add(r);
    }

    private static void runRefreshers(String view) {
        java.util.List<Runnable> list = viewRefreshers.get(view);
        if (list != null) for (Runnable rr : list) try { rr.run(); } catch (Exception ignored) {}
    }

    // --- GUI Components ---
    private JPanel mainContainer;
    private CardLayout cardLayout;

    // View Constants
    private static final String LOGIN_VIEW = "LOGIN";
    private static final String SIGNUP_VIEW = "SIGNUP";
    private static final String RESIDENT_DASHBOARD = "RESIDENT_DASH";
    private static final String STAFF_DASHBOARD = "STAFF_DASH";

    // Colors & Fonts
    private static final Color SIDEBAR_COLOR = new Color(33, 47, 61);
    private static final Color ACCENT_COLOR = new Color(52, 152, 219);
    private static final Color BG_COLOR = new Color(236, 240, 241);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public CompoundManagementGUI() {
        // Initialize Backend Data (loads from files or defaults)
        initGates();

        // Frame Setup
        setTitle("Compound Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // Add shutdown hook to save data when window closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("[GUI] Saving data on shutdown...");
            signUpSystem.saveUsers();
            busScheduleSystem.saveBuses();
            qrGateSystem.saveAllData();
        }));

        // Main Layout
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Add Views
        mainContainer.add(createLoginPanel(), LOGIN_VIEW);
        mainContainer.add(createSignUpPanel(), SIGNUP_VIEW);

        // Dashboards will be added dynamically on login

        add(mainContainer);
    }

    private void initGates() {
        if(compoundGates.isEmpty()) {
            Gate mainGate = new Gate(1, "Main Entrance");
            Gate backGate = new Gate(2, "Back Entrance");
            compoundGates.add(mainGate);
            compoundGates.add(backGate);
            qrGateSystem.addGate(mainGate);
            qrGateSystem.addGate(backGate);
        }
    }

    public static void main(String[] args) {
        // Set Modern Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new CompoundManagementGUI().setVisible(true));
    }

    // =========================================================================
    // 1. MODERN LOGIN PANEL
    // =========================================================================
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(40, 40, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(SIDEBAR_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subLabel = new JLabel("Compound Management System");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(Color.GRAY);
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField userField = new JTextField(20);
        JPasswordField passField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, ACCENT_COLOR);

        JButton signUpButton = new JButton("Create Account");
        styleButton(signUpButton, new Color(39, 174, 96));

        // Layout Components inside Card
        gbc.gridx = 0; gbc.gridy = 0;
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        card.add(subLabel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(30, 10, 5, 10);
        card.add(new JLabel("Username"), gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 10, 10, 10);
        card.add(userField, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(10, 10, 5, 10);
        card.add(new JLabel("Password"), gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 10, 20, 10);
        card.add(passField, gbc);

        gbc.gridy = 6;
        card.add(loginButton, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(10, 10, 0, 10);
        card.add(signUpButton, gbc);

        panel.add(card);

        // Actions
        loginButton.addActionListener(e -> performLogin(userField.getText(), new String(passField.getPassword())));
        signUpButton.addActionListener(e -> cardLayout.show(mainContainer, SIGNUP_VIEW));

        return panel;
    }

    private void performLogin(String username, String password) {
        person foundPerson = null;
        for (person p : signUpSystem.getUsers()) {
            if (p.getUsername().equals(username)) {
                foundPerson = p;
                break;
            }
        }

        if (foundPerson != null) {
            loginclass login = new loginclass(foundPerson);

            // --- CRITICAL FIX FOR FREEZING ---
            // The validateLogin method calls offerUpdateInfo() which creates a Scanner(System.in)
            // and waits for input. We temporarily redirect System.in to feed it "no" automatically.
            InputStream originalIn = System.in;
            try {
                System.setIn(new ByteArrayInputStream("no\n".getBytes()));

                boolean success = login.validateLogin(username, password);

                if (success) {
                    if (foundPerson instanceof Resident) {
                        mainContainer.add(createDashboard((Resident) foundPerson), RESIDENT_DASHBOARD);
                        cardLayout.show(mainContainer, RESIDENT_DASHBOARD);
                    } else if (foundPerson instanceof Staff) {
                        mainContainer.add(createStaffDashboard((Staff) foundPerson), STAFF_DASHBOARD);
                        cardLayout.show(mainContainer, STAFF_DASHBOARD);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect Password", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                System.setIn(originalIn); // Restore normal system input
            }
            // ---------------------------------

        } else {
            JOptionPane.showMessageDialog(this, "User not found. Please Sign Up.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // 2. SIGN UP PANEL
    // =========================================================================
    private JPanel createSignUpPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel header = new JLabel("Create New Account", SwingConstants.CENTER);
        header.setFont(HEADER_FONT);
        header.setBorder(new EmptyBorder(20, 0, 20, 0));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(20, 100, 20, 100));

        JTextField nameF = new JTextField();
        JTextField userF = new JTextField();
        JPasswordField passF = new JPasswordField();
        JTextField emailF = new JTextField();
        JTextField phoneF = new JTextField();
        JTextField ageF = new JTextField();
        JTextField nidF = new JTextField();
        JTextField depF = new JTextField("None");

        // Resident specific
        JTextField aptF = new JTextField("0");
        JTextField buildF = new JTextField("0");

        String[] types = {"Resident", "Staff"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        addFormField(formPanel, "Account Type:", typeBox);
        addFormField(formPanel, "Full Name:", nameF);
        addFormField(formPanel, "Username:", userF);
        addFormField(formPanel, "Password:", passF);
        addFormField(formPanel, "Email:", emailF);
        addFormField(formPanel, "Phone:", phoneF);
        addFormField(formPanel, "Age:", ageF);
        addFormField(formPanel, "National ID:", nidF);
        addFormField(formPanel, "Dependants:", depF);

        JLabel aptL = new JLabel("Apartment Num:");
        JLabel buildL = new JLabel("Building Num:");
        formPanel.add(aptL); formPanel.add(aptF);
        formPanel.add(buildL); formPanel.add(buildF);

        // Toggle visibility based on type
        typeBox.addActionListener(e -> {
            boolean isResident = typeBox.getSelectedItem().equals("Resident");
            aptF.setEnabled(isResident);
            buildF.setEnabled(isResident);
        });

        JButton submitBtn = new JButton("Register");
        styleButton(submitBtn, ACCENT_COLOR);

        JButton backBtn = new JButton("Back to Login");
        backBtn.setForeground(Color.GRAY);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);

        JPanel btnPanel = new JPanel();
        btnPanel.add(backBtn);
        btnPanel.add(submitBtn);

        panel.add(header, BorderLayout.NORTH);
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> cardLayout.show(mainContainer, LOGIN_VIEW));

        submitBtn.addActionListener(e -> {
            try {
                // Get form data
                String type = (String) typeBox.getSelectedItem();
                String email = emailF.getText().trim();
                String phone = phoneF.getText().trim();
                String nationalID = nidF.getText().trim();
                
                // Validate for duplicates
                String validationError = validateDuplicates(email, phone, nationalID);
                if (validationError != null) {
                    JOptionPane.showMessageDialog(this, validationError, "Duplicate Data", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Validate required fields are not empty
                if (nameF.getText().trim().isEmpty() || userF.getText().trim().isEmpty() || 
                    email.isEmpty() || phone.isEmpty() || nationalID.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields!");
                    return;
                }

                // Create account
                if(type.equals("Resident")) {
                    person r = new Resident(new String(passF.getPassword()), userF.getText(), nameF.getText(),
                            Integer.parseInt(ageF.getText()), nationalID, phone, email,
                            "Resident", depF.getText(), Integer.parseInt(aptF.getText()), Integer.parseInt(buildF.getText()));
                    signUpSystem.getUsers().add(r);
                } else {
                    person s = new Staff(new String(passF.getPassword()), userF.getText(), nameF.getText(),
                            Integer.parseInt(ageF.getText()), nationalID, phone, email,
                            "Staff", depF.getText());
                    signUpSystem.getUsers().add(s);
                }
                signUpSystem.saveUsers();
                JOptionPane.showMessageDialog(this, "Account Created Successfully!");
                cardLayout.show(mainContainer, LOGIN_VIEW);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error: Age and Numbers must be integers.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        return panel;
    }

    private void addFormField(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(REGULAR_FONT);
        p.add(l);
        p.add(field);
    }

    private String validateDuplicates(String email, String phone, String nationalID) {
        for (person user : signUpSystem.getUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return "Email already registered!";
            }
            if (String.valueOf(user.getPhonenumber()).equals(phone)) {
                return "Phone number already registered!";
            }
            if (user.getNationalID().equals(nationalID)) {
                return "National ID already registered!";
            }
        }
        return null; // No duplicates found
    }

    // =========================================================================
    // 3. DASHBOARD (RESIDENT)
    // =========================================================================
    private JPanel createDashboard(Resident r) {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG_COLOR);

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel welcome = new JLabel("Welcome, " + r.getName().split(" ")[0]);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(welcome);
        
        JLabel roleLabel = new JLabel("RESIDENT");
        roleLabel.setForeground(new Color(52, 152, 219));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Content Area (Card Layout for Tabs)
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BG_COLOR);

        // Main menu first
        contentPanel.add(createResidentMainMenu(r, contentPanel), "MAIN");
        contentPanel.add(createTransportPanel(), "TRANSPORT");
        contentPanel.add(createServicePanel(r), "SERVICE");
        contentPanel.add(createReportPanel(r), "REPORT");
        contentPanel.add(createGatePanel(r), "GATE");
        contentPanel.add(createProfilePanel(r), "PROFILE");

        // Show main menu first
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "MAIN");

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton("Logout");
        styleButton(logout, new Color(192, 57, 43));
        logout.setMaximumSize(new Dimension(180, 40));
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> cardLayout.show(mainContainer, LOGIN_VIEW));
        sidebar.add(logout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        dashboard.add(sidebar, BorderLayout.WEST);
        dashboard.add(contentPanel, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createResidentMainMenu(Resident r, JPanel contentPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Resident Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(SIDEBAR_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Select an option to get started");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        // Grid of buttons
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        gridPanel.setBackground(new Color(236, 240, 241));
        gridPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        CardLayout cl = (CardLayout) contentPanel.getLayout();

        JButton transportBtn = createDashboardButton("🚌 Bus & Transport", "Book buses for your journey", new Color(52, 152, 219));
        transportBtn.addActionListener(e -> cl.show(contentPanel, "TRANSPORT"));

        JButton serviceBtn = createDashboardButton("🔧 Request Service", "Request maintenance & repairs", new Color(39, 174, 96));
        serviceBtn.addActionListener(e -> cl.show(contentPanel, "SERVICE"));

        JButton reportBtn = createDashboardButton("📋 File Report", "Report issues in compound", new Color(231, 76, 60));
        reportBtn.addActionListener(e -> cl.show(contentPanel, "REPORT"));

        JButton gateBtn = createDashboardButton("🚪 Gate Access", "Generate QR for gate entry", new Color(155, 89, 182));
        gateBtn.addActionListener(e -> cl.show(contentPanel, "GATE"));

        JButton profileBtn = createDashboardButton("👤 My Profile", "View your profile info", new Color(241, 196, 15));
        profileBtn.addActionListener(e -> cl.show(contentPanel, "PROFILE"));

        JButton helpBtn = createDashboardButton("❓ Help & Info", "Get help and information", new Color(52, 73, 94));
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "For support, contact the compound office.\nPhone: +1-XXX-XXX-XXXX"));

        gridPanel.add(transportBtn);
        gridPanel.add(serviceBtn);
        gridPanel.add(reportBtn);
        gridPanel.add(gateBtn);
        gridPanel.add(profileBtn);
        gridPanel.add(helpBtn);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStaffMainMenu(Staff s, JPanel contentPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Staff Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(SIDEBAR_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Select an option to get started");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        // Grid of buttons (same visual style as Resident)
        JPanel gridPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        gridPanel.setBackground(new Color(236, 240, 241));
        gridPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        CardLayout cl = (CardLayout) contentPanel.getLayout();

        JButton residentsBtn = createDashboardButton("👥 Manage Residents", "View and remove residents", new Color(52, 152, 219));
        residentsBtn.addActionListener(e -> cl.show(contentPanel, "RESIDENTS"));

        JButton reportsBtn = createDashboardButton("📋 Assigned Reports", "Handle assigned reports", new Color(231, 76, 60));
        reportsBtn.addActionListener(e -> cl.show(contentPanel, "REPORTS"));

        JButton requestsBtn = createDashboardButton("🔧 Service Requests", "Respond to resident requests", new Color(39, 174, 96));
        requestsBtn.addActionListener(e -> cl.show(contentPanel, "REQUESTS"));

        JButton logsBtn = createDashboardButton("📊 Gate Logs", "View gate entry logs", new Color(155, 89, 182));
        logsBtn.addActionListener(e -> cl.show(contentPanel, "LOGS"));

        JButton profileBtn = createDashboardButton("👤 My Profile", "View your profile info", new Color(241, 196, 15));
        profileBtn.addActionListener(e -> cl.show(contentPanel, "PROFILE"));

        JButton helpBtn = createDashboardButton("❓ Help & Info", "Get help and information", new Color(52, 73, 94));
        helpBtn.addActionListener(e -> JOptionPane.showMessageDialog(panel, "For support, contact the compound office.\nPhone: +1-XXX-XXX-XXXX"));

        gridPanel.add(residentsBtn);
        gridPanel.add(reportsBtn);
        gridPanel.add(requestsBtn);
        gridPanel.add(logsBtn);
        gridPanel.add(profileBtn);
        gridPanel.add(helpBtn);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createDashboardButton(String title, String subtitle, Color color) {
        JButton btn = new JButton("<html><center><font size='5'>" + title + "</font><br><font size='3'>" + subtitle + "</font></center></html>");
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(250, 150));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(
                    Math.max(0, color.getRed() - 30),
                    Math.max(0, color.getGreen() - 30),
                    Math.max(0, color.getBlue() - 30)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }

    // =========================================================================
    // 4. DASHBOARD (STAFF)
    // =========================================================================
    private JPanel createStaffDashboard(Staff s) {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(BG_COLOR);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel welcome = new JLabel("Staff Portal");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(welcome);
        
        JLabel nameLabel = new JLabel(s.getName().split(" ")[0]);
        nameLabel.setForeground(new Color(52, 152, 219));
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(nameLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BG_COLOR);

        contentPanel.add(createResidentListPanel(), "RESIDENTS");
        contentPanel.add(createStaffReportsPanel(s), "REPORTS");
        contentPanel.add(createStaffRequestsPanel(), "REQUESTS");
        contentPanel.add(createGateLogsPanel(), "LOGS");
        contentPanel.add(createProfilePanel(s), "PROFILE");
        contentPanel.add(createStaffMainMenu(s, contentPanel), "MAIN");

        // Show main menu first (Resident-like behavior)
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "MAIN");

        // Role label similar to resident
        JLabel roleLabel = new JLabel("STAFF");
        roleLabel.setForeground(new Color(52, 152, 219));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(roleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        sidebar.add(Box.createVerticalGlue());

        JButton logout = new JButton("Logout");
        styleButton(logout, new Color(192, 57, 43));
        logout.setMaximumSize(new Dimension(180, 40));
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> cardLayout.show(mainContainer, LOGIN_VIEW));
        sidebar.add(logout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        dashboard.add(sidebar, BorderLayout.WEST);
        dashboard.add(contentPanel, BorderLayout.CENTER);

        return dashboard;
    }

    // =========================================================================
    // FEATURE PANELS
    // =========================================================================

    private JPanel createTransportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(BG_COLOR);

        // Back Button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);
        JButton backBtn = new JButton("← Back to Menu");
        styleButton(backBtn, SIDEBAR_COLOR);
        topPanel.add(backBtn);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(BG_COLOR);
        JTextField destField = new JTextField(15);
        JButton searchBtn = new JButton("Search Buses");
        styleButton(searchBtn, ACCENT_COLOR);

        searchPanel.add(new JLabel("Destination:"));
        searchPanel.add(destField);
        searchPanel.add(searchBtn);

        // Bus Selection Panel (with clickable buttons)
        JPanel busSelectionPanel = new JPanel();
        busSelectionPanel.setLayout(new BoxLayout(busSelectionPanel, BoxLayout.Y_AXIS));
        busSelectionPanel.setBackground(BG_COLOR);
        busSelectionPanel.setBorder(BorderFactory.createTitledBorder("Available Buses"));
        
        JScrollPane busScrollPane = new JScrollPane(busSelectionPanel);
        busScrollPane.setPreferredSize(new Dimension(600, 200));

        // Seat Booking Panel
        JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bookingPanel.setBackground(BG_COLOR);
        JLabel selectedBusLabel = new JLabel("Selected Bus: None");
        selectedBusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField seatField = new JTextField(5);
        JButton bookBtn = new JButton("Book Seats");
        styleButton(bookBtn, new Color(39, 174, 96));

        bookingPanel.add(selectedBusLabel);
        bookingPanel.add(Box.createHorizontalStrut(20));
        bookingPanel.add(new JLabel("Seats:"));
        bookingPanel.add(seatField);
        bookingPanel.add(bookBtn);

        JTextArea outputArea = createConsoleArea();

        final Bus[] selectedBus = {null};

        searchBtn.addActionListener(e -> captureOutput(outputArea, () -> {
            busSelectionPanel.removeAll();
            String destination = destField.getText();
            System.out.println("Searching for buses to: " + destination);
            boolean found = busScheduleSystem.searchByDestination(destination);
            
            if (found && !BusSchedules.matchedBuses.isEmpty()) {
                int i = 1;
                for (Bus b : BusSchedules.matchedBuses) {
                    JButton busBtn = new JButton("Bus " + b.getBusId() + " | Price: " + (b.getDistanceKm() * b.getPricePerKm()));
                    styleButton(busBtn, new Color(52, 152, 219));
                    busBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                    busBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                    
                    int busIndex = i;
                    busBtn.addActionListener(ev -> {
                        selectedBus[0] = BusSchedules.matchedBuses.get(busIndex - 1);
                        selectedBusLabel.setText("Selected Bus: " + selectedBus[0].getBusId());
                        selectedBusLabel.setForeground(new Color(39, 174, 96));
                    });
                    
                    busSelectionPanel.add(busBtn);
                    busSelectionPanel.add(Box.createVerticalStrut(5));
                    System.out.println(i + ". Bus " + b.getBusId() + " Available");
                    i++;
                }
            } else {
                System.out.println("No buses found for this destination.");
            }
            busSelectionPanel.revalidate();
            busSelectionPanel.repaint();
        }));

        bookBtn.addActionListener(e -> captureOutput(outputArea, () -> {
            if (selectedBus[0] == null) {
                System.out.println("Please select a bus first.");
                return;
            }
            try {
                int seats = Integer.parseInt(seatField.getText());
                selectedBus[0].bookSeats(seats);
                busScheduleSystem.saveBuses();
                System.out.println("Booking completed!");
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number of seats.");
            }
        }));

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(BG_COLOR);
        mainContentPanel.add(searchPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG_COLOR);
        centerPanel.add(busScrollPane, BorderLayout.WEST);
        centerPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        
        mainContentPanel.add(centerPanel, BorderLayout.CENTER);
        mainContentPanel.add(bookingPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(mainContentPanel, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            JPanel parent = (JPanel) SwingUtilities.getAncestorOfClass(JPanel.class, panel);
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "MAIN");
        });

        return panel;
    }

    private JPanel createServicePanel(Resident r) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Back Button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(BG_COLOR);
        JButton backBtn = new JButton("← Back to Menu");
        styleButton(backBtn, SIDEBAR_COLOR);
        backPanel.add(backBtn);

        JPanel form = new JPanel(new GridLayout(5, 2, 15, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("Request Service"));

        // Service Type Options
        String[] serviceTypes = {"Plumbing", "Electrical", "Carpentry", "Painting", "HVAC", "Other"};
        JComboBox<String> typeBox = new JComboBox<>(serviceTypes);
        
        JTextField otherTypeF = new JTextField();
        otherTypeF.setEnabled(false);
        
        typeBox.addActionListener(e -> otherTypeF.setEnabled(typeBox.getSelectedItem().equals("Other")));

        // Date Picker
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        JTextField detailsF = new JTextField();

        form.add(new JLabel("Service Type:")); form.add(typeBox);
        form.add(new JLabel("Other (if needed):")); form.add(otherTypeF);
        form.add(new JLabel("Preferred Date:")); form.add(dateSpinner);
        form.add(new JLabel("Description:")); form.add(detailsF);

        JButton submitBtn = new JButton("Submit Request");
        styleButton(submitBtn, ACCENT_COLOR);
        form.add(submitBtn); form.add(new JLabel(""));

        JTextArea logArea = createConsoleArea();
        logArea.setPreferredSize(new Dimension(400, 150));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.add(form, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        submitBtn.addActionListener(e -> captureOutput(logArea, () -> {
            String serviceType = typeBox.getSelectedItem().equals("Other") ? otherTypeF.getText() : (String) typeBox.getSelectedItem();
            String date = dateEditor.getFormat().format((Date) dateSpinner.getValue());
            request req = new request(r, serviceType, detailsF.getText());
            // add to shared requests so staff can see and respond
            sharedRequests.add(req);
            requestOwners.put(req, r);
            requestStatuses.put(req, "Pending");
            requestServices.put(req, serviceType);
            requestDetails.put(req, detailsF.getText());
            System.out.println("Service: " + serviceType);
            System.out.println("Preferred Date: " + date);
            req.ShowTicket();
            signUpSystem.saveUsers();
            // refresh staff request panels if open
            runRefreshers("REQUESTS");
            // refresh resident service view
            runRefreshers("SERVICE");
        }));

        panel.add(backPanel, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        // My Requests list for this resident
        DefaultListModel<String> myReqModel = new DefaultListModel<>();
        JList<String> myReqList = new JList<>(myReqModel);
        myReqList.setVisibleRowCount(6);
        myReqList.setFixedCellWidth(360);
        JPanel myReqPanel = new JPanel(new BorderLayout());
        myReqPanel.setBorder(BorderFactory.createTitledBorder("My Requests (status shown)"));
        myReqPanel.add(new JScrollPane(myReqList), BorderLayout.CENTER);
        mainPanel.add(myReqPanel, BorderLayout.EAST);

        Runnable refreshMyRequests = () -> {
            myReqModel.clear();
            for (request rq : sharedRequests) {
                Resident owner = requestOwners.get(rq);
                if (owner != null && owner.getUsername().equals(r.getUsername())) {
                    String status = requestStatuses.getOrDefault(rq, "Pending");
                    String svc = requestServices.getOrDefault(rq, "");
                    myReqModel.addElement(svc + " — " + status);
                }
            }
        };
        registerRefresher("SERVICE", refreshMyRequests);
        refreshMyRequests.run();

        backBtn.addActionListener(e -> {
            JPanel parent = (JPanel) SwingUtilities.getAncestorOfClass(JPanel.class, panel);
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "MAIN");
        });

        return panel;
    }

    private JPanel createReportPanel(Resident r) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Back Button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(BG_COLOR);
        JButton backBtn = new JButton("← Back to Menu");
        styleButton(backBtn, SIDEBAR_COLOR);
        backPanel.add(backBtn);

        JPanel form = new JPanel(new GridLayout(4, 2, 15, 15));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("File Issue Report"));

        // Report Type Options
        String[] reportTypes = {"Noise", "Maintenance", "Security", "Cleanliness", "Other"};
        JComboBox<String> typeBox = new JComboBox<>(reportTypes);
        
        JTextField otherTypeF = new JTextField();
        otherTypeF.setEnabled(false);
        
        typeBox.addActionListener(e -> otherTypeF.setEnabled(typeBox.getSelectedItem().equals("Other")));

        JTextField descF = new JTextField();

        form.add(new JLabel("Report Type:")); form.add(typeBox);
        form.add(new JLabel("Other (if needed):")); form.add(otherTypeF);
        form.add(new JLabel("Description:")); form.add(descF);

        JButton submitBtn = new JButton("Send Report");
        styleButton(submitBtn, new Color(231, 76, 60));
        form.add(submitBtn); form.add(new JLabel(""));

        JTextArea logArea = createConsoleArea();
        logArea.setPreferredSize(new Dimension(400, 150));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.add(form, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        submitBtn.addActionListener(e -> captureOutput(logArea, () -> {
            String reportType = typeBox.getSelectedItem().equals("Other") ? otherTypeF.getText() : (String) typeBox.getSelectedItem();
            Report report = new Report(r, reportType, descF.getText());
            System.out.println("Report ID: " + report.getReportId());
            System.out.println("Type: " + reportType);
            boolean assigned = false;
            for (person p : signUpSystem.getUsers()) {
                if (p instanceof Staff) {
                    ((Staff) p).assignReport(report);
                    System.out.println("Assigned to: " + p.getName());
                    assigned = true;
                    break;
                }
            }
            if(!assigned) System.out.println("No staff available.");
            signUpSystem.saveUsers();
        }));

        panel.add(backPanel, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            JPanel parent = (JPanel) SwingUtilities.getAncestorOfClass(JPanel.class, panel);
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "MAIN");
        });

        return panel;
    }

    private JPanel createGatePanel(Resident r) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);

        // Back Button Panel
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(BG_COLOR);
        JButton backBtn = new JButton("← Back to Menu");
        styleButton(backBtn, SIDEBAR_COLOR);
        backPanel.add(backBtn);

        // Control Panel (Top)
        JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
        controlPanel.setBackground(BG_COLOR);
        controlPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Gate Access Control");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(SIDEBAR_COLOR);
        
        JPanel controlsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlsRow.setBackground(BG_COLOR);
        
        JButton genQR = new JButton("Generate My QR");
        styleButton(genQR, ACCENT_COLOR);
        
        JButton saveQR = new JButton("Save QR as PNG");
        styleButton(saveQR, new Color(155, 89, 182));
        saveQR.setEnabled(false);
        
        JLabel gateLabel = new JLabel("Select Gate:");
        gateLabel.setFont(REGULAR_FONT);
        String[] gates = {"Main Entrance", "Back Entrance"};
        JComboBox<String> gateBox = new JComboBox<>(gates);
        
        JButton scanBtn = new JButton("Scan & Enter");
        styleButton(scanBtn, new Color(39, 174, 96));

        controlsRow.add(genQR);
        controlsRow.add(saveQR);
        controlsRow.add(gateLabel);
        controlsRow.add(gateBox);
        controlsRow.add(scanBtn);
        
        controlPanel.add(titleLabel, BorderLayout.NORTH);
        controlPanel.add(controlsRow, BorderLayout.CENTER);

        // Center Panel - QR Display
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // QR Code Box
        JPanel qrBoxPanel = new JPanel();
        qrBoxPanel.setLayout(new BoxLayout(qrBoxPanel, BoxLayout.Y_AXIS));
        qrBoxPanel.setBackground(Color.WHITE);
        qrBoxPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            "Your QR Code",
            0, 0, REGULAR_FONT, ACCENT_COLOR));
        qrBoxPanel.setPreferredSize(new Dimension(360, 430));
        qrBoxPanel.setMaximumSize(new Dimension(360, 430));
        
        // QR display label (will show actual image)
        JLabel qrImageLabel = new JLabel();
        qrImageLabel.setHorizontalAlignment(JLabel.CENTER);
        qrImageLabel.setVerticalAlignment(JLabel.CENTER);
        qrImageLabel.setPreferredSize(new Dimension(320, 320));
        qrImageLabel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.BLACK, 2),
            new EmptyBorder(0, 0, 0, 0)));
        qrImageLabel.setBackground(new Color(245, 245, 245));
        qrImageLabel.setOpaque(true);
        
        // Initial placeholder text
        qrImageLabel.setText("Click 'Generate My QR'");
        qrImageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        qrImageLabel.setForeground(Color.GRAY);
        
        qrBoxPanel.add(Box.createVerticalStrut(10));
        qrBoxPanel.add(qrImageLabel);
        qrBoxPanel.add(Box.createVerticalStrut(10));

        // Message/Status Panel (Bottom)
        JPanel messagePanel = new JPanel(new BorderLayout(10, 10));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(BorderFactory.createTitledBorder("Status Messages"));
        
        JTextArea statusArea = new JTextArea(5, 40);
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusArea.setBackground(new Color(245, 245, 245));
        statusArea.setText("System ready. Please generate your QR code first.\n\n");
        
        messagePanel.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        centerPanel.add(qrBoxPanel, BorderLayout.NORTH);
        centerPanel.add(messagePanel, BorderLayout.CENTER);

        // Wrap center panel to center it
        JPanel wrappedCenter = new JPanel(new GridBagLayout());
        wrappedCenter.setBackground(BG_COLOR);
        wrappedCenter.add(centerPanel);

        final QRCode[] myQR = {null};
        final java.awt.image.BufferedImage[] generatedQRImage = {null};

        genQR.addActionListener(e -> {
            try {
                // Generate QR using inline method
                myQR[0] = qrGateSystem.assignQRCodeToUser(r.getPhonenumber());
                String qrData = myQR[0].getCode();
                
                // Generate BufferedImage with crisp black and white pixels (315x315)
                generatedQRImage[0] = generateQrCodeImage(qrData);
                
                // Display as icon in label
                qrImageLabel.setIcon(new ImageIcon(generatedQRImage[0]));
                qrImageLabel.setText(null);
                
                statusArea.append("✓ QR Code generated successfully!\n");
                statusArea.append("Code: " + qrData + "\n\n");
                gateBox.setEnabled(true);
                scanBtn.setEnabled(true);
                saveQR.setEnabled(true);
                qrGateSystem.saveAllData();
            } catch (Exception ex) {
                statusArea.append("✗ Error generating QR: " + ex.getMessage() + "\n\n");
                ex.printStackTrace();
            }
        });

        saveQR.addActionListener(e -> {
            if (generatedQRImage[0] == null) {
                statusArea.append("✗ No QR code generated yet.\n\n");
                return;
            }
            try {
                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("qrcode_" + r.getPhonenumber() + ".png"));
                int result = fileChooser.showSaveDialog(this);
                if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();
                    saveQrCodeImage(generatedQRImage[0], file);
                    statusArea.append("✓ QR code saved to: " + file.getAbsolutePath() + "\n\n");
                }
            } catch (Exception ex) {
                statusArea.append("✗ Error saving QR: " + ex.getMessage() + "\n\n");
            }
        });

        scanBtn.addActionListener(e -> {
            try {
                if(myQR[0] == null) {
                    statusArea.append("✗ Error: Please generate QR code first.\n\n");
                    return;
                }
                int idx = gateBox.getSelectedIndex();
                Gate g = compoundGates.get(idx);
                statusArea.append("→ Scanning at " + g.getLocation() + "...\n");
                if(g.scanQRCode(myQR[0])) {
                    qrGateSystem.logEntry(r.getPhonenumber());
                    statusArea.append("✓ ✓ ✓ WELCOME HOME! ✓ ✓ ✓\n");
                    statusArea.append("Access granted at " + g.getLocation() + "\n\n");
                } else {
                    statusArea.append("✗ ✗ ✗ ACCESS DENIED ✗ ✗ ✗\n");
                    statusArea.append("QR Code invalid or expired.\n\n");
                }
            } catch (Exception ex) {
                statusArea.append("✗ Error: " + ex.getMessage() + "\n\n");
            }
        });

        panel.add(backPanel, BorderLayout.NORTH);
        panel.add(controlPanel, BorderLayout.SOUTH);
        panel.add(wrappedCenter, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            JPanel parent = (JPanel) SwingUtilities.getAncestorOfClass(JPanel.class, panel);
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "MAIN");
        });

        return panel;
    }



    // --- STAFF PANELS ---

    private JPanel createResidentListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Using JTable for better visualization
        String[] columns = {"Name", "Username", "Email", "Phone"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        JButton refresh = new JButton("Refresh List");
        JButton remove = new JButton("Remove Selected");
        styleButton(refresh, ACCENT_COLOR);
        styleButton(remove, new Color(192, 57, 43));

        JPanel actions = new JPanel();
        actions.add(refresh);
        actions.add(remove);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        // Helper to load data
        Runnable loadData = () -> {
            model.setRowCount(0);
            for(person p : signUpSystem.getUsers()) {
                if(p instanceof Resident) {
                    model.addRow(new Object[]{p.getName(), p.getUsername(), p.getEmail(), p.getPhonenumber()});
                }
            }
        };

        refresh.addActionListener(e -> loadData.run());

        remove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                String username = (String) model.getValueAt(row, 1);
                person toRemove = null;
                for(person p : signUpSystem.getUsers()) {
                    if(p.getUsername().equals(username)) {
                        toRemove = p; break;
                    }
                }
                if(toRemove != null) {
                    signUpSystem.getUsers().remove(toRemove);
                    signUpSystem.saveUsers(); // Save after removal
                    loadData.run();
                    JOptionPane.showMessageDialog(panel, "Resident Removed.");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Select a row first.");
            }
        });

        loadData.run(); // Initial load
        return panel;
    }

    private JPanel createStaffReportsPanel(Staff s) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Reports List Panel (clickable)
        JPanel reportsListPanel = new JPanel();
        reportsListPanel.setLayout(new BoxLayout(reportsListPanel, BoxLayout.Y_AXIS));
        reportsListPanel.setBackground(BG_COLOR);
        reportsListPanel.setBorder(BorderFactory.createTitledBorder("Your Assigned Reports"));
        
        JScrollPane reportsScroll = new JScrollPane(reportsListPanel);
        reportsScroll.setPreferredSize(new Dimension(300, 300));

        // Detail Panel
        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(new LineBorder(Color.BLACK, 1));
        detailPanel.setPreferredSize(new Dimension(400, 300));

        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        statusPanel.setBackground(Color.WHITE);
        String[] statuses = {"Sent", "In Progress", "Solved"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        JButton updateBtn = new JButton("Update Status");
        styleButton(updateBtn, ACCENT_COLOR);

        statusPanel.add(new JLabel("Status:"));
        statusPanel.add(statusBox);
        statusPanel.add(updateBtn);

        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        detailPanel.add(statusPanel, BorderLayout.SOUTH);

        final Report[] selectedReport = {null};

        Runnable loadReports = () -> {
            reportsListPanel.removeAll();
            for (Report r : s.getAssignedReports()) {
                JButton reportBtn = new JButton("[" + r.getStatus() + "] " + r.getType() + " - " + r.getCitizenName());
                reportBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                reportBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                styleButton(reportBtn, new Color(52, 152, 219));

                reportBtn.addActionListener(e -> {
                    selectedReport[0] = r;
                    detailArea.setText("Report ID: " + r.getReportId() + "\n" +
                            "Citizen: " + r.getCitizenName() + "\n" +
                            "Type: " + r.getType() + "\n" +
                            "Date: " + r.getDateTime() + "\n" +
                            "Status: " + r.getStatus() + "\n\n" +
                            "Description:\n" + r.getDescription());
                    statusBox.setSelectedItem(r.getStatus());
                });

                reportsListPanel.add(reportBtn);
                reportsListPanel.add(Box.createVerticalStrut(5));
            }
            reportsListPanel.revalidate();
            reportsListPanel.repaint();
        };

        updateBtn.addActionListener(e -> {
            if (selectedReport[0] != null) {
                String newStatus = (String) statusBox.getSelectedItem();
                s.updateReportStatus(selectedReport[0], newStatus);
                signUpSystem.saveUsers();
                JOptionPane.showMessageDialog(panel, "Report status updated to: " + newStatus);
                loadReports.run();
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a report first.");
            }
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_COLOR);
        leftPanel.add(reportsScroll, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);
        JButton refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn, ACCENT_COLOR);
        refreshBtn.addActionListener(e -> loadReports.run());
        topPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(detailPanel, BorderLayout.CENTER);

        loadReports.run();
        return panel;
    }

    private JPanel createGateLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JTextArea log = createConsoleArea();
        log.setPreferredSize(new Dimension(600, 400));
        
        JButton refresh = new JButton("Refresh Logs");
        styleButton(refresh, ACCENT_COLOR);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);
        topPanel.add(refresh);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(log), BorderLayout.CENTER);

        refresh.addActionListener(e -> captureOutput(log, () -> qrGateSystem.showLogs()));
        return panel;
    }

    private JPanel createStaffRequestsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Service Requests List Panel
        JPanel requestsListPanel = new JPanel();
        requestsListPanel.setLayout(new BoxLayout(requestsListPanel, BoxLayout.Y_AXIS));
        requestsListPanel.setBackground(BG_COLOR);
        requestsListPanel.setBorder(BorderFactory.createTitledBorder("Service Requests"));
        
        JScrollPane requestsScroll = new JScrollPane(requestsListPanel);
        requestsScroll.setPreferredSize(new Dimension(300, 400));

        // Detail Panel
        JPanel detailPanel = new JPanel(new BorderLayout(10, 10));
        detailPanel.setBackground(Color.WHITE);
        detailPanel.setBorder(new LineBorder(Color.BLACK, 1));
        detailPanel.setPreferredSize(new Dimension(400, 400));

        JTextArea detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        JButton respondBtn = new JButton("Mark as Completed");
        styleButton(respondBtn, new Color(39, 174, 96));
        actionPanel.add(respondBtn);

        detailPanel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        detailPanel.add(actionPanel, BorderLayout.SOUTH);

        Runnable loadRequests = () -> {
            requestsListPanel.removeAll();
            for (request rq : sharedRequests) {
                Resident owner = requestOwners.get(rq);
                String status = requestStatuses.getOrDefault(rq, "Pending");
                String svc = requestServices.getOrDefault(rq, "");
                String label = "[" + status + "] " + (owner != null ? owner.getName() : "Unknown") + " — " + svc;
                JButton reqBtn = new JButton(label);
                reqBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                reqBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
                styleButton(reqBtn, new Color(155, 89, 182));

                    reqBtn.addActionListener(e -> {
                    String ownerInfo = owner != null ? ("Resident: " + owner.getName() + "\nPhone: " + owner.getPhonenumber() + "\nEmail: " + owner.getEmail() + "\n\n") : "Resident info unavailable\n\n";
                    String det = requestDetails.getOrDefault(rq, "");
                    detailArea.setText(ownerInfo + "Service: " + svc + "\nDetails: " + det + "\n\nStatus: " + status);
                    // store selected in detail area client property
                    detailArea.putClientProperty("selectedRequest", rq);
                });

                requestsListPanel.add(reqBtn);
                requestsListPanel.add(Box.createVerticalStrut(5));
            }
            requestsListPanel.revalidate();
            requestsListPanel.repaint();
        };

        respondBtn.addActionListener(e -> {
            Object obj = detailArea.getClientProperty("selectedRequest");
            if (obj instanceof request) {
                request sel = (request) obj;
                requestStatuses.put(sel, "Completed");
                JOptionPane.showMessageDialog(panel, "Request marked as completed.");
                signUpSystem.saveUsers();
                loadRequests.run();
                runRefreshers("SERVICE");
            } else {
                JOptionPane.showMessageDialog(panel, "Select a request first from the list.");
            }
        });

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_COLOR);
        leftPanel.add(requestsScroll, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_COLOR);
        JButton refreshBtn = new JButton("Refresh");
        styleButton(refreshBtn, ACCENT_COLOR);
        refreshBtn.addActionListener(e -> loadRequests.run());
        topPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(detailPanel, BorderLayout.CENTER);

        loadRequests.run();
        // register refresher so new resident requests update this list
        registerRefresher("REQUESTS", loadRequests);
        return panel;
    }

    private JPanel createProfilePanel(person p) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20,20,20,20));
        panel.setBackground(BG_COLOR);

        // Back Button
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setBackground(BG_COLOR);
        JButton backBtn = new JButton("← Back to Menu");
        styleButton(backBtn, SIDEBAR_COLOR);
        backPanel.add(backBtn);

        // Profile Content with GridBagLayout
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2), 
            p.getName() + "'s Profile", 
            0, 0, HEADER_FONT, ACCENT_COLOR));

        // Header with name and role
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel nameLabel = new JLabel(p.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel("Role: " + p.getRole());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        roleLabel.setForeground(Color.WHITE);
        
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(ACCENT_COLOR);
        namePanel.add(nameLabel, BorderLayout.NORTH);
        namePanel.add(roleLabel, BorderLayout.SOUTH);
        headerPanel.add(namePanel, BorderLayout.CENTER);
        
        profilePanel.add(headerPanel);
        profilePanel.add(Box.createVerticalStrut(15));

        // Personal Information Section
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        infoPanel.add(createInfoRow("Name:", p.getName()));
        infoPanel.add(createInfoRow("Age:", String.valueOf(p.getAge())));
        infoPanel.add(createInfoRow("National ID:", p.getNationalID()));
        infoPanel.add(createInfoRow("Phone:", String.valueOf(p.getPhonenumber())));
        infoPanel.add(createInfoRow("Email:", p.getEmail()));
        infoPanel.add(createInfoRow("Dependants:", p.getDependants()));
        infoPanel.add(createInfoRow("Address:", p.getAddress()));

        profilePanel.add(infoPanel);
        profilePanel.add(Box.createVerticalStrut(15));

        // Resident-specific information
        if (p instanceof Resident) {
            Resident resident = (Resident) p;
            JPanel residentPanel = new JPanel();
            residentPanel.setLayout(new BoxLayout(residentPanel, BoxLayout.Y_AXIS));
            residentPanel.setBackground(new Color(236, 240, 241));
            residentPanel.setBorder(BorderFactory.createTitledBorder("Residence Information"));
            residentPanel.add(createInfoRow("Apartment #:", String.valueOf(resident.getApartmentNumber2())));
            residentPanel.add(createInfoRow("Building #:", String.valueOf(resident.getbuildingNumber())));
            profilePanel.add(residentPanel);
        }
        
        // Staff-specific information
        if (p instanceof Staff) {
            Staff staff = (Staff) p;
            JPanel staffPanel = new JPanel();
            staffPanel.setLayout(new BoxLayout(staffPanel, BoxLayout.Y_AXIS));
            staffPanel.setBackground(new Color(236, 240, 241));
            staffPanel.setBorder(BorderFactory.createTitledBorder("Staff Information"));
            staffPanel.add(createInfoRow("Assigned Reports:", String.valueOf(staff.getAssignedReports().size())));
            profilePanel.add(staffPanel);
        }

        JScrollPane scrollPane = new JScrollPane(profilePanel);
        scrollPane.setBorder(null);

        panel.add(backPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        backBtn.addActionListener(e -> {
            JPanel parent = (JPanel) SwingUtilities.getAncestorOfClass(JPanel.class, panel);
            CardLayout cl = (CardLayout) parent.getLayout();
            cl.show(parent, "MAIN");
        });

        return panel;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(8, 10, 8, 10));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setForeground(SIDEBAR_COLOR);
        labelComp.setPreferredSize(new Dimension(150, 20));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComp.setForeground(new Color(52, 73, 94));

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);

        return row;
    }

    // =========================================================================
    // UI HELPERS
    // =========================================================================

    private void addMenuButton(JPanel sidebar, String text, String cardName, JPanel contentPanel) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(180, 40));
        btn.setBackground(SIDEBAR_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(REGULAR_FONT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(44, 62, 80));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_COLOR);
            }
        });

        btn.addActionListener(e -> ((CardLayout)contentPanel.getLayout()).show(contentPanel, cardName));
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JTextArea createConsoleArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setBackground(new Color(40, 40, 40));
        area.setForeground(new Color(100, 255, 100));
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setMargin(new Insets(10,10,10,10));
        return area;
    }

    private void captureOutput(JTextArea targetArea, Runnable task) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream newOut = new PrintStream(baos);
        System.setOut(newOut);
        try {
            task.run();
        } finally {
            System.out.flush();
            System.setOut(originalOut);
            targetArea.setText(baos.toString());
        }
    }

    // =========================================================================
    // QR CODE GENERATION (INLINE)
    // =========================================================================
    
    private java.awt.image.BufferedImage generateQrCodeImage(String data) {
        if (data == null || data.trim().isEmpty()) {
            throw new IllegalArgumentException("Data cannot be empty");
        }
        
        int gridSize = 21; // 21x21 QR grid
        int pixelSize = 15;  // Each grid cell = 15x15 pixels (output 315x315)
        int size = gridSize * pixelSize;
        
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_RGB
        );
        
        // Create grid pattern
        boolean[][] grid = new boolean[gridSize][gridSize];
        
        // Add position markers (7x7 in three corners)
        addQrPositionMarker(grid, 0, 0);
        addQrPositionMarker(grid, 0, gridSize - 7);
        addQrPositionMarker(grid, gridSize - 7, 0);
        
        // Add timing patterns (alternating lines)
        for (int i = 8; i < gridSize - 8; i++) {
            grid[6][i] = (i % 2) == 0;
            grid[i][6] = (i % 2) == 0;
        }
        
        // Encode data into remaining grid cells
        encodeQrData(grid, data);
        
        // Draw grid to image as black and white pixels
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int color = grid[row][col] ? 0x000000 : 0xFFFFFF;  // Black or White
                
                int x = col * pixelSize;
                int y = row * pixelSize;
                
                // Fill entire pixel block
                for (int px = 0; px < pixelSize; px++) {
                    for (int py = 0; py < pixelSize; py++) {
                        image.setRGB(x + px, y + py, color);
                    }
                }
            }
        }
        
        return image;
    }
    
    private void addQrPositionMarker(boolean[][] grid, int startRow, int startCol) {
        // Black 7x7 border
        for (int i = 0; i < 7; i++) {
            grid[startRow + i][startCol] = true;
            grid[startRow][startCol + i] = true;
            grid[startRow + 6][startCol + i] = true;
            grid[startRow + i][startCol + 6] = true;
        }
        
        // White 5x5 inner area
        for (int i = 1; i < 6; i++) {
            for (int j = 1; j < 6; j++) {
                grid[startRow + i][startCol + j] = false;
            }
        }
        
        // Black 3x3 center
        for (int i = 2; i < 5; i++) {
            for (int j = 2; j < 5; j++) {
                grid[startRow + i][startCol + j] = true;
            }
        }
    }
    
    private void encodeQrData(boolean[][] grid, String data) {
        byte[] bytes = data.getBytes();
        int byteIndex = 0;
        int bitIndex = 0;
        int gridSize = grid.length;
        
        // Fill grid cells (avoid position markers and timing patterns)
        for (int row = gridSize - 1; row >= 0; row--) {
            for (int col = gridSize - 1; col >= 0; col--) {
                // Skip position markers and timing patterns
                if (isQrProtectedCell(col, row, gridSize)) {
                    continue;
                }
                
                boolean bit = false;
                if (byteIndex < bytes.length) {
                    bit = ((bytes[byteIndex] >> (7 - bitIndex)) & 1) == 1;
                    bitIndex++;
                    if (bitIndex == 8) {
                        bitIndex = 0;
                        byteIndex++;
                    }
                }
                
                grid[row][col] = bit;
            }
        }
    }
    
    private boolean isQrProtectedCell(int col, int row, int size) {
        // Top-left position marker area
        if (col < 8 && row < 8) return true;
        
        // Top-right position marker area
        if (col >= size - 8 && row < 8) return true;
        
        // Bottom-left position marker area
        if (col < 8 && row >= size - 8) return true;
        
        // Timing pattern lines
        if (col == 6 && row >= 8 && row < size - 8) return true;
        if (row == 6 && col >= 8 && col < size - 8) return true;
        
        return false;
    }
    
    private void saveQrCodeImage(java.awt.image.BufferedImage image, java.io.File outputFile) throws Exception {
        if (!javax.imageio.ImageIO.write(image, "png", outputFile)) {
            throw new Exception("Failed to write PNG: PNG format not supported");
        }
    }
}
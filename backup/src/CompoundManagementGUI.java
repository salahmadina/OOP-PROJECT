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

public class CompoundManagementGUI extends JFrame {

    // --- Backend Systems ---
    private static SignUp signUpSystem = new SignUp();
    private static BusSchedules busScheduleSystem = new BusSchedules();
    private static QRGateSystem qrGateSystem = new QRGateSystem();
    private static ArrayList<Gate> compoundGates = new ArrayList<>();

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
        // Initialize Backend Data
        initGates();

        // Frame Setup
        setTitle("Compound Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

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
                String type = (String) typeBox.getSelectedItem();
                // Basic validation could go here
                if(type.equals("Resident")) {
                    person r = new Resident(new String(passF.getPassword()), userF.getText(), nameF.getText(),
                            Integer.parseInt(ageF.getText()), nidF.getText(), phoneF.getText(), emailF.getText(),
                            "Resident", depF.getText(), Integer.parseInt(aptF.getText()), Integer.parseInt(buildF.getText()));
                    signUpSystem.getUsers().add(r);
                } else {
                    person s = new Staff(new String(passF.getPassword()), userF.getText(), nameF.getText(),
                            Integer.parseInt(ageF.getText()), nidF.getText(), phoneF.getText(), emailF.getText(),
                            "Staff", depF.getText());
                    signUpSystem.getUsers().add(s);
                }
                JOptionPane.showMessageDialog(this, "Account Created Successfully!");
                cardLayout.show(mainContainer, LOGIN_VIEW);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: Please check all fields. Age and Numbers must be integers.");
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

    // =========================================================================
    // 3. DASHBOARD (RESIDENT)
    // =========================================================================
    private JPanel createDashboard(Resident r) {
        JPanel dashboard = new JPanel(new BorderLayout());

        // --- Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel welcome = new JLabel("Hello, " + r.getName().split(" ")[0]);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(welcome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Content Area (Card Layout for Tabs)
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BG_COLOR);

        // -- Panels --
        contentPanel.add(createTransportPanel(), "TRANSPORT");
        contentPanel.add(createServicePanel(r), "SERVICE");
        contentPanel.add(createReportPanel(r), "REPORT");
        contentPanel.add(createGatePanel(r), "GATE");
        contentPanel.add(createProfilePanel(r), "PROFILE");

        // -- Menu Buttons --
        addMenuButton(sidebar, "Bus & Transport", "TRANSPORT", contentPanel);
        addMenuButton(sidebar, "Request Service", "SERVICE", contentPanel);
        addMenuButton(sidebar, "File Issue Report", "REPORT", contentPanel);
        addMenuButton(sidebar, "Gate Access", "GATE", contentPanel);
        addMenuButton(sidebar, "My Profile", "PROFILE", contentPanel);

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
    // 4. DASHBOARD (STAFF)
    // =========================================================================
    private JPanel createStaffDashboard(Staff s) {
        JPanel dashboard = new JPanel(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(220, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel welcome = new JLabel("Staff: " + s.getName().split(" ")[0]);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(welcome);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BG_COLOR);

        contentPanel.add(createResidentListPanel(), "RESIDENTS");
        contentPanel.add(createStaffReportsPanel(s), "REPORTS");
        contentPanel.add(createGateLogsPanel(), "LOGS");
        contentPanel.add(createProfilePanel(s), "PROFILE");

        addMenuButton(sidebar, "Manage Residents", "RESIDENTS", contentPanel);
        addMenuButton(sidebar, "Assigned Reports", "REPORTS", contentPanel);
        addMenuButton(sidebar, "Gate Entry Logs", "LOGS", contentPanel);
        addMenuButton(sidebar, "My Profile", "PROFILE", contentPanel);

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

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.setBackground(BG_COLOR);
        JTextField destField = new JTextField(15);
        JButton searchBtn = new JButton("Search Destination");
        styleButton(searchBtn, ACCENT_COLOR);

        controls.add(new JLabel("Destination:"));
        controls.add(destField);
        controls.add(searchBtn);

        JTextArea outputArea = createConsoleArea();

        JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookingPanel.setBackground(BG_COLOR);
        JTextField idxField = new JTextField(5);
        JTextField seatField = new JTextField(5);
        JButton bookBtn = new JButton("Book Seat");
        styleButton(bookBtn, new Color(39, 174, 96));

        bookingPanel.add(new JLabel("Bus Index:")); bookingPanel.add(idxField);
        bookingPanel.add(new JLabel("Seats:")); bookingPanel.add(seatField);
        bookingPanel.add(bookBtn);

        panel.add(controls, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        panel.add(bookingPanel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> captureOutput(outputArea, () -> {
            System.out.println("Searching for: " + destField.getText());
            boolean found = busScheduleSystem.searchByDestination(destField.getText());
            if (found) {
                int i = 1;
                for (Bus b : BusSchedules.matchedBuses) {
                    System.out.println(i + ". Bus ID: " + b.getBusId() + " (Price/Km: " + b.getPricePerKm() + ")");
                    i++;
                }
            }
        }));

        bookBtn.addActionListener(e -> captureOutput(outputArea, () -> {
            try {
                int idx = Integer.parseInt(idxField.getText()) - 1;
                int seats = Integer.parseInt(seatField.getText());
                if (idx >= 0 && idx < BusSchedules.matchedBuses.size()) {
                    Bus b = BusSchedules.matchedBuses.get(idx);
                    b.bookSeats(seats);
                } else {
                    System.out.println("Invalid Bus Selection");
                }
            } catch (Exception ex) {
                System.out.println("Error: Check numbers");
            }
        }));

        return panel;
    }

    private JPanel createServicePanel(Resident r) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);

        JPanel card = new JPanel(new GridLayout(4, 1, 10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField typeF = new JTextField();
        JTextField detF = new JTextField();
        JButton subBtn = new JButton("Submit Request");
        styleButton(subBtn, ACCENT_COLOR);

        card.add(new JLabel("Service Type (e.g. Plumbing):"));
        card.add(typeF);
        card.add(new JLabel("Details:"));
        card.add(detF);

        JTextArea log = createConsoleArea();
        log.setPreferredSize(new Dimension(400, 200));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0; gbc.insets=new Insets(10,10,10,10);
        panel.add(card, gbc);

        gbc.gridy=1;
        panel.add(subBtn, gbc);

        gbc.gridy=2;
        panel.add(new JScrollPane(log), gbc);

        subBtn.addActionListener(e -> captureOutput(log, () -> {
            request req = new request(r, typeF.getText(), detF.getText());
            req.ShowTicket();
        }));

        return panel;
    }

    private JPanel createReportPanel(Resident r) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_COLOR);

        JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("File New Issue"));
        JTextField typeF = new JTextField();
        JTextField descF = new JTextField();

        form.add(new JLabel("Issue Type:")); form.add(typeF);
        form.add(new JLabel("Description:")); form.add(descF);

        JButton btn = new JButton("Send Report");
        styleButton(btn, new Color(231, 76, 60));

        JTextArea log = createConsoleArea();
        log.setPreferredSize(new Dimension(400, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=0; gbc.fill=GridBagConstraints.HORIZONTAL;
        panel.add(form, gbc);
        gbc.gridy=1; gbc.insets=new Insets(10,0,10,0);
        panel.add(btn, gbc);
        gbc.gridy=2;
        panel.add(new JScrollPane(log), gbc);

        btn.addActionListener(e -> captureOutput(log, () -> {
            Report report = new Report(r, typeF.getText(), descF.getText());
            System.out.println("Report ID: " + report.getReportId());
            boolean assigned = false;
            for (person p : signUpSystem.getUsers()) {
                if (p instanceof Staff) {
                    ((Staff) p).assignReport(report);
                    System.out.println("Assigned to staff: " + p.getName());
                    assigned = true;
                    break;
                }
            }
            if(!assigned) System.out.println("No staff available.");
        }));

        return panel;
    }

    private JPanel createGatePanel(Resident r) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20,20,20,20));
        panel.setBackground(BG_COLOR);

        JPanel top = new JPanel();
        top.setBackground(BG_COLOR);
        JButton genQR = new JButton("Generate My QR");
        styleButton(genQR, ACCENT_COLOR);

        String[] gates = {"Main Entrance", "Back Entrance"};
        JComboBox<String> gateBox = new JComboBox<>(gates);
        JButton scanBtn = new JButton("Scan at Selected Gate");
        styleButton(scanBtn, new Color(39, 174, 96));

        top.add(genQR);
        top.add(Box.createHorizontalStrut(20));
        top.add(new JLabel("Gate:"));
        top.add(gateBox);
        top.add(scanBtn);

        JTextArea log = createConsoleArea();
        log.setFont(new Font("Monospaced", Font.BOLD, 14));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(log), BorderLayout.CENTER);

        final QRCode[] myQR = {null};

        genQR.addActionListener(e -> captureOutput(log, () -> {
            myQR[0] = qrGateSystem.assignQRCodeToUser(r.getPhonenumber());
            System.out.println("QR Generated: " + myQR[0].getCode());
            System.out.println("(Show this code to the scanner)");
        }));

        scanBtn.addActionListener(e -> captureOutput(log, () -> {
            if(myQR[0] == null) {
                System.out.println("Error: Generate QR first.");
                return;
            }
            int idx = gateBox.getSelectedIndex();
            Gate g = compoundGates.get(idx);
            System.out.println("Scanning at " + g.getLocation() + "...");
            if(g.scanQRCode(myQR[0])) {
                qrGateSystem.logEntry(r.getPhonenumber());
                System.out.println("WELCOME HOME!");
            } else {
                System.out.println("ACCESS DENIED");
            }
        }));

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
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea log = createConsoleArea();

        JPanel ctrl = new JPanel();
        JButton view = new JButton("View My Reports");
        JTextField idxF = new JTextField(3);
        JTextField statusF = new JTextField(10);
        JButton update = new JButton("Update Status");

        ctrl.add(view);
        ctrl.add(new JLabel(" | Update #")); ctrl.add(idxF);
        ctrl.add(new JLabel("Status:")); ctrl.add(statusF);
        ctrl.add(update);

        panel.add(ctrl, BorderLayout.NORTH);
        panel.add(new JScrollPane(log), BorderLayout.CENTER);

        view.addActionListener(e -> captureOutput(log, () -> {
            int i=1;
            for(Report r : s.getAssignedReports()) {
                System.out.println(i + ". [" + r.getStatus() + "] " + r.getType() + " - " + r.getDescription());
                i++;
            }
            if(s.getAssignedReports().isEmpty()) System.out.println("No reports assigned.");
        }));

        update.addActionListener(e -> captureOutput(log, () -> {
            try {
                int idx = Integer.parseInt(idxF.getText()) - 1;
                if(idx >= 0 && idx < s.getAssignedReports().size()) {
                    Report r = s.getAssignedReports().get(idx);
                    s.updateReportStatus(r, statusF.getText());
                    System.out.println("Status updated.");
                }
            } catch(Exception ex) { System.out.println("Invalid input"); }
        }));

        return panel;
    }

    private JPanel createGateLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea log = createConsoleArea();
        JButton refresh = new JButton("Refresh Logs");

        panel.add(refresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(log), BorderLayout.CENTER);

        refresh.addActionListener(e -> captureOutput(log, () -> qrGateSystem.showLogs()));
        return panel;
    }

    private JPanel createProfilePanel(person p) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20,20,20,20));
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setText(p.toString().replace(",", "\n"));
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
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
}

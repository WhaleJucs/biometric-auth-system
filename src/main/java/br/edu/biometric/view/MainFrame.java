package br.edu.biometric.view;

import br.edu.biometric.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Janela principal do sistema com menu e painéis
 */
public class MainFrame extends JFrame {

    private AuthenticationService authService;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Painéis
    private LoginPanel loginPanel;
    private UserManagementPanel userManagementPanel;
    private DashboardPanel dashboardPanel;
    private LogsPanel logsPanel;

    public MainFrame() {
        this.authService = new AuthenticationService();
        initializeFrame();
        createMenuBar();
        createMainPanel();
        showLoginPanel();
    }

    private void initializeFrame() {
        setTitle("Sistema de Autenticação Biométrica - Ministério do Meio Ambiente");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Sistema
        JMenu systemMenu = new JMenu("Sistema");
        
        JMenuItem loginItem = new JMenuItem("Autenticação");
        loginItem.addActionListener(e -> showLoginPanel());
        systemMenu.add(loginItem);

        JMenuItem exitItem = new JMenuItem("Sair");
        exitItem.addActionListener(e -> System.exit(0));
        systemMenu.add(exitItem);

        // Menu Usuários
        JMenu userMenu = new JMenu("Usuários");
        
        JMenuItem manageUsersItem = new JMenuItem("Gerenciar Usuários");
        manageUsersItem.addActionListener(e -> showUserManagementPanel());
        userMenu.add(manageUsersItem);

        // Menu Relatórios
        JMenu reportsMenu = new JMenu("Relatórios");
        
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        dashboardItem.addActionListener(e -> showDashboardPanel());
        reportsMenu.add(dashboardItem);

        JMenuItem logsItem = new JMenuItem("Logs de Acesso");
        logsItem.addActionListener(e -> showLogsPanel());
        reportsMenu.add(logsItem);

        menuBar.add(systemMenu);
        menuBar.add(userMenu);
        menuBar.add(reportsMenu);

        setJMenuBar(menuBar);
    }

    private void createMainPanel() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Cria os painéis
        loginPanel = new LoginPanel(authService, this);
        userManagementPanel = new UserManagementPanel(authService, this);
        dashboardPanel = new DashboardPanel(authService);
        logsPanel = new LogsPanel(authService);

        // Adiciona os painéis ao CardLayout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(userManagementPanel, "USER_MANAGEMENT");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(logsPanel, "LOGS");

        add(mainPanel, BorderLayout.CENTER);
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
        loginPanel.refresh();
    }

    public void showUserManagementPanel() {
        cardLayout.show(mainPanel, "USER_MANAGEMENT");
        userManagementPanel.refresh();
    }

    public void showDashboardPanel() {
        cardLayout.show(mainPanel, "DASHBOARD");
        dashboardPanel.refresh();
    }

    public void showLogsPanel() {
        cardLayout.show(mainPanel, "LOGS");
        logsPanel.refresh();
    }

    public AuthenticationService getAuthService() {
        return authService;
    }
}


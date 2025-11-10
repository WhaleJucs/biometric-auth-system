package br.edu.biometric.view;

import br.edu.biometric.model.AccessStatus;
import br.edu.biometric.service.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Painel de dashboard com estatísticas do sistema
 */
public class DashboardPanel extends JPanel {

    private AuthenticationService authService;
    
    private JLabel totalUsersLabel;
    private JLabel activeUsersLabel;
    private JLabel totalLogsLabel;
    private JLabel successfulLogsLabel;
    private JLabel failedLogsLabel;
    private JLabel modelTrainedLabel;

    public DashboardPanel(AuthenticationService authService) {
        this.authService = authService;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titleLabel = new JLabel("Dashboard - Estatísticas do Sistema", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Painel de estatísticas
        JPanel statsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Usuários
        gbc.gridx = 0;
        gbc.gridy = 0;
        statsPanel.add(createStatCard("Total de Usuários", totalUsersLabel = new JLabel("0")), gbc);
        
        gbc.gridx = 1;
        statsPanel.add(createStatCard("Usuários Ativos", activeUsersLabel = new JLabel("0")), gbc);

        // Logs
        gbc.gridx = 0;
        gbc.gridy = 1;
        statsPanel.add(createStatCard("Total de Acessos", totalLogsLabel = new JLabel("0")), gbc);
        
        gbc.gridx = 1;
        statsPanel.add(createStatCard("Acessos Bem-sucedidos", successfulLogsLabel = new JLabel("0")), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        statsPanel.add(createStatCard("Acessos Negados", failedLogsLabel = new JLabel("0")), gbc);

        // Status do modelo
        gbc.gridx = 1;
        gbc.gridy = 2;
        statsPanel.add(createStatCard("Status do Modelo", modelTrainedLabel = new JLabel("Não treinado")), gbc);

        add(statsPanel, BorderLayout.CENTER);

        // Botão atualizar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Atualizar Estatísticas");
        refreshButton.addActionListener(e -> refresh());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        card.setPreferredSize(new Dimension(250, 100));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);

        valueLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        valueLabel.setForeground(new Color(0, 100, 200));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void refresh() {
        // Atualiza estatísticas de usuários
        long totalUsers = authService.getUserRepository().count();
        long activeUsers = authService.getUserRepository().countActive();
        totalUsersLabel.setText(String.valueOf(totalUsers));
        activeUsersLabel.setText(String.valueOf(activeUsers));

        // Atualiza estatísticas de logs
        long totalLogs = authService.getLogRepository().count();
        long successfulLogs = authService.getLogRepository().countSuccessful();
        long failedLogs = authService.getLogRepository().countFailed();
        totalLogsLabel.setText(String.valueOf(totalLogs));
        successfulLogsLabel.setText(String.valueOf(successfulLogs));
        failedLogsLabel.setText(String.valueOf(failedLogs));

        // Status do modelo
        if (authService.isModelTrained()) {
            modelTrainedLabel.setText("Treinado");
            modelTrainedLabel.setForeground(new Color(0, 150, 0));
        } else {
            modelTrainedLabel.setText("Não treinado");
            modelTrainedLabel.setForeground(Color.RED);
        }
    }
}


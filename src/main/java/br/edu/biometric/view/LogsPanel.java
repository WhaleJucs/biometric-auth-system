package br.edu.biometric.view;

import br.edu.biometric.model.AccessLog;
import br.edu.biometric.model.AccessStatus;
import br.edu.biometric.service.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para visualização de logs de acesso
 */
public class LogsPanel extends JPanel {

    private AuthenticationService authService;
    
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private JComboBox<AccessStatus> filterComboBox;
    private JButton refreshButton;
    private JButton clearButton;

    public LogsPanel(AuthenticationService authService) {
        this.authService = authService;
        initializePanel();
        refresh(); // Carrega os logs ao inicializar
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        
        List<AccessLog> logs;
        AccessStatus selectedStatus = (AccessStatus) filterComboBox.getSelectedItem();
        
        if (selectedStatus != null) {
            logs = authService.getLogRepository().findByStatus(selectedStatus);
        } else {
            logs = authService.getLogRepository().findAll();
        }

        for (AccessLog log : logs) {
            tableModel.addRow(new Object[]{
                    log.getFormattedTimestamp(),
                    log.getUserName(),
                    log.getAccessLevel() != null ? log.getAccessLevel().getDisplayName() : "N/A",
                    log.getStatus().getDisplayName(),
                    String.format("%.2f", log.getConfidenceScore()),
                    log.getDetails()
            });
        }
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        JLabel titleLabel = new JLabel("Logs de Acesso ao Sistema", JLabel.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Painel de filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrar por Status:"));
        
        filterComboBox = new JComboBox<>();
        filterComboBox.addItem(null); // Todos
        for (AccessStatus status : AccessStatus.values()) {
            filterComboBox.addItem(status);
        }
        filterComboBox.addActionListener(e -> refresh());
        
        refreshButton = new JButton("Atualizar");
        refreshButton.addActionListener(e -> refresh());
        
        clearButton = new JButton("Limpar Logs");
        clearButton.addActionListener(e -> clearLogs());
        clearButton.setForeground(Color.RED);
        
        filterPanel.add(filterComboBox);
        filterPanel.add(refreshButton);
        filterPanel.add(clearButton);

        add(filterPanel, BorderLayout.NORTH);

        // Tabela de logs
        String[] columnNames = {"Data/Hora", "Usuário", "Nível de Acesso", "Status", "Confiança (%)", "Detalhes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logsTable = new JTable(tableModel);
        logsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        logsTable.setRowHeight(25);
        
        // Renderizador de cores para status
        logsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusCellRenderer());

        JScrollPane scrollPane = new JScrollPane(logsTable);
        scrollPane.setPreferredSize(new Dimension(900, 500));
        add(scrollPane, BorderLayout.CENTER);

        // Painel de informações
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel infoLabel = new JLabel("Total de registros: 0");
        infoPanel.add(infoLabel);
        add(infoPanel, BorderLayout.SOUTH);
    }


    private void clearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja limpar todos os logs? Esta ação não pode ser desfeita.",
                "Confirmar Limpeza",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.getLogRepository().clear();
            refresh();
            JOptionPane.showMessageDialog(this,
                    "Logs limpos com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Renderizador de células para colorir status
     */
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                String status = value.toString();
                if (status.contains("Concedido")) {
                    c.setBackground(new Color(200, 255, 200));
                } else if (status.contains("Negado")) {
                    c.setBackground(new Color(255, 200, 200));
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            
            return c;
        }
    }
}


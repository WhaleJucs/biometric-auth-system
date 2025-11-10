package br.edu.biometric.view;

import br.edu.biometric.model.AccessLevel;
import br.edu.biometric.service.AuthenticationService;
import br.edu.biometric.service.AuthenticationResult;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Painel de autenticação biométrica
 */
public class LoginPanel extends JPanel {

    private AuthenticationService authService;
    private MainFrame mainFrame;
    
    private JComboBox<AccessLevel> levelComboBox;
    private JLabel imageLabel;
    private JLabel statusLabel;
    private JButton selectImageButton;
    private JButton authenticateButton;
    private String selectedImagePath;

    public LoginPanel(AuthenticationService authService, MainFrame mainFrame) {
        this.authService = authService;
        this.mainFrame = mainFrame;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel superior - Seleção de nível e imagem
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Nível de acesso
        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Nível de Acesso:"), gbc);

        gbc.gridx = 1;
        levelComboBox = new JComboBox<>(AccessLevel.values());
        levelComboBox.setSelectedIndex(0);
        topPanel.add(levelComboBox, gbc);

        // Botão selecionar imagem
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        selectImageButton = new JButton("Selecionar Imagem");
        selectImageButton.addActionListener(e -> selectImage());
        topPanel.add(selectImageButton, gbc);

        // Label para exibir imagem
        gbc.gridy = 2;
        imageLabel = new JLabel("Nenhuma imagem selecionada", JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 300));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        topPanel.add(imageLabel, gbc);

        // Botão autenticar
        gbc.gridy = 3;
        authenticateButton = new JButton("Autenticar");
        authenticateButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        authenticateButton.setPreferredSize(new Dimension(200, 40));
        authenticateButton.addActionListener(e -> authenticate());
        authenticateButton.setEnabled(false);
        topPanel.add(authenticateButton, gbc);

        add(topPanel, BorderLayout.CENTER);

        // Painel inferior - Status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Selecione uma imagem para autenticação", JLabel.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Imagens (JPG, PNG, BMP)", "jpg", "jpeg", "png", "bmp"));
        fileChooser.setDialogTitle("Selecionar Imagem para Autenticação");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            // Exibe preview da imagem
            ImageIcon icon = new ImageIcon(selectedImagePath);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImg));
            imageLabel.setText("");

            authenticateButton.setEnabled(true);
            statusLabel.setText("Imagem selecionada. Clique em 'Autenticar' para continuar.");
        }
    }

    private void authenticate() {
        if (selectedImagePath == null || selectedImagePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Por favor, selecione uma imagem primeiro.", 
                    "Aviso", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AccessLevel requiredLevel = (AccessLevel) levelComboBox.getSelectedItem();
        
        // Desabilita botões durante autenticação
        authenticateButton.setEnabled(false);
        selectImageButton.setEnabled(false);
        statusLabel.setText("Processando autenticação...");

        // Executa autenticação em thread separada para não travar a UI
        SwingUtilities.invokeLater(() -> {
            try {
                AuthenticationResult result = authService.authenticate(selectedImagePath, requiredLevel);
                
                // Atualiza UI com resultado
                if (result.isSuccess()) {
                    statusLabel.setText(result.getMessage());
                    statusLabel.setForeground(new Color(0, 150, 0));
                    
                    JOptionPane.showMessageDialog(this,
                            String.format("Autenticação bem-sucedida!\n\n" +
                                    "Usuário: %s\n" +
                                    "Nível: %s\n" +
                                    "Confiança: %.2f%%",
                                    result.getUser().getName(),
                                    result.getUser().getAccessLevel().getDisplayName(),
                                    result.getConfidence()),
                            "Acesso Concedido",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    statusLabel.setText(result.getMessage());
                    statusLabel.setForeground(Color.RED);
                    
                    JOptionPane.showMessageDialog(this,
                            result.getMessage(),
                            "Acesso Negado",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                statusLabel.setText("Erro durante autenticação: " + e.getMessage());
                statusLabel.setForeground(Color.RED);
                JOptionPane.showMessageDialog(this,
                        "Erro durante autenticação: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                authenticateButton.setEnabled(true);
                selectImageButton.setEnabled(true);
            }
        });
    }

    public void refresh() {
        selectedImagePath = null;
        imageLabel.setIcon(null);
        imageLabel.setText("Nenhuma imagem selecionada");
        authenticateButton.setEnabled(false);
        statusLabel.setText("Selecione uma imagem para autenticação");
        statusLabel.setForeground(Color.BLACK);
        levelComboBox.setSelectedIndex(0);
    }
}


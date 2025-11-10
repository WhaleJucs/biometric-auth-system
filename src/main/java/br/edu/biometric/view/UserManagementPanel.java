package br.edu.biometric.view;

import br.edu.biometric.model.AccessLevel;
import br.edu.biometric.model.User;
import br.edu.biometric.service.AuthenticationService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Painel para gerenciamento de usuários
 */
public class UserManagementPanel extends JPanel {

    private AuthenticationService authService;
    private MainFrame mainFrame;
    
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField cpfField;
    private JTextField emailField;
    private JComboBox<AccessLevel> accessLevelComboBox;
    private JList<String> biometricImagesList;
    private DefaultListModel<String> imagesListModel;
    private java.util.List<String> biometricImagePaths; // Armazena caminhos completos
    private JButton addImageButton;
    private JButton removeImageButton;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newButton;
    
    private User currentUser;

    public UserManagementPanel(AuthenticationService authService, MainFrame mainFrame) {
        this.authService = authService;
        this.mainFrame = mainFrame;
        this.biometricImagePaths = new ArrayList<>();
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel esquerdo - Lista de usuários
        JPanel leftPanel = createUserListPanel();
        
        // Painel direito - Formulário de edição
        JPanel rightPanel = createUserFormPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createUserListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabela de usuários
        String[] columnNames = {"Nome", "CPF", "Email", "Nível", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow >= 0) {
                    loadUserFromTable(selectedRow);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botão novo usuário
        JPanel buttonPanel = new JPanel(new FlowLayout());
        newButton = new JButton("Novo Usuário");
        newButton.addActionListener(e -> newUser());
        buttonPanel.add(newButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUserFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // CPF
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cpfField = new JTextField(20);
        formPanel.add(cpfField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Nível de acesso
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nível de Acesso:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        accessLevelComboBox = new JComboBox<>(AccessLevel.values());
        formPanel.add(accessLevelComboBox, gbc);

        // Imagens biométricas
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Imagens Biométricas:"), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        imagesListModel = new DefaultListModel<>();
        biometricImagesList = new JList<>(imagesListModel);
        JScrollPane imagesScroll = new JScrollPane(biometricImagesList);
        imagesScroll.setPreferredSize(new Dimension(300, 150));
        formPanel.add(imagesScroll, gbc);

        // Botões de imagem
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        JPanel imageButtonsPanel = new JPanel(new FlowLayout());
        addImageButton = new JButton("Adicionar Imagem");
        addImageButton.addActionListener(e -> addBiometricImage());
        removeImageButton = new JButton("Remover Selecionada");
        removeImageButton.addActionListener(e -> removeSelectedImage());
        imageButtonsPanel.add(addImageButton);
        imageButtonsPanel.add(removeImageButton);
        formPanel.add(imageButtonsPanel, gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Botões de ação
        JPanel actionPanel = new JPanel(new FlowLayout());
        saveButton = new JButton("Salvar");
        saveButton.addActionListener(e -> saveUser());
        deleteButton = new JButton("Excluir");
        deleteButton.addActionListener(e -> deleteUser());
        actionPanel.add(saveButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadUserFromTable(int row) {
        String name = (String) tableModel.getValueAt(row, 0);
        List<User> users = authService.getUserRepository().findAll();
        currentUser = users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            cpfField.setText(currentUser.getCpf());
            emailField.setText(currentUser.getEmail());
            accessLevelComboBox.setSelectedItem(currentUser.getAccessLevel());
            
            imagesListModel.clear();
            biometricImagePaths.clear();
            for (String path : currentUser.getBiometricDataPaths()) {
                imagesListModel.addElement(new File(path).getName());
                biometricImagePaths.add(path);
            }
        }
    }

    private void newUser() {
        currentUser = null;
        nameField.setText("");
        cpfField.setText("");
        emailField.setText("");
        accessLevelComboBox.setSelectedIndex(0);
        imagesListModel.clear();
        biometricImagePaths.clear();
        userTable.clearSelection();
    }

    private void addBiometricImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                "Imagens (JPG, PNG, BMP)", "jpg", "jpeg", "png", "bmp"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setDialogTitle("Selecionar Imagens Biométricas");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                String path = file.getAbsolutePath();
                if (!biometricImagePaths.contains(path)) {
                    imagesListModel.addElement(file.getName());
                    biometricImagePaths.add(path);
                }
            }
        }
    }

    private void removeSelectedImage() {
        int selectedIndex = biometricImagesList.getSelectedIndex();
        if (selectedIndex >= 0) {
            imagesListModel.remove(selectedIndex);
            biometricImagePaths.remove(selectedIndex);
        }
    }

    private void saveUser() {
        if (nameField.getText().trim().isEmpty() ||
            cpfField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha todos os campos obrigatórios.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (imagesListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, adicione pelo menos uma imagem biométrica.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (currentUser == null) {
                // Novo usuário
                currentUser = new User(
                        nameField.getText().trim(),
                        cpfField.getText().trim(),
                        emailField.getText().trim(),
                        (AccessLevel) accessLevelComboBox.getSelectedItem()
                );
            } else {
                // Atualizar usuário existente
                currentUser.setName(nameField.getText().trim());
                currentUser.setCpf(cpfField.getText().trim());
                currentUser.setEmail(emailField.getText().trim());
                currentUser.setAccessLevel((AccessLevel) accessLevelComboBox.getSelectedItem());
            }

            // Adiciona imagens biométricas
            currentUser.getBiometricDataPaths().clear();
            for (String path : biometricImagePaths) {
                currentUser.addBiometricData(path);
            }

            // Salva usuário
            authService.getUserRepository().save(currentUser);
            
            // Retreina o modelo
            authService.trainModel();

            JOptionPane.showMessageDialog(this,
                    "Usuário salvo com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            refresh();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar usuário: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteUser() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um usuário para excluir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o usuário " + currentUser.getName() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            authService.getUserRepository().delete(currentUser.getId());
            authService.trainModel();
            JOptionPane.showMessageDialog(this,
                    "Usuário excluído com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }

    public void refresh() {
        // Atualiza tabela
        tableModel.setRowCount(0);
        List<User> users = authService.getUserRepository().findAll();
        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getName(),
                    user.getCpf(),
                    user.getEmail(),
                    user.getAccessLevel().getDisplayName(),
                    user.isActive() ? "Ativo" : "Inativo"
            });
        }
        
        // Limpa formulário
        newUser();
    }
}


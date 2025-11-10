package br.edu.biometric;

import br.edu.biometric.view.MainFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Classe principal do sistema de autenticação biométrica
 */
public class Main {

    public static void main(String[] args) {
        // Configura o Look and Feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }

        // Executa a aplicação na Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                
                // Exibe mensagem de boas-vindas
                JOptionPane.showMessageDialog(frame,
                        "Bem-vindo ao Sistema de Autenticação Biométrica!\n\n" +
                        "Este sistema permite:\n" +
                        "- Autenticação por reconhecimento facial\n" +
                        "- Gerenciamento de usuários\n" +
                        "- Controle de acesso por níveis\n" +
                        "- Visualização de logs e estatísticas",
                        "Bem-vindo",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar aplicação: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}


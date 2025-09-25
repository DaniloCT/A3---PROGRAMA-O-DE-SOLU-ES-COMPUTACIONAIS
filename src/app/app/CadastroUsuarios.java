package app.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CadastroUsuarios extends JFrame {
    private JTextField txtNome, txtCpf, txtEmail, txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<String> comboCargo;
    private JTextArea areaUsuarios;
    private List<Usuario> usuarios = new ArrayList<>();

    public CadastroUsuarios() {
        setTitle("Cadastro de Usuários");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de formulário
        JPanel panelForm = new JPanel(new GridLayout(7, 2, 5, 5));

        panelForm.add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        panelForm.add(txtNome);

        panelForm.add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        panelForm.add(txtCpf);

        panelForm.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        panelForm.add(txtEmail);

        panelForm.add(new JLabel("Cargo:"));
        comboCargo = new JComboBox<>(new String[]{"Administrador", "Gerente", "Colaborador"});
        panelForm.add(comboCargo);

        panelForm.add(new JLabel("Login:"));
        txtLogin = new JTextField();
        panelForm.add(txtLogin);

        panelForm.add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        panelForm.add(txtSenha);

        JButton btnCadastrar = new JButton("Cadastrar");
        panelForm.add(btnCadastrar);

        JButton btnListar = new JButton("Listar Usuários");
        panelForm.add(btnListar);

        add(panelForm, BorderLayout.NORTH);

        // Área para exibir usuários
        areaUsuarios = new JTextArea();
        areaUsuarios.setEditable(false);
        add(new JScrollPane(areaUsuarios), BorderLayout.CENTER);

        // Ações
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });

        btnListar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listarUsuarios();
            }
        });
    }

    private void cadastrarUsuario() {
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        String cargo = comboCargo.getSelectedItem().toString();
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());
        PerfilUsuario perfil;
        switch (cargo) {
            case "Administrador": perfil = PerfilUsuario.ADMINISTRADOR; break;
            case "Gerente": perfil = PerfilUsuario.GERENTE; break;
            default: perfil = PerfilUsuario.COLABORADOR;
        }
        if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Usuario novoUsuario = new Usuario(nome, cpf, email, cargo, login, senha, perfil);
        usuarios.add(novoUsuario);
        JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
        limparCampos();
    }

    private void listarUsuarios() {
        areaUsuarios.setText("Usuários cadastrados:\n");
        for (Usuario u : usuarios) {
            areaUsuarios.append(u.toString() + "\n");
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtCpf.setText("");
        txtEmail.setText("");
        txtLogin.setText("");
        txtSenha.setText("");
        comboCargo.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CadastroUsuarios().setVisible(true);
        });
    }
}

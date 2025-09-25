package app.app;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class AppProjetos {
    private JFrame frame;
    private JTextField usuarioField;
    private JPasswordField senhaField;
    private JButton loginButton, sairButton;
    private Usuario usuarioLogado;
    private final List<String> grupos = Arrays.asList("Administrador", "Gerente", "Usuario");
    private JPanel mainPanel;
    private JPanel areaPrincipal;

    // Esquema de cores moderno
    private final Color corPrimaria = new Color(25, 118, 210);
    private final Color corSecundaria = new Color(21, 101, 192);
    private final Color corFundo = new Color(247, 249, 252);
    private final Color corCard1 = new Color(76, 175, 80);
    private final Color corCard2 = new Color(255, 152, 0);
    private final Color corCard3 = new Color(103, 58, 183);
    private final Color corCard4 = new Color(233, 30, 99);

    // Constantes para textos sem acentos
    private static final String TEXTO_BEM_VINDO = "Bem vindo ao Sistema";
    private static final String TEXTO_USUARIO = "Usuario";
    private static final String TEXTO_SENHA = "Senha";
    private static final String TEXTO_ENTRAR = "Entrar";
    private static final String TEXTO_CADASTRAR = "Cadastrar Usuario";
    private static final String TEXTO_SAIR = "Sair";

    // Constantes para o calendario
    private static final String[] MESES = {
        "Janeiro", "Fevereiro", "Marco", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };
    private static final String[] DIAS_SEMANA = {"Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"};
    private int mesAtual = Calendar.getInstance().get(Calendar.MONTH);
    private int anoAtual = Calendar.getInstance().get(Calendar.YEAR);

    // Formatador de data padrão
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    public AppProjetos() {
        try {
            inicializarAplicacao();
        } catch (Exception e) {
            tratarErroFatal(e, "Erro ao inicializar o sistema");
        }
    }

    private void inicializarAplicacao() throws Exception {
        configurarLookAndFeel();
        configurarFontePadrao();
        DatabaseManager.inicializarBancoDados();
        criarJanelaPrincipal();
    }

    private void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Erro ao configurar Look and Feel: " + e.getMessage());
        }
    }

    private void configurarFontePadrao() {
        Font defaultFont = new Font("Segoe UI", Font.PLAIN, 12);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("ComboBox.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
    }

    private void criarJanelaPrincipal() {
        frame = new JFrame("Sistema de Projetos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 350);
        frame.setLocationRelativeTo(null);
        criarTelaLogin();
        frame.setVisible(true);
    }

    private void tratarErroFatal(Exception e, String mensagem) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            mensagem + ": " + e.getMessage(),
            "Erro Fatal",
            JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
    
    /**
     * Verifica as permissões do usuário para acessar funcionalidades específicas.
     * @param funcionalidade Nome da funcionalidade a ser verificada
     * @return true se o usuário tem permissão, false caso contrário
     */
    private boolean temPermissao(String funcionalidade) {
        if (usuarioLogado == null) return false;
        
        try {
            boolean isAdminPerfil = usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR;
            boolean isGerentePerfil = usuarioLogado.getPerfil() == PerfilUsuario.GERENTE;
            boolean isAdminGrupo = usuarioLogado.getGrupo() != null && 
                                 usuarioLogado.getGrupo().equalsIgnoreCase("Administrativo");
            
            switch (funcionalidade.toLowerCase()) {
                case "dashboard":
                    return true; // Todos têm acesso
                case "projetos":
                    return true; // Todos têm acesso
                case "equipes":
                    return isAdminPerfil || isGerentePerfil || usuarioLogado.getPerfil() == PerfilUsuario.COLABORADOR;
                case "calendario":
                    return true; // Todos têm acesso
                case "config":
                    return isAdminPerfil || isAdminGrupo;
                case "usuarios":
                    return isAdminPerfil || isAdminGrupo;
                default:
                    return false;
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar permissões: " + e.getMessage());
            return false;
        }
    }

    private void criarTelaLogin() {
        try {
            frame.setExtendedState(JFrame.NORMAL);
            frame.setSize(600, 350);
            final JPanel loginPanel = new JPanel(new GridBagLayout());
            loginPanel.setBackground(corFundo);
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            final JLabel titulo = new JLabel(TEXTO_BEM_VINDO);
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
            titulo.setForeground(new Color(44, 62, 80));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            loginPanel.add(titulo, gbc);

            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0; gbc.gridy = 1;
            final JLabel lblUsuario = new JLabel(TEXTO_USUARIO + ":");
            lblUsuario.setForeground(new Color(44, 62, 80));
            loginPanel.add(lblUsuario, gbc);
            
            gbc.gridx = 1;
            usuarioField = new JTextField(20);
            usuarioField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            loginPanel.add(usuarioField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            final JLabel lblSenha = new JLabel(TEXTO_SENHA + ":");
            lblSenha.setForeground(new Color(44, 62, 80));
            loginPanel.add(lblSenha, gbc);
            
            gbc.gridx = 1;
            senhaField = new JPasswordField(20);
            senhaField.setFont(new Font("Segue UI", Font.PLAIN, 14));
            loginPanel.add(senhaField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            final JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            btnPanel.setBackground(corFundo);
            btnPanel.setOpaque(true);

            loginButton = new JButton(TEXTO_ENTRAR);
            sairButton = new JButton(TEXTO_SAIR);

            estilizarBotaoLogin(loginButton, new Color(52, 152, 219));
            estilizarBotaoLogin(sairButton, new Color(231, 76, 60));

            btnPanel.add(loginButton);
            btnPanel.add(sairButton);
            loginPanel.add(btnPanel, gbc);

            // Event Handlers
            loginButton.addActionListener(e -> validarLogin());
            sairButton.addActionListener(e -> System.exit(0));

            // Enter key handlers
            usuarioField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        senhaField.requestFocus();
                    }
                }
            });

            senhaField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        validarLogin();
                    }
                }
            });

            frame.setContentPane(loginPanel);
            frame.revalidate();
            frame.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Erro ao criar tela de login: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para criar e configurar botão de menu
    private JButton criarBotaoMenu(final String texto, final String tooltip) {
        final JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(corPrimaria);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setToolTipText(tooltip);
        btn.setOpaque(true);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(corSecundaria);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(corPrimaria);
            }
        });

        return btn;
    }

    // Método para estilizar botão login
    private void estilizarBotaoLogin(final JButton botao, final Color cor) {
        botao.setPreferredSize(new Dimension(160, 40));
        botao.setBackground(cor);
        botao.setOpaque(true);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setContentAreaFilled(true);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.updateUI();

        // Efeito hover
        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(cor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(cor);
            }
        });
    }

    // Método para estilizar botão moderno
    private void estilizarBotaoModerno(final JButton botao, final Color cor) {
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));

        botao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                botao.setBackground(cor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                botao.setBackground(cor);
            }
        });
    }

    // Método para criar o menu lateral
    private JPanel criarMenuLateral() {
        try {
            final JPanel menuPanel = new JPanel(new BorderLayout());
            menuPanel.setPreferredSize(new Dimension(220, 0));
            menuPanel.setBackground(corPrimaria);

            final JLabel logoLabel = new JLabel("Sistema de Projetos");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            menuPanel.add(logoLabel, BorderLayout.NORTH);

            final JPanel menuItems = new JPanel();
            menuItems.setLayout(new BoxLayout(menuItems, BoxLayout.Y_AXIS));
            menuItems.setOpaque(false);
            menuItems.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            final Object[][] menuOpcoes = {
                {"Dashboard", "dashboard", "Visão geral"},
                {"Projetos", "projetos", "Gerenciar projetos"},
                {"Equipes", "equipes", "Gerenciar equipes"},
                {"Usuários", "usuarios", "Gerenciar usuários"},
                {"Configurações", "config", "Configurações do sistema"}
            };

            for (final Object[] opcao : menuOpcoes) {
                final String texto = (String)opcao[0];
                final String acao = (String)opcao[1];
                final String tooltip = (String)opcao[2];

                if (temPermissao(acao)) {
                    final JButton menuBtn = criarBotaoMenu(texto, tooltip);
                    menuBtn.setActionCommand(acao);
                    menuBtn.addActionListener(e -> navegarPara(acao));
                    menuItems.add(menuBtn);
                    menuItems.add(Box.createVerticalStrut(5));
                }
            }

            final JScrollPane scrollMenu = new JScrollPane(menuItems);
            scrollMenu.setBorder(null);
            scrollMenu.setOpaque(false);
            scrollMenu.getViewport().setOpaque(false);
            menuPanel.add(scrollMenu, BorderLayout.CENTER);

            return menuPanel;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Erro ao criar menu lateral: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return new JPanel(); // Retorna um painel vazio em caso de erro
        }
    }

    private void abrirCadastroUsuario() {
        JDialog dialog = new JDialog(frame, "Cadastro de Usuario", true);
        dialog.setSize(500, 350);
        dialog.setLocationRelativeTo(frame);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Cadastro de Usuario");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(44, 62, 80));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        JTextField login = new JTextField(15);
        login.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(login, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        JPasswordField senha = new JPasswordField(15);
        senha.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(senha, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1;
        JTextField cpfField = new JTextField(15);
        cpfField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(cpfField, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Grupo:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> grupoCombo = new JComboBox<>(grupos.toArray(new String[0]));
        grupoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(grupoCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(corFundo); // Define a cor de fundo do painel
        btnPanel.setOpaque(true); // Garante que o painel seja opaco
        JButton cadastrar = new JButton("Cadastrar");
        JButton voltar = new JButton("Voltar");
        cadastrar.setForeground(new Color(40, 40, 40));
        voltar.setForeground(new Color(40, 40, 40));
        estilizarBotao(cadastrar, new Color(46, 204, 113));
        estilizarBotao(voltar, new Color(52, 152, 219));
        btnPanel.add(cadastrar);
        btnPanel.add(voltar);
        panel.add(btnPanel, gbc);

        cadastrar.addActionListener(e -> {
            if (login.getText().isEmpty() || new String(senha.getPassword()).isEmpty() || emailField.getText().isEmpty() || cpfField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String grupoSelecionado = (String) grupoCombo.getSelectedItem();
            Usuario novoUsuario = new Usuario(
                login.getText(),
                cpfField.getText(),
                emailField.getText(),
                grupoSelecionado,
                login.getText(),
                new String(senha.getPassword()),
                PerfilUsuario.COLABORADOR
            );
            if (DatabaseManager.inserirUsuario(novoUsuario)) {
                JOptionPane.showMessageDialog(dialog, "Usuario cadastrado com sucesso!");
                dialog.dispose();
                // Atualiza apenas o painel central mantendo o menu lateral
                navegarPara("usuarios");
            } else {
                JOptionPane.showMessageDialog(dialog, "Login ja existe!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        voltar.addActionListener(e -> dialog.dispose());
        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    /**
     * Valida as credenciais do usuário e exibe a tela principal se autenticado.
     */
    private void validarLogin() {
        try {
            String user = usuarioField.getText().trim();
            String pass = new String(senhaField.getPassword());
            
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Preencha todos os campos.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseManager.autenticarUsuario(user, pass)) {
                usuarioLogado = DatabaseManager.buscarUsuario(user);
                if (usuarioLogado != null) {
                    mostrarTelaPrincipal();
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Erro ao carregar dados do usuário.", 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Usuário ou senha inválidos.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Erro ao validar login: " + e.getMessage(), 
                "Erro", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarTelaPrincipal() {
        try {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(corFundo);

            // Menu Lateral
            final JPanel menuLateral = criarMenuLateral();
            mainPanel.add(menuLateral, BorderLayout.WEST);

            // Área Principal
            areaPrincipal = new JPanel(new BorderLayout(20, 20));
            areaPrincipal.setBackground(corFundo);
            areaPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Cabeçalho
            final JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(corFundo);
            final String nomeUsuario = usuarioLogado != null ? usuarioLogado.getNomeCompleto() : "Usuário";
            final JLabel bemVindoLabel = new JLabel("Bem-vindo, " + nomeUsuario);
            bemVindoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            headerPanel.add(bemVindoLabel, BorderLayout.WEST);

            // Botões do cabeçalho
            final JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            headerBtns.setOpaque(false);
            final JButton notificacoesBtn = criarBotaoIcone("Notificações");
            final JButton perfilBtn = criarBotaoIcone("Perfil");
            final JButton sairBtn = criarBotaoIcone("Sair");
            sairBtn.addActionListener(e -> {
                int opcao = JOptionPane.showConfirmDialog(frame, 
                    "Deseja realmente sair?", 
                    "Confirmação", 
                    JOptionPane.YES_NO_OPTION);
                if (opcao == JOptionPane.YES_OPTION) {
                    usuarioLogado = null;
                    criarTelaLogin();
                }
            });
            headerBtns.add(notificacoesBtn);
            headerBtns.add(perfilBtn);
            headerBtns.add(sairBtn);
            headerPanel.add(headerBtns, BorderLayout.EAST);
            areaPrincipal.add(headerPanel, BorderLayout.NORTH);
            final JPanel painelInicial = criarTelaDashboard();
            areaPrincipal.add(painelInicial, BorderLayout.CENTER);
            mainPanel.add(areaPrincipal, BorderLayout.CENTER);
            frame.setContentPane(mainPanel);
            frame.revalidate();
            frame.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Erro ao exibir tela principal: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton criarBotaoIcone(final String texto) {
        final JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(new Color(44, 62, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(corPrimaria);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(new Color(44, 62, 80));
            }
        });

        return btn;
    }

    // Método para criar o painel de usuários
    private JPanel criarTelaUsuarios() {
        JPanel usuarios = new JPanel(new BorderLayout(20, 20));
        usuarios.setBackground(corFundo);

        // Barra de ferramentas
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(corFundo);
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton novoUsuario = new JButton("Novo Usuario");
        estilizarBotaoModerno(novoUsuario, corPrimaria);
        novoUsuario.addActionListener(e -> abrirCadastroUsuario()); // Reutiliza a função existente

        JTextField busca = new JTextField(20);
        busca.setPreferredSize(new Dimension(200, 30));
        busca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        toolbar.add(novoUsuario);
        toolbar.add(Box.createHorizontalStrut(20));
        toolbar.add(new JLabel("Buscar: "));
        toolbar.add(busca);
        usuarios.add(toolbar, BorderLayout.NORTH);

        // Tabela de usuários com dados do banco
        String[] colunas = {"Nome", "Login", "Grupo", "Perfil", "Status"};
        List<Usuario> listaUsuarios = DatabaseManager.listarUsuarios();
        Object[][] dados = new Object[listaUsuarios.size()][5];
        
        for (int i = 0; i < listaUsuarios.size(); i++) {
            Usuario u = listaUsuarios.get(i);
            dados[i] = new Object[]{

                u.getNomeCompleto(),
                u.getLogin(),
                u.getGrupo(),
                u.getPerfil().toString(),
                "Ativo"
            };
        }
        
        JTable tabela = new JTable(dados, colunas);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        usuarios.add(scroll, BorderLayout.CENTER);

        return usuarios;
    }

    /**
     * Navega para uma tela específica da aplicação.
     * @param tela Nome da tela para qual navegar
     */
    private void navegarPara(String tela) {
        try {
            if (!temPermissao(tela)) {
                JOptionPane.showMessageDialog(frame,
                    "Você não tem permissão para acessar esta funcionalidade.",
                    "Acesso Negado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            JPanel novoPainel = null;
            switch (tela.toLowerCase()) {
                case "dashboard":
                    novoPainel = criarTelaDashboard();
                    break;
                case "projetos":
                    novoPainel = criarTelaProjetos();
                    break;
                case "equipes":
                    novoPainel = criarTelaEquipes();
                    break;
                case "usuarios":
                    novoPainel = criarTelaUsuarios();
                    break;
                case "config":
                    novoPainel = criarTelaConfiguracoes();
                    break;
                default:
                    throw new IllegalArgumentException("Tela não encontrada: " + tela);
            }

            if (novoPainel != null && areaPrincipal != null) {
                areaPrincipal.removeAll();
                
                // Recria o cabeçalho
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setBackground(corFundo);
                JLabel bemVindoLabel = new JLabel("Bem-vindo, " + 
                    (usuarioLogado != null ? usuarioLogado.getNomeCompleto() : "Usuário"));
                bemVindoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                headerPanel.add(bemVindoLabel, BorderLayout.WEST);

                JPanel headerBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                headerBtns.setOpaque(false);
                
                final JButton notificacoesBtn = criarBotaoIcone("Notificações");
                final JButton perfilBtn = criarBotaoIcone("Perfil");
                final JButton sairBtn = criarBotaoIcone("Sair");
                
                headerBtns.add(notificacoesBtn);
                headerBtns.add(perfilBtn);
                headerBtns.add(sairBtn);
                headerPanel.add(headerBtns, BorderLayout.EAST);
                
                areaPrincipal.add(headerPanel, BorderLayout.NORTH);
                areaPrincipal.add(novoPainel, BorderLayout.CENTER);
                
                // Adiciona listener de logout
                sairBtn.addActionListener(e -> {
                    int opcao = JOptionPane.showConfirmDialog(frame,
                        "Deseja realmente sair?",
                        "Confirmação",
                        JOptionPane.YES_NO_OPTION);
                    if (opcao == JOptionPane.YES_OPTION) {
                        usuarioLogado = null;
                        criarTelaLogin();
                    }
                });

                areaPrincipal.revalidate();
                areaPrincipal.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "Erro ao navegar para a tela " + tela + ": " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para contar projetos por status
    private int[] contarProjetosPorStatus(List<String[]> listaProjetos) {
        int ativos = 0, emAndamento = 0, cancelados = 0, concluidos = 0;
        for (String[] projeto : listaProjetos) {
            String status = projeto[2].toLowerCase();
            if (status.equals("em andamento") || status.equals("planejado")) ativos++;
            if (status.equals("em andamento")) emAndamento++;
            if (status.equals("cancelado")) cancelados++;
            if (status.equals("concluido")) concluidos++;
        }
        return new int[]{ativos, emAndamento, cancelados, concluidos};
    }

    private JPanel criarTelaDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(20, 20));
        dashboard.setBackground(corFundo);

        // Obtém dados dos projetos
        java.util.List<String[]> listaProjetos = DatabaseManager.listarProjetos();
        int[] contagem = contarProjetosPorStatus(listaProjetos);
        int ativos = contagem[0];
        int emAndamento = contagem[1];
        int cancelados = contagem[2];
        int concluidos = contagem[3];

        // Cards de estatísticas
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsPanel.setOpaque(false);
        cardsPanel.add(criarCard("Projetos Ativos", String.valueOf(ativos), corCard1));
        cardsPanel.add(criarCard("Em andamento", String.valueOf(emAndamento), corCard2));
        cardsPanel.add(criarCard("Cancelados", String.valueOf(cancelados), corCard4));
        cardsPanel.add(criarCard("Concluídos", String.valueOf(concluidos), corCard3));
        dashboard.add(cardsPanel, BorderLayout.NORTH);

        // Tabela de projetos
        JPanel tabelaPanel = new JPanel(new BorderLayout());
        tabelaPanel.setOpaque(false);
        tabelaPanel.setBorder(BorderFactory.createTitledBorder("Projetos"));
        String[] colunas = {"Projeto", "Responsavel", "Status", "Prazo"};
        Object[][] dados = new Object[Math.min(10, listaProjetos.size())][4];
        for (int i = 0; i < Math.min(10, listaProjetos.size()); i++) {
            String[] projeto = listaProjetos.get(i);
            dados[i] = new Object[]{projeto[0], projeto[1], projeto[2], projeto[3]};
        }
        JTable tabela = new JTable(dados, colunas);
        JScrollPane scroll = new JScrollPane(tabela);
        tabelaPanel.add(scroll);
        dashboard.add(tabelaPanel, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel criarTelaProjetos() {
        JPanel projetos = new JPanel(new BorderLayout(20, 20));
        projetos.setBackground(corFundo);

        // Barra de ferramentas estilo Trello
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(new Color(235, 236, 240));
        
        final JButton novoProjeto = new JButton("+ Novo Projeto");
        estilizarBotaoModerno(novoProjeto, new Color(76,175,80));
        toolbar.add(novoProjeto);
        
        // Adiciona campo de busca
        final JTextField campoBusca = new JTextField(20);
        campoBusca.setToolTipText("Buscar projetos...");
        toolbar.addSeparator(new Dimension(20, 0));
        toolbar.add(new JLabel("Buscar: "));
        toolbar.add(campoBusca);
        
        // Filtro de status
        toolbar.addSeparator(new Dimension(20, 0));
        toolbar.add(new JLabel("Status: "));
        final JComboBox<String> filtroStatus = new JComboBox<>(

            new String[]{"Todos", "Planejado", "Em andamento", "Concluido", "Cancelado"}
        );
        toolbar.add(filtroStatus);
        
        projetos.add(toolbar, BorderLayout.NORTH);

        // Tabela dinâmica de projetos
        String[] colunas = {"Nome", "Responsavel", "Status", "Início", "Término", "Notes"};
        java.util.List<String[]> listaProjetos = DatabaseManager.listarProjetos();
        final DefaultTableModel model = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2; // Apenas status é editável
            }
        };
        
        // Preenche a tabela
        for (String[] projeto : listaProjetos) {
            model.addRow(projeto);
        }
        
        final JTable tabela = new JTable(model);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.setGridColor(new Color(220, 220, 220));
        tabela.setShowGrid(true);
        tabela.setSelectionBackground(new Color(197, 225, 165));

        // Configurando renderer personalizado para o status
        TableColumn statusColumn = tabela.getColumnModel().getColumn(2);
        statusColumn.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                
                if (status.equalsIgnoreCase("Em andamento")) {
                    c.setForeground(new Color(76, 175, 80));
                } else if (status.equalsIgnoreCase("Concluido")) {
                    c.setForeground(new Color(33, 150, 243));
                } else if (status.equalsIgnoreCase("Cancelado")) {
                    c.setForeground(new Color(244, 67, 54));
                } else {
                    c.setForeground(new Color(255, 152, 0));
                }
                
                return c;
            }
        });

        // Combo para edição do status
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{

            "Planejado", "Em andamento", "Concluido", "Cancelado"
        });
        statusColumn.setCellEditor(new DefaultCellEditor(statusCombo));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        projetos.add(scroll, BorderLayout.CENTER);

        // Listener para duplo clique na coluna Notes
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabela.rowAtPoint(e.getPoint());
                    int col = tabela.columnAtPoint(e.getPoint());
                    if (row >= 0 && col == 5) { // coluna Notes
                        String nome = (String) tabela.getValueAt(row, 0);
                        String responsavel = (String) tabela.getValueAt(row, 1);
                        String status = (String) tabela.getValueAt(row, 2);
                        String inicio = (String) tabela.getValueAt(row, 3);
                        String termino = (String) tabela.getValueAt(row, 4);
                        String notes = (String) tabela.getValueAt(row, 5);
                        abrirDialogEditarProjeto(nome, responsavel, status, inicio, termino, notes);
                    }
                }
            }
        });

        // Listener para atualização de status
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                int row = e.getFirstRow();
                String nome = (String) model.getValueAt(row, 0);
                String responsavel = (String) model.getValueAt(row, 1);
                String novoStatus = (String) model.getValueAt(row, 2);
                DatabaseManager.atualizarStatusProjeto(nome, responsavel, novoStatus);
            }
        });

        // Começo do código removido baseado na solicitação
        // Combo da direita para edição do status
        // JComboBox<String> novoStatusCombo = new JComboBox<>(new String[]{

        //     "Planejado", "Em andamento", "Concluido", "Cancelado"
        // });
        // JButton btnAtualizarStatus = new JButton("Atualizar Status");
        // estilizarBotaoModerno(btnAtualizarStatus, new Color(52, 152, 219));
        // btnAtualizarStatus.addActionListener(e -> {
        //     int selectedRow = tabela.getSelectedRow();
        //     if (selectedRow != -1) {
        //         String nome = (String) model.getValueAt(selectedRow, 0);
        //         String responsavel = (String) model.getValueAt(selectedRow, 1);
        //         String novoStatus = (String) novoStatusCombo.getSelectedItem();
        //         model.setValueAt(novoStatus, selectedRow, 2);
        //         DatabaseManager.atualizarStatusProjeto(nome, responsavel, novoStatus);
        //         JOptionPane.showMessageDialog(projetos, "Status atualizado com sucesso!");
        //     } else {
        //         JOptionPane.showMessageDialog(projetos, "Selecione um projeto para atualizar o status.");
        //     }
        // });

        // JPanel panelDireita = new JPanel(new BorderLayout());
        // panelDireita.setOpaque(false);
        // panelDireita.add(novoStatusCombo, BorderLayout.CENTER);
        // panelDireita.add(btnAtualizarStatus, BorderLayout.EAST);

        // projetos.add(panelDireita, BorderLayout.SOUTH);
        // Fim do código removido

        // Campo de busca
        campoBusca.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filtrarTabela(); }
            public void removeUpdate(DocumentEvent e) { filtrarTabela(); }
            public void insertUpdate(DocumentEvent e) { filtrarTabela(); }
            
            private void filtrarTabela() {
                String texto = campoBusca.getText().toLowerCase();
                String statusSelecionado = (String) filtroStatus.getSelectedItem();
                
                model.setRowCount(0);
                for (String[] projeto : listaProjetos) {
                    boolean matchTexto = texto.isEmpty() || 
                        projeto[0].toLowerCase().contains(texto) || 
                        projeto[1].toLowerCase().contains(texto);
                    
                    boolean matchStatus = statusSelecionado.equals("Todos") || 
                        projeto[2].equalsIgnoreCase(statusSelecionado);
                    
                    if (matchTexto && matchStatus) {
                        model.addRow(projeto);
                    }
                }
            }
        });

        // Filtro de status
        filtroStatus.addActionListener(e -> {
            String texto = campoBusca.getText().toLowerCase();
            String statusSelecionado = (String) filtroStatus.getSelectedItem();
            
            model.setRowCount(0);
            for (String[] projeto : listaProjetos) {
                boolean matchTexto = texto.isEmpty() || 
                    projeto[0].toLowerCase().contains(texto) || 
                    projeto[1].toLowerCase().contains(texto);
                
                boolean matchStatus = statusSelecionado.equals("Todos") || 
                    projeto[2].equalsIgnoreCase(statusSelecionado);
                
                if (matchTexto && matchStatus) {
                    model.addRow(projeto);
                }
            }
        });

        // Ao clicar em '+ Novo Projeto'
        novoProjeto.addActionListener(e -> {
            final JDialog dialog = new JDialog(frame, "Novo Projeto", true);
            dialog.setSize(420, 400);
            dialog.setLocationRelativeTo(frame);
            final JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(new Color(245, 247, 250));
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            final JLabel titulo = new JLabel("Adicionar Projeto");
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
            titulo.setForeground(new Color(76, 175, 80));
            panel.add(titulo, gbc);

            gbc.gridwidth = 1;
            gbc.anchor = GridBagConstraints.WEST;

            // Campo Nome
            gbc.gridx = 0; gbc.gridy = 1;
            panel.add(new JLabel("Nome:"), gbc);
            gbc.gridx = 1;
            final JTextField nomeField = new JTextField(20);
            panel.add(nomeField, gbc);

            // Campo Responsável
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(new JLabel("Responsavel:"), gbc);
            gbc.gridx = 1;
            final JTextField responsavelField = new JTextField(20);
            panel.add(responsavelField, gbc);

            // Campo Status
            gbc.gridx = 0; gbc.gridy = 3;
            panel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            final JComboBox<String> statusComboCadastro = new JComboBox<>(

                new String[]{"Planejado", "Em andamento", "Concluido", "Cancelado"}
            );
            panel.add(statusComboCadastro, gbc);

            // Campo Data Início
            gbc.gridx = 0; gbc.gridy = 4;
            panel.add(new JLabel("Início (dd/MM/yyyy):"), gbc);
            gbc.gridx = 1;
            // Correção: declaração única e atribuição condicional para inicioField e terminoField
            final JFormattedTextField inicioField;
            {
                JFormattedTextField temp;
                try {
                    MaskFormatter dateMask = new MaskFormatter("##/##/####");
                    dateMask.setPlaceholderCharacter('_');
                    temp = new JFormattedTextField(dateMask);
                } catch (ParseException ex) {
                    temp = new JFormattedTextField();
                }
                temp.setColumns(20);
                panel.add(temp, gbc);
                inicioField = temp;
            }

            gbc.gridx = 0; gbc.gridy = 5;
            panel.add(new JLabel("Término (dd/MM/yyyy):"), gbc);
            gbc.gridx = 1;
            final JFormattedTextField terminoField;
            {
                JFormattedTextField temp;
                try {
                    MaskFormatter dateMask = new MaskFormatter("##/##/####");
                    dateMask.setPlaceholderCharacter('_');
                    temp = new JFormattedTextField(dateMask);
                } catch (ParseException ex) {
                    temp = new JFormattedTextField();
                }
                temp.setColumns(20);
                panel.add(temp, gbc);
                terminoField = temp;
            }

            // Campo Notes
            gbc.gridx = 0; gbc.gridy = 6;
            panel.add(new JLabel("Notes (anotações):"), gbc);
            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            final JTextArea notesArea = new JTextArea(5, 20);
            notesArea.setFont(new Font("Segue UI", Font.PLAIN, 14));
            notesArea.setLineWrap(true);
            notesArea.setWrapStyleWord(true);
            notesArea.setDocument(new javax.swing.text.PlainDocument() {
                public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
                        throws javax.swing.text.BadLocationException {
                    if (str == null) return;
                    if ((getLength() + str.length()) <= 255) {
                        super.insertString(offs, str, a);
                    }
                }
            });
            final JScrollPane notesScroll = new JScrollPane(notesArea);
            panel.add(notesScroll, gbc);

            // Botões
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
            gbc.anchor = GridBagConstraints.CENTER;
            final JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            btnPanel.setOpaque(false);
            final JButton cadastrar = new JButton("Salvar");
            final JButton cancelar = new JButton("Cancelar");
            estilizarBotaoModerno(cadastrar, new Color(76, 175, 80));
            estilizarBotaoModerno(cancelar, new Color(127, 140, 141));
            btnPanel.add(cadastrar);
            btnPanel.add(cancelar);
            panel.add(btnPanel, gbc);

            // Ação do botão Cadastrar
            cadastrar.addActionListener(ev -> {
                String nome = nomeField.getText().trim();
                String responsavel = responsavelField.getText().trim();
                String status = (String) statusComboCadastro.getSelectedItem();
                String inicio = inicioField.getText().trim();
                String termino = terminoField.getText().trim();
                String notes = notesArea.getText().trim();

                // Validação dos campos
                if (nome.isEmpty() || responsavel.isEmpty() || 
                    !validarFormatoData(inicio) || !validarFormatoData(termino)) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Por favor, preencha todos os campos corretamente.\nDatas devem estar no formato dd/MM/yyyy", 
                        "Erro de validação", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validação das datas
                if (!validarDatasProjeto(inicio, termino)) {
                    JOptionPane.showMessageDialog(dialog,
                        "A data de término deve ser posterior à data de início",
                        "Erro de validação",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (DatabaseManager.inserirProjeto(nome, responsavel, status, inicio, termino, notes)) {
                    JOptionPane.showMessageDialog(dialog, "Projeto cadastrado com sucesso!");
                    dialog.dispose();
                    navegarPara("projetos");
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                        "Erro ao cadastrar projeto!\nVerifique se os dados estão corretos.", 
                        "Erro", 
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            cancelar.addActionListener(ev -> dialog.dispose());
            dialog.setContentPane(panel);
            dialog.setResizable(false);
            dialog.setVisible(true);
        });

        return projetos;
    }

    private void abrirDialogoNovoProjeto() {
        final JDialog dialog = new JDialog(frame, "Novo Projeto", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(frame);
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        final JLabel titulo = new JLabel("Adicionar Projeto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(76, 175, 80));
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Nome
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        final JTextField nomeField = new JTextField(20);
        panel.add(nomeField, gbc);

        // Campo Responsável
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Responsavel:"), gbc);
        gbc.gridx = 1;
        final JTextField responsavelField = new JTextField(20);
        panel.add(responsavelField, gbc);

        // Campo Status
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        final JComboBox<String> statusComboCadastro = new JComboBox<>(

            new String[]{"Planejado", "Em andamento", "Concluido", "Cancelado"}
        );
        panel.add(statusComboCadastro, gbc);

        // Campo Data Início
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Início (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        // Correção: declaração única e atribuição condicional para inicioField e terminoField
        final JFormattedTextField inicioField;
        {
            JFormattedTextField temp;
            try {
                MaskFormatter dateMask = new MaskFormatter("##/##/####");
                dateMask.setPlaceholderCharacter('_');
                temp = new JFormattedTextField(dateMask);
            } catch (ParseException ex) {
                temp = new JFormattedTextField();
            }
            temp.setColumns(20);
            panel.add(temp, gbc);
            inicioField = temp;
        }

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Término (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        final JFormattedTextField terminoField;
        {
            JFormattedTextField temp;
            try {
                MaskFormatter dateMask = new MaskFormatter("##/##/####");
                dateMask.setPlaceholderCharacter('_');
                temp = new JFormattedTextField(dateMask);
            } catch (ParseException ex) {
                temp = new JFormattedTextField();
            }
            temp.setColumns(20);
            panel.add(temp, gbc);
            terminoField = temp;
        }

        // Campo Notes
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Notes (anotações):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        final JTextArea notesArea = new JTextArea(5, 20);
        notesArea.setFont(new Font("Segue UI", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setDocument(new javax.swing.text.PlainDocument() {
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a) 
                    throws javax.swing.text.BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= 255) {
                    super.insertString(offs, str, a);
                }
            }
        });
        final JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesScroll, gbc);

        // Botões
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        final JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        final JButton cadastrar = new JButton("Salvar");
        final JButton cancelar = new JButton("Cancelar");
        estilizarBotaoModerno(cadastrar, new Color(76, 175, 80));
        estilizarBotaoModerno(cancelar, new Color(127, 140, 141));
        btnPanel.add(cadastrar);
        btnPanel.add(cancelar);
        panel.add(btnPanel, gbc);

        // Ação do botão Cadastrar
        cadastrar.addActionListener(ev -> {
            String nome = nomeField.getText().trim();
            String responsavel = responsavelField.getText().trim();
            String status = (String) statusComboCadastro.getSelectedItem();
            String inicio = inicioField.getText().trim();
            String termino = terminoField.getText().trim();
            String notes = notesArea.getText().trim();

            // Validação dos campos
            if (nome.isEmpty() || responsavel.isEmpty() || 
                !validarFormatoData(inicio) || !validarFormatoData(termino)) {
                JOptionPane.showMessageDialog(dialog, 
                    "Por favor, preencha todos os campos corretamente.\nDatas devem estar no formato dd/MM/yyyy", 
                    "Erro de validação", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validação das datas
            if (!validarDatasProjeto(inicio, termino)) {
                JOptionPane.showMessageDialog(dialog,
                    "A data de término deve ser posterior à data de início",
                    "Erro de validação",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseManager.inserirProjeto(nome, responsavel, status, inicio, termino, notes)) {
                JOptionPane.showMessageDialog(dialog, "Projeto cadastrado com sucesso!");
                dialog.dispose();
                navegarPara("projetos");
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Erro ao cadastrar projeto!\nVerifique se os dados estão corretos.", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelar.addActionListener(ev -> dialog.dispose());
        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Método para abrir diálogo de edição de projeto
    private void abrirDialogEditarProjeto(String nome, String responsavel, String status, String inicio, String termino, String notes) {
        final JDialog dialog = new JDialog(frame, "Editar Projeto", true);
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(frame);
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        final JLabel titulo = new JLabel("Editar Projeto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(76, 175, 80));
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Nome
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        final JTextField nomeField = new JTextField(nome, 20);
        panel.add(nomeField, gbc);

        // Campo Responsável
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Responsavel:"), gbc);
        gbc.gridx = 1;
        final JTextField responsavelField = new JTextField(responsavel, 20);
        panel.add(responsavelField, gbc);

        // Campo Status
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        final JComboBox<String> statusCombo = new JComboBox<>(

            new String[]{"Planejado", "Em andamento", "Concluido", "Cancelado"}
        );
        statusCombo.setSelectedItem(status);
        panel.add(statusCombo, gbc);

        // Campo Data Início
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Início (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        final JTextField inicioField = new JTextField(inicio, 20);
        panel.add(inicioField, gbc);

        // Campo Data Término
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Término (dd/MM/yyyy):"), gbc);
        gbc.gridx = 1;
        final JTextField terminoField = new JTextField(termino, 20);
        panel.add(terminoField, gbc);

        // Campo Notes
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Notes (anotações):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        final JTextArea notesArea = new JTextArea(notes, 5, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        final JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesScroll, gbc);

        // Botões
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        final JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        final JButton salvar = new JButton("Salvar");
        final JButton cancelar = new JButton("Cancelar");
        estilizarBotaoModerno(salvar, new Color(76, 175, 80));
        estilizarBotaoModerno(cancelar, new Color(127, 140, 141));
        btnPanel.add(salvar);
        btnPanel.add(cancelar);
        panel.add(btnPanel, gbc);

        salvar.addActionListener(ev -> {
            String novoNome = nomeField.getText().trim();
            String novoResponsavel = responsavelField.getText().trim();
            String novoStatus = (String) statusCombo.getSelectedItem();
            String novoInicio = inicioField.getText().trim();
            String novoTermino = terminoField.getText().trim();
            String novoNotes = notesArea.getText().trim();
            if (novoNome.isEmpty() || novoResponsavel.isEmpty() ||
                !validarFormatoData(novoInicio) || !validarFormatoData(novoTermino)) {
                JOptionPane.showMessageDialog(dialog,
                    "Por favor, preencha todos os campos corretamente.\nDatas devem estar no formato dd/MM/yyyy",
                    "Erro de validação",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!validarDatasProjeto(novoInicio, novoTermino)) {
                JOptionPane.showMessageDialog(dialog,
                    "A data de término deve ser posterior à data de início",
                    "Erro de validação",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean ok = DatabaseManager.atualizarProjeto(
                novoNome, novoResponsavel, novoStatus, novoInicio, novoTermino, novoNotes,
                nome, responsavel
            );
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "Projeto atualizado com sucesso!");
                dialog.dispose();
                navegarPara("projetos");
            } else {
                JOptionPane.showMessageDialog(dialog,
                    "Erro ao atualizar projeto!\nVerifique se os dados estão corretos.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelar.addActionListener(ev -> dialog.dispose());
        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Método para criar o painel de configurações
    private JPanel criarTelaConfiguracoes() {
        JPanel config = new JPanel(new BorderLayout(20, 20));
        config.setBackground(corFundo);

        JPanel opcoesPanel = new JPanel();
        opcoesPanel.setLayout(new BoxLayout(opcoesPanel, BoxLayout.Y_AXIS));
        opcoesPanel.setBackground(corFundo);
        opcoesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Se��es de configura��es
        opcoesPanel.add(criarSecaoConfiguracao("Configuracoes Gerais"));
        opcoesPanel.add(Box.createVerticalStrut(20));
        opcoesPanel.add(criarSecaoConfiguracao("Permissoes"));
        opcoesPanel.add(Box.createVerticalStrut(20));
        opcoesPanel.add(criarSecaoConfiguracao("Notificacoes"));
        opcoesPanel.add(Box.createVerticalStrut(20));
        opcoesPanel.add(criarSecaoConfiguracao("Backup"));

        config.add(new JScrollPane(opcoesPanel), BorderLayout.CENTER);
        return config;
    }

    private JPanel criarSecaoConfiguracao(String titulo) {
        JPanel secao = new JPanel();
        secao.setLayout(new BoxLayout(secao, BoxLayout.Y_AXIS));
        secao.setBackground(Color.WHITE);
        secao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(titulo),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Exemplo de op��es
        for (int i = 1; i <= 3; i++) {
            JCheckBox opcao = new JCheckBox("Opcao " + i);
            opcao.setBackground(Color.WHITE);
            secao.add(opcao);
            secao.add(Box.createVerticalStrut(5));
        }

        return secao;
    }

    // Método utilitário para estilizar botões genéricos
    private void estilizarBotao(JButton botao, Color cor) {
        botao.setFocusPainted(false);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 13));
        botao.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Método para criar o painel de equipes
    private JPanel criarTelaEquipes() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(corFundo);

        // Barra de ferramentas
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(corFundo);
        toolbar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton novaEquipeBtn = new JButton("Nova Equipe");
        estilizarBotaoModerno(novaEquipeBtn, corPrimaria);
        toolbar.add(novaEquipeBtn);
        toolbar.add(Box.createHorizontalStrut(20));

        JButton acessarEquipeBtn = new JButton("Acessar Equipe");
        estilizarBotaoModerno(acessarEquipeBtn, corSecundaria);
        acessarEquipeBtn.setEnabled(false);
        toolbar.add(acessarEquipeBtn);

        // Botões para admin: excluir e adicionar membro
        JButton excluirEquipeBtn = new JButton("Excluir Equipe");
        estilizarBotaoModerno(excluirEquipeBtn, corCard4);
        excluirEquipeBtn.setEnabled(false);
        toolbar.add(excluirEquipeBtn);

        JButton adicionarMembroBtn = new JButton("Adicionar Membro");
        estilizarBotaoModerno(adicionarMembroBtn, corCard1);
        adicionarMembroBtn.setEnabled(false);
        toolbar.add(adicionarMembroBtn);

        panel.add(toolbar, BorderLayout.NORTH);

        // Lista de todas as equipes do banco
        DefaultListModel<Equipe> equipesModel = new DefaultListModel<>();
        JList<Equipe> listaEquipes = new JList<>(equipesModel);
        listaEquipes.setCellRenderer(new EquipeCellRenderer());
        JScrollPane scrollEquipes = new JScrollPane(listaEquipes);
        scrollEquipes.setBorder(BorderFactory.createTitledBorder("Todas as Equipes"));
        panel.add(scrollEquipes, BorderLayout.CENTER);

        // Carrega todas as equipes do banco
        for (Equipe eq : DatabaseManager.listarEquipes()) {
            equipesModel.addElement(eq);
        }

        // Habilita botões ao selecionar equipe
        listaEquipes.addListSelectionListener(e -> {
            boolean selecionado = !listaEquipes.isSelectionEmpty();
            acessarEquipeBtn.setEnabled(selecionado);
            boolean admin = usuarioLogado != null && usuarioLogado.getPerfil() == PerfilUsuario.ADMINISTRADOR;
            excluirEquipeBtn.setEnabled(selecionado && admin);
            adicionarMembroBtn.setEnabled(selecionado && admin);
        });

        // Ação do botão acessar equipe
        acessarEquipeBtn.addActionListener(e -> {
            Equipe equipe = listaEquipes.getSelectedValue();
            if (equipe != null) {
                abrirDialogEquipe(equipe);
            }
        });

        // Ação do botão Nova Equipe
        novaEquipeBtn.addActionListener(e -> abrirDialogNovaEquipe(panel, equipesModel));

        // Ação do botão Excluir Equipe
        excluirEquipeBtn.addActionListener(e -> {
            Equipe equipe = listaEquipes.getSelectedValue();
            if (equipe != null) {
                int op = JOptionPane.showConfirmDialog(panel, "Excluir a equipe '" + equipe.getNome() + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (op == JOptionPane.YES_OPTION) {
                    if (DatabaseManager.excluirEquipe(equipe.getNome())) {
                        equipesModel.removeElement(equipe);
                        JOptionPane.showMessageDialog(panel, "Equipe excluída.");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Erro ao excluir equipe.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Ação do botão Adicionar Membro
        adicionarMembroBtn.addActionListener(e -> {
            Equipe equipe = listaEquipes.getSelectedValue();
            if (equipe != null) {
                // Busca todos usuários que não estão na equipe
                java.util.List<Usuario> todos = DatabaseManager.listarUsuarios();
                java.util.List<Usuario> disponiveis = new java.util.ArrayList<>();
                for (Usuario u : todos) {
                    boolean jaMembro = false;
                    for (Usuario m : equipe.getMembros()) {
                        if (m.getLogin().equals(u.getLogin())) {
                            jaMembro = true;
                            break;
                        }
                    }
                    if (!jaMembro) disponiveis.add(u);
                }
                if (disponiveis.isEmpty()) {
                    JOptionPane.showMessageDialog(panel, "Não há usuários disponíveis para adicionar.");
                    return;
                }
                JList<Usuario> listaDisp = new JList<>(disponiveis.toArray(new Usuario[0]));
                listaDisp.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                listaDisp.setCellRenderer(new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                        Usuario u = (Usuario) value;
                        String txt = u.getNomeCompleto() + " (" + u.getLogin() + ")";
                        return super.getListCellRendererComponent(list, txt, index, isSelected, cellHasFocus);
                    }
                });
                int op = JOptionPane.showConfirmDialog(panel, new JScrollPane(listaDisp), "Selecionar membros para adicionar", JOptionPane.OK_CANCEL_OPTION);
                if (op == JOptionPane.OK_OPTION) {
                    java.util.List<Usuario> selecionados = listaDisp.getSelectedValuesList();
                    for (Usuario u : selecionados) {
                        if (DatabaseManager.adicionarMembroEquipe(equipe.getNome(), u.getLogin())) {
                            equipe.adicionarMembro(u);
                        }
                    }
                    // Atualiza visualização
                    listaEquipes.repaint();
                }
            }
        });

        return panel;
    }

    // Diálogo para criar nova equipe
    private void abrirDialogNovaEquipe(JPanel parentPanel, DefaultListModel<Equipe> equipesModel) {
        JDialog dialog = new JDialog(frame, "Nova Equipe", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(frame);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titulo = new JLabel("Criar Nova Equipe");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(corPrimaria);
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Nome da Equipe:"), gbc);
        gbc.gridx = 1;
        JTextField nomeEquipeField = new JTextField(20);
        panel.add(nomeEquipeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        JTextField descricaoEquipeField = new JTextField(20);
        panel.add(descricaoEquipeField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Selecione os membros da equipe:"), gbc);
        gbc.gridy = 4;
        List<Usuario> usuarios = DatabaseManager.listarUsuarios();
        DefaultListModel<Usuario> membrosModel = new DefaultListModel<>();
        for (Usuario u : usuarios) membrosModel.addElement(u);
        JList<Usuario> membrosList = new JList<>(membrosModel);
        membrosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane membrosScroll = new JScrollPane(membrosList);
        membrosScroll.setPreferredSize(new Dimension(320, 80));
        panel.add(membrosScroll, gbc);

        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setOpaque(false);
        JButton salvar = new JButton("Salvar");
        JButton cancelar = new JButton("Cancelar");
        estilizarBotaoModerno(salvar, new Color(76, 175, 80));
        estilizarBotaoModerno(cancelar, new Color(127, 140, 141));
        btnPanel.add(salvar);
        btnPanel.add(cancelar);
        panel.add(btnPanel, gbc);

        salvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                String nome = nomeEquipeField.getText().trim();
                String descricao = descricaoEquipeField.getText().trim();
                List<Usuario> membrosSelecionados = membrosList.getSelectedValuesList();
                if (nome.isEmpty() || descricao.isEmpty() || membrosSelecionados.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos e selecione pelo menos um membro.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Equipe equipe = new Equipe(nome, descricao);
                for (Usuario u : membrosSelecionados) equipe.adicionarMembro(u);
                if (DatabaseManager.inserirEquipe(equipe)) {
                    equipesModel.clear();
                    for (Equipe eq : DatabaseManager.listarEquipes()) {
                        equipesModel.addElement(eq);
                    }
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Já existe uma equipe com esse nome.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelar.addActionListener(ev -> dialog.dispose());
        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Diálogo para exibir detalhes da equipe
    private void abrirDialogEquipe(Equipe equipe) {
        JDialog dialog = new JDialog(frame, "Equipe: " + equipe.getNome(), true);
        dialog.setSize(420, 350);
        dialog.setLocationRelativeTo(frame);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(245, 247, 250));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 247, 250));
        JLabel nomeLabel = new JLabel("Nome: " + equipe.getNome());
        nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JLabel descLabel = new JLabel("Descrição: " + equipe.getDescricao());
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(nomeLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(descLabel);
        panel.add(infoPanel, BorderLayout.NORTH);

        // Lista de membros
        DefaultListModel<String> membrosModel = new DefaultListModel<>();
        for (Usuario u : equipe.getMembros()) {
            membrosModel.addElement(u.getNomeCompleto() + " (" + u.getLogin() + ")");
        }
        JList<String> membrosList = new JList<>(membrosModel);
        membrosList.setBorder(BorderFactory.createTitledBorder("Membros da Equipe"));
        membrosList.setFont(new Font("Segue UI", Font.PLAIN, 13));
        panel.add(new JScrollPane(membrosList), BorderLayout.CENTER);

        JButton fecharBtn = new JButton("Fechar");
        estilizarBotaoModerno(fecharBtn, corPrimaria);
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 247, 250));
        btnPanel.add(fecharBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);
        fecharBtn.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Método para criar e exibir o diálogo de detalhes do projeto
    private void abrirDialogDetalhesProjeto(String nome, String responsavel, String status, String inicio, String termino, String notes) {
        final JDialog dialog = new JDialog(frame, "Detalhes do Projeto", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(frame);
        final JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 247, 250));
        final GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        final JLabel titulo = new JLabel("Detalhes do Projeto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(44, 62, 80));
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;

        // Campos de detalhes
        panel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(nome), gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Responsavel:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(responsavel), gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(status), gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Início:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(inicio), gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Término:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(termino), gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        JTextArea notesArea = new JTextArea(5, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setText(notes);
        notesArea.setEditable(false);
        final JScrollPane notesScroll = new JScrollPane(notesArea);
        panel.add(notesScroll, gbc);

        // Botão de fechar
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton fecharBtn = new JButton("Fechar");
        estilizarBotaoModerno(fecharBtn, new Color(52, 152, 219));
        panel.add(fecharBtn, gbc);

        fecharBtn.addActionListener(e -> dialog.dispose());

        dialog.setContentPane(panel);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }

    // Método para criar o painel de tarefas
    private JPanel criarTelaTarefas() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Funcionalidade de Tarefas não implementada."));
        return panel;
    }

    // Método utilitário para criar cards de estatísticas
    private JPanel criarCard(String titulo, String valor, Color cor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cor);
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblValor = new JLabel(valor, SwingConstants.RIGHT);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValor.setForeground(Color.WHITE);
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);
        return card;
    }
    
    // Validação de formato de data dd/MM/yyyy
    private boolean validarFormatoData(String data) {
        if (data == null || data.length() != 10) return false;
        try {
            int dia = Integer.parseInt(data.substring(0, 2));
            int mes = Integer.parseInt(data.substring(3, 5));
            int ano = Integer.parseInt(data.substring(6, 10));
            if (dia < 1 || dia > 31) return false;
            if (mes < 1 || mes > 12) return false;
            if (ano < 2000 || ano > 2100) return false;
            // Meses com 30 dias
            if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
                if (dia > 30) return false;
            }
            // Fevereiro
            if (mes == 2) {
                boolean bissexto = (ano % 4 == 0 && (ano % 100 != 0 || ano % 400 == 0));
                if (bissexto && dia > 29) return false;
                if (!bissexto && dia > 28) return false;
            }
            return true;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return false;
        }
    }

    // Validação lógica das datas (término > início)
    private boolean validarDatasProjeto(String inicio, String termino) {
        try {
            String[] partsInicio = inicio.split("/");
            String[] partsTermino = termino.split("/");
            int diaInicio = Integer.parseInt(partsInicio[0]);
            int mesInicio = Integer.parseInt(partsInicio[1]);
            int anoInicio = Integer.parseInt(partsInicio[2]);
            int diaTermino = Integer.parseInt(partsTermino[0]);
            int mesTermino = Integer.parseInt(partsTermino[1]);
            int anoTermino = Integer.parseInt(partsTermino[2]);
            if (anoTermino < anoInicio) return false;
            if (anoTermino == anoInicio && mesTermino < mesInicio) return false;
            if (anoTermino == anoInicio && mesTermino == mesInicio && diaTermino < diaInicio) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            // Configura o Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // Define o encoding
            System.setProperty("file.encoding", "UTF-8");
            // Carrega os drivers necessários
            try {
                Class.forName("org.sqlite.JDBC");
                System.out.println("Driver SQLite carregado com sucesso!");
            } catch (ClassNotFoundException e) {
                System.err.println("Erro ao carregar driver SQLite: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            // Verifica se consegue estabelecer conexão
            try (var conn = DriverManager.getConnection("jdbc:sqlite:projetos.db")) {
                System.out.println("Conexão com SQLite testada com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao testar conexão SQLite: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            // Inicia a aplicação
            SwingUtilities.invokeLater(() -> {
                try {
                    new AppProjetos();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Erro ao iniciar o sistema: " + e.getMessage(),
                        "Erro Fatal",
                        JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Erro ao inicializar o sistema: " + e.getMessage(), 
                "Erro Fatal", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    // Renderer customizado para exibir equipes de forma visual
    private static class EquipeCellRenderer extends JPanel implements ListCellRenderer<Equipe> {
        private final JLabel nomeLabel = new JLabel();
        private final JLabel descLabel = new JLabel();
        private final JLabel membrosLabel = new JLabel();
        public EquipeCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            nomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            membrosLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            add(nomeLabel, BorderLayout.NORTH);
            add(descLabel, BorderLayout.CENTER);
            add(membrosLabel, BorderLayout.SOUTH);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends Equipe> list, Equipe equipe, int index, boolean isSelected, boolean cellHasFocus) {
            nomeLabel.setText(equipe.getNome());
            descLabel.setText(equipe.getDescricao());
            StringBuilder membros = new StringBuilder("Membros: ");
            for (Usuario u : equipe.getMembros()) {
                membros.append(u.getNomeCompleto()).append(", ");
            }
            if (equipe.getMembros().size() > 0) membros.setLength(membros.length()-2);
            membrosLabel.setText(membros.toString());
            setBackground(isSelected ? new Color(220,235,255) : Color.WHITE);
            return this;
        }
    }
}

package app.app;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DatabaseManager {
    // URL do banco de dados SQLite
    private static final String DB_URL = "jdbc:sqlite:projetos.db";

    // Lista de equipes (não usada para persistência, apenas referência)
    private static final List<Equipe> equipes = new ArrayList<>();

    public static void inicializarBancoDados() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String createTableSQL = 
                    "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "login TEXT PRIMARY KEY, " +
                    "senha TEXT NOT NULL, " +
                    "nomeCompleto TEXT, " +
                    "email TEXT, " +
                    "grupo TEXT, " +
                    "perfil TEXT NOT NULL" +
                    ")";
                stmt.execute(createTableSQL);
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios WHERE login='admin'");
                if (rs.next() && rs.getInt(1) == 0) {
                    PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO usuarios (login, senha, nomeCompleto, email, grupo, perfil) " +
                        "VALUES (?, ?, ?, ?, ?, ?)"
                    );
                    ps.setString(1, "admin");
                    ps.setString(2, "123");
                    ps.setString(3, "Administrador");
                    ps.setString(4, "admin@sistema.com");
                    ps.setString(5, "Administrativo");
                    ps.setString(6, "ADMINISTRADOR");
                    ps.executeUpdate();
                    ps.close();
                }
                rs.close();
                String createProjetosSQL =
                    "CREATE TABLE IF NOT EXISTS projetos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "responsavel TEXT, " +
                    "status TEXT, " +
                    "inicio TEXT, " +
                    "termino TEXT, " +
                    "notes TEXT" +
                    ")";
                stmt.execute(createProjetosSQL);
                String createEquipesSQL =
                    "CREATE TABLE IF NOT EXISTS equipes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL UNIQUE, " +
                    "descricao TEXT NOT NULL" +
                    ")";
                stmt.execute(createEquipesSQL);
                String createEquipeMembrosSQL =
                    "CREATE TABLE IF NOT EXISTS equipe_membros (" +
                    "equipe_id INTEGER, " +
                    "usuario_login TEXT, " +
                    "FOREIGN KEY(equipe_id) REFERENCES equipes(id), " +
                    "FOREIGN KEY(usuario_login) REFERENCES usuarios(login)" +
                    ")";
                stmt.execute(createEquipeMembrosSQL);
                stmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Falha ao inicializar banco de dados", e);
        }
    }

    public static boolean autenticarUsuario(String login, String senha) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuarios WHERE login=? AND senha=?")) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Usuario buscarUsuario(String login) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuarios WHERE login=?")) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getString("nomeCompleto"),
                        "", // cpf não usado
                        rs.getString("email"),
                        rs.getString("grupo"),
                        rs.getString("login"),
                        rs.getString("senha"),
                        PerfilUsuario.valueOf(rs.getString("perfil"))
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean inserirUsuario(Usuario usuario) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (buscarUsuario(usuario.getLogin()) != null) {
                return false;
            }
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO usuarios (login, senha, nomeCompleto, email, grupo, perfil) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, usuario.getLogin());
                ps.setString(2, usuario.getSenha());
                ps.setString(3, usuario.getNomeCompleto());
                ps.setString(4, usuario.getEmail());
                ps.setString(5, usuario.getGrupo());
                ps.setString(6, usuario.getPerfil().toString());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios")) {
            while (rs.next()) {
                usuarios.add(new Usuario(
                    rs.getString("nomeCompleto"),
                    "", // cpf não usado
                    rs.getString("email"),
                    rs.getString("grupo"),
                    rs.getString("login"),
                    rs.getString("senha"),
                    PerfilUsuario.valueOf(rs.getString("perfil"))
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }

    public static boolean inserirProjeto(String nome, String responsavel, String status, String inicio, String termino, String notes) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO projetos (nome, responsavel, status, inicio, termino, notes) VALUES (?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, nome);
                ps.setString(2, responsavel);
                ps.setString(3, status);
                ps.setString(4, inicio);
                ps.setString(5, termino);
                ps.setString(6, notes);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir projeto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<String[]> listarProjetos() {
        List<String[]> projetos = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT nome, responsavel, status, inicio, termino, notes FROM projetos")) {
            while (rs.next()) {
                projetos.add(new String[]{
                    rs.getString("nome"),
                    rs.getString("responsavel"),
                    rs.getString("status"),
                    rs.getString("inicio"),
                    rs.getString("termino"),
                    rs.getString("notes")
                });
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar projetos: " + e.getMessage());
            e.printStackTrace();
        }
        return projetos;
    }

    public static void atualizarStatusProjeto(String nome, String responsavel, String status) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE projetos SET status=? WHERE nome=? AND responsavel=?")) {
                ps.setString(1, status);
                ps.setString(2, nome);
                ps.setString(3, responsavel);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do projeto: " + e.getMessage());
        }
    }

    public static void atualizarNotesProjeto(String nome, String responsavel, String notes) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE projetos SET notes=? WHERE nome=? AND responsavel=?")) {
                ps.setString(1, notes);
                ps.setString(2, nome);
                ps.setString(3, responsavel);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar notes do projeto: " + e.getMessage());
        }
    }

    public static void excluirProjeto(String nome, String responsavel) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM projetos WHERE nome=? AND responsavel=?")) {
                ps.setString(1, nome);
                ps.setString(2, responsavel);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir projeto: " + e.getMessage());
        }
    }

    public static String buscarNotesProjeto(String nome, String responsavel) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT notes FROM projetos WHERE nome=? AND responsavel=?")) {
            ps.setString(1, nome);
            ps.setString(2, responsavel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("notes");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar notes do projeto: " + e.getMessage());
        }
        return "";
    }

    public static boolean atualizarProjeto(String novoNome, String novoResponsavel, String novoStatus, String novoInicio, String novoTermino, String novoNotes, String nomeAntigo, String responsavelAntigo) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE projetos SET nome=?, responsavel=?, status=?, inicio=?, termino=?, notes=? WHERE nome=? AND responsavel=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, novoNome);
                ps.setString(2, novoResponsavel);
                ps.setString(3, novoStatus);
                ps.setString(4, novoInicio);
                ps.setString(5, novoTermino);
                ps.setString(6, novoNotes);
                ps.setString(7, nomeAntigo);
                ps.setString(8, responsavelAntigo);
                int updated = ps.executeUpdate();
                return updated > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar projeto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Insere equipe e membros no banco
    public static boolean inserirEquipe(Equipe equipe) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement check = conn.prepareStatement("SELECT id FROM equipes WHERE nome = ?");
            check.setString(1, equipe.getNome());
            ResultSet rsCheck = check.executeQuery();
            if (rsCheck.next()) {
                rsCheck.close();
                check.close();
                return false;
            }
            rsCheck.close();
            check.close();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO equipes (nome, descricao) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, equipe.getNome());
            ps.setString(2, equipe.getDescricao());
            int affected = ps.executeUpdate();
            if (affected == 0) return false;
            ResultSet rs = ps.getGeneratedKeys();
            int equipeId = -1;
            if (rs.next()) equipeId = rs.getInt(1);
            rs.close();
            ps.close();
            if (equipeId == -1) return false;
            for (Usuario u : equipe.getMembros()) {
                PreparedStatement psM = conn.prepareStatement(
                    "INSERT INTO equipe_membros (equipe_id, usuario_login) VALUES (?, ?)");
                psM.setInt(1, equipeId);
                psM.setString(2, u.getLogin());
                psM.executeUpdate();
                psM.close();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir equipe: " + e.getMessage());
            return false;
        }
    }

    // Lista todas as equipes do banco
    public static List<Equipe> listarEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nome, descricao FROM equipes");
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String desc = rs.getString("descricao");
                Equipe equipe = new Equipe(nome, desc);
                PreparedStatement psM = conn.prepareStatement(
                    "SELECT usuario_login FROM equipe_membros WHERE equipe_id = ?");
                psM.setInt(1, id);
                ResultSet rsM = psM.executeQuery();
                while (rsM.next()) {
                    Usuario u = buscarUsuario(rsM.getString("usuario_login"));
                    if (u != null) equipe.adicionarMembro(u);
                }
                rsM.close();
                psM.close();
                equipes.add(equipe);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar equipes: " + e.getMessage());
        }
        return equipes;
    }

    // Lista equipes de um usuário
    public static List<Equipe> listarEquipesDoUsuario(String login) {
        List<Equipe> equipes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT e.id, e.nome, e.descricao FROM equipes e " +
                "JOIN equipe_membros m ON e.id = m.equipe_id WHERE m.usuario_login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String desc = rs.getString("descricao");
                Equipe equipe = new Equipe(nome, desc);
                PreparedStatement psM = conn.prepareStatement(
                    "SELECT usuario_login FROM equipe_membros WHERE equipe_id = ?");
                psM.setInt(1, id);
                ResultSet rsM = psM.executeQuery();
                while (rsM.next()) {
                    Usuario u = buscarUsuario(rsM.getString("usuario_login"));
                    if (u != null) equipe.adicionarMembro(u);
                }
                rsM.close();
                psM.close();
                equipes.add(equipe);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Erro ao listar equipes do usuário: " + e.getMessage());
        }
        return equipes;
    }

    // Remove equipe do banco
    public static boolean excluirEquipe(String nomeEquipe) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement psId = conn.prepareStatement("SELECT id FROM equipes WHERE nome = ?");
            psId.setString(1, nomeEquipe);
            ResultSet rs = psId.executeQuery();
            if (!rs.next()) {
                rs.close();
                psId.close();
                return false;
            }
            int equipeId = rs.getInt("id");
            rs.close();
            psId.close();
            PreparedStatement psDelMembros = conn.prepareStatement("DELETE FROM equipe_membros WHERE equipe_id = ?");
            psDelMembros.setInt(1, equipeId);
            psDelMembros.executeUpdate();
            psDelMembros.close();
            PreparedStatement psDelEquipe = conn.prepareStatement("DELETE FROM equipes WHERE id = ?");
            psDelEquipe.setInt(1, equipeId);
            psDelEquipe.executeUpdate();
            psDelEquipe.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir equipe: " + e.getMessage());
            return false;
        }
    }

    // Adiciona membro à equipe
    public static boolean adicionarMembroEquipe(String nomeEquipe, String loginUsuario) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement psId = conn.prepareStatement("SELECT id FROM equipes WHERE nome = ?");
            psId.setString(1, nomeEquipe);
            ResultSet rs = psId.executeQuery();
            if (!rs.next()) {
                rs.close();
                psId.close();
                return false;
            }
            int equipeId = rs.getInt("id");
            rs.close();
            psId.close();
            PreparedStatement check = conn.prepareStatement("SELECT 1 FROM equipe_membros WHERE equipe_id = ? AND usuario_login = ?");
            check.setInt(1, equipeId);
            check.setString(2, loginUsuario);
            ResultSet rsCheck = check.executeQuery();
            if (rsCheck.next()) {
                rsCheck.close();
                check.close();
                return false;
            }
            rsCheck.close();
            check.close();
            PreparedStatement psAdd = conn.prepareStatement("INSERT INTO equipe_membros (equipe_id, usuario_login) VALUES (?, ?)");
            psAdd.setInt(1, equipeId);
            psAdd.setString(2, loginUsuario);
            psAdd.executeUpdate();
            psAdd.close();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar membro à equipe: " + e.getMessage());
            return false;
        }
    }
}

package app.app;

public class Usuario {
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String cargo; // usado como grupo
    private String login;
    private String senha;
    private PerfilUsuario perfil;

    public Usuario(String nomeCompleto, String cpf, String email, String cargo, String login, String senha, PerfilUsuario perfil) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public String getNomeCompleto() { return nomeCompleto; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public PerfilUsuario getPerfil() { return perfil; }

    // Adicionado para compatibilidade com DatabaseManager
    public String getGrupo() { return cargo; }

    @Override
    public String toString() {
        return nomeCompleto + " | " + perfil + " | " + email;
    }
}

package app.app;

import java.util.ArrayList;
import java.util.List;

public class Equipe {
    private String nome;
    private String descricao;
    private List<Usuario> membros;

    public Equipe(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public List<Usuario> getMembros() { return membros; }

    public void adicionarMembro(Usuario usuario) {
        if (!membros.contains(usuario)) {
            membros.add(usuario);
        }
    }

    @Override
    public String toString() {
        return nome + " (" + membros.size() + " membros)";
    }
}

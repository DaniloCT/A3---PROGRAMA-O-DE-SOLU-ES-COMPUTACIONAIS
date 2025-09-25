package app.app;

import java.util.Date;
import java.util.List;

public class Projeto {
    private String titulo;
    private String descricao;
    private Date dataInicio;
    private Date dataTerminoPrevista;
    private String status; // planejado, em andamento, concluido, cancelado
    private Usuario gerenteResponsavel;
    private List<Equipe> equipes;
    private String notes; // Adiciona campo notes à classe Projeto

    public Projeto(String titulo, String descricao, Date dataInicio, Date dataTerminoPrevista, String status, Usuario gerenteResponsavel, List<Equipe> equipes, String notes) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataTerminoPrevista = dataTerminoPrevista;
        this.status = status;
        this.gerenteResponsavel = gerenteResponsavel;
        this.equipes = equipes;
        this.notes = notes;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Date getDataInicio() { return dataInicio; }
    public Date getDataTerminoPrevista() { return dataTerminoPrevista; }
    public String getStatus() { return status; }
    public Usuario getGerenteResponsavel() { return gerenteResponsavel; }
    public List<Equipe> getEquipes() { return equipes; }
    public String getNotes() { return notes; } // Getter para notes
    public void setNotes(String notes) { this.notes = notes; } // Setter para notes

    @Override
    public String toString() {
        return titulo + " | " + status;
    }
}

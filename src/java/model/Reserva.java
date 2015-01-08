 package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.primefaces.model.ScheduleEvent;
import util.DateTools;
import util.TratamentoStrings;

@Entity
public class Reserva implements Serializable, ScheduleEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long iid;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private GrupoReserva grupoReserva;

    public GrupoReserva getGrupoReserva() {
        return grupoReserva;
    }
    
    private String styleClass;

    public void setGrupoReserva(GrupoReserva grupoReserva) {
        this.grupoReserva = grupoReserva;
    }

    @OneToOne(cascade = CascadeType.ALL)
    private Emprestimo emprestimo;

    public Emprestimo getEmprestimo() {
        return emprestimo;
    }

    public void setEmprestimo(Emprestimo emprestimo) {
        this.emprestimo = emprestimo;
    }

    private static final long serialVersionUID = 1L;
    
    
    @ManyToOne
    private Operador operador;

    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa reservante;
    @ManyToOne
    private Centro centro;
    String motivo;

    @ManyToMany(fetch = FetchType.EAGER)
    protected List<Recurso> recursos = new ArrayList<Recurso>();

    public List<Recurso> getRecursos() {
        return recursos;
    }

    public void addRecurso(Recurso recurso) {
        this.recursos.add(recurso);
    }

    public void setRecursos(List<Recurso> recursos) {
        this.recursos.clear();
        this.recursos.addAll(recursos);
    }

    public void addAll(List<Recurso> recursos) {
        this.recursos.addAll(recursos);
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        
        motivo = TratamentoStrings.addSlashes(motivo);
        this.motivo = motivo;
    }
    @Transient
    String id = null;

    public Centro getCentro() {
        return centro;
    }

    public void setCentro(Centro centro) {
        this.centro = centro;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    public void limparRecursos() {
        this.recursos.clear();
    }

    public Pessoa getReservante() {
        return reservante;
    }

    public void setReservante(Pessoa reservante) {
        this.reservante = reservante;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }
    
    

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public Date getRealizacao() {
        return realizacao;
    }

    public void setRealizacao(Date realizacao) {
        this.realizacao = realizacao;
    }

    public String getDataInicio() {
        return DateTools.getData(inicio);
    }
    
    @Transient
    public int getDiaSemana(){
        return DateTools.getDiaSemana(inicio);
    }

    public String getDataFim() {
        return DateTools.getData(fim);
    }

    public String getHoraInicio() {
        return DateTools.getHora(inicio);
    }

    public String getHoraFim() {
        return DateTools.getHora(fim);
    }

    public Reserva createClone() {
        Reserva res = new Reserva();
        res.centro = this.centro;
        res.fim = this.fim;
        res.inicio = this.inicio;
        res.fim = this.fim;
        res.realizacao = this.realizacao;
        res.recursos = this.recursos;
        res.reservante = this.reservante;
        res.motivo = this.motivo;
        return res;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicio;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fim;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date realizacao;
    
    

    public Long getIid() {
        return iid;
    }

    public void setIid(Long id) {
        this.iid = id;
    }

    @Override
    public int hashCode() {
        /*int hash = 5;
         hash = 61 * hash + (this.titulo != null ? this.titulo.hashCode() : 0);
         hash = 61 * hash + (this.inicio != null ? this.inicio.hashCode() : 0);
         hash = 61 * hash + (this.fim != null ? this.fim.hashCode() : 0);
         return hash;*/
        int hash = 0;
        hash += (iid != null ? iid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof Reserva)) {
            return false;
        }
        Reserva other = (Reserva) object;
        if ((this.iid == null && other.iid != null) || (this.iid != null && !this.iid.equals(other.iid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return reservante.getNome() + (motivo != null ? (" - " + motivo) : "");
    }

    // Métodos da Interface ScheduleEvent
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String string) {
        id = string;
    }

    @Override
    public Object getData() {
        return realizacao;
    }

    @Override
    public String getTitle() {
        if (recursos != null && !recursos.isEmpty()) {
            String title = " " + reservante.getNome() + (motivo == null || motivo.isEmpty() ? "\n :: nao especificado" : " \n :: " + motivo);

            for (Recurso recursoAssociado : recursos) {
                title += "\n" + recursoAssociado.toString();
            }

            return title;
        }
        return " " + reservante.getNome() + (motivo == null || motivo.isEmpty() ? "\n :: nao especificado" : " \n :: " + motivo);
    }

    @Override
    public Date getStartDate() {
        return inicio;
    }

    @Override
    public Date getEndDate() {
        return fim;
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

    @Override
    public String getStyleClass() {
        return styleClass;
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public void setStyleClass(String styleClass) {

        this.styleClass = styleClass;
    }

    

}

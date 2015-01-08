package model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import util.TratamentoStrings;

@Entity
public class Emprestimo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date retirada;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date devolucao;

    private String responsavelRetirada;

    private String responsavelDevolucao;
    
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa responsavelRecebimento;
    private String observacao;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getRetirada() {
        return retirada;
    }

    public void setRetirada(Date retirada) {
        this.retirada = retirada;
    }

    public Date getDevolucao() {
        return devolucao;
    }

    public void setDevolucao(Date devolucao) {
        this.devolucao = devolucao;
    }

    public String getResponsavelRetirada() {
        return responsavelRetirada;
    }

    public void setResponsavelRetirada(String responsavelRetirada) {
        responsavelRetirada = TratamentoStrings.addSlashes(responsavelRetirada);
        this.responsavelRetirada = responsavelRetirada;
    }

    public String getResponsavelDevolucao() {
        return responsavelDevolucao;
    }

    public void setResponsavelDevolucao(String responsavelDevolucao) {
        responsavelDevolucao = TratamentoStrings.addSlashes(responsavelDevolucao);
        this.responsavelDevolucao = responsavelDevolucao;
    }

    public Pessoa getResponsavelRecebimento() {
        return responsavelRecebimento;
    }

    public void setResponsavelRecebimento(Pessoa responsavelRecebimento) {
        
        this.responsavelRecebimento = responsavelRecebimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        observacao = TratamentoStrings.addSlashes(observacao);
        this.observacao = observacao;
    }

}

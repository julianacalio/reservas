/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author charles
 */
@Entity
public class Emprestimo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    private Date retirada;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date devolucao;
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa responsavelRetirada;
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa responsavelDevolucao;
    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa responsavelRecebimento;

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

    public Pessoa getResponsavelRetirada() {
        return responsavelRetirada;
    }

    public void setResponsavelRetirada(Pessoa responsavelRetirada) {
        this.responsavelRetirada = responsavelRetirada;
    }

    public Pessoa getResponsavelDevolucao() {
        return responsavelDevolucao;
    }

    public void setResponsavelDevolucao(Pessoa responsavelDevolucao) {
        this.responsavelDevolucao = responsavelDevolucao;
    }

    public Pessoa getResponsavelRecebimento() {
        return responsavelRecebimento;
    }

    public void setResponsavelRecebimento(Pessoa responsavelRecebimento) {
        this.responsavelRecebimento = responsavelRecebimento;
    }

    

}

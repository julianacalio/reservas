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
public class ReservaRecurso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Reserva reserva;

    @ManyToOne
    private Recurso recurso;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date retirado;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date devolvido;

    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa retirou;

    @ManyToOne(fetch = FetchType.EAGER)
    private Pessoa devolveu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public Date getRetirado() {
        return retirado;
    }

    public void setRetirado(Date retirado) {
        this.retirado = retirado;
    }

    public Date getDevolvido() {
        return devolvido;
    }

    public void setDevolvido(Date devolvido) {
        this.devolvido = devolvido;
    }

}

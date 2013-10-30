/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 *
 * @author charles
 */
@Entity
@DiscriminatorValue("Sala")
public class Sala extends Recurso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(unique = true)
    private String numero;
    private int tamanho_m2;

    public int getTamanho_m2() {
        return tamanho_m2;
    }

    public void setTamanho_m2(int tamanho_m2) {
        this.tamanho_m2 = tamanho_m2;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sala)) {
            return false;
        }
        Sala other = (Sala) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return numero;
    }
}

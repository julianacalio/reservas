/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author charles
 */
@Entity
@DiscriminatorValue("D")
public class Docente extends Servidor implements Serializable {

    private static final long serialVersionUID = 1L;
   
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "docente")
//    private List<Turma> turmas;
//
//    public List<Turma> getTurmas() {
//        return turmas;
//    }
//
//    public void setTurmas(List<Turma> turmas) {
//        this.turmas = turmas;
//    }

    
    
    
   
    
      
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Docente)) {
            return false;
        }
        Docente other = (Docente) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }
}

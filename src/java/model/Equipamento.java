/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import util.TratamentoStrings;

/**
 *
 * @author charles
 */
@Entity
@DiscriminatorValue("Equipamento")
public class Equipamento extends Recurso implements Serializable {

    private static final long serialVersionUID = 1L;
   
    @Column(unique = true)
    private String patrimonio;

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        
        if(patrimonio.equals("")){
            this.patrimonio = null;
        }
        else{
            this.patrimonio = patrimonio;
        }
    }
    
    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        descricao = TratamentoStrings.addSlashes(descricao);
        this.descricao = descricao;
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
        if (!(object instanceof Equipamento)) {
            return false;
        }
        Equipamento other = (Equipamento) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  descricao ;
    }
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package email;


import java.util.List;
import model.Equipamento;
import model.Recurso;
import model.Reserva;
import model.Sala;
import util.DateTools;

/**
 *
 * @author charles
 */
public class Email implements Runnable {

    Reserva reserva;
    int opcao;

    public Email(Reserva reserva, int opcao) {
        this.reserva = reserva;
        this.opcao = opcao;
    }

    public void enviaMsgReservaFeita(Reserva reserva) {
        SendMail sendMail = new SendMail();
        String remetente = "reservasdesalas.cmcc@gmail.com";
        String destinatario = reserva.getReservante().getEmail();
        String assunto = "Confirmação de Reserva";
        String msgReservaFeita = "Prezado(a) " + reserva.getReservante().getNome()
                + "  <br/><br/>Reservamos "
                + getDescricaoRecursos(reserva.getRecursos())
                + " para o dia " + DateTools.getData(reserva.getInicio())
                + " no período das " + DateTools.getHora(reserva.getInicio())
                + " às " + DateTools.getHora(reserva.getFim()) + "<br/><br/>Atenciosamente<br/>"
                + "Divisão Acadêmica do CMCC<br/>(11)4996-7950 ";
        
        
        int diaSemana = DateTools.getDiaSemana(reserva.getInicio());
        int hora = DateTools.getHoras(reserva.getInicio());
        List<Recurso> recursos = reserva.getRecursos();
        
        if(diaSemana == 7 || diaSemana == 1 || hora < 9 || hora > 18){
            
            msgReservaFeita += "<br/><br/><br/><b><FONT COLOR=\"red\">Obs: O horário de atendimento da Divisão Acadêmica é de segunda a sexta das"
                    + " 9h às 18h. Para reservas fora desse período, por favor, retirar a chave da sala no setor"
                    + " de segurança do respectivo bloco. </FONT></b>";
            
            if(recursos.size() > 1){
            msgReservaFeita += "<b><FONT COLOR=\"red\">O(s) equipamento(s) deve(m) ser retirado(s) no"
                    + " horário de funcionamento da Divisão Acadêmica</FONT></b>";
            }
            
        }
        
        

        sendMail.sendMail(remetente, destinatario, assunto, msgReservaFeita);
        
        
        
    }

    public void enviaMsgReservaFeita(List<Reserva> reserva) {

    }

    public void enviaMsgRecursoPego(Reserva reserva) {
        SendMail sendMail = new SendMail();
        String remetente = "reservasdesalas.cmcc@gmail.com";
        String destinatario = reserva.getReservante().getEmail();
        String assunto = "Confirmação de retirada";
        String msgRecursoPego = "Prezado(a) " + reserva.getReservante().getNome()
                + "  \n\n\nInformamos que o sr(sra) " + reserva.getEmprestimo().getResponsavelRetirada()
                + " retirou o(s) item(s) "
                + getDescricaoRecursosRetirados(reserva.getRecursos())
                + " em " + DateTools.getData(reserva.getEmprestimo().getRetirada())
                + " às " + DateTools.getHora(reserva.getEmprestimo().getRetirada())
                + "\n\nAtenciosamente\n"
                + "Divisão Acadêmica do CMCC\n(11)4996-7950 ";
        
        
        

        sendMail.sendMail(remetente, destinatario, assunto, msgRecursoPego);
    }

    public void enviaMsgRecursoDevolvido(Reserva reserva) {
        SendMail sendMail = new SendMail();
        String remetente = "reservasdesalas.cmcc@gmail.com";
        String destinatario = reserva.getReservante().getEmail();
        String assunto = "Confirmação de devolução";
        String msgRecursoDevolvido = "Prezado(a) " + reserva.getReservante().getNome()
                + "  \n\n\nAtestamos a devolução do(s) item(s) "
                + getDescricaoRecursosRetirados(reserva.getRecursos())
                + " em " + DateTools.getData(reserva.getEmprestimo().getDevolucao())
                + " às " + DateTools.getHora(reserva.getEmprestimo().getDevolucao())
                + "\n\nAtenciosamente\n"
                + "Divisão Acadêmica do CMCC\n(11)4996-7950 ";

        sendMail.sendMail(remetente, destinatario, assunto, msgRecursoDevolvido);
    }

    private String getDescricaoRecursos(List<Recurso> recursos) {
        String descricaoRecursos = "";
        for (Recurso recurso : recursos) {
            if (recurso instanceof Sala) {
                Sala sala = (Sala) recurso;
                descricaoRecursos += "a sala " + sala.getNumero() + ", ";
            } else {
                Equipamento equipamento = (Equipamento) recurso;
                descricaoRecursos += "o equipamento " + equipamento.getDescricao() + ", ";
            }
        }

        return descricaoRecursos;
    }

    private String getDescricaoRecursosRetirados(List<Recurso> recursos) {
        String descricaoRecursos = "";
        for (Recurso recurso : recursos) {
            if (recurso instanceof Sala) {
                Sala sala = (Sala) recurso;
                descricaoRecursos += "chave da sala " + sala.getNumero() + ", ";
            } else {
                Equipamento equipamento = (Equipamento) recurso;
                descricaoRecursos += "equipamento " + equipamento.getDescricao() + ", ";
            }
        }

        return descricaoRecursos;
    }

    @Override
    public void run() {
        switch (opcao) {
            case 1:
                enviaMsgReservaFeita(this.reserva);
                break;
            case 2:
                enviaMsgRecursoPego(this.reserva);
                break;
            case 3:
                enviaMsgRecursoDevolvido(this.reserva);
                break;
            default:
                break;

        }
    }

    

}

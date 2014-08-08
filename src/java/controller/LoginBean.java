package controller;

import static controller.HibernateUtil.getSessionFactory;
import facade.UsuarioFacade;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import model.Usuario;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.primefaces.context.RequestContext;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    @EJB
    private UsuarioFacade usuarioFacade;
    
    
    private String username;
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String password;
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = convertStringToMd5(password);
    }
    
    private boolean loggedIn;
    
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    private Usuario usuarioLogado;

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }
    
    private int indicadorSenha;
    

    /**
     * Aplica o algoritmo de criptografia MD5 a uma String
     * @param valor
     * @return 
     */
    public  static String convertStringToMd5(String valor) {
        
        MessageDigest mDigest;
        
        valor = valor.trim();
        
        try {
            mDigest = MessageDigest.getInstance("MD5"); //Convert a String valor para um array de bytes em MD5 
            byte[] valorMD5 = mDigest.digest(valor.getBytes("UTF-8")); //Convertemos os bytes para hexadecimal, assim podemos salvar 
            //no banco para posterior comparação se senhas 
            StringBuilder sb = new StringBuilder();
            for (byte b : valorMD5) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {             
            return null;
        } catch (UnsupportedEncodingException e) {             
            return null;
        }
    }
    
    
    // Verifica se usuário existe ou se pode logar 
    public Usuario isUsuarioReadyToLogin(String login, String senha) {


            try {
                Session session = getSessionFactory().openSession();
                Query query = session.createQuery("from Usuario u where u.login = :login and u.senha = :senha ");
                query.setParameter("login", login);
                query.setParameter("senha", senha);
                List resultado = query.list();

                if (resultado.size() == 1) {
                    Usuario userFound = (Usuario) resultado.get(0);
                    return userFound;
                } else {
                    return null;
                }
            } catch (HibernateException e) {
                return null;
            }

    }
    
    //Realiza o login caso de tudo certo 
    public void doLogin() {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg;
        loggedIn = false;
        
        this.username = this.username.toLowerCase().trim();
        this.password = this.password.trim();

        //Verifica se o e-mail e senha existem e se o usuario pode logar 
        Usuario usuarioFound = (Usuario) isUsuarioReadyToLogin(this.username, this.password);

        if (usuarioFound == null) {
            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
        } else {

            loggedIn = true;
            usuarioLogado = usuarioFound;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem-vindo(a)!", this.usuarioLogado.getLogin());
            
            //Verifica se usuário também pode cadastrar outros usuários
//            if(this.username.equals("juliana@ufabc")){
//                this.podeCadastrar = true;
//            }
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);

      
    }
    
    public void saveNewPassword(String oldPassword, String newPassword, String newPassword2){
        
        //RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg;
        
        //oldPassword = convertStringToMd5(oldPassword);
        
//        if(!(this.password.equals(oldPassword))){
//            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Senha antiga não corresponde ", "Tente novamente");
//            this.indicadorSenha = 0;
//        }
//        else{
//            if(!(newPassword.equals(newPassword2))){
//                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Novas senhas não correspondem ", "Tente novamente");
//                this.indicadorSenha = 0;
//            }
//            else{
                this.usuarioLogado.setSenha(newPassword);
                usuarioFacade.edit(usuarioLogado);
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Senha alterada com sucesso!", this.usuarioLogado.getNome());
                this.indicadorSenha = 1;
//            }
            
//        }
        
        FacesContext.getCurrentInstance().addMessage(null, msg);
        
    }
    public String pageSenha(){
        
        if(this.indicadorSenha == 1){
            return "/index";
        }
        else{
            return "/view/usuario/Edit";
        }
        
    }

    public String page() {
        if (loggedIn) {
            return "/Calendario";
        } else {
            return "/login";
        }
    }

    public void isLogado() {

        //loggedIn = true;
        
        if (!loggedIn) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml?faces-redirect=true");
            } catch (IOException ex) {
                Logger.getLogger(CalendarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    
//    
    
//    private boolean podeCadastrar;
//
//    private Servidor usuarioLogado;

//    public boolean isPodeCadastrar() {
//        return podeCadastrar;
//    }
//
//    public void setPodeCadastrar(boolean podeCadastrar) {
//        this.podeCadastrar = podeCadastrar;
//    }

     
    
    //Realiza o login caso de tudo certo 
    public void doLogout() {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg;
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Logout efetuado", this.usuarioLogado.getNome());
        loggedIn = false;      
        username = "";
        password = "";
//        podeCadastrar = false;
        usuarioLogado = null;

        FacesContext.getCurrentInstance().addMessage(null, msg);
       
        try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("http://localhost:8081/ReservasJboss/faces/login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(CalendarioController.class.getName()).log(Level.SEVERE, null, ex);
            }

    }
    
 
//    public String page() {
//        if (loggedIn) {
//            return "/index";
//        } else {
//            return "/login";
//        }
//    }
    
    public String page2() {
        if (loggedIn) {
            return "/view/usuario/Create";
        } else {
            return "/login";
        }
    }

   
    
    
    
    
}

package controller;

import facade.UsuarioFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import static javax.naming.Context.INITIAL_CONTEXT_FACTORY;
import static javax.naming.Context.PROVIDER_URL;
import static javax.naming.Context.SECURITY_AUTHENTICATION;
import static javax.naming.Context.SECURITY_CREDENTIALS;
import static javax.naming.Context.SECURITY_PRINCIPAL;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import model.Usuario;
import org.primefaces.context.RequestContext;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    @EJB
    private UsuarioFacade usuarioFacade;

    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    

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
        this.password = password;
    }

    private boolean loggedIn;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    private String nome;

    //Realiza o login caso de tudo certo 
    public void doLogin() {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg;
        loggedIn = false;

        this.username = this.username.trim();
        this.password = this.password.trim();

        LDAP ldap = new LDAP();

        // Create the initial context
        DirContext ctx = ldap.authenticate(username, password);

        if (ctx != null) {

            try {
                Attributes attrs = ctx.getAttributes(ldap.makeDomainName(username));
                nome = (String) attrs.get("cn").get();
                ctx.close();
            } catch (NamingException ex) {
                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            }

            usuario = usuarioFacade.findByLogin(username);

            if (usuario != null) {
                loggedIn = true;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem-vindo(a)!", nome);
            }
            else{
                loggedIn = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
            }

        } else {

            loggedIn = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("loggedIn", loggedIn);

    }

    public String page() {
        if (loggedIn) {
            return "/Calendario";
        } else {
            return "/login";
        }
    }

    public void isLogado() {

        if (!loggedIn) {
            try {
                
                FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml?faces-redirect=true");
            } catch (IOException ex) {
                Logger.getLogger(CalendarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String doLogout() {

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg;
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Logout efetuado", nome);
        loggedIn = false;
        username = "";
        password = "";
        usuario = null;

        FacesContext.getCurrentInstance().addMessage(null, msg);
//        
//
//        try {
//            FacesContext.getCurrentInstance().getExternalContext().redirect("/login.xhtml?faces-redirect=true");
////            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
////        ec.redirect(ec.getRequestContextPath() + "/" + "login.xhtml");
//        return;
//        } catch (IOException ex) {
//            Logger.getLogger(CalendarioController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        return "/login";

    }

    //Classe que contém as funcionalidades do LDAP
    class LDAP {

        private final Properties properties;

        public LDAP() {
            properties = new Properties();
            properties.put(INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            properties.put(PROVIDER_URL, "ldap://openldap.ufabc.int.br:389");
            properties.put(SECURITY_AUTHENTICATION, "simple");
        }

        public LdapContext authenticate(String user, String pw) {
            setUser(user);
            setPassword(pw);

            return getContextoLDAP();
        }

        private String makeDomainName(String user) {
            user = user.replaceAll("[\\\\\\']", "");
            return String.format("uid=%s,ou=users,dc=ufabc,dc=edu,dc=br", user);
        }

        private void setUser(String user) {
            String domainName = makeDomainName(user);
            properties.put(SECURITY_PRINCIPAL, domainName);
        }

        private void setPassword(String pw) {
            properties.put(SECURITY_CREDENTIALS, pw);
        }

        private LdapContext getContextoLDAP() {
            try {
                return new InitialLdapContext(properties, null);
            } catch (NamingException ex) {
                //System.err.println("ERROR: " + ex.getMessage());
                return null;
            }
        }

        public String getUID(String nome) {

            DirContext contexto = getContextoLDAP();

            Attributes atributos = new BasicAttributes(true);
            atributos.put(new BasicAttribute("cn"));
            String filter = "cn=" + nome;
            NamingEnumeration answer;
            try {
                answer = contexto.search("ou=users,dc=ufabc,dc=edu,dc=br", filter, null);
                SearchResult sr = (SearchResult) answer.next();
                return sr.getName().substring(4);
            } catch (NamingException ex) {
                Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
            }

//        System.out.println("Result: " + sr.getName().substring(4));
//        return "";
            return null;
        }

    }

}

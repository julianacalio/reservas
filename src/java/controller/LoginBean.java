package controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

@Named("loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String username;

    private String password;

    boolean loggedIn;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void login(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        loggedIn = false;

        if (username != null && username.equals("elaine") && password != null && password.equals("elaine.konno")) {
            loggedIn = true;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome", username);

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
            return "";
        }
    }

    public void isLogado() {

        loggedIn = true;
        
        if (!loggedIn) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml?faces-redirect=true");
            } catch (IOException ex) {
                Logger.getLogger(CalendarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}

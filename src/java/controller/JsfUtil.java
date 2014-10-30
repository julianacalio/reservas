package controller;


import static controller.JsfUtil.addErrorMessage;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

public class JsfUtil {

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem("", "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg, null);
        } else {
            addErrorMessage(defaultMsg, null);
        }
    }

    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message, null);
        }
    }

    public static void addErrorMessage(String msg, String msgComplement) {
//        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msgComplement);
//        
//        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "errorInfo:", msg));
    }

    public static void addSuccessMessage(String msg, String msgComplement) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msgComplement);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }
    
    public static void addSuccessMessage(String msg) {
    
//        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
//        FacesContext.getCurrentInstance().addMessage("sucessInfo", facesMsg);
          
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "successInfo", msg));
        
    }
    
    
}
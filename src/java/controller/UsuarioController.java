package controller;

import controller.LoginBean.LDAP;
import facade.UsuarioFacade;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import model.Usuario;
import util.UsuarioDataModel;

@Named(value = "usuarioController")
@SessionScoped
public class UsuarioController implements Serializable {

    public UsuarioController() {
//        usuario = new Usuario();

    }

    //Guarda o usuario atual
    private Usuario usuario;

    @EJB
    private UsuarioFacade usuarioFacade;
    private UsuarioDataModel usuarioDataModel;

    private int selectedItemIndex;

    private boolean loginManual;

    public boolean isLoginManual() {
        return loginManual;
    }

    public void setLoginManual(boolean loginManual) {
        this.loginManual = loginManual;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    private Usuario getUsuario(Long key) {
        return this.buscar(key);

    }

    public UsuarioDataModel getUsuarioDataModel() {
        if (usuarioDataModel == null) {
            List<Usuario> usuarios = this.listarTodas();
            usuarioDataModel = new UsuarioDataModel(usuarios);
        }
        return usuarioDataModel;
    }

    public void setUsuarioDataModel(UsuarioDataModel usuarioDataModel) {
        this.usuarioDataModel = usuarioDataModel;
    }

    private List<Usuario> listarTodas() {
        return usuarioFacade.findAll();

    }

    public void recriarModelo() {
        this.usuarioDataModel = null;
    }

    public SelectItem[] getItemsAvaiableSelectOne() {
        return JsfUtil.getSelectItems(usuarioFacade.findAll(), true);
    }

//    public Usuario getUsuario() {
//        if (usuario == null) {
//            usuario = new Usuario();
//        }
//        return usuario;
//    }
    public Usuario getSelected() {
        if (usuario == null) {
            usuario = new Usuario();
            selectedItemIndex = -1;
        }
        return usuario;
    }

//    public String ehAdm(){
//        return usuario.isAdm() ? "Sim" : "Não"; 
//    }
//    public String prepareCreate(int i) {
//        usuario = new Usuario();
//        if(i == 1){
//            return "/view/usuario/Create";
//        }
//        else{
//        return "Create";
//        }
//    }
    public String prepareList() {

        this.loginManual = false;
        this.usuarioDataModel = null;
        this.usuario = null;
        return "/view/usuario/List";
    }

    public void getLoginLDAP() {

        LDAP ldap = new LoginBean().new LDAP();
        this.usuario.setLogin(ldap.getUID(usuario.getNome()));

    }

    public void salvarNoBanco() {
        

        try {

            LDAP ldap = new LoginBean().new LDAP();
//            usuario = new Usuario();

            if (this.loginManual) {

            } else {
                
                String email = usuario.getTa().getEmail();
                if(email.contains("ufabc") || email.contains("aluno.ufabc")){
                    usuario.setLogin(email.substring(0, email.lastIndexOf("@")));
                }
                else{
//                    login = ldap.getUID(usuario.getNome());
                usuario.setLogin(ldap.getUID(usuario.getNome()));
                }
                
                
            }
            
//            usuario.setLogin(login);
            usuarioFacade.save(usuario);
            JsfUtil.addSuccessMessage("Usuario " + usuario.getLogin() + " criado com sucesso!");
            usuario = null;
            this.loginManual = false;
            recriarModelo();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Ocorreu um erro, insira o login manualmente");
            this.loginManual = true;
            usuario.setLogin("Insira o login");

        }

    }

    public void cancelarSalvar() {

        this.loginManual = false;
        this.usuario = null;

    }

    public Usuario buscar(Long id) {

        return usuarioFacade.find(id);
    }

    public String index() {
        usuario = null;
        usuarioDataModel = null;
        return "/index";
    }

    public void editar() {
        try {
            usuarioFacade.edit(usuario);
            JsfUtil.addSuccessMessage("Usuario Editado com sucesso!");
            usuario = null;
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Ocorreu um erro de persistência, não foi possível editar o usuario: " + e.getMessage());

        }
    }

    public void delete() {
        usuario = (Usuario) usuarioDataModel.getRowData();
        try {
            usuarioFacade.remove(usuario);
            usuario = null;
            JsfUtil.addSuccessMessage("Usuario Deletado");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Ocorreu um erro de persistência" + e.getMessage());
        }

        recriarModelo();
    }

    public String prepareEdit() {
        usuario = (Usuario) usuarioDataModel.getRowData();
        return "Edit";
    }

    public String prepareView() {
        usuario = (Usuario) usuarioDataModel.getRowData();
        //docente = usuarioFacade.find(usuario.getID());
        //docenteFacade.edit(usuarioFacade.find(usuario.getID()));
        //docenteFacade.edit(usuario);
        return "View";
    }

//    public String prepareList() {
//        
//
//        return "view/usuario/List";
//
//    }
    public String prepareListAutorizada() {
        this.usuarioDataModel = null;
        return "view/usuario/ListAutorizada";

    }

//    public String prepareCreate(boolean podeCriar){
//        
//        if(podeCriar){
//            return "/view/usuario/Create";
//        }
//        else{
//            return "/login";
//        }
//    }
    public String prepareCreate() {

        return "/view/usuario/Create";

    }

    @FacesConverter(forClass = Usuario.class)
    public static class UsuarioControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsuarioController controller = (UsuarioController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usuarioController");
            return controller.getUsuario(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Usuario) {
                Usuario d = (Usuario) object;
                return getStringKey(d.getID());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Usuario.class.getName());
            }
        }
    }

}

package pagecode.apps.login;

import java.io.Serializable;
import java.security.MessageDigest;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.context.FacesContext;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.LogService;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.usuarios.entity.helpers.UsuarioTO;
import ve.gob.onapre.comun.usuarios.session.ServiciosUsuariosLocal;
import atributes.SessionComunAtributes;

@ManagedBean
@RequestScoped
public class CambioClaveBean extends PageCodeBase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosUsuarios)
	private ServiciosUsuariosLocal serviciosUsuariosLocal;
	private UsuarioTO usuarioTOLogeado;

	@PostConstruct
	public void init(){
		usuarioTOLogeado = (UsuarioTO)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(SessionComunAtributes.LOGIN_USUARIO_LOGEADO);
	}

	public UsuarioTO getUsuarioTOLogeado(){
		return usuarioTOLogeado;
	}

	
	public String doAceptar(){
		String outcome = "";
		try{
			HtmlInputSecret passwordFieldHtmlInputSecret = (HtmlInputSecret)findComponentInRoot("clave");
			HtmlInputSecret passwordConfirmarFieldHtmlInputSecret = (HtmlInputSecret)findComponentInRoot("claveConfirmar");
			String passwordField = (String)passwordFieldHtmlInputSecret.getValue();
			String passwordConfirmarField = (String)passwordConfirmarFieldHtmlInputSecret.getValue();
			if(passwordField == null || passwordField.trim().equals("") || passwordConfirmarField == null || passwordConfirmarField.trim().equals("")){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas","Falta contraseña o confirmación de contraseña "));
			}else{
				boolean sw = false;
		        if(!passwordField.equals(passwordConfirmarField)){
		        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","No coinciden las contraseñas"));
		        	sw = true;
		        }
				
				if(!sw){
			        if (getUsuarioTOLogeado().getPassword_md5()!=null && Compare_Passwords_MD5(getUsuarioTOLogeado().getPassword_md5(),Convert_Password_MD5_to_String(Convert_Password_to_MD5(passwordConfirmarField)))){
			        	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","La Nueva Clave no puede ser igual a la Anterior"));
			        	sw = true;
			        }
			      }
				
				if(!sw){
					//System.out.println("MD5 "+Convert_Password_MD5_to_String(Convert_Password_to_MD5(passwordField)));
					
					boolean resultado = serviciosUsuariosLocal.guardarCambioClave(getUsuarioTOLogeado().getUsername(), Convert_Password_MD5_to_String(Convert_Password_to_MD5(getUsuarioTOLogeado().getPassword())), Convert_Password_MD5_to_String(Convert_Password_to_MD5(passwordField)));
					if(resultado){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Satisfactorio","Exito"));
					}else{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","Problema al cambiar la contraseña"));
					}
				}
				
			}
			
		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error","Problema al cambiar la contraseña"));
			LogService.logger.error("Ha ocurrido una exepcion", e);
		}
		return outcome;
	}
	
    public String Convert_Password_MD5_to_String( byte[] md5Bytes ){
        StringBuffer hexValue = new StringBuffer();
        
        for (int i=0; i<md5Bytes.length; i++){
            int val = ((int)md5Bytes[i]) & 0xff;
            if (val<16) hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    public byte[] Convert_Password_to_MD5(String password){
        byte[] md5Bytes = null;
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            
            char[] passwordArreglo = password.toCharArray();           
            byte[] ArregloMD5 = new byte[passwordArreglo.length];
            for (int i=0; i<passwordArreglo.length; i++)
                ArregloMD5[i] = (byte) passwordArreglo[i];
            
            md5Bytes = md5.digest(ArregloMD5);
            
            return md5Bytes;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return md5Bytes;
    }
    
    @SuppressWarnings("static-access")
	public boolean Compare_Passwords_MD5(String password_old, String password_new){
        boolean result=false;
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            
            result = md5.isEqual(Convert_Password_to_MD5(password_old),Convert_Password_to_MD5(password_new));
            
        }catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	public String doLogin(){
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "/login/login.xhtml?faces-redirect=true";
	}

}

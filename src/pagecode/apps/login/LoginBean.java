package pagecode.apps.login;

import java.io.Serializable;
import java.util.ArrayList;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.myfaces.custom.captcha.CAPTCHAComponent;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.LogService;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.organoentereceptor.entity.helpers.OrganoEnteReceptorTO;
import ve.gob.onapre.comun.organoentereceptor.session.ServiciosOrganoEnteReceptorLocal;
import ve.gob.onapre.comun.usuarios.entity.helpers.UsuarioTO;
import ve.gob.onapre.comun.usuarios.session.ServiciosUsuariosLocal;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.session.ServiciosEstructuraPresupuestariaLocal;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;

@ManagedBean
public class LoginBean extends PageCodeBase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosUsuarios)
	private ServiciosUsuariosLocal serviciosUsuariosLocal;
	private ArrayList<OrganoEnteReceptorTO> listaOrganos;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosOrganoEnteReceptor)
	private ServiciosOrganoEnteReceptorLocal serviciosOrganoEnteReceptorLocal;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosEstructuraPresupuestaria)
	private ServiciosEstructuraPresupuestariaLocal serviciosEstructuraPresupuestariaLocal;
	//private Integer anhoPresupuesto;
	public final static String SESSION_KEY_NAME = "captcha";
	private String value;
	private CAPTCHAComponent captchaComponent; 

	public String doAceptar(){
		String outcome = "";
		String captchaValue = getCaptchaComponent().getCaptchaSessionValue();

		HtmlInputText loginField = (HtmlInputText)findComponentInRoot("usuario");
		HtmlInputSecret passwordField = (HtmlInputSecret)findComponentInRoot("clave");
		
		String userName = (String)loginField.getValue();
		String userPassword = (String)passwordField.getValue();
		boolean sw = false;
		if(userName == null){
			FacesContext.getCurrentInstance().addMessage("mensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas","Debe indicar usuario"));
			sw = true;
		}
		if(userPassword == null){
			FacesContext.getCurrentInstance().addMessage("mensaje", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas","Debe indicar la clave"));
			sw = true;
		}
		
		if(!sw){

		
		if(captchaValue.equalsIgnoreCase(getValue())) {

				HttpServletRequest httpServletRequest = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
				try {
					httpServletRequest.login(userName, userPassword);
					UsuarioTO usuarioTO = serviciosUsuariosLocal.findUsuarioById(userName);
					usuarioTO.setPassword(userPassword);
					FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.LOGIN_USUARIO_LOGEADO,usuarioTO);
					LogService.logger.info("userName "+userName);

					//if(usuarioTO.getTipo().equals("O")){
						if(userPassword.equals("onapre2007") || userPassword.equals("onapre123") || userPassword.equals("ONAPRE2007")){
							outcome = "/apps/menu/cambio_clave.xhtml?faces-redirect=true";
						}else{
							ArrayList<OrganoEnteReceptorTO> organos = getListaOrganos(userName);
							if(!organos.isEmpty()){
								LogService.logger.info("organos.size() "+organos.size());

								if(organos.size() > 1){
									outcome = "/apps/seleccion/seleccion_ente.xhtml?faces-redirect=true";									
								}else{
									int anhoPresupuesto = (Integer)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.ANHO_PRESUPUESTO);
									OrganoEnteReceptorTO organoEnteReceptorTO = (OrganoEnteReceptorTO)organos.get(0);
									EstructuraPresupuestariaShortTO estructuraTO = serviciosEstructuraPresupuestariaLocal.getEstructuraPresupuestariaShortByOrganoByAnhoByFase(organoEnteReceptorTO.getId(), anhoPresupuesto , EstructuraPresupuestariaTO.FASE_PROYECTO);
									estructuraTO.setOrganoEnteReceptorTO(organoEnteReceptorTO);
									FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraTO);
									OrganoEnteReceptorTO to = (OrganoEnteReceptorTO)organos.get(0);
									LogService.logger.info("OrganoEnteReceptorTO "+to.getCodigoDenominacion());
									FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ORGANO_ACTUAL, to);
									outcome = "/apps/menu/menu.xhtml?faces-redirect=true";
								}
							}else{
								outcome = "/error/error_organismo.xhtml?faces-redirect=true";
							}
						}					
				
				} catch (ServletException e) {
					e.printStackTrace();
					FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas","Usuario y/o Password invalidos"));
				}
			}else{
				value = "";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Login","No coincide el texto"));
			}
		}
		return outcome;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<OrganoEnteReceptorTO> getListaOrganos(String username){
		if(listaOrganos == null){
			listaOrganos = serviciosOrganoEnteReceptorLocal.findMinisterioYEntesByUserName(username);
		}
		return listaOrganos;
	}

	 
    public void submit() {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Correct", "Correct");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getSessionKeyName() { 
    	return SESSION_KEY_NAME; 
    	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
	
	public CAPTCHAComponent getCaptchaComponent(){
		if(captchaComponent == null){
			captchaComponent = (CAPTCHAComponent)findComponentInRoot("captchaComponent");
		}
		return captchaComponent;
	}
}

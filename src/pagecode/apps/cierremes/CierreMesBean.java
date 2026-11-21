package pagecode.apps.cierremes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.context.RequestContext;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.CoDatabaseManager;
import ve.gob.onapre.apps.sispre.comunes.LogService;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.fuentefinanciamiento.session.ServiciosFuenteFinanciamientoLocal;
import ve.gob.onapre.sis_ejec.saldoInicialSession.ServicioSaldoInicialCajaLocal;
import ve.gob.onapre.sis_ejec.situacion_mensual.session.ServiciosSituacionMensualLocal;
import ve.gob.onapre.sispre.creditopresupuestarios.tIII.session.ServiciosCreditoPresupuestarioTIIILocal;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.session.ServiciosEstructuraPresupuestariaLocal;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;


@ManagedBean
@ViewScoped
@SuppressWarnings("rawtypes")
public class CierreMesBean extends PageCodeBase implements Serializable{

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB(mappedName = LookUpNames.SESSION_ServiciosEstructuraPresupuestaria)
	private ServiciosEstructuraPresupuestariaLocal serviciosEstructuraPresupuestaria;

	@EJB(mappedName = LookUpNames.SESSION_ServiciosFuenteFinanciamiento)
	private ServiciosFuenteFinanciamientoLocal serviciosFuenteFinanciamiento;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosCreditoPresupuestarioTIII)
	private ServiciosCreditoPresupuestarioTIIILocal serviciosCreditoPresupuestarioTIII;
	
	@EJB(mappedName = LookUpNames.SESSION_ServicioSaldoInicialCaja)
	private ServicioSaldoInicialCajaLocal servicioSaldoInicialCaja;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosSituacionMensual)
	private ServiciosSituacionMensualLocal serviciosSituacionMensualLocal;
	
	@Resource(mappedName = CoDatabaseManager.ONAPRE)
	private DataSource jdbcFactory;
	protected HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	protected String dir_Url = httpServletRequest.getRequestURL().toString();
	protected String dir_UrI = httpServletRequest.getRequestURI();
	private EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;		
	private ArrayList anhosPresupuesto;
	private int anhoSeleccionado;	
	private int mesSeleccionado;
	private String messageError="";
	private boolean  bloquearBotonGuardar=false;
	private CommandButton guardarButton;
	
	@PostConstruct
	public void init(){
		
		estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
		anhosPresupuesto = (ArrayList) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_ANHOS_PRESUPUESTO);			
	}	
	
	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO() {
		return estructuraPresupuestariaSessionTO;
	}
	
	public String doGuardarCierreMes(){		
		try{
			if( getMesSeleccionado()!=-1 && getAnhoSeleccionado()!=-1 && getMesSeleccionado()!=0 && getAnhoSeleccionado()!=0){
				if(!bloquearBotonGuardar){
					FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Advertencia:",messageError));
					setMessageError("");	
				}else{
					if(servicioSaldoInicialCaja.findAllVerifyCierreMes(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), getAnhoSeleccionado(), getMesSeleccionado()))
					{
						servicioSaldoInicialCaja.cierreMes(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo() , getAnhoSeleccionado(), getMesSeleccionado());			
						FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_INFO,"Mensaje:","Se Guardo Satisfactoriamente."));
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Este Mes ya fue Cerrado."));
					}										
				}
			}
		}catch(Exception e){
			e.printStackTrace();
	 		LogService.logger.error("Ha ocurrido una excepcion en doGuardar", e);
		}					
		return "/apps/cierre_mes/cierre_mes.xhtml?faces-redirect=true";
	}
	
	public String doRegresar(){
		return "/apps/cierre_mes/cierre_mes.xhtml?faces-redirect=true";
	}
	
	public void limpiaArreglos(){
		doBloquearGuardar();
		doMessageError();
		setMessageError("");
	}
	
	public void cerrarGuardar() {
        Map<String,Object> options = new HashMap<String, Object>();
        options.put("resizable", false);
        RequestContext.getCurrentInstance().openDialog("cerrarGuardar", options, null);
    }
	
	public ArrayList getAnhosPresupuesto() {
		return anhosPresupuesto;
	}

	public void setAnhosPresupuesto(ArrayList anhosPresupuesto) {
		this.anhosPresupuesto = anhosPresupuesto;
	}

	public int getAnhoSeleccionado() {
		return anhoSeleccionado;
	}

	public void setAnhoSeleccionado(int anhoSeleccionado) {
		this.anhoSeleccionado = anhoSeleccionado;
	}

	public int getMesSeleccionado() {
		return mesSeleccionado;
	}

	public void setMesSeleccionado(int mesSeleccionado) {
		this.mesSeleccionado = mesSeleccionado;
	}
	public String doBloquearGuardar() {	
		String estado="none";					
		if( getMesSeleccionado()!=-1 && getAnhoSeleccionado()!=-1 && getMesSeleccionado()!=0 && getAnhoSeleccionado()!=0){
			setBloquearBotonGuardar(servicioSaldoInicialCaja.findAllVerifyCierreMes(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), getAnhoSeleccionado(), getMesSeleccionado()));
			if(!isBloquearBotonGuardar()){				
				setMessageError("Este Mes ya Fue Cerrado.");		
				getGuardarButton().setDisabled(true);
			}else{				
				setMessageError("");	
				estado= "block";
				}			
		}
		return estado;			
	}
	
	public CommandButton getGuardarButton(){
		guardarButton = (CommandButton)findComponentInRoot("guardarField1");
		return guardarButton;
	}
	
	public void doMessageError() {
		if(!getMessageError().equals("")){
			FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Advertencia:",messageError));
			setMessageError("");
		}
	}

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	public boolean isBloquearBotonGuardar() {
		return bloquearBotonGuardar;
	}

	public void setBloquearBotonGuardar(boolean bloquearBotonGuardar) {
		this.bloquearBotonGuardar = bloquearBotonGuardar;
	}
}

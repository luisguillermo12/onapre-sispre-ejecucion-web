package pagecode.apps.sis_ejec.saldo_inicial;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

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
import org.primefaces.component.inputtext.InputText;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.CoDatabaseManager;
import ve.gob.onapre.apps.sispre.comunes.LogService;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.fuentefinanciamiento.session.ServiciosFuenteFinanciamientoLocal;
import ve.gob.onapre.sis_ejec.caja_inicial_ejec.entity.SaldoCajaInicial;
import ve.gob.onapre.sis_ejec.saldoInicialSession.ServicioSaldoInicialCajaLocal;
import ve.gob.onapre.sispre.creditopresupuestarios.tIII.session.ServiciosCreditoPresupuestarioTIIILocal;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.session.ServiciosEstructuraPresupuestariaLocal;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;


@ManagedBean
@ViewScoped
@SuppressWarnings("rawtypes")
public class SaldoInicialCajaBean extends PageCodeBase implements Serializable{

	/**
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
	
	@Resource(mappedName = CoDatabaseManager.ONAPRE)
	private DataSource jdbcFactory;
	protected HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	protected String dir_Url = httpServletRequest.getRequestURL().toString();
	protected String dir_UrI = httpServletRequest.getRequestURI();
	private EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;	
	private ArrayList listaSaldo;
	private DecimalFormat dff = new DecimalFormat(); 	
	private DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
	private ArrayList anhosPresupuesto;
	private int anhoSeleccionado;
	private InputText montoCajaField;
	private CommandButton guardarButton;
	
	@PostConstruct
	public void init(){
		
		estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);		
		anhosPresupuesto = (ArrayList) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_ANHOS_PRESUPUESTO);			
		
		newSymbols.setGroupingSeparator('.');
		newSymbols.setGroupingSeparator(',');
	    dff.setDecimalFormatSymbols(newSymbols); 
	    dff.setDecimalSeparatorAlwaysShown(false);
	}	

	@SuppressWarnings("unchecked")
	public ArrayList<SaldoCajaInicial> getSaldoInicialCaja(){		
		if(listaSaldo == null){
			//System.out.println("Codigo-->>"+getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo());
			listaSaldo = servicioSaldoInicialCaja.findSaldoInicialCaja(getAnhoSeleccionado(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo());				
		}	
		return listaSaldo;
	}
	
	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO() {
		return estructuraPresupuestariaSessionTO;
	}
	
	public String  doGuardar(){
		try{
			if(getAnhoSeleccionado()==-1){
				//getGuardarButton().setDisabled(true);
				limpiaArreglos();								
				FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_WARN,"Error:","Debe Seleccionar el Año."));
				//System.out.println("El Año del SALDO INICIAL en el if de verificar "+getAnhoSeleccionado());
			}
			else
			{
			String resultado = servicioSaldoInicialCaja.guardarSaldoCajaInicial(getSaldoInicialCaja(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo());								
			//System.out.println("El Año del SALDO INICIAL ES "+getAnhoSeleccionado());
			
			if(resultado.equals("")){
				getGuardarButton().setDisabled(true);
				FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_INFO,"Mensaje:","Se Guardo Satisfactoriamente."));					
			}else{
				getGuardarButton().setDisabled(true);
				limpiaArreglos();								
				FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_WARN,"Error:","Solo puede guardar Saldo Inicial de Caja Una VEZ"));					
			}
			}
		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Hay en Error en el Metodo de doGuardar SaldoInicialCajaBean."));
			e.printStackTrace();
	 		LogService.logger.error("Ha ocurrido una excepcion en doGuardar", e);
		}					
		return "";
	}
	
	public ArrayList getAnhosPresupuesto() {
		return anhosPresupuesto;
	}

	public ArrayList getListaSaldo() {
		getSaldoInicialCaja();
		return listaSaldo;
	}

	public void setListaSaldo(ArrayList listaSaldo) {
		this.listaSaldo = listaSaldo;
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
	
	public void limpiaArreglos(){
		listaSaldo = null;
	}
	
	public CommandButton getGuardarButton(){
		guardarButton = (CommandButton)findComponentInRoot("guardarField");
		return guardarButton;
	}
	
	public InputText getIdentificadorText(){
		montoCajaField = (InputText)findComponentInRoot("montoCajaField");
		return montoCajaField;
	}

}

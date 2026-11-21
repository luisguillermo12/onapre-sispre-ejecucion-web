package pagecode.apps.sis_ejec.situacionmensual;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;

import org.primefaces.component.commandbutton.CommandButton;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.CoDatabaseManager;
import ve.gob.onapre.apps.sispre.comunes.LogService;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.fuentefinanciamiento.session.ServiciosFuenteFinanciamientoLocal;
import ve.gob.onapre.comun.planunicocuentas.entity.helpers.PlanUnicoCuentasTO;
import ve.gob.onapre.comun.planunicocuentas.session.ServiciosPlanUnicoCuentaLocal;
import ve.gob.onapre.sis_ejec.saldoInicialSession.ServicioSaldoInicialCajaLocal;
import ve.gob.onapre.sis_ejec.situacion_mensual.entity.SituacionMensualEgresos;
import ve.gob.onapre.sis_ejec.situacion_mensual.session.ServiciosSituacionMensualLocal;
import ve.gob.onapre.sispre.creditopresupuestarios.tIII.session.ServiciosCreditoPresupuestarioTIIILocal;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.session.ServiciosEstructuraPresupuestariaLocal;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;


@ManagedBean
@ViewScoped
@SuppressWarnings("rawtypes")
public class SituacionMensualEgresosBean extends PageCodeBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB(mappedName = LookUpNames.SESSION_ServiciosEstructuraPresupuestaria)
	private ServiciosEstructuraPresupuestariaLocal serviciosEstructuraPresupuestaria;

	@EJB(mappedName = LookUpNames.SESSION_ServiciosFuenteFinanciamiento)
	private ServiciosFuenteFinanciamientoLocal serviciosFuenteFinanciamiento;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosPlanUnicoCuenta)
	private ServiciosPlanUnicoCuentaLocal serviciosPlanUnicoCuenta;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosCreditoPresupuestarioTIII)
	private ServiciosCreditoPresupuestarioTIIILocal serviciosCreditoPresupuestarioTIII;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosSituacionMensual)
	private ServiciosSituacionMensualLocal serviciosSituacionMensualLocal;
	
	@EJB(mappedName = LookUpNames.SESSION_ServicioSaldoInicialCaja)
	private ServicioSaldoInicialCajaLocal servicioSaldoInicialCajaLocal;
	
	private ArrayList aPlanUnicoCuentas;
	private String planUnicoCuentasSeleccionada;
	
	@Resource(mappedName = CoDatabaseManager.ONAPRE)
	private DataSource jdbcFactory;
	private StreamedContent file;
	private String ruta;
	private String rutaBanner;
	protected HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	protected String dir_Url = httpServletRequest.getRequestURL().toString();
	protected String dir_UrI = httpServletRequest.getRequestURI();
	private EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;	
	private ArrayList listaEjecucion;
	private SituacionMensualEgresos situacionMensualEgresos;
	private DecimalFormat dff = new DecimalFormat(); 	
	private DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
	private double montoAcordado;
	private int mesSeleccionado = -1;
	private double totalAcordado;
	private double totalProgramado;
	private double totalComprometido;
	private double totalCausado;
	private double totalPagado;
	private double totalPagadoAcumulado;
	private double totalDisponible;
	private double totalMontoDisponibleAFecha;
	private double totalCompromisoAcum;
	private double totalCausadoAcum;
	private ArrayList anhosPresupuesto;
	private int anhoSeleccionado;
	private ArrayList<SituacionMensualEgresos> listaDatos;
	private CommandButton guardarButton;
	
	@EJB(mappedName = LookUpNames.SESSION_ServicioSaldoInicialCaja)
	private ServicioSaldoInicialCajaLocal servicioSaldoInicialCaja;
	private boolean  bloquearBotonGuardar=false;
	private String messageError="";
	@PostConstruct
	public void init(){
		
		estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
		anhosPresupuesto = (ArrayList) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_ANHOS_PRESUPUESTO);							
		
		newSymbols.setGroupingSeparator('.');
		newSymbols.setGroupingSeparator(',');
	    dff.setDecimalFormatSymbols(newSymbols); 
	    dff.setDecimalSeparatorAlwaysShown(false);
	    
		aPlanUnicoCuentas = serviciosPlanUnicoCuenta.findNivelPartidaTituloIII("4");		
		PlanUnicoCuentasTO planUnicoCuentasTO = new PlanUnicoCuentasTO();
		planUnicoCuentasTO.setId(806);
		aPlanUnicoCuentas.remove(planUnicoCuentasTO);

		planUnicoCuentasTO = new PlanUnicoCuentasTO();
		planUnicoCuentasTO.setId(859);
		aPlanUnicoCuentas.remove(planUnicoCuentasTO);
		
		planUnicoCuentasTO = new PlanUnicoCuentasTO();
		planUnicoCuentasTO.setId(1165);
		aPlanUnicoCuentas.remove(planUnicoCuentasTO);							
		
	}	

	public String getPlanUnicoCuentasSeleccionada() {
		return planUnicoCuentasSeleccionada;
	}

	public void setPlanUnicoCuentasSeleccionada(String planUnicoCuentasSeleccionada) {
		this.planUnicoCuentasSeleccionada = planUnicoCuentasSeleccionada;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<SituacionMensualEgresos> getListaEjecucion(){		
		if(getPlanUnicoCuentasSeleccionada() != null){			
			if(listaEjecucion == null){
				int mes = getMesSeleccionado();				
				listaEjecucion = serviciosSituacionMensualLocal.findAllSituacionEgresosCarga(getPlanUnicoCuentasSeleccionada(), getAnhoSeleccionado(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), mes);				
			}
		}		
		return listaEjecucion;
	}
	
	public ArrayList<SituacionMensualEgresos> getListaDatos(){		
		if(getPlanUnicoCuentasSeleccionada() != null){			
			if(listaDatos == null){
				int mes = getMesSeleccionado();				
				listaDatos = serviciosSituacionMensualLocal.findMuestraDatosEgresos(getAnhoSeleccionado(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), mes);				
			}
		}		
		return listaDatos;
	}		
	
	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO() {
		return estructuraPresupuestariaSessionTO;
	}
	
	public String  doGuardar(){		 
		try{			
			String resultado = serviciosSituacionMensualLocal.guardarSituacionMensualEgresos(getListaEjecucion(), getPlanUnicoCuentasSeleccionada());
			if(resultado.equals("")){				
				limpiaArreglos();
				FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_INFO,"Mensaje:","Se Guardo Satisfactoriamente."));					
			}else{
				//limpiaArreglos();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia!", resultado));
				//FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_WARN,"Advertencia:",resultado));					
			}
		}catch(Exception e){
			FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Hay en Error en el Metodo de doGuardar SituacionMensualEgresosBean."));
			e.printStackTrace();
	 		LogService.logger.error("Ha ocurrido una excepcion en doGuardar", e);
		}					
		return "";
	}
	
	public double montoPagadoOld(){
		double resultado = serviciosSituacionMensualLocal.findMontoPagado(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), getPlanUnicoCuentasSeleccionada(), getAnhoSeleccionado(), getMesSeleccionado());
		return resultado;
	}
	
	public double montoRecursos(){
		double resultado = serviciosSituacionMensualLocal.ValidateMontoRecursos(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), getAnhoSeleccionado(), getMesSeleccionado());
		return resultado;
	}
	
	public double getTotalMontoAcordadoFormateado(){
		totalAcordado = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null){
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalAcordado = totalAcordado + to.getMontoAcordado();
			}
		}
		return totalAcordado;
	}
	
	public double getTotalMontoProgramado(){
		totalProgramado = 0D;			
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalProgramado = totalProgramado + to.getMontoProgramado();
			}
		return totalProgramado;
	}
	
	public double getTotalMontoDisponibleAFecha(){
		totalMontoDisponibleAFecha = 0D;			
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalMontoDisponibleAFecha = totalMontoDisponibleAFecha + to.getMontoDisponibleAFecha();
			}
		return totalMontoDisponibleAFecha;
	}
	
	public double getTotalMontoComprometido(){		
		totalComprometido = 0D;			
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalComprometido = totalComprometido + to.getMontoComprometido();
			}
		return totalComprometido;	
	}
	
	public double getTotalMontoCausado(){
		totalCausado = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalCausado = totalCausado + to.getMontoCausado();
			}
		return totalCausado;
	}
	
	public double getTotalMontoPagado(){
		totalPagado = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalPagado = totalPagado + to.getMontoPagado();
			}
		return totalPagado;
	}
	
	public double getTotalMontoPagadoAcumulado(){
		totalPagadoAcumulado = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalPagadoAcumulado = totalPagadoAcumulado + to.getMontoPagado();
			}
		return totalPagadoAcumulado;
	}
	
	public double getTotalMontoDisponible(){
		totalDisponible = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalDisponible = totalDisponible + to.getMontoDisponible();
			}
		return totalDisponible;
	}
	
	public double getTotalCompromisoAcum(){
		totalCompromisoAcum = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalCompromisoAcum = totalCompromisoAcum + to.getCompromisoAcumulado();
			}
		return totalCompromisoAcum;
	}
	
	public double getTotalCausadoAcum(){
		totalCausadoAcum = 0D;
		if(getPlanUnicoCuentasSeleccionada() != null)
			for(SituacionMensualEgresos to: getListaEjecucion()){
				totalCausadoAcum = totalCausadoAcum + to.getCausadoAcumulado();
			}
		return totalCausadoAcum;
	}
	
	public String getRutaReportes() {
		if (ruta == null) {
			try {
				/********************************************************************
										SERVIDOR DE PRUEBA
				//http://10.90.48.31:8080/SADE/apps/reportes/reportes_generales.xhtml
				  /home/iadiaz/DiscoG/Sistemas/Reportes/correspo
				*********************************************************************/
				/********************************************************************
										SERVIDOR LOCAL
				//http://localhost:8080/SADE/apps/reportes/reportes_generales.xhtml
				 
				*********************************************************************/
				if (dir_Url.contains("localhost:8080")){
					ruta = "G:/Sistemas/Reportes/Ejecucion/";
				}else{
					ruta = (String)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_RUTA_REPORTE)+"/Ejecucion/";
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ruta;
	}
	
	public String getRutaBanner() {
		if (rutaBanner == null) {
			try {
				if (dir_Url.contains("localhost:8080")){
					rutaBanner = "G:/Sistemas/Reportes/imagenes/";
				}else{
					rutaBanner = (String)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_RUTA_IMAGENES);
				}
				
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return rutaBanner;
	}
	
	@SuppressWarnings("unchecked")
	public void imprimeReporte() throws IOException {
		HashMap parameters = new HashMap();
		String reporte = "EjecucionFinancieraEgresos.jasper";
		try {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				facesContext.responseComplete();
				Connection conn = null;
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = jdbcFactory.getConnection();
				conn.setAutoCommit(false);
		    	parameters.put("PAR_LOGO", getRutaBanner()+"membrete2013.jpg");
				parameters.put("PAR_ANHO", getAnhoSeleccionado());
				parameters.put("PAR_CODIGO", getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo());
		    	parameters.put("PAR_MES", getMesSeleccionado());

				JasperPrint jasperPrint = JasperFillManager.fillReport(getRutaReportes() + reporte, parameters, conn);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
				exporter.exportReport();
	
				byte[] bytes = baos.toByteArray();	
				if (bytes != null && bytes.length > 0) {	
					InputStream input = new ByteArrayInputStream(bytes);	
					file = new DefaultStreamedContent(input, "PDF", reporte+"_"+getEstructuraPresupuestariaSessionTO().getId());
				}	
				if (conn != null){conn.close();}	
		} catch (Exception e) {	
			e.printStackTrace();	
		}
	}
	
	public StreamedContent getFile() {
		try {
				imprimeReporte();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return file;
	}
	
	public ArrayList getaPlanUnicoCuentas() {
		return aPlanUnicoCuentas;
	}
	
	public void limpiaArreglos(){
		listaEjecucion = null;
		montoRecursos();
		totalAcordado = 0D;
		doBloquearGuardar();
		doMessageError();
	}

	public SituacionMensualEgresos getSituacionMensualEgresos() {
		return situacionMensualEgresos;
	}

	public void setSituacionMensualEgresos(
			SituacionMensualEgresos situacionMensualEgresos) {
		this.situacionMensualEgresos = situacionMensualEgresos;
	}

	public double getMontoAcordado() {
		return montoAcordado;
	}

	public void setMontoAcordado(double montoAcordado) {
		this.montoAcordado = montoAcordado;
	}

	public int getMesSeleccionado() {		
		return mesSeleccionado;
	}

	public void setMesSeleccionado(int mesSeleccionado) {
		this.mesSeleccionado = mesSeleccionado;
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

	public boolean isBloquearBotonGuardar() {	
		return bloquearBotonGuardar;
	}

	public void setBloquearBotonGuardar(boolean bloquearBotonGuardar) {
		this.bloquearBotonGuardar = bloquearBotonGuardar;
	}
	public String doBloquearGuardar() {	
		String estado="none";
		if(getPlanUnicoCuentasSeleccionada() != null){			
			if(listaEjecucion == null && getMesSeleccionado()!=-1 && getAnhoSeleccionado()!=-1 && getMesSeleccionado()!=0 && getAnhoSeleccionado()!=0){
				setBloquearBotonGuardar(servicioSaldoInicialCaja.verifySaldoInicialCaja(getAnhoSeleccionado(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo()));
				if(!isBloquearBotonGuardar()){
					setMessageError("Debe Cargar el Saldo Inicial de Caja.");
					getGuardarButton().setDisabled(true);
				}
				else{
					setBloquearBotonGuardar(serviciosSituacionMensualLocal.findAllVerifySituacionEgresosCarga(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), getAnhoSeleccionado(), getMesSeleccionado()));
					if(!isBloquearBotonGuardar()){
						setMessageError("Ejecución Financiera para este mes Cerrada.");	
						getGuardarButton().setDisabled(true);
					}
					else{
						setMessageError("");
						getGuardarButton().setDisabled(false);
						estado= "block";
					}
				}
			}
		}		
		return estado;		
	}

	public CommandButton getGuardarButton(){
		guardarButton = (CommandButton)findComponentInRoot("guardarBoton");
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

	public void recibeParametrosRecursosPaga(){
		try{
			serviciosSituacionMensualLocal.ValidateMontoRecursos(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(), getAnhoSeleccionado(), getMesSeleccionado());
		}catch(Exception e){
				FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Hay en Error en el Metodo recibeParametrosRecursosPaga()."));
				e.printStackTrace();
		 		LogService.logger.error("Ha ocurrido una excepcion en doGuardar", e);
		}
		
	}
}

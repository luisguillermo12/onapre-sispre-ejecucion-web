package pagecode.apps.modificacion;

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
import ve.gob.onapre.comun.planunicocuentas.entity.helpers.PlanUnicoCuentasTO;
import ve.gob.onapre.comun.planunicocuentas.session.ServiciosPlanUnicoCuentaLocal;
import ve.gob.onapre.sis_ejec.modificacion.entity.ModificacionesRecursos;
import ve.gob.onapre.sis_ejec.modificacion.session.ServiciosModificacionesLocal;
import ve.gob.onapre.sis_ejec.saldoInicialSession.ServicioSaldoInicialCajaLocal;
import ve.gob.onapre.sis_ejec.situacion_mensual.session.ServiciosSituacionMensualLocal;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;

@ManagedBean
@ViewScoped
@SuppressWarnings({"unchecked","rawtypes"})
public class ModificacionRecursosBean extends PageCodeBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosPlanUnicoCuenta)
	private ServiciosPlanUnicoCuentaLocal serviciosPlanUnicoCuenta;
	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosModificaciones)
	private ServiciosModificacionesLocal serviciosModificacionesLocal;
	
	private EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;
	private ArrayList<PlanUnicoCuentasTO> aPlanUnicoCuentas;
	private String planUnicoCuentasSeleccionada;
	private ArrayList<ModificacionesRecursos> listaModificacionesRecursos;
	private double totalAcordado= 0D;
	private double totalIncremento= 0D;
	private double totalDisminucion= 0D;
	private int mesSeleccionado;
	private DecimalFormat dff = new DecimalFormat(); 
	private DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
	@Resource(mappedName = CoDatabaseManager.ONAPRE)
	DataSource jdbcFactory;
	private StreamedContent file;
	private String rutaReportes;
	private String rutaBanner;
	protected HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	protected String dir_Url = httpServletRequest.getRequestURL().toString();
	protected String dir_UrI = httpServletRequest.getRequestURI();	
	private ArrayList anhosPresupuesto;
	private int anhoSeleccionado;
	private ModificacionesRecursos modificacionesRecursos;
	private CommandButton guardarButton;
	
	@EJB(mappedName = LookUpNames.SESSION_ServicioSaldoInicialCaja)
	private ServicioSaldoInicialCajaLocal servicioSaldoInicialCaja;

	@EJB(mappedName = LookUpNames.SESSION_ServiciosSituacionMensual)
	private ServiciosSituacionMensualLocal serviciosSituacionMensualLocal;
		
	private boolean  bloquearBotonGuardar=true;
	private String messageError="";
	
	@PostConstruct
	public void init(){
		estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
		anhosPresupuesto = (ArrayList) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_ANHOS_PRESUPUESTO);
		aPlanUnicoCuentas = serviciosPlanUnicoCuenta.findNivelPartidaTituloIII("3");
		newSymbols.setGroupingSeparator('.'); 
	    dff.setDecimalFormatSymbols(newSymbols); 
	    dff.setDecimalSeparatorAlwaysShown(false);	
	}
	
	public ArrayList<PlanUnicoCuentasTO> getPlanUnicoCuentasByTipoByNivel() {
		return aPlanUnicoCuentas;
	}

	public String getPlanUnicoCuentasSeleccionada() {
		return planUnicoCuentasSeleccionada;
	}

	public void setPlanUnicoCuentasSeleccionada(String planUnicoCuentasSeleccionada) {
		this.planUnicoCuentasSeleccionada = planUnicoCuentasSeleccionada;
	}

	public ArrayList<ModificacionesRecursos> getListaModificacionesRecursos(){		
		if(getPlanUnicoCuentasSeleccionada()!=null){
			if(listaModificacionesRecursos == null && getMesSeleccionado()!=-1 && getAnhoSeleccionado()!=-1 && getMesSeleccionado()!=0 && getAnhoSeleccionado()!=0){
				int mes=(int)getMesSeleccionado();
				listaModificacionesRecursos = serviciosModificacionesLocal.findModificacionesRecursos(getPlanUnicoCuentasSeleccionada(), getAnhoSeleccionado(), getEstructuraPresupuestariaSessionTO().getFasePresupuestaria(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getCodigo(),mes);
				getTotales();
			}
		}
		return listaModificacionesRecursos;
	}

	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO() {
		return estructuraPresupuestariaSessionTO;
	}
	
	public void limpiaTabla(){
		listaModificacionesRecursos = null;
		totalAcordado = 0;	
		getGuardarButton();
		doBloquearGuardar();
		if(!getMessageError().equals("")){doMessageError();}
	}
	
	public String doGuardar(){				
		try {		
			String errors = serviciosModificacionesLocal.guardarModificacionRecursos(getListaModificacionesRecursos());			
			if(errors.equals("")){				
				limpiaTabla();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se guardó la información"));				
			}else{
				limpiaTabla();
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Advertencia:",errors));				
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Hay en Error en el Metodo de doGuardar ModificacionRecursosBean "));
			LogService.logger.error("ModificacionRecursosBean.doGuardar: Problemas al guardar",e);
		}
		return "";
	}

	public void getTotales(){
		totalAcordado = 0D;
		totalIncremento = 0D;
		totalDisminucion = 0D;
		if(getPlanUnicoCuentasSeleccionada()!=null && getMesSeleccionado()!=-1 && getAnhoSeleccionado()!=-1 && getMesSeleccionado()!=0 && getAnhoSeleccionado()!=0){
			for(ModificacionesRecursos to : getListaModificacionesRecursos()){
				totalAcordado = totalAcordado + to.getMontoLey();
				totalIncremento = totalIncremento + to.getMontoIncremento();
				totalDisminucion = totalDisminucion + to.getMontoDisminucion();
			}
		}
	}
	public String getTotalMontoLeyFormateado(){
		if(totalAcordado==0D)
			return dff.format(0);
		else
			return dff.format(totalAcordado);
	}
	
	public String getTotalMontoIncrementoFormateado(){
		return dff.format(totalIncremento);
	}
	
	public String getTotalMontoDisminucionFormateado(){
		return dff.format(totalDisminucion);
	}
	
	public String getTotalMontoModificacionFormateado(){
		return dff.format(totalIncremento-totalDisminucion);
	}
	
	public String getTotalMontoPptoModificacionFormateado(){
		return dff.format(totalAcordado+totalIncremento-totalDisminucion);
	}
	
	public String getRutaReportes() {
		if (rutaReportes == null) {
			try {
				if (dir_Url.contains("localhost:8080")){
					rutaReportes = "G:/Sistemas/Reportes/Ejecucion/";
				}else{
					rutaReportes = (String)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_RUTA_REPORTE)+"/Ejecucion/";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rutaReportes;
	}	
	
	public Double getTotalAcordado() {
		return totalAcordado;
	}

	public Double getTotalIncremento() {
		return totalIncremento;
	}

	public Double getTotalDisminucion() {
		return totalDisminucion;
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
				e.printStackTrace();
			}
		}
		return rutaBanner;
	}
	
	public void imprimeReporte() throws IOException {
		HashMap parameters = new HashMap();
		String reporte = "EjecucionFinancieraRecursos.jasper";
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
			e.printStackTrace();
		}
		return file;
	}

	public int getMesSeleccionado() {
		return mesSeleccionado;
	}

	public void setMesSeleccionado(int mesSeleccionado) {
		this.mesSeleccionado = mesSeleccionado;
	}
	
	public ArrayList getAnhosPresupuesto(){
		return anhosPresupuesto;
	}

	public int getAnhoSeleccionado() {
		return anhoSeleccionado;
	}

	public void setAnhoSeleccionado(int anhoSeleccionado) {
		this.anhoSeleccionado = anhoSeleccionado;
	}

	public ModificacionesRecursos getModificacionesRecursos() {		
		return modificacionesRecursos;
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
			if(listaModificacionesRecursos == null && getMesSeleccionado()!=-1 && getAnhoSeleccionado()!=-1 && getMesSeleccionado()!=0 && getAnhoSeleccionado()!=0){
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
		FacesContext.getCurrentInstance().addMessage("messages_list", new FacesMessage(FacesMessage.SEVERITY_ERROR,"Advertencia:",messageError));
		setMessageError("");
	}

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}

	public String getMessageError() {
		return messageError;
	}

}

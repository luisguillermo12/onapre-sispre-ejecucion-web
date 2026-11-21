package pagecode.apps.reporte;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.CoDatabaseManager;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.consultas.session.ServiciosConsultasLocal;
import ve.gob.onapre.comun.usuarios.entity.helpers.UsuarioTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;

@ManagedBean
//@ViewScoped
public class reportesBean extends PageCodeBase {
	private ArrayList arregloDeReportes;
	private int anhoActual;
	@Resource(mappedName = CoDatabaseManager.ONAPRE)
	DataSource jdbcFactory;
	private StreamedContent file;
	protected String ruta;
	protected String rutaBanner;
	protected HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	protected String dir_Url = httpServletRequest.getRequestURL().toString();
	protected String dir_UrI = httpServletRequest.getRequestURI();
	protected HtmlSelectOneMenu reportesCmb;	
	protected EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;	
	private ArrayList anhosPresupuesto;
	private UsuarioTO usuarioTOLogeado;
	private int anhoSeleccionado;
	private int mesSeleccionado;

	
	@EJB(mappedName = LookUpNames.SESSION_ServiciosConsultas)
	private ServiciosConsultasLocal serviciosConsultas;
	
	@PostConstruct
	public void init(){
		anhosPresupuesto = (ArrayList) FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.SISPRE_ANHOS_PRESUPUESTO);
		estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
		usuarioTOLogeado = (UsuarioTO)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(SessionComunAtributes.LOGIN_USUARIO_LOGEADO);		
	}
	
	
	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO(){
		return estructuraPresupuestariaSessionTO;
	}
	
	public ArrayList getReportes(){
			arregloDeReportes = new ArrayList();
			arregloDeReportes.add(new SelectItem("0", "------------------------------------------ Seleccione Un Reporte  -------------------------------------------"));
			arregloDeReportes.add(new SelectItem("0", "-----------------------------------------------------------------------------------------------------------------------"));			
			arregloDeReportes.add(new SelectItem("0", "------------------------------------------ Ejecución Mensual Recursos y Egresos  -------------------------------------------"));
			arregloDeReportes.add(new SelectItem("0", "-----------------------------------------------------------------------------------------------------------------------"));
		    arregloDeReportes.add(new SelectItem("EjecucionFinancieraRecursos.jasper", "Ejecución Mensual Recursos")); 
		    arregloDeReportes.add(new SelectItem("EjecucionFinancieraEgresos.jasper", "Ejecución Mensual Egresos"));
		    arregloDeReportes.add(new SelectItem("ResumenResultados.jasper", "Resumen de Resultados"));

		return arregloDeReportes;
	}
	
	public void setReportes(ArrayList arregloDeReportes){
		this.arregloDeReportes = arregloDeReportes;
	}

	public int getAnhoActual(){
		//anhoActual= getEstructuraPresupuestariaSessionTO().getAnhoPresupuestario();
		return anhoActual;
	}
	
	public void setAnhoActual(int anhoActual){
		this.anhoActual = anhoActual;
	}
	
	public String getRutarregloDeReportes(){
		if (ruta == null){
			if (dir_Url.indexOf("onpapp")>0 || dir_Url.indexOf("8080")>0){
				ruta = serviciosConsultas.findConexionReportes("I");					
			}else{
				ruta = serviciosConsultas.findConexionReportes("E");			
			}
		}
		return ruta;
	}
	
	public void setRutarregloDeReportes(String ruta){
		this.ruta = ruta;
	}
	
	
	public String getRutaReportes() {
		if (ruta == null) {
			try {
				/********************************************************************
										SERVIDOR DE PRUEBA
				//http://10.90.48.31:8080/home/onapre/jboss-as-7.1.1.Final/standalone/reportes/TituloII
				  
				*********************************************************************/
				/********************************************************************
										SERVIDOR LOCAL
				//http://localhost:8080/SADE/apps/reportes/reportes_generales.xhtml
				 
				*********************************************************************/
				if (dir_Url.contains("localhost:8080")){
					ruta = "/G:/Sistemas/Reportes/Ejecucion/";
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
	
  public void imprimeReporte() throws IOException {
		HashMap parameters = new HashMap();		
		ruta = getRutaReportes();
		rutaBanner = getRutaBanner();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String reporte = (String) ((HtmlSelectOneMenu) findComponentInRoot("reportesCmb")).getValue();
		//String reporte = (String) getReportesCmb().getValue();
		//LogService.logger.info("RUTA DEL REPORTE " + ruta);
		//LogService.logger.info("RUTA DEL BANNER " + rutaBanner);
		
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
		    	parameters.put("PAR_MES", getMesSeleccionado());parameters.put("PAR_FASE",getEstructuraPresupuestariaSessionTO().getFasePresupuestaria());		    									
				parameters.put("PAR_LOGO", getRutaBanner()+"membrete2013.jpg");
				JasperPrint jasperPrint = JasperFillManager.fillReport(ruta + reporte, parameters, conn);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
				exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
				exporter.exportReport();
	
				byte[] bytes = baos.toByteArray();
	
				if (bytes != null && bytes.length > 0) {
	
					InputStream input = new ByteArrayInputStream(bytes);
	
					file = new DefaultStreamedContent(input, "PDF", reporte);
	
				}
	
				if (conn != null)
					conn.close();
	
			} catch (Exception e) {
	
				e.printStackTrace();
	
			}

	}

	public StreamedContent getFile() {
		try {
			if(getReportesCmb().getValue().equals("0")){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:"," DEBE SELECCIONAR UN REPORTE "));
			}else if(getAnhoSeleccionado() == 0 || getAnhoSeleccionado() == -1){				
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:"," DEBE SELECCIONAR UN AÑO "));
			}else if(getMesSeleccionado() == -1){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:"," DEBE SELECCIONAR UN MES "));				
			}else{
				imprimeReporte();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		return file;
	}
	

	public HtmlSelectOneMenu getReportesCmb() {
		reportesCmb = (HtmlSelectOneMenu) findComponentInRoot("reportesCmb");
		return reportesCmb;
	}

	public void setReportesCmb(HtmlSelectOneMenu reportesCmb) {
		this.reportesCmb = reportesCmb;
	}
	
	public String doSalir(){
		return "/apps/menu/menu.xhtml";
	}

	public String getRutaBanner() {
		if (rutaBanner == null) {
			try {
				if (dir_Url.contains("localhost:8080")){		
					rutaBanner = "/G:/Sistemas/Reportes/imagenes/";
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

	public void setRutaBanner(String rutaBanner) {
		this.rutaBanner = rutaBanner;
	}


	public ArrayList getAnhosPresupuesto() {
		return anhosPresupuesto;
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
	
	
	
}
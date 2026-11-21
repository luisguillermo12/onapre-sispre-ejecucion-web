package pagecode.template;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.usuarios.entity.helpers.UsuarioTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.session.ServiciosEstructuraPresupuestariaLocal;
import atributes.ApplicationComunAtributes;
import atributes.SessionComunAtributes;

@ManagedBean
@ViewScoped
public class MenuBean extends PageCodeBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosEstructuraPresupuestaria)
	private ServiciosEstructuraPresupuestariaLocal serviciosEstructuraPresupuestaria;
	Calendar fechaActual = Calendar.getInstance();
	
	protected SelectOneMenu organoCmb;
	private Long organoSeleccionado;
	private Map<Long,EstructuraPresupuestariaShortTO> estructuraMap = new HashMap<Long,EstructuraPresupuestariaShortTO>();
	private UsuarioTO usuarioTOLogeado;
	protected EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;	

	@PostConstruct
	public void init(){
		usuarioTOLogeado = (UsuarioTO)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(SessionComunAtributes.LOGIN_USUARIO_LOGEADO);
		//System.out.println(usuarioTOLogeado);
	}
	
	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO(){
		if(estructuraPresupuestariaSessionTO == null){
			estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
		}
		return estructuraPresupuestariaSessionTO;
	}

	public UsuarioTO getUsuarioTOLogeado(){
		return usuarioTOLogeado;
	}

	public String doLogin(){
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "/login/login.xhtml?faces-redirect=true";
	}

	
	public Long getOrganoSeleccionado() {
		organoSeleccionado = (Long)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("ONAPRE_TITULO_II_ORGANO_ACTUAL");
		return organoSeleccionado;
	}
	
	public void setOrganoSeleccionado(Long organoSeleccionado) {
		this.organoSeleccionado = organoSeleccionado;
		//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("ONAPRE_TITULO_II_ORGANO_ACTUAL", organoSeleccionado);
	}
	
	public void doSeleccionOrgano(){
		getOrganoSeleccionado();
	}
	
	public SelectOneMenu getOrganoCmb(){
		if(organoCmb==null){
			organoCmb=(SelectOneMenu)findComponentInRoot("organoCmb");
		}
		return organoCmb;
	}
	

	
	public String getEtapaPresupuestaria(){
		String etapa = "";
		if (fechaActual.get(Calendar.MONTH)<12) 
			etapa= "Proyecto de Ley de Presupuesto ";
		else
			etapa= "Ley de Presupuesto ";
		return etapa+String.valueOf(fechaActual.get(Calendar.YEAR)+1);
	}
	
	public String getNavegarListaEstructura(){
		return "/faces/apps/estructura_presupuestaria/lista_estructura.xhtml";
	}
	
	public String getNavegarImportarNuevaEtapa(){
		//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraMap.get(getOrganoSeleccionado()));
		return "/faces/apps/estructura_presupuestaria/importar_proyectos.xhtml";
	}

	public String getNavegarListaUsuario(){
		//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraMap.get(getOrganoSeleccionado()));
		return "/faces/apps/usuario/lista_usuario.xhtml";
	}
	
	public String getUnidadOrganizacional()	{
		//FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraMap.get(getOrganoSeleccionado()));
		return "/faces/apps/unidad_organizacional/unidad_organizacional.xhtml";
	}
	
	@SuppressWarnings("unchecked")
	public void getEstructuraActual(){
		getSessionScope().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraMap.get(getOrganoSeleccionado()));
	}
		
	public String getNavegarFuentesFinanciamiento(){
		//getEstructuraActual();
		return "/faces/apps/fuentesFinanciamientoEstructura/fuentesFinanciamientoEstructura.xhtml";
	}
	
	public String getNavegarProyectosEndeudamiento(){
		//getEstructuraActual();
		return "/faces/apps/proyectosEndeudamiento/proyectosEndeudamiento.xhtml";
	}
	
	public String getNavegarImputacionDirecta(){
		//getEstructuraActual();
		return "/faces/apps/imputacionDirecta/imputacionDirecta.xhtml";
	}
	
	public String getNavegarImputacionTransferencias(){
		//getEstructuraActual();
		return "/faces/apps/imputacionTransferencias/imputacionTransferencias.xhtml";
	}

	public String getNavegarReportes(){
		//getEstructuraActual();
		return "/faces/apps/reportes/reportes.xhtml";
	}
	
	public String getNavegarRecursosHumanosClasificadosGenero(){
		//getEstructuraActual();
		EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)getSessionScope().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
		if(estructuraPresupuestariaSessionTO!= null){
			return "/faces/apps/recursos_humanos/tipo_cargo_activo.xhtml";
		}else{
			return"/faces/apps/menu/menu.xhtml";
		}
		
	}
	
	public String getNavegarPersonalPensionadoJubilado(){
		//getEstructuraActual();
		return "/faces/apps/recursos_humanos/tipo_cargo_pasivo.xhtml";
	}
	
	public String getNavegarEscalaSueldo(){
		//getEstructuraActual();
		return "/faces/apps/recursos_humanos/escala_de_sueldo.xhtml";
	}
	
	public String getNavegarEscalaSalario(){
		//getEstructuraActual();
		return "/faces/apps/recursos_humanos/escala_de_salario.xhtml";
	}
	
	public String getNavegarEscalaJubilado(){
		//getEstructuraActual();
		return "/faces/apps/recursos_humanos/escala_de_jubilado.xhtml";
	}
	
	public String getNavegarEscalaPensionado(){
		//getEstructuraActual();
		return "/faces/apps/recursos_humanos/escala_de_pensionado.xhtml";
	}
	
	public String getMensajeSISPRE(){
		String mensaje = "";
		if(FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.MENSAJE_SISPRE)!=null){
			mensaje= (String)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.MENSAJE_SISPRE);
		}
		return mensaje;
	}
	
	public void publicarMensaje(){
		FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().put(ApplicationComunAtributes.MENSAJE_SISPRE, true);
	}
	
	public boolean mostrarMensaje(){
		boolean resultado = false;
		if(FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.MENSAJE_SISPRE)!=null){
			resultado = true;
		}
		return resultado;
	}
	
	public boolean isMostrarCambioOrganismo(){
		if(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(SessionComunAtributes.MOSTRAR_MENU_CAMBIO_ORGA_mostrar)==null){
			return false;
		}else{
			return true;
		}
	}
	
    public String doCloseSession(){
    	ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	    ec.invalidateSession();
	    try {
			ec.redirect(ec.getRequestContextPath() + "/login/login.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return"";
    }
}

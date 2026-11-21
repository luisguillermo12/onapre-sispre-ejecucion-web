package pagecode.apps.seleccion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

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
@ViewScoped
public class SeleccionEnteBean extends PageCodeBase implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosOrganoEnteReceptor)
	private ServiciosOrganoEnteReceptorLocal serviciosOrganoEnteReceptorLocal;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosEstructuraPresupuestaria)
	private ServiciosEstructuraPresupuestariaLocal serviciosEstructuraPresupuestariaLocal;
	private ArrayList<OrganoEnteReceptorTO> listaOrganos;
	private UsuarioTO usuarioTOLogeado;
	private Integer anhoPresupuesto;
	private OrganoEnteReceptorTO organoEnteReceptorSeleccionadoTO;
	private List<OrganoEnteReceptorTO> filtro;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosUsuarios)
	private ServiciosUsuariosLocal serviciosUsuariosLocal;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		//usuarioTOLogeado = (UsuarioTO)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(SessionComunAtributes.LOGIN_USUARIO_LOGEADO);
		usuarioTOLogeado = serviciosUsuariosLocal.findUsuarioById(String.valueOf(FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal()));
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.LOGIN_USUARIO_LOGEADO,usuarioTOLogeado);
		listaOrganos = serviciosOrganoEnteReceptorLocal.findMinisterioYEntesByUserName(usuarioTOLogeado.getUsername());
		anhoPresupuesto = (Integer)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get(ApplicationComunAtributes.ANHO_PRESUPUESTO);
	}

	public ArrayList<OrganoEnteReceptorTO> getListaOrganos() {
		return listaOrganos;
	}

	public String doMenu(OrganoEnteReceptorTO to){
		EstructuraPresupuestariaShortTO estructuraTO = serviciosEstructuraPresupuestariaLocal.getEstructuraPresupuestariaShortByOrganoByAnhoByFase(to.getId(), getAnhoPresupuesto(), EstructuraPresupuestariaTO.FASE_PROYECTO);
		estructuraTO.setOrganoEnteReceptorTO(to);
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraTO);
		if(estructuraTO.getOrganoEnteReceptorTO().getTipo()==2){
			return "/apps/menu/menu_tiii.xhtml?faces-redirect=true";
		}
		return "";
	}
	
	public String fondo(int tipo){
		String color = "";
		switch (tipo) {
		case 1:
			color = "background_dodger_blue";
			break;
		case 3:
			color = "background_dodger_blue";
			break;
		default:
			break;
		}
		return color;
	}

	public Integer getAnhoPresupuesto() {
		return anhoPresupuesto;
	}

	public OrganoEnteReceptorTO getOrganoEnteReceptorSeleccionadoTO() {
		return organoEnteReceptorSeleccionadoTO;
	}

	public void setOrganoEnteReceptorSeleccionadoTO(OrganoEnteReceptorTO organoEnteReceptorSeleccionadoTO) {
		this.organoEnteReceptorSeleccionadoTO = organoEnteReceptorSeleccionadoTO;
	}
	
	public void onRowSelect(SelectEvent event){
		try {
			OrganoEnteReceptorTO to = (OrganoEnteReceptorTO)event.getObject();
			EstructuraPresupuestariaShortTO estructuraTO = serviciosEstructuraPresupuestariaLocal.getEstructuraPresupuestariaShortByOrganoByAnhoByFase(to.getId(), getAnhoPresupuesto(), EstructuraPresupuestariaTO.FASE_PROYECTO);
			estructuraTO.setOrganoEnteReceptorTO(to);
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria, estructuraTO);
			if(estructuraTO.getOrganoEnteReceptorTO().getTipo()!=1){
				FacesContext.getCurrentInstance().getExternalContext().redirect("/SIS_EJEC/faces/apps/menu/menu_tiii.xhtml?faces-redirect=true");
			}
		} catch (Exception e) {
			LogService.logger.error("SeleccionBean.onRowSelect(): Ha ocurrido una exepcion ", e);
		}
		
	}

	public List<OrganoEnteReceptorTO> getFiltro() {
		return filtro;
	}

	public void setFiltro(List<OrganoEnteReceptorTO> filtro) {
		this.filtro = filtro;
	}
		  
			  
		  


	
}

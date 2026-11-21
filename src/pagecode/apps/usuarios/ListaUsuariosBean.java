package pagecode.apps.usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CloseEvent;

import pagecode.PageCodeBase;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.comun.organoentereceptor.session.ServiciosOrganoEnteReceptorLocal;
import ve.gob.onapre.comun.usuarios.entity.helpers.UsuarioTO;
import ve.gob.onapre.comun.usuarios.session.ServiciosUsuariosLocal;
import ve.gob.onapre.sispre.comun.enamurated.UsuarioEstadoEnum;
import ve.gob.onapre.sispre.estructurapresupuestaria.entity.helpers.EstructuraPresupuestariaShortTO;
import ve.gob.onapre.sispre.estructurapresupuestaria.session.ServiciosEstructuraPresupuestariaLocal;
import atributes.SessionComunAtributes;

@ManagedBean
@ViewScoped
public class ListaUsuariosBean extends PageCodeBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosUsuarios)
	private ServiciosUsuariosLocal serviciosUsuarios;
	private EstructuraPresupuestariaShortTO estructuraPresupuestariaSessionTO;
	private ArrayList<UsuarioTO> usuarios;
	private List<UsuarioTO> filtroUsuario;
	private DataTable listaUsuarioTable;
	private UsuarioTO usuarioTOSeleccionado;
	private Boolean mostrarColumnaAdministrador;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosEstructuraPresupuestaria)
	private ServiciosEstructuraPresupuestariaLocal serviciosEstructuraPresupuestaria;
	@EJB(mappedName = LookUpNames.SESSION_ServiciosOrganoEnteReceptor)
	private ServiciosOrganoEnteReceptorLocal serviciosOrganoEnteReceptorLocal;
	//private ArrayList<UsuarioTO> usuariosEnte;
	
	@PostConstruct
	public void init(){
		estructuraPresupuestariaSessionTO = (EstructuraPresupuestariaShortTO)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(SessionComunAtributes.ONAPRE_TITULOII_estructuraPresupuestaria);
	}
	
	
	public EstructuraPresupuestariaShortTO getEstructuraPresupuestariaSessionTO(){
		return estructuraPresupuestariaSessionTO;
	}
	
	public ArrayList<UsuarioTO> getUsuarios(){
		if(usuarios==null){
			usuarios = new ArrayList<UsuarioTO>();
			usuarios = serviciosUsuarios.getUsuariosByIdEnteOrgano(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getId(), "", "O");
		}
		return usuarios;
	}
	
	/*public ArrayList<UsuarioTO> getUsuariosByEntes(){
		try{
			if(usuariosEnte == null){
				usuariosEnte = new ArrayList<UsuarioTO>();				
				usuariosEnte = serviciosUsuarios.getUsuariosAdministradoresByEntes(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getId());	
			}
		}catch(Exception e){
			LogService.logger.info("ListaUsuarioBean.getUsuariosByEntes Cargando la lista Ocurrio una Exception ", e);
		}
		
		return usuariosEnte;
	}*/

	public void limpiarListaUsuario(){
		usuarios = null;
		usuarioTOSeleccionado=null;
	}

	public void doGuardar(){
		boolean resultado = serviciosUsuarios.guardarPrivilegios(getUsuarios());
		if(resultado){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se guardó la información"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Problemas al registrar la información "));			
		}
		limpiarListaUsuario();
	}	
	/*public void doGuardarPrivilegiosAdminEnte(){		
		boolean resultado = serviciosUsuarios.guardarPrivilegiosAdministradorEnte(getUsuariosByEntes());
		if(resultado){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se guardó la información"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Problemas al registrar la información "));			
		}
	}*/
	
	public List<UsuarioTO> getFiltroUsuario() {
		return filtroUsuario;
	}

	public void setFiltroUsuario(List<UsuarioTO> filtroUsuario) {
		this.filtroUsuario = filtroUsuario;
	}
	
	public void desactivarRoles(UsuarioTO to) {  
        to.setRolAD(false);
		to.setRolAI(false);
		to.setRolAR(false);
		to.setRolCO(false);
		to.setRolAUE(false);
		to.setUserActivo(false);
		serviciosUsuarios.guardarUsuarioOrgEnt(to);
		usuarios=null;
    }
	
	public DataTable getListaUsuarioTable(){
		if(listaUsuarioTable==null){
			listaUsuarioTable = (DataTable)findComponentInRoot("listaUsuariosTable"); 
		}
		return listaUsuarioTable;
	}

	public UsuarioTO getUsuarioTOSeleccionado() {
		if(usuarioTOSeleccionado==null){
			usuarioTOSeleccionado= new UsuarioTO();
			usuarioTOSeleccionado.setNuevo(true);
		}
		return usuarioTOSeleccionado;
	}

	public void setUsuarioTOSeleccionado(UsuarioTO usuarioTOSeleccionado) {
		this.usuarioTOSeleccionado = usuarioTOSeleccionado;
	}
	
	public String getUserName(){
		if(getUsuarioTOSeleccionado().getUsername().equals("-1")){
			return "";
		}else{
			return getUsuarioTOSeleccionado().getUsername();
		}
	}
	
	public void actualizarUserName(){
		getUsuarioTOSeleccionado().setUsername(getUsuarioTOSeleccionado().getCedula());
		UsuarioTO existeUsuarioTO = serviciosUsuarios.findUsuarioById(getUsuarioTOSeleccionado().getCedula());
		if(existeUsuarioTO!=null){
			getUsuarioTOSeleccionado().setApellido(existeUsuarioTO.getApellido());
			getUsuarioTOSeleccionado().setNombre(existeUsuarioTO.getNombre());
			getUsuarioTOSeleccionado().setTelefono(existeUsuarioTO.getTelefono());
			getUsuarioTOSeleccionado().setCorreo(existeUsuarioTO.getCorreo());
			getUsuarioTOSeleccionado().setEstado(UsuarioEstadoEnum.ACTIVO.getEstado());
			getUsuarioTOSeleccionado().setTipo(existeUsuarioTO.getTipo());
			getUsuarioTOSeleccionado().setUserActivo(true);
			getUsuarioTOSeleccionado().setOrganoEnteReceptorTO(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO());
			getUsuarioTOSeleccionado().setNuevo(false);
		}
	}
	
	public Boolean getMostrarColumnasTituloII(){
		if(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getTipo()==1){
			mostrarColumnaAdministrador = true; 
		}else{
			mostrarColumnaAdministrador= false;
		}
		return mostrarColumnaAdministrador;
	}
	
	public String guardarUsuario() {  
		//System.out.println("GUARDAR USUARIO");
		boolean errors = false;
		if(getUsuarioTOSeleccionado().getCedula().trim().isEmpty() || getUsuarioTOSeleccionado().getCedula().trim()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Falta la cédula "));
			errors = true;
		}
		if(getUsuarioTOSeleccionado().getNombre().trim().isEmpty() || getUsuarioTOSeleccionado().getNombre().trim()==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Falta el nombre "));
			errors = true;
		}
		//if(estructuraMap.get(getOrganoSeleccionado()).getOrganoEnteReceptorTO().getTipo()==1){
		if(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getTipo()==2){			
			if(!getUsuarioTOSeleccionado().isRolCTA() && !getUsuarioTOSeleccionado().isRolCE() && !getUsuarioTOSeleccionado().isRolAE() && !getUsuarioTOSeleccionado().isRolAUE()){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Debe seleccionar al menos un rol"));
				errors = true;
			}
		}/*else{
			if(!getUsuarioTOSeleccionado().isRolAI() && !getUsuarioTOSeleccionado().isRolCO()){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Debe seleccionar al menos un rol"));
				errors = true;
			}
		}*/
		if(!errors){
			getUsuarioTOSeleccionado().setEstado(UsuarioEstadoEnum.ACTIVO.getEstado());
			getUsuarioTOSeleccionado().setTipo("O");
			getUsuarioTOSeleccionado().setUserActivo(true);
			getUsuarioTOSeleccionado().setOrganoEnteReceptorTO(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO());
			//getUsuarioTOSeleccionado().setOrganoEnteReceptorTO(estructuraMap.get(getOrganoSeleccionado()).getOrganoEnteReceptorTO());
			boolean resultado = serviciosUsuarios.guardarUsuarioOrgEnt(getUsuarioTOSeleccionado());
			if(resultado){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se guardó la información"));
			}else{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Problemas al registrar la información "));			
			}
			usuarios=null;
		}
		return null;
    }
	
	/*public void doGuardarUsuarioAdministradorEnte(){		
		getUsuarioTOSeleccionado().setEstado(UsuarioEstadoEnum.ACTIVO.getEstado());
		getUsuarioTOSeleccionado().setTipo("O");
		getUsuarioTOSeleccionado().setUserActivo(true);
		//getUsuarioTOSeleccionado().setOrganoEnteReceptorTO(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO());
		boolean resultado = serviciosUsuarios.guardarUsuarioAdministradorEnt(getUsuarioTOSeleccionado());
		if(resultado){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se guardó la información"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Problemas al registrar la información "));
		}
	}*/
	
	public void limpiarMensaje(){
		FacesContext.getCurrentInstance().getMessages().remove();
	}
	
	public void buscarUsuario(){
		serviciosUsuarios.findUsuarioById(getUsuarioTOSeleccionado().getCedula());
	}
	
	public String resetearClave(){
		boolean resultado = serviciosUsuarios.getResetearClaveUsuario(getUsuarioTOSeleccionado().getUsername());
		if(resultado){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se reseteo la contraseña"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Problemas al registrar la información "));			
		}
		
		return null;
	}
	
	public String doCerraOrgano(){
		boolean resultado = serviciosUsuarios.cerrarOrgano(getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getId(), getEstructuraPresupuestariaSessionTO().getOrganoEnteReceptorTO().getTipo());
		if(resultado){
			usuarios = null;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Satisfactorio:","Se cerró el órgano"));
		}else{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error:","Problemas al registrar la información "));			
		}
		return null;
	}
	
	public void handleClose(CloseEvent event) {
		usuarioTOSeleccionado=null;
    }
}

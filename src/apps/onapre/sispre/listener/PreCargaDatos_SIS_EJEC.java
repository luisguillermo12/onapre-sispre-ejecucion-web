package apps.onapre.sispre.listener;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import ve.gob.onapre.apps.sispre.comunes.LogService;
import ve.gob.onapre.apps.sispre.comunes.LookUpNames;
import ve.gob.onapre.apps.sispre.session.comunes.ServiciosComunesLocal;
import ve.gob.onapre.comun.generico.entity.helpers.GenericoTo;
import atributes.ApplicationComunAtributes;

@WebListener
public class PreCargaDatos_SIS_EJEC implements ServletContextListener {

	@EJB(mappedName = LookUpNames.SESSION_ServiciosComunes)
	private ServiciosComunesLocal serviciosComunes;	
	
    public PreCargaDatos_SIS_EJEC() {
        // TODO Auto-generated constructor stub
    }
    
    @SuppressWarnings("unchecked")
	public void contextInitialized(ServletContextEvent arg0) {
    	ServletContext context = arg0.getServletContext();
    	int anhoPresupuestario = serviciosComunes.getAnhoPresupuestoActivo();
    	context.setAttribute(ApplicationComunAtributes.ANHO_PRESUPUESTO,new Integer(anhoPresupuestario));
    	LogService.logger.info("PreCargaDatos_SIS_EJEC:Anho_Presupuesto Cargado "+anhoPresupuestario);
    	 	
    	ArrayList<GenericoTo> rutas = serviciosComunes.getRutaReporte();
    	for(GenericoTo to : rutas){
    		if(to.getC3().equals("imagenes")){
    	    	//context.setAttribute(ApplicationComunAtributes.SAGA_RUTA_IMAGENES,to.getC2());
    	    	LogService.logger.info("PreCargaDatos_SAGA_RUTA_IMAGENES: Cargado "+to.getC2());
    		}else{
    	    	context.setAttribute(ApplicationComunAtributes.SISPRE_RUTA_REPORTE,to.getC2());
    	    	LogService.logger.info("PreCargaDatos_SAGA_RUTA_REPORTE: Cargado "+to.getC2());
    		}
    	}
    	context.setAttribute(ApplicationComunAtributes.SISPRE_ANHOS_PRESUPUESTO,serviciosComunes.getAnhosPresupuesto());
    	LogService.logger.info("PreCargaDatos_SIS_EJEC:Anhos Presupuesto");
    }
    
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub
    }
	
}

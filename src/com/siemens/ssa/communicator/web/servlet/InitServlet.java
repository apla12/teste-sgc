package com.siemens.ssa.communicator.web.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.siemens.ssa.communicator.daemons.MotorDLCC2SSA;
import com.siemens.ssa.communicator.daemons.MotorDSSSSA;
import com.siemens.ssa.communicator.daemons.MotorDadosGerais;
import com.siemens.ssa.communicator.daemons.MotorEXPCAUSSA;
import com.siemens.ssa.communicator.daemons.MotorDAINSSA;
import com.siemens.ssa.communicator.daemons.MotorIMPECSSA;
import com.siemens.ssa.communicator.daemons.MotorNRSSA;
import com.siemens.ssa.communicator.daemons.MotorSFA2SSA;
import com.siemens.ssa.communicator.daemons.MotorDLCC2SIIAF;
import com.siemens.ssa.communicator.daemons.MotorSSASIMTEMM;
import com.siemens.ssa.communicator.daemons.MotorSIMTEMMSSA;
import com.siemens.ssa.communicator.daemons.MotorSSADLCC2;
import com.siemens.ssa.communicator.daemons.MotorSSADSS;
import com.siemens.ssa.communicator.daemons.MotorSSAEXPCAU;
import com.siemens.ssa.communicator.daemons.MotorSSADAIN;
import com.siemens.ssa.communicator.daemons.MotorSSAIMPEC;
import com.siemens.ssa.communicator.daemons.MotorSSANR;
import com.siemens.ssa.communicator.daemons.MotorSSASFA2;
import com.siemens.ssa.communicator.daemons.MotorSSASIMTEMM;
import com.siemens.ssa.communicator.daemons.MotorSSATRACAU;
import com.siemens.ssa.communicator.daemons.MotorTRACAUSSA;
import com.siemens.ssa.communicator.daemons.MotorSSATRACAUDEST;
import com.siemens.ssa.communicator.daemons.MotorTRACAUDESTSSA;
import com.siemens.ssa.communicator.daemons.MotorTemporizador;

import pt.atos.engines.factories.EngineFactory;
import pt.atos.engines.impl.EngineManager;
import pt.atos.engines.utils.EngineManagementProperties;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.logging.Log;
import pt.atos.util.properties.BaseProperties;

/**
 * Servlet implementation class InitServlet
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Log log = Log.getLogger(InitServlet.class);
    
    
//	public InitServlet() {
//        super();
//        /**
//         * Registar Motores
//         */
//        log.info("xxxx Registar Motor()...:");
//        boolean toStart=new SGCProperties().getPropertyAsBoolean("autostart."+System.getProperty("weblogic.Name"), false);
//
//        if(toStart){
//log.debug("xxxxxxxxxxxxxxxxx #02 InitServlet() start.... ");
//            EnginesSSAScheduler a = new EnginesSSAScheduler();
//    		a.initEngine();
//log.debug("xxxxxxxxxxxxxxxxx #02 InitServlet() endddd... ");
//log.debug("xxxxxxxxxxxxxxxxx #03..... start.... ");
//			EnginesSDScheduler sd = new EnginesSDScheduler();
//			sd.initEngine();
//log.debug("xxxxxxxxxxxxxxxxx #03 endddd... ");
//log.debug("xxxxxxxxxxxxxxxxx #04..... start.... ");
//			EnginesIMPECScheduler sdIMPEC = new EnginesIMPECScheduler();
//			sdIMPEC.initEngine();
//log.debug("xxxxxxxxxxxxxxxxx #04 endddd... ");
//log.debug("xxxxxxxxxxxxxxxxx #05..... start.... ");
//			EnginesSSAIMPECScheduler SSAIMPEC = new EnginesSSAIMPECScheduler();
//			SSAIMPEC.initEngine();
//log.debug("xxxxxxxxxxxxxxxxx #05 endddd... ");
//
//        }
//        else
//        	log.debug("xxxxxxxxxxxxxxxxx #02 InitServlet() NOT START ");
//	}
//	
//	@Override
//    public void destroy() {
//    	log.info(">>InitServlet<< Being Destroyed clearing up engines before we go.");
//
//    	EnginesSSAScheduler a= new EnginesSSAScheduler();
//    	a.disabled();
//    	EnginesSDScheduler b= new EnginesSDScheduler();
//    	b.disabled();
//    	EnginesSSAIMPECScheduler c = new EnginesSSAIMPECScheduler();
//    	c.disabled();
//    	EnginesIMPECScheduler d = new EnginesIMPECScheduler();
//    	d.disabled();
//    	super.destroy();
//    }
//
//
//	/**
//	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}
//
//	/**
//	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
//	 */
//	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	public void init(ServletConfig config) throws ServletException {

		super.init(config);
		doWork();
	}

	private void doWork() {
		System.out.println("Inicializar PropertyManager para carregar properties base e log4j definitions");

		// Inicializar PropertyManager para carregar properties para o LDL e afins
		BaseProperties baseProperties = new BaseProperties(InitServlet.class);
		baseProperties.forceReload(true);
		new EngineManagementProperties(false).forceReload(true);
		//obrigar as properties a assumir a nossa numercao de versao 
		baseProperties.setProperty("configuration.versao", new SGCProperties().getProperty("app.version"));

		/**
		 * Registar Motores
		 */
		
		EngineFactory.registerEngine(new MotorSFA2SSA());
		EngineFactory.registerEngine(new MotorSSASFA2());
		EngineFactory.registerEngine(new MotorSSAIMPEC());
		EngineFactory.registerEngine(new MotorIMPECSSA());
		EngineFactory.registerEngine(new MotorSSASIMTEMM());
		EngineFactory.registerEngine(new MotorSIMTEMMSSA());
		EngineFactory.registerEngine(new MotorSSADLCC2());
		EngineFactory.registerEngine(new MotorDLCC2SSA());
		EngineFactory.registerEngine(new MotorDLCC2SIIAF());
		
		EngineFactory.registerEngine(new MotorSSADAIN());
		EngineFactory.registerEngine(new MotorDAINSSA());
		EngineFactory.registerEngine(new MotorSSAEXPCAU());
		EngineFactory.registerEngine(new MotorEXPCAUSSA());
		EngineFactory.registerEngine(new MotorSSATRACAU());
		EngineFactory.registerEngine(new MotorTRACAUSSA());
		EngineFactory.registerEngine(new MotorSSATRACAUDEST());
		EngineFactory.registerEngine(new MotorTRACAUDESTSSA());
		EngineFactory.registerEngine(new MotorSSADSS());
		EngineFactory.registerEngine(new MotorDSSSSA());
		EngineFactory.registerEngine(new MotorNRSSA());
		EngineFactory.registerEngine(new MotorSSANR());
		EngineFactory.registerEngine(new MotorDadosGerais());
		
		EngineFactory.registerEngine(new MotorTemporizador());
	
		/**
		 * Arranque de motores
		 */
		log.info(">>InitServlet<< SGC Engines will start");
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException ignore) {
					log.info(">>InitServlet<< Problems " + ignore.getMessage());
				}
				
				log.info(">>InitServlet<< Starting engines ");
				EngineManager srv = new EngineManager();
				srv.startMyEngines(System.getProperty("weblogic.Name"), SGCConstantes.ENGINE_APPLICATION);

				try {
					Thread.sleep(1000L);
				} catch (InterruptedException ignore) {
					log.info(">>InitServlet<< Problems " + ignore.getMessage());
				}
			}
		});
		t.start();
	}

}

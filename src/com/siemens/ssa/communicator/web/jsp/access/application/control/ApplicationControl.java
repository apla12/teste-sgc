package com.siemens.ssa.communicator.web.jsp.access.application.control;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.click.control.Column;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.web.jsp.backoffice.CacheState;
import com.siemens.ssa.communicator.web.jsp.backoffice.CircuitoDescriptor;
import com.siemens.ssa.communicator.web.jsp.backoffice.CircuitosLista;
import com.siemens.ssa.communicator.web.jsp.backoffice.DaemonConsoleStatus;
import com.siemens.ssa.communicator.web.jsp.backoffice.EcrasLista;
import com.siemens.ssa.communicator.web.jsp.backoffice.PropertiesPage;

import pt.atos.engines.impl.EngineManager;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.FrameworkJsImport;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.controls.links.FrameworkActionLink;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;
import pt.atos.web.click.utils.StaticContentUtils;


public class ApplicationControl extends DgitaLayoutPage implements Serializable {

	private static final int MAX_VIEWABLE_FILES = 2500;

    private static final long serialVersionUID = -9143978788311343341L;

    private static Log log = Log.getLogger(ApplicationControl.class);
	
	public boolean displayApplication = true;

	public String daemonPath;
	
	public String circuitoPath;
	
	public String cachePath;
	 
	public String propertiesPath;
	
	public String ecrasPath;
	
	public String descriptorURL = getContextPath()+getContext().getPagePath(CircuitoDescriptor.class);
	
	public CompleteFieldSetPanel statusContainer = new CompleteFieldSetPanel("applicatonControlPage", null);

	public CompleteFieldSetPanel engineContainer = new CompleteFieldSetPanel("engineContainer", null);
	
	public CompleteFieldSetPanel circuitoContainer = new CompleteFieldSetPanel("circuitoContainer", null);
	
	public CompleteFieldSetPanel cacheContainer = new CompleteFieldSetPanel("cacheContainer", null);
	
	public CompleteFieldSetPanel propertiesContainer = new CompleteFieldSetPanel("propertiesContainer", null);
	
	public CompleteFieldSetPanel ecrasContainer = new CompleteFieldSetPanel("ecrasContainer", null);

	public ExpandableFieldSetPanel listStatus = new ExpandableFieldSetPanel("listStatus", "Status SGC");

	public DgitaTable tabelaStatus = new DgitaTable("tabelaStatus",null,false);

	private W3Submit btToggleLogging;

	private W3Submit flush;
	
	private W3Submit testError;

	protected TabGroup group = new TabGroup("tabGroup");	
	
	public String dataInstall;
	
	public ApplicationControl() {

		super();
	}

	protected void buildPage() {

		SGCProperties props = new SGCProperties();
		tituloPage = "Gestão Sistema";
		breadCrumbPath = "Gestão Sistema";
		sistema = "[" + props.getMainProperty("configuration.ambiente") + "]" + sistema;
		dataInstall= props.getMainProperty("configuration.deploy.date");
		
		int[] flds_abstract = {1,1,1};
		
		Tab status = new Tab("status");
		status.setNumberFieldsPerLine(flds_abstract);
		status.setForm(form);
		
		Tab engines = new Tab("engines");
		engines.setNumberFieldsPerLine(flds_abstract);
		engines.setForm(form);
		
		Tab circuitoTab = new Tab("circuito");
		circuitoTab.setNumberFieldsPerLine(flds_abstract);
		circuitoTab.setForm(form);
		
		Tab cacheTab = new Tab("Properties");
		cacheTab.setNumberFieldsPerLine(flds_abstract);
		cacheTab.setForm(form);
		
		Tab ecrasTab = new Tab("ecras");
		ecrasTab.setNumberFieldsPerLine(flds_abstract);
		ecrasTab.setForm(form);
		
//		daemonPath=getContextPath()+getContext().getPagePath(DaemonStatus.class);
		daemonPath=getContextPath()+getContext().getPagePath(DaemonConsoleStatus.class);
		circuitoPath=getContextPath()+getContext().getPagePath(CircuitosLista.class);
		cachePath=getContextPath()+getContext().getPagePath(CacheState.class);
		propertiesPath=getContextPath()+getContext().getPagePath(PropertiesPage.class);
		ecrasPath = getContextPath()+getContext().getPagePath(EcrasLista.class);
		
		getHeadElements().add(new FrameworkJsImport(STATIC_JS + "jscharts.js"));
		
		FieldSetLayout layoutApplication = new FieldSetLayout(2, new String[] { "25%", "25%", "25%", "25%" });
		statusContainer.setFieldSetLayout(layoutApplication);
		engineContainer.setFieldSetLayout(layoutApplication);
		circuitoContainer.setFieldSetLayout(layoutApplication);
		cacheContainer.setFieldSetLayout(layoutApplication);
		propertiesContainer.setFieldSetLayout(layoutApplication);
		ecrasContainer.setFieldSetLayout(layoutApplication);
		
		if (StringUtils.isNotBlank(ajaxOp.getValue())) {
			return;
		}
		
		
		
// SEPARADOR STATUS - INICIO
		listStatus.setWidth("95%");
		listStatus.setStyle("PADDING-TOP", "15px");
		//listStatus
		Column col1_status = new Column("descricao", "Descrição");
		Column col2_status = new Column("count", "Contador");
		tabelaStatus.addColumn(col1_status);
		tabelaStatus.addColumn(col2_status);
		listStatus.add(tabelaStatus);
		statusContainer.add(listStatus, 2);
		buildListStatus();
// SEPARADOR STATUS - FIM
		
		//refresh_icon.gif
		FrameworkActionLink lnk_Cache = new FrameworkActionLink("lnk_Cache");
		lnk_Cache.setImageSrc(StaticContentUtils.getImageURL(false)+"refresh_icon.gif");
		lnk_Cache.setAttribute("onclick", "javascript:refreshEditorPropriedades();return false");
		
		CompleteFieldSetPanel CacheLnkContainer = new CompleteFieldSetPanel("CacheLnkContainer", null);
		
		CacheLnkContainer.add(lnk_Cache);
		cacheTab.addField(CacheLnkContainer);
		
		//refresh_icon.gif
		FrameworkActionLink lnk_engines = new FrameworkActionLink("engines_lnk");
		lnk_engines.setImageSrc(StaticContentUtils.getImageURL(false)+"refresh_icon.gif");
		lnk_engines.setAttribute("onclick", "javascript:refreshEngines();return false");
		
		CompleteFieldSetPanel engineLnkContainer = new CompleteFieldSetPanel("engineLnkContainer", null);
		
		engineLnkContainer.add(lnk_engines);
		engines.addField(engineLnkContainer);
		
		status.addField(statusContainer,"100%");
		engines.addField(engineContainer,"100%");
		circuitoTab.addField(circuitoContainer,"100%");
		cacheTab.addField(propertiesContainer,"100%");
		cacheTab.addField(cacheContainer,"100%");
		ecrasTab.addField(ecrasContainer,"100%");
		
		group.setTab(status);
		group.setTab(engines);
		group.setTab(circuitoTab);
		group.setTab(cacheTab);
		group.setTab(ecrasTab);
		
		group.setExistsTabbedForm(true);
		form.add(group);
	}	
	
	private void buildListStatus() {
		TabelasApoioServiceT apoioSrv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		ArrayList<StatusView> listStatus = new ArrayList<StatusView>();
		//TO-SSA
		StatusView retry= new StatusView();
		retry.setDescricao("Comunicações SSA a processar");
		retry.setCount(apoioSrv.getListRetryPorProcessar());
		//TO-SistemasDeclarativos
		StatusView resposta= new StatusView();
		resposta.setDescricao("Respostas para os sistemas declarativos a processar");
		resposta.setCount(apoioSrv.getRespostasSDPorProcessar());
		//TO-RegistosComERRO
		StatusView err= new StatusView();
		err.setDescricao("Registos com erro mais de 30 tentativas");
		err.setCount(apoioSrv.getRegistosComErroUp30());
		//TABELA
		listStatus.add(retry);
		listStatus.add(resposta);
		listStatus.add(err);
		if (listStatus != null && listStatus.size() > 0) {
			tabelaStatus.setRowList(listStatus);
		}	
	}


	public void onRender() {
		super.onRender();
		if (this.getContext().isAjaxRequest() || StringUtils.isNotBlank(ajaxOp.getValue())) {
			setTemplate("/jsp/ajax_template.htm");
			setHeadInclude("");
			displayApplication = false;
		}
		
		String action = getContext().getRequestParameter("action");
		//botão start/stop engine
		if(StringUtils.isNotBlank(idCarregar)&&StringUtils.isNotBlank(action)){
			
			String server = getContext().getRequest().getParameter("server");
			
			if(action.equals("Start")){
				
				try {
					new EngineManager().startEngine(System.getProperty("weblogic.Name"), idCarregar,SGCConstantes.ENGINE_APPLICATION,server,SGCConstantes.QUARTZ_SCHEDULER_NAME);
				
	                showInfoMessage("Atenção que o arranque de motores de outros nós não é refrescada na lista abaixo de forma imediata.");
	               
	           } catch (ApplicationException e) {
					log.error(e);
					showErrorMessage("Erro a arrancar o motor."+e.getMessage());
	           }
			}else if(action.equals("Stop")){
			
				try{
					new EngineManager().stopEngine(System.getProperty("weblogic.Name"), idCarregar, SGCConstantes.ENGINE_APPLICATION,SGCConstantes.QUARTZ_SCHEDULER_NAME);
		
					showInfoMessage("Atenção que o arranque de motores de outros nós não é refrescada na lista abaixo de forma imediata.");
				} catch (ApplicationException e) {
					log.error(e);
					showErrorMessage("Erro a parar o motor."+e.getMessage());
				}
			}
			
			group.setTabVisible("engines");
		}
	}

	@Override
	protected void getFormData() {

	}

	@Override
	public boolean onCancelar() {

		return false;
	}

	@Override
	public boolean onGravar() {

		return false;
	}

	@Override
	protected void setFormData() {

	}
}

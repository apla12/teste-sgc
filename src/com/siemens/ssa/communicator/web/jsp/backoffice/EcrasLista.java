package com.siemens.ssa.communicator.web.jsp.backoffice;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.ControloServiceT;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.service.interfaces.TarefasService;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.PesquisaTarefas;
import com.siemens.ssa.communicator.web.client.SGCWebClient;
import com.siemens.ssa.communicator.web.client.SGCWebClientUtil;
import com.siemens.ssa.communicator.web.jsp.access.application.control.ApplicationControl;

import net.atos.at.gestao.tarefas.entidades.PesquisaTarefa;
import net.atos.at.gestao.tarefas.entidades.PropriedadeTarefa;
import net.atos.at.gestao.tarefas.entidades.Tarefa;
import net.atos.at.gestao.tarefas.webservice.TarefaException_Exception;
import pt.atos.sgccomunicator.utils.DataSourceFactory;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.date.DateUtil;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3DateField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.panels.ButtonPanel;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.panels.PesquisaPanel;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.DateFormatColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.form.W3Form;
import pt.atos.web.click.page.DgitaSearchPage;


public class EcrasLista extends DgitaSearchPage {
	
	private static Log log = Log.getLogger(EcrasLista.class);
	
	private DataSource dataSource;
	
	W3Select sistemaPesq = new W3Select("sistemaPesq", "Sistema");
	private W3TextField estancia = new W3TextField("estancia", "Estância", false, null);
	private W3TextField estadoPesq = new W3TextField("estadoPesq", "Estado ", false, null);
	private W3TextField mrn = new W3TextField("mrn", "MRN/Número Indentificação", false, null);
	private W3DateField dt1 = new W3DateField("ecras_dt1");
	private W3DateField dt2 = new W3DateField("ecras_dt2");
	
	public String mrnG = null;
	public String  versaoG = null;
	
	class EcraTable extends DgitaTable {

		public EcraTable(String name) {
			super(name);

		}


	}

	public DgitaTable table = new EcraTable("tableList");

	public EcrasLista() throws ApplicationException {
		super();
		SGCProperties props = new SGCProperties();

		sistema = "[" + props.getMainProperty("configuration.ambiente") + "]" + sistema;
		
		dataSource = DataSourceFactory.getJndiDataSource();
	}
	
	@Override
	protected void addButtonsToSearchPanel(ButtonPanel buttons) {
		((W3Form)form).setRenderTags(false);
		buttons.removeField("pesquisa_bt1");
		buttons.removeField("clear_bt2");
		
		W3Button pesquisaButton = new W3Button("pesquisa_bt1","Pesquisar");
		pesquisaButton.setAttribute("onclick","refreshEcras();");
		
		W3Button cleanButton = new W3Button("clear_bt2","Limpar");
		cleanButton.setAttribute("onclick","apagarForm('form');");
		
		buttons.add(pesquisaButton);
		buttons.add(cleanButton);
		super.addButtonsToSearchPanel(buttons);
	}

	@Override
	protected void addFieldsToPanel(PesquisaPanel searchPanel2) {
		
		resultsTable=new EcraTable("resultsTable");
		searchPanel2.setColumns(1);
		
		// Sistema
		
		TabelasApoioServiceT srvTab = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		List<String> listaSistemas = srvTab.getSistemas();
		
		ArrayList<Option> estados = new ArrayList<Option>();
		for(String sistema : listaSistemas) {
			estados.add(new Option(sistema.equals(SGCConstantes.SISTEMA_SIMTEMM) || sistema.equals(SGCConstantes.SISTEMA_SIMTEM_VIAS) ? "SIMTEM" : sistema, sistema));
	    }
		sistemaPesq.setLabelShown(true);
		sistemaPesq.setOptionList(estados.subList(0, estados.size()));
		
		
		// Estância
		estancia.setSize(22);
		estancia.setMaxLength(22);
		estancia.setLabelShown(true);
		

		// Estado
		estadoPesq.setSize(22);
		estadoPesq.setMaxLength(22);
		estadoPesq.setLabelShown(true);
		

		// MRN2
		mrn.setSize(30);
		mrn.setMaxLength(30);
		mrn.setLabelShown(true);
		
		
		// Datas
		
		dt1.setLabelShown(true);
		dt1.setStyle("font-weight", "normal");		
		dt1.setStyle("weight", "normal");
		dt2.setLabelShown(true);
		dt2.setStyle("font-weight", "normal");
		dt2.setStyle("weight", "normal");
		
		
		CompleteFieldSetPanel panel = new CompleteFieldSetPanel("wrap",null);
		panel.setFieldSetLayout(new FieldSetLayout(3,new String[]{"15%","22%","10%","15%","15%","19%"}));
		panel.add(sistemaPesq, 1);
		panel.add(estancia, 1);
		panel.add(estadoPesq, 1);
		panel.add(mrn,1);
		panel.add(dt1, 1);
		panel.add(dt2, 1);
		
		searchPanel2.add(panel);	
	}

	@Override
	protected void initTable(DgitaTable table) {
	
		table.addColumn(new Column("tipoTarefa", "Tipo"));
		table.addColumn(new Column("numIdentificacao", "Número Indentificação"));
		table.addColumn(new Column("numProvisorio", "Número Provisorio"));
		DateFormatColumn column = new DateFormatColumn("dataCriacao", "Data Criação");
		column.setFormat(DateUtil.DATETIME_FORMAT);
		table.addColumn(column);
		table.addColumn(new Column("estado", "Estado"));
		table.addColumn(new Column("responsavel", "Utilizador"));
		table.addColumn(new Column("controlo", "Controlo"));
		AnonymousCallColumn col = new AnonymousCallColumn("Acesso");
		col.setCall(new AnonymousCall() {			
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				
				StringBuffer buf = new StringBuffer();
				StringBuffer url = null;	
								
				if(((PesquisaTarefas)row).getControlo()!= null){
					
					String estancia = null;
					String utilizador = null;
					
					if(((PesquisaTarefas)row).getPropriedades() != null) {				
						for(int x = 0; x < ((PesquisaTarefas)row).getPropriedades().size(); x++)
						{
							PropriedadeTarefa prop = (PropriedadeTarefa)  ((PesquisaTarefas)row).getPropriedades().get(x);
							
							if (prop.getNome().equals("ESTANCIA")) {
								estancia = prop.getValor() ;
							}else if(prop.getNome().equals("UTILIZADOR")) {
								utilizador = prop.getValor() ;
							}												
						}
					}
					
					String scheme = getContext().getRequest().getScheme();
					String serverName = getContext().getRequest().getServerName();
					String contextPath = getContext().getRequest().getContextPath();
					
					url =construirLinkControlo(  ((PesquisaTarefas)row).getControlo().toString()
												  , ((PesquisaTarefas)row).getNumIdentificacao().substring(0,18)
												  , Short.valueOf(((PesquisaTarefas)row).getNumIdentificacao().substring(18,20))
												  , scheme
												  , serverName
												  , contextPath
												  , utilizador
												  , estancia
												  , true);
					
					buf.append("<a href="+url+" target=\"_blank\">Aceder à declaração </a>");
				return buf.toString();
				}
				return null;
			}		
		});
		col.setDataStyle("text-align", "left");
		table.addColumn(col);
		table.setAllowJscriptFiltering(true);
		
	}

	@Override
	protected void fillTable(Form form, DgitaTable resultsTable2) {
		
		boolean existPesq=false;
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		TarefasService srvTask = EJBUtil.getSessionInterface(TarefasService.class);
		
		List<Tarefa> resTarefasList = null;
		String sistemaPesq = StringUtils.trimToNull(form.getFieldValue("sistemaPesq"));
		String estanciaValor = StringUtils.trimToNull(form.getFieldValue("estancia"));
		String estadoPesq = StringUtils.trimToNull(form.getFieldValue("estadoPesq"));
		String mrnValor = StringUtils.trimToNull(form.getFieldValue("mrn"));
		String strDt1 = StringUtils.trimToNull(form.getFieldValue("ecras_dt1"));
		String strDt2 = StringUtils.trimToNull(form.getFieldValue("ecras_dt2"));
		
		PesquisaTarefa tarefa = new PesquisaTarefa();
		PropriedadeTarefa proppriedadeTarefa = new PropriedadeTarefa();
		List<PropriedadeTarefa> proppriedadeTarefaList = new ArrayList<>(); 
		
		
		if (sistemaPesq != null){
			if (sistemaPesq.equals("null")) {
				sistemaPesq = null;
				this.setStringToAjaxErrors("Seleccione um sistema");
				return;
			}else {
				tarefa.setSistema(sistemaPesq);
			}
		}
		if (estanciaValor != null){
			if (estanciaValor.equals("null")) {
				estanciaValor = null;
			}else {
				proppriedadeTarefa.setNome("ESTANCIA");
				proppriedadeTarefa.setValor(estanciaValor);
				proppriedadeTarefaList.add(proppriedadeTarefa);
				tarefa.setPropriedades(proppriedadeTarefaList);
			}
		}
		if (estadoPesq != null){
			if (estadoPesq.equals("null")) {
				estadoPesq = null;
			}else {
				List<String> estado2 = new ArrayList<String>();
				estado2.add(estadoPesq);
				tarefa.setEstado(estado2);
			}
		}
		if (mrnValor != null){
			if (mrnValor.equals("null")) {
				mrnValor = null;
			}else{
				if (sistemaPesq.equals(SGCConstantes.SISTEMA_IMPEC) ||
						sistemaPesq.equals(SGCConstantes.SISTEMA_EXPCAU) ||
						sistemaPesq.equals(SGCConstantes.SISTEMA_TRACAU) ||
						sistemaPesq.equals(SGCConstantes.SISTEMA_TRACAUDEST) ||
						sistemaPesq.equals(SGCConstantes.SISTEMA_DSS) ||
						sistemaPesq.equals(SGCConstantes.SISTEMA_NR)) {   
					proppriedadeTarefa.setNome("MRN");
					proppriedadeTarefa.setValor(mrnValor);
					proppriedadeTarefaList.add(proppriedadeTarefa);
					tarefa.setPropriedades(proppriedadeTarefaList);
				}else if (sistemaPesq.equals(SGCConstantes.SISTEMA_DAIN)) {
					proppriedadeTarefa.setNome("NUMERO_REF_LOCAL");
					proppriedadeTarefa.setValor(mrnValor);
					proppriedadeTarefaList.add(proppriedadeTarefa);
					tarefa.setPropriedades(proppriedadeTarefaList);
				}else  {	
					ArrayList<Object[]> res = new ArrayList<Object[]>();
					try {
						res = srvInfo.getTarefaByNumIdent(mrnValor);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(res!=null){
						for (int i=0; i<res.size();i++){
							if(res.get(i)[0]!=null) {
								tarefa.setIdTarefa((String) res.get(i)[0]);
							}else if (res.get(i)[1]!=null){
								tarefa.setIdTarefa((String) res.get(i)[1]);
							}else if (res.get(i)[2]!=null){
								tarefa.setIdTarefa((String) res.get(i)[2]);
							}								
						}
					}	
				}
					
			}
		}
		if(strDt1 != null) {
			try {
				
				Date date1 = dateFormatter.parse(strDt1);
				GregorianCalendar dataG = DateUtil.fromDateToGregorianCalendar(date1);
				XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataG);				
				tarefa.setDataInicio(xmlGregCal);
			}
			catch (ParseException | DatatypeConfigurationException e) {strDt1 = null;} 
		}
		if(strDt2 != null) {
			try {
				Date date2 = dateFormatter.parse(strDt2);
				GregorianCalendar dataG = DateUtil.fromDateToGregorianCalendar(date2);
				XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataG);				
				tarefa.setDataFim(xmlGregCal);
			}
			catch (ParseException | DatatypeConfigurationException e) {strDt2 = null;}
		}
		if (sistemaPesq != null || estanciaValor != null || estadoPesq != null || mrnValor != null || strDt1 != null || strDt2 != null){
			existPesq = true;
		}
		if(existPesq){			
			try {
				ArrayList<PesquisaTarefas> arrayResTarefas = new ArrayList<PesquisaTarefas>();
				resTarefasList = srvTask.pesquisarVariasTarefas(tarefa);
				if(resTarefasList != null) {					
					for(Tarefa t : resTarefasList) {
						PesquisaTarefas resTarefas = new PesquisaTarefas();
						resTarefas.setTipoTarefa(t.getTipoTarefa());
						resTarefas.setNumIdentificacao(srvInfo.getDadosEcrasLista(t.getIdTarefa(),"N_NUM_IDENT"));
						resTarefas.setNumProvisorio(srvInfo.getDadosEcrasLista(t.getIdTarefa(),"N_PROVISORIO"));
						resTarefas.setDataCriacao(t.getDataCriacao());
						resTarefas.setEstado(t.getEstado());
						resTarefas.setControlo(srvInfo.getDadosEcrasLista(t.getIdTarefa(),"I_NUM_CTRL"));
						resTarefas.setResponsavel(srvInfo.getDadosEcrasLista(t.getIdTarefa(),"N_CONFERENTE"));
						resTarefas.setPropriedades(t.getPropriedades());
						if(resTarefas.getControlo() != null) {
							arrayResTarefas.add(resTarefas);	
						}	
					}
					
				}
			
			resultsTable2.setRowList(arrayResTarefas);
			
			} catch (TarefaException_Exception | ParseException  e) {
				log.error(e.getMessage());
			}
			
			return;
		}
	}

	protected void setFormData() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addAndCheckRoles() {

		// we do nothing here
		return true;
	}


	@Override
	public void onRender() {
		super.onRender();
		
	}
	
	public static StringBuffer construirLinkControlo (String idControlo, String mrn, Short versao, String scheme, String serverName, String contextPath, String user, String estanciaUser, boolean isReadOnly) {
		
		SGCWebClient clienteWeb = new SGCWebClient(idControlo);
			
		//LINK para botão consultar declaração
		StringBuffer linkConsulta = new StringBuffer();
		linkConsulta.append(scheme);
		linkConsulta.append("://");
		linkConsulta.append(serverName);
		linkConsulta.append(contextPath);
		  
		linkConsulta.append("/consulta/consulta.htm?mrn=");
		linkConsulta.append(mrn);
		linkConsulta.append("&versao=");
		linkConsulta.append(0+versao);
		clienteWeb.setEnderecoConsulta(linkConsulta.toString());

		//LINK para botão voltar
		StringBuffer returnLink = new StringBuffer();		
		returnLink.append(scheme);
		returnLink.append("://");
		returnLink.append(serverName);
		returnLink.append(contextPath);
		returnLink.append("/homne/gestaoTarefas.htm");							      
		clienteWeb.setEnderecoRetorno(returnLink.toString());
	          
		clienteWeb.setVerificador(user); //Verificador (pode vir a null)
		clienteWeb.setConferente(user); //Conferente
		clienteWeb.setUtilizadorLigado(user); //Utilizador que estiver logado na aplicação, poder ser diferente dos anteriores
		clienteWeb.setEstancia(estanciaUser);
	        
		Properties props = new Properties();
		String url = "/SGCCommunicatorWeb/jsp/control/ControlResult.htm?";

		StringBuffer enderecoSGC = new StringBuffer(url);
		
		enderecoSGC.append("readOnly=");
		enderecoSGC.append(isReadOnly);
		enderecoSGC.append("&autSaida=false");

		enderecoSGC.append("&");
		enderecoSGC.append(SGCWebClientUtil.NOME_ATRIBUTO_URL);
		enderecoSGC.append("=");
	        
		try {
			enderecoSGC.append( SGCWebClientUtil.codificador2URL(clienteWeb) );
		} catch (IOException e) {
//			log.error(e.getMessage());
		}
	        
	    return enderecoSGC;
	}


}

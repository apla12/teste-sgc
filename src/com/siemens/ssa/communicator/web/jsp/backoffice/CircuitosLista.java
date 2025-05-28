package com.siemens.ssa.communicator.web.jsp.backoffice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.ComunicacoesRTR;
import com.siemens.ssa.communicator.retry.constants.RetryConstants;

import pt.atos.sgccomunicator.utils.DataSourceFactory;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.date.DateUtil;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3DateField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.panels.ButtonPanel;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.panels.PesquisaPanel;
import pt.atos.web.click.controls.table.DateFormatColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.controls.table.MultiplePropertyColumn;
import pt.atos.web.click.form.W3Form;
import pt.atos.web.click.page.DgitaSearchPage;


public class CircuitosLista extends DgitaSearchPage {
	private DataSource dataSource;
	private W3TextField numSequencia = new W3TextField("numeroSequencia", "Número de Sequência", false, null);
	private W3TextField numAceitacao = new W3TextField("numeroAceitacao", "Número de Aceitação", false, null);
	W3Select estadoFase = new W3Select("estado","Estado do Pedido");
	W3Select sistemaPesqCir = new W3Select("sistemaPesqCir", "Sistema");
	private W3DateField dt1 = new W3DateField("circuito_dt1");
	private W3DateField dt2 = new W3DateField("circuito_dt2");
	
	class CircuitoTable extends DgitaTable {

		public CircuitoTable(String name) {
			super(name);

		}

		@Override
		protected void addRowAttributes(Map attributes, Object row, int rowIndex) {
			StringBuffer sb = new StringBuffer();
			sb.append("showLoadDiv(Event.pointerX(event),Event.pointerY(event));");
			sb.append("new Ajax.Updater('dataAvailable', '" + getContextPath() + getContext().getPagePath(CircuitoDescriptor.class) + "', {");
			sb.append("	  parameters: { ID: '" + ((ComunicacoesRTR) row).getChave().getNumSequencia() + "' ,showLinks: 'true'}");
			sb.append("	  ,onComplete:function(x,y){$('dataAvailable').show();hideLoadDiv();}");
			sb.append("	  ,evalScripts:false"); 
			sb.append("				});");
			attributes.put("onClick", sb.toString());
		}

	}

	public DgitaTable table = new CircuitoTable("tableList");

	public CircuitosLista() throws ApplicationException {
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
		pesquisaButton.setAttribute("onclick","refreshCircuito();");
		
		W3Button cleanButton = new W3Button("clear_bt2","Limpar");
		cleanButton.setAttribute("onclick","apagarForm('form');");
		
		buttons.add(pesquisaButton);
		buttons.add(cleanButton);
		super.addButtonsToSearchPanel(buttons);
	}

	@Override
	protected void addFieldsToPanel(PesquisaPanel searchPanel2) {
		resultsTable=new CircuitoTable("resultsTable");
		searchPanel2.setColumns(1);
		
		// Número de referência local
		numAceitacao.setSize(22);
		numAceitacao.setMaxLength(22);
		numAceitacao.setLabelShown(true);
		

		// Número provisório
		numSequencia.setSize(22);
		numSequencia.setMaxLength(22);
		numSequencia.setLabelShown(true);
		
		ArrayList<Option> estados = new ArrayList<Option>();
		estadoFase.setLabelShown(true);
		estados.add(new Option("null","---"));
		estados.add(new Option(RetryConstants.ESTADO_PEDIDO_ENVIADO,"Enviado (P)"));
		estados.add(new Option(RetryConstants.ESTADO_PEDIDO_EM_CURSO,"Em Curso (C)"));
		estados.add(new Option(RetryConstants.ESTADO_PEDIDO_COM_ERRO,"Erro (E)"));
		estados.add(new Option(RetryConstants.ESTADO_PEDIDO_FALHOU,"Falhou (F)"));
		estados.add(new Option(RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,"Não Processado (N)"));
		estados.add(new Option(RetryConstants.ESTADO_PEDIDO_RESPONDIDO,"Respondido (R)"));
		estadoFase.setOptionList(estados.subList(0, estados.size()));
		
		
		// Sistema
		
		TabelasApoioServiceT srvTab = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		List<String> listaSistemas = srvTab.getSistemas();
		
		ArrayList<Option> sistemas = new ArrayList<Option>();
		sistemaPesqCir.setLabelShown(true);
	    sistemas.add(new Option("null","---"));
	    for(String sistema : listaSistemas) {
	    	sistemas.add(new Option(sistema, sistema));
	    }
	    sistemaPesqCir.setOptionList(sistemas.subList(0, sistemas.size()));
	    
	    // Datas
	    
	    dt1.setLabelShown(true);
		dt1.setRenderTime(true);		
		dt1.setStyle("font-weight", "normal");		
		dt1.setStyle("weight", "normal");
		dt2.setLabelShown(true);
		dt2.setRenderTime(true);
		dt2.setStyle("font-weight", "normal");		
		dt2.setStyle("weight", "normal");
		
		CompleteFieldSetPanel panel = new CompleteFieldSetPanel("wrap",null);
		panel.setFieldSetLayout(new FieldSetLayout(3,new String[]{"15%","22%","10%","15%","15%","19%"}));
		panel.add(numSequencia, 1);
		panel.add(numAceitacao, 1);
		panel.add(estadoFase);
		panel.add(sistemaPesqCir);
		panel.add(dt1, 1);
		panel.add(dt2, 1);
		
		searchPanel2.add(panel);	
	}

	@Override
	protected void initTable(DgitaTable table) {
	
		table.addColumn(new Column("chave.numSequencia", "Número de Sequencia"));
		table.addColumn(new Column("numAceitacao", "Numero Aceitacao"));
		MultiplePropertyColumn mc2 = new MultiplePropertyColumn("comunicacaoEstado","Comunicação (Estado)");
		mc2.addProperty("tipoComunicacao");
		mc2.addProperty("estadoPedido");
		mc2.addProperty("emissor");
		mc2.addProperty("receptor");
		mc2.setFormat("<%tipoComunicacao%> ( <%estadoPedido%> ) <%emissor%> - <%receptor%>");
		table.addColumn(mc2);
		DateFormatColumn column = new DateFormatColumn("dataEnvio", "Envio");
		column.setFormat(DateUtil.DATETIME_FORMAT);
		table.addColumn(column);
		DateFormatColumn column1 = new DateFormatColumn("dataRecepcao", "Recepção");
		column1.setFormat(DateUtil.DATETIME_FORMAT);
		table.addColumn(column1);
		table.addColumn(new Column("msgErro", "Mensagem"));
		table.addColumn(new Column("numeroTentativas", "Contador Tentativas"));
		DateFormatColumn column2 = new DateFormatColumn("timeStamp", "Data de alteração");
		column2.setFormat(DateUtil.DATETIME_FORMAT);
		table.addColumn(column2);
		table.setAllowJscriptFiltering(true);
		
	}

	@Override
	protected void fillTable(Form form, DgitaTable resultsTable2) {
		
		boolean existPesq=false;
		
		TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		ArrayList<ComunicacoesRTR> res = new ArrayList<ComunicacoesRTR>();
		String numAceit = StringUtils.trimToNull(form.getFieldValue("numeroAceitacao"));
		String numSequencia = StringUtils.trimToNull(form.getFieldValue("numeroSequencia"));
		String estado = StringUtils.trimToNull(form.getFieldValue("estado"));
		String sistemaPesqCir = StringUtils.trimToNull(form.getFieldValue("sistemaPesqCir"));		
		String strDt1 = StringUtils.trimToNull(form.getFieldValue("circuito_dt1"));
		String strDt2 = StringUtils.trimToNull(form.getFieldValue("circuito_dt2"));
		
		if (estado != null){
			if (estado.equals("null"))
				estado = null;
		}
		
		if (sistemaPesqCir != null){
			if (sistemaPesqCir.equals("null")) {
				sistemaPesqCir = null;
		 }
		}
		
		if (numAceit != null || numSequencia != null || estado != null || sistemaPesqCir != null || strDt1 != null || strDt2 != null) {
			existPesq = true;
			if (numAceit != null){
				String[] numAceitSplit;
				if (numAceit.contains("/")){
					numAceitSplit = numAceit.split("/");
					numAceit = numAceitSplit[0]+"PT";
					for(int i=0; i<6-numAceitSplit[1].length();i++){
						numAceit += "0";
					}
					numAceit += numAceitSplit[1];
					for(int i=0; i<8-numAceitSplit[2].length();i++){
						numAceit += "0";
					}
					numAceit += numAceitSplit[2];
				}
			}
		}
		if(existPesq){
			
			try {
 				res = srvInfo.getRTR(numAceit, numSequencia, estado, sistemaPesqCir, strDt1, strDt2);
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultsTable2.setRowList(res);
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

}

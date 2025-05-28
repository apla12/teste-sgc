package com.siemens.ssa.communicator.web.jsp.access.application.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.rpc.ServiceException;

import org.apache.click.control.Option;
import org.apache.click.element.JsImport;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.ControloServiceT;
import com.siemens.service.interfaces.RetryServiceT;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.service.interfaces.TarefasService;
import com.siemens.ssa.communicator.pojo.interfaces.ComunicacoesRTRD;
import com.siemens.ssa.communicator.retry.constants.RetryConstants;
import com.siemens.ssa.communicator.web.jsp.backoffice.DaemonConsoleStatus;

import net.atos.at.gestao.tarefas.entidades.PropriedadeTarefa;
import net.atos.at.gestao.tarefas.entidades.Tarefa;
import net.atos.at.gestao.tarefas.webservice.TarefaException_Exception;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.W3IntegerField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class AccessDirect extends DgitaLayoutPage implements Serializable {
	
	private Log log = Log.getLogger(AccessDirect.class); 
	
	public boolean displayApplication = true;
	public String daemonPath;
	
//	public String descriptorURL=getContextPath() + getContext().getPagePath(CircuitoDescriptor.class);
	public CompleteFieldSetPanel statusContainer = new CompleteFieldSetPanel("accessDirectPage", null);
	public ExpandableFieldSetPanel listStatus = new ExpandableFieldSetPanel("listStatus", "Status STADA Importação");
	public DgitaTable tabelaStatus = new DgitaTable("tabelaStatus",null,false);
	protected TabGroup group = new TabGroup("tabGroup");	
	 
	public String dataInstall;
	
//GERAL - START
	
	public ExpandableFieldSetPanel atualizarControlo = new ExpandableFieldSetPanel("atualizarControlo", "Atualizar Controlo");
	public ExpandableFieldSetPanel atualizarRTR = new ExpandableFieldSetPanel("atualizarRTR", "Atualizar RTR");
	public ExpandableFieldSetPanel atualizarMatriz = new ExpandableFieldSetPanel("atualizarMatriz", "Atualizar Matriz");
	public ExpandableFieldSetPanel inserirMatriz = new ExpandableFieldSetPanel("inserirMatriz", "Inserir Matriz");
	
//GERAL - END
	
//GESTAR - START
	public ExpandableFieldSetPanel criarTarefa = new ExpandableFieldSetPanel("criarTarefa", "Criar Tarefa(GESTAR)");
	public ExpandableFieldSetPanel eliminarTarefa = new ExpandableFieldSetPanel("eliminarTarefa", "Eliminar Tarefa(GESTAR)");
	public ExpandableFieldSetPanel concluirTarefa = new ExpandableFieldSetPanel("concluirTarefa", "Concluir Tarefa(GESTAR)");
	public ExpandableFieldSetPanel renomearTarefa = new ExpandableFieldSetPanel("renomearTarefa", "Renomear Tarefa(GESTAR)");
	
//GESTAR - END
	
//SSA - START
	//RTR
	public ExpandableFieldSetPanel reprocessarRTR = new ExpandableFieldSetPanel("reprocessarRTR1", "Processar RTR (SSA)");
	W3IntegerField retry= new W3IntegerField("retry");
	
	
	//RESET#1
	public ExpandableFieldSetPanel ssaReset1 = new ExpandableFieldSetPanel("suportResetSSA1", "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

	//Pedido XML
	public ExpandableFieldSetPanel updateXMLRTRD = new ExpandableFieldSetPanel("updateXMLRTRD", "Atualizar XML RTRD");
	W3IntegerField numSeqRTRD= new W3IntegerField("numSeqRTRD");
	W3TextArea oldTextRTRD= new W3TextArea("oldTextRTRD","Antigo",false);
	W3TextArea newTextRTRD= new W3TextArea("newTextRTRD","Novo",false);
	W3Submit updateXMLRTRDBT = new W3Submit("updatePedidoXMLRTRD",this,"updatePedidoXMLRTRD");
//SSA - END
	
	
// HIST DATICOB - START
	
	public ExpandableFieldSetPanel historialDaticob = new ExpandableFieldSetPanel("historialDaticob", "Cria Histórico da tabela HIST_RTR0 - 30 dias");
	W3Submit executeHist = new W3Submit("executeHist","Criar historico",this,"executeHist");
	W3TextField verificadorChave = new W3TextField("verificadorChave","chave",false); 
	
// HIST DATICOB - END
	
	public AccessDirect() {

		super();
	}

	protected void buildPage() {
		
//SEPARADORES-START
		
		Tab geral = new Tab("GERAL");
		int[] flds_abstractGeral = {1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		geral.setNumberFieldsPerLine(flds_abstractGeral);
		geral.setForm(form);
		
		Tab gestar = new Tab("GESTAR");
		int[] flds_abstractG = {1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		gestar.setNumberFieldsPerLine(flds_abstractG);
		gestar.setForm(form);
		
		Tab toSSA = new Tab("ToSSA");
		int[] flds_abstract_ssa = {1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		toSSA.setNumberFieldsPerLine(flds_abstract_ssa);
		toSSA.setForm(form);
		
		Tab histdaticob = new Tab("Expurgo");
		int[] flds_abstract_hiscob = {1,1,1,1};
		histdaticob.setNumberFieldsPerLine(flds_abstract_hiscob);
		histdaticob.setForm(form);
		
//SEPARADORES-END	
		
		daemonPath=getContextPath()+getContext().getPagePath(DaemonConsoleStatus.class);
		
		getHeadElements().add(new JsImport(STATIC_JS + "jscharts.js"));

		FieldSetLayout layoutApplication = new FieldSetLayout(2, new String[] {"25%","25%","25%","25%"});
		
		if (StringUtils.isNotBlank(ajaxOp.getValue())) {
			return;
		}
		SGCProperties props = new SGCProperties();
		tituloPage = "Gestão Sistema";
		breadCrumbPath = "Gestão Sistema";
		sistema = "[" + props.getMainProperty("configuration.ambiente") + "]" + sistema;
		dataInstall= props.getMainProperty("configuration.deploy.date");
		
		String scheme=getContext().getRequest().getScheme();
				
// SEPARADOR ACESSO DIRECTO - INICIO
		
		listStatus.setWidth("95%");
		listStatus.setStyle("PADDING-TOP", "15px");

//######   GERAL - START   ######

		//Atualizar Controlo
		W3TextField numeroAceitacaoControlo = new W3TextField("numeroAceitacaoControlo","Nº de Aceitação",false);
		W3Select sistemaControlo = new W3Select("sistemaControlo","Sistema",false);
		W3TextField conferente = new W3TextField("conferente","Conferente",false);
		W3TextField verificador = new W3TextField("verificador","Verificador",false);
		W3TextField identificadorTarefa = new W3TextField("identificadorTarefa","Identificador de Tarefa",false);
		W3Submit atualizarControloBT = new W3Submit("Atualizar Controlo",this,"atualizarControlo");
		
		sistemaControlo.add(new Option("SFA", "SFA"));
		sistemaControlo.add(new Option("STADA-IMP", "STADA-IMP"));
		sistemaControlo.add(new Option("STADA-EXP", "STADA-EXP"));
		sistemaControlo.add(new Option("ECOM", "ECOM"));	//------------------------
		
		atualizarControlo.setFieldSetLayout(layoutApplication);
		atualizarControlo.setColumns(1);
		atualizarControlo.add(numeroAceitacaoControlo);
		atualizarControlo.add(sistemaControlo);
		atualizarControlo.add(conferente);
		atualizarControlo.add(verificador);
		atualizarControlo.add(identificadorTarefa);
		atualizarControlo.add(atualizarControloBT);
		
		//Atualizar RTR
		W3TextField numeroSequenciaRTR = new W3TextField("numeroSequenciaRTR","Nº de Sequência",false);
		W3Select estado = new W3Select("estado", "Estado do Pedido");
		W3Submit atualizarRTRBT = new W3Submit("Atualizar RTR",this,"atualizarRTR");
		
		estado.add(new Option(RetryConstants.ESTADO_PEDIDO_ENVIADO,"Enviado (P)"));
		estado.add(new Option(RetryConstants.ESTADO_PEDIDO_EM_CURSO,"Em Curso (C)"));
		estado.add(new Option(RetryConstants.ESTADO_PEDIDO_COM_ERRO,"Erro (E)"));
		estado.add(new Option(RetryConstants.ESTADO_PEDIDO_FALHOU,"Falhou (F)"));
		estado.add(new Option(RetryConstants.ESTADO_PEDIDO_RESPONDIDO,"Respondido (R)"));
		
		atualizarRTR.setFieldSetLayout(layoutApplication);
		atualizarRTR.add(numeroSequenciaRTR);
		atualizarRTR.add(estado);
		atualizarRTR.add(atualizarRTRBT);
		
		//Atualizar Matriz
		W3TextField atualizarCodigoSeparadorAntigo = new W3TextField("atualizarCodigoSeparadorAntigo","Código Separador Antigo",false);
		W3TextField atualizarCodigoSeparadorNovo = new W3TextField("atualizarCodigoSeparadorNovo","Código Separador Novo",false);
		W3TextField atualizarSeparador = new W3TextField("atualizarSeparador","Nome Separador",false);
		W3Select atualizarSistemaMatriz = new W3Select("atualizarSistemaMatriz","Sistema",false);
		W3Submit atualizarMatrizBT = new W3Submit("Atualizar Matriz",this,"atualizarMatriz");
		
		atualizarSistemaMatriz.add(new Option("SFA", "SFA"));
		atualizarSistemaMatriz.add(new Option("STADA-IMP", "STADA-IMP"));
		atualizarSistemaMatriz.add(new Option("STADA-EXP", "STADA-EXP"));
		atualizarSistemaMatriz.add(new Option("ECOM", "ECOM"));	//-------------------
		
		atualizarMatriz.setFieldSetLayout(layoutApplication);
		atualizarMatriz.setColumns(1);
		atualizarMatriz.add(atualizarCodigoSeparadorAntigo);
		atualizarMatriz.add(atualizarCodigoSeparadorNovo);
		atualizarMatriz.add(atualizarSeparador);
		atualizarMatriz.add(atualizarSistemaMatriz);
		atualizarMatriz.add(atualizarMatrizBT);
		
		//Inserir Matriz
		W3TextField inserirCodigoSeparador = new W3TextField("inserirCodigoSeparador","Código Separador",false);
		W3TextField inserirSeparador = new W3TextField("inserirSeparador","Nome Separador",false);
		W3Select inserirSistemaMatriz = new W3Select("inserirSistemaMatriz","Sistema",false);
		W3Submit inserirMatrizBT = new W3Submit("Inserir Matriz",this,"inserirMatriz");
		
		inserirSistemaMatriz.add(new Option("SFA", "SFA"));
		inserirSistemaMatriz.add(new Option("STADA-IMP", "STADA-IMP"));
		inserirSistemaMatriz.add(new Option("STADA-EXP", "STADA-EXP"));
		inserirSistemaMatriz.add(new Option("ECOM", "ECOM"));	//--------------------
		
		inserirMatriz.setFieldSetLayout(layoutApplication);
		inserirMatriz.setColumns(1);
		inserirMatriz.add(inserirCodigoSeparador);
		inserirMatriz.add(inserirSeparador);
		inserirMatriz.add(inserirSistemaMatriz);
		inserirMatriz.add(inserirMatrizBT);
		
//######   GERAL - END   ######
		
		
//######   GESTAR - START   ######
		
		// Criar Tarefa(GESTAR)
		W3Select tipoTarefa = new W3Select("tipoTarefa","Tipo de Tarefa",false);
		W3IntegerField estancia= new W3IntegerField("estancia", "Estancia");
		W3TextField taskDescricao= new W3TextField("taskDesc","Descrição",false);
		W3TextField numAceita= new W3TextField("numAceita","Nº. Aceitação",false);
		W3TextField numRef= new W3TextField("numRef","Nº. Ref. Local",false);
		W3IntegerField numProv= new W3IntegerField("numProv","Nº. Provisório");
		W3IntegerField anoProv= new W3IntegerField("anoProv","Ano Provisório");
		W3Select sistemaCriar= new W3Select("sistemaCriar", "Sistema");
		W3Submit criarTarefaBT = new W3Submit("Criar Tarefa",this,"criarTarefa");
		
		criarTarefa.setColumns(1);
		tipoTarefa.setLabel("Tipo Tarefa");
		tipoTarefa.setLabelShown(true);
		tipoTarefa.add("---");
		tipoTarefa.add(new Option("1", "Aceitação"));
		tipoTarefa.add(new Option("2", "Controlo Documental"));
		tipoTarefa.add(new Option("3", "Controlo Fisico"));
		tipoTarefa.add(new Option("4", "CAP"));
		tipoTarefa.add(new Option("5", "Controlo Finalizado"));
		tipoTarefa.add(new Option("6", "Analise de REsposta"));
		tipoTarefa.add(new Option("7", "Controlo Pendencias"));
		tipoTarefa.add(new Option("8", "Pedido de Alteração"));
		tipoTarefa.add(new Option("9", "Pedido Anulação"));
		tipoTarefa.add(new Option("10", "Autorização de Saída"));
		tipoTarefa.add(new Option("11", "Complementar"));
		tipoTarefa.add(new Option("12", "Arquivo"));
		tipoTarefa.add(new Option("13", "Contingente"));
		tipoTarefa.add(new Option("14", "Aceitação Deferimento"));
		tipoTarefa.add(new Option("15", "Registo de Liquidação"));
		tipoTarefa.add(new Option("16", "Proposta de Revisão"));
		tipoTarefa.add(new Option("17", "Proposta de Rectificação"));
		tipoTarefa.add(new Option("18", "Deferimento Nível Superior"));
		tipoTarefa.add(new Option("19", "Anulação"));
		tipoTarefa.add(new Option("20", "Rectificação não estruturada"));
		tipoTarefa.add(new Option("21", "Revisão não estruturada"));
		taskDescricao.setMaxLength(200);
		taskDescricao.setSize(100);
		taskDescricao.setLabelShown(true);
		estancia.setMaxLength(3);
		estancia.setSize(3);
		estancia.setLabel("Estância");
		estancia.setLabelShown(true);
		numAceita.setLabel("Nº. Aceitação");
		numAceita.setLabelShown(true);
		numAceita.setSize(20);
		numRef.setLabel("Nº. Ref. Local");
		numRef.setLabelShown(true);
		numRef.setSize(22);
		numProv.setMaxLength(10);
		numProv.setSize(10);
		numProv.setLabelShown(true);	
		anoProv.setMaxLength(4);
		anoProv.setSize(4);
		anoProv.setLabelShown(true);
		sistemaCriar.add(new Option("SFA", "SFA"));
		sistemaCriar.add(new Option("STADA-IMP", "STADA-IMP"));
		sistemaCriar.add(new Option("STADA-EXP", "STADA-EXP"));		
		sistemaCriar.add(new Option("ECOM", "ECOM"));	//-----------------
		
		criarTarefa.setFieldSetLayout(layoutApplication);
		criarTarefa.add(tipoTarefa);
		criarTarefa.add(taskDescricao);
		criarTarefa.add(estancia);
		criarTarefa.add(numAceita);
		criarTarefa.add(numRef);
		criarTarefa.add(numProv);
		criarTarefa.add(anoProv);
		criarTarefa.add(sistemaCriar);
		criarTarefa.add(criarTarefaBT);
		
		// Eliminar Tarefa(GESTAR)
		W3TextField idTarefaEliminar= new W3TextField("idTarefaEliminar","Tarefa",false);
		W3Select sistemaEliminar= new W3Select("sistemaEliminar", "Sistema");
		W3Submit eliminarTarefaBT = new W3Submit("Eliminar Tarefa",this,"eliminarTarefa");
		
		idTarefaEliminar.setLabel("Tarefa");
		idTarefaEliminar.setLabelShown(true);
		idTarefaEliminar.setSize(100);
		sistemaEliminar.add(new Option("SFA", "SFA"));
		sistemaEliminar.add(new Option("STADA-IMP", "STADA-IMP"));
		sistemaEliminar.add(new Option("STADA-EXP", "STADA-EXP"));		
		sistemaEliminar.add(new Option("ECOM", "ECOM"));	//-----------------------
		
		eliminarTarefa.setFieldSetLayout(layoutApplication);
		eliminarTarefa.add(idTarefaEliminar);
		eliminarTarefa.add(sistemaEliminar);
		eliminarTarefa.add(eliminarTarefaBT);
		
		// Concluir Tarefa(GESTAR)
		W3TextField idTarefaConcluir= new W3TextField("idTarefaConcluir","Tarefa",false);
		W3Select sistemaConcluir= new W3Select("sistemaConcluir", "Sistema");
		W3Submit concluirTarefaBT = new W3Submit("Concluir Tarefa",this,"concluirTarefa");
		
		sistemaConcluir.add(new Option("SFA", "SFA"));
		sistemaConcluir.add(new Option("STADA-IMP", "STADA-IMP"));
		sistemaConcluir.add(new Option("STADA-EXP", "STADA-EXP"));
		sistemaConcluir.add(new Option("ECOM", "ECOM"));	//---------------------
		
		idTarefaConcluir.setLabel("Tarefa");
		idTarefaConcluir.setLabelShown(true);
		idTarefaConcluir.setSize(100);
		
		concluirTarefa.setFieldSetLayout(layoutApplication);
		concluirTarefa.add(idTarefaConcluir);
		concluirTarefa.add(sistemaConcluir);
		concluirTarefa.add(concluirTarefaBT);
		
		// Renomear Tarefa(GESTAR)
		W3TextField idTarefaRenomear= new W3TextField("idTarefaRenomear","Tarefa",false);
		W3Select sistemaRenomear= new W3Select("sistemaRenomear", "Sistema");
		W3TextField user= new W3TextField("user","Utilizador",false);
		W3Submit renomearTarefaBT = new W3Submit("Renomear Tarefa",this,"reatribuirTarefa");
		
		sistemaRenomear.add(new Option("SFA", "SFA"));
		sistemaRenomear.add(new Option("STADA-IMP", "STADA-IMP"));
		sistemaRenomear.add(new Option("STADA-EXP", "STADA-EXP"));
		sistemaRenomear.add(new Option("ECOM", "ECOM"));	//------------------------
		
		idTarefaRenomear.setLabel("Tarefa");
		idTarefaRenomear.setLabelShown(true);
		idTarefaRenomear.setSize(100);
		
		renomearTarefa.setFieldSetLayout(layoutApplication);
		renomearTarefa.setColumns(1);
		renomearTarefa.add(idTarefaRenomear);
		renomearTarefa.add(user);
		renomearTarefa.add(sistemaRenomear);
		renomearTarefa.add(renomearTarefaBT);
		
//######   GESTAR - END    ######

//######   ToSSA - START   ######
		
		//SSA RETRY
		W3Submit reprocessarRTRBT = new W3Submit("reprocessarRTR",this,"reprocessarRTR");
		reprocessarRTR.setColumns(1);
		reprocessarRTR.setFieldSetLayout(layoutApplication);
		retry.setMaxLength(10);
		retry.setSize(10);
		retry.setLabel("Comunicação");
		retry.setLabelShown(true);
		reprocessarRTR.add(retry);
		reprocessarRTR.add(reprocessarRTRBT);
		
		//Pedido XML RTRD
		updateXMLRTRD.setFieldSetLayout(layoutApplication);
		updateXMLRTRD.setColumns(1);
		numSeqRTRD.setMaxLength(10);
		numSeqRTRD.setSize(10);
		numSeqRTRD.setLabel("NumSequencia");
		numSeqRTRD.setLabelShown(true);
		oldTextRTRD.setMaxLength(5000);
		oldTextRTRD.setCols(100);
		//oldTextRTRD.setSize(100);
		//oldTextRTRD.setLabel("Antigo");
		//oldTextRTRD.setLabelShown(true);
		newTextRTRD.setMaxLength(5000);
		newTextRTRD.setCols(100);
		//newTextRTRD.setSize(100);
		//newTextRTRD.setLabel("Novo");
		//newTextRTRD.setLabelShown(true);
				
		updateXMLRTRD.add(numSeqRTRD);
		updateXMLRTRD.add(oldTextRTRD);
		updateXMLRTRD.add(newTextRTRD);
		updateXMLRTRD.add(updateXMLRTRDBT);
		
//######   ToSSA - END   ######		
		
//######   Hist Daticob - START ##### 		
		
		historialDaticob.setColumns(1);
		historialDaticob.setFieldSetLayout(layoutApplication);
		executeHist.setLabel("Criar Historico");
		verificadorChave.setLabel("Chave");
		historialDaticob.add(verificadorChave);
		historialDaticob.add(executeHist);
		
//######   Hist Daticob - END ##### 			
		
//ADICIONAR PAINEIS AO SEPARADOR - START
		
		//GERAL
		geral.addField(inserirMatriz);
		geral.addField(atualizarMatriz);
		geral.addField(atualizarControlo);
		geral.addField(atualizarRTR);
		
		//GESTAR		
		gestar.addField(criarTarefa);
		gestar.addField(eliminarTarefa);
		gestar.addField(concluirTarefa);
		gestar.addField(renomearTarefa);

		//SSA
		toSSA.addField(reprocessarRTR);;//ToPRD-3.02.009
		toSSA.addField(updateXMLRTRD);		
		
		//HIST DATICOB
		histdaticob.addField(historialDaticob);
		
//ADICIONAR PAINEIS AO SEPARADOR - END
// SEPARADOR ACESSO DIRECTO - FIM
		
//ORDEM DOS SEPARADORES
		group.setTab(geral);
		group.setTab(gestar);
		group.setTab(toSSA);
		group.setTab(histdaticob);
		group.setExistsTabbedForm(true);
		form.add(group);
	}	

	/****************************************************************/
	//Eventos dos butões
	/**
	 * @throws ApplicationException **************************************************************/
	
//	GERAL - START
	
	public boolean atualizarControlo() throws ApplicationException{
		
		String numeroAceitacao = form.getFieldValue("numeroAceitacaoControlo");
		String sistema = form.getFieldValue("sistemaControlo");
		String conferente = form.getFieldValue("conferente");
		String verificador = form.getFieldValue("verificador");
		String identificadorTarefa = form.getFieldValue("identificadorTarefa");
		
		if((StringUtils.isNotBlank(numeroAceitacao))
				&& (StringUtils.isNotBlank(sistema)
				|| StringUtils.isNotBlank(conferente)
				|| StringUtils.isNotBlank(verificador)
				|| StringUtils.isNotBlank(identificadorTarefa))){
			
			ControloServiceT ControloSvc = EJBUtil.getSessionInterface(ControloServiceT.class);
			ControloSvc.updateControlo(numeroAceitacao, sistema, conferente, verificador, identificadorTarefa);
		}
		
		return true;
		
	}
	
	public boolean atualizarRTR(){

		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		Long iNumSeq = new Long(form.getFieldValue("numeroSequenciaRTR"));
		String estado = form.getFieldValue("estado");
		
		if(iNumSeq != null){				
			
			int isOK=srv.updateEstado(estado,iNumSeq);
			if(isOK<0){
				showErrorMessage("Erro a reprocesar!");
				return false;
			}else{
				showSucessMessage(iNumSeq+" Reprocessado");
				form.clearValues();
				return true;
			}
		}	
		showErrorMessage("Número sequencial inválido!");
		return false;
		
	}
	
	public boolean atualizarMatriz() throws ApplicationException{
		
		String codSeparadorAntigo = form.getFieldValue("atualizarCodigoSeparadorAntigo");
		String codSeparadorNovo = form.getFieldValue("atualizarCodigoSeparadorNovo");
		String separador = form.getFieldValue("atualizarSeparador");
		String sistema = form.getFieldValue("atualizarSistemaMatriz");
		
		if(StringUtils.isNotBlank(codSeparadorAntigo)
				&& StringUtils.isNotBlank(codSeparadorNovo)
				&& StringUtils.isNotBlank(sistema)){
			
			TabelasApoioServiceT srvTab = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
			return srvTab.updateMatriz(sistema, codSeparadorNovo, codSeparadorAntigo, separador);
		}
		
		return false;	
	}
	
	public boolean inserirMatriz() throws ApplicationException{
		
		String codSeparador = form.getFieldValue("inserirCodigoSeparador");
		String separador = form.getFieldValue("inserirSeparador");
		String sistema = form.getFieldValue("inserirSistemaMatriz");
		
		if(StringUtils.isNotBlank(codSeparador)
				&& StringUtils.isNotBlank(sistema)){
			
			TabelasApoioServiceT srvTab = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
			return srvTab.insertMatriz(sistema, codSeparador, separador);
			
		}
		
		return true;
		
	}
	
	
//	GERAL - END
	
// GESTAR - START
	//Criar Tarefa GESTAR
	public boolean criarTarefa(){
		
		String tipoTarefa 	= form.getFieldValue("tipoTarefa");
		String descricao 	= form.getFieldValue("taskDesc");
		String estanciaAdua = form.getFieldValue("estancia");
		String numAceita 	= form.getFieldValue("numAceita");
		String numRef 		= form.getFieldValue("numRef");
		String numProvisorio= form.getFieldValue("numProv");
		String anoProvisorio= form.getFieldValue("anoProv");
		String sistema 		= form.getFieldValue("sistemaCriar");
		
		if(StringUtils.isNotBlank(estanciaAdua)
				&& StringUtils.isNotBlank(descricao)
				&& (StringUtils.isNotBlank(tipoTarefa) && !StringUtils.equals(tipoTarefa, "---"))
				&& StringUtils.isNotBlank(numRef)
				&& StringUtils.isNotBlank(numProvisorio)
				&& StringUtils.isNotBlank(anoProvisorio)
				&& StringUtils.isNotBlank(sistema)){	
			
			Tarefa preparaTarefaManual = new  Tarefa();
			List props = new ArrayList();
			
			PropriedadeTarefa numProv = new PropriedadeTarefa();
			PropriedadeTarefa estancia = new PropriedadeTarefa();
			PropriedadeTarefa anoProv = new PropriedadeTarefa();
			PropriedadeTarefa numAceitacao = new PropriedadeTarefa();
			PropriedadeTarefa numRefLocal = new PropriedadeTarefa();
			estancia.setNome(SGCConstantes.GESTAR_ESTANCIA);
			estancia.setValor(estanciaAdua);
			props.add(estancia);
			if(StringUtils.isNotBlank(numAceita)){
				numAceitacao.setNome(SGCConstantes.GESTAR_NUMERO_ACEITACAO);
				numAceitacao.setValor(numAceita);
				props.add(numAceitacao);
			}
			numProv.setNome(SGCConstantes.GESTAR_NUMERO_PROVISORIO);
			numProv.setValor(numProvisorio);
			props.add(numProv);
			anoProv.setNome(SGCConstantes.GESTAR_ANO_PROVISORIO);
			anoProv.setValor(anoProvisorio);
			props.add(anoProv);	
			numRefLocal.setNome(SGCConstantes.GESTAR_NUMERO_REF_LOCAL);
			numRefLocal.setValor(numRef);
			props.add(numRefLocal);
			
			preparaTarefaManual.setTipoTarefa(tipoTarefa);
			preparaTarefaManual.setPropriedades(props);
			preparaTarefaManual.setNumeroTarefa(numProvisorio+"/"+anoProvisorio);
			preparaTarefaManual.setDescricao(descricao);

			TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
			String idTarefa=null;
			try {
				idTarefa = tarefaSrv.criarTarefa(preparaTarefaManual, null, null, sistema);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				showErrorMessage("EXCEPTION a criar tarefa:"+e.getMessage());
				return false;
			} 		
			
			if(StringUtils.isBlank(idTarefa)){
				showErrorMessage("Erro a criar tarefa!");
				return false;
			}else{
				showSucessMessage("Criar Tarefa!");
				form.clearValues();
				return true;
			}
		}	
		showErrorMessage("Dados inválidos!");
		return false;
	}
	
	//Fechar Tarefa GESTAR
	public boolean concluirTarefa() throws TarefaException_Exception, ServiceException{
		
		String idTarefa 	= form.getFieldValue("idTarefaConcluir");
		String sistema 		= form.getFieldValue("sistemaConcluir");
		
		if(StringUtils.isNotBlank(idTarefa) && StringUtils.isNotBlank(sistema)){	
			TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
			boolean success = tarefaSrv.closeTask(idTarefa, sistema);
			if(!success){
				showErrorMessage("Erro a concluir tarefa!");
				return false;
			}else{
				showSucessMessage("Tarefa concluída!");
				form.clearValues();
				return true;
			}
		}	
		showErrorMessage("Dados inválidos!");
		return false;
	}
	
	//Fechar Tarefa GESTAR
	public boolean eliminarTarefa() throws TarefaException_Exception, ServiceException{
		
		String idTarefa 	= form.getFieldValue("idTarefa");
		String sistema 		= form.getFieldValue("sistemaEliminar");
		
		if(StringUtils.isNotBlank(idTarefa) && StringUtils.isNotBlank(sistema)){	
			TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
			boolean success = tarefaSrv.eliminarTarefa(idTarefa, sistema);
			if(!success){
				showErrorMessage("Erro a eliminar tarefa!");
				return false;
			}else{
				showSucessMessage("Tarefa eliminada!");
				form.clearValues();
				return true;
			}
		}	
		showErrorMessage("Dados inválidos!");
		return false;
	}
	
	public boolean renomearTarefa() throws TarefaException_Exception{
		
		String idTarefa = form.getFieldValue("idTarefaRenomear");	
		String user 	= form.getFieldValue("user");	
		String sistema 	= form.getFieldValue("sistemaRenomear");
		
		if(StringUtils.isNotBlank(idTarefa)
				&& StringUtils.isNotBlank(user)){	
							
			TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
			boolean success = tarefaSrv.renomearTarefa(idTarefa, user, sistema);		
			
			if(!success){
				showErrorMessage("Erro a reatribuir tarefa!");
				return false;
			}else{
				showSucessMessage("Tarefa Renomeada!");
				form.clearValues();
				return true;
			}
		}	
		showErrorMessage("Dados inválidos!");
		return false;
	}
	
// GESTAR - END
	
// SSA - START
	//reprocessar RTR SSA
	public boolean reprocessarRTR(){
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		String iNumSeq = form.getFieldValue("retry");
		
		if(StringUtils.isNotBlank(iNumSeq)){				
			
			int isOK=srv.updateReprocessarRTR(new Long(iNumSeq));
			if(isOK<0){
				showErrorMessage("Erro a reprocesar!");
				return false;
			}else{
				showSucessMessage("Nº "+iNumSeq+" Reprocessado");
				form.clearValues();
				return true;
			}
		}	
		showErrorMessage("Número sequencial inválido!");
		return false;
	}
	
	//update Pedido XML
	public boolean updatePedidoXMLRTRD(){
		
		String numSeqRTRDAux = form.getFieldValue("numSeqRTRD");
		String oldTextRTRDAux = form.getFieldValue("oldTextRTRD");
		String newTextRTRDAux = form.getFieldValue("newTextRTRD");
		
		if(StringUtils.isNotBlank(numSeqRTRDAux)
				&& (StringUtils.isNotBlank(StringUtils.trim(oldTextRTRDAux)))
				&& (StringUtils.isNotBlank(StringUtils.trim(newTextRTRDAux)))
		){				
			RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
			ComunicacoesRTRD ped = srv.getPedidoRTRD(numSeqRTRDAux);
			
			if(ped!=null && ped.getXml()!=null){
				Object xml = null;
				
				try {	                	   
					
					xml = deserializeObjectoComprimidoGZIP(ped.getXml());
					
	     	   	} catch(Exception e) {
	     	   		log.error("Erro Sistema Declarativo: ",e);
	     	   	}
				
				if(xml==null)
					return false;
				
				String	alterada = (String)xml;
				
				if(StringUtils.isNotBlank(alterada)){
					alterada=alterada.replace("\n","xx##xx");
					//alterada=alterada.replace(" ","");
					alterada=alterada.replace(oldTextRTRDAux,newTextRTRDAux);
					alterada=alterada.replace("xx##xx","\n");
					if(StringUtils.isNotBlank(alterada)){
						
						//ped.setXml(alterada.getBytes());
						try {						   
							ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
							GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
							ObjectOutputStream oos = new ObjectOutputStream(gzipOut);
							// Objecto mensagem vai ser serializado e comprimido em formato GZIP.
							oos.writeObject(alterada);
							oos.flush();							   
							gzipOut.finish(); // <-- Imprescindível invocar o finish()!!!
							gzipOut.flush();
							ped.setXml(baos.toByteArray());				   
						} 
						catch (Exception e)
						{							   
							log.error("registarBlobMensagemGZIP(): Erro a inserir mensagem!",e);
						}
						
						Long row = srv.updatePedidoRTRD(ped);
						if(row!=null){
							showSucessMessage("RTRD0 Actualizado:"+row);	
						}
						else{
							showErrorMessage("NULL NAO ACTUALIZADO check log.....");
						}
						form.clearValues();
						return true;
					}
				}
			}
			else {
				showAlertMessage("RTRD "+numSeqRTRDAux+" NAO ENCONTRADA CHECK DADOS!");		
			}
		}	
		showAlertMessage("Faltam dados!");
		return false;
	}
	
	
// SSA - END		
	
// HIST DATICOB - START
	public boolean executeHist(){
		
		SGCProperties props = new SGCProperties();
		String chave = form.getFieldValue("verificadorChave");
		boolean success = false;
		TabelasApoioServiceT srv= EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		
		if(StringUtils.isNotBlank(chave))
		{			
			if(chave.equals(props.getMainProperty("rtr.access.key")))
			{
				
				srv.criarHistoricoRTR();
				
				showSucessMessage("Historico do RTR criado");
				success = true;
			}
			else
			{
				showErrorMessage("Chave Errada");  
				return false;
			}
		}
		else
		{
			showErrorMessage("Chave não Preenchida"); 
			return false;
		}
		if(success)
		{
			srv.deleteRTR();
			
			showSucessMessage("Historico do RTR criado");
			return true;
			
		}
		else return false;
		
	}
// HIST DATICOB - END
	
	
	private Object deserializeObjectoComprimidoGZIP(byte[] bytesGZIP) throws Exception	{
		ByteArrayInputStream baip = null;
		GZIPInputStream gzipIn = null;
		ObjectInputStream ois = null;
		try {
			baip = new ByteArrayInputStream(bytesGZIP);
			gzipIn = new GZIPInputStream(baip);
			ois = new ObjectInputStream(gzipIn);
			Object obj = ois.readObject();
			return obj;			
		} catch (Exception e) {
			log.error("deserializeObjectoComprimidoGZIP(): erro a ler objecto em formato GZIP!", e);
			throw e;
		}
		finally {
			try {
				if (ois != null) ois.close();
			} catch (IOException e)	{}
			
			try {
				if (gzipIn != null) gzipIn.close();
			} catch (IOException e)	{}
			
			try {
				if (baip != null) baip.close();
			} catch (IOException e) {}
		}
	}
	
	
	
	public void onRender() {

		super.onRender();
		if (this.getContext().isAjaxRequest() || StringUtils.isNotBlank(ajaxOp.getValue())) {

			setTemplate("/jsp/ajax_template.htm");
			setHeadInclude("");
			displayApplication = false;
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

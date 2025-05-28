
package com.siemens.ssa.communicator.web.jsp.control;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.xml.datatype.DatatypeConfigurationException;

import com.siemens.service.interfaces.*;
import com.siemens.ssa.communicator.pojo.interfaces.*;
import com.siemens.ssa.communicator.web.jsp.control.pojo.ControloSeloWeb;
import org.apache.click.control.HiddenField;
import org.apache.commons.lang.StringUtils;

import com.siemens.security.session.SessionManager;
import com.siemens.security.user.UserInfo;
import com.siemens.ssa.communicator.common.TaskWorkPage;
import com.siemens.ssa.communicator.retry.constants.RetryConstants;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.util.SSACommunicatorUtils;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.web.client.SGCWebClientUtil;
import com.siemens.ssa.communicator.web.jsp.control.TabGeralProcedimentos.InterviningData;
import com.siemens.ssa.communicator.web.jsp.control.caus.TabAnaliseRisco;
import com.siemens.ssa.communicator.web.jsp.control.caus.TabDocumentosCAUS;
import com.siemens.ssa.communicator.web.jsp.control.caus.TabGeralCabecalhoCAUS;
import com.siemens.ssa.communicator.web.utils.ControlResultTransform;

import net.atos.at.gestao.tarefas.entidades.Tarefa;
import net.atos.at.gestao.tarefas.util.Constantes;
import net.atos.at.gestao.tarefas.webservice.TarefaException_Exception;
import pt.atos.sgccomunicator.utils.NumeroAceitacao;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.sgccomunicator.utils.constants.RequestConstants;
import pt.atos.util.dao.ChaveDescricao;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.AppRuntimeException;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.util.string.ToStringUtils;
import pt.atos.util.web.WebControlUtils;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.W3HiddenField;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.form.W3Form;

public class ControlResult extends TaskWorkPage implements Serializable {

	private static final long serialVersionUID = -1892232594339296283L;
	private static Log log = Log.getLogger(ControlResult.class);

	public W3HiddenField task = new W3HiddenField(RequestConstants.IDTASK, String.class);

	public TabGroup grupoTabsCabecalho = new TabGroup("tabGroupPop2");

	public W3Submit submeterControl = new W3Submit("control_doc_bt1", null, this, "submeterControlo");

	public W3Submit btPrintPDF = new W3Submit("control_btPrintPDF", null, this, "printPDF");
	public W3HiddenField validado = new W3HiddenField("tipoControloValido", false);
	public String URLInqueritoConfirm = null;
	public String URLInquerito = null;

	public W3Submit btVoltar = new W3Submit("bt_voltar", null, this, "voltar");
	public W3Submit btConsultDau = new W3Submit("bt_consultarDau", null, this, "consultarDau");

	public boolean tipoSistemaCaus = false;
	public boolean openConfirmSubmission = false;
	public boolean openProximaAccao = false;
	public boolean existGeralFisico = false;
	// Hidden para obter valor da popupAccao
	public W3HiddenField idProximaAccao = new W3HiddenField("idProximaAccao", String.class);
	public W3HiddenField valConfirm = new W3HiddenField("valConfirm", String.class);

	/**
	 * Separadores da pagina. Cabecalho - corresponde ao controlo da declaracao.
	 */
	TabGeralCabecalhoGeneric contDAU = null;
	TabGeralDocumentalGeneric contDoc = null;
	TabGeralIrregularidades geralIrreg = null;
	TabGeralProcedimentos geralProcedimentos = null;
	TabGeralFisico geralFisico = null;
	TabGeralConseqFinanc conseqFinanceira = null;
	TabGeralMercadoriaIrr mercIrregularidade = null;

	TabControlFisico ContPhy = null; // ITEM_RESULT
	TabItemIrregularidades itemIrreg = null;
	TabItemMercadoriaIrr itemIrregMerc = null;
	TabItemConseqFinanc itemConseqFinanc = null;
	
	TabSelos tabSelos = null;

	boolean carregaItems = true;
	/**
	 * Separadores da adicao.
	 */
	// TabItemIrregularidades itemIrreg = null;
	TabItemConseqFinanc conseqFinanceiraItem = null;
	TabItemMercadoriaIrr mercIrregularidadeItem = null;

	SGCProperties props = new SGCProperties();

	public boolean ocultaAudPrev = false;
	boolean doPartialGetForm = false;

	@Override
	protected void continueBuildPage() {
		URLInqueritoConfirm = getContextPath() + getContext().getPagePath(InqueritoConfirmSubmission.class)
				+ "?idControlo=" + idControlo;
		URLInquerito = getContextPath() + getContext().getPagePath(InqueritoProximaAccao.class) + "?idControlo="
				+ idControlo + "&autSaida=" + isAutSaida;
		HttpSession session = getSession();
		/**
		 * Obter titulo
		 */

		if (StringUtils.isNotBlank(titlePage)) {
			breadCrumbPath = getMessage("page.breadcrumb", titlePage);
		}
		

		Controlo ctrl = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
		tipoSistemaCaus = SGCUtils.validaSistemaCAU(ctrl.getSistema());
		HiddenField idControloHiden = new HiddenField("idControlo", ctrl.getChave().getNumeroControlo());
		getForm().add(idControloHiden);
		
		if(tipoSistemaCaus) {
			contDAU = new TabGeralCabecalhoCAUS(this, readOnly, idControlo, declarationProcessor, ctrl.getMomento());
			contDoc = new TabDocumentosCAUS(this, readOnly, idControlo, declarationProcessor, ctrl);		
		} else {
			contDAU = new TabGeralCabecalho(this, false, idControlo, declarationProcessor, ctrl.getMomento());			
			contDoc = new TabGeralDocumental(this, readOnly, idControlo, declarationProcessor);
		}

		ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session
				.getAttribute(SessionConstants.CONTROLO_MATRIZ);

		if (matriz != null) {
			for (int i = 0; i < matriz.size(); i++) {
				log.info("CodSeparador: " + matriz.get(i).getCodSeparador().toString());
				if (matriz.get(i).getCodSeparador().equals("CTR_RESULT")) {
					grupoTabsCabecalho.setTab(contDAU.ControlDAU);
					contDAU.ControlDAU.setLegend(matriz.get(i).getSeparador());
				} else if (matriz.get(i).getCodSeparador().equals("CTR_DOC")) {
					grupoTabsCabecalho.setTab(contDoc.PhyControlDoc);
					contDoc.PhyControlDoc.setLegend(matriz.get(i).getSeparador());
				} else if (matriz.get(i).getCodSeparador().equals("CTR_IRR")) {
					geralIrreg = new TabGeralIrregularidades(this, readOnly, declarationProcessor);
					grupoTabsCabecalho.setTab(geralIrreg.phyControlIrrhy);
					geralIrreg.phyControlIrrhy.setLegend(matriz.get(i).getSeparador());
				} else if (matriz.get(i).getCodSeparador().equals("CTR_OUTRO")) {
					if(SGCUtils.validaSistemaCAU(ctrl.getSistema())) {
						grupoTabsCabecalho.setTab(new TabAnaliseRisco(ctrl, declarationProcessor).getTab());
					}else {						
						geralProcedimentos = new TabGeralProcedimentos(this, declarationProcessor, readOnly, ctrl);
						grupoTabsCabecalho.setTab(geralProcedimentos.geralControlProc);
						geralProcedimentos.geralControlProc.setLegend(matriz.get(i).getSeparador());
					}
				} else if (matriz.get(i).getCodSeparador().equals("CTR_SELO")) {
					tabSelos = new TabSelos(this, declarationProcessor, readOnly, ctrl, this.getContextPath(), this, session);
					grupoTabsCabecalho.setTab(tabSelos.tabSelos);
					tabSelos.tabSelos.setLegend(matriz.get(i).getSeparador());
				} else if (matriz.get(i).getCodSeparador().equals("CTR_FIS")) {
					// Se o tipo de controlo for documental não cria a tab de controlo fisico
					if (!SGCConstantes.getSiglaTipoControlo(ctrl.getTipoControlo().toString())
							.equals(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL)) {
						geralFisico = new TabGeralFisico(this, readOnly, ctrl.getMomento(), declarationProcessor);
						grupoTabsCabecalho.setTab(geralFisico.geralControlFisic);
						geralFisico.geralControlFisic.setLegend(matriz.get(i).getSeparador());
					}
				} else if (matriz.get(i).getCodSeparador().equals("CTR_IRR_MERC")) {
					mercIrregularidade = new TabGeralMercadoriaIrr(this, readOnly);
					grupoTabsCabecalho.setTab(mercIrregularidade.geralrregMercador);
					mercIrregularidade.geralrregMercador.setLegend(matriz.get(i).getSeparador());
				} else if (matriz.get(i).getCodSeparador().equals("CTR_CONSEQ_FIN")) {
					conseqFinanceira = new TabGeralConseqFinanc(this, readOnly);
					grupoTabsCabecalho.setTab(conseqFinanceira.geralConseqFinanc);
					conseqFinanceira.geralConseqFinanc.setLegend(matriz.get(i).getSeparador());
				}
			}
		}

		((W3Form) form).setTabbed(true);
		form.setErrorsPosition(W3Form.POSITION_TOP);
		form.setValidate(true);
		form.setParent(this);

		form.add(task);

		grupoTabsCabecalho.setForm(form);
		grupoTabsCabecalho.setExistsTabbedForm(true);
		form.setValidate(false);

		form.add(grupoTabsCabecalho);
		form.add(validado);
		form.add(idProximaAccao);
		form.add(valConfirm);
		HeaderParameters = " ><base target=\"_self\" >  ";

		/**
		 * 
		 * BOTOES
		 * 
		 */

		if (!isReadOnly()) {
			btConsultDau.setOnClick("setTimeout(function(){ window.open('" 
				    + linkConsultarDau 
				    + "&" + SGCWebClientUtil.CONSULTA_ID_CONTROLO + "=" + idControlo 
				    + "', '_blank'); }, 1); return false;");
			addButtonToPage(btConsultDau);
			
			// TODO: Descomentar apenas quando existir irregularidades.
			// submeterControl.setOnClick("selectBox =
			// document.getElementById('form_control_irr_table2');" +
			// "for (var i = 0; i < selectBox.options.length; i++) {
			// selectBox.options[i].selected = true; } ");
			addButtonToPage(submeterControl);

			/**
			 * DE MOMENTO DESACTIVADO....
			 */
			btPrintPDF.setDisabled(true);
			btPrintPDF.setOnClick("ignorar.push(this.id)");
			if (false) { // TODO: por a aparecer por sistema na matriz
				addButtonToPage(btPrintPDF);
			}
		}
		addButtonToPage(btVoltar);

		/**
		 * Se existe um returnValue com devolucao da proxima accao, submete a pagina
		 */
		if (StringUtils.isNotBlank(getContext().getRequestParameter("idProximaAccao"))) {
			Controlo ctrlDecs = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
			ControloServiceT srvControlo = EJBUtil.getSessionInterface(ControloServiceT.class);
			btConsultDau.setDisabled(true);
			ctrlDecs.setAlterar(getContext().getRequestParameter("idProximaAccao"));
			/**
			 * passa accao seleccionada para o circuito...
			 */
			try {

				/**
				 * ctrlDec = Controlo Introduz o dados na base de dados
				 **/

				finalizaControl(false, ctrlDecs);
				idProximaAccao.setValue("");

				srvControlo.finalizaControlo(ctrlDecs);
				
				NumeroAceitacao numAceitacao = NumeroAceitacao.create(ctrlDecs.getNumIdentificacao(), ctrlDecs.getSistema());
				String aceitacao = numAceitacao.toFormatoSemVersao();
				String aceitCompleto = numAceitacao.toFormatoCompleto();
				
				RetryServiceT srvRetry = EJBUtil.getSessionInterface(RetryServiceT.class);
				
				//Notificacao do Resultado Controlo ao SD
				srvRetry.registaPedido(SGCConstantes.TIPO_COMUNICACAO_RETRY_RESPONDER_RESULTADO_CONTROLO,
										SGCConstantes.TIPO_COMUNICACAO_RETRY_SISTEMA_GESTAO_CONTROLO, 
										ctrlDecs.getSistema(), 
										aceitacao, 
										true, null, 
										aceitCompleto, 
										null, 
										null, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);

			} catch (ApplicationException e) {
				log.error("Erro ao finalizar coltrolo: " + idControlo, e);
				throw new AppRuntimeException(e);
			}
		}
	}
	
	@Override
	protected void postInit() {
		if (contDAU != null) {
			contDAU.gerirResultadosControlo(this, idControlo);
			setFormData();
		}
		if (geralFisico != null) {
			gereControloFisicoGeral(idControlo, readOnly);
		}
	}

	public void onRender() {
		super.onRender();
		if (this.getContext().isAjaxRequest()) {
			setTemplate("/jsp/ajax_template.htm");
			setHeadInclude("");
			dealAjaxRequest();
		}
		if (contDAU != null) {
			contDAU.onRender(this, readOnly, idControlo);
			idProximaAccao.setValue("");
		}
		if (geralProcedimentos != null)
			geralProcedimentos.onRender(this, readOnly);
	}

	private void dealAjaxRequest() {
		// contDoc.saveFormulario(form);
		/**
		 * nome do painel(div) a actualizar
		 */
		String ajaxOp = getContext().getRequestParameter("ajaxOp");
		/**
		 * identificacao da linha selecionada na tabela
		 */
		String ajaxOpId = getContext().getRequestParameter("ajaxOpId");

		// getFormData();
		if (ajaxOp != null) {

			HttpSession session = getSession();
			Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
			// carregaBarraAmarela(ctrlDec);
			if (ajaxOp.equals("tratar_adicao")) {
				
				grupoTabsCabecalho.setTabVisible("DadosCabecalho");

				ControloItem ctrlAdicao = null;
				String idAdicao = "";
				if (StringUtils.isNotBlank(ajaxOpId))
					idAdicao = (ajaxOpId);
				else {
					this.setStringToAjaxErrors("Seleccione uma adiï¿½ï¿½o!");
					return;
				}
				if (ctrlDec != null && ctrlDec.getListaControloItem().size() != 0) {
					ctrlAdicao = (ControloItem) contDAU.tabelaAdicoes.getTableItemFromTable("" + idAdicao);
					contDAU.tabelaAdicoes.setSelectedIDline("" + idAdicao);
				}

				if (ctrlAdicao == null) {
					this.setStringToAjaxErrors("Adicao nao e valida!");
					return;
				} else {
					if (ctrlDec.getTipoControlo() != null
							&& (SGCConstantes.TIPO_CONTROLO_CAP_COMBO.equals(ctrlDec.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlDec.getTipoControlo().toString()))) {
						ctrlAdicao.setTipoControlo(ctrlDec.getTipoControlo());
					}
					contDAU.showAdicao(this, ctrlAdicao, readOnly, idControlo);
					submeterControl.setDisabled(true);
					btConsultDau.setDisabled(false);
				}
			} else if (ajaxOp.equals("registarControloAdd")) {
				contDAU.registarControlAdicao(this, ajaxOpId, idControlo, submeterControl);
			} else if (ajaxOp.equals("consultarProcedimento")) {
				InterviningData intervining = (InterviningData) geralProcedimentos.intervsTable
						.getTableItemFromTable(ajaxOpId);
				geralProcedimentos.intervsTable.setSelectedIDline(ajaxOpId);
				geralProcedimentos.showDetails(intervining);
				grupoTabsCabecalho.setTabVisible(geralProcedimentos.geralControlProc.getName());
			} else if (ajaxOp.equals("documentos")) {
				String subOperacao = getContext().getRequestParameter("subOp");
				if(subOperacao.contentEquals("addInfo")) {					
					((TabDocumentosCAUS)contDoc).addInformacao(ajaxOpId, false);
				} else if(subOperacao.contentEquals("alterarInfo")) {					
					((TabDocumentosCAUS)contDoc).addInformacao(ajaxOpId, true);
				} else if(subOperacao.equals("carregaCodigos")) {	
					List<ControloDocumentoAdicional> lista = (List<ControloDocumentoAdicional>) getSession().getAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctrl.getChave().getNumeroControlo());
					((TabDocumentosCAUS)contDoc).tabelaDocumentosAdicionais.setRowList(lista);
				}
				grupoTabsCabecalho.setTabVisible("PhyControlDoc");
			} else if (ajaxOp.equals("tratar_edicao_documento_adicional")) {
				((TabDocumentosCAUS)contDoc).tabelaDocumentosAdicionais.setRowList((List<ControloDocumentoAdicional>) session.getAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctrl.getChave().getNumeroControlo()));
				String idLinha = getContext().getRequestParameter("idLinha");
				((TabDocumentosCAUS)contDoc).editarLinhaDocumentoAdicional(idLinha);
				grupoTabsCabecalho.setTabVisible("PhyControlDoc");
				
			} else if (ajaxOp.equals("tratar_edicao_selos")) {		
				tabSelos.editarLinhaIdentificacaoSelos(ajaxOpId);
				grupoTabsCabecalho.setTabVisible("TabSelos");		
			} else if (ajaxOp.equals("cancelarControloAdd")){
				//eliminar todos os registros de controlo item tipo em memória nao confirmados
				removerControloItemTipoManual();
				submeterControl.setDisabled(false);
			} else {
				//eliminar todos os registros de controlo item tipo em memória nao confirmados
				submeterControl.setDisabled(false);
			}
			// session.setAttribute(SessionConstants.RES_CONTROLO+idControlo, ctrlDec);
			// setFormData();
		}
	}
	
	public void removerControloItemTipoManual(){
		Map<String, List<ControloItemTipo>> mapControloItemTipoManual = (Map<String, List<ControloItemTipo>>) getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
		if (mapControloItemTipoManual != null) {
		    for (List<ControloItemTipo> lista : mapControloItemTipoManual.values()) {
		        if (lista != null) {
		        	lista.removeIf(i -> i.getFlagConfirmado() != null && i.getFlagConfirmado().equals(SGCConstantes.FLAG_BD_FALSO));
		        }
		    }

		}
		//confirma todos os equipamentos da sessão
		Map<String, List<ItemEquipamento>> mapEquipamento = (Map<String, List<ItemEquipamento>>) getSession().getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo);
		if (mapEquipamento != null) {
		    for (List<ItemEquipamento> lista : mapEquipamento.values()) {
		        if (lista != null) {
		        	lista.removeIf(i -> i.getFlagConfirmado() != null && i.getFlagConfirmado().equals(SGCConstantes.FLAG_BD_FALSO));
		        }
		    }
		}
		
		//confirma todos os equipamentos da sessão
		Map<String, List<ItemEquipamento>> mapEquipamentoManual = (Map<String, List<ItemEquipamento>>) getSession().getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo);
		if (mapEquipamentoManual != null) {
		    for (List<ItemEquipamento> lista : mapEquipamentoManual.values()) {
		        if (lista != null) {
		        	lista.removeIf(i -> i.getFlagConfirmado() != null && i.getFlagConfirmado().equals(SGCConstantes.FLAG_BD_FALSO));
		        }
		    }
		}
	}

	/**
	 * 
	 * Metodo do botao "Voltar"
	 * 
	 **/
	public boolean voltar() {
		removerSession();
		setRedirect(linkVoltar);
		return false;
	}

	/**
	 * 
	 * Introduz os dados do ecra para a sessao
	 * 
	 **/
	@Override
	protected void getFormData() {
		HttpSession session = getSession();
		Controlo ctrl = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
		ControloOutroService srvOutro = EJBUtil.getSessionInterface(ControloOutroService.class);
		if (ctrl != null) {

			// Atualiza a Declaracao
			contDAU.getFormulario(ctrl, form);

			ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session
					.getAttribute(SessionConstants.CONTROLO_MATRIZ);
			if (matriz != null) {

				for (int i = 0; i < matriz.size(); i++) {
					if (matriz.get(i).getCodSeparador().equals("CTR_DOC")) {
						// Tab dos Documentos
						if (!doPartialGetForm) {
							contDoc.getFormulario(ctrl, form);
						}
					} else if (matriz.get(i).getCodSeparador().equals("CTR_IRR")) {
						// Tab Geral Irregularidade
						geralIrreg.getFormulario(ctrl, form);
					}
					// Devido à interacao com o SSIAF, caso existas irregularidades, e obrigatorio
					// obter a informacao sobre a Situação dos Intervenientes
					// Para a criação da ficha do SSIAF - no ambito de DLCC2 , sem irregularidades, é uma tab de consulta
					//, logo não vai buscar informação à página inserida pelo utilizador 
					else if (matriz.get(i).getCodSeparador().equals("CTR_OUTRO") && ctrl.getControloIrregularidade()!=null){
							geralProcedimentos.getFormulario(ctrl, form);	
						} 

					// TODO: verificar se é necessário para a exportação/importação
					/*
					 * else if (matriz.get(i).getCodSeparador().equals("CTR_FIS") &&
					 * !existGeralFisico){ //Tab Geral Fisico if(existGeralFisico=false){
					 * geralFisico.getFormulario(ctrl, form); } }
					 */
					else if (matriz.get(i).getCodSeparador().equals("CTR_FIS")) {
						// Tab Geral Fisico
						// Se o tipo de controlo for documental não cria a tab de controlo fisico
						if (!SGCConstantes.getSiglaTipoControlo(ctrl.getTipoControlo().toString())
								.equals(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL)) {
							geralFisico.getFormulario(ctrl, form);
						}
					} else if (matriz.get(i).getCodSeparador().equals("CTR_CONSEQ_FIN")) {
						// Tab Consequencias Financeiras
						conseqFinanceira.getFormulario(ctrl, form);
					} else if (matriz.get(i).getCodSeparador().equals("CTR_IRR_MERC")) {
						// Tab Irregularidade Mercadoria
						mercIrregularidade.getFormulario(ctrl, form);
					}
					//DESCOMENTAR SE FOR PARA ENVIAR OS SELOS
//					else if (matriz.get(i).getCodSeparador().equals("CTR_SELO")) {
//						if (tabSelos != null) {
//							// Atualiza a lista de selos do objeto Controlo com os valores da tab de selos
//							ctrl = tabSelos.getFormulario(ctrl, form);
//						}
//					}
				}
			}

			/**
			 * Insere os dados na sessao
			 **/
			session.setAttribute(SessionConstants.RES_CONTROLO + idControlo, ctrl);
			SessionManager.getInstance().setSessao(session);
		}
	}

	@Override
	public boolean onCancelar() {
		return false;
	}

	@Override
	public boolean onGravar() {
		return false;
	}

	/**
	 * 
	 * Introduz os dados do controlo "base" da base de dados para ecra
	 * 
	 **/
	public void setFormData() {

		/**
		 * 
		 * Fetch dos dados em sessao
		 * 
		 **/
		HttpSession session = getSession();
		Controlo controlo = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);

		if (controlo != null) {
			// Tab Dados Controlo Cabecalho da declaracao
			contDAU.setFormulario(controlo, form);

			ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session
					.getAttribute(SessionConstants.CONTROLO_MATRIZ);
			if (matriz != null) {
				for (int i = 0; i < matriz.size(); i++) {
					if (matriz.get(i).getCodSeparador().equals("CTR_DOC")) {
						// Tab dos Documentos
						contDoc.setFormulario(controlo, form);
					} else if (matriz.get(i).getCodSeparador().equals("CTR_IRR")) {
						// Tab Geral Irregularidade
						geralIrreg.setFormulario(controlo, form);
					} else if (matriz.get(i).getCodSeparador().equals("CTR_OUTRO")) {
						// Tab Geral Procedimentos
						if(!SGCUtils.validaSistemaCAU(ctrl.getSistema())) {							
							geralProcedimentos.setFormulario(controlo, form);
						}
					} else if (matriz.get(i).getCodSeparador().equals("CTR_FIS")) {
						// Tab Geral Fisico
						// Se o tipo de controlo for documental não cria a tab de controlo fisico
						if (!SGCConstantes.getSiglaTipoControlo(ctrl.getTipoControlo().toString())
								.equals(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL)) {
							geralFisico.setFormulario(controlo, form);
						}
					}
				}
			}
		}
	}

	public boolean printPDF() {
		log.info("ControlResult#printPDFTo:" + idControlo);
		HttpSession session = getSession();
		Controlo ctrlDau = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);

		if (ctrlDau != null) {

			// Vai actualizar a Declaracao
			contDAU.getFormulario(ctrlDau, form);

			List<Object> dados = new ArrayList<Object>();

			// apagar valor dos valores conferidos
			if (ctrlDau.getListaControloItem() != null && ctrlDau.getListaControloItem().size() > 0) {

				for (int i = 0; i < ctrlDau.getListaControloItem().size(); i++) {
					ControloItem contAdd = ctrlDau.getListaControloItem().get(i);

					if (contAdd != null && contAdd.getListaControloDocumentoItem() != null
							&& contAdd.getListaControloDocumentoItem().size() > 0) {

						for (int z = 0; z < contAdd.getListaControloDocumentoItem().size(); z++) {

							ControloDocumento docAdicao = contAdd.getListaControloDocumentoItem().get(z);

							if (docAdicao != null && StringUtils.isNotBlank(docAdicao.getConferidorDocumento())) {
								docAdicao.setDescricaoConferido("");
							}
						}
					}
				}
			}

			// Controlo Declaracao
			dados.add(ctrlDau);

			// GERA o PDF
//			byte[] expediente = (new PDFCreator(new ResultadoControloPDF())).generatePDF(dados);
//
//			// MOSTRA O PDF
//			if (expediente != null && expediente.length > 0) {
//
//				HttpServletResponse response = getContext().getResponse();
//				response.setHeader("Content-Disposition","attachment; filename=\"STIMP"+System.currentTimeMillis()+".pdf\"");
//				//response.setHeader("Content-Disposition", "attachment; filename=\"Resultado_Controlo_"+ decActual.getChave().toString() + ".pdf\"");
//				response.setContentType(ClickUtils.getMimeType(".pdf"));
//				response.setHeader("Pragma", "no-cache");
//				OutputStream writer = null;
//				try {
//					writer = response.getOutputStream();
//					writer.write(expediente, 0, expediente.length);
//
//					//writer.flush();
//					setPath(null);
//				} catch (IOException ioe) {
//					ioe.printStackTrace();
//					ClickUtils.close(writer);
//				}
//
//				ClickUtils.close(writer);
//			}
			showAlertMessage(getMessage("control_result.print.preview"));
		}
		return true;
	}

	private boolean validaAddControlo(HttpSession session, Controlo ctrlDec) {
		log.info("ControlResult: validaAddControlo");
		UserInfo user = (UserInfo) session.getAttribute(SessionConstants.USER);
		if (ctrlDec != null && ctrlDec.getListaControloItem() != null && ctrlDec.getListaControloItem().size() > 0) {
			for (int i = 0; i < ctrlDec.getListaControloItem().size(); i++) {
				// Nao valida a primeira linha de controlo. Esta primeira linha e ficticia afim
				// de ajudar na construcao da pagina do control result,logo nao pertence ao
				// processo
				if (i != 0) {
					ControloItem controloItem = ctrlDec.getListaControloItem().get(i);

					if (controloItem != null && controloItem.getTipoControlo() != null) {
						if (!SGCConstantes.TIPO_CONTROLO_SEM_CONTROLO_COMBO.equals(controloItem.getTipoControlo().toString())
								&& !SGCConstantes.TIPO_CONTROLO_SEM_CONTROLO_CAU_COMBO.equals(controloItem.getTipoControlo().toString())
								&& !SGCConstantes.TIPO_CONTROLO_NAO_SUBMITIDO_CONTROLO.equals(controloItem.getTipoControlo().toString())) {
							if (controloItem.getResultadoControlo() == null) {
								showErrorMessage(getMessage("control_result.error.controlo.adicoes"));
								return false;
							}
						}

						if (SGCConstantes.TIPO_CONTROLO_FISICO_IMPEC_COMBO
								.equals(controloItem.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_IMPEC_COMBO
										.equals(controloItem.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_FISICO_SIMTEMM_COMBO
										.equals(controloItem.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO_SIMTEMM_COMBO
										.equals(controloItem.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO
								.equals(controloItem.getTipoControlo().toString())) {
							if (ctrlDec.getConferente() != null && ctrlDec.getVerificador() != null
									&& (!SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO
											.equals(controloItem.getResultadoControlo().toString())
										&& !SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO
										.equals(controloItem.getResultadoControlo().toString()))) {

								if (ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador())
										|| user.getUserId().equalsIgnoreCase(ctrlDec.getVerificador())) {
									if (controloItem.getInicioControloFisico() == null
											|| controloItem.getFimControloFisico() == null
											|| StringUtils.isBlank(controloItem.getTipoVerificacao())
											|| StringUtils.isBlank(controloItem.getVerificacao())
											|| StringUtils.isBlank(controloItem.getTipoPesagem())) {
										showErrorMessage(getMessage("control_result.error.controlo.adicoes.fisico"));
										return false;
									}

									if (controloItem.getInicioControloFisico() != null
											&& controloItem.getFimControloFisico() != null
											&& !controloItem.getFimControloFisico()
													.after(controloItem.getInicioControloFisico())) {
										showErrorMessage(getMessage(
												"control_result.error.controlo.adicoes.fisico.dataFimDepois"));
										return false;
									}
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	private boolean validaDocumentosPreenchidos(Controlo ctrlDec) {
		log.info("ControlResult#validaDocumentosPreenchidos");
		boolean isValid = true;

		if (!SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO
				.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())) {
			// Obtem a tabela de documentos e verifica se existem documentos preenchidos
			if (ctrlDec.getListaControloItem() != null && ctrlDec.getListaControloItem().size() > 0) {

				for (int i = 0; i < ctrlDec.getListaControloItem().size(); i++) {
					ControloItem adic = ctrlDec.getListaControloItem().get(i);

					if (adic != null && adic.getListaControloDocumentoItem() != null
							&& adic.getListaControloDocumentoItem().size() > 0) {

						for (int x = 0; x < adic.getListaControloDocumentoItem().size(); x++) {
							ControloDocumento docs = adic.getListaControloDocumentoItem().get(x);
							if (docs != null && StringUtils.isNotBlank(docs.getConferidorDocumento())
									&& SGCConstantes.FLAG_BD_VERDADEIRO.equals(docs.getConferidorDocumento())) {
								return true;
							} else {
								isValid = false;
							}
						}
					}
				}
			}
		}
		return isValid;
	}

	private boolean validaMeiosAferidosPreenchidos(Controlo ctrlDec) {
		log.info("ControlResult#validaMeiosAferidosPreenchidos");
		boolean meiosPreenchidos = true;
		boolean dataPreenchida = true;
		boolean dataCorreta = true;

		if (SGCConstantes.SISTEMA_DLCC2.equals(ctrlDec.getSistema())) {
			MeioAutAferido meioAferido = ctrlDec.getMeioAutAferido();

			if (meioAferido != null) {
				ArrayList<MeioAutAferidoDet> detalhes = meioAferido.getControloMeioDetalhes();

				if (detalhes != null) {
					int countMeiosSelecionados = 0;
					for (MeioAutAferidoDet detalhe : detalhes) {
						if (SGCConstantes.FLAG_BD_VERDADEIRO.equals(detalhe.getFlagConferido())) {
							countMeiosSelecionados++;

							if ("Outros".equalsIgnoreCase(detalhe.getNValor())) {
								if (StringUtils.isEmpty(meioAferido.getOutroMotivo())) {
									meiosPreenchidos = false;
								}
							}
						}
					}

					if (countMeiosSelecionados == 0)
						meiosPreenchidos = false;
				}
				if (!meiosPreenchidos)
					showErrorMessage(getMessage("control_result.error.documentos.meiosporpreencher"));

				if (meioAferido.getDataInicioCarregamento() == null || meioAferido.getDataFimCarregamento() == null) {
					dataPreenchida = false;
					showErrorMessage(getMessage("control_result.error.fisico.porpreencher"));
				} else {
					if (meioAferido.getDataInicioCarregamento().after(meioAferido.getDataFimCarregamento())) {
						dataCorreta = false;
						showErrorMessage(getMessage("control_result.error.fisico.dataincorreta"));
					}

					Date currentDate = new Date();

					if (meioAferido.getDataInicioCarregamento().after(currentDate)) {
						dataCorreta = false;
						showErrorMessage(getMessage("control_result.error.fisico.datainicioinvalida"));
					}

					if (meioAferido.getDataFimCarregamento().after(currentDate)) {
						dataCorreta = false;
						showErrorMessage(getMessage("control_result.error.fisico.datafiminvalida"));
					}
				}
			}
		}

		return !(meiosPreenchidos && dataPreenchida && dataCorreta);
	}

	private boolean validaIrregularidadesPreenchidas(Controlo ctrlDec) {
		if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
			if (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO
					.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())) {
				ControloIrregularidade controloIrregularidade = ctrlDec.getControloIrregularidade();

				if (controloIrregularidade != null) {
					if (controloIrregularidade.getNormaViolada() == null
							|| controloIrregularidade.getCodigosIrreg() == null
							|| controloIrregularidade.getIndicadorRisco() == null
							|| StringUtils.isEmpty(controloIrregularidade.getPraticaUtilizada())
							|| StringUtils.isEmpty(controloIrregularidade.getLocalPratica())) {
						showErrorMessage("Deve preencher todos os campos do Controlo Irregularidades");
						return true;
					}
				}
			}
		}

		return false;
	}

	private boolean validaTipoControlo(HttpSession session, String idControlo) {
		log.info("ControlResult#validaTipoControlo");
		// vamos verificar se o Resultado de Controlo da Declaracao encontra-se
		// em conformidade com o Resultado de Controlo das Adicoes.
		boolean tipoControloValido = true;
		// valor da Combo do Resultado do Controlo nos Dados dos Cabecalhos
		String comboDadosCabecalho = null;
		if (StringUtils.isNotBlank(form.getFieldValue("control_dau_comboResultadoControlo"))) {
			String[] idTpDec = WebControlUtils
					.separarIdFields(form.getFieldValue("control_dau_comboResultadoControlo"));
			comboDadosCabecalho = idTpDec[0];
		}

		if (comboDadosCabecalho != null) {
			log.info("ControlResult: validaTipoControlo-obtemPrioridadesResControloSSA ");
			// valor do Resultado do Controlo para as Adicoes
			Controlo controlo = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
			String prioridadeCabecalho = SSACommunicatorUtils.obtemPrioridadesResControloSSA(comboDadosCabecalho, controlo.getSistema());
	        String prioridadeMaisAlta = null;
 
			if (controlo.getListaControloItem() != null && controlo.getListaControloItem().size() > 0) {

				for (int i = 0; i < controlo.getListaControloItem().size(); i++) {

					ControloItem adicao = controlo.getListaControloItem().get(i);
					log.info("ControlResult: validaTipoControlo-obtemPrioridadesResControloSSA22222 ");
					String resControloAdicao = adicao.getResultadoControlo();
					String prioridadeAdicao = SSACommunicatorUtils.obtemPrioridadesResControloSSA(resControloAdicao, controlo.getSistema());

					if(SGCUtils.validaSistemaCAU(controlo.getSistema())) {
					 // Verifica se a prioridadeAdicao é maior que a prioridadeMaisAlta
	                if (prioridadeAdicao != null && (prioridadeMaisAlta == null || prioridadeAdicao.compareTo(prioridadeMaisAlta) > 0)) {
	                    prioridadeMaisAlta = prioridadeAdicao;
	                    tipoControloValido = prioridadeAdicao.compareTo(prioridadeCabecalho) == 0; // Devolve false ou true
	                }
				} else {
					if (prioridadeAdicao != null && prioridadeAdicao.compareTo(prioridadeCabecalho) > 0) {
						tipoControloValido = false;
					}
				}
					}
			}
		} else {
			tipoControloValido = false;
		}
		return tipoControloValido;
	}

	/**
	 * 
	 * Metodo e chamado imediatamente apos o click no botao para insercao dos dados
	 * para a base de dados.
	 * 
	 **/
	public boolean submeterControlo() throws ApplicationException, TarefaException_Exception {
		boolean erro = false;
		boolean relatorioControlo = false;
		boolean requerDadosAdd = false;

		doPartialGetForm = false;
		String valConfirm = getContext().getRequestParameter("valConfirm");
		if (StringUtils.isNotBlank(valConfirm) && valConfirm.equals("true")) {
			doPartialGetForm = true;
		}

		/**
		 * Insere todos os dados em sessao
		 **/
		getFormData();
		/**
		 * Vai buscar todos o dados inseridos no formulario preenchido no ecra da
		 * sessao.
		 **/
		HttpSession session = getSession();
		Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);

//TODO: Colocar o submeter no botao da pop up, quando essa pop up e chamada 

//		ArrayList<ChaveDescricao> tipoControloRes = SGCUtils.traduzTipoControlo(ctrlDec.getTipoControlo(), null, ctrlDec.getSistema());
//		String tipoControlo = null;
//		if(tipoControloRes != null && ! tipoControloRes.isEmpty()) {
//			tipoControlo = tipoControloRes.get(0).getCodigo().trim();
//		}
//		
//		if(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL.equals(tipoControlo)) {
//			if(ctrlDec.getDataControloDoc() != null) {
//				showErrorMessage("Controlo documental: comunicação já entregue.");
//				return true;
//			}
//		}
//		
//		if(SGCConstantes.TIPO_CONTROLO_FISICO.equals(tipoControlo)) {
//			if(ctrlDec.getDataControloFisico() != null && 
//					ctrlDec.getConferente() != null && ctrlDec.getVerificador() != null && 
//					ctrlDec.getConferente().equals(ctrlDec.getVerificador())) {
//				showErrorMessage("Controlo fisico: comunicação já entregue.");
//				return true;
//			}
//		}
//		

		if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
			// altera o resultado do controlo das adicoes conforme o resuldato do controlo
			// na declaracao
			for (int i = 1; i < ctrlDec.getListaControloItem().size(); i++) {
				ControloItem item = ctrlDec.getListaControloItem().get(i);
				// TODO: DAIN - rever devido ao DAIN
				item.setResultadoControlo(ctrlDec.getResultadoControlo());
				if (!SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_COMBO.equals(ctrlDec.getResultadoControlo())) {
					item.setMotivoControlo("relatorio automatico nao conforme");
				} else {
					item.setMotivoControlo("");
				}
			}
		}

		if (StringUtils.isBlank(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())) {
			erro = true;
			showErrorMessage("É necessário preencher o Resultado de controlo.");
			return true;
		}
		
		/**
		 * Verificar se é CD ou CF e se saidaNaoAutorizada é true (V)
		 */
		if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
			// Verificar se é controlo documental
			if(ctrlDec.getSaidaNaoAutorizada() != null && ctrlDec.getSaidaNaoAutorizada().equals(SGCConstantes.FLAG_BD_VERDADEIRO) && 
					ctrlDec.getTipoControlo().equals(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL) &&
					(!SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue()))) {
				erro = true;
				showErrorMessage("O Resultado de controlo deve ser 3P.");
				return true;
			} else if(ctrlDec.getSaidaNaoAutorizada() != null && ctrlDec.getSaidaNaoAutorizada().equals(SGCConstantes.FLAG_BD_VERDADEIRO) && 
					ctrlDec.getTipoControlo().equals(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL) &&
					(SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue()))) {
				String resultadoCombo = contDAU.ContAdd.headerPanel.getField("control_add_comboControloReSelecao").getValue();
				if(!resultadoCombo.equals(SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO)) {
					erro = true;
					showErrorMessage("O campo Controlo Re-Seleção deve ser Controlo Fisico, quando o Resultado do Controlo é 3P-Proposta de re-seleção.");
					return true;
				}
				}
			
			else if (ctrlDec.getSaidaNaoAutorizada() != null && ctrlDec.getSaidaNaoAutorizada().equals(SGCConstantes.FLAG_BD_VERDADEIRO) && 
					ctrlDec.getTipoControlo().equals(SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO)) {
				// Verificar se Conferente == Verificador e diferente de B1
				if(StringUtils.isNotBlank(ctrlDec.getConferente())
						&& StringUtils.isNotBlank(ctrlDec.getVerificador())
						&& (ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador()))
						&& ctrlDec.getIdTarefaFisico() != null &&
								(!SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue()))) {
					TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
					boolean tarefaAberta = false;
					Tarefa tarefa = tarefaSrv.pesquisarTarefa(ctrlDec.getIdTarefaFisico(), ctrlDec.getSistema());
					tarefaAberta = (tarefa.getEstado().equals(Constantes.Nomeada));
					if (tarefaAberta) {
						erro = true;
						showErrorMessage("O Resultado de controlo deve ser B1.");
						return true;
					}
				} 
				// Verificar se Conferente != Verificador
				else if (StringUtils.isNotBlank(ctrlDec.getConferente())
						&& StringUtils.isNotBlank(ctrlDec.getVerificador())
						&& !(ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador()))
						&& ctrlDec.getIdTarefaFisico() != null &&
								(!SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue()))) {
					TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
					boolean tarefaAberta = false;
					Tarefa tarefa = tarefaSrv.pesquisarTarefa(ctrlDec.getIdTarefaFisico(), ctrlDec.getSistema());
					tarefaAberta = (tarefa.getEstado().equals(Constantes.Nomeada));
					if (tarefaAberta) {
						erro = true;
						showErrorMessage("O Resultado de controlo deve ser B1.");
						return true;
					}
				}
				
			}
			}
		/**
		 * verificar se e ZZ
		 **/
		if (SGCConstantes.RESULTADO_CONTROLO_DADOS_ADICIONAIS_ZZ_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())
				|| SGCConstantes.RESULTADO_CONTROLO_DADOS_ADICIONAIS_ZZ_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())) {
			requerDadosAdd = true;
			if (StringUtils.isBlank(contDAU.headerPanel.getField("control_dau_txtDadosAdicionais").getValue())) {

				showErrorMessage("É necessário preencher o campo Requer dados adicionais.");
				return true;
			}
		} else {
			if (contDAU.headerPanel.getField("control_dau_txtDadosAdicionais") != null && StringUtils
					.isNotBlank(contDAU.headerPanel.getField("control_dau_txtDadosAdicionais").getValue())) {

				showErrorMessage(
						"Não é possível preencher o campo Requer dados adicionais para o Resultado do Controlo seleccionado.");
				return true;
			}
			
			String resultadoCombo = contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue();

			if (SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(resultadoCombo) ||
					SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(resultadoCombo)) {
				int countAdicoes3P_Preenchidas = verificaTipoControloAdicoes(ctrlDec.getListaControloItem(),
						"3P".equals(resultadoCombo) ? SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO : SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO, false,ctrlDec.getSistema());
					if (countAdicoes3P_Preenchidas == 0) {
					erro = true;
					showErrorMessage("É necessário preencher pelo menos uma Adição com Resultado de controlo 3P.");
					return true;
				} else if (contDAU.headerPanel.getField("control_dau_comboControloReSelecao") != null && StringUtils
						.isBlank(contDAU.headerPanel.getField("control_dau_comboControloReSelecao").getValue())) {
					erro = true;
					showErrorMessage(
							"É necessário preencher o campo Controlo Re-Seleção, quando o Resultado do Controlo é 3P-Proposta de re-seleção.");
					return true;
				}
			}

			if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_SFA)) {

				if ((SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())
						|| SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_CAU_COMBO.contentEquals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue()))
						|| (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(	contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue()))) {

					if (StringUtils
							.isBlank(contDAU.headerPanel.getField("control_dau_txtResultadoControlo").getValue())) {
						erro = true;
						showErrorMessage("É necessário preencher o campo Relatório do Resultado do Controlo.");
						return true;
					} else {
						relatorioControlo = true;
					}
				}

			} else {

				if (SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())
						|| SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())	 ) {

					
					String resultadoControlo = null;
					if (SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())) {
						resultadoControlo = SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_COMBO;
					} else if (SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_CAU_COMBO.equals(contDAU.headerPanel.getField("control_dau_comboResultadoControlo").getValue())) {
						resultadoControlo = SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_CAU_COMBO;
					}
					int totalAdicoes = ctrlDec.getListaControloItem().size() - 1; // nao conta adicao 0
					int countAdicoesA1Preenchidas = verificaTipoControloAdicoes(ctrlDec.getListaControloItem(),
							resultadoControlo, true, ctrlDec.getSistema());

					// Se 0, entao deve preencher campo relatorio controlo do cabecalho
					if (countAdicoesA1Preenchidas == 0) {
						if (StringUtils
								.isBlank(contDAU.headerPanel.getField("control_dau_txtResultadoControlo").getValue())) {
							erro = true;
							showErrorMessage("É necessário preencher o campo Relatório do Resultado do Controlo.");
							return true;
						} else {
							relatorioControlo = true;
						}
					} else if (countAdicoesA1Preenchidas > 0 && countAdicoesA1Preenchidas < totalAdicoes) {
						// se count<size adicoes e count>0, entao deve preencher todas as adicoes
						if (StringUtils
								.isBlank(contDAU.headerPanel.getField("control_dau_txtResultadoControlo").getValue())) {
							erro = true;
							showErrorMessage(
									"Deve preencher o campo Relatório do Resultado do Controlo de todas as adições.");
							return true;
						} else {
							erro = true;
							showErrorMessage(
									"Deve preencher o campo Relatório do Resultado do Controlo de todas as adições ou preenche apenas o Relatório do Resultado do Controlo ao nível da Declaração.");
							return true;
						}
					} else if (countAdicoesA1Preenchidas == totalAdicoes) {
						// se count=size adicoes, entao nao deve preencher o cabecalho e deve avancar
						if (StringUtils.isNotBlank(
								contDAU.headerPanel.getField("control_dau_txtResultadoControlo").getValue())) {
							erro = true;
							showErrorMessage("Não pode preencher o campo Relatório do Resultado do Controlo.");
							return true;
						}
					}
				} else {
					boolean adicoesValidas = verificaRelatorioPreenchidoAdicoes(ctrlDec.getListaControloItem());
					boolean relatorioDeclaracaoPreenchido = StringUtils.isNotBlank(
							contDAU.headerPanel.getField("control_dau_txtResultadoControlo").getValue());
					if(ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DLCC2) && adicoesValidas) {
						relatorioControlo = true;
					}
					else if (adicoesValidas && relatorioDeclaracaoPreenchido) {
							erro = true;
							showErrorMessage(
									"Deve preencher o campo Relatório do Resultado do Controlo de todas as adições ou preenche apenas o Relatório do Resultado do Controlo ao nível da Declaração.");
							return true;
						
					}
					else if (adicoesValidas && !relatorioDeclaracaoPreenchido) {
						relatorioControlo = true;
					} else {
						erro = true;
						showErrorMessage(
								"Deve preencher o campo Relatório do Resultado do Controlo de todas as adições que não sejam conformes.");
						return true;
					}

				}

			}

			// Verifica se todas as adicoes que estiverem marcadas para um controlo
			// documental ou fisico, devem ser registadas antes de submeter o controlo
			if (!validaAddControlo(session, ctrlDec)) {
				erro = true;
			}

			// Retira a obrigatoriedade da verificação da área documental se a flag for "F" e o sistema for caus
			String flagNotificacaoDoc = null;
			if (ctrlDec.getFlagNotificacaoDoc() != null) {
				flagNotificacaoDoc = ctrlDec.getFlagNotificacaoDoc();
			}

			if (flagNotificacaoDoc != null) {
				if (!(SGCUtils.validaSistemaCAU(ctrlDec.getSistema()) && flagNotificacaoDoc.equals("F"))){
					/**
					 * Valida se existe pelo menos um documento preenchido qd: - Controlo Documental
					 * - Controlo Fisico com verificador = conferente - Controlo CAP
					 **/
					if (!validaDocumentosPreenchidos(ctrlDec)) {

						erro = true;
						showErrorMessage(getMessage("control_result.error.documentos.porpreencher"));
						return true;
					}
				}
			} else {
				if (!validaDocumentosPreenchidos(ctrlDec)) {

					erro = true;
					showErrorMessage(getMessage("control_result.error.documentos.porpreencher"));
					return true;
				}
			}


//			// No caso de existir irregularidades valida se o resultado é não conforme
//			if(ctrl.getControloIrregularidade()!=null && !(new Short(SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO).equals(ctrl.getResultadoControlo()))){
//				erro = true;
//				showErrorMessage(getMessage("control_result.error.irregularidades.resultadocontrolonaoconforme"));
//				return true;
//				
//			}

			erro |= validaMeiosAferidosPreenchidos(ctrlDec);
			erro |= validaIrregularidadesPreenchidas(ctrlDec);

			if (existGeralFisico = false) {
				if (!validControloGeralFisico(ctrlDec)) {
					erro = true;
					showErrorMessage(getMessage("control_result.error.controlo.geral.fisico"));
					return true;
				}
			}
			if (erro) {
				setFormData();
				return true;
			}

		}
		if (!requerDadosAdd && !validaTipoControlo(session, idControlo)) {

			showErrorMessage(
					"Existem adições com resultado de controlo diferente do resultado do controlo atribuído ao cabeçalho.");
			return true;
		}

		/**
		 * 
		 * Sem erros de validacao
		 * 
		 **/

		try {

			if (ctrlDec != null) {

				getFormData();
				ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);

				/**
				 * 
				 * Vai actualizar a Declaracao
				 * 
				 **/

				ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session
						.getAttribute(SessionConstants.CONTROLO_MATRIZ);

				contDAU.getFormulario(ctrlDec, form);
				contDoc.getFormulario(ctrlDec, form);

				UserInfo user = (UserInfo) session.getAttribute(SessionConstants.USER);

				if (ctrlDec.getConferente() != null && ctrlDec.getVerificador() != null
						&& ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador())) {

					if (ctrlDec.getResultadoControlo() != null && SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO
							.equalsIgnoreCase(ctrlDec.getResultadoControlo().toString())) {
						ctrlDec.setDataControloDoc(new java.sql.Date(System.currentTimeMillis()));
					}

					ArrayList<ChaveDescricao> res = new ArrayList<ChaveDescricao>();
	                
	                if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())){
	    				ChaveDescricao cd = new ChaveDescricao();
	    				cd.setCodigo(ctrl.getTipoControlo());
	    				res.add(cd);
	        		} else {
	        			res = SGCUtils.traduzTipoControlo(ctrlDec.getTipoControlo(), null, ctrlDec.getSistema(), SGCConstantes.getIdMomento(ctrlDec.getMomento()));
	        		}
					
					if (ctrlDec.getDataControloDoc() == null && res != null
							&& (SGCConstantes.TIPO_CONTROLO_DOCUMENTAL.equalsIgnoreCase(res.get(0).getCodigo().trim())
									|| SGCConstantes.TIPO_CONTROLO_DOCUMENTAL_SPA
											.equalsIgnoreCase(res.get(0).getCodigo().trim())
									|| SGCConstantes.TIPO_CONTROLO_DOCUMENTAL_COMPL
											.equalsIgnoreCase(res.get(0).getCodigo().trim()))) {
						ctrlDec.setDataControloDoc(new java.sql.Date(System.currentTimeMillis()));
					}

					if (ctrlDec.getDataControloDoc() == null && res != null
							&& (SGCConstantes.TIPO_CONTROLO_FISICO.equalsIgnoreCase(res.get(0).getCodigo().trim())
									|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA
											.equalsIgnoreCase(res.get(0).getCodigo().trim())
									|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO
											.equalsIgnoreCase(res.get(0).getCodigo().trim()))) {
						ctrlDec.setDataControloFisico(new java.sql.Date(System.currentTimeMillis()));
					}

				} else if (ctrlDec.getConferente() != null && user.getUserId().equalsIgnoreCase(ctrlDec.getConferente())
						&& ctrlDec.getDataControloDoc() == null) {
					ctrlDec.setDataControloDoc(new java.sql.Date(System.currentTimeMillis()));
				} else if (ctrlDec.getVerificador() != null && user.getUserId().equalsIgnoreCase(ctrlDec.getVerificador())
						&& ctrlDec.getDataControloFisico() == null) {
					ctrlDec.setDataControloFisico(new java.sql.Date(System.currentTimeMillis()));
				}
				
				
				if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
					
					//Recupera a informação de resultado pendente de amostra
					String resultadoPendenteAmostra = contDAU.headerPanel.getField("control_dau_txtPendenteResultadoAmostra").getValue();
					if (resultadoPendenteAmostra.isEmpty())
						resultadoPendenteAmostra = null;
					ctrlDec.setPendenteAmostra(resultadoPendenteAmostra);

					//Recupera as data de inicio e fim de análise
					String dataInicioAnalise = contDAU.headerPanel.getField("item_cabecalho_caus_inicioControlo_label").getValue();
					String dataFimAnalise = contDAU.headerPanel.getField("item_cabecalho_caus_fimControlo_label").getValue();

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

					try {
						Timestamp timestampInicio = null;
						Timestamp timestampFim = null;

						if (StringUtils.isNotBlank(dataInicioAnalise)) {
							LocalDateTime localDateTimeInicio = LocalDateTime.parse(dataInicioAnalise.trim(), formatter);
							timestampInicio = Timestamp.valueOf(localDateTimeInicio);
						}

						if (StringUtils.isNotBlank(dataFimAnalise)) {
							LocalDateTime localDateTimeFim = LocalDateTime.parse(dataFimAnalise.trim(), formatter);
							timestampFim = Timestamp.valueOf(localDateTimeFim);
						}

						// Verifica se a data de início é posterior à data de fim
						if (timestampInicio != null && timestampFim != null && timestampInicio.after(timestampFim)) {
							showErrorMessage("A data de início não pode ser posterior à data de fim.");
							log.error("A data de início é posterior à data de fim.");
							return false;
						}

						// Atribui os valores convertidos ao objeto ctrlDec
						if (timestampInicio != null) {
							ctrlDec.setInicioCtrlFisi(timestampInicio);
						}

						if (timestampFim != null) {
							ctrlDec.setFimCtrlFisi(timestampFim);
						}

					} catch (DateTimeParseException e) {
						showErrorMessage("Erro ao converter datas. Formato esperado: yyyy-MM-dd HH:mm");
						log.error("Erro ao converter datas do controlo físico.", e);
						return false;
					}
					
					//Recupera dados do documento adicional para sistemas CAU's
					List<ControloDocumentoAdicional> cda = (List<ControloDocumentoAdicional>) session.getAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctrl.getChave().getNumeroControlo());
					ctrlDec.setListaDocumentosAdicionais(cda);
					
					//Recupera documentos conferidos para atualizar selos
					if(tabSelos != null) {
						List<ControloSeloWeb> selos = tabSelos.tabelaSelos.getRowList();
						if(selos != null && !selos.isEmpty()) {
							ControloSeloServiceT controloSeloService = EJBUtil.getSessionInterface(ControloSeloServiceT.class);
							for (ControloSeloWeb controloSelo : selos) {
								//String check = (String)form.getPage().getContext().getRequestParameter("control_selo_"+controloSelo.getNumItem()+"_chk_"+controloSelo.getChave().getIdSelo());
								ControloSelo controloSeloEntity = new ControloSelo();
								controloSeloEntity.setChave(controloSelo.getChave());
								controloSeloEntity.setEquipamentoSelo(controloSelo.getEquipamentoSelo());
//								controloSeloEntity.setNumControlo(controloSelo.getNumControlo());
								controloSeloEntity.setIdentSelo(controloSelo.getIdentSelo());
								controloSeloEntity.setRefMercadoriaSelo(controloSelo.getRefMercadoriaSelo());
								controloSeloEntity.setIndicadorSelo(controloSelo.getIndicadorSelo());
//								controloSeloEntity.setNumItem(controloSelo.getNumItem());
//								controloSeloEntity.setPosicao(controloSelo.getPosicao());
//								controloSeloEntity.setValor(controloSelo.getValor());
//								if("on".equalsIgnoreCase(check)){
//									controloSeloEntity.setIndicadorSelo("V");
//								}else {
//									controloSeloEntity.setIndicadorSelo("F");
//								}
								controloSeloService.atualizar(controloSeloEntity);
							}
						}
					}
					
					//ARMAZENA AS IDENTIFICACOES SELO NO CONTROLO
					List<IdentificacaoSelo> is = (List<IdentificacaoSelo>) session.getAttribute("identificacaoSelos"+ctrlDec.getChave().getNumeroControlo());
					if(is != null && !is.isEmpty()) {
						
						for (IdentificacaoSelo identificacaoSelo : is) {
							// Converte descrição selo em codigo
							if(identificacaoSelo.getSelos().contentEquals(SGCConstantes.RESULTADO_COMBOS_TRUE_DESCRICAO)) {
								identificacaoSelo.setSelos(SGCConstantes.RESULTADO_COMBOS_TRUE);
							}else if(identificacaoSelo.getSelos().contentEquals(SGCConstantes.RESULTADO_COMBOS_FALSE_DESCRICAO)) {
								identificacaoSelo.setSelos(SGCConstantes.RESULTADO_COMBOS_FALSE);
							}							
							// Converte descrição selagem meio Transporte em codigo
							if(identificacaoSelo.getSelTransp().contentEquals(SGCConstantes.RESULTADO_COMBOS_TRUE_DESCRICAO)) {
								identificacaoSelo.setSelTransp(SGCConstantes.RESULTADO_COMBOS_TRUE);
							}else if(identificacaoSelo.getSelTransp().contentEquals(SGCConstantes.RESULTADO_COMBOS_FALSE_DESCRICAO)) {
								identificacaoSelo.setSelTransp(SGCConstantes.RESULTADO_COMBOS_FALSE);
							}	
							//insere a lista no controlo para posterior persistir.
							ctrlDec.setListaSelos(is);
						}						
					}
					
					//Recupera a lista atualizada de controlo tipo item para atualizar
					Map<String, List> mapaComControloItemTipo =  (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
					
					if(mapaComControloItemTipo != null) {
						List<ControloItemTipo> listaControloItemTipo = new ArrayList<ControloItemTipo>();
						mapaComControloItemTipo.forEach((chave, valor) -> {
							listaControloItemTipo.addAll(valor);
						});
						
						ctrlDec.setListaControloItemTipo(listaControloItemTipo);
					}
					
					//Recupera a lista de item equipamentos para salvar
					Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo);
					if(listEquipamentosMap != null) {						
						
						List<ItemEquipamento> allItems = new ArrayList<>();
						
						for (List<ItemEquipamento> itemList : listEquipamentosMap.values()) {
							allItems.addAll(itemList);
						}
						
						ctrlDec.setListaEquipamentosSSA(allItems);
					}
					
					//Recupera a lista atualizada de controlo tipo item MANUAL para atualizar
					Map<String, List> mapaComControloItemTipoManual =  (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
					
					if(mapaComControloItemTipoManual != null) {
						List<ControloItemTipo> listaControloItemTipo = new ArrayList<ControloItemTipo>();
						mapaComControloItemTipoManual.forEach((chave, valor) -> {
							listaControloItemTipo.addAll(valor);
						});
						
						ctrlDec.setListaControloItemTipoManual(listaControloItemTipo);
						
						Map<String, List<ItemEquipamento>> listEquipamentosManuaisMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo);
						
						ctrlDec.setListEquipamentosManuaisMap(listEquipamentosManuaisMap);
					}
				}

				log.info("CONTROLO SUBMETER: " + ToStringUtils.toString(ctrlDec));
				session.setAttribute(SessionConstants.RES_CONTROLO + idControlo, ctrlDec);
				SessionManager.getInstance().setSessao(session);

				if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
					if (!SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO
							.equals(ctrlDec.getResultadoControlo().toString())
							&& !StringUtils.isNotBlank(getContext().getRequestParameter("valConfirm"))) {
						openConfirmSubmission = true;
						return true;
					}
				}

				/**
				 * Verifica se tipo de controlo e dos tipos Nao Conforme (1P, B1)
				 **/

				if (SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(ctrlDec.getResultadoControlo().toString()) 
					|| SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_CAU_COMBO.contentEquals(ctrlDec.getResultadoControlo().toString()) 
					|| SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.contentEquals(ctrlDec.getResultadoControlo().toString()) 
					|| SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(ctrlDec.getResultadoControlo().toString())) {

					if (StringUtils.isNotBlank(ctrlDec.getConferente())
							&& StringUtils.isNotBlank(ctrlDec.getVerificador())
							&& !(ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador()))
							&& ctrlDec.getDataControloDoc() != null && ctrlDec.getDataControloFisico() == null) {
						openProximaAccao = false;
						finalizaControl(relatorioControlo, ctrlDec);
					} else {
						if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_SIMTEMM)||ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS)||ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_TRACAU)||ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST)) {
							ctrlDec.setAlterar(SGCConstantes.PROXIMA_ACCAO_ESTRUTURADA);
							openProximaAccao = false;
							finalizaControl(relatorioControlo, ctrlDec);
						} else if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {

							if (SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(ctrlDec.getResultadoControlo().toString())) {
								openProximaAccao = false;
								finalizaControl(relatorioControlo, ctrlDec);
							}

							if (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(ctrlDec.getResultadoControlo().toString())) {
								openProximaAccao = true;
							}

						} else {
							openProximaAccao = true;
						}
					}

				} else {
					/**
					 * Se nao for, invoca o SD
					 **/
					finalizaControl(relatorioControlo, ctrlDec);
				}
			} else {
				setReadOnly(false);
				showErrorMessage("Erro ao Submeter o Resultado de Controlo - Contacte o Helpdesk, sff.");
			}

		} catch (ApplicationException e) {
			log.error("Erro ao Submeter o Resultado de Controlo - Contacte o Helpdesk, sff", e);
			showErrorMessage("Erro ao Submeter o Resultado de Controlo - Contacte o Helpdesk, sff");
			return false;
		}

		if (ctrlDec != null) {
			if (!openProximaAccao) {
				getFormData();
				ControloServiceT srvControlo = EJBUtil.getSessionInterface(ControloServiceT.class);

				// ctrlDec = Controlo
				// Introduz o dados na base de dados

				srvControlo.finalizaControlo(ctrlDec);
				
				NumeroAceitacao numAceitacao = NumeroAceitacao.create(ctrlDec.getNumIdentificacao(), ctrlDec.getSistema());
				String aceitacao = numAceitacao.toFormatoSemVersao();
				String aceitCompleto = numAceitacao.toFormatoCompleto();
				
				RetryServiceT srvRetry = EJBUtil.getSessionInterface(RetryServiceT.class);
				
				//Notificacao do Resultado Controlo ao SD
				srvRetry.registaPedido(SGCConstantes.TIPO_COMUNICACAO_RETRY_RESPONDER_RESULTADO_CONTROLO,
										SGCConstantes.TIPO_COMUNICACAO_RETRY_SISTEMA_GESTAO_CONTROLO, 
										ctrlDec.getSistema(), 
										aceitacao, 
										true, null, 
										aceitCompleto, 
										null, 
										null, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);

			} else {
				session.setAttribute(SessionConstants.RES_CONTROLO + idControlo, ctrlDec);
				SessionManager.getInstance().setSessao(session);
			}
		}
		if (!openProximaAccao) {
			setFormData();
			session.setAttribute(SessionConstants.REMOVE_CONTROLO, true);
			SessionManager.getInstance().setSessao(session);
		} else {//TODO: Siaaf
			session.setAttribute(SessionConstants.ON_POPUP, true);
			SessionManager.getInstance().setSessao(session);
		}
		return true;
	}

	private boolean validControloGeralFisico(Controlo ctrlDec) {

		if (ctrlDec != null && ctrlDec.getMeioAutAferido() != null) {
			MeioAutAferido meio = ctrlDec.getMeioAutAferido();
			if (StringUtils.isBlank(meio.getFlagMeioAut())) {
				return false;
			}
			if (StringUtils.isBlank(meio.getFlagSelagem())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * Metodo que invoca o circuito para processar o servico associado a submissao
	 * do controlo...
	 * 
	 * @param ctrlDec
	 * @throws TarefaException_Exception
	 * 
	 **/
	private void finalizaControl(boolean relatorioControlo, Controlo ctrlDec) throws ApplicationException {

		if (relatorioControlo)
			relatorioControlo(ctrlDec);

		if (ctrlDec != null) {		
			NumeroAceitacao numAceitacao = NumeroAceitacao.create(ctrlDec.getNumIdentificacao(), ctrlDec.getSistema());

			try {
				log.info("tryGenerateXmlNotifcontrol_v2");
				boolean notificaSSA = true;
				if (StringUtils.isNotBlank(ctrlDec.getConferente()) && StringUtils.isNotBlank(ctrlDec.getVerificador())
						&& !(ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador()))
						&& ctrlDec.getDataControloDoc() != null && ctrlDec.getDataControloFisico() == null) {
					notificaSSA = false;
				}
				if (SGCConstantes.RESULTADO_CONTROLO_DADOS_ADICIONAIS_ZZ_COMBO.equalsIgnoreCase(ctrlDec.getResultadoControlo().toString())
						|| SGCConstantes.RESULTADO_CONTROLO_DADOS_ADICIONAIS_ZZ_CAU_COMBO.equalsIgnoreCase(ctrlDec.getResultadoControlo().toString())
						|| SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equalsIgnoreCase(ctrlDec.getResultadoControlo().toString())
						|| SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equalsIgnoreCase(ctrlDec.getResultadoControlo().toString())) {
					notificaSSA = false;
				}
				if (notificaSSA) {
					generateXmlNotifControl(ctrlDec,numAceitacao,false);
				}

			} catch (DatatypeConfigurationException e) {
				log.error("DatatypeConfigurationException", e);
			}

			TarefasService srvTask = EJBUtil.getSessionInterface(TarefasService.class);
			boolean isClose = true;
			
				try {
//					 verifica se existe tarefa em aberto no GESTAR
					
					String idTarefaToClose = getTaskToClose(ctrlDec);
					if (StringUtils.isNotBlank(idTarefaToClose)) {
						isClose = srvTask.closeTask(idTarefaToClose, ctrlDec.getSistema());
						//Fecha a tarefa na SAT_CONTROLO_DET0 inserindo a data de fecho
						ControloDetalhesServiceT srvDet = EJBUtil.getSessionInterface(ControloDetalhesServiceT.class);
						srvDet.updateControloDetalhes(ctrlDec.getPK(), idTarefaToClose,new java.sql.Date(System.currentTimeMillis()));
				}
					} catch (TarefaException_Exception e) {
//					// TODO Auto-generated catch block
					log.error("Não foi possivel concluir a tarefa no GESTAR.", e);
				} catch (Exception e) {
					log.error("Apanhada excepcao geral. " + e.getClass(), e);
				}
	
				if (isClose)
					showSucessMessage(getMessage("control_result.submit.normal.message"));

				else
					showErrorMessage("Não foi possivel concluir a tarefa no GESTAR.");
				


			setReadOnly(true);
			this.setReadOnly(true);
			form.setReadonly(true);
			contDoc.docsTable.setReadOnly(true);
			contDoc.confereDoc.setDisabled(true);
			pageButtons.remove(submeterControl);
			pageButtons.remove(btPrintPDF);
			pageButtons.remove(btConsultDau);

			// removerSession();
		} else {
			showErrorMessage("Ocorreu um erro no procesamento do Controlo.");
		}
	}

	private String generateXmlNotifControl(Controlo ctrlDau, NumeroAceitacao numAceitacao, boolean isNotificacao) throws DatatypeConfigurationException {

		if (ctrlDau != null) {

			String utilizador = ctrlDau.getConferente();
			if (SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_SPA_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_POSTERIORI_IMPEC_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_IMPEC_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_IMPEC_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_SIMTEMM_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO_SIMTEMM_COMBO
							.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_POSTERIORI_DLCC2_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_DLCC2_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO.equals(ctrlDau.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlDau.getTipoControlo().toString())) {
				utilizador = ctrlDau.getVerificador();
			}
			log.info("generateXmlNotifControl#1:" + ctrlDau.getMomento());
			com.siemens.ssa.communicator.webservices.ssaservices.ControlResult result = ControlResultTransform
					.transformControloDeclaracaoIntoControlResultSSA(ctrlDau, numAceitacao, utilizador, isNotificacao);

			log.info("generateXmlNotifControl#2:" + ctrlDau.getNumAceitacao() + ";" + ctrlDau.getSistema() + ";"
					+ ctrlDau.getNumAceitacao() + ";" + ctrlDau.getMomento());
			if (result != null) {
				log.info("generateXmlNotifControl#3: result != null ");
				String xml = SGCUtils.transformSSAObjectToXml(result);
				String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_NOTIFICACAO_RESULTADO_CONTROLO;

				RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
	
				numAceitacao = NumeroAceitacao.create(ctrlDau.getNumIdentificacao(), ctrlDau.getSistema());
				String aceitacao = numAceitacao.toFormatoSemVersao();
				String aceitCompleto = numAceitacao.toFormatoCompleto();
				
				try {
					
					srv.registaPedido(	tipoComunicacao,
										ctrlDau.getSistema(), 
										SGCConstantes.SISTEMA_SSA, 
										aceitacao, 
										false, null, 
										aceitCompleto, 
										null, 
										xml, true, false,RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
					
				} catch (ApplicationException e) {
					log.error("Erro_notifyControlResult:",e);
					return SGCConstantes.RESULT_STADA_WS_ERRO;
				}
				return SGCConstantes.RESULT_STADA_WS_OK;

			}
		}

		return null;
	}

	private String getTaskToClose(Controlo ctrlDec) {

		ctrlDec.getListaControloOutro();

		String task = null;
		boolean tarefaAberta = false;
		TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);

		log.info("getTaskToClose#Controlo:" + ToStringUtils.toString(ctrlDec));
		if (ctrlDec != null) {

			String sistema = ctrlDec.getSistema();
			try {
				// Vai validar se a tarefa que tem de fechar e dos dados adicionais
				if (StringUtils.isNotBlank(ctrlDec.getIdTarefaZZ())) {
					Tarefa tarefa = tarefaSrv.pesquisarTarefa(ctrlDec.getIdTarefaZZ(), sistema);
					tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada));
					if (tarefaAberta) {
						task = ctrlDec.getIdTarefaZZ();
						return task;
					}
				}

				if (StringUtils.isNotBlank(ctrlDec.getIdTarefa())) {
					Tarefa tarefa = tarefaSrv.pesquisarTarefa(ctrlDec.getIdTarefa(), sistema);
					tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada));
					if (tarefaAberta) {
						task = ctrlDec.getIdTarefa();
						return task;
					}
				}

				if (StringUtils.isNotBlank(ctrlDec.getIdTarefaFisico())) {
					Tarefa tarefa = tarefaSrv.pesquisarTarefa(ctrlDec.getIdTarefaFisico(), sistema);
					tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada));
					if (tarefaAberta) {
						task = ctrlDec.getIdTarefaFisico();
						return task;
					}
				}

			} catch (TarefaException_Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private void removerSession() {
		log.info("ControlResult#removerSessionTo:" + idControlo);
		HttpSession session = getSession();
		session.removeAttribute(SessionConstants.RES_CONTROLO + idControlo);
		session.removeAttribute(SessionConstants.RES_CONTROLO_ADICAO);
		session.removeAttribute(SessionConstants.CONTROLO_MATRIZ);
		session.removeAttribute(SessionConstants.WEB_CLIENT + idControlo);
		session.removeAttribute(SessionConstants.WEB_CLIENT);
		session.removeAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + idControlo);
		session.removeAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
		session.removeAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
		session.removeAttribute(SessionConstants.ATTR_LISTA_RESULTADO_CONTROLO);
		session.removeAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo);
		session.removeAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo);
		session.removeAttribute("identificacaoSelos"+idControlo);
		session.removeAttribute("ControloTipoItemList");

		SessionManager.getInstance().setSessao(session);
	}

	private void relatorioControlo(Controlo controlo) {
		log.info("relatorioControlo");
		if ((SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_COMBO.equals(controlo.getResultadoControlo().toString())
				|| SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_CAU_COMBO.equals(controlo.getResultadoControlo().toString()))
				&& StringUtils.isNotBlank(controlo.getMotivoControlo())) {

			if (controlo.getListaControloItem() != null && controlo.getListaControloItem().size() > 0) {
				for (int i = 0; i < controlo.getListaControloItem().size(); i++) {

					ControloItem adicao = controlo.getListaControloItem().get(i);
					if (adicao != null && adicao.getResultadoControlo() != null
							&& (SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_COMBO.equals(adicao.getResultadoControlo().toString())
									|| SGCConstantes.RESULTADO_CONTROLO_CONFORME_A1_CAU_COMBO.equals(adicao.getResultadoControlo().toString()))
							&& StringUtils.isBlank(adicao.getMotivoControlo())) {
						adicao.setMotivoControlo("");
						adicao.setMotivoControlo(controlo.getMotivoControlo());
					}
				}
			}
		}
	}

	private int verificaTipoControloAdicoes(ArrayList<ControloItem> listAdicao, String resultadoControlo,
			boolean useSemControlo, String sistema) {
		log.info("verificaTipoControloAdicoes");
		int countPreenchidas = 0;
		int countFundamento = 0;

		if (listAdicao != null && listAdicao.size() > 0) {

			for (int i = 1; i < listAdicao.size(); i++) {

				ControloItem adicao = listAdicao.get(i);
				if (adicao != null && adicao.getResultadoControlo() != null
						&& resultadoControlo.equals(adicao.getResultadoControlo().toString())) {

					if (StringUtils.isNotBlank(adicao.getMotivoControlo()) && !SGCUtils.validaSistemaCAU(sistema)) {
						countPreenchidas++;
					}
					
					if(SGCUtils.validaSistemaCAU(sistema) && (SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(adicao.getResultadoControlo().toString()))) {
						if(StringUtils.isNotBlank(adicao.getFundamento())) {
							countFundamento++;
						}
		
					} else if (SGCUtils.validaSistemaCAU(sistema) && !(SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(adicao.getResultadoControlo().toString()))) {
						if (StringUtils.isNotBlank(adicao.getMotivoControlo())) {
							countPreenchidas++;
						}
					}
				}
				// se a Adicao for SC tem de contar como preenchido, pois num SC nao se preenche
				// nada da adicao
				else if (useSemControlo && adicao != null && adicao.getTipoControlo() != null
						&& (SGCConstantes.TIPO_CONTROLO_SEM_CONTROLO_COMBO.equals(adicao.getTipoControlo().toString()) 
								|| SGCConstantes.TIPO_CONTROLO_SEM_CONTROLO_CAU_COMBO.equals(adicao.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_NAO_SUBMITIDO_CONTROLO.equals(adicao.getTipoControlo().toString()))) {

					countPreenchidas++;
				}
			}
		}
		return countPreenchidas + countFundamento;
	}

	private boolean verificaRelatorioPreenchidoAdicoes(ArrayList<ControloItem> listAdicao) {
		log.info("verificaRelatorioPreenchidoAdicoes");
		if (listAdicao != null && listAdicao.size() > 0) {

			for (int i = 1; i < listAdicao.size(); i++) {

				ControloItem adicao = listAdicao.get(i);
				if (adicao != null && adicao.getResultadoControlo() != null) {
				    String resultado = adicao.getResultadoControlo().toString();
				    if (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(resultado)
				            || SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.equals(resultado)
				            || SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(resultado)
				            || SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_CAU_COMBO.equals(resultado)) {


						if (!StringUtils.isNotBlank(adicao.getMotivoControlo())) {
							return false;
						} else {
							return true;
						}
					
				    }
				}
			}
		}
		return true;
	}

	private void gereControloFisicoGeral(String numControlo, boolean readOnly) {
		log.info("gereControloFisicoGeral");
		// Usar circuito para determinar tipo de fase
		// Usar tarefa para determinar validade do utilizador contra a fase
		HttpSession session = getSession();
		Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + numControlo);
		UserInfo user = (UserInfo) session.getAttribute(SessionConstants.USER);
		if (ctrlDec == null) {
			log.info("Return Fisico Geral");
			return;
		}
		if (readOnly) {
			log.info("readonly true, Fisico Geral");
			geralFisico.setReadonly(true);
			existGeralFisico = true;
			contDoc.PhyControlDoc.setReadonly(true);
		} else {
			log.info("readonly false, Fisico Geral");
			/**
			 * 
			 * No caso de fase de circuito documental campos do fisico devem estar
			 * bloqueados
			 * 
			 * No caso de fase de circuito fisico: * Se user diferente
			 * (verificador!=conferente) entao parte documental tem de estar readOnly * Se
			 * users iguais (verificador==conferente): * Se tipo de controlo adicao
			 * documental - parte fisica deve estar readonly * Se tipo de controlo adicao
			 * fisico - ambos os separadores devem estar enabled
			 * 
			 * Se tipo de fase CAP: * Se tipo de controlo adicao documental - parte fisica
			 * deve estar readonly * Se tipo de controlo adicao fisico - ambos os
			 * separadores devem estar enabled
			 * 
			 */
//					Circuito circ=(Circuito)session.getAttribute( SessionConstants.ATTR_CIRCUITO);

//					ControloDocumento ctrlDec=(ControloDocumento)session.getAttribute(SGCConstantes.RES_CONTROLO);

			// Comeca sempre como readonly(true)
			if (geralFisico != null)
				geralFisico.setReadonly(true);

			if (SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_CAP_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_SPA_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_POSTERIORI_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_SIMTEMM_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO_SIMTEMM_COMBO
							.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_POSTERIORI_DLCC2_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_DLCC2_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO.equals(ctrlDec.getTipoControlo().toString())
					|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlDec.getTipoControlo().toString())) {
				if (ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador())) {
					if (geralFisico != null)
						geralFisico.setReadonly(false);
					if (contDoc != null)
						contDoc.setReadOnly(true);
				} else {

					if (user.getUserId().equalsIgnoreCase(ctrlDec.getVerificador())) {

						geralFisico.setReadonly(false);
						contDoc.setReadOnly(true);

					} else {

						geralFisico.setReadonly(true);
						contDoc.setReadOnly(false);

					}
				}
			}
		}
	}
}
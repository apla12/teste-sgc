package com.siemens.ssa.communicator.web.jsp.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.siemens.ssa.communicator.pojo.interfaces.*;
import com.siemens.ssa.communicator.util.SessionConstants;
import org.apache.click.Context;
import org.apache.click.control.Decorator;
import org.apache.click.control.Form;
import org.apache.click.control.Option;

import com.siemens.security.session.SessionManager;
import com.siemens.service.interfaces.ControloSeloServiceT;
import com.siemens.service.interfaces.IdentificacaoSeloServiceT;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;
import com.siemens.ssa.communicator.web.jsp.control.pojo.ControloSeloWeb;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.W3IntegerField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.ControlFieldColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabSelos extends DgitaLayoutPage {
	
	private static Log log = Log.getLogger(TabSelos.class);
	
	protected Tab tabSelos;
	public DgitaTable tabelaSelos = new DgitaTable("tabela_selos");
	public DgitaTable tabelaIdentificacaoSelos = new DgitaTable("tabela_identificacao_selos");
	
	//protected W3Submit confereSelos = new W3Submit("Conferir Todos",null,this,"conferir");
	//protected W3Submit confereSelos = new W3Submit("Conferir Todos", null, this, "conferir");
	//protected W3Submit desconferirSelos = new W3Submit("Desconferir Todos", null, this, "desconferir");
	protected W3Button confereSelos = new W3Button("Conferir Todos","conferir");
	protected W3Button desconferirSelos = new W3Button("Desconferir Todos","desconferir");

	public CompleteFieldSetPanel headerPanelSelos = new CompleteFieldSetPanel("painelSelos", "", "");
	
	public W3IntegerField cabecalhoAdicao = new W3IntegerField("cabecalhoAdicao");
	public W3Select selos = new W3Select("selos");
	public W3Select selagemMeioTransporte = new W3Select("selagemMeioTransporte");
	public W3IntegerField numeroSelos = new W3IntegerField("numeroSelos");
	public W3TextField identificacaoSelos = new W3TextField("identificacaoSelos");
	public W3TextField naoSelagemMotivo = new W3TextField("naoSelagemMotivo");
	W3Button botaoAdicionar = new W3Button("addInformacaoSelo", "Adicionar");
	W3Button botaoCancelar = new W3Button("btCancelarAlteracaoSelo", "Cancelar");
	Controlo controlo = null;
	DgitaLayoutPage page = null;
	private HttpSession session;
		
	public String url;
	
	public TabSelos(DgitaLayoutPage pg, DeclarationProcessor declarationProcessor, boolean readOnly,Controlo ctrl, String contextPath, DgitaLayoutPage tbControl, HttpSession session) {

		this.session = session;
		controlo = ctrl;
		headerPanelSelos.setFieldSetLayout(new FieldSetLayout(5, new String[] { "15%", "30%", "15%", "25%", "15%", "25%", "15%", "25%", "15%", "25%"}));
		headerPanelSelos.setAttribute("vertical-align", "bottom");
		headerPanelSelos.setStyle("margin-bottom-sm", "margin-bottom: 5px;");
		tabSelos = new Tab("TabSelos");
		page = pg;
		url = contextPath + tbControl.getContext().getPagePath(ControlResult.class);
		url += "?readOnly=" + readOnly;
		
		//Monta a tabela com os registros de controlo selo
		montaTabelaSelos(pg, readOnly, ctrl, declarationProcessor);
		
		//Monta o formulário de cadastro de IDENTIFICACAO SELO
		montaFormularioCadastroIdentificacao(readOnly, declarationProcessor);
		
		//Monta a tabela de reistros de identificação de selo
		montaTabelaIdentificacaoSelos(session, declarationProcessor, readOnly);
		
		//Algumas configurações para a TAB SELOS
		int[] flds = {1};
		tabSelos.setNumberFieldsPerLine(flds);
		tabSelos.addField(headerPanelSelos);
		if (readOnly) {
			tabSelos.setReadonly(readOnly);
		}
		
	}
	
	private void montaTabelaIdentificacaoSelos(HttpSession session, DeclarationProcessor declarationProcessor, boolean readonly) {
		
		tabelaIdentificacaoSelos.setAttribute("align", "center");
		tabelaIdentificacaoSelos.setShowBanner(false);
		tabelaIdentificacaoSelos.setShowLabelResult(false);
		
		AnonymousCallColumn editar = new AnonymousCallColumn("editar", "");
		editar.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				IdentificacaoSelo item = (IdentificacaoSelo) row;
				StringBuilder sb = new StringBuilder();
            	if(item.getChave().getIdSelo() != null ) { 
            		sb.append("'");
            		sb.append(item.getChave().getIdSelo());
            		sb.append("',");
            		
            		sb.append("'");
            		sb.append(item.getNumItem());
            		sb.append("',");
            		
            		sb.append("'");
            		sb.append(item.getSelos());
            		sb.append("',");
            		
            		sb.append("'");
            		sb.append(item.getSelTransp());
            		sb.append("',");
            		
            		sb.append("'");
            		sb.append(item.getQuantidadeSelosStr());
            		sb.append("',");
            		
            		sb.append("'");
            		sb.append(item.getIdentSelos());
            		sb.append("',");
            		
            		sb.append("'");
            		sb.append(item.getMotivoSelagem());
            		sb.append("'");
            	}
                
                return "<a href=\"#\" onclick=\"editarIdentificacaoSelo(" + sb.toString() + "); return false;\" class=\"fa fa-edit\"></a>";
			}
		}); 
		if(!readonly) {		
			tabelaIdentificacaoSelos.addColumn(editar);
		}

		ControlFieldColumn cabecalhoAdicao = new ControlFieldColumn("numItemStr");
		cabecalhoAdicao.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_selo_num_adicao"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_selo_num_adicao")) tabelaIdentificacaoSelos.addColumn(cabecalhoAdicao);
		
		ControlFieldColumn selos = new ControlFieldColumn("selos");
		selos.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_selo_flag_selo"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_selo_flag_selo")) tabelaIdentificacaoSelos.addColumn(selos);
		
		ControlFieldColumn selagemMeioTransporte = new ControlFieldColumn("selTransp");
		selagemMeioTransporte.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_selo_selagem"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_selo_selagem")) tabelaIdentificacaoSelos.addColumn(selagemMeioTransporte);
		
		ControlFieldColumn numeroSelos = new ControlFieldColumn("quantidadeSelosStr");
		numeroSelos.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_numero_selos"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_numero_selos")) tabelaIdentificacaoSelos.addColumn(numeroSelos);
		
		ControlFieldColumn identificacaoSelos = new ControlFieldColumn("identSelos");
		identificacaoSelos.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_identificacao_selos"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_identificacao_selos")) tabelaIdentificacaoSelos.addColumn(identificacaoSelos);
		
		ControlFieldColumn selagemMotivo = new ControlFieldColumn("motivoSelagem");
		selagemMotivo.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_nao_selagem_motivo"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_nao_selagem_motivo")) tabelaIdentificacaoSelos.addColumn(selagemMotivo);
		
		tabelaIdentificacaoSelos.setWidth(WebConstants.POPUP_TABLE_SIZE);
		
		AnonymousCallColumn remover = new AnonymousCallColumn("remover", "");
		remover.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				IdentificacaoSelo item = (IdentificacaoSelo) row;
            	if(item.getNumItem() != null ) { 
            		return "<a href=\"#\" onclick=\"excluirIdentificacaoSelo('" + item.getNumItemStr() + "'); return false;\" class=\"fa fa-trash-o\"></a>";
            	}
                return "";
			}
		}); 
		
		if(!readonly) {		
			tabelaIdentificacaoSelos.addColumn(remover);
		}

		if(!declarationProcessor.getWidgetVisible("control_dau_tabela_selo_num_adicao") &&
				!declarationProcessor.getWidgetVisible("control_dau_tabela_selo_flag_selo") &&
				!declarationProcessor.getWidgetVisible("control_dau_tabela_selo_selagem") &&
				!declarationProcessor.getWidgetVisible("control_dau_tabela_numero_selos") &&
				!declarationProcessor.getWidgetVisible("control_dau_tabela_identificacao_selos") &&
				!declarationProcessor.getWidgetVisible("control_dau_tabela_nao_selagem_motivo")) {
			tabelaIdentificacaoSelos.setNoPopUp(true);
			tabelaIdentificacaoSelos.setShowBanner(false);
			tabelaIdentificacaoSelos.setShowLabelResult(false);
		} else {
			headerPanelSelos.add(tabelaIdentificacaoSelos, 5);
		}
		
		carregaRegistrosIdentificacaoSelos(session);
		
	}

	private void carregaRegistrosIdentificacaoSelos(HttpSession session) {
		IdentificacaoSeloServiceT identificacaoSeloService = EJBUtil.getSessionInterface(IdentificacaoSeloServiceT.class);
		try {
			List<IdentificacaoSelo> listaExistente = (List<IdentificacaoSelo>) session.getAttribute("identificacaoSelos"+controlo.getChave().getNumeroControlo());
			if(listaExistente == null) {
				listaExistente = identificacaoSeloService.listaIdentificacaoSelos(controlo.getChave().getNumeroControlo());
			}
			List<IdentificacaoSelo> listaConvertida = new ArrayList<>();
			if (!listaExistente.isEmpty() ) {
				for (IdentificacaoSelo selo : listaExistente) {
					// Converte codigo selo em descrição
					if(selo.getSelos().contentEquals(SGCConstantes.RESULTADO_COMBOS_TRUE)) {
						selo.setSelos(SGCConstantes.RESULTADO_COMBOS_TRUE_DESCRICAO);
					}else if(selo.getSelos().contentEquals(SGCConstantes.RESULTADO_COMBOS_FALSE)) {
						selo.setSelos(SGCConstantes.RESULTADO_COMBOS_FALSE_DESCRICAO);
					}
					// Converte codigo selagem meio Transporte em descrição
					if(selo.getSelTransp().contentEquals(SGCConstantes.RESULTADO_COMBOS_TRUE)) {
						selo.setSelTransp(SGCConstantes.RESULTADO_COMBOS_TRUE_DESCRICAO);
					}else if(selo.getSelTransp().contentEquals(SGCConstantes.RESULTADO_COMBOS_FALSE)) {
						selo.setSelTransp(SGCConstantes.RESULTADO_COMBOS_FALSE_DESCRICAO);
					}					
					
					listaConvertida.add(selo);
				}
				tabelaIdentificacaoSelos.setRowList(listaConvertida);
				session.setAttribute("identificacaoSelos"+controlo.getChave().getNumeroControlo(), listaConvertida);
				SessionManager.getInstance().setSessao(session);
			}else {
				tabelaIdentificacaoSelos.setRowList(listaExistente);
				session.setAttribute("identificacaoSelos"+controlo.getChave().getNumeroControlo(), listaExistente);
				SessionManager.getInstance().setSessao(session);
			}
				
		} catch (ApplicationException e) {
			log.error("Erro ao carregar os resistros na tabela de Selos");
		}
		
	}

	private void montaFormularioCadastroIdentificacao(boolean readOnly, DeclarationProcessor declarationProcessor) {
		cabecalhoAdicao.setLabel(declarationProcessor.getWidgetLabel("control_dau_tabela_selo_num_adicao"));
		cabecalhoAdicao.setLabelShown(true);
		cabecalhoAdicao.setMaxLength(4);
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_selo_num_adicao")) headerPanelSelos.add(cabecalhoAdicao, 1);
		
		selos.setLabel(declarationProcessor.getWidgetLabel("control_dau_tabela_selo_flag_selo"));
		selos.setLabelShown(true);
		selos.add(new Option(SGCConstantes.RESULTADO_COMBOS_TRUE_DESCRICAO));
		selos.add(new Option(SGCConstantes.RESULTADO_COMBOS_FALSE_DESCRICAO));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_selo_flag_selo")) headerPanelSelos.add(selos, 1);
		
		selagemMeioTransporte.setLabel(declarationProcessor.getWidgetLabel("control_dau_tabela_selo_selagem"));
		selagemMeioTransporte.setLabelShown(true);
		selagemMeioTransporte.add(new Option(SGCConstantes.RESULTADO_COMBOS_TRUE_DESCRICAO));
		selagemMeioTransporte.add(new Option(SGCConstantes.RESULTADO_COMBOS_FALSE_DESCRICAO));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_selo_selagem")) headerPanelSelos.add(selagemMeioTransporte, 4);
		
		numeroSelos.setLabel(declarationProcessor.getWidgetLabel("control_dau_tabela_numero_selos"));
		numeroSelos.setLabelShown(true);
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_numero_selos")) headerPanelSelos.add(numeroSelos, 1);
		
		identificacaoSelos.setLabel(declarationProcessor.getWidgetLabel("control_dau_tabela_identificacao_selos"));
		identificacaoSelos.setLabelShown(true);
		identificacaoSelos.setMaxLength(60);
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_identificacao_selos")) headerPanelSelos.add(identificacaoSelos, 1);
		
		naoSelagemMotivo.setLabel(declarationProcessor.getWidgetLabel("control_dau_tabela_nao_selagem_motivo"));
		naoSelagemMotivo.setLabelShown(true);
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_nao_selagem_motivo")) headerPanelSelos.add(naoSelagemMotivo, 1);

		botaoAdicionar.setStyle("vertical-align","bottom;");
		botaoAdicionar.setStyle("margin-bottom", "5px;");
		botaoAdicionar.setAttribute("onclick", "adicionarIdentificacaoSelo(); return false;");
		
		botaoCancelar.setAttribute("onclick", "cancelarAlteracaoSelo(); return false;");
		botaoCancelar.setStyle("display", "none");
		botaoCancelar.setStyle("vertical-align","bottom");
		botaoCancelar.setStyle("margin-bottom", "5px");
		
		DIV buttonContainer = new DIV();
		buttonContainer.setStyle("text-align","right;");
		buttonContainer.setStyle("vertical-align", "bottom;");

		if (!readOnly) {
			if(!declarationProcessor.getWidgetVisible("control_dau_tabela_selo_num_adicao") &&
					!declarationProcessor.getWidgetVisible("control_dau_tabela_selo_flag_selo") &&
					!declarationProcessor.getWidgetVisible("control_dau_tabela_selo_selagem") &&
					!declarationProcessor.getWidgetVisible("control_dau_tabela_numero_selos") &&
					!declarationProcessor.getWidgetVisible("control_dau_tabela_identificacao_selos") &&
					!declarationProcessor.getWidgetVisible("control_dau_tabela_nao_selagem_motivo")) {
				buttonContainer.remove(botaoAdicionar);
				buttonContainer.remove(botaoCancelar);
			} else {
				buttonContainer.add(botaoAdicionar);
				buttonContainer.add(botaoCancelar);
			}
		}

		headerPanelSelos.add(buttonContainer, 1);
	}

	private void montaTabelaSelos(DgitaLayoutPage pg, boolean readOnly, Controlo ctrl, DeclarationProcessor declarationProcessor) {
		tabelaSelos.setAttribute("align", "center");

		ControlFieldColumn colunaNomeSelo = new ControlFieldColumn("equipamentoSelo");
		colunaNomeSelo.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_equipamentos_transporte_declarativo"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_equipamentos_transporte_declarativo")) tabelaSelos.addColumn(colunaNomeSelo);
		
		ControlFieldColumn colunaIdentificacaoSelo = new ControlFieldColumn("identSelo");
		colunaIdentificacaoSelo.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_identificacao_selos_declarativo"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_identificacao_selos_declarativo")) tabelaSelos.addColumn(colunaIdentificacaoSelo);

		ControlFieldColumn colunaRefMercadoriaSelo = new ControlFieldColumn("refMercadoriaSelo");
		colunaRefMercadoriaSelo.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_referencia_mercadorias_declarativo"));
		if(declarationProcessor.getWidgetVisible("control_dau_tabela_referencia_mercadorias_declarativo")) tabelaSelos.addColumn(colunaRefMercadoriaSelo);

		ControlFieldColumn colunaConfere = new ControlFieldColumn("conferido","Conferido");
		colunaConfere.setDecorator(new Decorator() {
			@Override
			public String render(Object object, Context context) {
				// O objeto aqui é uma instância de ControloSeloWeb
				ControloSeloWeb selo = (ControloSeloWeb) object;
				return selo.getConferido();
			}
		});

//		colunaConfere.setDecorator((object, context) -> {
//            ControloSeloWeb selo = (ControloSeloWeb) object;
//            // Se o selo estiver conferido, marca o checkbox, senão não:
//            String checked = SGCConstantes.FLAG_BD_VERDADEIRO.equals(selo.getIndicadorSelo()) ? "checked" : "";
//            // Adiciona a classe .checkbox-selos
//            return "<input type='checkbox' class='checkbox-selos' " + checked + " />";
//        });

		colunaConfere.setAttribute("style", "text-align:center");
		tabelaSelos.addColumn(colunaConfere);
		
		tabelaSelos.setWidth(WebConstants.POPUP_TABLE_SIZE);
		
		tabelaSelos.setSelectable(false);
		tabelaSelos.setOneRowOnly(false);
		if(!readOnly){
			confereSelos.setAttribute("onclick", "conferirSelos(); return false;");
			tabelaSelos.addButtonToTable(confereSelos, pg.getForm().getName());
			desconferirSelos.setAttribute("onclick", "desconferirSelos(); return false;");
			tabelaSelos.addButtonToTable(desconferirSelos, pg.getForm().getName());
		}
		headerPanelSelos.add(tabelaSelos, 5);
		carregaRegistros(ctrl, readOnly);
		
	}

	private void carregaRegistros(Controlo ctrl, boolean readOnly) {
		ControloSeloServiceT controloSeloService = EJBUtil.getSessionInterface(ControloSeloServiceT.class);
		try {
			List<ControloSeloWeb> listaSession = (List<ControloSeloWeb>) session.getAttribute("listaSelos" + controlo.getChave().getNumeroControlo());

			if (listaSession != null) {
				for (ControloSeloWeb controloSelo : listaSession) {
					controloSelo.setRead(readOnly);
				}
				tabelaSelos.setRowList(listaSession);
			} else {
				List<ControloSelo> selos = controloSeloService.listaSelosPorNumeroControlo(ctrl.getChave().getNumeroControlo());
				List<ControloSeloWeb> selosWeb = new ArrayList<ControloSeloWeb>();
				for (ControloSelo controloSelo : selos) {
					ControloSeloWeb csw = new ControloSeloWeb();
					csw.setChave(controloSelo.getChave());
//				csw.setNumControlo(controloSelo.getNumControlo());
//				csw.setPosicao(controloSelo.getPosicao());
//				csw.setNumItem(controloSelo.getNumItem());
//				csw.setValor(controloSelo.getValor());
					csw.setEquipamentoSelo(controloSelo.getEquipamentoSelo());
					csw.setIdentSelo(controloSelo.getIdentSelo());
					csw.setRefMercadoriaSelo(controloSelo.getRefMercadoriaSelo());
					csw.setIndicadorSelo(controloSelo.getIndicadorSelo());
					csw.setRead(readOnly);
					selosWeb.add(csw);
				}
				tabelaSelos.setRowList(selosWeb);
				session.setAttribute("listaSelos" + ctrl.getChave().getNumeroControlo(), selosWeb);
				SessionManager.getInstance().setSessao(session);
			}
		} catch (ApplicationException e) {
			log.error("Erro ao carregar os resistros na tabela de Selos");
		}
	}
	
	public void editarLinhaIdentificacaoSelos(String id) {
		IdentificacaoSelo is = (IdentificacaoSelo) tabelaIdentificacaoSelos.getTableItemFromTable(id);
		identificacaoSelos.setValue(is.getIdentSelos());
		selos.setValue(is.getSelos());
		naoSelagemMotivo.setValue(is.getMotivoSelagem());
		cabecalhoAdicao.setValue(is.getNumItemStr());
		numeroSelos.setValue(is.getQuantidadeSelosStr());
		selagemMeioTransporte.setValue(is.getSelTransp());
		botaoAdicionar.setAttribute("onclick", "alterarIdentificacaoSelo(); return false;");
		botaoAdicionar.setLabel("Alterar");
	}

	public boolean conferir() {
		log.info("TabSelos#conferir - Conferindo todos os selos do controlo " + controlo.getChave().getNumeroControlo());

		HttpSession session = this.session;


		List<ControloSeloWeb> listaSelos = (List<ControloSeloWeb>) tabelaSelos.getRowList();

		if (listaSelos != null) {
			for (ControloSeloWeb selo : listaSelos) {
				selo.setIndicadorSelo(SGCConstantes.FLAG_BD_VERDADEIRO);
			}
			tabelaSelos.setRowList(listaSelos);
		}

		// Atualiza na sessão se necessário (se for usada para persistência)
		session.setAttribute("listaSelos" + controlo.getChave().getNumeroControlo(), listaSelos);
		SessionManager.getInstance().setSessao(session);

		return true;
	}

	public boolean desconferir() {
		log.info("TabSelos#desconferir - Desconferindo todos os selos do controlo " + controlo.getChave().getNumeroControlo());

		List<ControloSeloWeb> listaSelos = (List<ControloSeloWeb>) tabelaSelos.getRowList();

		if (listaSelos != null) {
			for (ControloSeloWeb selo : listaSelos) {
				selo.setIndicadorSelo(SGCConstantes.FLAG_BD_FALSO);
			}
			tabelaSelos.setRowList(listaSelos);
		}

		session.setAttribute("listaSelos" + controlo.getChave().getNumeroControlo(), listaSelos);
		SessionManager.getInstance().setSessao(session);

		return true;
	}

	public Controlo getFormulario(Controlo ctrlDec, Form form) {
		// Recupera os registros da tabela de selos (objeto ControloSeloWeb)
		List<ControloSeloWeb> listaWeb = tabelaSelos.getRowList();
		List<IdentificacaoSelo> listaSelosConvertida = new ArrayList<>();

		if (listaWeb != null) {
			for (ControloSeloWeb ws : listaWeb) {
				IdentificacaoSelo is = converterSelo(ws);
				listaSelosConvertida.add(is);
			}
		}

		// Atualiza o objeto de controle com a lista convertida
		ctrlDec.setListaSelos(listaSelosConvertida);
		return ctrlDec;
	}

	private IdentificacaoSelo converterSelo(ControloSeloWeb ws) {
		IdentificacaoSelo is = new IdentificacaoSelo();

		IdentificacaoSeloPK chave = new IdentificacaoSeloPK();

		chave.setIdSelo(ws.getChave().getIdSelo());
		chave.setNumControlo(ws.getChave().getNumControlo());
		is.setChave(chave);
		is.setIdentSelos(ws.getIdentSelo());
		is.setSelos(ws.getEquipamentoSelo());
		is.setSelTransp(ws.getRefMercadoriaSelo());

		return is;
	}

	@Override
	protected void buildPage() {

	}

	@Override
	protected void setFormData() {

	}

	@Override
	protected void getFormData() {

	}
}

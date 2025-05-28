package com.siemens.ssa.communicator.web.jsp.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.Option;
import org.apache.click.control.Panel;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

import com.siemens.security.session.SessionManager;
import com.siemens.service.interfaces.ControloItemTipoServiceT;
import com.siemens.service.interfaces.DadosGeraisService;
import com.siemens.service.interfaces.ItemEquipamentoServiceT;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItemTipo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloMatriz;
import com.siemens.ssa.communicator.pojo.interfaces.DadosGerais;
import com.siemens.ssa.communicator.pojo.interfaces.ItemEquipamento;
import com.siemens.ssa.communicator.pojo.interfaces.ItemEquipamentoPK;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;
import com.siemens.ssa.communicator.web.jsp.control.caus.SistemasCLsEnum;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.dao.ChaveDescricao;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3Checkbox;
import pt.atos.web.click.controls.field.W3HiddenField;
import pt.atos.web.click.controls.field.W3Label;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.ButtonPanel;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.panels.Span;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabControlAdicao extends DgitaLayoutPage{
	private static final long serialVersionUID = 821290961702149287L;
	private static Log log = Log.getLogger(TabControlAdicao.class);
	
	protected CompleteFieldSetPanel headerPanel = new CompleteFieldSetPanel("control_add_headerPanel", null, null);
	protected CompleteFieldSetPanel headerPanelAdicao = new CompleteFieldSetPanel("adicao_add_headerPanel", null, null);
	 
	public Tab PhyControlAdd;
	private W3Select comboResultadoControlo;
	private W3Select comboControloAtribuido;
	private W3Select comboControloReSelecao;
	private W3Select comboSubResultadoControlo;
	private W3Select comboSubControloReSelecao;
	private W3Select comboResultadoAdicao;
	
	private W3TextField controloTipoItem;
	private W3Checkbox chkDeclarantePresente;
	private W3TextArea txtRelatorioResultadoControlo;
	private W3TextField txtFundamentoMotivo;
	private W3TextArea txtMotivoAtribuicaoControlo;
	private W3Button btRegisto 	= new W3Button("botaoRegistoControloAdicao","Registar");
	
	private W3Button btRegistoResultadoAdicao 	= new W3Button("btRegistoResultadoAdicao","Registar");
	private W3Button btCancelarResultadoAdicao 	= new W3Button("btCancelarResultadoAdicao","Cancelar");
	private W3Button btCancelar = new W3Button("bt_cancelar", "Cancelar");
	private ButtonPanel botaoRegistoControloAdicao = new ButtonPanel("painelControloAdicaoBotoes", "", true);
	public DgitaTable tabelaResultadoAdicao = new DgitaTable("tabela_resultado_adicao");
	private String sistema;
	private String idControlo;
	public String url;
	private DIV divCombos;
	Panel wrapperPanel;
	
	private DIV tituloIdentificadores;
	W3Select selectIdentificador;
	W3Select selectAreaRisco;
	W3TextField inputAnaliseRisco;
	W3Select selectTipo;
	W3Select selectResultado;
	W3Label titulo;
	
	private W3Button btRegistroItemTipo	= new W3Button("btRegistroItemTipo","Adicionar");
	private W3Button btCancelarRegistroItemTipo	= new W3Button("btCancelarRegistroItemTipo","Cancelar");
	private DIV divBotoes;
	public DgitaTable tabelaItemTipoManual = new DgitaTable("tabela_item_tipo_manual");
	
	public TabControlAdicao(DgitaLayoutPage tbControl, ControloItem ctrlAdi,boolean readOnly, String sistema, String idControlo, DeclarationProcessor declarationProcessor) {
		this.sistema = sistema;
		this.idControlo = idControlo;
		url = contextPath + "/SeloServlet";
		url += "?readOnly=" + readOnly;
		
		PhyControlAdd = new Tab("PhyControlAdd");
		
		HiddenField idControloTipoItem = new HiddenField("idControloTipoItem", String.class);
		HiddenField input_hidden_id_row = new HiddenField("id_row", String.class);
		
		tbControl.getForm().add(idControloTipoItem);
		tbControl.getForm().add(input_hidden_id_row);
		
		comboResultadoControlo = new W3Select("control_add_comboResultadoControlo", false);
		comboResultadoControlo.setLabel(declarationProcessor.getWidgetLabel(comboResultadoControlo.getId()));
		comboResultadoControlo.setLabelShown(true);
		
		comboResultadoControlo.setRequired(true);
		comboResultadoControlo.setTooltip(declarationProcessor.getWidgetHelp(comboResultadoControlo.getId()));
		
		comboControloAtribuido = new W3Select("control_add_comboControloAtribuido", false);
		comboControloAtribuido.setLabel(declarationProcessor.getWidgetLabel(comboControloAtribuido.getId()));
		comboControloAtribuido.setLabelShown(true);
		comboControloAtribuido.setReadonly(true);
		comboControloAtribuido.setTooltip(declarationProcessor.getWidgetHelp(comboControloAtribuido.getId()));
		
		comboControloReSelecao = new W3Select("control_add_comboControloReSelecao", false);
		comboControloReSelecao.setLabel(declarationProcessor.getWidgetLabel(comboControloReSelecao.getId()));
		comboControloReSelecao.setLabelShown(true);
		comboControloReSelecao.setTooltip(declarationProcessor.getWidgetHelp(comboControloReSelecao.getId()));
		
		comboSubResultadoControlo = new W3Select("control_add_comboResultadoSubControlo", false);
		comboSubResultadoControlo.setLabel(declarationProcessor.getWidgetLabel(comboSubResultadoControlo.getId()));
		comboSubResultadoControlo.setLabelShown(true);
		//comboSubResultadoControlo.setTooltip(declarationProcessor.getHelpLabel(comboResultadoControlo.getId()));
		
		comboSubControloReSelecao = new W3Select("control_add_comboSubControloReSelecao", false);
		comboSubControloReSelecao.setLabel(declarationProcessor.getWidgetLabel(comboSubControloReSelecao.getId()));
		comboSubControloReSelecao.setLabelShown(true);
		//comboSubControloReSelecao.setTooltip(declarationProcessor.getHelpLabel(comboControloReSelecao.getId()));
		
		chkDeclarantePresente = new W3Checkbox("control_add_chkDeclarantePresente");
		chkDeclarantePresente.setChecked(false);
		chkDeclarantePresente.setLabelShown(true);

		txtRelatorioResultadoControlo = new W3TextArea("control_add_txtRelatorioResultadoControlo", false);
		txtRelatorioResultadoControlo.setLabel(declarationProcessor.getWidgetLabel(txtRelatorioResultadoControlo.getId()));
		txtRelatorioResultadoControlo.setLabelShown(true);
		txtRelatorioResultadoControlo.setFocus(false);
		txtRelatorioResultadoControlo.setMaxLength(2000);
		txtRelatorioResultadoControlo.setCols(80);
		txtRelatorioResultadoControlo.setRows(3);
		txtRelatorioResultadoControlo.setTooltip(declarationProcessor.getWidgetHelp(txtRelatorioResultadoControlo.getId()));
		
		Controlo ctrlDec = (Controlo) tbControl.getContext().getSession().getAttribute(SessionConstants.RES_CONTROLO+idControlo);
		
		if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
			
			comboResultadoControlo.setAttribute("onchange", "fundamentoAdicao(this); reseleccaoAdicao(this);");

			DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
			
			Column identificadorSSA = new Column("identificadorSSA");
			identificadorSSA.setWidth("15%");
			identificadorSSA.setRenderId(true);
			identificadorSSA.setHeaderTitle(declarationProcessor.getWidgetLabel("column_identificador_tabela_controlo_item"));

			Column areaRisco = new Column("areaRisco");
			areaRisco.setWidth("15%");
			areaRisco.setRenderId(true);
			areaRisco.setHeaderTitle(declarationProcessor.getWidgetLabel("column_area_risco_tabela_detalhe_item_tipo"));
			areaRisco.setDecorator(new Decorator() {	
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						if (item != null && item.getAreaRisco() != null){
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAreaRisco(), ctrlDec.getSistema());
							return item.getAreaRisco() +" - "+ dg.getDescricao();		
						}
					} catch (ApplicationException e) {
					}
					return "";
				}
			});
			
			Column analiseRisco = new Column("analiseRisco");
			analiseRisco.setWidth("15%");
			analiseRisco.setRenderId(true);
			analiseRisco.setHeaderTitle(declarationProcessor.getWidgetLabel("column_codigo_identificacao_tabela_analise_risco"));
			analiseRisco.setDecorator(new Decorator() {	
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						if (item != null && item.getAnaliseRisco() != null){
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAnaliseRisco(), ctrlDec.getSistema());
							return item.getAnaliseRisco() +" - "+ dg.getDescricao();		
						}
					} catch (ApplicationException e) {
					}
					return "";
				}
			});
			
			
			
			Column tipoAdicao = new Column("tipoControlo");
			tipoAdicao.setWidth("25%");
			tipoAdicao.setRenderId(true);
			tipoAdicao.setHeaderTitle(declarationProcessor.getWidgetLabel("column_tipo_tabela_item_tipo"));
			tipoAdicao.setDecorator(new Decorator() {	
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						if(item != null && item.getTipoControlo() != null) {
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getTipoControlo(), ctrlDec.getSistema());
							return item.getTipoControlo() +" - "+ dg.getDescricao();
						}
					} catch (ApplicationException e) {
					}
					return "";
				}
			});
			
			AnonymousCallColumn resultadoControlo = new AnonymousCallColumn("resultado");
			resultadoControlo.setHeaderTitle(declarationProcessor.getWidgetLabel("column_resultado_tabela_item_tipo"));
			resultadoControlo.setRenderId(true);
			resultadoControlo.setWidth("20%");
			resultadoControlo.setCall(new AnonymousCall() {
				@Override
				public String getDataContent(Object row, Context context, int rowIndex) {
					ControloItemTipo item = (ControloItemTipo) row;
	            	String resultado = item.getResultado() != null ? item.getResultado() : "";
	            	String str= "";
	            	if(!readOnly) {	 
	            		String itemId = item.getChave().getDetItemControlo().toString();
	            		str = str.concat("<a href=\"javascript:void(0);\" onclick=\"recuperaLinhaTipoItem('" + itemId + "'); return false;\" class=\"fa fa-edit\"></a>&nbsp;");
	            	}
	            	if(!resultado.equals("")) {  
	            		try {
	            			if(resultado.length() > 1) {
	            				DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getResultado().split("-")[0].trim(), ctrlDec.getSistema());
	            				if(dg != null) {	            					
	            					str = str.concat("<span id=txtResult"+item.getChave().getDetItemControlo()+">"+item.getResultado().split("-")[0].trim() + " - " + dg.getDescricao() + "</span>");
	            				}
	            			}
						} catch (ApplicationException e) {
						}
	            	} else {
	            		str = str.concat("<span id=txtResult"+item.getChave().getDetItemControlo()+"></span>");
	            	}
	               return str;
				}
			});
			
			Column resultadoAdicao = new Column("select", "");
			resultadoAdicao.setDecorator(new Decorator() {
				
				@Override
				public String render(Object object, Context context) {
					String idUnico = "select-" + ((ControloItemTipo) object).getChave().getDetItemControlo().toString(); 
					comboResultadoAdicao = new W3Select("select_form_resultado_item_tipo", false);
					comboResultadoAdicao.setLabel(declarationProcessor.getWidgetLabel(comboResultadoAdicao.getId()));
					comboResultadoAdicao.setLabelShown(false);
					comboResultadoAdicao.setRequired(false);
					comboResultadoAdicao.setAttribute("style", "display:none;");		
					comboResultadoAdicao.setId(idUnico);
					comboResultadoAdicao.setTooltip(declarationProcessor.getWidgetHelp(comboResultadoAdicao.getId()));
					return comboResultadoAdicao.toString();
				}
			});
			
			tabelaResultadoAdicao.setNoPopUp(true);
	        tabelaResultadoAdicao.setShowBanner(false);
	        tabelaResultadoAdicao.setShowLabelResult(false);

	        tabelaResultadoAdicao.setStyle("margin-top", "10px");
	        tabelaResultadoAdicao.addColumn(identificadorSSA);
			tabelaResultadoAdicao.addColumn(areaRisco);
			tabelaResultadoAdicao.addColumn(analiseRisco);
			tabelaResultadoAdicao.addColumn(tipoAdicao);
			tabelaResultadoAdicao.addColumn(resultadoControlo);
			tabelaResultadoAdicao.addColumn(resultadoAdicao);
			
	        controloTipoItem = new W3TextField("input_form_tipo_item_tipo");
	        controloTipoItem.setLabel(declarationProcessor.getWidgetLabel(controloTipoItem.getId()));
	        controloTipoItem.setLabelShown(true);
	        controloTipoItem.setDisabled(true);
	        controloTipoItem.setAttribute("style", "width: 300px");
	        controloTipoItem.setTooltip(declarationProcessor.getWidgetHelp(controloTipoItem.getId()));
			
			btRegistoResultadoAdicao.setStyle("vertical-align","bottom;");
			btRegistoResultadoAdicao.setStyle("margin-bottom", "0px;");
			
			btCancelarResultadoAdicao.setStyle("vertical-align","bottom;");
			btCancelarResultadoAdicao.setStyle("margin-bottom", "0px;");

			txtFundamentoMotivo = new W3TextField("txt_fundamento_form_item_tipo", false);
			txtFundamentoMotivo.setLabel(declarationProcessor.getWidgetLabel(txtFundamentoMotivo.getId()));
			txtFundamentoMotivo.setLabelShown(true);
			txtFundamentoMotivo.setFocus(false);
			txtFundamentoMotivo.setMaxLength(2000);
			txtFundamentoMotivo.setTooltip(declarationProcessor.getWidgetHelp(txtFundamentoMotivo.getId()));
			
			String tituloIdentificadores = "<h3 style=\"margin-left:0px ; margin-top:0px\" class='panel-title-non-expandable'>Identificadores adicionais</h3>\r\n" + 
					"<div class='panel-heading' style='padding-top: 0px;margin-left:0px ; width: 99%'></div>";
			titulo = new W3Label("titulo", tituloIdentificadores);
			
			// ####### IDENTIFICADORES MANUAIS #######
			DIV divInputIdentificador = new DIV();
			divInputIdentificador.setStyle("width", "228px");
			selectIdentificador = new W3Select("input_identificador_form_tipo_item_tipo");
			selectIdentificador.setAttribute("disabled", "true");
			selectIdentificador.setLabel(declarationProcessor.getWidgetLabel("column_identificador_tabela_controlo_item"));
			divInputIdentificador.add(selectIdentificador);
			
			DIV divSelectAreaRisco = new DIV();
			divSelectAreaRisco.setStyle("width", "180px");
			divSelectAreaRisco.setStyle("margin-left", "8px");
			selectAreaRisco = new W3Select("input_area_risco_form_tipo_item_tipo");
			selectAreaRisco.setAttribute("onchange", "preencherIdentificadores()");
			selectAreaRisco.setLabel(declarationProcessor.getWidgetLabel("column_area_risco_tabela_detalhe_item_tipo"));
			divSelectAreaRisco.add(selectAreaRisco);
			
			DIV divSelectAnaliseRisco = new DIV();
			divSelectAnaliseRisco.setStyle("width", "288px");
			divSelectAnaliseRisco.setStyle("margin-left", "8px");
			inputAnaliseRisco = new W3TextField("input_analise_risco_form_tipo_item_tipo");
			inputAnaliseRisco.setLabel(declarationProcessor.getWidgetLabel("column_codigo_identificacao_tabela_analise_risco"));
			divSelectAnaliseRisco.add(inputAnaliseRisco);
			
			DIV divSelectTipo = new DIV();
			divSelectTipo.setStyle("width", "180px");
			divSelectTipo.setStyle("margin-left", "8px");
			selectTipo = new W3Select("input_tipo_form_tipo_item_tipo");
			selectTipo.setLabel(declarationProcessor.getWidgetLabel("input_form_tipo_item_tipo"));
			divSelectTipo.add(selectTipo);
			
			DIV divSelectResultado = new DIV();
			divSelectResultado.setStyle("width", "180px");
			divSelectResultado.setStyle("margin-left", "8px");
			selectResultado = new W3Select("input_resultado_form_tipo_item_tipo");
			selectResultado.setLabel(declarationProcessor.getWidgetLabel("select_form_resultado_item_tipo"));
			divSelectResultado.add(selectResultado);
			
			divBotoes = new DIV();
			
			divBotoes.setStyle("align-items","flex-end;");
			divBotoes.setStyle("display","inline-flex");
			divBotoes.setStyle("vertical-align", "bottom;");
			divBotoes.setStyle("margin-bottom", "5px;");

			btRegistroItemTipo.setStyle("margin-left", "8px");
			btRegistroItemTipo.setOnClick("salvarControleTipoItemManual()");
			btCancelarRegistroItemTipo.setStyle("margin-left", "8px");
			btCancelarRegistroItemTipo.setOnClick("cancelarEdicaoItemTipoManual()");
			
			
			btRegistroItemTipo.setStyle("vertical-align","bottom;");
			btRegistroItemTipo.setStyle("margin-bottom", "0px;");
			
			btCancelarRegistroItemTipo.setStyle("vertical-align","bottom;");
			btCancelarRegistroItemTipo.setStyle("margin-bottom", "0px;");
			btCancelarRegistroItemTipo.setStyle("display", "none;");
			
			divBotoes.add(btRegistroItemTipo);
			divBotoes.add(btCancelarRegistroItemTipo);
			
			// TABELA DE ITEM TIPO MANUAL
			AnonymousCallColumn edit = new AnonymousCallColumn("editar");
			edit.setWidth("5%");
			edit.setHeaderTitle("");
			if(!readOnly) {				
				edit.setCall(new AnonymousCall() {
					@Override
					public String getDataContent(Object row, Context context, int rowIndex) {
						ControloItemTipo item = (ControloItemTipo) row;
						if (item != null && item.getChave() != null) {
							
							StringBuilder sb = new StringBuilder();
							sb.append("this");
							sb.append(",'");
							sb.append(item.getChave().getDetItemControlo());
							sb.append("','");
							sb.append(item.getIdentificadorSSA());
							sb.append("','");
							sb.append(item.getAreaRisco());
							sb.append("','");
							sb.append(item.getAnaliseRisco());
							sb.append("','");
							sb.append(item.getTipoControlo());
							sb.append("','");
							sb.append(item.getResultado());
							return "<a href=\"#\" onclick=\"recuperaRegistroManualItemTipo(" + sb.toString() + "'); return false;\" class=\"fa fa-pencil fa-lg\"></a>";
						}
						return "";
					}
				});
			} else {
				edit.setCall(new AnonymousCall() {
					@Override
					public String getDataContent(Object row, Context context, int rowIndex) {
						return "";
					}
				});
			}

			Column identificadorSSAManual = new Column("identificadorSSA");
			identificadorSSAManual.setWidth("15%");
			identificadorSSAManual.setRenderId(true);
			identificadorSSAManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_identificador_tabela_controlo_item"));

			Column areaRiscoManual = new Column("areaRisco");
			areaRiscoManual.setWidth("20%");
			areaRiscoManual.setRenderId(true);
			areaRiscoManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_area_risco_tabela_detalhe_item_tipo"));
			areaRiscoManual.setDecorator(new Decorator() {
				
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAreaRisco(), ctrlDec.getSistema());
						return item.getAreaRisco() +" - "+ dg.getDescricao();		
					} catch (ApplicationException e) {
					}
					return "";
//					try {
//						List<DadosGerais> dgs = dadosGeraisService.getPorValorSistema(item.getAreaRisco(), ctrlDec.getSistema());
//						if(dgs != null && !dgs.isEmpty()) {							
//							return dgs.get(0).getChave().getValor() + " - " + dgs.get(0).getDescricao();
//						}
//					} catch (ApplicationException e) {
//						
//					}
//					return "";
				}
			});
			
			Column analiseRiscoManual = new Column("analiseRisco");
			analiseRiscoManual.setWidth("20%");
			analiseRiscoManual.setRenderId(true);
			analiseRiscoManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_codigo_identificacao_tabela_analise_risco"));
			analiseRiscoManual.setDecorator(new Decorator() {
				
				@Override
				public String render(Object row, Context context) {
					
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						DadosGerais dgs = dadosGeraisService.buscarPorCodigoSistema(item.getAnaliseRisco(), ctrlDec.getSistema());
						if(dgs != null) {							
							return dgs.getCodigo() + " - " + dgs.getDescricao();
						}
					} catch (ApplicationException e) {
						
					}
					return "";
				}
			});
			
			Column tipoAdicaoManual = new Column("tipoControlo");
			tipoAdicaoManual.setWidth("25%");
			tipoAdicaoManual.setRenderId(true);
			tipoAdicaoManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_tipo_tabela_item_tipo"));
			tipoAdicaoManual.setDecorator(new Decorator() {	
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						if(item != null && item.getTipoControlo() != null) {
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getTipoControlo(), ctrlDec.getSistema());
							return item.getTipoControlo() +" - "+ dg.getDescricao();
						}
					} catch (ApplicationException e) {
					}
					return "";
				}
			});
			
			AnonymousCallColumn resultadoControloManual = new AnonymousCallColumn("resultado");
			resultadoControloManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_resultado_tabela_item_tipo"));
			resultadoControloManual.setRenderId(true);
			resultadoControloManual.setWidth("20%");
			resultadoControloManual.setCall(new AnonymousCall() {
				@Override
				public String getDataContent(Object row, Context context, int rowIndex) {
					ControloItemTipo item = (ControloItemTipo) row;
	            	String resultado = item.getResultado() != null ? item.getResultado() : "";
	            	if(!resultado.equals("")) {  
	            		try {
	            			if(resultado.length() > 1) {
	            				DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getResultado().split("-")[0].trim(), ctrlDec.getSistema());
	            				if(dg != null) {	            					
	            					return item.getResultado().split("-")[0].trim() + " - " + dg.getDescricao();
	            				}
	            			}
						} catch (ApplicationException e) {
						}
	            	} 
	               return "";
				}
			});
			
			AnonymousCallColumn remove = new AnonymousCallColumn("remover");
			remove.setHeaderTitle("");
			if(!readOnly) {				
				remove.setCall(new AnonymousCall() {
					@Override
					public String getDataContent(Object row, Context context, int rowIndex) {
						ControloItemTipo item = (ControloItemTipo) row;
						if (item != null && item.getChave() != null) {
							return "<a href=\"#\" onclick=\"removerItemTipoManual('" + item.getChave().getDetItemControlo() + "', this); return false;\" class=\"fa fa-trash-o\"></a>";
						}
						return "";
					}
				});
			} else {
				remove.setCall(new AnonymousCall() {
					@Override
					public String getDataContent(Object row, Context context, int rowIndex) {
						return "";
					}
				});
			}
			
			tabelaItemTipoManual.addColumn(edit);
			tabelaItemTipoManual.addColumn(identificadorSSAManual);
			tabelaItemTipoManual.addColumn(areaRiscoManual);
			tabelaItemTipoManual.addColumn(analiseRiscoManual);
			tabelaItemTipoManual.addColumn(tipoAdicaoManual);
			tabelaItemTipoManual.addColumn(resultadoControloManual);
			tabelaItemTipoManual.addColumn(remove);
			tabelaItemTipoManual.setStyle("style", "none;");
			tabelaItemTipoManual.setStyle("margin-top", "20px;");
			tabelaItemTipoManual.setNoPopUp(true);
			tabelaItemTipoManual.setShowBanner(false);
			tabelaItemTipoManual.setShowLabelResult(false);
			
			
		} else {
			
			comboResultadoControlo.setAttribute("onchange", "reseleccaoAdicao(this);");
			
			txtMotivoAtribuicaoControlo = new W3TextArea("control_add_txtMotivoAtribuicaoControlo");
			txtMotivoAtribuicaoControlo.setReadonly(true);
			txtMotivoAtribuicaoControlo.setLabel(declarationProcessor.getWidgetLabel(txtMotivoAtribuicaoControlo.getId()));
			txtMotivoAtribuicaoControlo.setLabelShown(true);
			txtMotivoAtribuicaoControlo.setMaxLength(2000);
			txtMotivoAtribuicaoControlo.setCols(80);
			txtMotivoAtribuicaoControlo.setRows(3);
			txtMotivoAtribuicaoControlo.setTooltip(declarationProcessor.getWidgetHelp(txtMotivoAtribuicaoControlo.getId()));
		
		}
		
		headerPanel.setFieldSetLayout(new FieldSetLayout(5,new String[]{"10%","10%","5%","15%","10%","10%","10%","5%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%"}));
		headerPanel.setPage(tbControl);
		
		headerPanelAdicao.setFieldSetLayout(new FieldSetLayout(6,new String[]{"10%","10%","10%","10%","10%","15%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","10%","5%","5%"}));
		
		if(declarationProcessor.getWidgetVisible(comboControloAtribuido.getId())) headerPanel.add(comboControloAtribuido,3);
		if(declarationProcessor.getWidgetVisible(comboResultadoControlo.getId())) headerPanel.add(comboResultadoControlo,2);
		if(declarationProcessor.getWidgetVisible(comboControloReSelecao.getId())) headerPanel.add(comboControloReSelecao,3);
		if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
			if(declarationProcessor.getWidgetVisible(txtFundamentoMotivo.getId()))headerPanel.add(txtFundamentoMotivo, 4);
		}

		tabelaResultadoAdicao.setShowBanner(false);
		tabelaResultadoAdicao.setShowLabelResult(false);
		tabelaItemTipoManual.setShowBanner(false);
		tabelaItemTipoManual.setShowLabelResult(false);
		
		if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
			if(declarationProcessor.getWidgetVisible(tabelaResultadoAdicao.getId())) headerPanel.add(tabelaResultadoAdicao, 5);
			headerPanel.add(titulo, 5);
			headerPanelAdicao.add(selectIdentificador, 1);
			headerPanelAdicao.add(selectAreaRisco, 1);
			headerPanelAdicao.add(inputAnaliseRisco, 1);
			headerPanelAdicao.add(selectTipo, 1);
			headerPanelAdicao.add(selectResultado, 1);
			headerPanelAdicao.add(divBotoes, 1);
			
			headerPanel.add(headerPanelAdicao, 5);
			headerPanel.add(tabelaItemTipoManual, 5);
			
			//if(declarationProcessor.getWidgetVisible(controloTipoItem.getId()) || declarationProcessor.getWidgetVisible(comboResultadoAdicao.getId())) headerPanel.add(divCombos,4); //Não tem sentido desabilitar apenas um dos campos
			if(declarationProcessor.getWidgetVisible(txtRelatorioResultadoControlo.getId())) headerPanel.add(txtRelatorioResultadoControlo, 5);
		}else {
			if(declarationProcessor.getWidgetVisible(txtMotivoAtribuicaoControlo.getId())) headerPanel.add(txtMotivoAtribuicaoControlo, 5);
			if(declarationProcessor.getWidgetVisible(txtRelatorioResultadoControlo.getId())) headerPanel.add(txtRelatorioResultadoControlo, 5);
		}
		
		botaoRegistoControloAdicao.setPage(tbControl);
		botaoRegistoControloAdicao.setForm(tbControl.form);
		
		int[] flds = {1,1};
		PhyControlAdd.setNumberFieldsPerLine(flds);
		PhyControlAdd.setForm(tbControl.form);
		
		PhyControlAdd.addField(headerPanel);
		//PhyControlAdd.addField(headerPanelAdicao);
		
	}

	/**
	 * Funï¿½ï¿½o para fazer colocar no forumlï¿½rio os dados
	 * passados como parï¿½metro
	 * 
	 * @param adi_ - pojo do controlo da declaraï¿½ï¿½o para o preenchimento
	 * @param tbControl - formulï¿½rio da pï¿½gina passado por parï¿½metro
	 */
	public void setFormulario(ControloItem adi_, DgitaLayoutPage tbControl){
		log.info("TabControlAdicao .... SetFormulario");
		
		TabelasApoioServiceT tabS = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		ArrayList<String> codigo = null;
		
		HttpSession session2 = getSession();
		Controlo ctrlDec = (Controlo) session2.getAttribute(SessionConstants.RES_CONTROLO + idControlo);	
		if((ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_IMPEC)) &&
				ctrlDec.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_POSTERIORI_IMPEC_COMBO)) {
			codigo = new ArrayList<String>(Arrays.asList("3","4","6","7","9"));
		}
		ArrayList<ChaveDescricao> descResControlo = new ArrayList<ChaveDescricao>();

		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		
		if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
			try {
				List<DadosGerais> dadosGerais = dadosGeraisService.getPorValorSistema(SistemasCLsEnum.obterCodigoPeloNome(ctrlDec.getSistema()),ctrlDec.getSistema());
				for (DadosGerais item : dadosGerais) {
					ChaveDescricao cd = new ChaveDescricao();
					cd.setChaveFields(new Object[] {item.getCodigo()});
					cd.setCodigo(item.getCodigo());
					cd.setDescricao(item.getCodigo() + " - " + item.getDescricao());
					descResControlo.add(cd);
				}
				
				descResControlo.removeIf(filter -> filter.getCodigo().equals(SGCConstantes.RESULTADO_CONTROLO_DADOS_ADICIONAIS_ZZ_CAU_COMBO));
				
			} catch (ApplicationException e) {
	
			}
		
			// VAI POPULAR O COMBO COM AS AREAS DE RISCO
			List<Option> listaAreasRisco;
			try {
				listaAreasRisco = dadosGeraisService.getPorValorSistema(SGCConstantes.CHAVE_DOMINIO_COMBO_CLIAREA, ctrlDec.getSistema()).stream().map(s -> new Option(s.getCodigo(),  s.getCodigo() + " - " + s.getDescricao())).collect(Collectors.toList());
				listaAreasRisco.add(0, new Option("0", "---"));
				selectAreaRisco.setOptionList(listaAreasRisco);
				selectAreaRisco.setValue("0");
				
			} catch (ApplicationException e1) {
				log.error("Não foi possível recuperar a lista de tipos");
			}
			
			// VAI POPULAR O COMBO DE TIPO PARA INSERÇÃO MANUAL
			List<Option> listaDadosGeraisTipo;
			try {
				listaDadosGeraisTipo = dadosGeraisService.getPorValorSistema(SGCConstantes.CHAVE_DOMINIO_COMBO_CL716, ctrlDec.getSistema(), SGCConstantes.FLAG_BD_FALSO).stream().map(s -> new Option(s.getCodigo(),  s.getCodigo() + " - " + s.getDescricao())).collect(Collectors.toList());
				
				// SE FOR CONTROLO DOCUMENTAL SO PODE CONSTAR O TIPO 10
				// SE FOR CONTROLO FÍSICO E F_DOC VERDADEIRO SÓ PODE CONSTAR O TIPO 20 E 30
				
				if(ctrlDec.getTipoControlo().contentEquals(SGCConstantes.TIPO_CONTROLO_DOCUMENTAL)) {
					listaDadosGeraisTipo.removeIf(item -> !item.getValue().contentEquals("10"));
				} else if (ctrlDec.getTipoControlo().contentEquals(SGCConstantes.TIPO_CONTROLO_FISICO) && ctrlDec.getFlagNotificacaoDoc() != null && ctrlDec.getFlagNotificacaoDoc().contentEquals(SGCConstantes.FLAG_BD_FALSO)) {
					listaDadosGeraisTipo.removeIf(item -> !item.getValue().contentEquals("20") && !item.getValue().contentEquals("30") );
				}
				
				selectTipo.setOptionList(listaDadosGeraisTipo);
			} catch (ApplicationException e1) {
				log.error("Não foi possível recuperar a lista de tipos");
			}
			
			// SETA VALOR DEFAULT PARA CAR
			try {		
				DadosGerais dadosGeraisValorX = dadosGeraisService.buscarPorCodigoSistema("X", ctrlDec.getSistema());
				inputAnaliseRisco.setValue(dadosGeraisValorX.getCodigo() + " - " + dadosGeraisValorX.getDescricao());
				inputAnaliseRisco.setAttribute("disabled", "true");
			} catch (ApplicationException e2) {
				log.error("Não foi possível recuperar a descrição");
			}
			
			// VAI POPULAR O COMBO DE RESULTADO DO CONTROLO
			try {
				List<Option> resultadoControlo = dadosGeraisService.getPorValorSistema(SGCConstantes.CHAVE_DOMINIO_COMBO_SGC_CL, ctrlDec.getSistema()).stream().map(s -> new Option(s.getCodigo(),  s.getCodigo() + " - " +s.getDescricao())).collect(Collectors.toList());
				selectResultado.setOptionList(resultadoControlo);
				
			} catch (ApplicationException e1) {
				log.error("Não foi possível recuperar a lista de area de risco");
			}
			
		} else {			
			descResControlo= tabS.getDescricaoResultadoControloPorCodigo(codigo, false, true);
		}
		
		for(ChaveDescricao chaveDescricao : descResControlo) {
			comboResultadoControlo.add(new Option(chaveDescricao.getID(), chaveDescricao.getDescricao()));
		}
		

		ArrayList<ChaveDescricao> descControloAtrib = new ArrayList<ChaveDescricao>();
		ArrayList<ChaveDescricao> tipoControloReseleccao = new ArrayList<ChaveDescricao>();
		
		if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DAIN)
				|| ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU)
				|| ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_TRACAU)
				|| ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST)
				|| ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_NR)
				|| ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {

			try {
				List<DadosGerais> dadosGerais = dadosGeraisService
						.getPorValorSistema(SGCConstantes.CHAVE_DOMINIO_COMBO_CL716, ctrlDec.getSistema());
				for (DadosGerais item : dadosGerais) {
					ChaveDescricao cd = new ChaveDescricao();
					cd.setChaveFields(new Object[] { item.getCodigo() });
					cd.setCodigo(item.getCodigo());
					cd.setDescricao(item.getDescricao());
					descControloAtrib.add(cd);
				}
				
				//Para o campo reseleção dos CAU, só pode constar o CF
				List<DadosGerais> dadosGeraisNacionais = dadosGeraisService
						.getPorValorCodigoSistema(SGCConstantes.CHAVE_DOMINIO_COMBO_CL716, "CF", ctrlDec.getSistema(), "V");
				for (DadosGerais item : dadosGeraisNacionais) {
					ChaveDescricao cd = new ChaveDescricao();
					cd.setChaveFields(new Object[] { item.getCodigo() });
					cd.setCodigo(item.getCodigo());
					cd.setDescricao(item.getDescricao());
					tipoControloReseleccao.add(cd);
				}
			} catch (ApplicationException e) {

			}

		} else {
			descControloAtrib = tabS.getChaveDescricaoTipoControlo(ctrlDec.getSistema(), null, null, false,SGCConstantes.getIdMomento(ctrlDec.getMomento()), false);
			Short codSistema = SGCConstantes.HASH_SYSTEM_CODE.get(sistema);
			tipoControloReseleccao= tabS.getChaveDescricaoTipoControloReSeleccao(true, codSistema.toString(),SGCConstantes.getIdMomento(ctrlDec.getMomento()));
		}
		
		for(ChaveDescricao chaveDescricao : tipoControloReseleccao) {
			comboControloReSelecao.add(new Option(chaveDescricao.getID(), chaveDescricao.getDescricao()));
		}
		
		for(ChaveDescricao chaveDescricao : descControloAtrib) {
			comboControloAtribuido.add(new Option(chaveDescricao.getID(), chaveDescricao.getDescricao()));
		}
		
		
		if(adi_!=null){			
			log.info("TabControlAdicao .... SetFormulario, Com adicao");
			headerPanel.getField("control_add_comboResultadoControlo").setValue(adi_.getResultadoControlo());	
			if(adi_.getResultadoControlo() != null && adi_.getResultadoControlo().equals(SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO)) {
				headerPanel.getField("control_add_comboControloReSelecao").setDisabled(false);
			}
			headerPanel.getField("control_add_comboControloAtribuido").setValue(adi_.getTipoControlo());
			log.info("Resultado controlo Adicao: "+adi_.getResultadoControlo());
			headerPanel.getField("control_add_comboControloReSelecao").setValue(adi_.getTipoControloReSelecao());	
			
			//O campo foi movido para o TabCopntrolFisico
			
			/*if(SGCConstantes.FLAG_BD_VERDADEIRO.equalsIgnoreCase(("" + adi_.getIndicadorOperadorPresente()).trim())){
				((W3Checkbox)headerPanel.getField("control_add_chkDeclarantePresente")).setChecked(true);
			} else {
				((W3Checkbox)headerPanel.getField("control_add_chkDeclarantePresente")).setChecked(false);
			}*/

			log.info("SSA Adicao: "+adi_.getMotivoControlo());
			log.info("Motivo Adicao: "+adi_.getInfoSSA());
			/*if(showLabelCD){
				if(StringUtils.isNotBlank(adi_.getMotivoControlo())){
					headerPanel.getField("control_add_txtRelatorioResultadoControlo").setValue(adi_.getMotivoControlo());
				} else {
					headerPanel.getField("control_add_txtRelatorioResultadoControlo").setValue("");
				}
			} else {*/
				headerPanel.getField("control_add_txtRelatorioResultadoControlo").setValue(adi_.getMotivoControlo());	
			//}
			String numAdicao=null;
			if(adi_.getChave()!=null && adi_.getChave().getNumeroItem()!=null){
				numAdicao=new Integer(adi_.getChave().getNumeroItem()).toString();
				if(StringUtils.isNotBlank(adi_.getChave().getIndVirtual())){
					numAdicao=numAdicao.concat(adi_.getChave().getIndVirtual());
				}
			}
			HttpSession session = getSession();
			ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session.getAttribute(SessionConstants.CONTROLO_MATRIZ);
			if (matriz != null){
				for (int i=0; i < matriz.size(); i++){		 
					
					if (matriz.get(i).getCodSeparador().equals("ITEM_IRR")){
						btRegisto.setOnClick("selectItemBox = document.getElementById('form_item_irr_table2');" +
								 "for (var x = 0; x < selectItemBox.options.length; x++) { selectItemBox.options[x].selected = true; } " +
								 "ajaxUpdateDivWithLine('registarControloAdd','main-form','"+numAdicao+"','"+tbControl.getContextPath()+tbControl.getContext().getPagePath(ControlResult.class)+"');");
					}
					else {
						btRegisto.setOnClick("ajaxUpdateDivWithLine('registarControloAdd','main-form','"+numAdicao+"','"+tbControl.getContextPath()+tbControl.getContext().getPagePath(ControlResult.class)+"');");
					}
				}
			}
		
		if(adi_ != null) {			
			if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())) {
				
				
				if(adi_.getResultadoControlo() == null || adi_.getResultadoControlo().contentEquals("-1") || !adi_.getResultadoControlo().equals(SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO)) {
					txtFundamentoMotivo.setDisabled(true);
				}
				
				if(txtFundamentoMotivo != null) {
					txtFundamentoMotivo.setValue(adi_.getFundamento());	
				}
				
				ControloItemTipoServiceT controloItemService = EJBUtil.getSessionInterface(ControloItemTipoServiceT.class);
				List<ControloItemTipo> listItemTipo = null;
				Map<String, List> mapControloItemTipo = null;
				
				try {
					//Verifica se existe a sessão que trata os identificadores do SSA (SSA - TRUE) na sessão.
					if(getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo) != null) {
						//se nao é nula recupera o mapa da sessão. A chave vo mapa é composta pelo id do controlo e o numero da adicao. Desta forma ela fica distinta de outras declarações.
						mapControloItemTipo = (Map) getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
						//recupera a lista específica da adição que está sendo detalhada.
						listItemTipo = mapControloItemTipo.get(idControlo + "" + adi_.getChave().getNumeroItem());
						if(listItemTipo == null) {
							//se a lista é nula o sistema tenta verificar se existem registros já cadastrados no database.
							listItemTipo = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo(), SGCConstantes.FLAG_BD_VERDADEIRO);
						}
					} else {
						//Não existe ainda o mapa na sessão, então ele vai criar um mapa vazio e vai buscar no db a lista de controlo item tipo e armazenar no mapa e depois na sessão.
						mapControloItemTipo = new HashMap<String, List>();
						listItemTipo = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo(), SGCConstantes.FLAG_BD_VERDADEIRO);					
					}
					mapControloItemTipo.put(idControlo + "" + adi_.getNumAdicao(), listItemTipo);
					
					//adiciona a lista de controlo item tipo na tabela
					tabelaResultadoAdicao.setRowList(listItemTipo);
					
					//os resultados são colocados em sessão para serem recuperados via servlet CAUSSERVLET
					tbControl.getContext().getSession().setAttribute(SessionConstants.ATTR_LISTA_RESULTADO_CONTROLO+idControlo,comboResultadoControlo.getOptionList());
					
					tbControl.getContext().getSession().setAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo, mapControloItemTipo);
					SessionManager.getInstance().setSessao(tbControl.getContext().getSession());
					
					
					// ############ EQUIPAMENTOS #########
					Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map<String, List<ItemEquipamento>>) tbControl.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo);

					if (listEquipamentosMap == null) {
					    ItemEquipamentoServiceT itemEquipamentoServiceT = 
					        EJBUtil.getSessionInterface(ItemEquipamentoServiceT.class);
					    listEquipamentosMap = new HashMap<>();

					    try {    
					    	//Já busca e armazena todos os equipamentos que foram cadastrados nos identificadores do ssa.
					        List<ItemEquipamento> listaExistente = itemEquipamentoServiceT.listarEquipamentos(
					            tabelaResultadoAdicao.getRowList());

					        for (ItemEquipamento itemEquipamento : listaExistente) {
					        	//Se a lista for existente, percorremos a lista armazenando no mapa. Considerando a chave sendo o id do controlo item tipo

					            List<ItemEquipamento> listaEquipamento = listEquipamentosMap.get(itemEquipamento.getChave().getDetItemControlo().toString());

					            if (listaEquipamento != null) {
					                listaEquipamento.add(itemEquipamento);
					            } else {
					                listaEquipamento = new ArrayList<>();
					                listaEquipamento.add(itemEquipamento);
					                listEquipamentosMap.put(itemEquipamento.getChave().getDetItemControlo().toString(), listaEquipamento);
					            }
					        }

					        tbControl.getContext().getSession().setAttribute(
					            SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo, listEquipamentosMap);
					        SessionManager.getInstance().setSessao(tbControl.getContext().getSession());

					    } catch (ApplicationException e) {
					        log.error("Erro ao obter a lista de equipamentos", e);
					    }
					}
					
					// VAI MONTAR A LISTA DE CONTROLO ITEM TIPO E COLOCAR EM SESSÃO (segue a mesma logica feita para os controlo item tipo)
					Map<String, List> mapControloItemTipoManual = null;
					List<ControloItemTipo> listItemTipoManual = null;
					if(getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo) != null) {
						mapControloItemTipoManual = (Map) getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
						listItemTipoManual = mapControloItemTipoManual.get(idControlo+""+adi_.getNumAdicao());
						if(listItemTipoManual == null) {
							listItemTipoManual = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo(), SGCConstantes.FLAG_BD_FALSO);
						}
					} else {
						mapControloItemTipoManual = new HashMap<String, List>();
						listItemTipoManual = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo(), SGCConstantes.FLAG_BD_FALSO);					
					}
					mapControloItemTipoManual.put( adi_.getChave().getNumeroControlo()+""+adi_.getNumAdicao(), listItemTipoManual);
					
					tabelaItemTipoManual.setRowList(listItemTipoManual);
					
					tbControl.getContext().getSession().setAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo, mapControloItemTipoManual);
					SessionManager.getInstance().setSessao(tbControl.getContext().getSession());
					
				} catch (ApplicationException e) {

				}
				
				// ############ EQUIPAMENTOS MANUAIS #########
				Map<String, List<ItemEquipamento>> listEquipamentosManuaisMap = (Map<String, List<ItemEquipamento>>) tbControl.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo);

				if (listEquipamentosManuaisMap == null) {
				    ItemEquipamentoServiceT itemEquipamentoServiceT = 
				        EJBUtil.getSessionInterface(ItemEquipamentoServiceT.class);
				    listEquipamentosManuaisMap = new HashMap<>();

				    try {    
				        List<ItemEquipamento> listaExistente = itemEquipamentoServiceT.listarEquipamentos(tabelaItemTipoManual.getRowList());

				        for (ItemEquipamento itemEquipamento : listaExistente) {
				            String sbChaveMapa = itemEquipamento.getChave().getDetItemControlo().toString();

				            List<ItemEquipamento> listaEquipamento = listEquipamentosManuaisMap.get(sbChaveMapa);

				            if (listaEquipamento != null) {
				                listaEquipamento.add(itemEquipamento);
				            } else {
				                listaEquipamento = new ArrayList<>();
				                listaEquipamento.add(itemEquipamento);
				                listEquipamentosManuaisMap.put(sbChaveMapa, listaEquipamento);
				            }
				        }

				        tbControl.getContext().getSession().setAttribute(
				            SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo, listEquipamentosManuaisMap);
				        SessionManager.getInstance().setSessao(tbControl.getContext().getSession());

				    } catch (ApplicationException e) {
				        log.error("Erro ao obter a lista de equipamentos", e);
				    }
				}
			} else {
				headerPanel.getField("control_add_txtMotivoAtribuicaoControlo").setValue(adi_.getInfoSSA());
			}
		}
	}
	
//					btRegisto.setOnClick("selectItemBox = document.getElementById('form_item_irr_table2');" +
//								 "for (var x = 0; x < selectItemBox.options.length; x++) { selectItemBox.options[x].selected = true; } " +
//								 "ajaxUpdateDivWithLine('registarControloAdd','main-DIV','"+numAdicao+"','"+tbControl.getContextPath()+tbControl.getContext().getPagePath(ControlResult.class)+"');");

	}

	/**
	 * 
	 * Mï¿½todo que preenche o POJO com os dados correspondentes no FORM da pï¿½gina
	 * 
	 * */
	public ControloItem getFormulario(ControloItem adi_, Form form/*,boolean showLabelCD*/){
		
		HttpSession session = getSession();
		Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);	
		
		TabelasApoioServiceT tabS = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		//Combo Resultado Controlo
		String comb1 = (String)form.getPage().getContext().getRequestParameter("control_add_comboResultadoControlo");
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
				
		if (StringUtils.isNotBlank(comb1) && !comb1.equals("-1")) {
			adi_.setResultadoControlo(comb1);
			ArrayList<String> list = new ArrayList<String>();
			list.add(comb1);

			if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())){
				try {
					DadosGerais dadosGerais = dadosGeraisService.buscarPorCodigoSistema(comb1, ctrlDec.getSistema());
					if(dadosGerais != null) {					
						adi_.setDescResultadoControlo(dadosGerais.getCodigo() + " - " + dadosGerais.getDescricao());
					}
				} catch (ApplicationException e) {
				}
				
			} else {
				ArrayList<ChaveDescricao> descResControlo= tabS.getDescricaoResultadoControloPorCodigo(list,false, true);
				if(descResControlo!=null && descResControlo.size()>0) {				
					adi_.setDescResultadoControlo(descResControlo.get(0).getDescricao());
				}				
			}
			
			log.info("getFormulario#RESULTADO CONTROLO: "+adi_.getDescResultadoControlo());			
		}
		
		//Combo Tipo Controlo
		String comb2 = (String)form.getPage().getContext().getRequestParameter("control_add_comboControloAtribuido");
		
		if (StringUtils.isNotBlank(comb2)) {
			adi_.setTipoControlo(comb2);
			
			if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())){
				try {
					DadosGerais dadosGerais = dadosGeraisService.buscarPorCodigoSistema(adi_.getTipoControlo(), ctrlDec.getSistema());
					adi_.setDescTipoControlo(dadosGerais.getDescricao());
				} catch (ApplicationException e) {
					
				}
				
			} else {				
				ArrayList<ChaveDescricao> descTpControlo= tabS.getChaveDescricaoTipoControlo(ctrlDec.getSistema(), adi_.getTipoControlo(),null,new Boolean(false),SGCConstantes.getIdMomento(ctrlDec.getMomento()),new Boolean(true));
				adi_.setDescTipoControlo(descTpControlo.get(0).getDescricao());
			}
		}
		
		//Combo Tipo Controlo Re-Selecï¿½ao
		if(StringUtils.isNotBlank(comb1) && (SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(comb1) || SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(comb1))){
		
			String comb3 = (String)form.getPage().getContext().getRequestParameter("control_add_comboControloReSelecao");
			
			if (StringUtils.isNotBlank(comb3)) {
				adi_.setTipoControloReSelecao(comb3);
			}
		}else{
			adi_.setTipoControloReSelecao(null);
		}
			
		
		/*if(showLabelCD){
			String motivo=(String)form.getPage().getContext().getRequestParameter("control_add_txtRelatorioResultadoControlo");
log.info("ContoloAdicao-getFormulario#motivo:"+motivo);	
			String text=(String)form.getPage().getContext().getRequestParameter("control_add_txtRelatorioResultadoControlo");
			if(StringUtils.isNotBlank(text)){
				motivo=motivo.concat(text);
			}
			adi_.setMotivoControlo(motivo);
		}
		else{*/
			adi_.setMotivoControlo((String)form.getPage().getContext().getRequestParameter("control_add_txtRelatorioResultadoControlo"));	
		//}
		
		adi_.setInfoSSA((String)form.getPage().getContext().getRequestParameter("control_add_txtMotivoAtribuicaoControlo"));
		if(txtFundamentoMotivo != null) {			
			adi_.setFundamento(txtFundamentoMotivo.getValue());
		}
		
		return adi_;	
	}
	
	public void onRender(DgitaLayoutPage tbControl,boolean readOnly, boolean reduceMaxLengthDoc, String motivo) {
		
		if (readOnly){
			headerPanel.setReadonly(readOnly);	
			btCancelar.setLabel("Fechar");
		}
		else{

			botaoRegistoControloAdicao.add(btRegisto);
		}
		int maxLength=2000;
		
		//actualiza o campo
		txtRelatorioResultadoControlo.setMaxLength(maxLength);
		
		String ajaxURL = tbControl.getContextPath()+tbControl.getContext().getPagePath(ControlResult.class);
		ajaxURL += "?readOnly=" + readOnly;
		
		btCancelar.setOnClick("ajaxUpdateDIV('cancelarControloAdd','main-form','"+ajaxURL+"');");
		botaoRegistoControloAdicao.add(btCancelar);
		PhyControlAdd.addField(botaoRegistoControloAdicao);
	}

	@Override
	protected void buildPage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void getFormData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onCancelar() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onGravar() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void setFormData() {
		// TODO Auto-generated method stub
		
	}
}
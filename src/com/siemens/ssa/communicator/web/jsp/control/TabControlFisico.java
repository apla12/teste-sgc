package com.siemens.ssa.communicator.web.jsp.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.click.Context;
import org.apache.click.control.AbstractControl;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.security.session.SessionManager;
import com.siemens.security.user.UserInfo;
import com.siemens.service.interfaces.ControloItemTipoServiceT;
import com.siemens.service.interfaces.DadosGeraisService;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItemTipo;
import com.siemens.ssa.communicator.pojo.interfaces.DadosGerais;
import com.siemens.ssa.communicator.pojo.interfaces.ItemEquipamento;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.ControlFieldContainer;
import pt.atos.web.click.controls.field.TabField;
import pt.atos.web.click.controls.field.W3Checkbox;
import pt.atos.web.click.controls.field.W3DateField;
import pt.atos.web.click.controls.field.W3Field;
import pt.atos.web.click.controls.field.W3HiddenField;
import pt.atos.web.click.controls.field.W3Label;
import pt.atos.web.click.controls.field.W3Radio;
import pt.atos.web.click.controls.field.W3RadioGroup;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.panels.Span;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabControlFisico {
	private static final long serialVersionUID = 821290961702149287L;
	
	protected CompleteFieldSetPanel headerPanel = new CompleteFieldSetPanel("control_phy_headerPanel", null, null);
	public DgitaTable tabelaResultadoAdicao = new DgitaTable("tabela_resultado_adicao");
	
	public Tab PhyControlPhy;
	
	//Datas
	protected W3DateField dt1;
	protected W3DateField dt2;
	private Controlo controlo;
	private W3Checkbox chkDeclarantePresente;
	
	//Equipamentos
	Span tituloEquipamentos;
	Span tituloEquipamentosItemsManuais;
	public DgitaTable tabelaEquipamentos = new DgitaTable("tabela_equipamentos");
	public DgitaTable tabelaEquipamentosItemsManuais = new DgitaTable("tabela_equipamentos_items_manuais");
	protected CompleteFieldSetPanel headerPanelEquipamento = new CompleteFieldSetPanel("headerPanelEquipamento", null, null);
	protected CompleteFieldSetPanel headerPanelEquipamentoItemsManuais = new CompleteFieldSetPanel("headerPanelEquipamentoManuais", null, null);
	private W3Button btAdicionarEquipamento 	= new W3Button("botaoAdicionarEquipamento","Adicionar");
	private W3Button btAdicionarEquipamentoItemsManuais 	= new W3Button("botaoAdicionarEquipamentoItemsManuais","Adicionar");
	private W3Button botaoFecharEquipamento 	= new W3Button("botaoFecharEquipamento","Fechar");
	private W3Button botaoFecharEquipamentoItemsManuais 	= new W3Button("botaoFecharEquipamentoItemsManuais","Fechar");
	private DIV divEquipamentos;
	private DIV divEquipamentosItemsManuais;
	private W3Select comboEquipamentos;
	private W3Select comboEquipamentosItemsManuais;
	W3Label titulo;
	
	DgitaLayoutPage dgitaLayout;
	
	public DgitaTable tabelaItemTipoManual = new DgitaTable("tabela_item_tipo_manual_equipamentos");

	public TabControlFisico(DgitaLayoutPage pg, boolean readOnly, DeclarationProcessor declarationProcessor, String idControlo) {
		dgitaLayout = pg;
		controlo = (Controlo) pg.getContext().getSession().getAttribute(SessionConstants.RES_CONTROLO+idControlo);
		
		boolean bloqueado = readOnly? readOnly : gereControloFisico(pg, new ControloItem(), readOnly, controlo);
		
		PhyControlPhy = new Tab("PhyControlPhy");
		
		W3Label inicioLabel = new W3Label();
		inicioLabel.setId("item_phy_inicioControlo_label");
		inicioLabel.setLabelShown(true);
		inicioLabel.setName(declarationProcessor.getWidgetLabel(inicioLabel.getId()));
		inicioLabel.setLabel(declarationProcessor.getWidgetLabel(inicioLabel.getId()));
		inicioLabel.setTooltip(declarationProcessor.getWidgetHelp(inicioLabel.getId()));
		
		W3Label fimLabel = new W3Label();
		fimLabel.setId("item_phy_fimControlo_label");
		fimLabel.setLabelShown(true);
		fimLabel.setName(declarationProcessor.getWidgetLabel(fimLabel.getId()));
		fimLabel.setLabel(declarationProcessor.getWidgetLabel(fimLabel.getId()));
		fimLabel.setTooltip(declarationProcessor.getWidgetHelp(fimLabel.getId()));
		
		dt1 = new W3DateField("item_phy_dt1");
		dt1.setLabelShown(false);
		dt1.setRenderTime(true);
		dt1.setRequired(false);

		dt2 = new W3DateField("item_phy_dt2");
		dt2.setLabelShown(false);
		dt2.setRenderTime(true);
		
		W3TextArea txt1 = new W3TextArea("item_phy_txt1_1","");
		txt1.setMaxLength(100);
		txt1.setCols(50);
		txt1.setRows(2);
		
		W3Radio rad1 = new W3Radio(SGCConstantes.FLAG_BD_VERDADEIRO);
		rad1.setId("item_phy_radio_1");
		rad1.setLabel(declarationProcessor.getWidgetLabel(rad1.getId()));
		
		W3Radio rad2 = new W3Radio(SGCConstantes.FLAG_BD_FALSO);
		rad2.setId("item_phy_radio_2");
		rad2.setLabel(declarationProcessor.getWidgetLabel(rad2.getId()));
		
		W3Select metodoPesagem = null;
		W3RadioGroup conf = null;
		
		if(SGCUtils.validaSistemaCAU(controlo.getSistema())) {				
			metodoPesagem = new W3Select("item_phy_radioGroup1_1", false);
			metodoPesagem.setLabelShown(false);
			metodoPesagem.setFocus(false);
			metodoPesagem.setOptionList(SGCConstantes.listaPesagem);
		} else {			
			conf = new W3RadioGroup("item_phy_radioGroup1_1");
			conf.setVerticalLayout(false);
			conf.add(rad1);
			conf.add(rad2);
			conf.setLabelShown(true);
			conf.setDisabled(false);
		}
		
		
		W3TextArea txt2 = new W3TextArea("item_phy_txt1_2","");
		txt2.setMaxLength(100);
		txt2.setCols(50);
		txt2.setRows(2);
		
		W3Radio rad3 = new W3Radio(SGCConstantes.CONTROLO_FISICO_VERIFICACAO_TOTAL);
		rad3.setId("item_phy_radio_3");
		rad3.setLabel(declarationProcessor.getWidgetLabel(rad3.getId()));
		W3Radio rad4 = new W3Radio(SGCConstantes.CONTROLO_FISICO_VERIFICACAO_PARCIAL);
		rad4.setId("item_phy_radio_4");
		rad4.setLabel(declarationProcessor.getWidgetLabel(rad4.getId()));
		W3RadioGroup conf2 = new W3RadioGroup("item_phy_radioGroup1_2", false);
		
		conf2.setVerticalLayout(false);
		conf2.add(rad3);
		conf2.add(rad4);
		conf2.setLabelShown(true);
		conf2.setDisabled(false);
		FieldSetLayout layout = new FieldSetLayout(6,new String[]{"0%","20%","0%","20%","0%","20%","0%","20%","0%","20%","0%","20%"});
		
		headerPanel.setFieldSetLayout(layout);
		
		headerPanel.setPage(pg);
		headerPanel.setForm(pg.form);
			
		W3Label verificacaoLabel = new W3Label();
		verificacaoLabel.setId("item_phy_verificacao_label");
		verificacaoLabel.setName(declarationProcessor.getWidgetLabel(verificacaoLabel.getId()));
		verificacaoLabel.setLabel(declarationProcessor.getWidgetLabel(verificacaoLabel.getId()));
		
		W3Label pesagemLabel = new W3Label();
		pesagemLabel.setId("item_phy_pesagem_label");
		pesagemLabel.setName(declarationProcessor.getWidgetLabel(pesagemLabel.getId()));
		pesagemLabel.setLabel(declarationProcessor.getWidgetLabel(pesagemLabel.getId()));
		
		chkDeclarantePresente = new W3Checkbox("control_add_chkDeclarantePresente");
		chkDeclarantePresente.setChecked(false);
		chkDeclarantePresente.setLabelShown(true);
		chkDeclarantePresente.setTooltip(declarationProcessor.getWidgetHelp(chkDeclarantePresente.getId()));
		
		headerPanel.setStyle("padding-top", "20px");
		if(declarationProcessor.getWidgetVisible(inicioLabel.getId())) headerPanel.add(inicioLabel, 2);
		if(declarationProcessor.getWidgetVisible(fimLabel.getId())) headerPanel.add(fimLabel, 2);
		headerPanel.add(new W3Label("spacer_label", ""), 2);
		if(declarationProcessor.getWidgetVisible(dt1.getId())) headerPanel.add(dt1,3);
		if(declarationProcessor.getWidgetVisible(dt2.getId())) headerPanel.add(dt2,3);
		if(declarationProcessor.getWidgetVisible(chkDeclarantePresente.getId())) headerPanel.add(chkDeclarantePresente,1);
		headerPanel.add(new W3Label("spacer_label", ""), 2);
		if(declarationProcessor.getWidgetVisible(verificacaoLabel.getId())) headerPanel.add(verificacaoLabel, 1);
		if(declarationProcessor.getWidgetVisible(conf2.getId())) headerPanel.add(conf2,1);
		if(declarationProcessor.getWidgetVisible(txt2.getId())) headerPanel.add(txt2,3);
		headerPanel.add(new W3Label("spacer_label", ""), 2);
		if(declarationProcessor.getWidgetVisible(pesagemLabel.getId())) headerPanel.add(pesagemLabel, 1);
		
		if(SGCUtils.validaSistemaCAU(controlo.getSistema())) {		
			if(declarationProcessor.getWidgetVisible(metodoPesagem.getId())) headerPanel.add(metodoPesagem,1);
		} else {
			if(declarationProcessor.getWidgetVisible(conf.getId())) headerPanel.add(conf,1);
		}
		if(declarationProcessor.getWidgetVisible(txt1.getId())) headerPanel.add(txt1,3);
		headerPanel.add(new W3Label("spacer_label", ""), 2);
		
		Controlo controlo = (Controlo) pg.getContext().getSession().getAttribute(SessionConstants.RES_CONTROLO+idControlo);
		
		//DADOS novos para CAU's
		if(SGCUtils.validaSistemaCAU(controlo.getSistema())) {	
			
			List<Option> options = new ArrayList<Option>();
			options.add(new Option("S","Sim"));
			options.add(new Option("N", "Não"));
			options.add(0, new Option("----"));
			
			W3Select capacidadeRecetaculo = new W3Select("item_capacidade_recetaculo", false);
			capacidadeRecetaculo.setLabel(declarationProcessor.getWidgetLabel(capacidadeRecetaculo.getId()));
			capacidadeRecetaculo.setLabelShown(true);
			capacidadeRecetaculo.setFocus(false);
			capacidadeRecetaculo.setOptionList(options);

			W3Select assistenciaCarga = new W3Select("item_assistencia_carga", false);
			assistenciaCarga.setLabel(declarationProcessor.getWidgetLabel(assistenciaCarga.getId()));
			assistenciaCarga.setLabelShown(true);
			assistenciaCarga.setFocus(false);
			assistenciaCarga.setOptionList(options);
			
			W3Select assistenciaDescarga = new W3Select("item_assistencia_descarga", false);
			assistenciaDescarga.setLabel(declarationProcessor.getWidgetLabel(assistenciaDescarga.getId()));
			assistenciaDescarga.setLabelShown(true);
			assistenciaDescarga.setFocus(false);
			assistenciaDescarga.setOptionList(options);
			
			W3Select contagem = new W3Select("id_contagem", false);
			contagem.setLabel(declarationProcessor.getWidgetLabel(contagem.getId()));
			contagem.setLabelShown(true);
			contagem.setFocus(false);
			contagem.setOptionList(SGCConstantes.listaContagem);
			
			W3Select estampilhasFiscais = new W3Select("item_estampilhas_fiscais", false);
			estampilhasFiscais.setLabel(declarationProcessor.getWidgetLabel(estampilhasFiscais.getId()));
			estampilhasFiscais.setLabelShown(true);
			estampilhasFiscais.setFocus(false);
			estampilhasFiscais.setOptionList(options);

			W3DateField dataInicioAmostras = new W3DateField("item_data_inicio_amostras");
			dataInicioAmostras.setLabel(declarationProcessor.getWidgetLabel(dataInicioAmostras.getId()));
			
			W3DateField dataFimAmostras = new W3DateField("item_data_fim_amostras");
			dataFimAmostras.setLabel(declarationProcessor.getWidgetLabel(dataFimAmostras.getId()));
			
			W3TextArea relatorioAmostras = new W3TextArea("item_relatorio_amostras");
			relatorioAmostras.setLabel(declarationProcessor.getWidgetLabel(relatorioAmostras.getId()));
			relatorioAmostras.setMaxLength(100);
			relatorioAmostras.setCols(50);
			relatorioAmostras.setRows(2);
			
			W3TextArea descricaoProcessoAmostras = new W3TextArea("item_descricao_processo_amostras");
			descricaoProcessoAmostras.setLabel(declarationProcessor.getWidgetLabel(descricaoProcessoAmostras.getId()));
			descricaoProcessoAmostras.setMaxLength(100);
			descricaoProcessoAmostras.setCols(50);
			descricaoProcessoAmostras.setRows(2);
			
			
			
			//Tabela de identificadores
			AnonymousCallColumn editar = new AnonymousCallColumn("editar");
			editar.setHeaderTitle(declarationProcessor.getWidgetLabel("column_equipamento_tabela_detalhe_item_tipo"));
			editar.setWidth("10%");
			editar.setCall(new AnonymousCall() {
				@Override
				public String getDataContent(Object row, Context ctx, int arg2) {
					ControloItemTipo item = (ControloItemTipo) row;
					if(item != null) {

						if(controlo.getTipoControlo().contentEquals(SGCConstantes.TIPO_CONTROLO_FISICO) && SGCConstantes.TIPOS_CONTROLOS_PERMITEM_EQUIPAMENTOS.contains(item.getTipoControlo())) {
							
							Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) pg.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + item.getChave().getNumeroControlo());
							
							List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(item.getChave().getDetItemControlo().toString());
							
							StringBuilder countEquipamentos = new StringBuilder("<span id='");
							countEquipamentos.append(item.getChave().getDetItemControlo());
							countEquipamentos.append("_countEquipamento'>");	
							if(listaDaAdicao != null) {								
								countEquipamentos.append(listaDaAdicao.size());
							}
					        countEquipamentos.append("</span>");       
							if (!bloqueado) {
								return "<a href=\"javascript:void(0);\" onclick=\"cadastroEquipamentos('" + item.getChave().getDetItemControlo() + "','" + item.getChave().getNumeroItem() +  "','" + item.getTipoControlo() + "','" + item.getIdentificadorSSA() + "'); return false;\" class=\"fa fa-pencil fa-lg\"></a>&nbsp;".concat(countEquipamentos.toString());
							} else {
								return "<a href=\"javascript:void(0);\" onclick=\"recuperaEquipamentos('" + item.getChave().getDetItemControlo() + "','" + item.getIdentificadorSSA() +"', true); return false;\" class=\"fa fa-pencil fa-lg\"></a>&nbsp;".concat(countEquipamentos.toString());
							}
						}	
					}
					return "";
				}
			});

			DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
			
			Column identificadorSSA = new Column("identificadorSSA");
			identificadorSSA.setWidth("10%");
			identificadorSSA.setRenderId(true);
			identificadorSSA.setHeaderTitle(declarationProcessor.getWidgetLabel("column_identificador_tabela_controlo_item"));

			Column areaRisco = new Column("areaRisco");
			areaRisco.setWidth("25%");
			areaRisco.setRenderId(true);
			areaRisco.setHeaderTitle(declarationProcessor.getWidgetLabel("column_area_risco_tabela_detalhe_item_tipo"));
			areaRisco.setDecorator(new Decorator() {	
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						if (item != null && item.getAreaRisco() != null){
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAreaRisco(), controlo.getSistema());
							return item.getAreaRisco() +" - "+ dg.getDescricao();		
						}
					} catch (ApplicationException e) {
					}
					return "";
				}
			});
			
			Column analiseRisco = new Column("analiseRisco");
			analiseRisco.setWidth("20%");
			analiseRisco.setRenderId(true);
			analiseRisco.setHeaderTitle(declarationProcessor.getWidgetLabel("column_codigo_identificacao_tabela_analise_risco"));
			analiseRisco.setDecorator(new Decorator() {	
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
					try {
						if (item != null && item.getAnaliseRisco() != null){
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAnaliseRisco(), controlo.getSistema());
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
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getTipoControlo(), controlo.getSistema());
							return item.getTipoControlo() +" - "+ dg.getDescricao();
						}
					} catch (ApplicationException e) {
					}
					return "";
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
			tabelaResultadoAdicao.addColumn(editar);
			
			// ########################## EQUIPAMENTOS ########################

			headerPanelEquipamento.setFieldSetLayout(new FieldSetLayout(1,new String[]{"50%","50%"}));
			headerPanelEquipamento.setPage(pg);
			headerPanelEquipamento.addStyleClass("equipamentosHeader");
			
			tituloEquipamentos = new Span("");
			tituloEquipamentos.setId("txt_titulo");
			tituloEquipamentos.setStyle("margin-top", "10px");
			
			btAdicionarEquipamento.setOnClick("adicionarEquipamento()");
			botaoFecharEquipamento.setOnClick("fecharEquipamentos()");
			
			DIV divBotaoFecharEquipamentos = new DIV();
			divBotaoFecharEquipamentos.setStyle("padding-bottom", "-6px");
			divBotaoFecharEquipamentos.setStyle("margin-left", "8px");
			divBotaoFecharEquipamentos.setStyle("display", "none");
			divBotaoFecharEquipamentos.setId("botaoFecharEquipamentoDiv");
			divBotaoFecharEquipamentos.add(botaoFecharEquipamento);
			
			W3HiddenField identificadorItemTipo = new W3HiddenField("identificadorItemTipo", String.class);
			identificadorItemTipo.setId("identificadorItemTipo");
			pg.getForm().add(identificadorItemTipo);
			
			W3HiddenField numeroItem = new W3HiddenField("numeroItem", String.class);
			numeroItem.setId("numeroItem");
			pg.getForm().add(numeroItem);
			
			W3HiddenField detItemControlo = new W3HiddenField("detItemControlo", String.class);
			detItemControlo.setId("detItemControlo");
			pg.getForm().add(detItemControlo);

			divEquipamentos = new DIV();
			divEquipamentos.setStyle("display", "none;");
			divEquipamentos.setStyle("margin-left", "10px");
			divEquipamentos.setId("divEquipamentos");
						
			comboEquipamentos = new W3Select("select_form_equipamentos");
			comboEquipamentos.setId("select_form_equipamentos");
			comboEquipamentos.setLabel(declarationProcessor.getWidgetLabel(comboEquipamentos.getId()));
			comboEquipamentos.setRequired(false);
			
			btAdicionarEquipamento.setId("btAdicionarEquipamento");
			btAdicionarEquipamento.setStyle("vertical-align","bottom");
			btAdicionarEquipamento.setStyle("margin-bottom", "0px");
			btAdicionarEquipamento.setStyle("margin-left", "8px");
			
			divEquipamentos.add(comboEquipamentos);
			divEquipamentos.add(btAdicionarEquipamento);
			
			Column identificador = new Column("column_identificador_tabela_item_equipamento");
			identificador.setWidth("20%");
			identificador.setRenderId(true);
			identificador.setHeaderTitle(declarationProcessor.getWidgetLabel(identificador.getId()));
			
			Column tipoEquipamento = new Column("column_tipo_equipamento_tabela_item_equipamento");
			tipoEquipamento.setWidth("75%");
			tipoEquipamento.setRenderId(true);
			tipoEquipamento.setHeaderTitle(declarationProcessor.getWidgetLabel(tipoEquipamento.getId()));
			
			AnonymousCallColumn acaoRemover = new AnonymousCallColumn("remover");
			acaoRemover.setHeaderTitle("");
			acaoRemover.setRenderId(true);
			acaoRemover.setWidth("5%");
			acaoRemover.setCall(new AnonymousCall() {
				
				@Override
				public String getDataContent(Object row, Context ctx, int index) {
					ItemEquipamento item = (ItemEquipamento) row;
					return "<a href=\"javascript:void(0);\" onclick=\"remover('" + item.getChave().getItemEquipamento() + "'); return false;\" class=\"fa fa-trash-o\"></a>&nbsp;";
				}
			});

			tabelaEquipamentos.setStyle("display", "none");
			tabelaEquipamentos.setStyle("margin-top", "10px");
			tabelaEquipamentos.addColumn(identificador);
			tabelaEquipamentos.addColumn(tipoEquipamento);
			tabelaEquipamentos.addColumn(acaoRemover);
			
			tabelaEquipamentos.setShowBanner(false);
			tabelaEquipamentos.setShowLabelResult(false);
			tabelaEquipamentos.setNoPopUp(true);
			
			headerPanelEquipamento.add(tituloEquipamentos, 1);
			headerPanelEquipamento.add(divEquipamentos, 1);
			headerPanelEquipamento.add(tabelaEquipamentos, 1);
			headerPanelEquipamento.add(divBotaoFecharEquipamentos, 1);
			
			// TABELA DE ITEM TIPO MANUAL
			String tituloIdentificadores = "<h3 style=\"margin-left:0px ; margin-top:0px\" class='panel-title-non-expandable'>Identificadores adicionais</h3>\r\n" + 
					"<div class='panel-heading' style='padding-top: 0px;margin-left:0px ; width: 99%'></div>";
			titulo = new W3Label("titulo", tituloIdentificadores);
			
			Column identificadorSSAManual = new Column("identificadorSSA");
			identificadorSSAManual.setWidth("10%");
			identificadorSSAManual.setRenderId(true);
			identificadorSSAManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_identificador_tabela_controlo_item"));

			Column areaRiscoManual = new Column("areaRisco");
			areaRiscoManual.setWidth("25%");
			areaRiscoManual.setRenderId(true);
			areaRiscoManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_area_risco_tabela_detalhe_item_tipo"));
			areaRiscoManual.setDecorator(new Decorator() {
				
				@Override
				public String render(Object row, Context context) {
					ControloItemTipo item = (ControloItemTipo) row;
//					try {
//						List<DadosGerais> dgs = dadosGeraisService.getPorValorSistema(item.getAreaRisco(), controlo.getSistema());
//						if(dgs != null && !dgs.isEmpty()) {							
//							return dgs.get(0).getChave().getValor() + " - " + dgs.get(0).getDescricao();
//						}
//					} catch (ApplicationException e) {
//						
//					}
//					return "";
					try {
						DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAreaRisco(), controlo.getSistema());
						return item.getAreaRisco() +" - "+ dg.getDescricao();		
					} catch (ApplicationException e) {
					}
					return "";
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
						DadosGerais dgs = dadosGeraisService.buscarPorCodigoSistema(item.getAnaliseRisco(), controlo.getSistema());
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
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getTipoControlo(), controlo.getSistema());
							return item.getTipoControlo() +" - "+ dg.getDescricao();
						}
					} catch (ApplicationException e) {
					}
					return "";
				}
			});
			
			AnonymousCallColumn resultadoControloManual = new AnonymousCallColumn("resultado");
			resultadoControloManual.setHeaderTitle(declarationProcessor.getWidgetLabel("column_equipamento_tabela_detalhe_item_tipo"));
			resultadoControloManual.setRenderId(true);
			resultadoControloManual.setWidth("10%");
			resultadoControloManual.setCall(new AnonymousCall() {
				@Override
				public String getDataContent(Object row, Context ctx, int arg2) {
					ControloItemTipo item = (ControloItemTipo) row;
					if(item != null) {

						if(controlo.getTipoControlo().contentEquals(SGCConstantes.TIPO_CONTROLO_FISICO) && SGCConstantes.TIPOS_CONTROLOS_PERMITEM_EQUIPAMENTOS.contains(item.getTipoControlo())) {
							
							Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) pg.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + item.getChave().getNumeroControlo());
							StringBuilder sb = new StringBuilder();
							sb.append(item.getChave().getDetItemControlo().toString());
							
							List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(sb.toString());
							
							StringBuilder countEquipamentos = new StringBuilder("<span id='");
							countEquipamentos.append(item.getChave().getDetItemControlo());
							countEquipamentos.append("_countEquipamentoItemManual'>");	
							if(listaDaAdicao != null) {								
								countEquipamentos.append(listaDaAdicao.size());
							}
					        countEquipamentos.append("</span>");       
							if (!bloqueado) {
								return "<a href=\"javascript:void(0);\" onclick=\"cadastroEquipamentosItemManual('" + item.getChave().getDetItemControlo() + "','" + item.getChave().getNumeroItem() +  "','" + item.getTipoControlo() + "','" + item.getIdentificadorSSA() + "'); return false;\" class=\"fa fa-pencil fa-lg\"></a>&nbsp;".concat(countEquipamentos.toString());
							} else {
								return "<a href=\"javascript:void(0);\" onclick=\"recuperaEquipamentosItemsManuais('" + item.getChave().getDetItemControlo() + "','" + item.getIdentificadorSSA() +"', true); return false;\" class=\"fa fa-pencil fa-lg\"></a>&nbsp;".concat(countEquipamentos.toString());
							}
						}	
					}
					return "";
				}
			});
			
			tabelaItemTipoManual.addColumn(identificadorSSAManual);
			tabelaItemTipoManual.addColumn(areaRiscoManual);
			tabelaItemTipoManual.addColumn(analiseRiscoManual);
			tabelaItemTipoManual.addColumn(tipoAdicaoManual);
			tabelaItemTipoManual.addColumn(resultadoControloManual);
			tabelaItemTipoManual.setStyle("style", "none;");
			tabelaItemTipoManual.setStyle("margin-top", "20px;");
			tabelaItemTipoManual.setNoPopUp(true);
			tabelaItemTipoManual.setShowBanner(false);
			tabelaItemTipoManual.setShowLabelResult(false);
			
			
			// ########################## EQUIPAMENTOS ITEMS MANUAIS ########################

			headerPanelEquipamentoItemsManuais.setFieldSetLayout(new FieldSetLayout(1,new String[]{"50%","50%"}));
			headerPanelEquipamentoItemsManuais.setPage(pg);
			headerPanelEquipamentoItemsManuais.addStyleClass("equipamentosHeader");
			
			tituloEquipamentosItemsManuais = new Span("");
			tituloEquipamentosItemsManuais.setId("txt_tituloItemsManuais");
			tituloEquipamentosItemsManuais.setStyle("margin-top", "10px");
			
			btAdicionarEquipamentoItemsManuais.setOnClick("adicionarEquipamentoItemsManuais()");
			botaoFecharEquipamentoItemsManuais.setOnClick("fecharEquipamentosItemsManuais()");
			
			DIV divBotaoFecharEquipamentosItemsManuais = new DIV();
			divBotaoFecharEquipamentosItemsManuais.setStyle("padding-bottom", "-6px");
			divBotaoFecharEquipamentosItemsManuais.setStyle("margin-left", "8px");
			divBotaoFecharEquipamentosItemsManuais.setStyle("display", "none");
			divBotaoFecharEquipamentosItemsManuais.setId("botaoFecharEquipamentoItemsManuaisDiv");
			divBotaoFecharEquipamentosItemsManuais.add(botaoFecharEquipamentoItemsManuais);
			
			W3HiddenField identificadorItemTipoItemsManuais = new W3HiddenField("identificadorItemTipoItemManuais", String.class);
			identificadorItemTipoItemsManuais.setId("identificadorItemTipoItemManuais");
			pg.getForm().add(identificadorItemTipoItemsManuais);
			
			
			W3HiddenField detItemControloItemsManuais = new W3HiddenField("detItemControloItemManual", String.class);
			detItemControloItemsManuais.setId("detItemControloItemManual");
			pg.getForm().add(detItemControloItemsManuais);

			divEquipamentosItemsManuais = new DIV();
			divEquipamentosItemsManuais.setStyle("display", "none;");
			divEquipamentosItemsManuais.setStyle("margin-left", "10px");
			divEquipamentosItemsManuais.setId("divEquipamentosItemsManuais");
						
			comboEquipamentosItemsManuais = new W3Select("select_form_equipamentos_items_manuais");
			comboEquipamentosItemsManuais.setId("select_form_equipamentos_items_manuais");
			comboEquipamentosItemsManuais.setLabel(declarationProcessor.getWidgetLabel(comboEquipamentos.getId()));
			comboEquipamentosItemsManuais.setRequired(false);
			
			btAdicionarEquipamentoItemsManuais.setId("btAdicionarEquipamentoItemManual");
			btAdicionarEquipamentoItemsManuais.setStyle("vertical-align","bottom");
			btAdicionarEquipamentoItemsManuais.setStyle("margin-bottom", "0px");
			btAdicionarEquipamentoItemsManuais.setStyle("margin-left", "8px");
			
			divEquipamentosItemsManuais.add(comboEquipamentosItemsManuais);
			divEquipamentosItemsManuais.add(btAdicionarEquipamentoItemsManuais);
			
			Column identificadorItemManual = new Column("column_identificador_tabela_item_equipamento");
			identificadorItemManual.setWidth("20%");
			identificadorItemManual.setRenderId(true);
			identificadorItemManual.setHeaderTitle(declarationProcessor.getWidgetLabel(identificador.getId()));
			
			Column tipoEquipamentoItemManual = new Column("column_tipo_equipamento_tabela_item_equipamento");
			tipoEquipamentoItemManual.setWidth("75%");
			tipoEquipamentoItemManual.setRenderId(true);
			tipoEquipamentoItemManual.setHeaderTitle(declarationProcessor.getWidgetLabel(tipoEquipamento.getId()));
			
			AnonymousCallColumn acaoRemoverItemManual = new AnonymousCallColumn("removerItemManual");
			acaoRemoverItemManual.setHeaderTitle("");
			acaoRemoverItemManual.setRenderId(true);
			acaoRemoverItemManual.setWidth("5%");
			acaoRemoverItemManual.setCall(new AnonymousCall() {
				
				@Override
				public String getDataContent(Object row, Context ctx, int index) {
					ItemEquipamento item = (ItemEquipamento) row;
					return "<a href=\"javascript:void(0);\" onclick=\"removerEquipamentoManual('" + item.getChave().getItemEquipamento() + "'); return false;\" class=\"fa fa-trash-o\"></a>&nbsp;";
				}
			});

			tabelaEquipamentosItemsManuais.setStyle("display", "none");
			tabelaEquipamentosItemsManuais.setStyle("margin-top", "10px");
			tabelaEquipamentosItemsManuais.addColumn(identificador);
			tabelaEquipamentosItemsManuais.addColumn(tipoEquipamento);
			tabelaEquipamentosItemsManuais.addColumn(acaoRemover);
			
			tabelaEquipamentosItemsManuais.setShowBanner(false);
			tabelaEquipamentosItemsManuais.setShowLabelResult(false);
			tabelaEquipamentosItemsManuais.setNoPopUp(true);
			
			headerPanelEquipamentoItemsManuais.add(tituloEquipamentosItemsManuais, 1);
			headerPanelEquipamentoItemsManuais.add(divEquipamentosItemsManuais, 1);
			headerPanelEquipamentoItemsManuais.add(tabelaEquipamentosItemsManuais, 1);
			headerPanelEquipamentoItemsManuais.add(divBotaoFecharEquipamentosItemsManuais, 1);
			
			//SE o campo nao for DATE, CHECKBOX e LABEL coloque ele nesta lista para que o ajuda seja configurado.
			List tooltipFields = Arrays.asList(capacidadeRecetaculo, 
					assistenciaCarga, 
					assistenciaDescarga, 
					contagem, 
					estampilhasFiscais, 
					relatorioAmostras, 
					descricaoProcessoAmostras);
			
			tooltipFields.forEach(item -> { 
					String id = ((AbstractControl)item).getId();
					((W3Field)item).setTooltip(declarationProcessor.getWidgetHelp(id));
			});
			
			if(declarationProcessor.getWidgetVisible(capacidadeRecetaculo.getId()))headerPanel.add(capacidadeRecetaculo, 1);
			if(declarationProcessor.getWidgetVisible(assistenciaCarga.getId()))headerPanel.add(assistenciaCarga, 1);
			if(declarationProcessor.getWidgetVisible(assistenciaDescarga.getId()))headerPanel.add(assistenciaDescarga, 1);
			if(declarationProcessor.getWidgetVisible(contagem.getId()))headerPanel.add(contagem, 1);
			if(declarationProcessor.getWidgetVisible(estampilhasFiscais.getId()))headerPanel.add(estampilhasFiscais, 2);
			if(declarationProcessor.getWidgetVisible(dataInicioAmostras.getId()))headerPanel.add(dataInicioAmostras, 1);
			if(declarationProcessor.getWidgetVisible(dataFimAmostras.getId()))headerPanel.add(dataFimAmostras, 5);
			headerPanel.add(new W3Label("spacer_label", ""), 6);
			if(declarationProcessor.getWidgetVisible(relatorioAmostras.getId()))headerPanel.add(relatorioAmostras, 3);
			if(declarationProcessor.getWidgetVisible(descricaoProcessoAmostras.getId()))headerPanel.add(descricaoProcessoAmostras, 3);
			headerPanel.add(tabelaResultadoAdicao, 6);
			headerPanel.add(headerPanelEquipamento, 6);
			headerPanel.add(titulo, 6);
			headerPanel.add(tabelaItemTipoManual, 6);
			headerPanel.add(headerPanelEquipamentoItemsManuais, 6);
			
			
			
		}
		
		int[] flds = {1};
		PhyControlPhy.setNumberFieldsPerLine(flds);

		PhyControlPhy.addField(headerPanel);

		PhyControlPhy.setWidth("100%");
		
		_setReadonly(readOnly);
	}

	/**
	 * Função para fazer colocar no forumlário os dados
	 * passados como parâmetro
	 * 
	 * @param adi_ - pojo do controlo da declaração para o preenchimento
	 * @param form - formulário da página passado por parâmetro
	 */
	public void setFormulario(ControloItem adi_, Form form){
		if(adi_!=null && adi_.getInicioControloFisico()!=null)
			((W3DateField)headerPanel.getField("item_phy_dt1")).setValue(adi_.getInicioControloFisico());
		else
			headerPanel.getField("item_phy_dt1").setValue(null);
		
		if(adi_!=null && adi_.getFimControloFisico()!=null)
			((W3DateField)headerPanel.getField("item_phy_dt2")).setValue(adi_.getFimControloFisico());
		else
			headerPanel.getField("item_phy_dt2").setValue(null);
		if(adi_ != null && adi_.getIndicadorOperadorPresente() != null) {			
			if(SGCConstantes.FLAG_BD_VERDADEIRO.equalsIgnoreCase(("" + adi_.getIndicadorOperadorPresente()).trim())){
				((W3Checkbox)headerPanel.getField("control_add_chkDeclarantePresente")).setChecked(true);
			} else {
				((W3Checkbox)headerPanel.getField("control_add_chkDeclarantePresente")).setChecked(false);
			}
		}
		
		if(adi_!=null && adi_.getVerificacao()!=null)
			headerPanel.getField("item_phy_txt1_2").setValue(adi_.getVerificacao());
		else
			headerPanel.getField("item_phy_txt1_2").setValue(null);
			
		if(adi_!=null && adi_.getPesagem()!=null)
			headerPanel.getField("item_phy_txt1_1").setValue(adi_.getPesagem());
		else
			headerPanel.getField("item_phy_txt1_1").setValue(null);
		
		if(adi_!=null && adi_.getTipoVerificacao()!=null)
			((W3RadioGroup)headerPanel.getField("item_phy_radioGroup1_2")).setValue(adi_.getTipoVerificacao());
		else{
			((W3RadioGroup)headerPanel.getField("item_phy_radioGroup1_2")).setValue(null);		
		}
		
		if(adi_!=null && adi_.getTipoPesagem()!=null)
			headerPanel.getField("item_phy_radioGroup1_1").setValue(adi_.getTipoPesagem());
		else
			headerPanel.getField("item_phy_radioGroup1_1").setValue(null);
		
		if( SGCUtils.validaSistemaCAU(controlo.getSistema()) && adi_ != null ) {
			headerPanel.getField("item_capacidade_recetaculo").setValue(adi_.getCapacidadeRecetaculo());
			headerPanel.getField("item_assistencia_carga").setValue(adi_.getAssistCarga());
			headerPanel.getField("item_assistencia_descarga").setValue(adi_.getAssistDescarga());
			headerPanel.getField("id_contagem").setValue(adi_.getMetodoContagemControlo());
			headerPanel.getField("item_estampilhas_fiscais").setValue(adi_.getEstampilhasFiscais());
			((W3DateField)headerPanel.getField("item_data_fim_amostras")).setValue(adi_.getDataInicioAmostra());
			((W3DateField)headerPanel.getField("item_data_inicio_amostras")).setValue(adi_.getDataFimAmostra());
			headerPanel.getField("item_relatorio_amostras").setValue(adi_.getRelatorioAmostra());
			headerPanel.getField("item_descricao_processo_amostras").setValue(adi_.getExtracaoAmostra());
		}
		
		if (adi_ != null) {
			ControloItemTipoServiceT controloItemService = EJBUtil.getSessionInterface(ControloItemTipoServiceT.class);
			List<ControloItemTipo> listItemTipo = null;
			Map<String, List> mapControloItemTipo = null;
			
			try {
				if(dgitaLayout.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + controlo.getChave().getNumeroControlo()) != null) {
					mapControloItemTipo = (Map) dgitaLayout.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + controlo.getChave().getNumeroControlo());
					listItemTipo = mapControloItemTipo.get(controlo.getChave().getNumeroControlo() +""+adi_.getChave().getNumeroItem());
					if(listItemTipo == null) {
						listItemTipo = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo());
					}
				} else {
					mapControloItemTipo = new HashMap<String, List>();
					listItemTipo = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo());					
				}
				mapControloItemTipo.put(controlo.getChave().getNumeroControlo() +""+adi_.getNumAdicao(), listItemTipo);
				
				tabelaResultadoAdicao.setRowList(listItemTipo);
				
				dgitaLayout.getContext().getSession().setAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + controlo.getChave().getNumeroControlo(), mapControloItemTipo);
				SessionManager.getInstance().setSessao(dgitaLayout.getContext().getSession());
			} catch (ApplicationException e) {
				
			}
			//CONTROLO ITENS TIPO MANUAL

			List<ControloItemTipo> listItemTipoManual = null;
			Map<String, List> mapControloItemTipoManual = null;
			
			try {
				if(dgitaLayout.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + controlo.getChave().getNumeroControlo()) != null) {
					mapControloItemTipoManual = (Map) dgitaLayout.getContext().getSession().getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + controlo.getChave().getNumeroControlo());
					listItemTipoManual = mapControloItemTipoManual.get(controlo.getChave().getNumeroControlo()+""+adi_.getNumAdicao());
					if(listItemTipoManual == null) {
						listItemTipoManual = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo());
					}
				} else {
					mapControloItemTipoManual = new HashMap<String, List>();
					listItemTipoManual = controloItemService.getListaItemTipo(adi_.getNumAdicao(), adi_.getChave().getNumeroControlo());					
				}
				mapControloItemTipoManual.put(controlo.getChave().getNumeroControlo()+""+adi_.getNumAdicao(), listItemTipoManual);

				List<ControloItemTipo> listItemTipoManualFiltrada = listItemTipoManual.stream().filter((s) -> SGCConstantes.TIPOS_CONTROLOS_PERMITEM_EQUIPAMENTOS.contains(s.getTipoControlo())).collect(Collectors.toList());
				
				tabelaItemTipoManual.setRowList(listItemTipoManualFiltrada);
				
				dgitaLayout.getContext().getSession().setAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + controlo.getChave().getNumeroControlo(), mapControloItemTipoManual);
				SessionManager.getInstance().setSessao(dgitaLayout.getContext().getSession());
			} catch (ApplicationException e) {
				
			}
		}
		
		
		
	}

	/**
	 * 
	 * Método que preenche o POJO com os dados correspondentes no FORM da página
	 * 
	 * */ 
	public ControloItem getFormulario(ControloItem adi_, Form form){
		
		if (adi_ == null)
			adi_ = new ControloItem();
		
	
		String inicioControloFisicoAno = (String)form.getPage().getContext().getRequestParameter("item_phy_dt1.ano");
		String inicioControloFisicoDia = (String)form.getPage().getContext().getRequestParameter("item_phy_dt1.dia");
		String inicioControloFisicoMes = (String)form.getPage().getContext().getRequestParameter("item_phy_dt1.mes");
		String inicioControloFisicoHora = (String)form.getPage().getContext().getRequestParameter("item_phy_dt1.hora");
		String inicioControloFisicoMint = (String)form.getPage().getContext().getRequestParameter("item_phy_dt1.minuto");

		
		String fimControloFisicoAno = (String)form.getPage().getContext().getRequestParameter("item_phy_dt2.ano");
		String fimControloFisicoDia = (String)form.getPage().getContext().getRequestParameter("item_phy_dt2.dia");
		String fimControloFisicoMes = (String)form.getPage().getContext().getRequestParameter("item_phy_dt2.mes");
		String fimControloFisicoHora = (String)form.getPage().getContext().getRequestParameter("item_phy_dt2.hora");
		String fimControloFisicoMint = (String)form.getPage().getContext().getRequestParameter("item_phy_dt2.minuto");
		
		Timestamp inicioControloFisico = null;
		if(StringUtils.isNotBlank(inicioControloFisicoAno)
				&& StringUtils.isNotBlank(inicioControloFisicoDia)
				&& StringUtils.isNotBlank(inicioControloFisicoMes)
				&& StringUtils.isNotBlank(inicioControloFisicoHora)
				&& StringUtils.isNotBlank(inicioControloFisicoMint)){
			
			int ano= new Integer(inicioControloFisicoAno).intValue();
			int mes= new Integer(inicioControloFisicoMes).intValue();
			int dia= new Integer(inicioControloFisicoDia).intValue();
			int hora= new Integer(inicioControloFisicoHora).intValue();
			int mint= new Integer(inicioControloFisicoMint).intValue();
	
			Calendar cl = Calendar.getInstance();
			cl.set(ano, mes-1, dia, hora, mint);
			inicioControloFisico=new Timestamp(cl.getTimeInMillis());
		}
		adi_.setInicioControloFisico(inicioControloFisico);
		
		Timestamp fimControloFisico = null;
		if(StringUtils.isNotBlank(fimControloFisicoAno)
				&& StringUtils.isNotBlank(fimControloFisicoDia)
				&& StringUtils.isNotBlank(fimControloFisicoMes)
				&& StringUtils.isNotBlank(fimControloFisicoHora)
				&& StringUtils.isNotBlank(fimControloFisicoMint)){
			
			int anoFim= new Integer(fimControloFisicoAno).intValue();
			int mesFim= new Integer(fimControloFisicoMes).intValue();
			int diaFim= new Integer(fimControloFisicoDia).intValue();
			int horaFim= new Integer(fimControloFisicoHora).intValue();
			int mintFim= new Integer(fimControloFisicoMint).intValue();
	
			Calendar c2 = Calendar.getInstance();
			c2.set(anoFim, mesFim-1, diaFim, horaFim, mintFim);
			fimControloFisico=new Timestamp(c2.getTimeInMillis());
		}
		adi_.setFimControloFisico(fimControloFisico);
		
		String indOprPresente = (String)form.getPage().getContext().getRequestParameter("control_add_chkDeclarantePresente");
		if(StringUtils.isNotBlank(indOprPresente)
				&& "on".equalsIgnoreCase(indOprPresente)){
			
			adi_.setIndicadorOperadorPresente(SGCConstantes.FLAG_BD_VERDADEIRO);	
		}
		else{
			adi_.setIndicadorOperadorPresente(SGCConstantes.FLAG_BD_FALSO);	
		}
		
		adi_.setVerificacao((String)form.getPage().getContext().getRequestParameter("item_phy_txt1_2"));
		adi_.setPesagem((String)form.getPage().getContext().getRequestParameter("item_phy_txt1_1"));
		adi_.setTipoVerificacao((String)form.getFieldValue("item_phy_radioGroup1_2"));
		if(StringUtils.isNotBlank(adi_.getTipoVerificacao())){
			if(SGCConstantes.CONTROLO_FISICO_VERIFICACAO_TOTAL.equals(adi_.getTipoVerificacao())){
				adi_.setDescTipoVerificacao(SGCConstantes.CONTROLO_FISICO_DESCRICAO_VERIFICACAO_TOTAL);
			}
			else{
				adi_.setDescTipoVerificacao(SGCConstantes.CONTROLO_FISICO_DESCRICAO_VERIFICACAO_PARCIAL);
			}
		}
		adi_.setTipoPesagem((String)form.getFieldValue("item_phy_radioGroup1_1"));
		if(StringUtils.isNotBlank(adi_.getTipoPesagem())){
			if(SGCConstantes.FLAG_BD_VERDADEIRO.equals(adi_.getTipoVerificacao())){
				adi_.setDescTipoPesagem(SGCConstantes.CONTROLO_FISICO_DESCRICAO_PESAGEM_VERDADEIRA);
			}
			else{
				adi_.setDescTipoPesagem(SGCConstantes.CONTROLO_FISICO_DESCRICAO_PESAGEM_FALSA);
			}
		}				
		
		if(SGCUtils.validaSistemaCAU(controlo.getSistema())) {
			String dataInicioAmostraAno = (String)form.getPage().getContext().getRequestParameter("item_data_inicio_amostras.ano");
			String dataInicioAmostraDia = (String)form.getPage().getContext().getRequestParameter("item_data_inicio_amostras.dia");
			String dataInicioAmostraMes = (String)form.getPage().getContext().getRequestParameter("item_data_inicio_amostras.mes");
			
			Timestamp dataInicioAmostra = null;
			if(StringUtils.isNotBlank(dataInicioAmostraAno)
					&& StringUtils.isNotBlank(dataInicioAmostraDia)
					&& StringUtils.isNotBlank(dataInicioAmostraMes)){
				
				int anoFim= new Integer(dataInicioAmostraAno).intValue();
				int mesFim= new Integer(dataInicioAmostraMes).intValue();
				int diaFim= new Integer(dataInicioAmostraDia).intValue();
		
				Calendar c2 = Calendar.getInstance();
				c2.set(anoFim, mesFim-1, diaFim);
				dataInicioAmostra=new Timestamp(c2.getTimeInMillis());
			}
			adi_.setDataInicioAmostra(dataInicioAmostra);
			
			String dataFimAmostraAno = (String)form.getPage().getContext().getRequestParameter("item_data_fim_amostras.ano");
			String dataFimAmostraDia = (String)form.getPage().getContext().getRequestParameter("item_data_fim_amostras.dia");
			String dataFimAmostraMes = (String)form.getPage().getContext().getRequestParameter("item_data_fim_amostras.mes");
			
			Timestamp dataFimAmostra = null;
			if(StringUtils.isNotBlank(dataFimAmostraAno)
					&& StringUtils.isNotBlank(dataFimAmostraDia)
					&& StringUtils.isNotBlank(dataFimAmostraMes)){
				
				int anoFim= new Integer(dataFimAmostraAno).intValue();
				int mesFim= new Integer(dataFimAmostraMes).intValue();
				int diaFim= new Integer(dataFimAmostraDia).intValue();
		
				Calendar c2 = Calendar.getInstance();
				c2.set(anoFim, mesFim-1, diaFim);
				dataFimAmostra=new Timestamp(c2.getTimeInMillis());
			}
			adi_.setDataFimAmostra(dataFimAmostra);

			String capacidadeRecetaculo = form.getFieldValue("item_capacidade_recetaculo");
			if(!capacidadeRecetaculo.contentEquals("----")) {				
				adi_.setCapacidadeRecetaculo(capacidadeRecetaculo);
			}
			
			String itemAssistenciaCarga = form.getFieldValue("item_assistencia_carga");
			if(!itemAssistenciaCarga.contentEquals("----")) {				
				adi_.setAssistCarga(itemAssistenciaCarga);
			}
			
			String itemAssistenciaDescarga = form.getFieldValue("item_assistencia_descarga");
			if(!itemAssistenciaDescarga.contentEquals("----")) {				
				adi_.setAssistDescarga(itemAssistenciaDescarga);
			}
			
			String estampilhasFiscais = form.getFieldValue("item_estampilhas_fiscais");
			if(!estampilhasFiscais.contentEquals("----")) {				
				adi_.setEstampilhasFiscais(estampilhasFiscais);
			}
			
			String idContagem = form.getFieldValue("id_contagem");
			if(!idContagem.contentEquals("---")) {				
				adi_.setMetodoContagemControlo(idContagem);
			}

			adi_.setRelatorioAmostra(form.getFieldValue("item_relatorio_amostras"));
			adi_.setExtracaoAmostra(form.getFieldValue("item_descricao_processo_amostras"));
			
		}
		
		return adi_;
	}



	public void setReadonly(boolean readOnly) {
		
		_setReadonly(readOnly);
		
	}
	
	public void _setReadonly(boolean readOnly) {
		for (TabField x : PhyControlPhy.getTabFields()) {
			if (x.tab_fld != null){
				x.tab_fld.setReadonly(readOnly);	
				
				//remover o required dos campos da TAB
				if(readOnly == true){
					for (Object field : x.tab_fld.getControls()) {
						if (field instanceof Field)
							((Field)field).setRequired(false);
						if (field instanceof ControlFieldContainer) {
							ControlFieldContainer cont = (ControlFieldContainer) field;
							for (Object fieldCont : cont.getControls()) {
								if(fieldCont instanceof Span)
									continue;
								((Field)fieldCont).setReadonly(true);
								((Field)fieldCont).setRequired(false);
							}
						}
						if (field instanceof CompleteFieldSetPanel) {
							CompleteFieldSetPanel cont = (CompleteFieldSetPanel) field;
							for (Object fieldCont : cont.getControls()) {
								
								if (field instanceof ControlFieldContainer) {
									ControlFieldContainer contComp = (ControlFieldContainer) field;
									for (Object fieldContComp : contComp.getControls()) {
										if(fieldContComp instanceof Span)
											continue;
										((Field)fieldContComp).setReadonly(true);
										((Field)fieldContComp).setRequired(false);
									}
								}
								
								if(fieldCont instanceof Span || fieldCont instanceof DgitaTable || fieldCont instanceof DIV)
									continue;
								((Field)fieldCont).setReadonly(true);
								((Field)fieldCont).setRequired(false);
							}
						}
					}
				}
			}
			if(x.tab_table!=null && x.tab_table instanceof DgitaTable)
				((DgitaTable)x.tab_table).setReadOnly(readOnly);

		}
	}
	
	public boolean gereControloFisico(DgitaLayoutPage tbControl, ControloItem ctrlAdicao, boolean readOnly,Controlo ctrlDec){
		boolean readonlyRetorno = true;
		HttpSession session = tbControl.getContext().getSession();
		UserInfo user = (UserInfo) session.getAttribute(SessionConstants.USER);
			
		if(SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_SPA_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_POSTERIORI_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_SIMTEMM_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO_SIMTEMM_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO.equals(ctrlDec.getTipoControlo().toString())
				|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlDec.getTipoControlo().toString())){
			
			if(ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador())){
				readonlyRetorno = false;
			} else {
				if (user.getUserId().equalsIgnoreCase(ctrlDec.getVerificador())){
					readonlyRetorno = false;
				}
			}
		}
		return readonlyRetorno;
	}	
}
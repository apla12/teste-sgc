package com.siemens.ssa.communicator.web.jsp.control.caus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.click.Context;
import org.apache.click.control.Checkbox;
import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.DadosGeraisService;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloMatriz;
import com.siemens.ssa.communicator.pojo.interfaces.DadosGerais;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;
import com.siemens.ssa.communicator.web.jsp.control.ControlResult;
import com.siemens.ssa.communicator.web.jsp.control.TabControlAdicao;
import com.siemens.ssa.communicator.web.jsp.control.TabControlFisico;
import com.siemens.ssa.communicator.web.jsp.control.TabGeralCabecalhoGeneric;
import com.siemens.ssa.communicator.web.jsp.control.TabItemIrregularidades;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.dao.ChaveDescricao;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.web.click.controls.field.W3Checkbox;
import pt.atos.web.click.controls.field.W3DateField;
import pt.atos.web.click.controls.field.W3Label;
import pt.atos.web.click.controls.field.W3ReadOnlyField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.controls.links.AjaxTableActionLink;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.ErrorDIV;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabGeralCabecalhoCAUS extends TabGeralCabecalhoGeneric {

	private static final long serialVersionUID = 1L;

	public TabGeralCabecalhoCAUS(DgitaLayoutPage tbControl, boolean readOnly, String idControlo,
								 DeclarationProcessor declarationProcessor, String momento) {

		chaveControloAtribuido = "control_dau_comboControloAtribuido_cau";

		ControlDAU = new Tab("DadosCabecalho");
		// Header Panel
		headerPanel.setForm(tbControl.form);
		headerPanel.setFieldSetLayout(
				new FieldSetLayout(4, new String[] { "0%", "25%", "0%", "25%", "0%", "25%", "0%", "25%" }));

		headerPanel.setPage(tbControl);
		// headerPanel.setForm(tbControl.form);
		headerPanel.setParent(tbControl);
		headerPanel.setAttribute("onload", "audprevia();");

		W3ReadOnlyField conferente = new W3ReadOnlyField("conferente");
		conferente.setLabelShown(true);
		W3ReadOnlyField verificador = new W3ReadOnlyField("verificador");
		verificador.setLabelShown(true);

		// Controlo atribuído
		W3Select comboControloAtribuido = new W3Select(chaveControloAtribuido, false);
		comboControloAtribuido.setLabel(declarationProcessor.getWidgetLabel(comboControloAtribuido.getId()));
		comboControloAtribuido.setLabelShown(true);
		comboControloAtribuido.setReadonly(true);
		comboControloAtribuido.setFocus(false);
		comboControloAtribuido.setTooltip(declarationProcessor.getWidgetHelp(comboControloAtribuido.getId()));

		// AnaliseRisco
		W3Select comboAnaliseRisco = new W3Select("control_dau_comboAnaliseRisco", false);
		comboAnaliseRisco.setLabelShown(true);
		comboAnaliseRisco.setReadonly(true);
		comboAnaliseRisco.setFocus(false);

		// Resultado Amostra
		W3Select comboResultadoAmostra = new W3Select("control_dau_comboResultadoAmostra", false);
		comboResultadoAmostra.setLabelShown(true);
		comboResultadoAmostra.setReadonly(true);
		comboResultadoAmostra.setFocus(false);

		// Datas
		W3DateField dataInicioControloFisico = new W3DateField("item_cabecalho_caus_inicioControlo_label");
		dataInicioControloFisico.setLabelShown(true);
		dataInicioControloFisico.setLabel(declarationProcessor.getWidgetLabel(dataInicioControloFisico.getId()));
		dataInicioControloFisico.setStyle("font-weight", "normal");
		dataInicioControloFisico.setStyle("weight", "normal");
		dataInicioControloFisico.setDisabled(true);
		dataInicioControloFisico.setRenderTime(true);
		dataInicioControloFisico.setTooltip(declarationProcessor.getWidgetHelp(dataInicioControloFisico.getId()));

		W3DateField dataFimControloFisico = new W3DateField("item_cabecalho_caus_fimControlo_label");
		dataFimControloFisico.setLabel(declarationProcessor.getWidgetLabel(dataFimControloFisico.getId()));
		dataFimControloFisico.setLabelShown(true);
		dataFimControloFisico.setStyle("font-weight", "normal");
		dataFimControloFisico.setStyle("weight", "normal");
		dataFimControloFisico.setDisabled(true);
		dataFimControloFisico.setRenderTime(true);
		dataFimControloFisico.setTooltip(declarationProcessor.getWidgetHelp(dataFimControloFisico.getId()));

		// Resultado Controlo
		W3Select comboResultadoControlo = new W3Select("control_dau_comboResultadoControlo", false);
		comboResultadoControlo.setLabel(declarationProcessor.getWidgetLabel(comboResultadoControlo.getId()));
		comboResultadoControlo.setLabelShown(true);
		comboResultadoControlo.setRequired(true);
		//comboResultadoControlo.setAttribute("onchange", "desactivaIrreg(this); desativaConseqIrr(this);");
		comboResultadoControlo.setFocus(false);
		comboResultadoControlo.setTooltip(declarationProcessor.getWidgetHelp(comboResultadoControlo.getId()));

		// Requer dados adicionais
		W3TextArea requerDadosAdd = new W3TextArea("control_dau_txtDadosAdicionais", false);
		requerDadosAdd.setLabel(declarationProcessor.getWidgetLabel(requerDadosAdd.getId()));
		requerDadosAdd.setLabelShown(true);
		requerDadosAdd.setFocus(false);
		requerDadosAdd.setMaxLength(2000);
		requerDadosAdd.setCols(95);
		requerDadosAdd.setRows(2);
		requerDadosAdd.setTooltip(declarationProcessor.getWidgetHelp(requerDadosAdd.getId()));

		// identificador
		W3TextField identificadorAdd = new W3TextField("control_dau_txtIdentificador", false);
		identificadorAdd.setSize(30);
		identificadorAdd.setMaxLength(30);
		identificadorAdd.setLabelShown(true);

		// pir
		W3TextField pirAdd = new W3TextField("control_dau_txtPir", false);
		pirAdd.setSize(30);
		pirAdd.setMaxLength(30);
		pirAdd.setLabelShown(true);

		// motivo Controlo
		// W3TextArea motivoControloAdd = new
		// W3TextArea("control_dau_txtMotivoControlo", false);
		// motivoControloAdd.setLabelShown(true);
		// motivoControloAdd.setFocus(false);
		// motivoControloAdd.setMaxLength(200);
		// motivoControloAdd.setCols(95);
		// motivoControloAdd.setRows(2);

		List<Option> options = new ArrayList<Option>();
		try {
			DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
			List<DadosGerais> dadosGerais = dadosGeraisService.getPorValorSistema(
					SGCConstantes.CHAVE_DOMINIO_COMBO_CL027, declarationProcessor.ctrl.getSistema());
			for (DadosGerais item : dadosGerais) {
				Option op = new Option(item.getCodigo(), item.getDescricao());
				options.add(op);
			}
		} catch (ApplicationException e) {
			log.error("Erro ao consultar os valores para a code list: " + SGCConstantes.CHAVE_DOMINIO_COMBO_CL027
					+ " Sistema: " + declarationProcessor.ctrl.getSistema());
		}

		// RelatorioAmostra
		W3Select relatorioAmostraAdd = new W3Select("control_dau_txtPendenteResultadoAmostra", false);
		relatorioAmostraAdd.setLabel(declarationProcessor.getWidgetLabel(relatorioAmostraAdd.getId()));
		relatorioAmostraAdd.setLabelShown(true);
		relatorioAmostraAdd.setFocus(false);
		relatorioAmostraAdd.setOptionList(options);

		// Extraccao Amostra
		// W3TextArea extracaoAmostraAdd = new
		// W3TextArea("control_dau_txtExtracaoAmostra", false);
		// extracaoAmostraAdd.setLabelShown(true);
		// extracaoAmostraAdd.setFocus(false);
		// extracaoAmostraAdd.setMaxLength(50);
		// extracaoAmostraAdd.setCols(95);
		// extracaoAmostraAdd.setRows(2);

		// Extraccao Amostra
		W3TextArea textoAdd = new W3TextArea("control_dau_txtTexto", false);
		textoAdd.setLabelShown(true);
		textoAdd.setFocus(false);
		textoAdd.setMaxLength(50);
		textoAdd.setCols(95);
		textoAdd.setRows(2);

		// Relatório do Resultado do Controlo
		W3TextArea relatResultControlo = new W3TextArea("control_dau_txtResultadoControlo", false);
		relatResultControlo.setLabel(declarationProcessor.getWidgetLabel(relatResultControlo.getId()));
		relatResultControlo.setLabelShown(true);
		relatResultControlo.setFocus(false);
		relatResultControlo.setMaxLength(2000);
		relatResultControlo.setCols(95);
		relatResultControlo.setRows(2);
		relatResultControlo.setTooltip(declarationProcessor.getWidgetHelp(relatResultControlo.getId()));

		W3TextArea observacoes = new W3TextArea("control_dau_txtObservacoes", false);
		observacoes.setLabel(declarationProcessor.getWidgetLabel(observacoes.getId()));
		observacoes.setLabelShown(true);
		observacoes.setFocus(false);
		observacoes.setMaxLength(2000);
		observacoes.setCols(95);
		observacoes.setRows(2);
		observacoes.setReadonly(true);
		observacoes.setTooltip(declarationProcessor.getWidgetHelp(observacoes.getId()));

		DIV divSaidaNaoAutorizada = new DIV();
		divSaidaNaoAutorizada.setStyle("display", "flex;");
		divSaidaNaoAutorizada.setStyle("width", "400px;");

		W3Label saidaNaoAutorizadaLabel = new W3Label("control_dau_saida_nao_autorizada", "control_dau_saida_nao_autorizada");
		saidaNaoAutorizadaLabel.setLabel(declarationProcessor.getWidgetLabel(saidaNaoAutorizadaLabel.getId()));

		Checkbox saidaNAutorizada = new Checkbox("control_dau_saida_nao_autorizada_check", "control_dau_saida_nao_autorizada_check");
		saidaNAutorizada.setStyle("width", "50px");
		saidaNAutorizada.setReadonly(true);

		if(declarationProcessor.ctrl != null && declarationProcessor.ctrl.getSaidaNaoAutorizada() != null) {
			if(declarationProcessor.ctrl.getSaidaNaoAutorizada().equals("V")) {
				saidaNAutorizada.setAttribute("checked", "true");
			}
		}

		saidaNAutorizada.setDisabled(true);

		divSaidaNaoAutorizada.add(saidaNAutorizada);
		divSaidaNaoAutorizada.add(saidaNaoAutorizadaLabel);

		Column col1 = new Column("descNumAdicao");
		col1.setWidth("30%");
		col1.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_adicao_num_adicao")); //"Adição"

		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);

		AnonymousCallColumn col2 = new AnonymousCallColumn("descTipoControlo");
		col2.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_adicao_controlo_atribuido")); //"Controlo Atribuído"
		col2.setWidth("20%");
		col2.setCall(new AnonymousCall() {

			@Override
			public String getDataContent(Object object, Context context, int arg2) {
				if(object != null) {
					ControloItem item = (ControloItem) object;
					try {
						if(item.getTipoControlo() != null && declarationProcessor.ctrl.getSistema() != null){
							DadosGerais dadosGerais = dadosGeraisService.buscarPorCodigoSistema(item.getTipoControlo(), declarationProcessor.ctrl.getSistema());
							return dadosGerais != null ? dadosGerais.getDescricao() : "";
						}
					} catch (ApplicationException e) {
						log.error("Erro ao tentar recuperar a descrição para o tipo controlo: " + item.getTipoControlo() + " para o Sistema: " + declarationProcessor.ctrl.getSistema() );
					}
				}
				return "";
			}
		});

		Column col3 = new Column("numDocumentos");
		col3.setWidth("20%");
		col3.setTextAlign("center");
		col3.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_adicao_numero_documentos")); //"Número de Documentos"

		AnonymousCallColumn col4 = new AnonymousCallColumn("descResultadoControlo");
		col4.setWidth("30%");
		col4.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_adicao_resultado_controlo")); //"Resultado do Controlo"

		col4.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				String content = "";
				ControloItem item = (ControloItem) row;
				if (item != null) {
					try {
						if(item.getResultadoControlo() != null) {
							DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getResultadoControlo(), declarationProcessor.ctrl.getSistema());
							if(dg != null) {
								content = dg.getCodigo() + " - " + dg.getDescricao();
							}
						}
					} catch (ApplicationException e) {
						log.error("Erro ao tentar recuperar a descrição do resultado do controlo: " + item.getTipoControlo() + " para o Sistema: " + declarationProcessor.ctrl.getSistema() );
					}
				}
				return content;
			}
		});

		grupoTabsAdicao = new TabGroup("tabGroupPop");
		grupoTabsAdicao.setParent(this);
		grupoTabsAdicao.setPageToAll(tbControl);
		grupoTabsAdicao.setForm(tbControl.form);
		grupoTabsAdicao.setExistsTabbedForm(true);

		HttpSession session = getSession();
		ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session
				.getAttribute(SessionConstants.CONTROLO_MATRIZ);

		String Sistema = null;

		if (property.equals("false")) {
			siiafEnable = false;
		}

		if (matriz != null) {

			Sistema = matriz.get(0).getChave().getSistema();

			for (int i = 0; i < matriz.size(); i++) {
				if (matriz.get(i).getCodSeparador().equals("ITEM_RESULT")) {
					ContAdd = new TabControlAdicao(tbControl, new ControloItem(), readOnly, Sistema, idControlo,
							declarationProcessor);
					grupoTabsAdicao.setTab(ContAdd.PhyControlAdd);
					ContAdd.PhyControlAdd.setLegend(matriz.get(i).getSeparador());
					carregaItems = true;
				} else if (matriz.get(i).getCodSeparador().equals("ITEM_FIS")) {
					ContPhy = new TabControlFisico(tbControl, readOnly, declarationProcessor, idControlo);
					grupoTabsAdicao.setTab(ContPhy.PhyControlPhy);
					ContPhy.PhyControlPhy.setLegend(matriz.get(i).getSeparador());
				} else if (matriz.get(i).getCodSeparador().equals("ITEM_IRR")) {
					itemIrreg = new TabItemIrregularidades(tbControl, readOnly);
					grupoTabsAdicao.setTab(itemIrreg.itemIrreg);
					itemIrreg.itemIrreg.setLegend(matriz.get(i).getSeparador());
				}
//				else if (matriz.get(i).getCodSeparador().equals("ITEM_CONSQ_FIN") && siiafEnable){
//					itemConseqFinanc = new TabItemConseqFinanc(tbControl, readOnly);
//					grupoTabsAdicao.setTab(itemConseqFinanc.itemConseqFinanc);
//					itemConseqFinanc.itemConseqFinanc.setLegend(matriz.get(i).getSeparador());
//				}
//				else if (matriz.get(i).getCodSeparador().equals("ITEM_IRR_MERC") && siiafEnable){
//					itemIrregMerc = new TabItemMercadoriaIrr(tbControl, readOnly);
//					grupoTabsAdicao.setTab(itemIrregMerc.itemIrregMercador);
//					itemIrregMerc.itemIrregMercador.setLegend(matriz.get(i).getSeparador());
//				}
			}
		} else {
			log.info("NOOOOOOmatrizTabGeralCab");
		}

		log.info("Items: " + carregaItems);

		if (carregaItems) {
			// Adicionar a tabela dos resultados de pesquisa
			tabelaAdicoes.addColumn(col1);
			tabelaAdicoes.addColumn(col2);
			tabelaAdicoes.addColumn(col3);
			tabelaAdicoes.addColumn(col4);

			tabelaAdicoes.setNoPopUp(true);
		}

		if (declarationProcessor.getWidgetVisible(conferente.getId()))
			headerPanel.add(conferente, 3);
		if (declarationProcessor.getWidgetVisible(verificador.getId()))
			headerPanel.add(verificador, 1);
		if (declarationProcessor.getWidgetVisible(comboControloAtribuido.getId()))
			headerPanel.add(comboControloAtribuido, 3);
		if (declarationProcessor.getWidgetVisible(dataInicioControloFisico.getId()))
			headerPanel.add(dataInicioControloFisico, 1);
		// headerPanel.add(comboAnaliseRisco,3);
		if (declarationProcessor.getWidgetVisible(comboResultadoControlo.getId()))
			headerPanel.add(comboResultadoControlo, 3);
		// headerPanel.add(comboResultadoAmostra,3);
		// headerPanel.add(identificadorAdd,1);
		// headerPanel.add(pirAdd,1);
		if (declarationProcessor.getWidgetVisible(dataFimControloFisico.getId()))
			headerPanel.add(dataFimControloFisico, 1);
		if (declarationProcessor.getWidgetVisible(requerDadosAdd.getId()))
			headerPanel.add(requerDadosAdd, 3);
		if (declarationProcessor.getWidgetVisible(relatorioAmostraAdd.getId()))
			headerPanel.add(relatorioAmostraAdd, 3);
		// headerPanel.add(motivoControloAdd,3);
		// headerPanel.add(extracaoAmostraAdd,3);
		if (declarationProcessor.getWidgetVisible(relatResultControlo.getId()))
			headerPanel.add(relatResultControlo, 3);
		if (declarationProcessor.getWidgetVisible(saidaNAutorizada.getId()))
			headerPanel.add(divSaidaNaoAutorizada, 2);

		// headerPanel.add(textoAdd,3);
		// if(declarationProcessor.getWidgetVisible(observacoes.getId()))
		// headerPanel.add(observacoes, 4);

		int[] flds = { 1, 1 };
		ControlDAU.setNumberFieldsPerLine(flds);
		ControlDAU.setForm(tbControl.form);
		ControlDAU.addField(headerPanel, "100%");
		ControlDAU.setWidth("100%");

		if (carregaItems) {

			headerPanel.add(tabelaAdicoes, 4);

			// set hide do grupo
			containerTabAdicao = new ExpandableFieldSetPanel("container_grupoAdicao", "");
			containerTabAdicao.setForm(tbControl.form);
			containerTabAdicao.setParent(this);
			containerTabAdicao.setLegend("Controlo da Adição nº.");

			errorDv = new ErrorDIV("errorsDiv-ctrAdicao");
			errorDv.setStyle("display", "none");
			errorDv.setStyle("width", "100%");
			containerTabAdicao.add(errorDv, 2);
			containerTabAdicao.setPadLeftFirstColumn(false);
			containerTabAdicao.add(grupoTabsAdicao, 4);
			containerTabAdicao.setStyle("display", "none");

			ControlDAU.addField(containerTabAdicao);

		}
	}

	@Override
	public void onRender(DgitaLayoutPage tbControl, boolean readOnly, String idControlo) {
		log.info("#onRenderTo:" + idControlo);

		// vai carregar a informacao dos separadores
		HttpSession session = getSession();
		ControloItem ctrlAdicao = (ControloItem) session.getAttribute(SessionConstants.RES_CONTROLO_ADICAO);
		Controlo ctrl = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
		String motivoControl = null;

		if (ctrlAdicao != null) {
			log.info("Show Adicao On Render");
			showAdicao(tbControl, ctrlAdicao, readOnly, idControlo);
			motivoControl = ctrlAdicao.getMotivoControlo();
		}

		if (carregaItems) {

			String ajaxURL = tbControl.getContextPath() + tbControl.getContext().getPagePath(ControlResult.class);
			ajaxURL += "?readOnly=" + readOnly;

			if (!readOnly) {

				AjaxTableActionLink lnk = new AjaxTableActionLink("Tratar", ajaxURL, "tratar_adicao", "main-form");
				tabelaAdicoes.setEditLink(lnk);
			} else {
				headerPanel.setReadonly(readOnly);

				AjaxTableActionLink lnk = new AjaxTableActionLink("Consultar", ajaxURL, "tratar_adicao", "main-form");
				tabelaAdicoes.setDetailLink(lnk);
			}

			ContAdd.onRender(tbControl, readOnly, false, motivoControl);
		}

		ArrayList<Option> optionList = new ArrayList<Option>();

		ArrayList<ChaveDescricao> res = new ArrayList<ChaveDescricao>();

		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);

		if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_DAIN)
				|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU)
				|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_TRACAU)
				|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST)
				|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_NR)
				|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {

			try {
				List<DadosGerais> dadosGerais = dadosGeraisService
						.getPorValorSistema(SistemasCLsEnum.obterCodigoPeloNome(ctrl.getSistema()), ctrl.getSistema());
				for (DadosGerais item : dadosGerais) {
					ChaveDescricao cd = new ChaveDescricao();
					cd.setChaveFields(new Object[] { item.getCodigo() });
					cd.setCodigo(item.getCodigo());
					cd.setDescricao(item.getCodigo() + " - " +item.getDescricao());
					res.add(cd);
				}
			} catch (ApplicationException e) {

			}

		} else {
			res = null;
		}

		for (int i = 0; i < res.size(); i++) {
			if (i == 0 && res.size() > 1)
				optionList.add(new Option("", "---"));
			optionList.add(new Option(res.get(i).getPK(), res.get(i).getValue()));
		}
		if (optionList.size() == ((short) 1)) {
			headerPanel.getField("control_dau_comboResultadoControlo").setValue(optionList.get(0).getValue());
			headerPanel.getField("control_dau_comboResultadoControlo").setReadonly(true);
		}
		((W3Select) headerPanel.getField("control_dau_comboResultadoControlo")).setOptionList(optionList);

		//Vai buscar os dados para CL716 para popular a combo de controlo atribuido
		// No Caso dos outros sistema esses dados são obtidos por meio e preenchimento automático configurado no arquivo ControllResult_pt.properties


		try {
			ArrayList<Option> resControloAtribuido = new ArrayList<Option>();
			List<DadosGerais> dadosGerais = dadosGeraisService
					.getPorValorSistema(SGCConstantes.CHAVE_DOMINIO_COMBO_CL716, ctrl.getSistema());
			for (DadosGerais item : dadosGerais) {
				Option cd = new Option(item.getCodigo(), item.getDescricao());
				resControloAtribuido.add(cd);
			}

			((W3Select) headerPanel.getField(chaveControloAtribuido)).setOptionList(resControloAtribuido);

		} catch (ApplicationException e) {

		}
	}

	@Override
	public void setFormulario(Controlo dec_, Form form){
		headerPanel.getField("conferente").setValue(dec_.getConferente());
		headerPanel.getField("verificador").setValue(dec_.getVerificador());

		if(headerPanel.getField(chaveControloAtribuido)!=null
				&& StringUtils.isBlank(headerPanel.getField(chaveControloAtribuido).getValue())
				&& dec_.getTipoControlo()!=null){
			headerPanel.getField(chaveControloAtribuido).setValue(dec_.getTipoControlo());
		}

		boolean controleData = gereControloFisico(this, null, readOnly, dec_);

		headerPanel.getField("item_cabecalho_caus_inicioControlo_label").setDisabled(controleData);
		headerPanel.getField("item_cabecalho_caus_fimControlo_label").setDisabled(controleData);
		headerPanel.getField("control_dau_txtPendenteResultadoAmostra").setDisabled(controleData);

		if(headerPanel.getField("control_dau_comboResultadoControlo")!=null){
//			if ((dec_.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO) || (dec_.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_SPA_COMBO)) && dec_.getSistema().equals(SGCConstantes.SISTEMA_SFA))){
			if(dec_.getTipoControlo() != null) {
				if ((dec_.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO) && dec_.getSistema().equals(SGCConstantes.SISTEMA_SFA))){
//				headerPanel.getField("control_dau_comboResultadoControlo").setValue(props.getMainProperty("ssa.resultado.controlo.b1"));
//				headerPanel.getField("control_dau_comboResultadoControlo").setDisabled(true);
				} else {
					if(dec_.getResultadoControlo() != null){
						headerPanel.getField("control_dau_comboResultadoControlo").setValue(dec_.getResultadoControlo().toString());
					}
				}
			}

		}
		if(dec_.getInicioCtrlFisi() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			headerPanel.getField("item_cabecalho_caus_inicioControlo_label").setValue(sdf.format(dec_.getInicioCtrlFisi()));
		}
		if(dec_.getFimCtrlFisi() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			headerPanel.getField("item_cabecalho_caus_fimControlo_label").setValue(sdf.format(dec_.getFimCtrlFisi()));
		}

		if(dec_.getPendenteAmostra() != null) {
			headerPanel.getField("control_dau_txtPendenteResultadoAmostra").setValue(dec_.getPendenteAmostra());
		}

		if(headerPanel.getField("control_dau_txtDadosAdicionais")!=null
				&& StringUtils.isBlank(headerPanel.getField("control_dau_txtDadosAdicionais").getValue())){

			headerPanel.getField("control_dau_txtDadosAdicionais").setValue(dec_.getRequerDadosOperador());
		}
		if(headerPanel.getField("control_dau_txtResultadoControlo")!=null
				&& StringUtils.isBlank(headerPanel.getField("control_dau_txtResultadoControlo").getValue())){

			headerPanel.getField("control_dau_txtResultadoControlo").setValue(dec_.getMotivoControlo());
		}
		if(dec_.getSistema().equals(SGCConstantes.SISTEMA_SFA)) {
			if(dec_.getInfoSSA()!=null)
				headerPanel.getField("control_dau_txtObservacoes").setValue(dec_.getInfoSSA().substring(13));
		}

		if(dec_.getListaControloItem()!=null){
			ArrayList<ControloItem> listaControloItem = null;
			listaControloItem = (ArrayList<ControloItem>) dec_.getListaControloItem().clone();
			if(	dec_.getSistema().equals(SGCConstantes.SISTEMA_DAIN) ||
					dec_.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU)||
					dec_.getSistema().equals(SGCConstantes.SISTEMA_TRACAU) ||
					dec_.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST) ||
					dec_.getSistema().equals(SGCConstantes.SISTEMA_NR) ||
					dec_.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {
				listaControloItem.remove(0);
			}
			tabelaAdicoes.setRowList(listaControloItem);
		}


	}

}
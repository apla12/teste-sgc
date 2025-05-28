package com.siemens.ssa.communicator.web.jsp.control;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloMatriz;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.dao.ChaveDescricao;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.web.click.controls.field.W3ReadOnlyField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.controls.panels.ErrorDIV;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabGeralCabecalho extends TabGeralCabecalhoGeneric {
	
	private static final long serialVersionUID = 6338987938544575968L;

	public TabGeralCabecalho(DgitaLayoutPage tbControl, boolean readOnly, String idControlo, DeclarationProcessor declarationProcessor,String momento) {
		
		ControlDAU = new Tab("DadosCabecalho");
		chaveControloAtribuido = "control_dau_comboControloAtribuido";
		//Header Panel
		headerPanel.setForm(tbControl.form);
		headerPanel.setFieldSetLayout(new FieldSetLayout(2, new String[]{"15%","35%","15%","35%"}));
		
		headerPanel.setPage(tbControl);
		//headerPanel.setForm(tbControl.form);
		headerPanel.setParent(tbControl);
		headerPanel.setAttribute("onload", "audprevia();");
	
		W3ReadOnlyField conferente = new W3ReadOnlyField("conferente");
		conferente.setLabelShown(true);
		W3ReadOnlyField verificador = new W3ReadOnlyField("verificador");
		verificador.setLabelShown(true);
		
		
		//Controlo atribuído	
		W3Select comboControloAtribuido = new W3Select("control_dau_comboControloAtribuido", false);
		comboControloAtribuido.setLabel(declarationProcessor.getWidgetLabel(comboControloAtribuido.getId()));
		comboControloAtribuido.setLabelShown(true);
		comboControloAtribuido.setReadonly(true);
		comboControloAtribuido.setFocus(false);
		comboControloAtribuido.setTooltip(declarationProcessor.getWidgetHelp(comboControloAtribuido.getId()));
		
		//Resultado Controlo
		W3Select comboResultadoControlo = new W3Select("control_dau_comboResultadoControlo", false);
		comboResultadoControlo.setLabel(declarationProcessor.getWidgetLabel(comboResultadoControlo.getId()));
		comboResultadoControlo.setLabelShown(true);
		comboResultadoControlo.setRequired(true);
		comboResultadoControlo.setAttribute("onchange", "reseleccaoCabecalho(this);");
		//comboResultadoControlo.setAttribute("onchange", "desactivaIrreg(this); desativaConseqIrr(this);");
		comboResultadoControlo.setFocus(false);
		comboResultadoControlo.setTooltip(declarationProcessor.getWidgetHelp(comboResultadoControlo.getId()));
		
		//Dropbox reseleção
		W3Select comboReselecao = new W3Select("control_dau_comboReselecao", false);
		comboReselecao.setLabel(declarationProcessor.getWidgetLabel(comboReselecao.getId()));
		comboReselecao.setLabelShown(true);
		comboReselecao.setFocus(false);
		comboReselecao.setTooltip(declarationProcessor.getWidgetHelp(comboReselecao.getId()));
		comboReselecao.setDisabled(true);

		ArrayList<Option> optionList = new ArrayList<Option>();
		optionList.add(new Option("","---"));
		optionList.add(new Option("CF","Controlo Fisico"));
		comboReselecao.setOptionList(optionList);
		
		if (optionList.size()==((short)1)){
			comboReselecao.setValue(optionList.get(0).getValue());
			comboReselecao.setReadonly(true);
		} else {
			comboReselecao.setReadonly(false);
		}
		
		//Requer dados adicionais
		W3TextArea requerDadosAdd = new W3TextArea("control_dau_txtDadosAdicionais", false);
		requerDadosAdd.setLabel(declarationProcessor.getWidgetLabel(requerDadosAdd.getId()));
		requerDadosAdd.setLabelShown(true);
		requerDadosAdd.setFocus(false);
		requerDadosAdd.setMaxLength(2000);
		requerDadosAdd.setCols(95);
		requerDadosAdd.setRows(2);
		requerDadosAdd.setTooltip(declarationProcessor.getWidgetHelp(requerDadosAdd.getId()));
	
		//Relatório do Resultado do Controlo
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
	
		Column col1 = new Column("descNumAdicao");
		col1.setWidth("30%");
		col1.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_adicao_num_adicao")); //"Adição"
		
		TabelasApoioServiceT tabelaApoioServiceT = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		Controlo ctrl = (Controlo) getContext().getSession().getAttribute(SessionConstants.RES_CONTROLO + idControlo);
		
		AnonymousCallColumn col2 = new AnonymousCallColumn("descTipoControlo");
		col2.setHeaderTitle(declarationProcessor.getWidgetLabel("control_dau_tabela_adicao_controlo_atribuido")); //"Controlo Atribuído"
		col2.setWidth("20%");
		col2.setCall(new AnonymousCall() {
			
			@Override
			public String getDataContent(Object object, Context context, int arg2) {
				if(object != null) {
					ControloItem item = (ControloItem) object;
					return tabelaApoioServiceT.getChaveDescricaoTipoControlo(ctrl.getSistema(), item.getTipoControlo(), "", false, "", false).get(0).getDescricao();
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
				String content= "";
				if(((ControloItem)row).getResultadoControlo()!=null){
					TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
					ArrayList<String> list = new ArrayList<String>();
					list.add(((ControloItem)row).getResultadoControlo());
					ArrayList<ChaveDescricao> res = srv.getDescricaoResultadoControloPorCodigo(list,false,true );
					
					if(res!=null && res.size()>0){
						content = res.get(0).getDescricao();
					}
					
					if(((ControloItem)row).getResultadoControlo()!=null 
							&& SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(((ControloItem)row).getResultadoControlo().toString())){
						res = srv.getChaveDescricaoTipoControlo(null, ((ControloItem)row).getTipoControloReSelecao(),null,new Boolean(false),SGCConstantes.getIdMomento(momento),new Boolean(true));
						if(res!=null && res.size()>0){
							content+=" ("+StringUtils.trim(res.get(0).getCodigo())+")";
						}
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
		ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session.getAttribute(SessionConstants.CONTROLO_MATRIZ);
		
		String Sistema = null;
		
		if (property.equals("false")){
			siiafEnable = false;
		}
		
		if (matriz != null){
			
			Sistema = matriz.get(0).getChave().getSistema();
			
			for (int i=0; i < matriz.size(); i++){		 
				if(matriz.get(i).getCodSeparador().equals("ITEM_RESULT")){
					ContAdd    = new TabControlAdicao(tbControl, new ControloItem(), readOnly, Sistema, idControlo, declarationProcessor);
					grupoTabsAdicao.setTab(ContAdd.PhyControlAdd);
					ContAdd.PhyControlAdd.setLegend(matriz.get(i).getSeparador());
					carregaItems=true;
				}
				else if (matriz.get(i).getCodSeparador().equals("ITEM_FIS")){
					ContPhy = new TabControlFisico(tbControl, readOnly, declarationProcessor, idControlo);
					grupoTabsAdicao.setTab(ContPhy.PhyControlPhy);
					ContPhy.PhyControlPhy.setLegend(matriz.get(i).getSeparador());
				}
				else if (matriz.get(i).getCodSeparador().equals("ITEM_IRR")){
					itemIrreg  = new TabItemIrregularidades(tbControl, readOnly);
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
		}
		else{
			log.info("NOOOOOOmatrizTabGeralCab");		
		}
		
		log.info("Items: "+ carregaItems);	
		
		if(carregaItems){
			//Adicionar a tabela dos resultados de pesquisa
		    tabelaAdicoes.addColumn(col1);
		    tabelaAdicoes.addColumn(col2);
		    tabelaAdicoes.addColumn(col3);
		    tabelaAdicoes.addColumn(col4);

		    tabelaAdicoes.setNoPopUp(true);
		}		
	    
		if(declarationProcessor.getWidgetVisible(conferente.getId())) headerPanel.add(conferente, 1);
		if(declarationProcessor.getWidgetVisible(verificador.getId())) headerPanel.add(verificador, 1);
		if(declarationProcessor.getWidgetVisible(comboControloAtribuido.getId())) headerPanel.add(comboControloAtribuido, 4);
		if(declarationProcessor.getWidgetVisible(comboResultadoControlo.getId())) headerPanel.add(comboResultadoControlo, 1);
		if(declarationProcessor.getWidgetVisible(comboReselecao.getId())) headerPanel.add(comboReselecao, 1);
		if(declarationProcessor.getWidgetVisible(requerDadosAdd.getId())) headerPanel.add(requerDadosAdd, 4);
		if(declarationProcessor.getWidgetVisible(observacoes.getId())) headerPanel.add(observacoes, 4);
		if(declarationProcessor.getWidgetVisible(relatResultControlo.getId())) headerPanel.add(relatResultControlo, 4);
	    
		int[] flds = {1,1};
		ControlDAU.setNumberFieldsPerLine(flds);
		ControlDAU.setForm(tbControl.form);
		ControlDAU.addField(headerPanel,"100%");
		ControlDAU.setWidth("100%");
		
		
		if(carregaItems){
			
			headerPanel.add(tabelaAdicoes,4);
			
			//set hide do grupo
			containerTabAdicao = new ExpandableFieldSetPanel("container_grupoAdicao","");
			containerTabAdicao.setForm(tbControl.form);
			containerTabAdicao.setParent(this);
			containerTabAdicao.setLegend("Controlo da Adição nº.");
			
			
			errorDv = new ErrorDIV("errorsDiv-ctrAdicao"); 
			errorDv.setStyle("display", "none");
			errorDv.setStyle("width", "100%");
			containerTabAdicao.add(errorDv,2);
			containerTabAdicao.setPadLeftFirstColumn(false);
			containerTabAdicao.add(grupoTabsAdicao,4);
			containerTabAdicao.setStyle("display", "none");
			
			ControlDAU.addField(containerTabAdicao);
			
			

			
		}
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
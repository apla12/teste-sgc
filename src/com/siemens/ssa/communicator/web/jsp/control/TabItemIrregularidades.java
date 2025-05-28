package com.siemens.ssa.communicator.web.jsp.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.OptionGroup;
import org.apache.commons.lang.StringUtils;

import pt.atos.util.cache.CacheResultados;
import pt.atos.util.date.DateUtil;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.logging.Log;
import pt.atos.util.string.ToStringUtils;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.ControloConseqIrr;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidade;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidadePK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ListagemIrregularidades;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.sgccomunicator.utils.SGCUtils;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.ButtonPanel;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.page.DgitaLayoutPage;

/**
 * Separador para o controlo documental para cada adição
 * 
 *
 */
public class TabItemIrregularidades extends DgitaLayoutPage{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralIrregularidades.class);
	
	public W3Select possivelItemTable   = new W3Select("item_irr_table1");
	public W3Select detectadasItemTable = new W3Select("item_irr_table2");
	W3Select indicadorRisco = new W3Select("control_irr_ind_risc", "Indicador Risco", false, null);
	W3Select normaViolada = new W3Select("control_irr_nv", "Norma Violada");
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("item_irr_mainPanel", "");
	protected ExpandableFieldSetPanel headerPanel = new ExpandableFieldSetPanel("item_irr_headerPanel", "");
	protected ExpandableFieldSetPanel secondPanel = new ExpandableFieldSetPanel("item_irr_secondPanel", "");
	
	public boolean displayAjax = false;
	public boolean displayTabIrreg = false;
	public boolean displayButtons = true;
	
	SGCProperties props = new SGCProperties();
	Boolean siiafEnable = true;
	String property = props.getMainProperty("siiaf.enable");
	
	/**
	 * Separador de Irregularidades
	 */
	public Tab itemIrreg;
		
	@SuppressWarnings("unchecked")
	public TabItemIrregularidades(DgitaLayoutPage tbControl, boolean readOnly) {
			
		itemIrreg = new Tab("ItemIrr");
	
		//Tabela de Irregularidades Possiveis		
		possivelItemTable.setMultiple(false);
		possivelItemTable.setSize(700);
		possivelItemTable.setWidth(WebConstants.POPUP_INNER_TABLE_SIZE);
		possivelItemTable.setStyle("height", "120px");
		possivelItemTable.setDisabled(false);
		
		HashMap<String, Object[]> map = null;
		//HttpSession session = getSession();
		TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		ArrayList<ListagemIrregularidades> listagem = srv.getListaIrreg();
		OptionGroup opcoesPossiv = new OptionGroup(null);
		
		for(int i=0; i<listagem.size(); i++){
			opcoesPossiv.add(new Option(listagem.get(i).getValue(),listagem.get(i).getDescricao()));
		}		
		possivelItemTable.add(opcoesPossiv);
		
		detectadasItemTable.setSize(700);
		detectadasItemTable.setMultiple(true);
		detectadasItemTable.setTitle("Irregularidades Detectadas");
		detectadasItemTable.setWidth(WebConstants.POPUP_INNER_TABLE_SIZE);
		detectadasItemTable.setStyle("height", "120px");
		detectadasItemTable.setDisabled(false);
//		
		if(detectadasItemTable.getOptionList()!=null && detectadasItemTable.getOptionList().isEmpty())
			detectadasItemTable.add(new OptionGroup(null));	
//		if(valoresDetectIrr.isEmpty()){
//log.info("onRender..valoresDetectIrr.isEmpty()");
//			detectadasTable.add(new OptionGroup(null));			
//		}
//		else{
//log.info("onRender..valoresDetectIrr:"+valoresDetectIrr.size());			
//			OptionGroup opcoesDetect = new OptionGroup(null);
//			
//			for(int i=0; i<valoresDetectIrr.size(); i++){
//				opcoesDetect.add(new Option(valoresDetectIrr.get(i).getValue(),valoresDetectIrr.get(i).getDescricao()));
//			}		
//			detectadasTable.add(opcoesDetect);
//		}

		//Botões para adicionar ou remover
		W3Button btRemove = new W3Button("item_irr_bt1", "Remover");
		btRemove.setAttribute("onclick","var e = document.getElementById('form_item_irr_table2');" +
											"var linha2 = e.options[e.selectedIndex];" +
											"e.remove(e.selectedIndex);");
		
		W3Button btAdd = new W3Button("control_item_bt2", "Adicionar");
		btAdd.setAttribute("onclick","var e = document.getElementById('form_item_irr_table1');" +
										"var linha     = e.options[e.selectedIndex].text;" +
										"var table     = document.getElementById('form_item_irr_table2');" +
										"var optiongrp = table.children;"+
										"var option    = document.createElement('option');"+
										"option.text   = linha;"+
										"option.value  = e.options[e.selectedIndex].value;" +
										"optiongrp[0].appendChild(option);");										
										
		
		btRemove.setStyle("align", "center");
		btAdd.setStyle("align", "center");
		
		ButtonPanel btpan = new ButtonPanel("item_irr_btpan", null, true);
		btpan.setPage(this);
		btpan.setForm(this.getForm());
		btpan.setInnerClass("button_pannel_ajax");
		btpan.setColumns(8);
		btpan.setAttribute("align", "middle");
		btpan.setStyle("padding-left", "385px");
		btpan.setStyle("padding-top", "30px");
		btpan.setStyle("padding-bottom", "30px");
		btpan.add(btRemove);
		btpan.add(btAdd);
		
		//NormaViolada
		
		normaViolada.setAttribute("align", "left");
		normaViolada.setLabelShown(true);
		normaViolada.setFocus(false);
		
		//Prática Utilizada
		W3TextArea txt2 = new W3TextArea("control_irr_pu", "Prática Utilizada");
		txt2.setAttribute("align", "left");
		txt2.setLabelShown(true);
		txt2.setFocus(false);
		txt2.setMaxLength(2000);
		txt2.setCols(40);
		txt2.setRows(4);
		
		
		//indicador de risco
		
		
		indicadorRisco.setLabelShown(true);
		indicadorRisco.setReadonly(false);
		indicadorRisco.setFocus(false);
		indicadorRisco.setWidth("98%");
		ArrayList<Option> list = null;
		map = CacheResultados.getMapResultados();
		String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
//		for(int i=0;i<SGCConstantes.DOMINIOS_WEBSERVICE.length;i++){
//			String dominio = SGCConstantes.DOMINIOS_WEBSERVICE[i];
//			Object[] object = map.get(SGCConstantes.DOMINIO_COD_RUBRICA);
//			
			//Object[] object = map.get(i);
//			for(int k=0;k<result.size();k++){
//				if(map.get(k).(SGCConstantes.DOMINIO_COD_RUBRICA) && indicadorRisco.getOptionList().isEmpty()){	
//					log.info("Result Cache: "+ToStringUtils.toString(result));
//					indicadorRisco.setOptionList(result);
//					HashMap<String, ArrayList<Option>> map = (HashMap<String, ArrayList<Option>>)session.getAttribute(dominio);
//					if (map!=null && !map.isEmpty()){
//						for(int j=0;j<map.size();j++){
//							list = map.get(dominio);
//							indicadorRisco.setOptionList(list);
//						}
//					}
//				}
//			}
		//}

		//graduador de risco
		
		
		W3Select graduadorRisco = new W3Select("control_irr_gr_risc", "Graduador Risco", false, null);
		graduadorRisco.setLabelShown(true);
		graduadorRisco.setReadonly(false);
		graduadorRisco.setFocus(false);
		graduadorRisco.add("---");
		
		//Painel das tabelas e Botoes
		headerPanel.add(possivelItemTable,8);
//		headerPanel.add(combo1,8);
		headerPanel.add(btpan,16);
//		headerPanel.add(combo2,8);
		headerPanel.add(detectadasItemTable,8);
		headerPanel.setLabel("Tipos de Irregularidades");
//		int i[] = {1,1,1,1,1,1,1,1,1,1,1};
		headerPanel.hasDeclarationHouse = false;
		headerPanel.setColumns(8);
//		headerPanel.setColumnsWidth(i);
		headerPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		headerPanel.setPage(tbControl);
		headerPanel.setForm(tbControl.form);
		
		//Painel Dos Campos de Texto
		secondPanel.setPage(tbControl);
		secondPanel.setForm(tbControl.form);
		secondPanel.hasDeclarationHouse = false;
		secondPanel.setColumns(6);
//		secondPanel.setColumnsWidth(new int[]{1,1,1,1,1,1,1,1});
		secondPanel.setStyle("padding-top", "5px");
		secondPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		if (property.equals("false")) {
			siiafEnable = false;
		}
		if (siiafEnable){
			secondPanel.add(indicadorRisco,6);
			secondPanel.add(normaViolada,6);
		}
		secondPanel.add(new ButtonPanel("separador", null, false),6);
		secondPanel.add(txt2,6);
		
		//Painel que engloba os anteriores 
		mainPanel.add(secondPanel,1);
		mainPanel.add(headerPanel,1);

		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setColumns(1);
		mainPanel.setPage(tbControl);
		mainPanel.setForm(tbControl.form);
		
		itemIrreg.addField(mainPanel);
		
		if (readOnly){
			itemIrreg.setReadonly(readOnly);	
		}
	}

	public ControloItem getFormulario(ControloItem adi_, String numIdentificacao, Form form){
log.info("ITEM_IRREG_GET");	
		if (adi_ == null)
			adi_ = new ControloItem();

		ControloIrregularidade irreg = new ControloIrregularidade();
		
		if(adi_.getControloIrregularidadeItem()!=null){
			irreg = adi_.getControloIrregularidadeItem();
		}
		
		StringBuffer codigosIrreg=null;
		if(detectadasItemTable!=null && detectadasItemTable.getSelectedValues()!=null && detectadasItemTable.getSelectedValues().size()>0){
log.info("detectadasTable.sizeListaValoresSelected:"+detectadasItemTable.getSelectedValues().size());
			codigosIrreg=new StringBuffer("");
			for(int x=0; x<detectadasItemTable.getSelectedValues().size(); x++){

				codigosIrreg.append(detectadasItemTable.getSelectedValues().get(x));
				if(x!=(detectadasItemTable.getSelectedValues().size()-1))
					codigosIrreg.append(SGCConstantes.SPLIT_CODS_IRREG);
			}
		} 
		String norma= (String) form.getFieldValue("control_irr_nv");
		String pratica=(String) form.getFieldValue("control_irr_pu");
		String risco= form.getFieldValue("control_irr_ind_risc");
		
		if(codigosIrreg!=null){
			irreg.setCodigosIrreg(codigosIrreg.toString());
		}
		
		//Combo Norma Violada
		if (StringUtils.isNotBlank(norma) && norma.contains(";")) {
			String[] cod = norma.split(";");
			irreg.setNormaViolada(cod[1]);
			
		}
		
		//Combo Indicador Risco
		if (StringUtils.isNotBlank(risco) && risco.contains(";")) {
			String[] cod = risco.split(";");
			irreg.setIndicadorRisco(cod[1]);
		}
			
		if(irreg==null){
log.info("IRREG_NULL");
			irreg =	new ControloIrregularidade();
		}	
		if(irreg.getChave()==null)
			irreg.setChave(new ControloIrregularidadePK());

		
		if(adi_.getChave()!=null){
			
			irreg.getChave().setNumeroItem(adi_.getChave().getNumeroItem());	
			irreg.getChave().setNumeroControlo(adi_.getChave().getNumeroControlo());
			irreg.getChave().setIndVirtual(adi_.getChave().getIndVirtual());
		}
		
		if(numIdentificacao!= null){
			irreg.setNumIdentificacao(numIdentificacao);
		}
		
		
		if(pratica!=null){
			irreg.setPraticaUtilizada(pratica);
		}
		
		adi_.setControloIrregularidadeItem(irreg);
		return adi_;
	}
	
	public void setFormulario(ControloItem adi_, Form form, Boolean siiafEnable){


		log.info("Item Irregularidades - Set Formulário");
		
		ArrayList<Option> list = null;
		ArrayList<Option> indicadorRiscoList = new ArrayList<Option>();
		ArrayList<Option> normaVioladaList = new ArrayList<Option>();
		String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
		list = CacheResultados.getResultado(dataAtual);
		
		if (siiafEnable){
		
			//COMBO-Indicador Risco
			for (int i=0; i<list.size(); i++){
				if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_INDICADOR_RISCO)){
					indicadorRiscoList.add(list.get(i));
				}
			}
			indicadorRisco.setOptionList(indicadorRiscoList);
			
			//COMBO-Norma Violada
			for (int i=0; i<list.size(); i++){
				if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_NORMA_VIOLADA)){
					normaVioladaList.add(list.get(i));
				}
			}
			normaViolada.setOptionList(normaVioladaList);
		}
	}
	
	@Override
	protected void postInit() {
		
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
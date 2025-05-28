
package com.siemens.ssa.communicator.web.jsp.control;

import java.util.ArrayList;
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
import pt.atos.util.object.ObjectUtil;
import pt.atos.util.string.ToStringUtils;
import pt.atos.util.web.WebControlUtils;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloConseqIrr;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumento;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidade;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidadePK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ListagemIrregularidades;

import pt.atos.sgccomunicator.utils.SGCConstantes;
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
public class TabGeralMercadoriaIrr extends DgitaLayoutPage{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralIrregularidades.class);
	
	W3Select entidadeResponsavel = new W3Select("control_dau_irr_merc_ent", "Entidade Responsavel Guarda", false, null);
	W3Select situacaoMercadoria = new W3Select("control_dau_irr_merc_sit", "Situação da Mercadoria", false, null);
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("item_irr_mainPanel", "");
	protected ExpandableFieldSetPanel secondPanel = new ExpandableFieldSetPanel("item_irr_secondPanel", "");

	
	public boolean displayAjax = false;
	public boolean displayTabIrreg = false;
	public boolean displayButtons = true;
	
	
	/**
	 * Separador de Irregularidades
	 */
	public Tab geralrregMercador;
		
	public TabGeralMercadoriaIrr(DgitaLayoutPage tbControl, boolean readOnly) {
			
		TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		HttpSession session = getSession();
		geralrregMercador = new Tab("geralrregMercador");
	

		
		//Entidade Responsavel Guarda
		
		entidadeResponsavel.setLabelShown(true);
		entidadeResponsavel.setReadonly(false);
		entidadeResponsavel.setFocus(false);
		
		//Situação da Mercadoria
		
		
		situacaoMercadoria.setLabelShown(true);
		situacaoMercadoria.setReadonly(false);
		situacaoMercadoria.setFocus(false);
		
		//Painel Dos Campos de Texto
//		secondPanel.setPage(tbControl);
//		secondPanel.setForm(tbControl.form);
//		secondPanel.hasDeclarationHouse = false;
//		secondPanel.setColumns(6);
////		secondPanel.setColumnsWidth(new int[]{1,1,1,1,1,1,1,1});
//		secondPanel.setStyle("padding-top", "5px");
//		secondPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
//
//		secondPanel.add(new ButtonPanel("separador", null, false),6);

		 
		mainPanel.add(entidadeResponsavel,6);
		mainPanel.add(situacaoMercadoria,6);
		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setColumns(1);
		mainPanel.setPage(tbControl);
		mainPanel.setForm(tbControl.form);
		
		geralrregMercador.addField(mainPanel);
		geralrregMercador.setForm(tbControl.form);
		
		if (readOnly){
			geralrregMercador.setReadonly(readOnly);	
		}
	}

	public Controlo getFormulario(Controlo ctrl, Form form){
		
		ControloConseqIrr ctrlConseqIrr = ctrl.getControloConseqIrr();
		
		Boolean erro = false;
		
		if (ctrlConseqIrr == null)
			ctrlConseqIrr = new ControloConseqIrr();
		
		//Combo Entidade Responsável
		String combEntidadeResponsavel = form.getFieldValue("control_dau_irr_merc_ent");
		log.info("Mercadoria combo Entidade Responsável: "+combEntidadeResponsavel);
		if (StringUtils.isNotBlank(combEntidadeResponsavel) && combEntidadeResponsavel.contains(";")) {
			String[] cod = combEntidadeResponsavel.split(";");
			log.info("Mercadoria Entidade Responsável cod: "+ToStringUtils.toString(cod));
			log.info("Mercadoria Entidade Responsável cod: "+cod[1]);
			ctrlConseqIrr.setEntidadeResponsavel(cod[1]);
		}else{
			ctrlConseqIrr.setEntidadeResponsavel(null);
			erro = true;
		}
		
		String combSituacaoMercadoria = form.getFieldValue("control_dau_irr_merc_sit");
		log.info("Mercadoria combo Situação Mercadoria: "+combSituacaoMercadoria);
		//Combo Situação Mercadoria
		if (StringUtils.isNotBlank(combSituacaoMercadoria) && combSituacaoMercadoria.contains(";")) {
			String[] cod = combSituacaoMercadoria.split(";");
			log.info("Mercadoria Situação Mercadoria cod: "+ToStringUtils.toString(cod));
			ctrlConseqIrr.setSituacaoMercadoria(cod[1]);
		}else{
			ctrlConseqIrr.setSituacaoMercadoria(null);
			erro = true;
		}
		
		if(erro){
			ctrl.setControloConseqIrr(null);
		} else {
			ctrl.setControloConseqIrr(ctrlConseqIrr);
		}
		
		return ctrl;	
		
	}
	
	public void setFormulario(Controlo ctrl, Form form){
		
		log.info("Irregularidade Mercadoria - Set Formulário");
		
		ArrayList<Option> list = null;
		ArrayList<Option> entidadeResponsavelList = new ArrayList<Option>();
		ArrayList<Option> situacaoMercadoriaList = new ArrayList<Option>();
		String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
		list = CacheResultados.getResultado(dataAtual);

		//COMBO-Entidade Responsável
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_ENTIDADE_RESPONSAVEL) ){
				entidadeResponsavelList.add(list.get(i));
			}
		}
		entidadeResponsavel.setOptionList(entidadeResponsavelList);
		
		//COMBO-Situação Mercadoria
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_SITUACAO_MERCADORIA)){
				situacaoMercadoriaList.add(list.get(i));
			}
		}
		situacaoMercadoria.setOptionList(situacaoMercadoriaList);		
		
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
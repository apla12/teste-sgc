

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

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloConseqIrr;
import com.siemens.ssa.communicator.pojo.interfaces.ControloConseqIrrPK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidade;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidadePK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ListagemIrregularidades;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCUtils;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3IntegerField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.field.W3TextField;
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
public class TabItemConseqFinanc extends DgitaLayoutPage{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralIrregularidades.class);
	
	
	
	W3Select tipoMontante = new W3Select("control_add_conseq_financ_tipo_mont", "Tipo Montante", false, null);
	W3Select natureza = new W3Select("control_add_conseq_financ_natureza", "Natureza", false, null);
	W3Select codRubrica = new W3Select("control_add_conseq_financ_codRub", "Codigo Rubrica", false, null);
	W3IntegerField valor = new W3IntegerField("control_add_conseq_financ_valor", "Valor", false);
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("item_irr_mainPanel", "");
	protected ExpandableFieldSetPanel secondPanel = new ExpandableFieldSetPanel("item_irr_secondPanel", "");

	
	public boolean displayAjax = false;
	public boolean displayTabIrreg = false;
	public boolean displayButtons = true;
	

	/**
	 * Separador de Irregularidades
	 */
	public Tab itemConseqFinanc;
		
	public TabItemConseqFinanc(DgitaLayoutPage tbControl, boolean readOnly) {
			
		TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		itemConseqFinanc = new Tab("itemConseqFinanc");
		HttpSession session = getSession();
		

		//Tipo Montante
		
		
		tipoMontante.setLabelShown(true);
		tipoMontante.setReadonly(false);
		tipoMontante.setFocus(false);
	
		//Natureza
		
		natureza.setLabelShown(true);
		natureza.setReadonly(false);
		natureza.setFocus(false);
		
		//Codigo Rubrica
		
		codRubrica.setLabelShown(true);
		codRubrica.setReadonly(false);
		codRubrica.setFocus(false);
		
		//Valor
		
		valor.setLabelShown(true);
		valor.setReadonly(false);
		valor.setFocus(false);
		valor.setSize(14);
		
		//Painel que engloba os anteriores 

		mainPanel.add(tipoMontante,6);
		mainPanel.add(natureza,6);
		mainPanel.add(codRubrica,6);
		mainPanel.add(valor,6);

		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setColumns(1);
		mainPanel.setPage(tbControl);
		mainPanel.setForm(tbControl.form);
		
		itemConseqFinanc.addField(mainPanel);
		itemConseqFinanc.setForm(tbControl.form);
		
		if (readOnly){
			itemConseqFinanc.setReadonly(readOnly);	
		}
	}
	
public ControloItem getFormulario(ControloItem ctrlItem, Form form){
		
		Boolean erro = false;
		
		ControloConseqIrr ctrlConseqIrr = new ControloConseqIrr();
		
		if(ctrlItem.getControloConseqIrrItem()!=null){
			ctrlConseqIrr = ctrlItem.getControloConseqIrrItem();
		}
		
		//Combo Tipo Montante
		String combTipoMontante = form.getFieldValue("control_add_conseq_financ_tipo_mont");
		log.info("ITEM TIPO MONTANTE: "+combTipoMontante);
		if (StringUtils.isNotBlank(combTipoMontante) && combTipoMontante.contains(";")) {
			String[] cod = combTipoMontante.split(";");
			ctrlConseqIrr.setTipoMontante(cod[1]);
		}else{
			ctrlConseqIrr.setTipoMontante(null);
			erro = true;
		}
		
		String combNatureza = form.getFieldValue("control_add_conseq_financ_natureza");
		log.info("ITEM NATUREZA: "+combNatureza);
		//Combo Natureza
		if (StringUtils.isNotBlank(combNatureza) && combNatureza.contains(";")) {
			String[] cod = combNatureza.split(";");
			ctrlConseqIrr.setNatureza(cod[1]);
		}else{
			ctrlConseqIrr.setNatureza(null);
			erro = true;
		}
		
		String combCodRubrica = form.getFieldValue("control_add_conseq_financ_codRub");
		log.info("Consequencias combo Codigo Rubrica: "+combCodRubrica);
		//Combo Codigo Rubrica
		if (StringUtils.isNotBlank(combCodRubrica) && combCodRubrica.contains(";")) {
			String[] cod = combCodRubrica.split(";");
			ctrlConseqIrr.setCodRubrica((String)cod[1]);
		}else{
			ctrlConseqIrr.setCodRubrica(null);
			erro = true;
		}
		
		//Valor
		String txt1 = (String) form.getFieldValue("control_add_conseq_financ_valor");
		if (StringUtils.isNotBlank(txt1)) {
			ctrlConseqIrr.setValor(txt1);
		}
		else{
			ctrlConseqIrr.setValor(null);
			erro = true;
		}
		
		if(ctrlItem.getChave()!=null){
			
			ControloConseqIrrPK chave = new ControloConseqIrrPK();
			
			chave.setNumeroItem(ctrlItem.getChave().getNumeroItem());	
			chave.setNumeroControlo(ctrlItem.getChave().getNumeroControlo());
			chave.setIndVirtual(ctrlItem.getChave().getIndVirtual());
			
			ctrlConseqIrr.setChave(chave);
		}
		
		if (!erro){
			ctrlItem.setControloConseqIrrItem(ctrlConseqIrr);
		} else {
			ctrlItem.setControloConseqIrrItem(null);
		}
		
		return ctrlItem;	
		
	}
	
	public void setFormulario(ControloItem ctrlItem, Form form){
		log.info("Consequencias Financeiras - Set Formulário");
		
		ArrayList<Option> list = null;
		ArrayList<Option> tipoMontanteList = new ArrayList<Option>();
		ArrayList<Option> naturezaList = new ArrayList<Option>();
		ArrayList<Option> codRubricaList = new ArrayList<Option>();
		String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
		list = CacheResultados.getResultado(dataAtual);

		//COMBO-Tipo Montante
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_TIPO_MONTANTE)){
				tipoMontanteList.add(list.get(i));
			}
		}
		tipoMontante.setOptionList(tipoMontanteList);
		
		//COMBO-Natureza
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_NATUREZA)){
				naturezaList.add(list.get(i));
			}
		}
		natureza.setOptionList(naturezaList);
		
		//COMBO-Código Rubrica
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_COD_RUBRICA)){
				codRubricaList.add(list.get(i));
			}
		}
		codRubrica.setOptionList(codRubricaList);
		
		valor.setValue("");
	
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
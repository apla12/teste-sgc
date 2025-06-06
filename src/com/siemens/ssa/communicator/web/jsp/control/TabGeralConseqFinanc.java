
package com.siemens.ssa.communicator.web.jsp.control;

import java.util.ArrayList;

import javax.servlet.http.HttpSession;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloConseqIrr;
import com.siemens.ssa.communicator.webservices.siafservice.DomainQueryType;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.util.cache.CacheResultados;
import pt.atos.util.date.DateUtil;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.logging.Log;
import pt.atos.util.string.ToStringUtils;
import pt.atos.web.click.controls.field.W3IntegerField;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.page.DgitaLayoutPage;

/**
 * Separador para o controlo documental para cada adi��o
 * 
 *
 */
public class TabGeralConseqFinanc extends DgitaLayoutPage{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralConseqFinanc.class);
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("control_dau_conseq_financ_headerPanel", "");
	
	W3Select tipoMontante = new W3Select("control_dau_conseq_financ_tipo_mont", "Tipo Montante", false, null);
	W3Select natureza = new W3Select("control_dau_conseq_financ_natureza", "Natureza", false, null);
	W3Select codRubrica = new W3Select("control_dau_conseq_financ_codRub", "Codigo Rubrica", false, null);
	W3IntegerField valor = new W3IntegerField("control_dau_conseq_financ_valor", "Valor", false);
	
	public boolean displayAjax = false;
	public boolean displayTabIrreg = false;
	public boolean displayButtons = true;
	
	DomainQueryType query;

	/**
	 * Separador de Irregularidades
	 */
	public Tab geralConseqFinanc;
		
	public TabGeralConseqFinanc(DgitaLayoutPage tbControl, boolean readOnly) {
			
		TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		HttpSession session = getSession();
		
		geralConseqFinanc = new Tab("geralConseqFinanc");

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
		
		mainPanel.add(tipoMontante,6);
		mainPanel.add(natureza,6);
		mainPanel.add(codRubrica,6);
		mainPanel.add(valor,6);
		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setColumns(1);
		mainPanel.setPage(tbControl);
		mainPanel.setForm(tbControl.form);
		
		geralConseqFinanc.addField(mainPanel);
		geralConseqFinanc.setForm(tbControl.form);
		
		if (readOnly){
			geralConseqFinanc.setReadonly(readOnly);	
		}
	}

	public Controlo getFormulario(Controlo ctrl, Form form){
		
		ControloConseqIrr ctrlConseqIrr = ctrl.getControloConseqIrr();
		
		Boolean erro = false;
		
		if (ctrlConseqIrr == null)
			ctrlConseqIrr = new ControloConseqIrr();
		
		//Combo Tipo Montante
		String combTipoMontante = form.getFieldValue("control_dau_conseq_financ_tipo_mont");
		log.info("Consequencias combo Tipo Montante: "+combTipoMontante);
		if (StringUtils.isNotBlank(combTipoMontante) && combTipoMontante.contains(";")) {
			String[] cod = combTipoMontante.split(";");
			log.info("cod Tipo Montante"+ToStringUtils.toString(cod));
			ctrlConseqIrr.setTipoMontante(cod[1]);
		}else{
			ctrlConseqIrr.setTipoMontante(null);
			erro = true;
		}
		
		String combNatureza = form.getFieldValue("control_dau_conseq_financ_natureza");
		log.info("Consequencias combo natureza: "+combNatureza);
		//Combo Natureza
		if (StringUtils.isNotBlank(combNatureza) && combNatureza.contains(";")) {
			String[] cod = combNatureza.split(";");
			ctrlConseqIrr.setNatureza(cod[1]);
		}else{
			ctrlConseqIrr.setNatureza(null);
			erro = true;
		}
		
		String combCodRubrica = form.getFieldValue("control_dau_conseq_financ_codRub");
		log.info("Consequencias combo Codigo Rubrica: "+combCodRubrica);
		//Combo Codigo Rubrica
		if (StringUtils.isNotBlank(combCodRubrica) && combCodRubrica.contains(";")) {
			String[] cod = combCodRubrica.split(";");
			ctrlConseqIrr.setCodRubrica(cod[1]);
		}else{
			ctrlConseqIrr.setCodRubrica(null);
			erro = true;
		}
		
		//Valor
		String txt1 = form.getFieldValue("control_dau_conseq_financ_valor");
		log.info("text1 valor: "+txt1);
		if (StringUtils.isNotBlank(txt1)) {
			ctrlConseqIrr.setValor(txt1);
		}
		else{
			ctrlConseqIrr.setValor(null);
			erro = true;
		}
		
		if(erro){
			ctrl.setControloConseqIrr(null);
		} else {
			ctrl.setControloConseqIrr(ctrlConseqIrr);
		}
		
		
		log.info("CTRL:"+ToStringUtils.toString(ctrl));
		
		return ctrl;		
	}

	public void setFormulario(Controlo ctrl, Form form){
log.info("Consequencias Financeiras - Set Formul�rio");
		
		ArrayList<Option> tipoMontanteList = new ArrayList<Option>();
		ArrayList<Option> naturezaList = new ArrayList<Option>();
		ArrayList<Option> codRubricaList = new ArrayList<Option>();
		String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
		ArrayList<Option> list = CacheResultados.getResultado(dataAtual);

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
		
		//COMBO-C�digo Rubrica
		for (int i=0; i<list.size(); i++){
			if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_COD_RUBRICA)){
				codRubricaList.add(list.get(i));
			}
		}
		codRubrica.setOptionList(codRubricaList);	
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
		
	}	
}
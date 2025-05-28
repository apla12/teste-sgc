package com.siemens.ssa.communicator.web.jsp.control;

import org.apache.click.control.Form;

import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.page.DgitaLayoutPage;

/**
 * Separador para o controlo documental para cada adição
 * 
 *
 */
public class TabGeralIrregularidades extends DgitaLayoutPage{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralIrregularidades.class);
		
	private TabGroup grupoTabsIrr;
	private TabRegistarIrregularidades  regIrreg = null;
	private TabConsultarIrregularidades consIrreg = null;

	/**
	 * Separador de Irregularidades
	 */
	public Tab phyControlIrrhy;
		
	public TabGeralIrregularidades(DgitaLayoutPage tbControl, boolean readOnly, DeclarationProcessor declarationProcessor) {
		
		log.info("TabGeralIrregularidades - Inicio - Criar layout");
		
		phyControlIrrhy = new Tab("PhyControlIrr");
		
		grupoTabsIrr = new TabGroup("tabGroupPop");
		grupoTabsIrr.setParent(this);
		grupoTabsIrr.setPageToAll(tbControl);
		grupoTabsIrr.setForm(tbControl.form);
		grupoTabsIrr.setExistsTabbedForm(true);
				
		regIrreg    = new TabRegistarIrregularidades(tbControl, readOnly);
		grupoTabsIrr.setTab(regIrreg.registarIrreg);
		
		consIrreg  = new TabConsultarIrregularidades(tbControl, readOnly);
		grupoTabsIrr.setTab(consIrreg.consultaIrreg);
		
		phyControlIrrhy.addField(grupoTabsIrr);
		
	}

	public Controlo getFormulario(Controlo dau, Form form){
		
		return regIrreg.getFormulario(dau, form);
	}
	
	public void setFormulario(Controlo ctrl, Form form){
		log.info("Irregularidades - Set Formulário");
		// Popular os campos TabRegistarIrregularidades 
	    regIrreg.setFormulario(ctrl);

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
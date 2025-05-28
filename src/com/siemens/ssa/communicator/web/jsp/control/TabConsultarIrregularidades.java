package com.siemens.ssa.communicator.web.jsp.control;

import org.apache.click.control.Column;
import org.apache.click.control.Form;

import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;

import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

/**
 * Separador para a consulta de irregularidades
 * 
 *
 */
public class TabConsultarIrregularidades{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralIrregularidades.class);
	
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("consulta_irr_mainPanel", "");
	
	
	DgitaTable tabelaConsulta = new DgitaTable("consulta_irregularidades_tabela");
	
	/**
	 * Separador consulta de Irregularidades
	 */
	public Tab consultaIrreg;
		
	@SuppressWarnings("unchecked")
	public TabConsultarIrregularidades(DgitaLayoutPage tbControl, boolean readOnly) {
		
		log.info("TabConsultarIrregularidades - Inicio - Criar layout");
		
		consultaIrreg = new Tab("consulta");
		
		Column col1 = new Column("versao");
		col1.setWidth("30%");
		col1.setHeaderTitle("Versão");
		
		Column col2 = new Column("resContr");
		col2.setWidth("30%");
		col2.setHeaderTitle("Resultado Controlo");
		
		Column col3 = new Column("irregularidade");
		col3.setWidth("30%");
		col3.setHeaderTitle("Irregularidade");
		
		Column col4 = new AnonymousCallColumn("drop");
		col4.setHeaderTitle("");
		col4.setWidth("30%");
		
		tabelaConsulta.addColumn(col1);
		tabelaConsulta.addColumn(col2);
		tabelaConsulta.addColumn(col3);
		tabelaConsulta.addColumn(col4);		
		tabelaConsulta.setNoPopUp(true);	
		
		FieldSetLayout layout = new FieldSetLayout(6,new String[]{"0%","20%","0%","20%","0%","20%","0%","20%","0%","20%","0%","20%"});
		
		mainPanel.setFieldSetLayout(layout);
		
		mainPanel.add(tabelaConsulta,6);

		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setColumns(1);
		mainPanel.setPage(tbControl);
		mainPanel.setForm(tbControl.form);
		consultaIrreg.addField(mainPanel);
	
		log.info("TabConsultarIrregularidades - Fim - Criar layout");
		
	}

	public ControloItem getFormulario(ControloItem adi_, String numIdentificacao, Form form){
	 return null;
	}
	
	public void setFormulario(){
	
	}
	
	protected void postInit() {
		
	}
	
	
	protected void buildPage() {
		// TODO Auto-generated method stub
		
	}

	protected void getFormData() {
		// TODO Auto-generated method stub
		
	}

	public boolean onCancelar() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onGravar() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void setFormData() {
		// TODO Auto-generated method stub	
	}	
}
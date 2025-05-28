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
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
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
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.page.DgitaLayoutPage;

/**
 * Separador para o registo irregularidades
 * 
 *
 */
public class TabRegistarIrregularidades{
	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralIrregularidades.class);
	
	
	W3Select tipoIrregularidade   = new W3Select("control_tip_irr","Tipo de irregularidade");
	W3Select indicadorRisco = new W3Select("control_ind_risc", "Indicador Risco");
	W3Select normaViolada = new W3Select("control_nv", "Norma Violada");
	
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("regista_irr_mainPanel", "");
	
	/**
	 * Separador registo de Irregularidades
	 */
	public Tab registarIrreg;
		
	@SuppressWarnings("unchecked")
	public TabRegistarIrregularidades(DgitaLayoutPage tbControl, boolean readOnly) {
		
		log.info("TabRegistarIrregularidades - Inicio - Criar layout");
			
		registarIrreg = new Tab("registar");
	
		//Tipo Irregularidade		
		tipoIrregularidade.setAttribute("align", "left");
		tipoIrregularidade.setLabelShown(true);
		tipoIrregularidade.setFocus(false);
		
		//NormaViolada		
		normaViolada.setAttribute("align", "left");
		normaViolada.setLabelShown(true);
		normaViolada.setFocus(false);
		
		//indicador de risco		
		indicadorRisco.setLabelShown(true);
		//indicadorRisco.setReadonly(false);
		indicadorRisco.setFocus(false);
		
		//Prática Utilizada
		W3TextArea txt2 = new W3TextArea("control_irr_pu", "Prática Utilizada");
		txt2.setAttribute("align", "left");
		txt2.setLabelShown(true);
		txt2.setFocus(false);
		txt2.setMaxLength(2000);
		txt2.setCols(40);
		txt2.setRows(4);
		
		//Local da Prática 
		W3TextArea txt3 = new W3TextArea("control_irr_lp", "Local da Prática ");
		txt3.setAttribute("align", "left");
		txt3.setLabelShown(true);
		txt3.setFocus(false);
		txt3.setMaxLength(2000);
		txt3.setCols(40);
		txt3.setRows(4);
		
		FieldSetLayout layout = new FieldSetLayout(6,new String[]{"0%","20%","0%","20%","0%","20%","0%","20%","0%","20%","0%","20%"});
		
		mainPanel.setFieldSetLayout(layout);
		
		mainPanel.add(normaViolada,6);
		mainPanel.add(tipoIrregularidade,6);
		mainPanel.add(indicadorRisco,6);
		mainPanel.add(txt2,6);
		mainPanel.add(txt3,6);
		
		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setColumns(1);
		mainPanel.setPage(tbControl);
		mainPanel.setForm(tbControl.form);
		registarIrreg.addField(mainPanel);
		
		log.info("TabRegistarIrregularidades - FIM - Criar layout");
	}

	public Controlo getFormulario(Controlo dau, Form form) {
	
		if (dau == null)
			dau = new Controlo();
		// Se o controlo nao for 'B1-Não Conforme' não existe irregularidades, e não carrega a lista
		if(SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(dau.getResultadoControlo())){	
			ControloIrregularidade irreg = new ControloIrregularidade();
							
			String norm = form.getFieldValue("control_nv");
			//Combo Norma Violada
			if (StringUtils.isNotBlank(norm) && norm.contains(";")) {
				String[] cod = norm.split(";");
				irreg.setNormaViolada(cod[1]);
			}
			
			String risco = form.getFieldValue("control_ind_risc");
			//Combo Indicador Risco
			if (StringUtils.isNotBlank(risco) && risco.contains(";")) {
				String[] cod = risco.split(";");
				irreg.setIndicadorRisco(cod[1]);
			}
			
			String tipoIrreg = form.getFieldValue("control_tip_irr");
			//Combo Indicador Risco
			if (StringUtils.isNotBlank(tipoIrreg) && tipoIrreg.contains(";")) {
				String[] cod = tipoIrreg.split(";");
				irreg.setCodigosIrreg(cod[1]);
			}
				
			String pratica = form.getFieldValue("control_irr_pu");
			
			String localPratica = form.getFieldValue("control_irr_lp");
	
				irreg.setChave(new ControloIrregularidadePK());
				irreg.getChave().setNumeroItem(new Short(SGCConstantes.CONTROLO_IRREGULARIDADE_GERAL));
				irreg.getChave().setIndVirtual(SGCConstantes.FLAG_BD_FALSO);
				irreg.getChave().setNumeroControlo(dau.getChave().getNumeroControlo());
				irreg.setNumIdentificacao(dau.getNumIdentificacao());
				irreg.setPraticaUtilizada(pratica);
				irreg.setLocalPratica(localPratica);
				dau.setControloIrregularidade(irreg);
		}	 
		
		return dau;
	}
	
	public void setFormulario(Controlo dau){
	
		log.info("TabRegistarIrregularidades - Set Formulário");
		
		ArrayList<Option> list = null;
		ArrayList<Option> tipoIrregularidadeList = new ArrayList<Option>();
		ArrayList<Option> indicadorRiscoList = new ArrayList<Option>();
		ArrayList<Option> normaVioladaList = new ArrayList<Option>();
		String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
		list = CacheResultados.getResultado(dataAtual);
		
		if (dau.getControloIrregularidade() == null){ // caso esteja vazio preenche a drop com todos registos dos dominios - modo submeter controlo
				
			if (list != null) {
				
				//COMBO-Tipo irregularidade
				for (int i=0; i<list.size(); i++){
					if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_TIPO_IRREGULARIDADE)){
						tipoIrregularidadeList.add(list.get(i));
					}
				}
				tipoIrregularidade.setOptionList(tipoIrregularidadeList);
				
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
		}else { // preenche a drop com o registo ja selecionado - modo consulta do controlo
			
			for (int i=0; i<list.size(); i++){
				if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_TIPO_IRREGULARIDADE)){
					if ( list.get(i).getValue().contains(dau.getControloIrregularidade().getCodigosIrreg())) {
						tipoIrregularidadeList.add(list.get(i));
						tipoIrregularidade.setOptionList(tipoIrregularidadeList);
					}
				}
			}
			for (int i=0; i<list.size(); i++){
				if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_INDICADOR_RISCO)){
					if ( list.get(i).getValue().contains(dau.getControloIrregularidade().getIndicadorRisco()))  {
						indicadorRiscoList.add(list.get(i));
						indicadorRisco.setOptionList(indicadorRiscoList);
					}
				}
			}
			for (int i=0; i<list.size(); i++){
				if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_NORMA_VIOLADA)){
					if ( list.get(i).getValue().contains(dau.getControloIrregularidade().getNormaViolada()))  {
						normaVioladaList.add(list.get(i));
						normaViolada.setOptionList(normaVioladaList);
					}
				}
			}
				
			
//			mainPanel.getField("control_ind_risc").setValue(dau.getControloIrregularidade().getIndicadorRisco());
//			mainPanel.getField("control_nv").setValue(dau.getControloIrregularidade().getNormaViolada());
			mainPanel.getField("control_irr_pu").setValue(dau.getControloIrregularidade().getPraticaUtilizada());
			mainPanel.getField("control_irr_lp").setValue(dau.getControloIrregularidade().getLocalPratica());

		}
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
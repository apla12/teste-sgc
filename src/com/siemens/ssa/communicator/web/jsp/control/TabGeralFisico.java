package com.siemens.ssa.communicator.web.jsp.control;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.click.control.Field;
import org.apache.click.control.Form;
import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.ControloDocumentoDeclaracaoService;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoDecl;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoDeclPK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.MeioAutAferido;
import com.siemens.ssa.communicator.pojo.interfaces.MeioAutAferidoDet;
import com.siemens.ssa.communicator.pojo.interfaces.MeioAutAferidoDetPK;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.util.presentation.TableItem;
import pt.atos.web.click.controls.field.W3Checkbox;
import pt.atos.web.click.controls.field.W3DateField;
import pt.atos.web.click.controls.field.W3IntegerField;
import pt.atos.web.click.controls.field.W3Label;
import pt.atos.web.click.controls.field.W3Radio;
import pt.atos.web.click.controls.field.W3RadioGroup;
import pt.atos.web.click.controls.field.W3ReadOnlyField;
import pt.atos.web.click.controls.field.W3TextArea;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabGeralFisico {
	
	private static final long serialVersionUID = 1L;
	private static Log log = Log.getLogger(TabGeralFisico.class);
	
	protected CompleteFieldSetPanel mainPanel = new CompleteFieldSetPanel("control_phy_mainPanel", "");
	protected ExpandableFieldSetPanel automaticPanel = new ExpandableFieldSetPanel("control_phy_automaticPanel", "");
	protected ArrayList<MeioAferidoItem> listaMeiosAferidos = new ArrayList<MeioAferidoItem>();
	protected DIV automaticTableDiv = new DIV("automaticTableDiv");
	protected ExpandableFieldSetPanel datePanel = new ExpandableFieldSetPanel("control_phy_datePanel", "");
	protected W3DateField dt1 = new W3DateField("geral_phy_dt1");
	protected W3DateField dt2 = new W3DateField("geral_phy_dt2");
	protected ExpandableFieldSetPanel observacoesPanel = new ExpandableFieldSetPanel("control_phy_observacoesPanel", "");
	protected W3TextArea observacoes = new W3TextArea("geral_phy_observ", false);
	
	
	
	
	protected ExpandableFieldSetPanel selagemPanel = new ExpandableFieldSetPanel("control_phy_selagemPanel", "");
	protected ExpandableFieldSetPanel selagemPanel2 = new ExpandableFieldSetPanel("control_phy_selagemPanel2", "");
	protected ExpandableFieldSetPanel motivoPanel = new ExpandableFieldSetPanel("control_phy_motivoPanel", "");
	
	
	
	public W3RadioGroup conf3 = new W3RadioGroup("geral_phy_radioSelagem1_1",false);
	
	public Tab geralControlFisic;
	private boolean readOnly = false;
	
	public TabGeralFisico(DgitaLayoutPage pg, boolean readOnly, String momento, DeclarationProcessor declarationProcessor) {
		
		geralControlFisic = new Tab("geralControlFisic") {
			@Override
			public void setReadonly(boolean readOnly) {
				setTabReadonly(readOnly);
			}
		};
				
		automaticPanel.setFieldSetLayout(new FieldSetLayout(1, new String[]{"0%","100%"}));
		automaticPanel.setWidth("100%");
		automaticPanel.setStyle("padding-top","10px");
		automaticPanel.setStyle("padding-bottom","10px");
		automaticPanel.setInnerPanelClass("");
				
		W3ReadOnlyField meios = new W3ReadOnlyField("Controlos efetuados:");
		meios.setId("geral_phy_meios");
		meios.setLabel(declarationProcessor.getWidgetLabel(meios.getId()));
		meios.setLabelShown(true);
		meios.setStyle("padding-bottom","5px");
		automaticPanel.add(meios);
		
		try {
			ControloDocumentoDeclaracaoService srvDecl = EJBUtil.getSessionInterface(ControloDocumentoDeclaracaoService.class);
			ArrayList<ControloDocumentoDecl> campos = srvDecl.getListaControloFisico(SGCConstantes.SISTEMA_DLCC2, momento, "CTR_FIS");
			
			for(ControloDocumentoDecl campo : campos) {
				boolean txtBox = false;
				if(campo.getLabel().equalsIgnoreCase("Outros")) txtBox = true;
				MeioAferidoItem item = new MeioAferidoItem(campo.getChave(), campo.getLabel(), campo.getPosicao(), txtBox);
				listaMeiosAferidos.add(item);
			}
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		
		
		
		
		
		ExpandableFieldSetPanel itemPanelHeader = new ExpandableFieldSetPanel("itemPanelHeader", "");
		itemPanelHeader.setFieldSetLayout(new FieldSetLayout(2, new String[]{"0%","50%", "0%","50%"}));
		itemPanelHeader.setStyle("border-bottom","2px solid black");
		itemPanelHeader.setStyle("margin","0px 15px");
		W3Label controloLabel = new W3Label("controloLabel");
		controloLabel.setId("geral_phy_controlo_label");
		controloLabel.setLabel(declarationProcessor.getWidgetLabel(controloLabel.getId()));
		itemPanelHeader.add(controloLabel);
		W3Label conferidoLabel = new W3Label("conferidoLabel");
		conferidoLabel.setId("geral_phy_conferido_label");
		conferidoLabel.setLabel(declarationProcessor.getWidgetLabel(conferidoLabel.getId()));
		itemPanelHeader.add(conferidoLabel);
		automaticTableDiv.add(itemPanelHeader);
		
		int index = 0;
		for(MeioAferidoItem item : listaMeiosAferidos) {
			
			ExpandableFieldSetPanel itemPanel = new ExpandableFieldSetPanel("itemPanel_" + index, "");
			itemPanel.setFieldSetLayout(new FieldSetLayout(2, new String[]{"0%","50%", "0%","50%"}));
			if(index != 0) itemPanel.setStyle("border-top","1px dotted black");
			itemPanel.setStyle("margin","0px 15px");
			
			W3Label itemLabel = new W3Label("itemLabel_" + index, item.getMeio());
			itemPanel.add(itemLabel);
			
			itemPanel.add(item.div);
			
			automaticTableDiv.add(itemPanel);
			
			index++;
		}
		
		
		
		
		
		
		automaticTableDiv.setStyle("background-color", "white");
		automaticTableDiv.setStyle("padding-top","10px");
		automaticTableDiv.setStyle("padding-bottom","10px");
		automaticPanel.add(automaticTableDiv);
		
		//Controlo Físico e Carregamento
		W3ReadOnlyField controloFisico = new W3ReadOnlyField("Controlo Físico e Carregamento");
		controloFisico.setId("geral_phy_controlo_fisico_carregamento_label");
		controloFisico.setLabel(declarationProcessor.getWidgetLabel(controloFisico.getId()));
		controloFisico.setLabelShown(true);
		controloFisico.setStyle("padding-bottom","5px");
					
		dt1.setLabelShown(true);
		dt1.setRenderTime(true);		
		dt1.setStyle("font-weight", "normal");		
		dt1.setStyle("weight", "normal");
		dt2.setLabelShown(true);
		dt2.setRenderTime(true);
		dt2.setStyle("font-weight", "normal");		
		dt2.setStyle("weight", "normal");
		
		datePanel.add(controloFisico, 4);		
		datePanel.add(dt1);
		datePanel.add(dt2);

		datePanel.hasDeclarationHouse = false;
		datePanel.setWidth("100%");
		datePanel.setLabel("Controlo Físico e Carregamento(Opcional)");
		datePanel.setFieldSetLayout(new FieldSetLayout(2, new String[]{"15%","35%","5%","35%"}));
		datePanel.setStyle("padding-top","10px");
		datePanel.setStyle("padding-bottom","10px");
		
		//Observacoes
		observacoes.setLabel(declarationProcessor.getWidgetLabel(observacoes.getId()));
		observacoes.setLabelShown(true);
		observacoes.setFocus(false);
		observacoes.setMaxLength(2000);
		observacoes.setCols(69);
		observacoes.setRows(4);
		observacoes.setTooltip(declarationProcessor.getWidgetHelp(observacoes.getId()));
		
		observacoesPanel.add(observacoes,1);
		observacoesPanel.setFieldSetLayout(new FieldSetLayout(1, new String[]{"15%","35%","5%","35%"}));
		observacoesPanel.setWidth("100%");
		observacoesPanel.setStyle("padding-top","10px");
		observacoesPanel.setInnerPanelClass("");
		
		
		
		
		
		
		W3ReadOnlyField selagem = new W3ReadOnlyField("Selagem meios(s) de Transporte");
		selagem.setLabelShown(true);
		selagem.setStyle("padding-bottom","5px");
		
		W3Radio rad8 = new W3Radio("sim","Sim");
		rad8.setAttribute("onclick","document.getElementById('form_geral_phy_motivoSelagem').disabled= false;" +
									"document.getElementById('form_geral_phy_totalSelos').disabled= false;");
		W3Radio rad9 = new W3Radio("nao","Não");
		rad9.setAttribute("onclick","document.getElementById('form_geral_phy_motivoSelagem').disabled= true;" +
									"document.getElementById('form_geral_phy_totalSelos').disabled= true;");
		
		
		conf3.add(rad8);
		conf3.add(rad9);
		conf3.setLabelShown(true);
		conf3.setVerticalLayout(false);
		conf3.setRequired(true);
		conf3.setDisabled(false);
		
		W3TextArea motivo = new W3TextArea("geral_phy_motivoSelagem", "Motivo");
		motivo.setLabelShown(true);
		motivo.setFocus(false);
		motivo.setMaxLength(2000);
		motivo.setCols(69);
		motivo.setRows(4);
		
		W3IntegerField totalSelos = new W3IntegerField("geral_phy_totalSelos");
		totalSelos.setLabelShown(true);
		totalSelos.setMaxLength(4);
		totalSelos.setSize(4);
		
		
		selagemPanel.add(selagem,4);
		selagemPanel.add(conf3,1);
		selagemPanel.add(totalSelos,1);
		selagemPanel.add(motivo,4);
		selagemPanel.hasDeclarationHouse = false;
		selagemPanel.setWidth("100%");
		selagemPanel.setFieldSetLayout(new FieldSetLayout(3, new String[]{"15%","35%","5%","35%", "15%","35%"}));
		selagemPanel.setStyle("padding-top","10px");
		
		
	
//		motivoPanel.add(motivo,2);
//		motivoPanel.hasDeclarationHouse = false;
//		motivoPanel.setWidth("100%");
//		motivoPanel.setFieldSetLayout(new FieldSetLayout(2, new String[]{"15%","35%","5%","35%"}));
	
		selagemPanel2.add(selagemPanel,1);
//		selagemPanel2.add(motivoPanel,1);
		selagemPanel2.setFieldSetLayout(new FieldSetLayout(1, new String[]{"15%","35%","5%","35%"}));
		selagemPanel2.setWidth(WebConstants.POPUP_TABLE_SIZE);
//		selagemPanel2.setBorder(5);
		
		//
 
		//Cor das divisões dos campos
		selagemPanel2.setStyle("border", "1px solid gray");
//		selagemPanel2.setStyle("border", "1px solid lightgray");
		selagemPanel2.setInnerPanelClass("");
		

	
		
	
		
		if(declarationProcessor.getWidgetVisible(automaticPanel.getId())) mainPanel.add(automaticPanel, 4);
		if(declarationProcessor.getWidgetVisible(datePanel.getId())) mainPanel.add(datePanel, 4);
		//mainPanel.add(selagemPanel,4);
		if(declarationProcessor.getWidgetVisible(observacoesPanel.getId())) mainPanel.add(observacoesPanel, 4);
		mainPanel.setWidth(WebConstants.POPUP_TABLE_SIZE);
		mainPanel.setPage(pg);
		mainPanel.setForm(pg.form);
//		mainPanel.setFieldSetLayout(new FieldSetLayout(2, new String[]{"15%","35%","5%","35%"}));
		mainPanel.setParent(pg);
		
		
		int[] flds = {1,1};
		geralControlFisic.setNumberFieldsPerLine(flds);
		geralControlFisic.addField(mainPanel,"100%");
		geralControlFisic.setWidth("100%");
	}

	
	/**
	 * Função para fazer colocar no forumlário os dados
	 * passados como parâmetro
	 * 
	 * @param adi_ - pojo do controlo da declaração para o preenchimento
	 * @param form - formulário da página passado por parâmetro
	 */
	public void setFormulario(Controlo ctrl, Form form){
log.info("GERAL_FISICO_SET...");

		if(ctrl!=null && ctrl.getMeioAutAferido()!=null){
			MeioAutAferido aferid = ctrl.getMeioAutAferido();
			
			setMeiosTable(readOnly, aferid.getControloMeioDetalhes(), aferid.getOutroMotivo());
			
			//Controlo Físico e Carregamento			
			if(aferid.getDataInicioCarregamento() != null)
				dt1.setValue(aferid.getDataInicioCarregamento());
			else
				((Field) dt1).setValue(null);
			
			if(aferid.getDataFimCarregamento() != null)
				dt2.setValue(aferid.getDataFimCarregamento());
			else
				((Field) dt2).setValue(null);
			
			//Observacoes
			observacoes.setValue(aferid.getObservacoes());
		}
		
		
		
		
		
		
		
//		
//		if(adi_!=null && adi_.getFimControloFisico()!=null)
//			((W3DateField)headerPanel.getField("control_phy_phy_dt2")).setValue(adi_.getFimControloFisico());
//		else
//			headerPanel.getField("control_phy_phy_dt2").setValue(null);
//		
//		if(adi_!=null && adi_.getVerificacao()!=null)
//			headerPanel.getField("control_ind_txt1_2").setValue(adi_.getVerificacao());
//		else
//			headerPanel.getField("control_ind_txt1_2").setValue(null);
//			
//		if(adi_!=null && adi_.getPesagem()!=null)
//			headerPanel.getField("control_ind_txt1_1").setValue(adi_.getPesagem());
//		else
//			headerPanel.getField("control_ind_txt1_1").setValue(null);
//		
//		if(adi_!=null && adi_.getTipoVerificacao()!=null)
//			((W3RadioGroup)headerPanel.getField("control_ind_radioGroup1_2")).setValue(adi_.getTipoVerificacao());
//		else{
//			((W3RadioGroup)headerPanel.getField("control_ind_radioGroup1_2")).setValue(null);		
//		}	
//		
//		if(adi_!=null && adi_.getTipoPesagem()!=null)
//			((W3RadioGroup)headerPanel.getField("control_ind_radioGroup1_1")).setValue(adi_.getTipoPesagem());
//		else
//			((W3RadioGroup)headerPanel.getField("control_ind_radioGroup1_1")).setValue(null);
	}
	
	private void setMeiosTable(boolean disabled, ArrayList<MeioAutAferidoDet> meioDetalhes, String outrosTxt) {
		for(MeioAferidoItem item : listaMeiosAferidos) {
			item.setDisabled(disabled);
					
			for(MeioAutAferidoDet meioDetalhe : meioDetalhes) {
				if(item.getMeio().equals(meioDetalhe.getNValor())) {
					item.setChecked(SGCConstantes.FLAG_BD_VERDADEIRO.equals(meioDetalhe.getFlagConferido()));
					item.setBoxTxt(outrosTxt);
					break;
				}
			}
		}
	}

	/**
	 * 
	 * Método que preenche o POJO com os dados correspondentes no FORM da página
	 * 
	 * */ 
	public ControloItem getFormulario(Controlo ctrl, Form form){
		log.info("GERAL_FISICO_GET...");
				
		MeioAutAferido meioAut = ctrl.getMeioAutAferido();
		if(meioAut == null) {
			meioAut = new MeioAutAferido();
			ctrl.setMeioAutAferido(meioAut);
		}
		
		meioAut.setFlagMeioAut(SGCConstantes.FLAG_BD_VERDADEIRO);
		meioAut.setFlagSelagem(SGCConstantes.FLAG_BD_FALSO);
		
		
		ArrayList<MeioAutAferidoDet> detalhes = meioAut.getControloMeioDetalhes();
		if(detalhes == null) {
			detalhes = new ArrayList<MeioAutAferidoDet>();
			meioAut.setControloMeioDetalhes(detalhes);
		}
				
		for(MeioAferidoItem item : listaMeiosAferidos) {
			MeioAutAferidoDet controloMeioDet = null;			
			for(MeioAutAferidoDet detalhe : detalhes) {
				if(item.getMeio().equals(detalhe.getNValor())) {
					controloMeioDet = detalhe;
				}
			}
			
			if(controloMeioDet == null) {
				controloMeioDet = new MeioAutAferidoDet();
				MeioAutAferidoDetPK detalhePK = new MeioAutAferidoDetPK();
				detalhePK.setPosicao(item.getPosicao().intValue());
				if(meioAut.getChave() != null) detalhePK.setIdMeioAferido(meioAut.getChave().getIdMeioAferido());
				controloMeioDet.setChave(detalhePK);
				detalhes.add(controloMeioDet);
			}
								
			controloMeioDet.setNValor(item.getMeio());
			String itemParameter = form.getPage().getContext().getRequestParameter("meioAferidoItemCbx_" + item.getPK());
			controloMeioDet.setFlagConferido("on".equals(itemParameter) ? SGCConstantes.FLAG_BD_VERDADEIRO : SGCConstantes.FLAG_BD_FALSO);
			
			String itemTxtParameter = form.getPage().getContext().getRequestParameter("meioAferidoItemTxt_" + item.getPK());
			if(item.getMeio().equalsIgnoreCase("Outros")) meioAut.setOutroMotivo(itemTxtParameter);
		}
			
		//Controlo Físico e Carregamento
		String dataInicioCarregamentoAno = form.getPage().getContext().getRequestParameter("geral_phy_dt1.ano");
		String dataInicioCarregamentoMes = form.getPage().getContext().getRequestParameter("geral_phy_dt1.mes");
		String dataInicioCarregamentoDia = form.getPage().getContext().getRequestParameter("geral_phy_dt1.dia");		
		String dataInicioCarregamentoHora = form.getPage().getContext().getRequestParameter("geral_phy_dt1.hora");
		String dataInicioCarregamentoMinuto = form.getPage().getContext().getRequestParameter("geral_phy_dt1.minuto");
		
		Timestamp dataInicioCarregamento = null;
		if(StringUtils.isNotBlank(dataInicioCarregamentoAno)
				&& StringUtils.isNotBlank(dataInicioCarregamentoMes)
				&& StringUtils.isNotBlank(dataInicioCarregamentoDia)
				&& StringUtils.isNotBlank(dataInicioCarregamentoHora)
				&& StringUtils.isNotBlank(dataInicioCarregamentoMinuto)) {
			
			int ano = new Integer(dataInicioCarregamentoAno).intValue();
			int mes = new Integer(dataInicioCarregamentoMes).intValue();
			int dia = new Integer(dataInicioCarregamentoDia).intValue();
			int hora = new Integer(dataInicioCarregamentoHora).intValue();
			int miuto = new Integer(dataInicioCarregamentoMinuto).intValue();
	
			Calendar cl = Calendar.getInstance();
			cl.set(ano, mes-1, dia, hora, miuto);
			dataInicioCarregamento = new Timestamp(cl.getTimeInMillis());
		}
		meioAut.setDataInicioCarregamento(dataInicioCarregamento);
		
		
		String dataFimCarregamentoAno = form.getPage().getContext().getRequestParameter("geral_phy_dt2.ano");
		String dataFimCarregamentoMes = form.getPage().getContext().getRequestParameter("geral_phy_dt2.mes");
		String dataFimCarregamentoDia = form.getPage().getContext().getRequestParameter("geral_phy_dt2.dia");		
		String dataFimCarregamentoHora = form.getPage().getContext().getRequestParameter("geral_phy_dt2.hora");
		String dataFimCarregamentoMinuto = form.getPage().getContext().getRequestParameter("geral_phy_dt2.minuto");
		
		Timestamp dataFimCarregamento = null;
		if(StringUtils.isNotBlank(dataFimCarregamentoAno)
				&& StringUtils.isNotBlank(dataFimCarregamentoMes)
				&& StringUtils.isNotBlank(dataFimCarregamentoDia)
				&& StringUtils.isNotBlank(dataFimCarregamentoHora)
				&& StringUtils.isNotBlank(dataFimCarregamentoMinuto)) {
			
			int ano = new Integer(dataFimCarregamentoAno).intValue();
			int mes = new Integer(dataFimCarregamentoMes).intValue();
			int dia = new Integer(dataFimCarregamentoDia).intValue();
			int hora = new Integer(dataFimCarregamentoHora).intValue();
			int miuto = new Integer(dataFimCarregamentoMinuto).intValue();
	
			Calendar cl = Calendar.getInstance();
			cl.set(ano, mes-1, dia, hora, miuto);
			dataFimCarregamento = new Timestamp(cl.getTimeInMillis());
		}
		meioAut.setDataFimCarregamento(dataFimCarregamento);
		
		// Observacoes
		String observacoes = form.getPage().getContext().getRequestParameter("geral_phy_observ");
		meioAut.setObservacoes(observacoes);
		
		
		
		
		/*
//CONTROLO FÍSICO E CARREGAMENTO - END
//SELAGEM MEIOS(S) DE TRANSPORTE - START
		String selagem=form.getFieldValue("geral_phy_radioSelagem1_1");
		if(StringUtils.isNotBlank(selagem)){
			if (StringUtils.equals(selagem, SGCConstantes.FLAG_RADIO_NAO)){
				meioAut.setFlagSelagem("F");
			}
			else if (StringUtils.equals(selagem, SGCConstantes.FLAG_RADIO_SIM)){
				meioAut.setFlagSelagem("V");
			}
		}
		String qtSelos=form.getFieldValue("geral_phy_totalSelos");
		if(StringUtils.isNotBlank(qtSelos)){
			meioAut.setQuantidadeSelos(new Short(qtSelos));
		}
		meioAut.setMotivoSelagem(form.getFieldValue("geral_phy_motivoSelagem"));
//SELAGEM MEIOS(S) DE TRANSPORTE - END
//OBSERVAÇÕES
		meioAut.setObservacoes(form.getFieldValue("geral_phy_observ"));
log.info("#meioAut.getObservacoes:"+meioAut.getObservacoes());		
		*/
		
		return null;
	}

	
	public class MeioAferidoItem implements TableItem
	{
		public ControloDocumentoDeclPK chave;
		public String meio;
		public DIV div;
		public W3Checkbox w3Checkbox;
		public W3TextField meiosOutrosDesc;
		public Number posicao;
		
		public MeioAferidoItem(ControloDocumentoDeclPK chave, String meio, Number posicao, boolean txtBox) {
			this.chave = chave;
			this.meio = meio;
			this.posicao = posicao;
			
			div = new DIV("meioAferidoItemDIV_" + chave.getIdentDocumentoDeclaracao().toString());
			div.addStyleClass("item_cbx");
			
			w3Checkbox = new W3Checkbox("meioAferidoItemCbx_" + chave.getIdentDocumentoDeclaracao().toString());
			w3Checkbox.setLabelShown(false);
			div.add(w3Checkbox);
			
			meiosOutrosDesc = new W3TextField("meioAferidoItemTxt_" + chave.getIdentDocumentoDeclaracao().toString(), "");
			meiosOutrosDesc.setMaxLength(40);		
			meiosOutrosDesc.setWidth("200px");
			if(txtBox) div.add(meiosOutrosDesc);
		}
		
		public String getMeio() {
			return meio;
		}
		
		public String getBox() {
			return div.toString();
		}
		
		public void setDisabled(boolean disabled) {
			w3Checkbox.setDisabled(disabled);
			meiosOutrosDesc.setDisabled(disabled);
		}
		
		public void setChecked(boolean checked) {
			w3Checkbox.setChecked(checked);
		}
		
		public Number getPosicao() {
			return posicao;
		}
		
		public void setBoxTxt(String boxTxt) {
			meiosOutrosDesc.setValue(boxTxt);
		}

		@Override
		public String getPK() {
			return chave.getIdentDocumentoDeclaracao().toString();
		}
		
	}
	


	public void setTabReadonly(boolean readOnly) {
		this.readOnly = readOnly;
		
		for(MeioAferidoItem item : listaMeiosAferidos) {
			item.setDisabled(readOnly);
		}
		
		datePanel.setReadonly(readOnly);
		observacoesPanel.setReadonly(readOnly);
	}
	
	public void setReadonly(boolean readOnly) {
		setTabReadonly(readOnly);
	}
	
}





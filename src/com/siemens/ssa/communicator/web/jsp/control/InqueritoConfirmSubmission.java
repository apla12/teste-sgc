package com.siemens.ssa.communicator.web.jsp.control;

import org.apache.click.control.FieldSet;

import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.W3Label;
import pt.atos.web.click.controls.field.W3RadioGroup;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.page.DgitaLayoutPage;


/**
 * Inquérito para confirmar submissão do controlo
 * 
 */
public class InqueritoConfirmSubmission extends DgitaLayoutPage {

	private static Log log = Log.getLogger(InqueritoConfirmSubmission.class);
	private static final long serialVersionUID = -1L;

	/**
	 * Variavel para controlar na jsp através de velocity o fecho da janela.
	 */
	public boolean closeWindow = false;
	public String returnVal = "";
	
	public InqueritoConfirmSubmission() {
		super();
	}

	public boolean subm_adition() {
		
		if(form.isValid()){
			closeWindow = true;
		}else{
			showInfoMessage("");
		}

		return form.isValid();
	}

	@Override
	public void setFormData() {}

	@Override
	public void getFormData() {}

	@SuppressWarnings("boxing")
	@Override
	protected void buildPage() {

		form.setValidate(true);
		
		String show="inquerito_textoInicial";
		String breadcrumb="page.breadcrumb";
		
		String idControlo = getContext().getRequest().getParameter("idControlo");
		String controlresult = getContext().getRequest().getParameter("controlresult");
		
		breadCrumbPath=getMessageNoException(breadcrumb);
		showInfoMessageFromKey(show);
		
		ExpandableFieldSetPanel fsGeral = new ExpandableFieldSetPanel("geralConfirmar", "Confirmar o resultado do controlo:");
		fsGeral.setLabel("confirm");
		fsGeral.setShowBorder(false);
		fsGeral.setForm(form);
		fsGeral.setColumns(1);
		form.add(fsGeral);
		
		
		W3Label verificacaoLabel = new W3Label();
		verificacaoLabel.setId("send_confirm_label");
		verificacaoLabel.setName("Confirm");
		verificacaoLabel.setLabel("Deseja submeter o controlo com " + controlresult + "?");
		fsGeral.add(verificacaoLabel);
		

		W3Submit bt1 = new W3Submit("btnConfirmar", null, this, "subm_adition");
		// Voltar fecha a página
		W3Submit bt2 = new W3Submit("btnCancelar", null, this, "subm_adition");

		bt1.setParent(this);
		bt2.setParent(this);
	}

	@Override
	protected void postInit() {
		setFormData();
	}

	@Override
	public boolean onCancelar() {
		return false;
	}

	@Override
	public boolean onGravar() {
		return false;
	}
}
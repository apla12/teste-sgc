package com.siemens.ssa.communicator.web.jsp.control.pojo;

import pt.atos.util.presentation.TableItem;

import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumento;

import pt.atos.sgccomunicator.utils.SGCConstantes;

import com.siemens.ssa.communicator.web.jsp.control.ControlResult;

import pt.atos.web.click.controls.field.W3Checkbox;
import pt.atos.web.click.page.DgitaLayoutPage;

/**
 * devido à necessidade de separação controlo do documento pojo da apresentacao
 *
 */
public class ControloDocumentoWeb extends ControloDocumento implements TableItem{
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 4868095483683474301L;
	
	public DgitaLayoutPage pg;
	
	public boolean conferido;
	
	public String ficheiro;
	
	public boolean read;

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * Para colocar uma checkbox na coluna da tabela, 
	 * 
	 * dissociando assim a apresentacao do pojo
	 * 
	 * @author Ricardo Peres
	 * 
	 * @return
	 */
	public String getConferido(){

		if(getConferidorDocumento()==null){
			
			return null;
		}
		else{
			
			W3Checkbox conf = new W3Checkbox("control_doc_"+getNumAdicao()+"_virt_"+getIndVirtual()+"_chk_"+getChave().getIdentDocumento());
			conf.setLabel("");
			conf.setAttribute("class", "checkbox-adicoes");
			conf.setAttribute("onClick", "marcaValorUnico(this.id)");
			if(getConferidorDocumento()!=null){
				if(SGCConstantes.FLAG_BD_VERDADEIRO.equals(getConferidorDocumento())){
					conf.setValue("ok");
					conf.setChecked(true);
				}
			}
			else{
				conf.setValue("");
				conf.setAttribute("onClick","selectedValues_control_doc_docsTable=new Array();pushValue('control_doc_docsTable',"+getChave().getIdentDocumento()+");ajaxEditTableWithSelection( 'PhyControlDoc','control_doc_docsTable',"+pg.getContextPath()+pg.getContext().getPagePath(ControlResult.class)+");clearSelectedValuesList('control_doc_docsTable');");
				conf.setChecked(false);
			}
			
			if(this.isRead()){
				conf.setDisabled(true);
			}
			return conf.toString();	
		}
	}


	
	
	public void toControloDocumento(ControloDocumento original) {
	
		this.setChave(original.getChave());
		this.setCodigoDocumento(original.getCodigoDocumento());
		this.setConferidorDocumento(original.getConferidorDocumento());
		this.setNumAdicao(original.getNumAdicao());
		this.setIndVirtual(original.getIndVirtual());
		this.setDescricaoDocumento(SGCConstantes.TAB+original.getDescricaoDocumento());
	}
//
//	@Override
//	public String getPK() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//	

	public String getFicheiro() {
		return getFilenetLink();
	}
}


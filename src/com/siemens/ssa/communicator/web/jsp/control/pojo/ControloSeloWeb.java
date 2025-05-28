package com.siemens.ssa.communicator.web.jsp.control.pojo;

import com.siemens.ssa.communicator.pojo.interfaces.ControloSelo;
import com.siemens.ssa.communicator.web.jsp.control.ControlResult;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.presentation.TableItem;
import pt.atos.web.click.controls.field.W3Checkbox;
import pt.atos.web.click.page.DgitaLayoutPage;

public class ControloSeloWeb extends ControloSelo implements TableItem{
	
public DgitaLayoutPage pg;
	
	public boolean conferido;
	
	public boolean read;

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
	
	public String getConferido(){

		if( getIndicadorSelo() == null ){
			
			return null;
			
		} else {
			
			W3Checkbox conf = new W3Checkbox("control_selo_"+getChave().getNumItem()+"_chk_"+getChave().getIdSelo());
			conf.setLabel("");
			conf.setAttribute("class", "checkbox-selos");
			conf.setAttribute("onClick", "marcaValorUnicoSelo(this.id, this.checked)");
			if(getIndicadorSelo()!=null){
				if(SGCConstantes.FLAG_BD_VERDADEIRO.equals(getIndicadorSelo())){
					conf.setValue("ok");
					conf.setChecked(true);
				}
				else{
					conf.setValue("");
					conf.setChecked(false);
				}
			}
			
			if(this.isRead()){
				conf.setDisabled(true);
			}
			return conf.toString();	
		}
	}

}

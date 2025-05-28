package com.siemens.ssa.communicator.web.jsp.access.application.control;

import java.io.Serializable;

import pt.atos.util.presentation.TableItem;

public class StatusView implements TableItem,Serializable{
	
	private String descricao;
	private int count;
	
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String getPK() {
		
		return this.descricao;
	}	
}
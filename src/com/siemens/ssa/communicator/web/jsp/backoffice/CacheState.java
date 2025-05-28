package com.siemens.ssa.communicator.web.jsp.backoffice;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.click.control.Column;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import pt.atos.util.cache.CacheResultados;
import pt.atos.util.date.DateUtil;
import pt.atos.util.presentation.TableItem;
import pt.atos.util.string.ToStringUtils;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.table.DateFormatColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class CacheState extends DgitaLayoutPage {
	
	
	public DgitaTable cache = new DgitaTable("cache");
	
	public class ObjectoCache implements TableItem{
		private String chave;
		private Date dataInsercao;
		private String valor;
		private Long hits;
		
		/**
		 * @return the chave
		 */
		public String getChave() {
			return chave;
		}
		/**
		 * @param chave the chave to set
		 */
		public void setChave(String chave) {
			this.chave = chave;
		}
		/**
		 * @return the dataInsercao
		 */
		public Date getDataInsercao() {
			return dataInsercao;
		}
		/**
		 * @param dataInsercao the dataInsercao to set
		 */
		public void setDataInsercao(Date dataInsercao) {
			this.dataInsercao = dataInsercao;
		}
		/**
		 * @return the valor
		 */
		public String getValor() {
			return valor;
		}
		public ObjectoCache(String chave, Date dataInsercao, String valor) {
			super();
			this.chave = chave;
			this.dataInsercao = dataInsercao;
			this.valor = valor;
		}
		/**
		 * @param valor the valor to set
		 */
		public void setValor(String valor) {
			this.valor = valor;
		}
		@Override
		public String getPK() {
			return chave;
		}
		public Long getHits() {
			return hits;
		}
		public void setHits(Long hits) {
			this.hits = hits;
		}
	}
	
	@Override
	protected void buildPage() {
		
		
		Column column0 = new Column("chave", "Chave");
		cache.addColumn(column0);
		DateFormatColumn column1 = new DateFormatColumn("dataInsercao", "Data");
		column1.setFormat(DateUtil.DATETIME_FORMAT);
		Column column2 = new Column("hits", "Hits");
		cache.addColumn(column1);
		cache.addColumn(new Column("valor", "Valor"));
		cache.addColumn(column2);
		
		cache.setCanChangeNumberOfResults(false);
		cache.setPageSize(5);
		
		HashMap<String, Object[]> map = CacheResultados.getMapResultados();
		fillTable( map);
		
		W3Button cacheLnk = new W3Button("Limpar"); 
		cacheLnk.setAttribute("class", "fb_button");
		
		cacheLnk.setOnClick("ajaxUpdateDiv('','form_cacheContainer','"+getContextPath()+getContext().getPagePath(CacheState.class)+"?action=Limpar')");
		
		cache.setAllowJscriptFiltering(true);
		cache.addButtonToTable(cacheLnk, null);
		if(StringUtils.equals(getContext().getRequest().getParameter("action"),"Limpar")){
			limpar();
		}
	}

	private void fillTable( HashMap<String, Object[]> map) {
		ArrayList<ObjectoCache> cacheli= new ArrayList<ObjectoCache>();
		if(map!=null){
			Set<String> chaves = map.keySet();
		
			for (Iterator<String> iterator = chaves.iterator(); iterator.hasNext();) {
				String chave = (String) iterator.next();
				Object[] obj = map.get(chave);
				Object[] data = ArrayUtils.remove(obj, 0);
				data = ArrayUtils.remove(data, 1);
				ObjectoCache c = new ObjectoCache(chave, new Date((Long) obj[0]), ToStringUtils.toString(data));
				c.setHits((Long) obj[2]);
				cacheli.add(c);
			}
		}
		cache.setRowList(cacheli);
	}

	public boolean limpar(){
		
		CacheResultados.removeResultados();
		fillTable(CacheResultados.getMapResultados());
		
		return true;
		
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

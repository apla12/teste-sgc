package com.siemens.ssa.communicator.web.jsp.backoffice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.commons.lang.StringUtils;

import pt.atos.util.logging.Log;
import pt.atos.util.presentation.TableItem;
import pt.atos.util.properties.BaseProperties;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;
import pt.atos.web.click.utils.FrameworkUtils;
import pt.atos.web.click.utils.StaticContentUtils;



public class PropertiesPage extends DgitaLayoutPage {
	
	private static final String UPDATE_PROPERTY = "updateProperty";

	private static final long serialVersionUID = 1L;
	
	public Log log = Log.getLogger(PropertiesPage.class);

	public ArrayList<PropertyDto> properties = new ArrayList<PropertyDto>();
	
	public DgitaTable allProperties = new DgitaTable("allProperties");
	
	public boolean displayEditorProp = false;
	
	public W3TextField propertySearch = new W3TextField("propertySearch");
	public W3Button findProp = new W3Button("findProp","Pesquisar");

	public W3TextField property = new W3TextField("property");
	public W3TextField propertyValue = new W3TextField("propertyValue");
	public W3Button changeProp = new W3Button("changeProp","Mudar Propriedade");
	
	
	public class PropertyDto implements TableItem{
		private String nome;
		private String valor;

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getValor() {
			return valor;
		}

		public void setValor(String valor) {
			this.valor = valor;
		}

		public PropertyDto(String nome, String valor) {
			super();
			this.nome = nome;
			this.valor = valor;
		}

		public PropertyDto(String nome) {
			super();
			this.nome = nome;
		}

		@Override
		public String getPK() {
			return null;
		}
	}
	
	@Override
	protected void buildPage() {
		
		Column nome = new Column("nome", "Nome");
		Column valor = new Column("valor", "Valor");
		
		propertySearch.setWidth("300px");
		
		property.setWidth("300px");
		propertyValue.setWidth("300px");

//		if(profileAtos.equals(userProfile)) {
			displayEditorProp = true;
			
			nome.setDecorator(new Decorator() {
				
				@Override
				public String render(Object row, Context context) {
	
					String nome = null;
					String valor = null;
					if(((PropertyDto)row).getNome() != null)
						nome = ((PropertyDto)row).getNome();
					
					if(((PropertyDto)row).getValor() != null)
						valor = ((PropertyDto)row).getValor();
					
					String imgurl = StaticContentUtils.getImageURL(FrameworkUtils.isUserPortal(getContext())) + "img_icone_editar.jpg";
					
					return "<img src='"+imgurl+"' alt='copy' onclick=\"copyProp('"+nome+"', '"+valor+"')\">"+ "  " + nome;
				}
			});
//		}
		
		changeProp.setOnClick("refreshEditorPropriedades(true)");
		
		findProp.setOnClick("if($('propertySearch').value != null && $('propertySearch').value != '') findProperty($('propertySearch').value)");

		allProperties.addColumn(nome);
		allProperties.addColumn(valor);
		
		allProperties.setTableTitle("Propriedades");

	}
	
	@Override
	public void onRender() {
		
		deLog("ajaxOp " + getContext().getRequestParameter("ajaxOp"));
		
		String propSearch = null;
		
		//if ajax call
		//then call setProperty
		if(StringUtils.equals(getContext().getRequestParameter("ajaxOp"),UPDATE_PROPERTY)){
			setProperty();
		} else if(getContext().getRequest().getParameter("searchProp") != null) {	
			propSearch = getContext().getRequest().getParameter("searchProp");
		}

		fillTable(propSearch);
		
		deLog("EditorPropriedades >> Property table loaded!");

	}

	private void fillTable(String searchProp) {
		
		Properties props = new BaseProperties().findAllProperties();
		
		Set<Map.Entry<Object,Object>> set = props.entrySet();
		
		for(Map.Entry entry : set){
			
			String value = entry.getValue().toString();
			
			if(StringUtils.startsWith(value, "[")) {
				char[] charArray = value.toCharArray();
				value = new String(Arrays.copyOfRange(charArray, 1, charArray.length-1));
			}
			
			if(StringUtils.isBlank(searchProp)) {
				properties.add(new PropertyDto(entry.getKey().toString(), value));
			} else if(entry.getKey().toString().contains(searchProp)) {
				properties.add(new PropertyDto(entry.getKey().toString(), value));
			}
		}
		
		Collections.sort(properties, new Comparator<PropertyDto>(){
			@Override
			public int compare(PropertyDto prop1, PropertyDto prop2)
			{
				return prop1.getNome().compareTo(prop2.getNome());
			}
		});
		
		allProperties.setRowList(properties);
		
	}
	
	private void setProperty(){
		
		BaseProperties props = new BaseProperties();
		
		String prop = getContext().getRequestParameter("property");
		String propValue = getContext().getRequestParameter("propertyValue");
		
		deLog("prop: "+ prop);
		deLog("propvalue: " + propValue);

		if(StringUtils.isNotBlank(prop)){
			deLog("Changing " + prop + " from " 
					+ props.getProperty(prop) 
					+ " to " +propValue
					+ "");
			
//			if(StringUtils.isNotBlank(props.getProperty(prop))){
				
				props.setProperty(prop, propValue);
//				deLog("#### Property changed ####");
				showSucessMessage("Alterado/adicionado com sucesso");
//			}else deLog("#### Property not changed ####");
		}
	}
	

	@Override
	protected void getFormData() {
		
	}

	@Override
	protected void setFormData() {
		
	}
	
	private void deLog(String message){
		log.debug("EditorPropriedades >> " + message);
	}

}

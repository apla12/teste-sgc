package com.siemens.ssa.communicator.web.jsp.control;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import org.apache.click.Context;
import org.apache.click.control.Form;
import org.apache.click.control.Option;

import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutro;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutroDet;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutroTipoInterveniente;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutroTipoIntervenientePK;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.util.cache.CacheResultados;
import pt.atos.util.date.DateUtil;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.logging.Log;
import pt.atos.util.presentation.TableItem;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.links.AjaxTableActionLink;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.ControlFieldColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabGeralProcedimentos {
	private static Log log = Log.getLogger(TabGeralProcedimentos.class);
	
	protected Tab geralControlProc;
	protected DgitaTable intervsTable = new DgitaTable("control_intervsTable");
	private ExpandableFieldSetPanel containerProcedures = new ExpandableFieldSetPanel("container_procedures","");
	private DgitaTable procsTable = new DgitaTable("control_procsTable");
	W3Select tipoIrregularidade   = new W3Select("control_tip_irr","Tipo de irregularidade");
	
	public TabGeralProcedimentos(DgitaLayoutPage pg, DeclarationProcessor declarationProcessor, boolean readOnly,Controlo ctrl) {
		geralControlProc = new Tab("geralControlProc");
		int[] flds = {1, 1};
		geralControlProc.setNumberFieldsPerLine(flds);
		
		ControlFieldColumn intervsColumn1 = new ControlFieldColumn("intervining", "");
		intervsColumn1.setAttribute("style", "text-align: left; height: 40px;");
		intervsTable.addColumn(intervsColumn1);
	
		// Esta coluna foi necessaria para criar drops com a Situacao do Interveniente na framework click
//		AnonymousCallColumn column11 = new AnonymousCallColumn("Situacao Int Irr");
//		column11.setCall(new AnonymousCall() {
//            @Override
//            public String getDataContent(Object row, Context context, int rowIndex) {
//                StringBuffer buf = new StringBuffer();
//	                ArrayList<Option> list = null;
//	                String dataAtual = DateUtil.fromDateToString(System.currentTimeMillis());
//	        		list = CacheResultados.getResultado(dataAtual); // Lista de dominios recolhidos pelo webservice do SIIAF e inseridos em cache do SGC
//	        		
//	        		// Criacao das drops o formato do id e name de cada drop tem a configuracao TipoInterv_+rowIndex
//	        		// é necessario para fazermos o get da Drop por cada intervenienete no getFormulario
//	                buf.append("<select id="+"TipoInterv_"+rowIndex+ " name="+"TipoInterv_"+rowIndex);
//	                if(readOnly){ // se a pagina for so de consulta do controlo desabilita a drop
//	                    buf.append(" disabled=\"disabled\" ");
//	                }
//	                buf.append(">");
//	                    // Preenche as drops com os dominios correspondentes do Tipo de interveniente 
//		                for (int i=0; i<list.size(); i++){
//		    				if (list.get(i).getValue().contains(SGCConstantes.DOMINIO_TIPO_SERVIVO_INTERVENIENTE)){
//		    					 if (ctrl.getControloOutroTipoInterveniente() == null) { // caso o controlo ja tenha sido submetido, esta lista vem preenchida
//			    					buf.append("<option value=\""+list.get(i).getValue()+"\" ");
//			    					buf.append(">"+list.get(i).getLabel()+"</option>");
//		    					 }else { // logo só preenche as drops, para consulta, com o que foi selecionado anteriormente e esta registado na SAT_CONTROLO_OUTRO_INFO0 
//		    						 if (ctrl.getControloOutroTipoInterveniente().get(rowIndex).getNValor().contains(list.get(i).getValue())
//		    								 || !StringUtils.isEmpty(ctrl.getControloOutroTipoInterveniente().get(rowIndex).getDominioValor()) && ctrl.getControloOutroTipoInterveniente().get(rowIndex).getDominioValor().contains(list.get(i).getValue())) {
//		    							 buf.append("<option value=\""+list.get(i).getValue()+"\" ");
//		    		    				 buf.append(" SELECTED ");
//		    		    				 buf.append(">"+list.get(i).getLabel()+"</option>");
//		    						 }
//		    					 }
//		    				}
//		                }   
//		            buf.append("</select>");              
//                return buf.toString();
//            }
//        });
//		intervsTable.addColumn(column11);
		intervsTable.setWidth(WebConstants.POPUP_TABLE_SIZE);
		intervsTable.setValShowDetailLink(new DgitaTable.ValidateShowLink() {
			@Override
			public boolean canShow(TableItem tableItem) {
				return ((InterviningData) tableItem).hasProcedures();
			}
		});
		
		if(declarationProcessor.getWidgetVisible(intervsTable.getId())) geralControlProc.addField(intervsTable);		
		containerProcedures.setForm(pg.form);
		containerProcedures.setParent(this);
		containerProcedures.setLegend("Interveniente nº.");
		containerProcedures.setStyle("padding-top","30px");
		containerProcedures.setStyle("display", "none");
		
		if(declarationProcessor.getWidgetVisible(containerProcedures.getId())) geralControlProc.addField(containerProcedures);		
		ControlFieldColumn procsColumn1 = new ControlFieldColumn("procedure", "");
		procsTable.addColumn(procsColumn1);
		procsTable.setWidth(WebConstants.POPUP_TABLE_SIZE);
		procsTable.setShowBanner(false);
		containerProcedures.add(procsTable);
		
	}
	
	public void setFormulario(Controlo ctrl, Form form) {
		
			ArrayList<ControloOutro> listControloOutro = ctrl.getListaControloOutro();
		if(listControloOutro != null) {
			Map<String, InterviningData> mapIntervinings = new LinkedHashMap<String, InterviningData>();
			
			for(ControloOutro controloOutro : listControloOutro) {
				if (controloOutro.getChave().getIndPosicao()!=5) {				
					String id = "" + controloOutro.getChave().getNumeroItem();				
					InterviningData interviningData = mapIntervinings.get(id);
					if(interviningData == null) {
						interviningData = new InterviningData(id);
						mapIntervinings.put(id, interviningData);
					}				
					interviningData.addData(controloOutro.getChave().getIndPosicao(), controloOutro.getNValor());			
					
					List<ControloOutroDet> controloOutroDetalhes = controloOutro.getControloOutroDetalhes();
					if(controloOutroDetalhes!= null && ! controloOutroDetalhes.isEmpty()) {
						List<ProcedureData> ListProcedures = new LinkedList<ProcedureData>();
						
						for(ControloOutroDet controloOutroDet : controloOutroDetalhes) {
							ProcedureData procedureData = new ProcedureData("" + controloOutroDet.getChave().getIdOutroDet());
							procedureData.setProcedure(controloOutroDet.getNValor());
							ListProcedures.add(procedureData);
						}
						
						interviningData.setProceduresList(ListProcedures);
					}
					
			
					intervsTable.setRowList(new ArrayList<InterviningData>(mapIntervinings.values()));
				}
			}
		}
	}
	
	public ArrayList<Controlo> getFormulario(Controlo dau, Form form){
	
		log.info("TabGeralProcedimentos - Get Formulário / Inicio");
		TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		ArrayList<ControloOutroTipoInterveniente> listControloOutro = new ArrayList<ControloOutroTipoInterveniente>();				

		
		 Map m= form.getContext().getRequest().getParameterMap();// Recolhe os parametros de sessão que corresponde à situaçao Interveniente 
		 Set s = m.entrySet();
		 Iterator it = s.iterator();

		 while(it.hasNext()){// Verifica os parametros de sessão

             Map.Entry<String,String[]> entry = (Map.Entry<String,String[]>)it.next();

             String key             = entry.getKey();
             String[] value           = entry.getValue();

             if (key.contains("TipoInterv_")) { // Caso comece por TipoInterv_ é referente ao ID criado no set formulario desta classe
            	 ControloOutroTipoIntervenientePK controloOutroIntPK = new ControloOutroTipoIntervenientePK();
         		 ControloOutroTipoInterveniente controloOutroInt = new ControloOutroTipoInterveniente();
         		 
            	 controloOutroIntPK.setNumeroControlo(dau.getChave().getNumeroControlo());
            	 controloOutroIntPK.setNumeroItem(new Short (key.substring(key.length() - 1))); 
            	 try {
					controloOutroIntPK.setIndPosicao(new Short (srv.getPosicaoSituacaoInterveniente("SituacaoInterveniente","CTR_OUTRO","DLCC2")));
				} catch (ParseException e) {
					e.printStackTrace();
				} // Foi definido que a Situação do interveninete seria a posicao 5 da tabela SAT_CONTOLO_OUTRA_INFO0
            	 controloOutroInt.setChave(controloOutroIntPK);
            	 controloOutroInt.setDominioValor(value[0]);
            	 // Devido ao SIIAF devolver o valor do dominio no formato "dominio;valor" é necessário retirar so o valor
            	 String a = value[0];
            	 String[] cod = a.split(";");
            	 controloOutroInt.setNValor(cod[1]);
            	             	 
            	 listControloOutro.add(controloOutroInt);
            	 
         		} 
          }
             // Adiciona a situacao do interveninete ao controlo, que será posteriormente utilizado na criação da ficha SIIAF ( ControloServiceBean/ finalizaControlo() )
			 dau.setControloOutroTipoInterveniente(listControloOutro);	
		  
	    log.info("TabGeralProcedimentos - Get Formulário / Fim");
		return null;
	}
	
	public void showDetails(InterviningData procedimento) {
		procsTable.setRowList(procedimento.getProceduresList());
		containerProcedures.setLegend("Interveniente nº. " + procedimento.getPK());
		containerProcedures.setStyle("display", "block");
	}
	
	public void onRender(DgitaLayoutPage pg, boolean readOnly) {
		
		String ajaxURL = pg.getContextPath() + pg.getContext().getPagePath(ControlResult.class);
		ajaxURL += "?readOnly=" + readOnly;
		ajaxURL += "#geralControlProc";
		
		AjaxTableActionLink lnk = new AjaxTableActionLink("Consultar", ajaxURL, "consultarProcedimento", "main-form");
		intervsTable.setDetailLink(lnk);
	}
	
	public class InterviningData implements TableItem {
		
		private String pk;
		private Map<Integer, String> interviningData = new TreeMap<Integer, String>();
		private List<ProcedureData> proceduresList = new LinkedList<ProcedureData>();
		
		public InterviningData(String pk) {
			this.pk = pk;
		}
		
		@Override
		public String getPK() {
			return pk;
		}
		
		public void addData(int position, String data) {
			interviningData.put(position, data);
		}
		
		public String getIntervining() {
			StringBuilder strBuilder = new StringBuilder();
						
			for(Entry<Integer, String> entry : interviningData.entrySet()) {
				strBuilder.append(entry.getValue());
			}
			
			return strBuilder.toString();
		}
		
		public void setProceduresList(List<ProcedureData> proceduresList) {
			this.proceduresList = proceduresList;
		}
		
		public List<ProcedureData> getProceduresList() {
			return proceduresList;
		}
		
		public boolean hasProcedures() {
			return proceduresList != null && ! proceduresList.isEmpty();
		}

	}
	
	public class ProcedureData implements TableItem {
		private String pk;
		private String data;
		
		public ProcedureData(String pk) {
			this.pk = pk;
		}
		
		@Override
		public String getPK() {
			return pk;
		}
		
		public void setProcedure(String data) {
			this.data = data;
		}
		
		public String getProcedure() {
			return data;
		}
	}
	
	
}





package com.siemens.ssa.communicator.web.jsp.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.util.ContainerUtils;
import org.apache.click.util.HtmlStringBuffer;
import org.apache.commons.lang.StringUtils;

import com.siemens.security.session.SessionManager;
import com.siemens.security.user.UserInfo;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItemTipo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloMatriz;
import com.siemens.ssa.communicator.pojo.interfaces.ItemEquipamento;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.util.SSACommunicatorUtils;
import com.siemens.ssa.communicator.util.SSAProperties;
import com.siemens.ssa.communicator.util.SessionConstants;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.dao.ChaveDescricao;
import pt.atos.util.date.DateUtil;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.logging.Log;
import pt.atos.util.web.WebControlUtils;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.form.TabGroup;
import pt.atos.web.click.controls.links.AjaxTableActionLink;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.ErrorDIV;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.form.W3Form;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabGeralCabecalhoGeneric extends DgitaLayoutPage {
	private static final long serialVersionUID = 1L;
	public static Log log = Log.getLogger(TabGeralCabecalhoGeneric.class);
	
	public SGCProperties props = new SGCProperties();
	
	public CompleteFieldSetPanel headerPanel = new CompleteFieldSetPanel("control_dau_headerPanel", null, null);
	
	protected ExpandableFieldSetPanel containerTabAdicao;
	
	public TabControlFisico ContPhy = null;			//ITEM_RESULT
	public TabControlAdicao ContAdd = null;			//ITEM_FIS
	public TabItemIrregularidades itemIrreg = null;
	public TabItemMercadoriaIrr itemIrregMerc = null;
	
	public ArrayList<ControloItem> listaControloItem = new ArrayList<ControloItem>(); 
	public Tab ControlDAU;
	public TabItemConseqFinanc itemConseqFinanc = null;
	
	public DgitaTable tabelaAdicoes = new DgitaTable("controlo_Adicoes_tabela");
	
	public ErrorDIV errorDv;
	public TabGroup grupoTabsAdicao;
	
	public String chaveControloAtribuido = "";
	
	public boolean carregaItems=false;
	
	SGCProperties propsSGC = new SGCProperties();
	public Boolean siiafEnable = true;
	public String property = propsSGC.getMainProperty("siiaf.enable");
	
	public boolean gereControloFisico(DgitaLayoutPage tbControl, ControloItem ctrlAdicao, boolean readOnly,Controlo ctrlDec){
		boolean readonlyRetorno = true;
		log.info("gereControloFisico");
				HttpSession session = getSession();
				//ControloDeclaracao ctrlDec=(ControloDeclaracao)session.getAttribute( SessionConstants.RES_CONTROLO_DECLARACAO);
				//Usar circuito para determinar tipo de fase
				//Usar tarefa para determinar validade do utilizador contra a fase
				if(readOnly){
					ContPhy._setReadonly(true);
					readonlyRetorno = true;
				}
				else{
					/**
					 * 
					 * No caso de fase de circuito documental campos do fisico devem estar bloqueados
					 * 
					 * No caso de fase de circuito fisico:
					 * 		* Se user diferente (verificador!=conferente) entï¿½o parte documental tem de estar readOnly
					 * 	    * Se users iguais (verificador==conferente):
					 * 			* Se tipo de controlo adiï¿½ï¿½o documental - parte fisica deve estar readonly
					 * 			* Se tipo de controlo adiï¿½ï¿½o fisico - ambos os separadores devem estar enabled
					 * 
					 * Se tipo de fase CAP:
					 *		* Se tipo de controlo adiï¿½ï¿½o documental - parte fisica deve estar readonly
					 * 		* Se tipo de controlo adiï¿½ï¿½o fisico - ambos os separadores devem estar enabled
					 * 
					 */

					UserInfo user = (UserInfo) session.getAttribute(SessionConstants.USER);
					
					// Comeca sempre como readonly(true)
					ContPhy._setReadonly(true);
					
					if(SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_SPA_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_POSTERIORI_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_IMPEC_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_SIMTEMM_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO_SIMTEMM_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlDec.getTipoControlo().toString())){
						
						if(ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador())){
							ContPhy._setReadonly(false);
							readonlyRetorno = false;
						} else {
							if (user.getUserId().equalsIgnoreCase(ctrlDec.getVerificador())){
								ContPhy._setReadonly(false);
								readonlyRetorno = false;
							}
						}
					}
				}
				return readonlyRetorno;
			}
			
			public void showAdicao(DgitaLayoutPage tbControl, ControloItem ctrlAdicao, boolean readOnly, String idControlo){
				log.info("#showAdicaoTo:"+idControlo);
				HttpSession session = getSession();
				Controlo ctrlDec=(Controlo)session.getAttribute(SessionConstants.RES_CONTROLO+idControlo);
				gereControloFisico(tbControl, ctrlAdicao, readOnly,ctrlDec);
				
				log.info("Resultado controlo Show Adicao: "+ctrlAdicao.getResultadoControlo());
				
				//Desabilita a combo do Controlo Re-Selecï¿½ï¿½o quando o Resultado de controlo atribuï¿½do: 3P
				if(ctrlAdicao!=null && ctrlAdicao.getResultadoControlo()!=null && (SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(ctrlAdicao.getResultadoControlo().toString()) || SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(ctrlAdicao.getResultadoControlo().toString()))){

					((CompleteFieldSetPanel)((Tab)((TabGroup)containerTabAdicao.getField("tabGroupPop")).getField("PhyControlAdd")).getField("control_add_headerPanel")).getField("control_add_comboControloReSelecao").setReadonly(false);
				}
				else{
					
					((CompleteFieldSetPanel)((Tab)((TabGroup)containerTabAdicao.getField("tabGroupPop")).getField("PhyControlAdd")).getField("control_add_headerPanel")).getField("control_add_comboControloReSelecao").setReadonly(true);
				}

				if (property.equals("false")){
					siiafEnable = false;
				}
				
				// Tab Resultado Adicao
				ContAdd.setFormulario(ctrlAdicao, tbControl/*,validateLabelCD(ctrlDec)*/);
				// Tab Parte Fisica
				ContPhy.setFormulario(ctrlAdicao, tbControl.form);
				// Tab Parte Irregularidade
				if (itemIrreg != null) {		
					itemIrreg.setFormulario(ctrlAdicao, tbControl.form, siiafEnable);
				}

				containerTabAdicao.setStyle("display", "block");
				
				String legend="Registar Controlo Adição nº"+ ctrlAdicao.getChave().getNumeroItem();
				if(SGCConstantes.FLAG_BD_VERDADEIRO.equals(ctrlAdicao.getChave().getIndVirtual())){
					legend=legend.concat(" - Virtual");
				}
				containerTabAdicao.setLegend(legend);
			}
			
			public void showErrorMessage(DgitaLayoutPage pg ,String error){
				
				errorDv.setStyle("display", "block");
				errorDv.setError(error);
			}
			
			/**
			 * Funï¿½ï¿½o para fazer colocar no forumlï¿½rio os dados
			 * passados como parï¿½metro
			 * 
			 * @param dec_ - pojo do controlo da declaraï¿½ï¿½o para o preenchimento
			 * @param form - formulï¿½rio da pï¿½gina passado por parï¿½metro
			 */
			public void setFormulario(Controlo dec_, Form form){
				
				SSAProperties props = new SSAProperties();
				
				headerPanel.getField("conferente").setValue(dec_.getConferente());
				headerPanel.getField("verificador").setValue(dec_.getVerificador());
				
				if(headerPanel.getField(chaveControloAtribuido)!=null 
						&& StringUtils.isBlank(headerPanel.getField(chaveControloAtribuido).getValue())
						&& dec_.getTipoControlo()!=null){
					
					headerPanel.getField(chaveControloAtribuido).setValue(dec_.getTipoControlo().toString());	
				}	
				
				
				if(headerPanel.getField("control_dau_comboResultadoControlo")!=null){
//					if ((dec_.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO) || (dec_.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_SPA_COMBO)) && dec_.getSistema().equals(SGCConstantes.SISTEMA_SFA))){

					if ((dec_.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_FISICO_PARCIAL_COMBO) && dec_.getSistema().equals(SGCConstantes.SISTEMA_SFA)))
					{
//						headerPanel.getField("control_dau_comboResultadoControlo").setValue(props.getMainProperty("ssa.resultado.controlo.b1"));
//						headerPanel.getField("control_dau_comboResultadoControlo").setDisabled(true);
					} else {
						if(dec_.getResultadoControlo() != null){
							headerPanel.getField("control_dau_comboResultadoControlo").setValue(dec_.getResultadoControlo().toString());
						}
					}	
					
				}
				if(dec_.getInicioCtrlFisi() != null) {
					headerPanel.getField("item_cabecalho_caus_inicioControlo_label").setValue(dec_.getInicioCtrlFisi().toString());
				}
				if(dec_.getFimCtrlFisi() != null) {			
					headerPanel.getField("item_cabecalho_caus_fimControlo_label").setValue(dec_.getFimCtrlFisi().toString());
				}
				
				
				if(headerPanel.getField("control_dau_txtDadosAdicionais")!=null 
						&& StringUtils.isBlank(headerPanel.getField("control_dau_txtDadosAdicionais").getValue())){
					
					headerPanel.getField("control_dau_txtDadosAdicionais").setValue(dec_.getRequerDadosOperador());	
				}
				if(headerPanel.getField("control_dau_txtResultadoControlo")!=null 
						&& StringUtils.isBlank(headerPanel.getField("control_dau_txtResultadoControlo").getValue())){
					
					headerPanel.getField("control_dau_txtResultadoControlo").setValue(dec_.getMotivoControlo());	
				}
				if(dec_.getSistema().equals(SGCConstantes.SISTEMA_SFA)) {
					if(dec_.getInfoSSA()!=null)
						headerPanel.getField("control_dau_txtObservacoes").setValue(dec_.getInfoSSA().substring(13));
				}
				
				if(dec_.getListaControloItem()!=null){
					ArrayList<ControloItem> listaControloItem = null;
					listaControloItem = (ArrayList<ControloItem>) dec_.getListaControloItem().clone();
					if(dec_.getSistema().equals(SGCConstantes.SISTEMA_IMPEC) || 
							dec_.getSistema().equals(SGCConstantes.SISTEMA_SIMTEMM) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_DLCC2) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_DAIN) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU)||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_TRACAU) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_NR) ||
							dec_.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {
						listaControloItem.remove(0);
					}
					tabelaAdicoes.setRowList(listaControloItem);
				}	
				
				
			}

			/**
			 * 
			 * Mï¿½todo que preenche o POJO com os dados correspondentes no FORM da pï¿½gina
			 * 
			 * */
			public Controlo getFormulario(Controlo dec_, Form form){
				
				if (dec_ == null)
					dec_ = new Controlo();
				
				String combControloAtribuido = "";
				
				if(SGCUtils.validaSistemaCAU(dec_.getSistema())){				
					combControloAtribuido = (String)form.getFieldValue(chaveControloAtribuido);
					log.info("control_dau_comboControloAtribuido:"+form.getFieldValue(chaveControloAtribuido));
					dec_.setPendenteAmostra((String)form.getFieldValue("control_dau_txtPendenteResultadoAmostra"));
				} else {
					combControloAtribuido = (String)form.getFieldValue("control_dau_comboControloAtribuido");
					log.info("control_dau_comboControloAtribuido:"+form.getFieldValue("control_dau_comboControloAtribuido"));
				}
				
				dec_.setTipoControlo(combControloAtribuido);
								
				//Combo Res. Controlo
				log.info("Resultado Controlo: "+form.getFieldValue("control_dau_comboResultadoControlo"));
				String combResultadoControlo =  form.getFieldValue("control_dau_comboResultadoControlo");
				if (StringUtils.isNotBlank(combResultadoControlo)) {
					String [] idResCont = WebControlUtils.separarIdFields(combResultadoControlo);
					dec_.setResultadoControlo(idResCont[0]);
				}else{
					dec_.setResultadoControlo(null);
				}
				
				
				log.info("control_dau_txtDadosAdicionaisToLOG:"+form.getFieldValue("control_dau_txtDadosAdicionais"));
				dec_.setRequerDadosOperador(form.getFieldValue("control_dau_txtDadosAdicionais"));
				dec_.setMotivoControlo(form.getFieldValue("control_dau_txtResultadoControlo"));
//				if(dec_.getSistema().equals("SFA"))
//					if(form.getFieldValue("control_dau_txtObservacoes").length()>0)
//				dec_.setInfoSSA("Observaï¿½ï¿½es: "+form.getFieldValue("control_dau_txtObservacoes"));
				return dec_;
			}

			public void onRender(DgitaLayoutPage tbControl, boolean readOnly, String idControlo) {
		log.info("#onRenderTo:"+idControlo);	

		 		//vai carregar a informacao dos separadores
				HttpSession session = getSession();
				ControloItem ctrlAdicao=(ControloItem)session.getAttribute(SessionConstants.RES_CONTROLO_ADICAO);
				Controlo ctrl=(Controlo)session.getAttribute(SessionConstants.RES_CONTROLO+idControlo);
				String motivoControl =null;		 		
				
				if(ctrlAdicao!=null){
					log.info("Show Adicao On Render");
					showAdicao(tbControl,ctrlAdicao,readOnly,idControlo);
					motivoControl = ctrlAdicao.getMotivoControlo();
				}		
				
				if(carregaItems){
					
					String ajaxURL = tbControl.getContextPath()+tbControl.getContext().getPagePath(ControlResult.class);
					ajaxURL += "?readOnly=" + readOnly;
					
					if(!readOnly){

						AjaxTableActionLink lnk = new AjaxTableActionLink("Tratar", ajaxURL,"tratar_adicao", "main-form");
						tabelaAdicoes.setEditLink(lnk);
					}
					else{
						headerPanel.setReadonly(readOnly);
						
						AjaxTableActionLink lnk = new AjaxTableActionLink("Consultar", ajaxURL,"tratar_adicao", "main-form");
						tabelaAdicoes.setDetailLink(lnk);
					}
					
					ContAdd.onRender(tbControl, readOnly,false,motivoControl);
				}		
				
				TabelasApoioServiceT srv = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
				ArrayList<Option> optionList = new ArrayList<Option>();
				// TODO: Adicionou-se a fase para distinguir os resultados de controlo do DLCC2. Melhorar no futuro
				ArrayList<String> codigoList = SGCConstantes.getResultadoControloCombo(ctrl.getSistema()+"."+ctrl.getTipoControlo(),ctrl.getFase());
				ArrayList<ChaveDescricao> res = new ArrayList<ChaveDescricao>();
				if(ctrl.getSistema().equals(SGCConstantes.SISTEMA_SFA) ||
						ctrl.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
				   res = srv.getDescricaoResultadoControloPorCodigo(codigoList,false,true);
				} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_IMPEC) ||
						   ctrl.getSistema().equals(SGCConstantes.SISTEMA_SIMTEMM) ||
						   ctrl.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS)) {
					res = srv.getDescricaoResultadoControloPorCodigo(codigoList,true,true);
				} else {
					res = null;			
				}
				
				for(int i=0;i<res.size();i++){
					if(i==0 && res.size()>1)
						optionList.add(new Option("","---"));
					optionList.add(new Option(res.get(i).getPK(), res.get(i).getValue()));
				}
				if (optionList.size()==((short)1)){
					headerPanel.getField("control_dau_comboResultadoControlo").setValue(optionList.get(0).getValue());
					headerPanel.getField("control_dau_comboResultadoControlo").setReadonly(true);
				}
				((W3Select)headerPanel.getField("control_dau_comboResultadoControlo")).setOptionList(optionList);
				
			}

			public void registarControlAdicao(DgitaLayoutPage pg,String ajaxOpId, String idControlo, W3Submit submeterControl) {
				log.info("#registarControlAdicaoTo:"+idControlo);

				HttpSession session = getSession();
				Controlo ctrlDec=(Controlo)session.getAttribute(SessionConstants.RES_CONTROLO+idControlo);
				String idAdicao = "";
				//1P = A4
				// Valida que a combo Resultado de controlo está preenchida
				String comboRC = (String)pg.form.getPage().getContext().getRequestParameter("control_add_comboResultadoControlo");	
				boolean erros=false;
				if(!StringUtils.isNotBlank(comboRC) || comboRC.contentEquals("-1")){
					showErrorMessage(pg,"É obrigatório preencher o resultado de controlo da adição!");
					 erros=true;
				}
				// 
				
				if(StringUtils.isNotBlank(ajaxOpId)){
					idAdicao = (ajaxOpId);			
				}		
				else{
					showErrorMessage(pg,"Não foi possivel adicionar o controlo de adições!");
					erros= true;
				}

				ControloItem ctrlAdicao = null;
				ControloItem ctrlAdicaoTemp= null;
				if(ctrlDec.getListaControloItem().size() != 0){
					
					ctrlAdicao = (ControloItem)tabelaAdicoes.getTableItemFromTable(""+idAdicao);
					
					if(ctrlAdicao==null){
						showErrorMessage(pg,"Adição não é válida!");
						return;
					}
					// Vai actualizar a Adicao
					else{			
						
						
						ctrlAdicaoTemp= new ControloItem();
						ctrlAdicaoTemp.setChave(ctrlAdicao.getChave());
						ctrlAdicaoTemp.setResultadoControlo(ctrlAdicao.getResultadoControlo());
						ctrlAdicaoTemp.setTipoControlo(ctrlAdicao.getTipoControlo());
						ctrlAdicaoTemp.setListaControloDocumentoItem(ctrlAdicao.getListaControloDocumentoItem());
						gereControloFisico(pg, ctrlAdicaoTemp, false,ctrlDec);
										
						
						if (property=="false"){
							siiafEnable = false;
						}
						
						ArrayList<ControloMatriz> matriz = (ArrayList<ControloMatriz>) session.getAttribute(SessionConstants.CONTROLO_MATRIZ);
						if (matriz != null){
							
							for (int i=0; i < matriz.size(); i++){		 
								if(matriz.get(i).getCodSeparador().equals("ITEM_RESULT")){
									// Tab Resultad Adicao
									ContAdd.getFormulario(ctrlAdicaoTemp, pg.form/*,validateLabelCD(ctrlDec)*/);
									log.info("Resultado controlo Tab Geral Cabecalho: "+ctrlAdicaoTemp.getResultadoControlo());
									
								}
								else if (matriz.get(i).getCodSeparador().equals("ITEM_FIS")){
									ContPhy.getFormulario(ctrlAdicaoTemp, pg.form);
								}
								else if (matriz.get(i).getCodSeparador().equals("ITEM_IRR")){
									itemIrreg.getFormulario(ctrlAdicaoTemp, ctrlDec.getNumIdentificacao(), pg.form);
								}
								else if (matriz.get(i).getCodSeparador().equals("CTR_CONSEQ_FIN") && siiafEnable){
									//Tab Consequencias Financeiras
									itemConseqFinanc.getFormulario(ctrlAdicaoTemp, pg.form);
								}
								else if (matriz.get(i).getCodSeparador().equals("CTR_IRR_MERC") && siiafEnable){
									//Tab Irregularidade Mercadoria
									itemIrregMerc.getFormulario(ctrlAdicaoTemp, pg.form);
								}
							}
						}
						
						ctrlDec.getListaControloItem().get(ctrlAdicaoTemp.getChave().getNumeroItem()-1).setControloConseqIrrItem(ctrlAdicaoTemp.getControloConseqIrrItem());
						ctrlDec.getListaControloItem().get(ctrlAdicaoTemp.getChave().getNumeroItem()-1).setControloIrregularidadeItem(ctrlAdicaoTemp.getControloIrregularidadeItem());
						
						if(ContainerUtils.getErrorFields(pg.form).size()>0){
							((W3Form)pg.form).setOverrideErrors(true);
							containerTabAdicao.setStyle("display", "block");
							
							containerTabAdicao.setLegend("Registar Controlo Adição nº"+ ctrlAdicao.getChave().getNumeroItem());
							HtmlStringBuffer buffer= new HtmlStringBuffer();
							((W3Form)pg.form).renderErrorsFromFields(buffer);
							showErrorMessage(pg, buffer.toString());
							log.info("Show Adicao registarControlAdicao");
							showAdicao(pg, ctrlAdicaoTemp, false, idControlo);
							return;
						}
						

						
						if (ctrlAdicaoTemp.getResultadoControlo()!=null
								&& ctrlAdicaoTemp.getResultadoControlo().equals("-1")) {
							erros=true;
							grupoTabsAdicao.setTabVisible("PhyControlAdd");
							showErrorMessage(pg,"É necessário preencher o Resultado de controlo no Resultado Adição.");
						}
						
						
						if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())){	
							if(ctrlAdicaoTemp.getResultadoControlo()!=null
									&& SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo().toString())
									&& (ctrlAdicaoTemp.getTipoControloReSelecao()==null || ctrlAdicaoTemp.getTipoControloReSelecao().equals("-1"))){
								erros=true;
								grupoTabsAdicao.setTabVisible("PhyControlAdd");
								showErrorMessage(pg,"É necessário preencher o campo Controlo Re-Seleção no Resultado Adição, quando o Resultado do Controlo é 3P-Proposta de re-seleção.");
							}
							
							if(ctrlAdicaoTemp.getResultadoControlo()!=null
									&& SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo().toString())
									&& StringUtils.isBlank(ctrlAdicaoTemp.getFundamento())){
								erros=true;
								grupoTabsAdicao.setTabVisible("PhyControlAdd");
								showErrorMessage(pg,"É necessário preencher o campo Fundamento, quando o Resultado do Controlo é 3P-Proposta de re-seleção.");
							}
							// Verificar se resultado controlo do identificador foi preenchido
							
							Map<String, List> mapaComControloItemTipo =  (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
							if(mapaComControloItemTipo != null) {
								List<ControloItemTipo> listaDaAdicao = mapaComControloItemTipo.get(ctrlAdicao.getChave().getNumeroControlo() +""+ctrlAdicao.getChave().getNumeroItem());
								if(listaDaAdicao != null && !listaDaAdicao.isEmpty()) {
									boolean resultadoSemPreenchimento = listaDaAdicao.stream().anyMatch(s -> s.getResultado() == null || s.getResultado().equals(""));
									if(resultadoSemPreenchimento) {
										grupoTabsAdicao.setTabVisible("PhyControlAdd");
										showErrorMessage(pg, "O campo resultado do controlo do identificador deve estar preenchido.");
								        erros = true;
									}
								}
							}
							
						    if((!erros) && !validaTipoControlo(pg, session, idControlo, ctrlAdicao)) {
						    	grupoTabsAdicao.setTabVisible("PhyControlAdd");
							    showErrorMessage(pg, "Existem Tipos de Controlo com resultado de controlo diferente do resultado do controlo atribuido na adição.");
							    erros = true;
						    }
											
						} else {
							if(ctrlAdicaoTemp.getResultadoControlo()!=null
									&& SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo().toString())
									&& (ctrlAdicaoTemp.getTipoControloReSelecao()==null || ctrlAdicaoTemp.getTipoControloReSelecao().equals("-1"))){
								erros=true;
								grupoTabsAdicao.setTabVisible("PhyControlAdd");
								showErrorMessage(pg,"É necessário preencher o campo Controlo Re-Seleção no Resultado Adição, quando o Resultado do Controlo é 3P-Proposta de re-seleção.");
							}
							
							if(ctrlAdicaoTemp.getResultadoControlo()!=null
									&& SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo().toString())
									&& StringUtils.isBlank(ctrlAdicaoTemp.getMotivoControlo())){
								erros=true;
								grupoTabsAdicao.setTabVisible("PhyControlAdd");
								showErrorMessage(pg,"É necessário preencher o campo Relatório do Resultado do Controlo, quando o Resultado do Controlo é 3P-Proposta de re-seleção.");
							}		
						}
						
						if(ctrlAdicaoTemp.getResultadoControlo()!=null 
								&&(SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo())
										|| SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo())
										|| SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo())
										|| SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_CAU_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo())
										|| SGCConstantes.RESULTADO_CONTROLO_SIT_PENDENTES_4P_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo()))
								&& StringUtils.isBlank(ctrlAdicaoTemp.getMotivoControlo())){
							erros=true;
							grupoTabsAdicao.setTabVisible("PhyControlAdd");
							showErrorMessage(pg,"É necessário preencher o campo Relatório do Resultado do Controlo.");
						}
						
					
						// Se for CAP, pode nao preencher nenhum campo do Fisico, mas se preencher 1 tera de preencher todos os obrigatorios
						if(SGCConstantes.TIPO_CONTROLO_CAP_COMBO.equals(ctrlAdicao.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlAdicao.getTipoControlo().toString())){
							
							//vamos validar os campos do FISICO
							int count=0;
							if(ctrlAdicaoTemp.getInicioControloFisico() != null)
								count++;
							if(ctrlAdicaoTemp.getFimControloFisico()!=null)
								count++;
							if(StringUtils.isNotBlank(ctrlAdicaoTemp.getTipoVerificacao()))
								count++;
							if(StringUtils.isNotBlank(ctrlAdicaoTemp.getTipoPesagem()))
								count++;
									
							if(count>0 && count<4){
										
								erros=true;
								grupoTabsAdicao.setTabVisible("PhyControlPhy");
								showErrorMessage(pg,"É necessário preencher os campos obrigatórios do Controlo Físico.");
							}
						}
						
						UserInfo user = (UserInfo) session.getAttribute(SessionConstants.USER);
						
						// Se for controlo fisico com conferente = verificador, todos os campos do fisico tem de estar preenchidos
						if(SGCConstantes.TIPO_CONTROLO_FISICO_IMPEC_COMBO.equals(ctrlAdicao.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_FISICO_SPA_IMPEC_COMBO.equals(ctrlAdicao.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_FISICO_SIMTEMM_COMBO.equals(ctrlAdicao.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_NAO_INTRUSIVO_SIMTEMM_COMBO.equals(ctrlAdicao.getTipoControlo().toString())
								|| SGCConstantes.TIPO_CONTROLO_FISICO_TOTAL_CAUS_COMBO.equals(ctrlAdicao.getTipoControlo().toString())){
							if(ctrlAdicaoTemp.getResultadoControlo() != null && ctrlDec.getConferente() != null && ctrlDec.getVerificador() != null && 
									! (SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo().toString())
											|| SGCConstantes.RESULTADO_CONTROLO_RE_SELECAO_CAU_COMBO.equals(ctrlAdicaoTemp.getResultadoControlo().toString()))) {
								
								if(ctrlDec.getConferente().equalsIgnoreCase(ctrlDec.getVerificador()) || 
										user.getUserId().equalsIgnoreCase(ctrlDec.getVerificador())) {
									if(ctrlAdicaoTemp.getInicioControloFisico() == null
											|| ctrlAdicaoTemp.getFimControloFisico() == null
											|| StringUtils.isBlank(ctrlAdicaoTemp.getTipoVerificacao())
											|| StringUtils.isBlank(ctrlAdicaoTemp.getVerificacao())
											|| StringUtils.isBlank(ctrlAdicaoTemp.getTipoPesagem())) {
										erros=true;
										grupoTabsAdicao.setTabVisible("PhyControlPhy");
										showErrorMessage(pg,"É necessário preencher todos os campos obrigatórios do Controlo Físico.");
									}
									
									if(ctrlAdicaoTemp.getInicioControloFisico() != null && ctrlAdicaoTemp.getFimControloFisico() != null &&
											! ctrlAdicaoTemp.getFimControloFisico().after(ctrlAdicaoTemp.getInicioControloFisico())) {
										erros=true;
										grupoTabsAdicao.setTabVisible("PhyControlPhy");
										showErrorMessage(pg,"A data de fim tem de ser depois da data de início do Controlo.");
									}
								}
							}
						}
						
						if(SGCUtils.validaSistemaCAU(ctrlDec.getSistema())){
							// Validações de equipamentos:: quando um identificador sendo adicional ou não, for do tipo 20 ou 30 é necessário que o usuário informe
							// Pelo menos um equipamento para cada linha com essa condição.
							Map<String, List> mapaComControloItemTipo = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
							
							Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo);
													
							List<ItemEquipamento> listaEquipamentos = listEquipamentosMap.values().stream()
									.flatMap(List::stream)
									.collect(Collectors.toList());
							
							boolean temEquipamento = validaEquipamentosCadastrados("20", mapaComControloItemTipo, listaEquipamentos);
							
							if(!temEquipamento) {
								erros = true;
								showErrorMessage(pg,"Pelo menos um equipamento deve ser registado para o tipo controlo 20");
							}
							
							temEquipamento = validaEquipamentosCadastrados("30", mapaComControloItemTipo, listaEquipamentos);
							
							if(!temEquipamento) {
								erros = true;
								showErrorMessage(pg,"Pelo menos um equipamento deve ser registado para o tipo controlo 30");
							}

						    //validação dos equipamentos adicionais (manuais)
						    Map<String, List> mapaComControloItemTipoManual = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
						    Map<String, List<ItemEquipamento>> listEquipamentosManualMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo);
							
						    List<ItemEquipamento> listaEquipamentosManuais = listEquipamentosManualMap.values().stream()
					                .flatMap(List::stream)
					                .collect(Collectors.toList());
						    
						    temEquipamento = validaEquipamentosCadastrados("20", mapaComControloItemTipoManual, listaEquipamentosManuais);
							
							if(!temEquipamento) {
						    	erros = true;
						    	showErrorMessage(pg,"Pelo menos um equipamento deve ser registado para o tipo controlo 20 para Identificadores Adicionais");
						    }
							
							temEquipamento = validaEquipamentosCadastrados("30", mapaComControloItemTipoManual, listaEquipamentosManuais);
							
							if(!temEquipamento) {
						    	erros = true;
						    	showErrorMessage(pg,"Pelo menos um equipamento deve ser registado para o tipo controlo 30 para Identificadores Adicionais");
						    }
						}
					}
				}
				
				if(erros){
					containerTabAdicao.setStyle("display","block");
					
					containerTabAdicao.setLegend("Registar Controlo Adição nº"+ ctrlAdicao.getChave().getNumeroItem());
					
					submeterControl.setDisabled(true);
					
					if(ctrlAdicaoTemp!=null)
					{
						showAdicao(pg, ctrlAdicaoTemp, false, idControlo);
					}
					return;
				}
				
				if(ctrlAdicao!=null)
				{
					if(ctrlDec.getTipoControlo()!=null &&
							(SGCConstantes.TIPO_CONTROLO_CAP_COMBO.equals(ctrlDec.getTipoControlo().toString())
							|| SGCConstantes.TIPO_CONTROLO_CAP.equals(ctrlDec.getTipoControlo().toString()))){
			
						   ctrlAdicao.setTipoControlo(ctrlDec.getTipoControlo());	
						
					}
					
					//grupoTabsAdicao.validate();
					// Vai actualizar a Declaracao
					getFormulario(ctrlDec, pg.form);
					ContAdd.getFormulario(ctrlAdicao, pg.form/*, validateLabelCD(ctrlDec)*/);
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 3_START XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			for(int xx=0;xx<ctrlDec.getListaControloItem().size();xx++){
			log.info("DealAjax#Item:"+ctrlDec.getListaControloItem().get(xx).getChave().getNumeroItem()+";ResControlo:"+ctrlDec.getListaControloItem().get(xx).getResultadoControlo());
			}
			log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 3_END XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			
					ContPhy.getFormulario(ctrlAdicao, pg.form);
					//limpar dados formulario contadd
					ContAdd.setFormulario(null, pg/*, validateLabelCD(ctrlDec)*/);				
					ContPhy.setFormulario(null, pg.form);
					containerTabAdicao.setStyle("display", "none");
					
					session.setAttribute(SessionConstants.RES_CONTROLO+idControlo, ctrlDec);
					session.removeAttribute(SessionConstants.RES_CONTROLO_ADICAO);
					
					SessionManager.getInstance().setSessao(session);
				}
				
				//confirma todos os registros de controlo item tipo em memória
				Map<String, List<ControloItemTipo>> mapControloItemTipoManual = (Map<String, List<ControloItemTipo>>) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
				if (mapControloItemTipoManual != null) {
				    for (List<ControloItemTipo> lista : mapControloItemTipoManual.values()) {
				        if (lista != null) {
				            for (ControloItemTipo item : lista) {
				                item.setFlagConfirmado(SGCConstantes.FLAG_BD_VERDADEIRO);
				            }
				        }
				    }
				    // Atualizar a sessão com o mapa modificado (todos os item confirmados)
				    session.setAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo, mapControloItemTipoManual);
				}
				
				//confirma todos os equipamentos da sessão
				Map<String, List<ItemEquipamento>> mapEquipamento = (Map<String, List<ItemEquipamento>>) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo);
				if (mapEquipamento != null) {
				    for (List<ItemEquipamento> lista : mapEquipamento.values()) {
				        if (lista != null) {
				            for (ItemEquipamento item : lista) {
				            	 item.setFlagConfirmado(SGCConstantes.FLAG_BD_VERDADEIRO);
				            }
				        }
				    }
				    // Atualizar a sessão com o mapa modificado (todos os item confirmados)
				    session.setAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + idControlo, mapEquipamento);
				}
				
				//confirma todos os equipamentos da sessão
				Map<String, List<ItemEquipamento>> mapEquipamentoManual = (Map<String, List<ItemEquipamento>>) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo);
				if (mapEquipamentoManual != null) {
				    for (List<ItemEquipamento> lista : mapEquipamentoManual.values()) {
				        if (lista != null) {
				            for (ItemEquipamento item : lista) {
				            	 item.setFlagConfirmado(SGCConstantes.FLAG_BD_VERDADEIRO);
				            }
				        }
				    }
				    // Atualizar a sessão com o mapa modificado (todos os item confirmados)
				    session.setAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + idControlo, mapEquipamentoManual);
				}
			}
			
			public boolean validaEquipamentosCadastrados(String tipo, Map<String, List> mapaComControloItemTipo, List<ItemEquipamento> listaEquipamentos){
				
				boolean temEquipamento = true;
				
				List<ControloItemTipo> listaTipo = mapaComControloItemTipo.values().stream()
					    .map(obj -> (List<ControloItemTipo>) obj)
					    .flatMap(List::stream) 
					    .filter(item -> item.getTipoControlo().contentEquals(tipo))
					    .collect(Collectors.toList());
				
				if(!listaTipo.isEmpty()) {
					if (mapaComControloItemTipo != null && !mapaComControloItemTipo.isEmpty() && mapaComControloItemTipo.values().stream().anyMatch(list -> !list.isEmpty())) {
						
						if(listaEquipamentos.isEmpty()) {
							temEquipamento = false;
						} else {
							for(List<ControloItemTipo> listControloItemTipo :mapaComControloItemTipo.values()) {
								for(ControloItemTipo cit: listControloItemTipo) {
									//VERIFICA SE EXISTE UM EQUIPAMENTO CADASTRADO CASO O TIPO SEJA 20 OU 30
									if(cit.getTipoControlo().contentEquals(tipo)) {
										temEquipamento = listaEquipamentos.stream().anyMatch(equip -> equip.getChave().getDetItemControlo().toString().equals(cit.getChave().getDetItemControlo().toString()));					    			
									}
								}
							}
						}
					}
				}
				return temEquipamento;
			}

			public void gerirResultadosControlo(DgitaLayoutPage pg,String idControlo) {
				log.info("#gerirResultadosControloTo:"+idControlo);
				/**
				 * Se o tipo de controlo for CAP temos de remover a opï¿½ï¿½o 3P das listas
				 */
				HttpSession session = getSession();
				Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO+idControlo);
				//TODO após a passagem do tipo do controlo para string faz se necessário rever esse trecho abaixo.
				/*if(ctrlDec!=null){
					if(ctrlDec.getTipoControlo().equals(SGCConstantes.TIPO_CONTROLO_CAP_COMBO)){
						List<Option> lista = ((W3Select)headerPanel.getField("control_dau_comboResultadoControlo")).getOptionList();
						Option resOpt = null;
						for (Iterator<Option> iterator = lista.iterator(); iterator.hasNext();) {
							Option opt = (Option) iterator.next();
							if(opt.getValue().equals(SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO))
								resOpt=opt;
						}
						if(resOpt!=null)
							((W3Select)headerPanel.getField("control_dau_comboResultadoControlo")).getOptionList().remove(resOpt);
						
						
						
						List<Option> lista2 = ((W3Select)ContPhy.PhyControlPhy.getField("control_addition_combo1")).getOptionList();
						Option resOpt2 = null;
						for (Iterator<Option> iterator = lista2.iterator(); iterator.hasNext();) {
							Option opt = (Option) iterator.next();
							if(opt.getValue().equals(SGCConstantes.RESULTADO_CONTROLO_RE_SELECCAO_COMBO))
								resOpt=opt;
						}
						if(resOpt2!=null)
							((W3Select)ContPhy.PhyControlPhy.getField("control_addition_combo1")).getOptionList().remove(resOpt2);
						
					}
				}*/
			}
			

	private boolean validateDatesFisico(Controlo ctrlDec, ControloItem ctrlAdicao) {
	
	
			if(ctrlDec.getInfoDatAceita()!=null)
			{
				Date referencia = null;
				
				try {
					SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
					referencia = formato.parse(ctrlDec.getInfoDatAceita());
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				}
		
				return DateUtil.compareTSIntervalReferenceDate(ctrlAdicao.getInicioControloFisico(),ctrlAdicao.getFimControloFisico(),referencia);
			}
			else return false;
	}
			
	private boolean validaTipoControlo(DgitaLayoutPage pg, HttpSession session, String idControlo, ControloItem adicao) {
		
		boolean tipoControloValido = true;
		String resultadoControloAdicao = (String) pg.form.getPage().getContext().getRequestParameter("control_add_comboResultadoControlo");

		if (resultadoControloAdicao != null) {
			log.info("ControlResult: validaTipoControlo-obtemPrioridades ");

			Controlo controlo = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
			String prioridadeAdicao = SSACommunicatorUtils.obtemPrioridadesResControloSSA(resultadoControloAdicao, controlo.getSistema());
	        String prioridadeMaisAlta = null;
	        
	        Map<Short, List> mapaComControloItemTipo =  (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
			List<ControloItemTipo> listaItemTipoAdicao = mapaComControloItemTipo.get(adicao.getChave().getNumeroControlo() +""+adicao.getChave().getNumeroItem());
 
			if (listaItemTipoAdicao != null && listaItemTipoAdicao.size() > 0) {
				for (ControloItemTipo controloItemTipo : listaItemTipoAdicao) {
					Optional<String> resultadoNormalizado = Optional.of(controloItemTipo.getResultado().split("-")[0].trim());
					if(resultadoNormalizado.isPresent()){						
					
						String prioridadeIdentificador = SSACommunicatorUtils.obtemPrioridadesResControloSSA(resultadoNormalizado.get(), controlo.getSistema());
						
						if (prioridadeIdentificador != null && (prioridadeMaisAlta == null || prioridadeIdentificador.compareTo(prioridadeMaisAlta) > 0)) {
							prioridadeMaisAlta = prioridadeIdentificador;
							tipoControloValido = prioridadeIdentificador.compareTo(prioridadeAdicao) == 0; 
						}
						
					}
				}
			}
			
			Map<Short, List> mapaComControloItemTipoManual =  (Map) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + idControlo);
			List<ControloItemTipo> listaItemTipoAdicaoManual = mapaComControloItemTipoManual.get(adicao.getChave().getNumeroControlo() +""+adicao.getChave().getNumeroItem());
 
			if (listaItemTipoAdicaoManual != null && listaItemTipoAdicaoManual.size() > 0) {
				for (ControloItemTipo controloItemTipo : listaItemTipoAdicaoManual) {
					Optional<String> resultadoNormalizado = Optional.of(controloItemTipo.getResultado().split("-")[0].trim());
					if(resultadoNormalizado.isPresent()){						
					
						String prioridadeIdentificador = SSACommunicatorUtils.obtemPrioridadesResControloSSA(resultadoNormalizado.get(), controlo.getSistema());
						
						if (prioridadeIdentificador != null && (prioridadeMaisAlta == null || prioridadeIdentificador.compareTo(prioridadeMaisAlta) > 0)) {
							prioridadeMaisAlta = prioridadeIdentificador;
							tipoControloValido = prioridadeIdentificador.compareTo(prioridadeAdicao) == 0; 
						}
						
					}
				}
			}
		} else {
			tipoControloValido = false;
		}
		return tipoControloValido;
	}
			
			
			
			
	@Override
	protected void buildPage() {
		// TODO Auto-generated method stub
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
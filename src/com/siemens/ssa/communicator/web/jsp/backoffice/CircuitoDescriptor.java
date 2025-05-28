package com.siemens.ssa.communicator.web.jsp.backoffice;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.click.Context;
import org.apache.click.control.Button;
import org.apache.commons.lang.StringUtils;

import com.siemens.retry.entity.Interaccao;
import com.siemens.service.RetryServiceBean;
import com.siemens.service.interfaces.RetryServiceT;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.dao.sat.controloRtr.SAT_CONTROLO_RTR0;
import com.siemens.ssa.communicator.pojo.interfaces.ComunicacoesRTR;
import com.siemens.ssa.communicator.pojo.interfaces.ComunicacoesRTRPK;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItemPK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloPK;

import pt.atos.sgccomunicator.utils.DataSourceFactory;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.util.string.ToStringUtils;
import pt.atos.web.click.page.DgitaLayoutPage;

public class CircuitoDescriptor extends DgitaLayoutPage{

	Log log = Log.getLogger(CircuitoDescriptor.class);
	
	public boolean dataAvailable=true;
	public boolean ctrlAvailable=true;
	public String ID="";
	public boolean show=true;
	public ArrayList<Object[]> obj;
	public ArrayList<ComunicacoesRTR> rtrList;
	public ComunicacoesRTR rtr;
	public Controlo ctrl;
	public ControloItem ctrlItem;
	private DataSource datasource;
	public Button processarCircuito;
	public boolean showLinks=true;
	public String urlPedido;
	public String urlListaRetry;
	
	public CircuitoDescriptor() throws ApplicationException {
		datasource = DataSourceFactory.getJndiDataSource();
	}
	
	@Override
	protected void buildPage() {
		SGCProperties props = new SGCProperties();
		showLinks = StringUtils.equalsIgnoreCase(getContext().getRequest().getParameter("showLinks"),"true");
		if(StringUtils.isNotBlank(ID)){
			TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
			obj = srvInfo.getRTRByNumSequencia(ID);
			
			try {
				if(srvInfo.getRTRD(ID)){
					ID = getContext().getRequest().getParameter("ID");
					urlListaRetry = props.getMainProperty("listaRetry")+ID;
				}
				if(obj.size()<=0){
					obj = null;
					rtrList = srvInfo.getRTR(null,ID,null,null, null, null);
					if(rtrList.size()<=0){
						dataAvailable=false;
						return;
					}
				}			
				
				rtr = new ComunicacoesRTR();
				ctrl = new Controlo();
				ctrlItem = new ControloItem();
				if(obj!=null){
					if(obj.get(0)[0]!=null){
						ComunicacoesRTRPK rtrPK = new ComunicacoesRTRPK(new Long(obj.get(0)[0].toString()));
						rtr.setChave(rtrPK);
					}
					if(obj.get(0)[1]!=null)
						rtr.setTipoComunicacao(obj.get(0)[1].toString());				
					if(obj.get(0)[2]!=null)
						rtr.setEstadoPedido(obj.get(0)[2].toString());
					if(obj.get(0)[3]!=null)
						rtr.setNumAceitacao(obj.get(0)[3].toString());
					if(obj.get(0)[4]!=null)
						rtr.setMsgErro(obj.get(0)[4].toString());
					if(obj.get(0)[5]!=null)
						rtr.setNumeroTentativas(new Short(obj.get(0)[5].toString()));
					if(obj.get(0)[6]!=null)
						rtr.setEmissor(obj.get(0)[6].toString());
					if(obj.get(0)[7]!=null)
						rtr.setReceptor(obj.get(0)[7].toString());
					if(obj.get(0)[8]!=null)
						rtr.setSistOrigem(obj.get(0)[8].toString());
					if(obj.get(0)[9]!=null)
						rtr.setSistDestino(obj.get(0)[9].toString());
					if(obj.get(0)[10]!=null)
						rtr.setNumeroMensagemOriginal(new Long(obj.get(0)[10].toString()));
					if(obj.get(0)[11]!=null){
						ControloPK ctrlPK = new ControloPK(new Long(obj.get(0)[11].toString()));					
						ctrl.setChave(ctrlPK);
					}
					if(obj.get(0)[12]!=null)
						ctrl.setSistema(obj.get(0)[12].toString());
					if(obj.get(0)[13]!=null)
						ctrl.setConferente(obj.get(0)[13].toString());
					if(obj.get(0)[14]!=null)
						ctrl.setVerificador(obj.get(0)[14].toString());
					if(obj.get(0)[15]!=null)
						ctrl.setIdTarefa(obj.get(0)[15].toString());
					if(obj.get(0)[16]!=null)
						ctrl.setMomento(obj.get(0)[16].toString());
					if(obj.get(0)[17]!=null)
						ctrl.setNomeTarefa(obj.get(0)[17].toString());
					if(obj.get(0)[18]!=null)
						ctrl.setAlterar(obj.get(0)[18].toString());
					if(obj.get(0)[11]!=null && obj.get(0)[19]!=null && obj.get(0)[20]!=null){
						ControloItemPK ctrlItemPK = new ControloItemPK(new Long(obj.get(0)[11].toString()), new Short(obj.get(0)[19].toString()), obj.get(0)[20].toString());
						ctrlItem.setChave(ctrlItemPK);
					}
					if(obj.get(0)[21]!=null)
						ctrlItem.setResultadoControlo(obj.get(0)[21].toString());
					if(obj.get(0)[22]!=null)
						ctrlItem.setInfoSSA(obj.get(0)[22].toString());
				} else if(rtrList != null){
					
					if(rtrList.get(0).getChave()!=null){
						rtr.setChave(rtrList.get(0).getChave());
					}
					if(rtrList.get(0).getTipoComunicacao()!=null)
						rtr.setTipoComunicacao(rtrList.get(0).getTipoComunicacao());				
					if(rtrList.get(0).getEstadoPedido()!=null)
						rtr.setEstadoPedido(rtrList.get(0).getEstadoPedido());
					if(rtrList.get(0).getNumAceitacao()!=null)
						rtr.setNumAceitacao(rtrList.get(0).getNumAceitacao());
					if(rtrList.get(0).getMsgErro()!=null)
						rtr.setMsgErro(rtrList.get(0).getMsgErro());
					if(rtrList.get(0).getNumeroTentativas()!=null)
						rtr.setNumeroTentativas(rtrList.get(0).getNumeroTentativas());
					if(rtrList.get(0).getEmissor()!=null)
						rtr.setEmissor(rtrList.get(0).getEmissor());
					if(rtrList.get(0).getReceptor()!=null)
						rtr.setReceptor(rtrList.get(0).getReceptor());
					if(rtrList.get(0).getSistOrigem()!=null)
						rtr.setSistOrigem(rtrList.get(0).getSistOrigem());
					if(rtrList.get(0).getSistDestino()!=null)
						rtr.setSistDestino(rtrList.get(0).getSistDestino());
					if(rtrList.get(0).getNumeroMensagemOriginal()!=null)
						rtr.setNumeroMensagemOriginal(rtrList.get(0).getNumeroMensagemOriginal());
				
					ctrlAvailable=false;

						
						
				}
			} catch (NumberFormatException e) {
				dataAvailable=false;
				log.error(e.getMessage(), e);
			} catch (ParseException e) {
				dataAvailable=false;
				log.error(e.getMessage(), e);
			}
		} else { 
			dataAvailable=false;
		}
	}

	@Override
	protected void getFormData() {}

	@Override
	protected void setFormData() {}

	
}

package com.siemens.ssa.communicator.web.jsp.ListaRetry;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.click.control.Column;
import org.apache.click.control.Form;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.logging.Log;

import com.siemens.retry.constants.RetryConstants;
import com.siemens.retry.entity.Interaccao;
import com.siemens.service.interfaces.RetryServiceT;
import com.siemens.ssa.communicator.retry.operacoes.SSARetryOperacoes;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.panels.PesquisaPanel;
import pt.atos.web.click.controls.table.DateFormatColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.controls.table.MultiplePropertyColumn;
import pt.atos.web.click.page.DgitaSearchPage;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class ListaRetry extends DgitaSearchPage {

	private static Log log = Log.getLogger(ListaRetry.class);
	
	public Interaccao interaccao;
	@Override
	protected void addFieldsToPanel(PesquisaPanel arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRender() {
		if(idCarregar!=null && getContext().getRequest().getParameter("action")!=null &&  getContext().getRequest().getParameter("action").equals("process") ){
			processar();
			setRedirect(ListaRetry.class, null);
			return;
		}else if (idCarregar!=null && getContext().getRequest().getParameter("action")!=null &&  getContext().getRequest().getParameter("action").equals("reset") ){
			reset();
		}else if (idCarregar!=null && getContext().getRequest().getParameter("action")!=null &&  getContext().getRequest().getParameter("action").equals("inspect") ){
			inspect();
		}
			
		super.onRender();
	}
	
	private void inspect() {
log.info("inspect()To:"+idCarregar);
		SSARetryOperacoes ssaRetryOperacoes = new SSARetryOperacoes();
		interaccao = ssaRetryOperacoes.getInformacaoPedido(idCarregar);
		try {
            final Document document = parseXmlFile((String) interaccao.getMensagemEnviada());

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            interaccao.setMensagemEnviada(out.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String temp = StringEscapeUtils.escapeXml((String) interaccao.getMensagemEnviada());
        temp= temp.replace("\n", "<br/>");
        temp=temp.replace(" ", "&nbsp;");
        temp= temp.replace("com.siemens.ssa.communicator.webservices.response.", "");
        temp= temp.replace("controlType", "<b>controlType</b>");
        
		interaccao.setMensagemEnviada(temp);
		
	}

	  private Document parseXmlFile(String in) {
	        try {
	            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            InputSource is = new InputSource(new StringReader(in));
	            return db.parse(is);
	        } catch (ParserConfigurationException e) {
	            throw new RuntimeException(e);
	        } catch (SAXException e) {
	            throw new RuntimeException(e);
	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
	    }


	private void reset() {
		SSARetryOperacoes ssaRetryOperacoes = new SSARetryOperacoes();
//		ssaRetryOperacoes.resetPedido(new long[]{new Long(idCarregar)});
	}

	private void processar() {
		SSARetryOperacoes ssaRetryOperacoes = new SSARetryOperacoes();
		Interaccao inte = ssaRetryOperacoes.getInformacaoPedido(idCarregar);
		if(inte!=null){
			RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
			if(StringUtils.equals(inte.getEstadoPedido(),RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO) 
					|| StringUtils.equals(inte.getEstadoPedido(),RetryConstants.ESTADO_PEDIDO_COM_ERRO)){
				inte = srv.processaInteraccaoRetry(inte);
				srv.updateInteracao(inte);
				if(inte!=null){
					showInfoMessage(inte.getEstadoPedido() +" - "+inte.getMensagemErro());
				}
			}else{
				showInfoMessage("Pedido não pode ser processado de novo. Estado Actual: "+inte.getEstadoPedido() +" - "+inte.getMensagemErro());
			}
		}else{
			showErrorMessage("OOOPS");
		}
	}

	@Override
	protected void fillTable(Form arg0, DgitaTable arg1) {
		SSARetryOperacoes ssaRetryOperacoes = new SSARetryOperacoes();
		arg1.setSelectable(true);
		arg1.setOneRowOnly(true);
		ArrayList<Interaccao> lista = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_SFA, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> lista1 = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_IMPEC, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> lista2 = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_SIMTEMM, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaSimtemVias = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_SIMTEM_VIAS, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaDlcc2 = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_DLCC2, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaDain = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_DAIN, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaExpcau = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_EXPCAU, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaTracau = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_TRACAU, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaTracauDest = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_TRACAUDEST, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaDss = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_DSS, SGCConstantes.SISTEMA_SSA, 30, 200);
		ArrayList<Interaccao> listaNr = ssaRetryOperacoes.getListaSSAPorProcessar(SGCConstantes.SISTEMA_NR, SGCConstantes.SISTEMA_SSA, 30, 200);
		lista.addAll(lista1);
		lista.addAll(lista2);
		lista.addAll(listaSimtemVias);
		lista.addAll(listaDlcc2);
		lista.addAll(listaDain);
		lista.addAll(listaExpcau);
		lista.addAll(listaTracau);
		lista.addAll(listaTracauDest);
		lista.addAll(listaDss);
		lista.addAll(listaNr);
		ArrayList<InteraccaoView> lista3 = new  ArrayList<InteraccaoView>();
		for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
			Interaccao interaccao = (Interaccao) iterator.next();
			lista3.add(new InteraccaoView(interaccao));
		}
	
		arg1.setRowList(lista3);
		
		W3Button consultarDecl = new W3Button("Processar");
		consultarDecl.setOnClick("if(getSelectedValueList('" + arg1.getName() + "').length >0){"
							+" var val = getSelectedValueList('" + arg1.getName() + "');"
							+" window.location.href='"+getContextPath()+"/"+getContext().getPagePath(ListaRetry.class)+"?action=process&idCarregar='+val;"
									+"}");
		arg1.addButtonToTable(consultarDecl, form.getName());
		W3Button resetPedido = new W3Button("Reset");
		resetPedido.setOnClick("if(getSelectedValueList('" + arg1.getName() + "').length >0){"
							+" var val = getSelectedValueList('" + arg1.getName() + "');"
							+" window.location.href='"+getContextPath()+"/"+getContext().getPagePath(ListaRetry.class)+"?action=reset&idCarregar='+val;"
									+"}");
		arg1.addButtonToTable(resetPedido, form.getName());
		W3Button inspectPedido = new W3Button("Inspect");
		inspectPedido.setOnClick("if(getSelectedValueList('" + arg1.getName() + "').length >0){"
							+" var val = getSelectedValueList('" + arg1.getName() + "');"
							+" window.location.href='"+getContextPath()+"/"+getContext().getPagePath(ListaRetry.class)+"?action=inspect&idCarregar='+val;"
									+"}");
		arg1.addButtonToTable(inspectPedido, form.getName());
	}

	@Override
	protected void initTable(DgitaTable arg0) {
		Column c = new Column("numeroSequencial");
		arg0.addColumn(c);
		Column c9 = new Column("TipoComunicacao");
		arg0.addColumn(c9);
		Column c1 = new Column("numeroTentativas");
		arg0.addColumn(c1);
		MultiplePropertyColumn c2 = new MultiplePropertyColumn("Sentido");
		c2.addProperty("sistemaEmissor");
		c2.addProperty("sistemaReceptor");
		c2.setFormat(" <%sistemaEmissor%> -> <%sistemaReceptor%>");
		arg0.addColumn(c2);
		DateFormatColumn c4 = new DateFormatColumn("dataRecepcao","Data Pedido");
		arg0.addColumn(c4);
		DateFormatColumn c5 = new DateFormatColumn("dataEnvio","Data Processamento");
		arg0.addColumn(c5);
		Column c6 = new Column("MensagemErro");
		arg0.addColumn(c6);
		
	}

}

package com.siemens.webservice.status;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.siemens.retry.entity.Interaccao;
import com.siemens.ssa.communicator.retry.operacoes.SSARetryOperacoes;

/**
 * Servlet implementation class ChangeStuff
 */
public class ChangeStuff extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ChangeStuff() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SSARetryOperacoes ssaRetryOperacoes = new SSARetryOperacoes();
		Interaccao inte = ssaRetryOperacoes.getInformacaoPedido("3352");
		inte.setMensagemEnviada("<com.siemens.ssa.communicator.webservices.response.SelectionResult>  <header>" +
				"    <origin>Importacao</origin>    <selectionMoment>A</selectionMoment>    <key>      <idFields>       " +
				" <com.siemens.ssa.communicator.webservices.response.DeclarationField>          <id>casaaa</id>          <value>2010</value> " +
				"       </com.siemens.ssa.communicator.webservices.response.DeclarationField>        <com.siemens.ssa.communicator.webservices.response.DeclarationField> " +
				"         <id>casaab</id>          <value>PT</value>        </com.siemens.ssa.communicator.webservices.response.DeclarationField>       " +
				" <com.siemens.ssa.communicator.webservices.response.DeclarationField>          <id>casaac</id>          <value>000040</value>   " +
				"     </com.siemens.ssa.communicator.webservices.response.DeclarationField>        <com.siemens.ssa.communicator.webservices.response.DeclarationField>   " +
				"       <id>casaad</id>          <value>0000037</value>        </com.siemens.ssa.communicator.webservices.response.DeclarationField> " +
				"       <com.siemens.ssa.communicator.webservices.response.DeclarationField>          <id>casaae</id>          <value>5</value>     " +
				"   </com.siemens.ssa.communicator.webservices.response.DeclarationField>        <com.siemens.ssa.communicator.webservices.response.DeclarationField>   " +
				"       <id>casaaf</id>          <value>01</value>        </com.siemens.ssa.communicator.webservices.response.DeclarationField>       " +
				" <com.siemens.ssa.communicator.webservices.response.DeclarationField>          <id>casaag</id>          <value>00</value>  " +
				"      </com.siemens.ssa.communicator.webservices.response.DeclarationField>      </idFields>    </key>    <controlType>SC</controlType>   " +
				" <responseDate class=\"com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl\">      <year>2010</year>      <month>7</month>      " +
						"<day>27</day>      <timezone>0</timezone>      <hour>10</hour>      <minute>36</minute>      <second>42</second>   " +
						"   <fractionalSecond>0.311</fractionalSecond>    </responseDate>  </header>  <details>    <detail>      " +
						"<com.siemens.ssa.communicator.webservices.response.SelectionResultDetail>        <number>1</number>      " +
						"  <controlType>SC</controlType>        <observations></observations>     " +
						" </com.siemens.ssa.communicator.webservices.response.SelectionResultDetail> </detail> " +
						"</details></com.siemens.ssa.communicator.webservices.response.SelectionResult>");
//		RetryService srv = EJBUtil.getSessionInterface(RetryService.class);
		
		//srv.registarRespostaEmNovaTransaccao("x99",inte.getTipoComunicacao(),"PSC", inte.getNumeroAceitacao(), inte.getMensagemEnviada(), inte.getDataEnvio(), inte.getIdentificacaoMensagem());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

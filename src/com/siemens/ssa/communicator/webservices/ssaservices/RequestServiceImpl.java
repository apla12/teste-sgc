package com.siemens.ssa.communicator.webservices.ssaservices;

import com.siemens.service.interfaces.RetryServiceT;
import com.siemens.ssa.communicator.retry.constants.RetryConstants;
import com.siemens.ssa.communicator.util.SGCUtils;

import pt.atos.sgccomunicator.utils.NumeroAceitacao;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.util.string.ToStringUtils;



/**
 * 
 * Implementação dos vários serviços definidos para invocação do SSA.
 * 
 * A classe gerada automáticamente delega nesta a implementação dos serviços
 * 
 * @author ATOS
 */
public class RequestServiceImpl 
{
	Log log = Log.getLogger(RequestServiceImpl.class);
	
	/**
	 * 
	 * @param declaration
	 * @return 
	 * @throws SelectionException_Exception
	 * @throws STADAException 
	 */
	public String requestSelection(Declaration declaration) throws SelectionException_Exception 
	{
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_PEDIDO_SELECCAO_CONTROLO;
		String sistema = declaration.getHeader().getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(declaration.getHeader().getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
				
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(declaration.getHeader().getKey(), sistema);
		
		// Transforma Declaration numa string XML.
		String xml = SGCUtils.transformSSACommunicatorDeclarationToXml(declaration);

		log.info("requestSelection#"+declaration.getHeader().getOrigin()+";"+sistema+";"+numAceitacao.toFormatoCompleto());
		
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		log.info("SD Declaration: "+ToStringUtils.toString(declaration));
		try {
			srv.registaPedido(tipoComunicacao,
								sistema, 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								SGCUtils.getEstanciaControlo(declaration.getHeader().getSubmitter()), 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_requestSelection:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
		log.info("requestSelection#RESULT_STADA_WS_OK");
		return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	/**
	 * ReSelecao
	 * 
	 * @param declaration
	 * @return 
	 * @throws SelectionException_Exception
	 * @throws STADAException 
	 */
	public String requestReselectionIM(ReselectionIM reselectionIM) throws SelectionException_Exception 
	{
		// Não é utilizado no fluxo do STADAIMPCAU
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_PEDIDO_RESELECCAO_SEM_CONTROLO;
		
		String sistema = reselectionIM.getOrigin();
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(reselectionIM.getKey(), sistema);

		// Transforma Declaration numa string XML.
		String xml = SGCUtils.transformSSAObjectToXml(reselectionIM);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao,
								SGCConstantes.getOriginSystem(reselectionIM.getOrigin()), 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								null, 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_requestReselectionIM:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}

		return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	
	/**
	 * 
	 * @param declarationRectification
	 * @return 
	 * @throws SelectionException_Exception
	 * @throws STADAException 
	 */
	public String requestRectification(DeclarationRectification declarationRectification) throws SelectionException_Exception 
	{
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_PEDIDO_SELECCAO_RECTIFICACAO;	
		
		String sistema = declarationRectification.getHeader().getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(declarationRectification.getHeader().getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(declarationRectification.getHeader().getKey(), sistema);

		// Transforma Declaration numa string XML.
		String xml = SGCUtils.transformSSAObjectToXml(declarationRectification);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao,
								sistema, 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								SGCUtils.getEstanciaControlo(declarationRectification.getHeader().getSubmitter()), 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_requestRectification:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
		return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	/**
	 * 
	 * @param controlResult
	 * @return 
	 * @throws SelectionException_Exception
	 * @throws STADAException 
	 */
	public String notifyControlResult(ControlResult controlResult) throws SelectionException_Exception 
	{
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_NOTIFICACAO_RESULTADO_CONTROLO;
				
		String sistema = controlResult.getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(controlResult.getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(controlResult.getKey(), sistema);
		
		String xml = SGCUtils.transformSSAObjectToXml(controlResult);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao,
								sistema, 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								null, 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_notifyControlResult:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
		return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	/**
	 * 
	 * @param cancellation
	 * @return
	 * @throws SelectionException_Exception
	 * @throws STADAException 
	 */
	public String requestCancellation(Cancellation cancellation) throws SelectionException_Exception 
	{
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_NOTIFICACAO_ANULACAO_DESVIO;
				
		String sistema = cancellation.getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(cancellation.getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(cancellation.getKey(), sistema);

		String xml = SGCUtils.transformSSAObjectToXml(cancellation);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao,
								sistema, 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								null, 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_requestCancellation:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
		return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	/**
	 * 
	 * @param releaseAuthorization
	 * @return
	 * @throws SelectionException_Exception
	 * @throws STADAException 
	 */
	public String notifyReleaseAuthorization(ReleaseAuthorization releaseAuthorization) throws SelectionException_Exception
	{
		log.info("notifyReleaseAuthorization#"+ToStringUtils.toString(releaseAuthorization));
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_NOTIFICACAO_AUTORIZACAO_SAIDA;
				
		String sistema = releaseAuthorization.getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(releaseAuthorization.getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}

		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(releaseAuthorization.getKey(), sistema);
		
		String xml = SGCUtils.transformSSAObjectToXml(releaseAuthorization);	
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao,
								sistema, 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								null, 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_notifyReleaseAuthorization:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
		return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	/**
	 * 
	 * @param reselectionPAC
	 * @return
	 * @throws STADAException 
	 */
	public String requestReselectionPAC(ReselectionPAC reselectionPAC) 
	{
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_PEDIDO_RESELECCAO_CONTROLO;
				
		String sistema = reselectionPAC.getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(reselectionPAC.getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(reselectionPAC.getKey(), sistema);
		
		String xml = SGCUtils.transformSSAObjectToXml(reselectionPAC);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao,
								sistema, 
								SGCConstantes.SISTEMA_SSA, 
								numAceitacao.toFormatoSemVersao(), 
								false, null, 
								numAceitacao.toFormatoCompleto(), 
								null, 
								xml, true, false, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_requestReselectionPAC:",e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}

		return SGCConstantes.RESULT_STADA_WS_OK;
	}
}
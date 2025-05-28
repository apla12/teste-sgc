package com.siemens.ssa.communicator.webservices.response.impl;

import pt.atos.sgccomunicator.utils.NumeroAceitacao;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.util.string.ToStringUtils;

import com.siemens.service.interfaces.RetryServiceT;
import com.siemens.ssa.communicator.retry.constants.RetryConstants;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.webservices.interfaces.ResponseHandler;
import com.siemens.ssa.communicator.webservices.response.CAPList;
import com.siemens.ssa.communicator.webservices.response.ComunicationResult;
import com.siemens.ssa.communicator.webservices.response.RectificationResult;
import com.siemens.ssa.communicator.webservices.response.RenominationList;
import com.siemens.ssa.communicator.webservices.response.ReselectionResult;
import com.siemens.ssa.communicator.webservices.response.ResultRegistControl;
import com.siemens.ssa.communicator.webservices.response.SelectionException_Exception;
import com.siemens.ssa.communicator.webservices.response.SelectionResult;
import com.siemens.ssa.communicator.webservices.response.SelectionResultDetails;
import com.siemens.ssa.communicator.webservices.response.SelectionResultHeader;
import com.siemens.ssa.communicator.webservices.response.ValidateAtribControl;

/**
 * 
 * Implementação dos vários serviços definidos para resposta do SSA.
 * 
 * A classe gerada automáticamente delega nesta a implementação dos serviços
 * 
 * @author ATOS
 * 
 */
public final class ResponseHandlerImpl implements ResponseHandler 
{
	private static Log log = Log.getLogger(ResponseHandlerImpl.class); 
	
	/* (non-Javadoc)
	 * @see com.siemens.ssa.communicator.webservices.response.impl.ResponseHandler#selectionResult(com.siemens.ssa.communicator.webservices.response.SelectionResult)
	 */
	public String selectionResult(SelectionResult selectionResult) throws SelectionException_Exception, ApplicationException 
	{
		SGCUtils.setupLogging(SGCConstantes.getOriginSystem(selectionResult.getHeader().getOrigin()));
		SGCUtils.setupLogTiming();
		log.info("SGCResponse_selectionResult(): "+ToStringUtils.toString(selectionResult));
		
		String sistema = selectionResult.getHeader().getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(selectionResult.getHeader().getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(selectionResult.getHeader().getKey(), sistema);
		if(numAceitacao!=null){
			if (numAceitacao.getMrn() != null) {
				log.info("SGCResponse_selectionResultTO:"+numAceitacao.getNumeroSequencial());
			}					
		}
		// TODO: DAIN - ver estancia exclusivo DAIN 
		String estancia = null;
		if (sistema.equals(SGCConstantes.SISTEMA_DAIN)){
			estancia = "000";	
		} else {
			if (numAceitacao.getMrn() != null) {
				estancia = numAceitacao.getEstanciaSemPais().substring(numAceitacao.getEstanciaSemPais().length()-3);	
			}
		}	
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_RESULTADO_SELECCAO_CONTROLO;		
		String xmlSelectionResult = SGCUtils.transformSSASelectionResultToXml(selectionResult);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao, 
								SGCConstantes.SISTEMA_SSA, 
								sistema, 
								numAceitacao.toFormatoSemVersao(), 
								true, null, 
								numAceitacao.toFormatoCompleto(), 
								estancia, 
								xmlSelectionResult, true, true, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (Exception e) {
			log.error("Erro_selectionResult:",e);
			log.info("SGCResponse_selectionResult()TO:"+numAceitacao.getNumeroSequencial()+";RESPOSTA:"+SGCConstantes.RESULT_STADA_WS_ERRO);
			throw new SelectionException_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
//			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
log.info("SGCResponse_selectionResult()TO:"+numAceitacao.getNumeroSequencial()+";RESPOSTA:"+SGCConstantes.RESULT_STADA_WS_OK);
			return SGCConstantes.RESULT_STADA_WS_OK;
	}
	
	/* (non-Javadoc)
	 * @see com.siemens.ssa.communicator.webservices.response.impl.ResponseHandler#rectificationResult(com.siemens.ssa.communicator.webservices.response.RectificationResult)
	 */
	public String rectificationResult(RectificationResult rectificationResult) throws SelectionException_Exception, ApplicationException 
	{
		SGCUtils.setupLogging(SGCConstantes.getOriginSystem(rectificationResult.getHeader().getOrigin()));
		SGCUtils.setupLogTiming();
		log.info("SGCResponse_rectificationResult()");
		
		String sistema = rectificationResult.getHeader().getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
				// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
				// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(rectificationResult.getHeader().getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(rectificationResult.getHeader().getKey(), sistema);
		String estancia = null;
		if(numAceitacao!=null){
			if (numAceitacao.getMrn() != null) {
				log.info("SGCResponse_rectificationResultTO:"+numAceitacao.getNumeroSequencial());
				estancia = numAceitacao.getEstanciaSemPais().substring(numAceitacao.getEstanciaSemPais().length()-3);
			}else {
				estancia = "000";
			}
		}
		
		
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_RESULTADO_SELECCAO_RECTIFICACAO;
		String xmlSelectionResult = SGCUtils.transformSSAObjectToXml(rectificationResult);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao, 
					  			SGCConstantes.SISTEMA_SSA, 
					  			sistema, 
					  			numAceitacao.toFormatoSemVersao(), 
					  			true, null, 
					  			numAceitacao.toFormatoCompleto(), 
					  			estancia, 
					  			xmlSelectionResult, true, true, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (Exception e) {
			log.error("Erro_rectificationResult:",e);
			log.info("SGCResponse_rectificationResult()TO:"+numAceitacao.getNumeroSequencial()+";RESPOSTA:"+SGCConstantes.RESULT_STADA_WS_ERRO);
			throw new SelectionException_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
			//			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}
log.info("SGCResponse_rectificationResult()TO:"+numAceitacao.getNumeroSequencial()+";RESPOSTA:"+SGCConstantes.RESULT_STADA_WS_OK);
		return SGCConstantes.RESULT_STADA_WS_OK;
	}

	/* (non-Javadoc)
	 * @see com.siemens.ssa.communicator.webservices.response.impl.ResponseHandler#renominationList(com.siemens.ssa.communicator.webservices.response.RenominationList)
	 */
	public String renominationList(RenominationList renominationList) throws SelectionException_Exception 
	{
		SGCUtils.setupLogging(SGCConstantes.getOriginSystem(renominationList.getHeader().getOrigin()));
		SGCUtils.setupLogTiming();
log.info("SGCResponse_renominationList()");
		boolean sucesso=false;
    	// Existem multiplos numeros de aceitacao na lista, colocar a null 
    	// para o registo do retry.
		if(renominationList!=null 
				&& renominationList.getDetail()!=null
				&& renominationList.getDetail().size()>0){
			
			for(int x=0; x<renominationList.getDetail().size(); x++) {
				if(renominationList.getDetail().get(0)!=null){
					
					String sistema = renominationList.getHeader().getOrigin();
					
					// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
					// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
					// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
					if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
						// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
						NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(renominationList.getDetail().get(0).getKey(), sistema);
						// se não vier é SIMTEMVIAS
						if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
							sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
						}
					}
					
					NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(renominationList.getDetail().get(0).getKey(), sistema);
					String estancia = null;
					if(numAceitacao.getMrn()!=null){
						estancia = numAceitacao.getEstanciaSemPais().substring(numAceitacao.getEstanciaSemPais().length()-3); // TODO: rever este TIAGO
					}else {
						estancia ="000" ;
					}
						
					

					String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_LISTA_RENOMEACAO;		
					String tipoComunicacaoInicial = SGCUtils.getTipoComunicacaoInicial(tipoComunicacao);
					String xmlSelectionResult = SGCUtils.transformSSAObjectToXml(renominationList);
					RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
					try {
						srv.registaPedido(tipoComunicacao, 
								  			SGCConstantes.SISTEMA_SSA, 
								  			sistema, 
								  			numAceitacao.toFormatoSemVersao(), 
								  			true, null, 
								  			numAceitacao.toFormatoCompleto(), 
								  			estancia, 
								  			xmlSelectionResult, true, true, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
					} catch (Exception e) {
						log.error("Erro_renominationList:",e);
						throw new SelectionException_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
//						return SGCConstantes.RESULT_STADA_WS_ERRO;
					}
					sucesso=true;
				}
			}	
		}
		if(sucesso){
			return SGCConstantes.RESULT_STADA_WS_OK;	
		}
		else{
			return SGCConstantes.RESULT_STADA_WS_ERRO;	
		}
	}
	
    @Override
	public String capList(CAPList capList) throws SelectionException_Exception 
	{
		SGCUtils.setupLogging(SGCConstantes.getOriginSystem(capList.getHeader().getOrigin()));
		SGCUtils.setupLogTiming();
log.info("SGCResponse_capList()");		
    	// Existem multiplos numeros de aceitacao na lista, colocar a null para o registo do retry.
		if(capList.getDetail().get(0)!=null){
						
			String sistema = capList.getHeader().getOrigin();

			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(capList.getDetail().get(0).getKey(), sistema);
			String estancia = null;
			if(numAceitacao!=null){
				if (numAceitacao.getMrn() != null) {
					log.info("SGCResponse_rectificationResultTO:"+numAceitacao.getNumeroSequencial());
					estancia = numAceitacao.getEstanciaSemPais().substring(numAceitacao.getEstanciaSemPais().length()-3);
				}else {
					estancia = "000";
				}
			}
			
			String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_LISTA_CAP;		
			String tipoComunicacaoInicial = SGCUtils.getTipoComunicacaoInicial(tipoComunicacao);
			String xmlSelectionResult = SGCUtils.transformSSAObjectToXml(capList);
			
			RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
			try {
				srv.registaPedido(tipoComunicacao, 
									SGCConstantes.SISTEMA_SSA, 
									SGCConstantes.getOriginSystem(capList.getHeader().getOrigin()),
									numAceitacao.toFormatoSemVersao(), 
									true, null, 
									numAceitacao.toFormatoCompleto(), 
									estancia, 
									xmlSelectionResult, true,true, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
			} catch (ApplicationException e) {
				log.error("Erro_capList:",e);
				throw new SelectionException_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
//				return SGCConstantes.RESULT_STADA_WS_ERRO;
			}
		}
		return SGCConstantes.RESULT_STADA_WS_OK;
	}
    
	@Override
	public String reselectionResult(ReselectionResult reselectionResult) throws SelectionException_Exception 
	{
		SGCUtils.setupLogging(SGCConstantes.getOriginSystem(reselectionResult.getHeader().getOrigin()));
		SGCUtils.setupLogTiming();
		log.info("SGCResponse_reselectionResult()");
				
		String sistema = reselectionResult.getHeader().getOrigin();
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM normal chave MRN+VERSAO+SEQUENCIA
		// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
		if(sistema.contentEquals(SGCConstantes.SISTEMA_SIMTEMM)) {
			// a unica forma de distinguir entre SIMTEM vs SIMTEMVIAS é se o MRN vier, ou não preenchido
			NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(reselectionResult.getHeader().getKey(), sistema);
			// se não vier é SIMTEMVIAS
			if (numAceitacao.getMrn() == null || numAceitacao.getMrn().isEmpty()) {
				sistema = SGCConstantes.SISTEMA_SIMTEM_VIAS;
			}
		}
		
		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacao(reselectionResult.getHeader().getKey(), sistema);
		String estancia = null;
		if(numAceitacao!=null){
			if (numAceitacao.getMrn() != null) {
				log.info("SGCResponse_rectificationResultTO:"+numAceitacao.getNumeroSequencial());
				estancia = numAceitacao.getEstanciaSemPais().substring(numAceitacao.getEstanciaSemPais().length()-3);
			}else {
				estancia = "000";
			}
		}
		
		String tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_RESULTADO_RESELECCAO_CONTROLO;		
		String tipoComunicacaoInicial = SGCUtils.getTipoComunicacaoInicial(tipoComunicacao);
		String xmlSelectionResult = SGCUtils.transformSSAObjectToXml(reselectionResult);
		
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		try {
			srv.registaPedido(tipoComunicacao, 
								SGCConstantes.SISTEMA_SSA, 
								sistema,
								numAceitacao.toFormatoSemVersao(), 
								true, null, 
								numAceitacao.toFormatoCompleto(), 
								estancia, 
								xmlSelectionResult, true,true, RetryConstants.ESTADO_PEDIDO_NAO_PROCESSADO,null);
		} catch (ApplicationException e) {
			log.error("Erro_reselectionResult:",e);
			throw new SelectionException_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
//			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}		
		
//		ssaRetryService.registarRespostaEmNovaTransaccao("x5",tipoComunicacao,
//													   	 tipoComunicacaoInicial, 
//													   	 numAceitacaoStr, 
//													   	xmlSelectionResult, 
//													   	 new Date(),
//													   	 numAceitacaoComVersaoStr);
		return SGCConstantes.RESULT_STADA_WS_OK;
	}

	@Override
	public String comunicationResult(ComunicationResult comunicationResult)throws SelectionException_Exception {
		// TODO Auto-generated method stub
		return null;
	}	
	@Override
	public String validateControl(ValidateAtribControl validateControl) throws SelectionException_Exception{
		// TODO Auto-generated method stub
		return null;
	}		
	@Override
	public String resultRegistoControl(ResultRegistControl resultRegistoControl) throws SelectionException_Exception{
		// TODO Auto-generated method stub
		return null;
	}	
}
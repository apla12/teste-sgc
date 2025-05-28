package com.siemens.ssa.communicator.web.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;

import pt.atos.sgccomunicator.utils.NumeroAceitacao;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.dao.ChaveDescricao;
import pt.atos.util.date.DateUtil;
import pt.atos.util.logging.Log;
import pt.atos.util.object.ObjectUtil;
import pt.atos.util.string.ToStringUtils;

import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.util.SSACommunicatorUtils;
import com.siemens.ssa.communicator.web.jsp.control.caus.SistemasCLsEnum;
import com.siemens.ssa.communicator.webservices.ssaservices.ControlResult;
import com.siemens.ssa.communicator.webservices.ssaservices.ControlResultDetail;
import com.siemens.ssa.communicator.webservices.ssaservices.DeclarationField;
import com.siemens.ssa.communicator.webservices.ssaservices.DeclarationId;

import pt.atos.sgccomunicator.utils.HeaderTransform;

/**
 * Mï¿½todos utilitï¿½rios para transformar as estruturas
 * do resultado do controlo do STADA-IMP para as estruturas
 * utilizadas pelo webservice do SSA.
 * 
 * @author siemens-alopes
 */
public class ControlResultTransform extends HeaderTransform
{
	static Log	log	= Log.getLogger(ControlResultTransform.class);
    /**
     * Transforma a estrutura BD do Resultado do Controlo da Declaraï¿½ï¿½o do STADA-IMP 
     * na estrutura aceite pelo webservice do SSA.
     * 
     * @param controloDec
     * @param numAceitacao
     * @return
     * @throws DatatypeConfigurationException 
     */

	public static ControlResult transformControloDeclaracaoIntoControlResultSSA(Controlo controloDec,
																				NumeroAceitacao numAceitacao, 
																				String utilizador,
																				boolean isNotificacao) throws DatatypeConfigurationException
	{
		ControlResult controlResultSSA = new ControlResult();
		 
		//########## KEY ##########
		DeclarationId key= new DeclarationId();
		ArrayList<DeclarationField> listaFields = null;
		if(SGCConstantes.SISTEMA_STADA_EXP.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderSTADAEXP(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_STADA_IMP.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderSTADAIMP(numAceitacao, isNotificacao)));
		} 
		else if(SGCConstantes.SISTEMA_SFA.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderSFA(numAceitacao, isNotificacao)));		
		}
		else if(SGCConstantes.SISTEMA_IMPEC.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderIMPEC(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_SIMTEMM.equals(controloDec.getSistema())||SGCConstantes.SISTEMA_SIMTEM_VIAS.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderSIMTEM(controloDec, numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_DLCC2.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderDLCC2(controloDec, numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_DAIN.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderIMPCAU(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_EXPCAU.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderEXPCAU(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_TRACAU.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderTRACAU(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_TRACAUDEST.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderTRACAUDEST(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_DSS.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderDSS(numAceitacao, isNotificacao)));
		}
		else if(SGCConstantes.SISTEMA_NR.equals(controloDec.getSistema())){
			listaFields = new ArrayList<>(Arrays.asList(constroiHeaderNR(numAceitacao, isNotificacao)));
		}
		
		
		List listagemFields = listaFields;
		key.getIdFields().addAll(listagemFields);
		
		controlResultSSA.setKey(key);
        

		
		//########## CONTROLO ##########
//        ArrayList<ChaveDescricao> tipoControlo = SSACommunicatorUtils.traduzTipoControlo(controloDec.getTipoControlo(), null);
//		String tipoControlTraduz = StringUtils.trim(tipoControlo.get(0).getCodigo());
		
		GregorianCalendar dataG = DateUtil.fromDateToGregorianCalendar(new java.sql.Date(System.currentTimeMillis()));
		XMLGregorianCalendar dataXML = DatatypeFactory.newInstance().newXMLGregorianCalendar(dataG);	
		controlResultSSA.setControlDate(dataXML);
		
		// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves e distinguir o SIMTEM internamente por:
		// SIMTEM ou SIMTEMVIAS mas para o exterior é sempre identificado como SIMTEMM.Foi feito o if abaixo para contemplar essa nuance 
		// caso o sistema seja SIMTEMVIAS ele altera a origem para SIMTEMM
		if(controloDec.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS)) {
			controlResultSSA.setOrigin(SGCConstantes.SISTEMA_SIMTEMM);
		} else {
			controlResultSSA.setOrigin(controloDec.getSistema());	
		}
				
        controlResultSSA.setFi(null);
        controlResultSSA.setFia(null);
        
        if(SGCUtils.validaSistemaCAU(controloDec.getSistema())) {
        	controlResultSSA.setControlResultType(controloDec.getResultadoControlo());
        } else {        	
        	controlResultSSA.setControlResultType(SSACommunicatorUtils.traduzResultadoControlo(controloDec.getResultadoControlo()));
        }
        controlResultSSA.setController(utilizador);
        controlResultSSA.setSelectionMoment(controloDec.getMomento());
        
        //########## CONTROLO ADICAO ##########
        //##### SFA2 nao tem adicoes #####
        if (controloDec.getListaControloItem()!=null && controloDec.getListaControloItem().size() > 0 &&
        		!SGCConstantes.SISTEMA_SFA.equals(controloDec.getSistema())){
	        ArrayList<ControlResultDetail> detailList = new ArrayList<ControlResultDetail>(); 
	        for (int i=0; i<controloDec.getListaControloItem().size(); i++)
	        {
		        ControloItem item = controloDec.getListaControloItem().get(i);
		        
		        ControlResultDetail detailSSA = new ControlResultDetail();
				// TODO: DAIN - rever numeroAceitacao
		        String resultadoControlo = i == 0 ? controloDec.getResultadoControlo() : item.getResultadoControlo();
		        //TFS 9414 IMPEC: Erro em declarações com Adições com Controlo e Sem Controlo
		        //devido aos sistemas caus, o resultado de controlo passou para varchar, entao tivemos de trabalhar a variavel quando devolve null
		        if (resultadoControlo == null) {
		            resultadoControlo = controloDec.getResultadoControlo(); // quando é null fica com a resultado da declaracao
		        }
		        if(SGCUtils.validaSistemaCAU(controloDec.getSistema())) {
		        	detailSSA.setControlResultType(resultadoControlo.toString());
		        } else {		        	
		        	detailSSA.setControlResultType(SSACommunicatorUtils.traduzResultadoControlo(resultadoControlo.toString()));
		        }
		        int numAdi = ObjectUtil.castIntegerFromShort(item.getChave().getNumeroItem()).intValue();
		        if(StringUtils.isNotBlank(item.getChave().getIndVirtual())
		        		&& SGCConstantes.FLAG_BD_VERDADEIRO.equals(item.getChave().getIndVirtual())){
		        	numAdi=numAdi+100;
		        }
		        detailSSA.setLine(numAdi);
		        detailSSA.setObservations(item.getMotivoControlo());
		        detailList.add(detailSSA);
	        }
	        controlResultSSA.getControlResultDetail().addAll(detailList);
        }
        
		return controlResultSSA;
	}
	
	
	/**
	 * Obtem a estrutura do Header para os varios objectos
	 * na estrutura aceite pelo webservice do SSA.
	 * 
	 */
	private static DeclarationField[] constroiHeaderSTADAEXP (NumeroAceitacao numAceitacao, boolean isNotificacao){
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[7];
			
			headerId[0]= createDeclarationField(SGCConstantes.EXP_FIELD_ANO, numAceitacao.getAno().toString());
			headerId[1]= createDeclarationField(SGCConstantes.EXP_FIELD_PAIS, numAceitacao.getEstancia().substring(0,2));
			headerId[2]= createDeclarationField(SGCConstantes.EXP_FIELD_ESTANCIA, numAceitacao.getEstancia().substring(2) );
			if (isNotificacao){
				headerId[3] = createDeclarationField(SGCConstantes.EXP_FIELD_NUMERO, SGCConstantes.NUM_SEQUENCIAL_NOTIFICACAO+numAceitacao.getNumeroSequencialSemDigitoControlo());
			}else{
				headerId[3] = createDeclarationField(SGCConstantes.EXP_FIELD_NUMERO,numAceitacao.getNumeroSequencialSemDigitoControlo());
			}
			headerId[4] = createDeclarationField(SGCConstantes.EXP_FIELD_DIGITO, numAceitacao.getDigitoControlo().toString());
			headerId[5] = createDeclarationField(SGCConstantes.EXP_FIELD_VERSAO, numAceitacao.getVersao().toString());
			headerId[6] = createDeclarationField(SGCConstantes.EXP_FIELD_REVISAO, numAceitacao.getRevisao().toString());
		}
		return headerId;	
	}
	
private static DeclarationField[] constroiHeaderSTADAIMP (NumeroAceitacao numAceitacao, boolean isNotificacao){
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[7];
			
			headerId[0]= createDeclarationField(SGCConstantes.IMP_FIELD_ANO, numAceitacao.getAno().toString());
			headerId[1]= createDeclarationField(SGCConstantes.IMP_FIELD_PAIS, numAceitacao.getEstancia().substring(0,2));
			headerId[2]= createDeclarationField(SGCConstantes.IMP_FIELD_ESTANCIA, numAceitacao.getEstancia().substring(2) );
			if (isNotificacao){
				headerId[3] = createDeclarationField(SGCConstantes.IMP_FIELD_NUMERO, SGCConstantes.NUM_SEQUENCIAL_NOTIFICACAO+numAceitacao.getNumeroSequencialSemDigitoControlo());
			}else{
				headerId[3] = createDeclarationField(SGCConstantes.IMP_FIELD_NUMERO,numAceitacao.getNumeroSequencialSemDigitoControlo());
			}
			headerId[4] = createDeclarationField(SGCConstantes.IMP_FIELD_DIGITO, numAceitacao.getDigitoControlo().toString());
			headerId[5] = createDeclarationField(SGCConstantes.IMP_FIELD_VERSAO, numAceitacao.getVersao().toString());
			headerId[6] = createDeclarationField(SGCConstantes.IMP_FIELD_REVISAO, numAceitacao.getRevisao().toString());
		}
		return headerId;
	}

	private static DeclarationField[] constroiHeaderSFA (NumeroAceitacao numAceitacao, boolean isNotificacao){
	
	DeclarationField [] headerId = null;
	
	if(numAceitacao!=null){
		
		headerId = new DeclarationField[7];
		
		headerId[0]= createDeclarationField(SGCConstantes.SFA_FIELD_ANO, numAceitacao.getAno().toString());
		headerId[1]= createDeclarationField(SGCConstantes.SFA_FIELD_PAIS, numAceitacao.getEstancia().substring(0,2));
		headerId[2]= createDeclarationField(SGCConstantes.SFA_FIELD_ESTANCIA, numAceitacao.getEstancia().substring(2) );
		if (isNotificacao){
			headerId[3] = createDeclarationField(SGCConstantes.SFA_FIELD_NUMERO, SGCConstantes.NUM_SEQUENCIAL_NOTIFICACAO+numAceitacao.getNumeroSequencialSemDigitoControlo());
		}else{
			headerId[3] = createDeclarationField(SGCConstantes.SFA_FIELD_NUMERO,numAceitacao.getNumeroSequencialSemDigitoControlo());
		}
		headerId[4] = createDeclarationField(SGCConstantes.SFA_FIELD_DIGITO, numAceitacao.getDigitoControlo().toString());
		headerId[5] = createDeclarationField(SGCConstantes.SFA_FIELD_VERSAO, numAceitacao.getVersao().toString());
		headerId[6] = createDeclarationField(SGCConstantes.SFA_FIELD_REVISAO, numAceitacao.getRevisao().toString());
	}
	return headerId;
}
	
	private static DeclarationField[] constroiHeaderIMPEC(NumeroAceitacao numAceitacao, boolean isNotificacao){
		
	DeclarationField [] headerId = null;
	
	if(numAceitacao!=null){
		
		headerId = new DeclarationField[2];
		
		headerId[0]= createDeclarationField(SGCConstantes.IMPEC_FIELD_MRN, numAceitacao.getMrn());
		headerId[1] = createDeclarationField(SGCConstantes.IMPEC_FIELD_VERSAO, numAceitacao.getVersaoStr());
	}
	return headerId;
}
	
	
	private static DeclarationField[] constroiHeaderSIMTEM (Controlo controloDec, NumeroAceitacao numAceitacao, boolean isNotificacao){
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[5];
			
			headerId[0]= createDeclarationField(SGCConstantes.SIMTEM_FIELD_CONTRAMARCA, controloDec.getContramarca());
			headerId[1]= createDeclarationField(SGCConstantes.SIMTEM_FIELD_NUM_DOC, controloDec.getNumeroDocumento());
			// Devido à implementacao da via area/maritima no SIMTEM tivemos de criar 2 chaves
			if (numAceitacao.getMrn() != null) { // SIMTEM normal chave MRN+VERSAO+SEQUENCIA
				headerId[2]= createDeclarationField(SGCConstantes.SIMTEM_FIELD_MRN, numAceitacao.getMrn());
				headerId[3] = createDeclarationField(SGCConstantes.SIMTEM_FIELD_VERSAO, numAceitacao.getVersaoStr());
				headerId[4] = createDeclarationField(SGCConstantes.SIMTEM_FIELD_SEQUENCIA, numAceitacao.getSequencia());
			} else {// SIMTEMVIAS chave CONTAMARCA+NUMERODOCUMENTO+SEQUENCIA
				headerId[2]= createDeclarationField(SGCConstantes.SIMTEM_FIELD_MRN, "");
				headerId[3] = createDeclarationField(SGCConstantes.SIMTEM_FIELD_VERSAO, numAceitacao.getVersaoStr());
				headerId[4] = createDeclarationField(SGCConstantes.SIMTEM_FIELD_SEQUENCIA, numAceitacao.getSequencia());
			}
			
		}
		return headerId;
	}
	
	private static DeclarationField[] constroiHeaderDLCC2 (Controlo controloDec, NumeroAceitacao numAceitacao, boolean isNotificacao){
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[4];
			
			headerId[0]= createDeclarationField(SGCConstantes.DLCC2_FIELD_ENDOSSO, numAceitacao.getEndosso());
			headerId[1]= createDeclarationField(SGCConstantes.DLCC2_FIELD_VERSAOTIPO, numAceitacao.getVersaoStr());
			headerId[2]= createDeclarationField(SGCConstantes.DLCC2_FIELD_FASE, numAceitacao.getFase());
			headerId[3]= createDeclarationField(SGCConstantes.DLCC2_FIELD_REVISAO, numAceitacao.getRevisaoStr());
		}
		return headerId;
	}
	
	private static DeclarationField[] constroiHeaderIMPCAU(NumeroAceitacao numAceitacao, boolean isNotificacao) {
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[2];
			
			headerId[0]= createDeclarationField(SGCConstantes.DAIN_FIELD_NREFLOCAL, numAceitacao.getMrn());
			headerId[1] = createDeclarationField(SGCConstantes.DAIN_FIELD_VERSAO, numAceitacao.getVersaoStr());
		}
		return headerId;
	}
	
	private static DeclarationField[] constroiHeaderEXPCAU(NumeroAceitacao numAceitacao, boolean isNotificacao) {
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[2];
			
			headerId[0]= createDeclarationField(SGCConstantes.EXPCAU_FIELD_MRN, numAceitacao.getMrn());
			headerId[1] = createDeclarationField(SGCConstantes.EXPCAU_FIELD_VERSAO, numAceitacao.getVersaoStr());
		}
		return headerId;
	}
	
	private static DeclarationField[] constroiHeaderTRACAU(NumeroAceitacao numAceitacao, boolean isNotificacao) {
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[2];
			
			headerId[0]= createDeclarationField(SGCConstantes.TRACAU_FIELD_MRN, numAceitacao.getMrn());
			headerId[1] = createDeclarationField(SGCConstantes.TRACAU_FIELD_VERSAO, numAceitacao.getVersaoStr());
		}
		return headerId;
	}

	private static DeclarationField[] constroiHeaderTRACAUDEST(NumeroAceitacao numAceitacao, boolean isNotificacao) {

		DeclarationField [] headerId = null;

		if(numAceitacao!=null){

			headerId = new DeclarationField[2];

			headerId[0]= createDeclarationField(SGCConstantes.TRACAUDEST_FIELD_MRN, numAceitacao.getMrn());
			headerId[1] = createDeclarationField(SGCConstantes.TRACAUDEST_FIELD_VERSAO, numAceitacao.getVersaoStr());
		}
		return headerId;
	}
	
	private static DeclarationField[] constroiHeaderDSS(NumeroAceitacao numAceitacao, boolean isNotificacao) {
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[2];
			
			headerId[0]= createDeclarationField(SGCConstantes.DSS_FIELD_MRN, numAceitacao.getMrn());
			headerId[1] = createDeclarationField(SGCConstantes.DSS_FIELD_VERSAO, numAceitacao.getVersaoStr());
		}
		return headerId;
	}
	
	private static DeclarationField[] constroiHeaderNR(NumeroAceitacao numAceitacao, boolean isNotificacao) {
		
		DeclarationField [] headerId = null;
		
		if(numAceitacao!=null){
			
			headerId = new DeclarationField[2];
			
			headerId[0]= createDeclarationField(SGCConstantes.NR_FIELD_MRN, numAceitacao.getMrn());
			headerId[1] = createDeclarationField(SGCConstantes.NR_FIELD_VERSAO, numAceitacao.getVersaoStr());
		}
		return headerId;
	}
	
	private static DeclarationField createDeclarationField(String id, String value){
		
		DeclarationField df = new DeclarationField();
		df.setId(id);
		df.setValue(value);
		return df;
	}
	
	
	public static String getSelectMoment(Controlo controloDec){
		
		if(controloDec != null){
			
			if(controloDec.getSistema().equals(SGCConstantes.SISTEMA_STADA_IMP)){
				
				return SGCConstantes.SELECTION_MOMENTO_IMPORTACAO;
			}
			else if(controloDec.getSistema().equals(SGCConstantes.SISTEMA_STADA_EXP)) {
			 
				if(controloDec.getMomento().equals("1")){
					return SGCConstantes.SELECTION_MOMENTO_EXPORTACAO_1;
				}
				else if(controloDec.getMomento().equals("2")){
					return SGCConstantes.SELECTION_MOMENTO_EXPORTACAO_2;
				}
				else if(controloDec.getMomento().equals("3")){
					return SGCConstantes.SELECTION_MOMENTO_EXPORTACAO_3;
				}
				else if(controloDec.getMomento().equals("4")){
					return SGCConstantes.SELECTION_MOMENTO_EXPORTACAO_4;
				}
			}
		}
		return null;
	}
}
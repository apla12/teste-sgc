package com.siemens.sgc.temporizador.webservices.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import com.siemens.service.interfaces.ControloServiceT;
import com.siemens.service.interfaces.RetryServiceT;
import com.siemens.service.interfaces.TarefasService;
import com.siemens.ssa.communicator.dao.sat.controloDAO.SAT_CONTROLO0;
import com.siemens.ssa.communicator.dao.sat.controloDocAdicional.SAT_DOCUMENTO_ADICIONAL0;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoAdicional;
import com.siemens.ssa.communicator.retry.constants.RetryConstants;
import com.siemens.ssa.communicator.util.SGCUtils;

import net.atos.at.gestao.tarefas.entidades.Tarefa;
import net.atos.at.gestao.tarefas.util.Constantes;
import net.atos.at.gestao.tarefas.webservice.TarefaException_Exception;
import pt.atos.sgccomunicator.utils.DataSourceFactory;
import pt.atos.sgccomunicator.utils.NumeroAceitacao;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.SGCProperties;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.ws.client.sgc.temporizador.Exception_Exception;
import pt.atos.ws.client.sgc.temporizador.TimerInput;


public class TemporizadorRequestServiceImpl {

	private static Log log = Log.getLogger(TemporizadorRequestServiceImpl.class);
	private DataSource datasource;
	
	public TemporizadorRequestServiceImpl() throws ApplicationException {
		datasource = DataSourceFactory.getJndiDataSource();
	}

	public String iniciarPararTemporizador(TimerInput input) throws Exception {

		String tipoComunicacao;
		String state = input.getHeader().getState();
		RetryServiceT srv = EJBUtil.getSessionInterface(RetryServiceT.class);
		
		// Verifica se é para arrancar ou parar um temporizador
		if (SGCConstantes.TEMPORIZADOR_START.equals(state) || SGCConstantes.TEMPORIZADOR_START_SEGUNDA_FASE.equals(state)) {
			tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_PEDIDO_TEMPORIZADOR_COMUNICACAO;
		} else if (SGCConstantes.TEMPORIZADOR_STOP.equals(state)) {
			tipoComunicacao = SGCConstantes.TIPO_COMUNICACAO_PEDIDO_TEMPORIZADOR_PARAR;
		} else {
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}

		String sistRecetor = input.getHeader().getOrigin();
		String momento = input.getHeader().getSelectionMoment();

		NumeroAceitacao numAceitacao = SGCUtils.getNumeroAceitacaoTimer(
				(pt.atos.ws.client.sgc.temporizador.TimerId) input.getHeader().getKey(), sistRecetor);
		// Transforma o input numa string XML
		String xml = SGCUtils.transformSSACommunicatorTimerToXml(input);
		String sist = SGCConstantes.getOriginSystem(input.getHeader().getOrigin());

		try {
			// Recupera o serviço de controlo
			ControloServiceT srvCrtl = EJBUtil.getSessionInterface(ControloServiceT.class);
			Controlo controlo = srvCrtl.getControloByNumIdent(numAceitacao.toFormatoCompletoSemBarra());

			// Verifica se existe uma tarefa aberta
			if (!verificarTarefaAberta(controlo, sist)) {
				// Regista a comunicação na tabela SAT_CONTROLO_RTR0 com o estado em erro			
				try {
					srv.registaPedido(tipoComunicacao, sist, SGCConstantes.TIPO_COMUNICACAO_RETRY_SISTEMA_GESTAO_CONTROLO, numAceitacao.toFormatoSemVersao(), false, null,
							numAceitacao.toFormatoCompletoSemBarra(), null, xml, true, false,
							RetryConstants.ESTADO_PEDIDO_COM_ERRO, SGCConstantes.TEMPORIZADOR_ERRO_VALIDA_TAREFAS_ABERTAS);
				} catch (ApplicationException e) {
					log.error("Erro_capList:", e);
					throw new Exception_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
				}
				// Se nenhuma tarefa estiver aberta, retorna erro
				return SGCConstantes.RESULT_STADA_WS_ERRO;
						
			}

			String timerValue = null;
			if (SGCConstantes.TEMPORIZADOR_START.equals(state) || SGCConstantes.TEMPORIZADOR_START_SEGUNDA_FASE.equals(state)) { // Se state for true, verifica se timerValue é null ou
																	// vazio
				if (input.getHeader().getTimer() == null || input.getHeader().getTimer().toString().isEmpty()) {
					// Regista a comunicação na tabela SAT_CONTROLO_RTR0 com o estado em erro			
					try {
						srv.registaPedido(tipoComunicacao, sist, SGCConstantes.TIPO_COMUNICACAO_RETRY_SISTEMA_GESTAO_CONTROLO, numAceitacao.toFormatoSemVersao(), false, null,
								numAceitacao.toFormatoCompletoSemBarra(), null, xml, true, false,
								RetryConstants.ESTADO_PEDIDO_COM_ERRO,SGCConstantes.TEMPORIZADOR_ERRO_TAG_TIMER);
					} catch (ApplicationException e) {
						log.error("Erro_capList:", e);
						throw new Exception_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
					}
					return SGCConstantes.RESULT_STADA_WS_ERRO;
				} else {
					timerValue = input.getHeader().getTimer().toString();
				}
			} else {
				if (input.getHeader().getTimer() != null && !input.getHeader().getTimer().toString().isEmpty()) {
					// Regista a comunicação na tabela SAT_CONTROLO_RTR0 com o estado em erro			
					try {
						srv.registaPedido(tipoComunicacao, sist,  SGCConstantes.TIPO_COMUNICACAO_RETRY_SISTEMA_GESTAO_CONTROLO, numAceitacao.toFormatoSemVersao(), false, null,
								numAceitacao.toFormatoCompletoSemBarra(), null, xml, true, false,
								RetryConstants.ESTADO_PEDIDO_COM_ERRO,SGCConstantes.TEMPORIZADOR_ERRO_TAG_TIMER);
					} catch (ApplicationException e) {
						log.error("Erro_capList:", e);
						throw new Exception_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
					}
					return SGCConstantes.RESULT_STADA_WS_ERRO;
				}
			}
			
			updateEstadoTarefa(numAceitacao, sist, momento, state,controlo);
			
			// Regista a data recebida no campo D_TEMPORIZADOR da tabela SAT_CONTROLO0 se
			// state = 1
			// Se o state for 0 coloca null no campo D_TEMPORIZADOR
			updateTabelaControlo(timerValue, numAceitacao, momento, input, controlo);

			// Regista a comunicação na tabela SAT_CONTROLO_RTR0 com o estado ENVIADO			
			try {
				srv.registaPedido(tipoComunicacao, sist,  SGCConstantes.TIPO_COMUNICACAO_RETRY_SISTEMA_GESTAO_CONTROLO, numAceitacao.toFormatoSemVersao(), false, null,
						numAceitacao.toFormatoCompletoSemBarra(), null, xml, true, false,
						RetryConstants.ESTADO_PEDIDO_ENVIADO,null);
			} catch (ApplicationException e) {
				log.error("Erro_capList:", e);
				throw new Exception_Exception(SGCConstantes.RESULT_STADA_WS_ERRO, null);
			}
		

		} catch (ApplicationException e) {
			log.error("Erro_temporizador:", e);
			return SGCConstantes.RESULT_STADA_WS_ERRO;
		}

		return SGCConstantes.RESULT_STADA_WS_OK;
	}

	private void updateTabelaControlo(String string, NumeroAceitacao numAceitacao, String momento, TimerInput input,
			Controlo controlo) throws ApplicationException, TarefaException_Exception, ParseException {
		
		SAT_DOCUMENTO_ADICIONAL0 daoAdd = new SAT_DOCUMENTO_ADICIONAL0(datasource);
		
		ArrayList<ControloDocumentoAdicional> docs = daoAdd.findByDynamicWhere("I_NUM_CTRL = ? AND D_DATA_PEDIDO IS NOT NULL AND D_DATA_NOTIFICACAO IS NULL", 
		        new Object[]{controlo.getChave().getNumeroControlo()});

		String state = input.getHeader().getState();

		if (SGCConstantes.TEMPORIZADOR_START.equals(state)) {		
			controlo.setTemporizador(input.getHeader().getTimer().toString());
			controlo.setFlagTemporizador(SGCConstantes.TEMPORIZADOR_START);
			
			// Preenche o campo D_DATA_NOTIFICACAO da tabela SAT_DOCUMENTO_ADICIONAL0
			for (ControloDocumentoAdicional docAdicional : docs) {
			      docAdicional.setDataNotificacao(input.getHeader().getTimer().toGregorianCalendar().getTime());
			      daoAdd.update(docAdicional);
			}
			
		} else if (SGCConstantes.TEMPORIZADOR_START_SEGUNDA_FASE.equals(state)) {
			controlo.setTemporizador(input.getHeader().getTimer().toString());
			controlo.setFlagTemporizador(SGCConstantes.TEMPORIZADOR_START_SEGUNDA_FASE);		
		} else if (SGCConstantes.TEMPORIZADOR_STOP.equals(state)) {
			// Coloca a data a null na coluna D_TEMPORIZADOR da tabela SAT_CONTROLO0
			controlo.setTemporizador(null);
			controlo.setFlagTemporizador(SGCConstantes.TEMPORIZADOR_STOP);
		} else {
			new Exception(SGCConstantes.RESULT_STADA_WS_ERRO);
		}

		// Faz update à tabela SAT_CONTROLO0
		SAT_CONTROLO0 ctrl = new SAT_CONTROLO0(datasource, SGCConstantes.DAO_ACTUALIZA_PK_NAO);
		ctrl.update(controlo);

	}

	private boolean verificarTarefaAberta(Controlo controlo, String sistema) throws Exception {
		TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
		boolean tarefaAberta = false;

		// Verifica se a tarefa está aberta para IdTarefaZZ
		if (StringUtils.isNotBlank(controlo.getIdTarefaZZ())) {
			Tarefa tarefa = tarefaSrv.pesquisarTarefa(controlo.getIdTarefaZZ(), sistema);
			tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada)
					|| tarefa.getEstado().equals(Constantes.Eliminada));
			if (tarefaAberta) {
				return true;
			}
		}

		// Verifica se a tarefa está aberta para IdTarefa
		if (StringUtils.isNotBlank(controlo.getIdTarefa())) {
			Tarefa tarefa = tarefaSrv.pesquisarTarefa(controlo.getIdTarefa(), sistema);
			tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada)
					|| tarefa.getEstado().equals(Constantes.Eliminada));
			if (tarefaAberta) {
				return true;
			}
		}

		// Verifica se a tarefa está aberta para IdTarefaFisico
		if (StringUtils.isNotBlank(controlo.getIdTarefaFisico())) {
			Tarefa tarefa = tarefaSrv.pesquisarTarefa(controlo.getIdTarefaFisico(), sistema);
			tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada)
					|| tarefa.getEstado().equals(Constantes.Eliminada));
			if (tarefaAberta) {
				return true;
			}
		}

		// Retorna false se nenhuma tarefa estiver aberta
		return false;
	}

	private void updateEstadoTarefa(NumeroAceitacao numAceitacao, String sist, String momento, String state,
			Controlo controlo) throws ApplicationException, TarefaException_Exception {

		TarefasService tarefaSrv = EJBUtil.getSessionInterface(TarefasService.class);
		// quando o estado chega aqui como Nomeada, a tarefa devera estar em "Nomeada e
		// Pendente de Operador" e ViceVersa
		// Verifica se a tarefa está aberta para IdTarefaZZ
		if (SGCConstantes.TEMPORIZADOR_STOP.equals(state)) {// Nomeada
			String estado = Constantes.Nomeada;
			if (StringUtils.isNotBlank(controlo.getIdTarefaZZ())) {
				Tarefa tarefa = tarefaSrv.pesquisarTarefa(controlo.getIdTarefaZZ(), sist);
				boolean tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada)
						|| tarefa.getEstado().equals(Constantes.Eliminada)
						|| tarefa.getEstado().equals(Constantes.Nomeada));
				if (tarefaAberta) {
					tarefaSrv.atualizarEstadoTarefa(controlo.getIdTarefaZZ(), sist, estado);
				}
			} else if (StringUtils.isNotBlank(controlo.getIdTarefaFisico())) {// Verifica se a tarefa está aberta para
				// IdTarefaFisico
				Tarefa tarefa = tarefaSrv.pesquisarTarefa(controlo.getIdTarefaFisico(), sist);
				boolean tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada)
				|| tarefa.getEstado().equals(Constantes.Eliminada)
				|| tarefa.getEstado().equals(Constantes.Nomeada));
				if (tarefaAberta) {
				tarefaSrv.atualizarEstadoTarefa(controlo.getIdTarefaFisico(), sist, estado);
				}
			} else if (StringUtils.isNotBlank(controlo.getIdTarefa())) { // Verifica se a tarefa está aberta para
					// IdTarefa
					Tarefa tarefa = tarefaSrv.pesquisarTarefa(controlo.getIdTarefa(), sist);
					boolean tarefaAberta = !(tarefa.getEstado().equals(Constantes.Finalizada)
					|| tarefa.getEstado().equals(Constantes.Eliminada)
					|| tarefa.getEstado().equals(Constantes.Nomeada));
					if (tarefaAberta) {
					tarefaSrv.atualizarEstadoTarefa(controlo.getIdTarefa(), sist, estado);
			}
			} else {
				new Exception(SGCConstantes.RESULT_STADA_WS_ERRO);
			}
		} 
	}
}

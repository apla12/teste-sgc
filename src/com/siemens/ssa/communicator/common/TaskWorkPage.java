package com.siemens.ssa.communicator.common;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.siemens.security.session.SessionManager;
import com.siemens.security.user.UserInfo;
import com.siemens.security.utils.LogHelper;
import com.siemens.service.interfaces.ControloDocumentoDetalhesService;
import com.siemens.service.interfaces.ControloDocumentoService;
import com.siemens.service.interfaces.ControloItemServiceT;
import com.siemens.service.interfaces.ControloOutroDetalhesService;
import com.siemens.service.interfaces.ControloOutroService;
import com.siemens.service.interfaces.ControloServiceT;
import com.siemens.service.interfaces.MeioAferidoDetalhesService;
import com.siemens.service.interfaces.MeioAutAferidoService;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.service.interfaces.TarefasService;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumento;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoDet;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoDetPK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoPK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloIrregularidade;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItemPK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloMatriz;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutro;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutroDet;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutroTipoInterveniente;
import com.siemens.ssa.communicator.pojo.interfaces.ControloOutroTipoIntervenientePK;
import com.siemens.ssa.communicator.pojo.interfaces.ControloPK;
import com.siemens.ssa.communicator.pojo.interfaces.MeioAutAferido;
import com.siemens.ssa.communicator.pojo.interfaces.MeioAutAferidoDet;
import com.siemens.ssa.communicator.pojo.interfaces.MeioAutAferidoPK;
import com.siemens.ssa.communicator.pojo.interfaces.TarefaDetails;
import com.siemens.ssa.communicator.util.ObterDadosDeclaracao;
import com.siemens.ssa.communicator.util.SGCUtils;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.SiiafUtils;
import com.siemens.ssa.communicator.util.declproc.DeclarationAttribute;
import com.siemens.ssa.communicator.util.declproc.DeclarationFieldType;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;
import com.siemens.ssa.communicator.web.client.SGCWebClient;
import com.siemens.ssa.communicator.web.client.SGCWebClientUtil;

import net.atos.at.gestao.tarefas.entidades.Tarefa;
import net.atos.at.gestao.tarefas.util.Constantes;
import net.atos.at.gestao.tarefas.webservice.TarefaException_Exception;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.RequestConstants;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.util.object.ObjectUtil;
import pt.atos.util.string.ToStringUtils;
import pt.atos.web.click.controls.panels.Span;
import pt.atos.web.click.page.DgitaLayoutPage;
import pt.atos.web.click.utils.DefaultButtons;

/**
 * NAO ADICIONAR CODIGO ESPECIALIZADO NESTA PAGINA.
 * 
 * O CODIGO DESTA PAGINA SUPORTA AS PAGINAS DE TAREFAS
 * 
 */
public abstract class TaskWorkPage extends DgitaLayoutPage {

	protected Controlo ctrl = null;
	private SGCWebClient clienteweb = null;
	protected String titlePage = null;
	protected TarefaDetails task;
	protected String idControlo = null;
	protected boolean isAutSaida = false;
	protected String linkConsultarDau = null;
	protected String linkVoltar = null;
	public String username;
	public String estancia;
	public String idUser;
	protected DeclarationProcessor declarationProcessor;
	protected LogHelper logHelper;
	boolean carregaAll = true;
	ArrayList<String> listaErros = new ArrayList<>();

	private static Log log = Log.getLogger(TaskWorkPage.class);

	public TaskWorkPage() {
		super();
	}

	public TaskWorkPage(DefaultButtons... buttons) {
		super(buttons);
	}

	@Override
	protected void buildPage() {
		log.info("############### TaskWorkPage - BuildPage ###############");
		try {
			String requestURL = getContext().getRequest().getParameter(SGCWebClientUtil.NOME_ATRIBUTO_URL);
			TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
			HttpSession session = getSession();
			log.info("Request URL: " + requestURL + ";");
			// Recolhe a informação do clienteweb através da descodificacao do url e faz set
			// a variavel global 'clienteweb'
			getClienteWebInfo(requestURL, session);
			// carrega informacao do user para o topo da pagina
			UserInfo userInfo = (UserInfo) getSession().getAttribute(SessionConstants.USER);
			if (userInfo != null) {
				estancia = userInfo.getIdentificaoEstancia();
				username = userInfo.getUserId();
			}
			if (clienteweb != null) {
				idControlo = clienteweb.getIdentificadorControlo();
				log.info("ID Controlo:" + idControlo);
				// Valida se a declaracao é uma Autorização de saída. Preeche a variavel Global
				// 'isAutSaida'
				// true - é uma autorizacao de saida // false: não é autorizacao de saida
				validaAutorizacaoSaida();
				log.info("GetTarefa: ID Controlo: " + idControlo + ", ReadOnly: " + isReadOnly() + ", AutSaida: "
						+ isAutSaida);
				if (StringUtils.isNotBlank(idControlo) && ctrl == null) {
					log.info("IDControlo existe e Ctrl E null");
					Boolean removeControlo = (Boolean) session.getAttribute(SessionConstants.REMOVE_CONTROLO);
					if (removeControlo != null && removeControlo) {
						session.removeAttribute(SessionConstants.REMOVE_CONTROLO);
						session.removeAttribute(SessionConstants.RES_CONTROLO + idControlo);
						SessionManager.getInstance().setSessao(session);
					}
					//TODO: Siaaf
					Boolean onPopupVar = (Boolean) session.getAttribute(SessionConstants.ON_POPUP);
					boolean onPopup = false;
					if (onPopupVar != null && onPopupVar) {
						onPopup = true;
						session.removeAttribute(SessionConstants.ON_POPUP);
						SessionManager.getInstance().setSessao(session);
					}
					ctrl = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);
					// Caso a sessao nao tenha informacao do controlo vai pesquisar a informacao à
					// BD
					if (ctrl == null) {
						log.info("Controlo null");
						getControloInfoBD();
					} else {
						carregaAll = false;
					}
					if (ctrl != null) {
						log.info("Controlo - ID Controlo: " + idControlo + ", Sistema: " + ctrl.getSistema()
								+ ", Nome da tarefa: " + ctrl.getNomeTarefa());
						ctrl.setTituloPage(ctrl.getSistema() + " > " + ctrl.getNomeTarefa());
						// No caso do DLCC2, é necessário o momento, para identificar os campos da
						// matriz para criacao da pagina
						String momento = null;
						if (SGCConstantes.SISTEMA_DLCC2.equals(ctrl.getSistema()) || SGCUtils.validaSistemaCAU(ctrl.getSistema())) {
							momento = ctrl.getMomento();
						}
						// Pesquisa na BD os campos da matriz necessarios para construcao da pagina
						// consuante o sistema
						ArrayList<ControloMatriz> matriz = srvInfo.getMatriz(ctrl.getSistema(), momento);
						session.setAttribute(SessionConstants.CONTROLO_MATRIZ, matriz);
						SessionManager.getInstance().setSessao(session);
						boolean needItems = true;
						boolean needDocxs = false;
						log.info("Matriz size: " + matriz.size());
						// Verifica se a declaracao tem documentos e adicoes através dos campos
						// retornados da matriz
						for (int i = 0; i < matriz.size(); i++) {
							if (matriz.get(i).getCodSeparador().equals("CTR_DOC")) {
								needDocxs = true;
							}
							if (matriz.get(i).getCodSeparador().equals("SEM_ITEM")) {
								needItems = false;
							}
						}
						log.info("Numero de Identificacao: " + ctrl.getNumIdentificacao());
						boolean checkNotDeclaredDocs = false;
						if (SGCConstantes.SISTEMA_IMPEC.equals(ctrl.getSistema())
								|| SGCConstantes.SISTEMA_DAIN.equals(ctrl.getSistema())
								|| SGCConstantes.SISTEMA_EXPCAU.equals(ctrl.getSistema())
								|| SGCConstantes.SISTEMA_TRACAU.equals(ctrl.getSistema())
								|| SGCConstantes.SISTEMA_TRACAUDEST.equals(ctrl.getSistema())
								|| SGCConstantes.SISTEMA_DSS.equals(ctrl.getSistema())
								|| SGCConstantes.SISTEMA_NR.equals(ctrl.getSistema())
								) {
							checkNotDeclaredDocs = true;
						}
						// vai recolher os dados da declaracao consoante o sistema
						ObterDadosDeclaracao obterDados = new ObterDadosDeclaracao();
						if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_SFA)) {
							log.info("###################### SISTEMA SFA ######################");
							// Obtem os dados necessários para construcao da pagina através do WS do SFA2
							declarationProcessor = obterDados.getDadosSFA(ctrl);
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do SFA na TaskWorkPage");
								return;
							}
							// No SFA, caso seja necessario documentos adicionais, vai pesquisar à BD do SGC
							// insere na declaracao
							if (needDocxs) {
								ArrayList<ControloDocumento> listDocs = srvInfo.getInfoDoc(ctrl.getSistema(),
										ctrl.getNumIdentificacao(), ctrl.getChave().getNumeroControlo());
								if (listDocs.isEmpty()) {
									insertMissingDocs(declarationProcessor, ctrl);
								}
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_IMPEC)) {
							log.info("###################### SISTEMA IMPEC ######################");
							// Obtem os dados necessários para construcao da pagina através do WS do IMPEC
							declarationProcessor = obterDados.getDadosIMPEC(ctrl);
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do IMPEC na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do IMPEC na TaskWorkPage");
								return;
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_SIMTEMM)|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS)) {
							log.info("###################### SISTEMA SIMTEM ######################");
							// Obtem os dados necessários para construcao da pagina através do WS do SIMTEM
							declarationProcessor = obterDados.getDadosSIMTEM(ctrl);
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do SIMTEM na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do SIMTEM na TaskWorkPage");
								return;
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
							log.info("###################### SISTEMA DLCC2 ######################");
							// Obtem os dados necessários para construcao da pagina através do WS do DLCC2
							declarationProcessor = obterDados.getDadosDLCC2(ctrl, momento);
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do DLCC2 na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do DLCC2 na TaskWorkPage");
								return;
							}
							// Carrega os Meios Auferidos
							carregaMeioAuferidoDLCC2();
							//TODO: Siaaf
							// Carrega Procedimentos DLCC2
							if (!onPopup) {
								carregaProcedimentosDLCC2();
							}
							// CARREGA IRREGULARIDADE
							ControloIrregularidade listaIrregularidades = null;
							try {
								listaIrregularidades = srvInfo.getIrregularidades(ctrl.getChave().getNumeroControlo().toString());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if ( listaIrregularidades != null)
							ctrl.setControloIrregularidade(listaIrregularidades);

							
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_DAIN)) {
							log.info("###################### SISTEMA DAIN ######################");
							try {
								declarationProcessor = obterDados.getDadosDAIN(ctrl, momento);
							} catch (org.omg.CORBA.portable.ApplicationException e) {
								log.info("Erro ao obter dados da declaracao do DAIN na TaskWorkPage");
							}
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do DAIN na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do DAIN na TaskWorkPage");
								return;
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU)) {
							log.info("###################### SISTEMA EXPCAU ######################");
							try {
								declarationProcessor = obterDados.getDadosEXPCAU(ctrl, momento);
							} catch (org.omg.CORBA.portable.ApplicationException e) {
								log.info("Erro ao obter dados da declaracao do EXPCAU na TaskWorkPage");
							}
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do EXPCAU na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do EXPCAU na TaskWorkPage");
								return;
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_TRACAU)) {
							log.info("###################### SISTEMA TRACAU ######################");
							try {
								declarationProcessor = obterDados.getDadosTRACAU(ctrl, momento);
							} catch (org.omg.CORBA.portable.ApplicationException e) {
								log.info("Erro ao obter dados da declaracao do TRACAU na TaskWorkPage");
							}
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do TRACAU na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do TRACAU na TaskWorkPage");
								return;
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST)) {
							log.info("###################### SISTEMA TRACAUDEST ######################");
							try {
								declarationProcessor = obterDados.getDadosTRACAUDEST(ctrl, momento);
							} catch (org.omg.CORBA.portable.ApplicationException e) {
								log.info("Erro ao obter dados da declaracao do TRACAU Destino na TaskWorkPage");
							}
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do TRACAU Destino na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do TRACAU Destino na TaskWorkPage");
								return;
							}
						}else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {
							log.info("###################### SISTEMA DSS ######################");
							try {
								declarationProcessor = obterDados.getDadosDSS(ctrl, momento);
							} catch (org.omg.CORBA.portable.ApplicationException e) {
								log.info("Erro ao obter dados da declaracao do DSS na TaskWorkPage");
							}
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do DSS Destino na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do DSS Destino na TaskWorkPage");
								return;
							}
						} else if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_NR)) {
							log.info("###################### SISTEMA NR ######################");
							try {
								declarationProcessor = obterDados.getDadosNR(ctrl, momento);
							} catch (org.omg.CORBA.portable.ApplicationException e) {
								log.info("Erro ao obter dados da declaracao do NR na TaskWorkPage");
							}
							// Caso não devolva dados aborta o processo
							if (declarationProcessor == null) {
								setListaErros("Erro ao obter dados da declaracao do NR Destino na TaskWorkPage");
								log.info("Erro ao obter dados da declaracao do NR Destino na TaskWorkPage");
								return;
							}
						}
						// Insere no controlo os dados da declaracao definidos na DECL
						setDadosDeclaracao();
						// Insere no controlo os documentos nao declarados caso seja necessario
						if (checkNotDeclaredDocs) {
							checkNotDeclaredDocs(needItems);
						}
						if (carregaAll) {
							// Carrega as Adicoes e Documentos da declaracao caso existam
							carregarAdicoesDocumentos(needDocxs, needItems);
						}
						// check se tarefa foi ja finalizada
						Constantes constantes = new Constantes();
						boolean tarefaZZFinalizada = true;
						boolean tarefaFisicaFinalizada = true;
						boolean tarefaFinalizada = true;
						TarefasService srvTask = EJBUtil.getSessionInterface(TarefasService.class);
						Tarefa tarefaZZ = null;
						Tarefa tarefaFisica = null;
						Tarefa tarefa = null;
						try {
							// Verifica se existem tarefas para cada um dos controlos para posteriormente validar se alguma esta Finalizada
							if (StringUtils.isNotBlank(ctrl.getIdTarefaZZ())) {
								tarefaZZ = srvTask.pesquisarTarefa(ctrl.getIdTarefaZZ(), ctrl.getSistema());
							} 
							if (StringUtils.isNotBlank(ctrl.getIdTarefaFisico())) {
								tarefaFisica = srvTask.pesquisarTarefa(ctrl.getIdTarefaFisico(), ctrl.getSistema());
							} 
							if(StringUtils.isNotBlank(ctrl.getIdTarefa())){
								tarefa = srvTask.pesquisarTarefa(ctrl.getIdTarefa(), ctrl.getSistema());
							}
						} catch (TarefaException_Exception e) {
							// TODO Auto-generated catch block
							log.error(e.getMessage(), e);
						}
//						Valida se alguma esta Finalizada
						if (tarefaZZ != null) {
							tarefaZZFinalizada = tarefaZZ.getEstado().equals(constantes.Finalizada) || 
			                         tarefaZZ.getEstado().equals(constantes.Nomeada_PendenteOperador); 
						}
						if (tarefaFisica != null) {
							tarefaFisicaFinalizada = tarefaFisica.getEstado().equals(constantes.Finalizada)|| 
									tarefaFisica.getEstado().equals(constantes.Nomeada_PendenteOperador); 
						}
						if (tarefa != null) {
							tarefaFinalizada = tarefa.getEstado().equals(constantes.Finalizada)|| 
									tarefa.getEstado().equals(constantes.Nomeada_PendenteOperador); 
							
						}
						
//						Preenche array com os estados das tarefas
						Boolean[] tarefas = {tarefaZZFinalizada, tarefaFisicaFinalizada, tarefaFinalizada};

						boolean tarefaFechada = true;
						
//						Caso uma tarefa esteja no estado Aberto, mete a variavel a false para nao permitir o readOnly
						for (Boolean estadoTarefa : tarefas) {
							if(estadoTarefa == false) {
								tarefaFechada = false;
							}
						}

						// Se a tarefa estiver finalizada, não permite fazer alteracoes ao contolo,
						// colocando o readOnly a true
						if (tarefaFechada) {
							readOnly = true;
						}
					} else {
						log.info("xXx#2 TaskWorkPage# NO CTRL");
						setListaErros("Os parãmetros fornecidos não correspondem a nenhum controlo.");
						return;
					}
					session.setAttribute(SessionConstants.RES_CONTROLO + idControlo, ctrl);
					SessionManager.getInstance().setSessao(session);
				}
				if (ctrl != null) {
					titlePage = SGCConstantes.getTitlesDesc(ctrl.getSistema()) + " > " + ctrl.getNomeTarefa();
					log.info("TaskWorkPage# titlePage:" + titlePage);
					if (idUser != null && !idUser.equalsIgnoreCase(ctrl.getConferente()) && !idUser.equalsIgnoreCase(ctrl.getVerificador())) {
						readOnly = true;
					}
				}
				// Carrega informação da dau na barra amarela no topo do ecrã
				carregaBarraAmarela(ctrl, declarationProcessor);
				// Info para os botoes
				linkVoltar = clienteweb.getEnderecoRetorno();
				linkConsultarDau = clienteweb.getEnderecoConsulta();
				session.setAttribute(SessionConstants.WEB_CLIENT, clienteweb);
				SessionManager.getInstance().setSessao(session);
				// So carrega as listas SIIAF para o DLCC2
//				if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
//					// Carrega comboBoxs das irregularidades
//					SiiafUtils siiafUtils = new SiiafUtils();
//					siiafUtils.loadInfoComboxSIIAF();
//				}

				continueBuildPage();
			} else {
				// PAG ERRO.......
				log.info("NotFound.......");
				setListaErros("Os parametros fornecidos nao correspondem a nenhum controlo5.");
			}
		} catch (NumberFormatException e) {
			log.error("NumberFormatException....", e);
		} catch (ApplicationException e) {
			log.error("SGCException....", e);
		}

	}

	/*******************************************************************************
	 * 
	 * NOME: carregaBarraAmarela
	 * 
	 * FUNCIONALIDADE: Método que carrega os dados da Barra amarela no topo do ecrã
	 * 
	 *******************************************************************************/

	public void carregaBarraAmarela(Controlo ctrl2, DeclarationProcessor declarationProcessor) {
		beforePageContainer.getControls().clear();
		beforePageContainer.setAttribute("class", "declarationInfoBox");

		List<DeclarationFieldType> barraTypes = declarationProcessor.getDeclBarraTypes();
		for (DeclarationFieldType field : barraTypes) {
			for (DeclarationAttribute attr : field.attributes) {
				String label = attr.label;
				String text = "";
				for (String value : attr.attrValues) {
					text += value;
				}
				Span nRefNacionalLb = new Span(label);
				nRefNacionalLb.setAttribute("class", "declarationInfoBoldText");
				beforePageContainer.add(nRefNacionalLb);
				Span nRefNacional = new Span(text + "&nbsp;&nbsp;");
				nRefNacional.setAttribute("class", "declarationInfoNormalText");
				beforePageContainer.add(nRefNacional);
			}
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: setDadosDeclaracao
	 * 
	 * FUNCIONALIDADE: carrega os dados da declaracao na variavel global de controlo
	 * 
	 *******************************************************************************/

	private void setDadosDeclaracao() {
		List<DeclarationFieldType> attributeTypes = declarationProcessor.getDeclAttributeTypes();
		for (DeclarationFieldType field : attributeTypes) {
			for (DeclarationAttribute attr : field.attributes) {
				if (attr.posicao != null && attr.metodo != null && !attr.attrValues.isEmpty()) {
					String text = "";
					for (String value : attr.attrValues)
						text += value;
					try {
						Method method = ctrl.getClass().getMethod(attr.metodo, String.class);
						try {
							method.invoke(ctrl, text);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							log.info("Erro ao invocar metodo da classe Controlo");
							log.error("Erro ao invocar metodo da classe Controlo: ", e);
						}
					} catch (NoSuchMethodException | SecurityException e) {
						log.info("Erro ao obter metodo da classe Controlo");
						log.error("Erro ao invocar metodo da classe Controlo: ", e);
					}
				}
			}
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: setListaErros
	 * 
	 * FUNCIONALIDADE: carrega as mensagens de erro
	 * 
	 *******************************************************************************/

	private void setListaErros(String mensagem) {
		breadCrumbPath = " ";
		listaErros = new ArrayList<>();
		listaErros.add(mensagem);
		setErrorMessages(listaErros);
	}

	/*******************************************************************************
	 * 
	 * NOME: validaAutorizacaoSaida
	 * 
	 * FUNCIONALIDADE: valida se é uma autorizacao de saida atraves do parametro do
	 * url
	 * 
	 *******************************************************************************/

	private void validaAutorizacaoSaida() {
		String saida = null;
		if (StringUtils.isNotBlank(idControlo)) {
			saida = getContext().getRequest().getParameter(RequestConstants.AUTSAIDA);
			if (StringUtils.isNotEmpty(saida)) {
				isAutSaida = Boolean.parseBoolean(saida);
			}
		} else {
			setListaErros("Os parametros fornecidos nao correspondem a nenhum controlo.");
			log.info("ID Controlo Vazio");
			return;
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: getControloInfoBD
	 * 
	 * FUNCIONALIDADE: pesquisa info sobre o controlo na BD do SGC
	 * 
	 *******************************************************************************/

	private void getControloInfoBD() {
		try {
			ControloServiceT srv = EJBUtil.getSessionInterface(ControloServiceT.class);
			if (readOnly) {
				log.info("GetControloByPK");
				ctrl = srv.getControloByPk(new ControloPK(new Long(idControlo)));
			} else {
				log.info("GetControloOpen");
				ctrl = srv.getControloOpen(new ControloPK(new Long(idControlo)));
			}
			if (ctrl == null) {
				log.info("getControloInfoBD e null");
				setListaErros("Os parametros fornecidos nao correspondem a nenhum controlo.");
				return;
			}
		} catch (ApplicationException e) {
			setListaErros("Erro a carregar ControloInfoBD");
			log.error("Erro a carregar ControloInfoBD: ", e);
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: carregaMeioAuferidoDLCC2
	 * 
	 * FUNCIONALIDADE: carrega meios auferidos do DLCC2
	 * 
	 *******************************************************************************/

	private void carregaMeioAuferidoDLCC2() {
		try {
			if (ctrl.getMeioAferido() != null) {
				MeioAutAferidoService srvAferid = EJBUtil.getSessionInterface(MeioAutAferidoService.class);
				MeioAutAferidoPK meioAutAferidoPK = new MeioAutAferidoPK();
				meioAutAferidoPK.setIdMeioAferido(ctrl.getMeioAferido());
				MeioAutAferido meioAutAferido = srvAferid.getMeioAutAferido(meioAutAferidoPK);
				ctrl.setMeioAutAferido(meioAutAferido);

				MeioAferidoDetalhesService srvAferidDet = EJBUtil.getSessionInterface(MeioAferidoDetalhesService.class);
				ArrayList<MeioAutAferidoDet> controloMeioDetalhes = srvAferidDet
						.getMeioDetalhes(meioAutAferido.getChave().getIdMeioAferido());
				meioAutAferido.setControloMeioDetalhes(controloMeioDetalhes);
			}
		} catch (ApplicationException e) {
			setListaErros("Erro a carregar MeioAuferido DLCC2");
			log.error("Erro a carregar MeioAuferido DLCC2: ", e);
		}

	}

	/*******************************************************************************
	 * 
	 * NOME: carregaProcedimentosDLCC2
	 * 
	 * FUNCIONALIDADE: carrega os procedimentos do DLCC2
	 * 
	 *******************************************************************************/

	private void carregaProcedimentosDLCC2() {
		try {
			ControloOutroService srvOutro = EJBUtil.getSessionInterface(ControloOutroService.class);
			ArrayList<ControloOutroTipoInterveniente> listControloOutroInterveniente = new ArrayList<ControloOutroTipoInterveniente>();
			ArrayList<ControloOutro> listaControloOutro = srvOutro.getListaOutro(ctrl.getChave().getNumeroControlo());
			ctrl.setListaControloOutro(listaControloOutro);
				
			ControloOutroDetalhesService srvOutroDetalhes = EJBUtil.getSessionInterface(ControloOutroDetalhesService.class);
			
			ArrayList<ControloOutroDet> listaControloOutroDetalhes = srvOutroDetalhes.getListaOutroDetalhes(ctrl.getChave().getNumeroControlo());
			Map<Long, ControloOutro> mapControloOutro = new TreeMap<Long, ControloOutro>();
			for(ControloOutro controloOutro : listaControloOutro) {
				// Posicao 5 é referente à Situacao do Interveninete (criaçao da ficha SIIAF), e preenchida na pagina de controlo pelo user e não obtida na declaracao.
				// devido à forma como e apresentada no ecrã (dropdown via ajax), na TabGeralProcedimentos, foi necessario criar uma lista especifica so para esta posicao
				// daí, caso ela exista, entra no if abaixo  
				if (controloOutro.getChave().getIndPosicao()==5) { 
					 ControloOutroTipoIntervenientePK controloOutroIntPK = new ControloOutroTipoIntervenientePK();
	         		 ControloOutroTipoInterveniente controloOutroInt = new ControloOutroTipoInterveniente();
	         		 
	            	 controloOutroIntPK.setNumeroControlo(ctrl.getChave().getNumeroControlo());
	            	 controloOutroIntPK.setNumeroItem(controloOutro.getChave().getNumeroItem()); 
	            	 controloOutroIntPK.setIndPosicao(controloOutro.getChave().getIndPosicao());
	            	 controloOutroInt.setChave(controloOutroIntPK);
	            	// E necessario adicionar o dominio "BSTCSIT0", antes do valor do registo, devido à forma que os dominios sao devolvidos
	            	// pelo webservice do SIIAF: dominio+;+valor
	            	 controloOutroInt.setNValor("BSTCSIT0;"+controloOutro.getNValor());
	            	 
	            	 listControloOutroInterveniente.add(controloOutroInt);
				} else {// preenche as outras posicoes as-is
					mapControloOutro.put(controloOutro.getChave().getIdOutro(), controloOutro);
				}									
			}
			for(ControloOutroDet controloOutroDetalhes : listaControloOutroDetalhes) {
				ControloOutro controloOutro = mapControloOutro.get(controloOutroDetalhes.getChave().getIdOutro());
				controloOutro.getControloOutroDetalhes().add(controloOutroDetalhes);
			}
			
			// Se existirem registos referentes à posicao 5, Situação do interveniente, adiciona ao controlo para ser utilizado posterioremnte no
			// setFormulario da TabGeralProcedimentos
			if (!listControloOutroInterveniente.isEmpty()) {
				ctrl.setControloOutroTipoInterveniente(listControloOutroInterveniente);
			}

		} catch (ApplicationException e) {
			setListaErros("Erro a carregar procedimentos DLCC2");
			log.error("Erro a carregar procedimentos DLCC2 : ", e);
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: checkNotDeclaredDocs
	 * 
	 * FUNCIONALIDADE: carrega os documentos nao declarados
	 * 
	 *******************************************************************************/
	
	public String formatarData(String dataOriginal, String formato) {
	    try {
	        // Remove o prefixo "DATE:" do formato
	        String formatoLimpo = formato.replace("DATE:", "").trim();

	        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); // Formato que vem a data
	        Date data = parser.parse(dataOriginal);

	        // Formatar a data para o novo formato
	        SimpleDateFormat formatter = new SimpleDateFormat(formatoLimpo);
	        return formatter.format(data);
	    } catch (Exception e) {
	        // Em caso de erro, retornar a data original sem formato
	        return dataOriginal;
	    }
	}

	private void checkNotDeclaredDocs(boolean needItems) {
		try {
			ArrayList<ControloItem> listControloItem = null;
			if (needItems) {
				ControloItemServiceT itemSrv = EJBUtil.getSessionInterface(ControloItemServiceT.class);
				listControloItem = itemSrv.getListaAdicoes(ctrl);
			} else {
				listControloItem = new ArrayList<ControloItem>();
				ControloItem item = new ControloItem();
				ControloItemPK chave = new ControloItemPK();
				chave.setNumeroItem((short) 0);
				item.setChave(chave);
				listControloItem.add(item);
			}
			TreeMap<Short, TreeMap<String, LinkedList<String>>> newDocsMap = new TreeMap<Short, TreeMap<String, LinkedList<String>>>();
			Map<String, List<String>> fileNetValueMap = new HashMap<>();
			Map<String, List<String>> dateValueMap = new HashMap<>();
			Map<String, List<String>> cceDocValueMap = new HashMap<>();
			
			for (ControloItem controloItem : listControloItem) {
				short numItem = controloItem.getChave().getNumeroItem();
				List<DeclarationFieldType> declAdicaoDocTypes = declarationProcessor.getDeclAdicaoDocTypes(numItem);
				TreeMap<String, LinkedList<String>> itemMap = newDocsMap.get(numItem);
				if (itemMap == null) {
					itemMap = new TreeMap<String, LinkedList<String>>();
					newDocsMap.put(numItem, itemMap);
				}
				if(declAdicaoDocTypes != null) {					
					for (DeclarationFieldType declarationFieldType : declAdicaoDocTypes) {
						DeclarationAttribute codDocNaoDeclaradosAttr = null;
						DeclarationAttribute nRefDocNaoDeclaradosAttr = null;
						DeclarationAttribute idFileNetNaoDeclaradosAttr = null;
						DeclarationAttribute DataEntregueDocNaoDeclaradosAttr = null;
						DeclarationAttribute CCEDocNaoDeclaradosAttr = null;
						for (DeclarationAttribute declarationAttribute : declarationFieldType.attributes) {
							Number posicao = declarationAttribute.posicao;
							Number inicio = declarationAttribute.inicio;
							if (posicao != null && posicao.intValue() == 1 && inicio != null && inicio.intValue() == 1) { // CodDocNaoDeclarados
								codDocNaoDeclaradosAttr = declarationAttribute;
								continue;
							}
							if (posicao != null && posicao.intValue() == 2 && inicio != null && inicio.intValue() == 1) { // NRefDocNaoDeclarados
								nRefDocNaoDeclaradosAttr = declarationAttribute;
								continue;
							}
							if (posicao != null && posicao.intValue() == 0 && inicio != null && inicio.intValue() == 1) {
								idFileNetNaoDeclaradosAttr = declarationAttribute;
								continue;
							}
							if (posicao != null && posicao.intValue() == 4 && inicio != null && inicio.intValue() == 1) { 
								DataEntregueDocNaoDeclaradosAttr = declarationAttribute;
								continue;
							}
							if (posicao != null && posicao.intValue() == 5 && inicio != null && inicio.intValue() == 1) {  // CCEDocNaoDeclarados
								CCEDocNaoDeclaradosAttr = declarationAttribute;
								continue;
							}
						}
						if (codDocNaoDeclaradosAttr != null && nRefDocNaoDeclaradosAttr != null) {
							for (int i = 0; i < codDocNaoDeclaradosAttr.attrValues.size(); i++) {
								String docId = codDocNaoDeclaradosAttr.attrValues.get(i).trim();
								String docValue = null;
								String fileNetValue = null;
								String dateValue = null;
								String cceDocValue = null;
								
								if(!nRefDocNaoDeclaradosAttr.attrValues.isEmpty()) {
									docValue = nRefDocNaoDeclaradosAttr.attrValues.get(i);
								}
								if(idFileNetNaoDeclaradosAttr != null) {
									fileNetValue = idFileNetNaoDeclaradosAttr.attrValues.get(i).trim();
								}
								if(DataEntregueDocNaoDeclaradosAttr != null) {
									dateValue = DataEntregueDocNaoDeclaradosAttr.attrValues.get(i);
								}
								if(CCEDocNaoDeclaradosAttr != null) {
									cceDocValue = CCEDocNaoDeclaradosAttr.attrValues.get(i);
								}

								LinkedList<String> docIdList = itemMap.get(docId);
								if (docIdList == null) {
									docIdList = new LinkedList<String>();
									itemMap.put(docId, docIdList);
								}
								
								docIdList.add(docValue != null ? docValue.trim() : null);

								
								
							    List<String> fileNetValues = fileNetValueMap.get(docId);
							    if (fileNetValues == null) {
							        fileNetValues = new LinkedList<>();
							        fileNetValueMap.put(docId, fileNetValues);
							    }
							    if (fileNetValue != null) {
						            fileNetValues.add(fileNetValue);
						        }
							    
			                    
			                    // Vai ao campo X_FORMATO ver o formato da data
						        String formatoData = DataEntregueDocNaoDeclaradosAttr != null ? DataEntregueDocNaoDeclaradosAttr.formato : null;

						        // Formata a data apenas se não for null
						        String dateValueFormatada = null;
						        if (dateValue != null && formatoData != null) {
						            dateValueFormatada = formatarData(dateValue, formatoData);
						        }
						        
			                    // Obtém a lista de datas correspondente ao docId
						        List<String> dataValues = dateValueMap.get(docId);
						        if (dataValues == null) {
						            dataValues = new LinkedList<>();
						            dateValueMap.put(docId, dataValues);
						        }

//						        // Adiciona a data formatada à lista
						        if (dateValueFormatada != null) {
						            dataValues.add(dateValueFormatada.trim());
						        }
								
						        // Adiciona CCEDocNaoDeclarado
						        List<String> cceDocValues = cceDocValueMap.get(docId);
						        if (cceDocValues == null) {
						        	cceDocValues = new LinkedList<>();
						        	cceDocValueMap.put(docId, cceDocValues);
						        }
						        if(cceDocValue != null) {
						        	cceDocValues.add(cceDocValue);
						        }
						        	
							}
						}
					}
				}
			}
			TabelasApoioServiceT srvTab = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
			ArrayList<ControloDocumento> listTodosDocs = srvTab.getInfoDoc(ctrl.getSistema(),
					ctrl.getNumIdentificacao(), ctrl.getChave().getNumeroControlo());
			ControloDocumentoService srvDocItem = EJBUtil.getSessionInterface(ControloDocumentoService.class);
			ControloDocumentoDetalhesService itemDetSrv2 = EJBUtil
					.getSessionInterface(ControloDocumentoDetalhesService.class);
			TreeMap<Short, Short> maxIdDocItem = new TreeMap<Short, Short>();
			for (ControloDocumento controloDocumento : listTodosDocs) {
				String docCod = controloDocumento.getCodigoDocumento().trim();
				String docValue = controloDocumento.getDescricaoDocumento();
				docValue = docValue.replace(docCod + ", ", "");
				Short docId = controloDocumento.getChave().getIdentDocumento();
				Short documentoCod = maxIdDocItem.get(controloDocumento.getChave().getNumeroItem());
				if (documentoCod == null) {
					maxIdDocItem.put(controloDocumento.getChave().getNumeroItem(), docId);
				} else {
					if (docId > documentoCod) {
						maxIdDocItem.put(controloDocumento.getChave().getNumeroItem(), docId);
					}
				}
				if (controloDocumento.getNaoDeclarado()) {
					TreeMap<String, LinkedList<String>> itemMap = newDocsMap
							.get(controloDocumento.getChave().getNumeroItem());
					if (itemMap != null && !itemMap.isEmpty()) {
						LinkedList<String> docIdList = itemMap.get(docCod);
						String docValueWithoutDate = null;

						    // Aplica o replace para remover a parte da data
						    String temp = docValue.replaceFirst(", \\d{4}-\\d{2}-\\d{2}.*", "");
						    // Se, após a remoção, o resultado for vazio ou conter somente dígitos (possivelmente a data), atribua null
						    if (temp.isEmpty() || temp.matches("\\d{4}-\\d{2}-\\d{2}.*")) {
						        docValueWithoutDate = null;
						    } else {
						        docValueWithoutDate = temp;
						    }

						if (docIdList != null && !docIdList.isEmpty() && docIdList.contains(docValueWithoutDate)) {
						// Obter a lista de valores fileNet para este docCod
		                List<String> fileNetValues = fileNetValueMap.get(docCod);
		                
		                if (fileNetValues == null || fileNetValues.isEmpty()) {
		                	docIdList.remove();
		                } else {
		                    // Obter o filenet atual do documento
		                    String currentFilenet = controloDocumento.getFilenetLink();
		                 // Inicializa a flag para indicar se houve match
		                    boolean matchFound = false;
		                    // Cria um iterator para percorrer a lista de fileNetValues
		                    Iterator<String> expectedIter = fileNetValues.iterator();

		                    // Percorre todos os expected filenets
		                    while (expectedIter.hasNext()) {
		                        String expectedFilenet = expectedIter.next();
		                        if (currentFilenet != null && expectedFilenet != null &&
		                            currentFilenet.trim().equals(expectedFilenet.trim())) {
		                            // Encontrou o expected que bate com o filenet atual
		                            matchFound = true;
		                            // Remove o expected filenet atual do iterator (remoção segura durante a iteração)
		                            expectedIter.remove();
		                            // Remove o correspondente da lista de docId
		                            docIdList.remove(docValueWithoutDate);
		                            break;
		                        }
		                    }

		                    // Se nenhum match foi encontrado, atualiza o filenet do documento com o primeiro elemento da lista
		                    if (!matchFound && !fileNetValues.isEmpty()) {
		                        // Obtém o primeiro filenet esperado (não dependente de índice, mas podemos pegar o elemento retornado pelo iterator ou usar fileNetValues.iterator().next())
		                        String newFilenet = fileNetValues.iterator().next();
		                        controloDocumento.setFilenetLink(newFilenet);
		                        controloDocumento.setNrefDoc(docValueWithoutDate.trim());
		                        srvDocItem.updateControloDocumento(controloDocumento);
		                        // Remove este filenet da lista para não usá-lo novamente
		                        fileNetValues.remove(newFilenet);
		                        // Remove o docId correspondente
		                        docIdList.remove(docValueWithoutDate);
		                    }

		            }
				}
					}
			}}
			ArrayList<ControloDocumento> listaDocumentos = new ArrayList<>();
			ArrayList<ControloDocumentoDet> listDocsDetails = new ArrayList<>();
			for (Map.Entry<Short, TreeMap<String, LinkedList<String>>> docItem : newDocsMap.entrySet()) {
				Short itemKey = docItem.getKey();
				TreeMap<String, LinkedList<String>> docsMap = docItem.getValue();
				int adiDocIndex = 1;
				Short maxDocItem = maxIdDocItem.get(itemKey);
				if (maxDocItem != null) {
					adiDocIndex = maxDocItem + 1;
				}
				
				for (Map.Entry<String, LinkedList<String>> doc : docsMap.entrySet()) {
					String docCod = doc.getKey();
					LinkedList<String> docValues = doc.getValue();
					
					// Obtém a lista de fileNetValues correspondente ao docId
			        List<String> fileNetValues = fileNetValueMap.get(docCod);
			        List<String> dateValues = dateValueMap.get(docCod);
			        List<String> cceValues = cceDocValueMap.get(docCod);
			            
			            
			        int valueCount = docValues.size();
			        for (int i = 0; i < valueCount; i++) {
			            String docValue = docValues.get(i);
			            String fileNetValue = (fileNetValues != null && i < fileNetValues.size()) ? fileNetValues.get(i) : null;
			            String dateValue = (dateValues != null && i < dateValues.size()) ? dateValues.get(i) : null;
			            String cceValue = (cceValues != null && i < cceValues.size()) ? cceValues.get(i) : null;
			            ArrayList<ControloDocumento> existingDocs = null;
			         // Verifica se já existe um documento com N_REF_DOC igual ao docValue
			            if(docValue != null) {
				            existingDocs = srvDocItem.findByNRefDoc(ctrl.getChave().getNumeroControlo().toString(),docValue.trim(), docCod.trim());
			            }
			            if (existingDocs != null && !existingDocs.isEmpty()) {
			            	if (existingDocs.size() == 1 && (existingDocs.get(0).getFilenetLink() == null || existingDocs.get(0).getFilenetLink().isEmpty())) {
			                // Se existir, atualiza o filenet se houver um fileNetValue correspondente
			                if (fileNetValue != null && !fileNetValue.trim().equalsIgnoreCase("null")) {
			                	existingDocs.get(0).setFilenetLink(fileNetValue.trim());
			                	if(!docValue.trim().equalsIgnoreCase("null")) {
			                		existingDocs.get(0).setNrefDoc(docValue.trim());
			                	}
			                    srvDocItem.updateControloDocumento(existingDocs.get(0));
			            
			                //Verificar se já existem detalhes na SAT_CONTROLO_DOC_DET0 
			                ArrayList<ControloDocumentoDet> existingDets = itemDetSrv2.findByDocChave(ctrl.getChave().getNumeroControlo().toString(),existingDocs.get(0).getChave().getIdentDocumento(),existingDocs.get(0).getChave().getNumeroItem());
			                if (existingDets != null && !existingDets.isEmpty()) {
			                    // Se os detalhes já existem para esse i_id_doc e i_num_item, então
			                    // apenas insere o novo detalhe com a posição "4".
			                    ControloDocumentoDet novoDetalhe = new ControloDocumentoDet();
			                    ControloDocumentoDetPK novoPk = new ControloDocumentoDetPK();
			                    novoPk.setNumeroControlo(existingDocs.get(0).getChave().getNumeroControlo());
			                    novoPk.setNumeroItem(existingDocs.get(0).getChave().getNumeroItem());
			                    novoPk.setIdentDocumento(existingDocs.get(0).getChave().getIdentDocumento());
			                    novoPk.setIndPosicao("4");
			                    novoDetalhe.setChave(novoPk);
			                    // Usa o valor da data correspondente ao matchedIndex
			                    novoDetalhe.setNumValor(dateValue);
			                    novoDetalhe.setInicio((short) 1);
			                    try {
			                        itemDetSrv2.insertControloDocumentoDetalhe(novoDetalhe);
			                    } catch (Exception e) {
			    					log.info("nao foi possivel inserir documentoDet", e);
			    				}
			                    // atualizar docCod para X_INICIO 1
			                    existingDets.get(0).setInicio((short) 1);
			                    itemDetSrv2.updateControloDocumentoDetalhe(existingDets.get(0));

			                    continue; 
			                }
			            }
			                // Não insere um novo documento para este docValue e passa para o próximo índice
			                continue;
			            
			            	} else if (existingDocs.size() == 1 && (existingDocs.get(0).getFilenetLink() != null || !existingDocs.get(0).getFilenetLink().isEmpty())) {
			            		continue;
			            	}
			            }
			            
						// insere na lista os documentos
						ControloDocumento controloDocumento = new ControloDocumento();
						ControloDocumentoPK docPK = new ControloDocumentoPK();
						docPK.setNumeroControlo(ctrl.getChave().getNumeroControlo());
						docPK.setNumeroItem(itemKey);
						docPK.setIndVirtual("F");
						docPK.setIdentDocumento((short) adiDocIndex);
						controloDocumento.setChave(docPK);
						controloDocumento.setCodigoDocumento(docCod);
						controloDocumento.setConferidorDocumento("F");
						controloDocumento.setDescricaoConferido("Não");
						if (fileNetValue != null && !fileNetValue.trim().equalsIgnoreCase("null")) {
						    controloDocumento.setFilenetLink(fileNetValue.trim());
						} else {
						    controloDocumento.setFilenetLink(null);
						}
						controloDocumento.setNumIdentificacao(ctrl.getNumIdentificacao());
						if(docValue != null && !docValue.trim().equalsIgnoreCase("null")) {
							controloDocumento.setNrefDoc(docValue.trim());
						}
						listaDocumentos.add(controloDocumento);
						
						// insere os detalhes do documento
						ControloDocumentoDet headerDocDet = new ControloDocumentoDet();
						ControloDocumentoDetPK headerDocDetPK = new ControloDocumentoDetPK();
						headerDocDetPK.setNumeroControlo(ctrl.getChave().getNumeroControlo());
						headerDocDetPK.setNumeroItem(itemKey);
						headerDocDetPK.setIndPosicao("" + 1);
						headerDocDetPK.setIdentDocumento((short) adiDocIndex);
						headerDocDet.setChave(headerDocDetPK);
						headerDocDet.setNumValor(docCod);
						headerDocDet.setInicio((short) 1);
						listDocsDetails.add(headerDocDet);
						ControloDocumentoDet headerDocDet2 = new ControloDocumentoDet();
						ControloDocumentoDetPK headerDocDetPK2 = new ControloDocumentoDetPK();
						headerDocDetPK2.setNumeroControlo(ctrl.getChave().getNumeroControlo());
						headerDocDetPK2.setNumeroItem(itemKey);
						headerDocDetPK2.setIndPosicao("" + 2);
						headerDocDetPK2.setIdentDocumento((short) adiDocIndex);
						headerDocDet2.setChave(headerDocDetPK2);
						if (docValue != null && !docValue.trim().equalsIgnoreCase("null")) {
						headerDocDet2.setNumValor(docValue.trim());
						}
						headerDocDet2.setInicio((short) 1);
						listDocsDetails.add(headerDocDet2);
						ControloDocumentoDet headerDocDet3 = new ControloDocumentoDet();
						ControloDocumentoDetPK headerDocDetPK3 = new ControloDocumentoDetPK();
						headerDocDetPK3.setNumeroControlo(ctrl.getChave().getNumeroControlo());
						headerDocDetPK3.setNumeroItem(itemKey);
						headerDocDetPK3.setIndPosicao("" + 4);
						headerDocDetPK3.setIdentDocumento((short) adiDocIndex);
						headerDocDet3.setChave(headerDocDetPK3);
						if (dateValue != null && !dateValue.trim().equalsIgnoreCase("null")) {
							headerDocDet3.setNumValor(dateValue);
						}
						headerDocDet3.setInicio((short) 1);
						listDocsDetails.add(headerDocDet3);
						ControloDocumentoDet headerDocDet4 = new ControloDocumentoDet();
						ControloDocumentoDetPK headerDocDetPK4 = new ControloDocumentoDetPK();
						headerDocDetPK4.setNumeroControlo(ctrl.getChave().getNumeroControlo());
						headerDocDetPK4.setNumeroItem(itemKey);
						headerDocDetPK4.setIndPosicao("" + 5);
						headerDocDetPK4.setIdentDocumento((short) adiDocIndex);
						headerDocDet4.setChave(headerDocDetPK4);
						if (cceValue != null && !cceValue.trim().equalsIgnoreCase("null")) {
							headerDocDet4.setNumValor(cceValue);
						}
						headerDocDet4.setInicio((short) 1);
						listDocsDetails.add(headerDocDet4);
						
						adiDocIndex++;
					}

				}
			}
			
			
			for (ControloDocumento doc : listaDocumentos) {
				try {
					srvDocItem.insertControloDocumento(doc);
				} catch (Exception e) {
					log.info("nao foi possivel inserir documento", e);
				}
			}
			ControloDocumentoDetalhesService srvDocDetItem = EJBUtil
					.getSessionInterface(ControloDocumentoDetalhesService.class);
			for (ControloDocumentoDet controloDocumentoDet : listDocsDetails) {
				try {
					srvDocDetItem.insertControloDocumentoDetalhe(controloDocumentoDet);
				} catch (Exception e) {
					log.info("nao foi possivel inserir detalhes do documento", e);
				}
			}
		} catch (ApplicationException e) {
			setListaErros("Erro a checkNotDeclaredDocs");
			log.error("Erro a checkNotDeclaredDocs : ", e);
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: getClienteWebInfo
	 * 
	 * FUNCIONALIDADE: carrega informacao sobre o cliente web
	 * 
	 *******************************************************************************/

	private void getClienteWebInfo(String requestURL, HttpSession session) {
		user = new UserInfo();
		try {
			if (StringUtils.isNotBlank(requestURL)) {
				clienteweb = SGCWebClientUtil.descodificador(requestURL);
				log.debug("clientweb contents: ");
				log.debug(ToStringUtils.toString(clienteweb));
				idUser = clienteweb.getUtilizadorLigado();
				// TFS 7469 - Atualização para o Log4j (Versão2) foram comentadas as 2 linhas
				// abaixo devido a um erro no AtosSecurity
//				logHelper = new LogHelper(true,true);
//				logHelper.saveUserForLogging(idUser);
				estancia = StringUtils.leftPad(clienteweb.getEstancia(), 6, "0");
				try {
					user.setUserId(idUser);
					user.setIdentificaoEstancia(estancia);
					user.setNome("");
					user.setDescricaoEstancia("");
					session.setAttribute(SessionConstants.USER, user);
					SessionManager.getInstance().setSessao(session);
					log.info("User: " + ToStringUtils.toString(user));
				} catch (Exception e) {
					setListaErros("O utilizador que está a aceder à tarefa não está registado: " + idUser);
					readOnly = true;
				}
			} else {
				String consultaIdControlo = getContext().getRequest()
						.getParameter(SGCWebClientUtil.CONSULTA_ID_CONTROLO);
				clienteweb = (SGCWebClient) session.getAttribute(SessionConstants.WEB_CLIENT);
				idUser = clienteweb.getUtilizadorLigado();
				if (StringUtils.isNotBlank(consultaIdControlo)) {
					if (clienteweb != null && !consultaIdControlo.equals(clienteweb.getIdentificadorControlo())) {
						setListaErros("Os parãmetros fornecidos não correspondem a nenhum controlo.");
						return;
					} else {
						ctrl = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + consultaIdControlo);
						carregaAll = false;
					}
				}
			}
		} catch (ClassNotFoundException | ClassCastException | IOException e) {
			log.error("Exception....", e);
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: insertMissingDocs
	 * 
	 * FUNCIONALIDADE: carrega documentos em falta da declaracao
	 * 
	 *******************************************************************************/

	private void insertMissingDocs(DeclarationProcessor declarationProcessor, Controlo ctrl) {
		short numItem = 0;// controloItem.getChave().getNumeroItem();
		List<DeclarationFieldType> declAdicaoDocTypes = declarationProcessor.getDeclAdicaoDocTypes(numItem);
		ArrayList<ControloDocumento> listaDocumentos = new ArrayList<>();
		ArrayList<ControloDocumentoDet> listDocsDetails = new ArrayList<>();
		for (DeclarationFieldType declarationFieldType : declAdicaoDocTypes) {
			DeclarationAttribute mainAttribute = declarationFieldType.mainAttribute;
			int adiDocIndex = 1;
			for (String mainValue : mainAttribute.attrValues) {
				// insere na lista os documentos
				ControloDocumento doc = new ControloDocumento();
				ControloDocumentoPK docPK = new ControloDocumentoPK();
				docPK.setNumeroControlo(ctrl.getChave().getNumeroControlo());
				docPK.setNumeroItem(declarationFieldType.numItem);
				docPK.setIndVirtual("F");
				docPK.setIdentDocumento((short) adiDocIndex);
				doc.setChave(docPK);
				doc.setCodigoDocumento(mainValue);
				// doc.setDescricaoDocumento(mainAttributeValue + numero + data);
				doc.setConferidorDocumento("F");
				doc.setDescricaoConferido("Não");
				doc.setNumIdentificacao(ctrl.getNumIdentificacao());
				listaDocumentos.add(doc);
				int attrIndex = 0;
				for (DeclarationAttribute declarationAttribute : declarationFieldType.attributes) {
					String attrValue = null;
					try {
						attrValue = declarationAttribute.attrValues.get(adiDocIndex - 1);
					} catch (IndexOutOfBoundsException e) {
						break;
					}
					// insere os detalhes do documento
					ControloDocumentoDet docDet = new ControloDocumentoDet();
					ControloDocumentoDetPK docDetPK = new ControloDocumentoDetPK();
					docDetPK.setNumeroControlo(ctrl.getChave().getNumeroControlo());
					docDetPK.setNumeroItem(declarationFieldType.numItem);
					docDetPK.setIndPosicao("" + (attrIndex + 1));
					docDetPK.setIdentDocumento((short) adiDocIndex);
					docDet.setChave(docDetPK);
					docDet.setNumValor(attrValue);
					docDet.setInicio((short) 1);
					listDocsDetails.add(docDet);
					attrIndex++;
				}
				adiDocIndex++;
			}
		}
		ControloDocumentoService srvDocItem = EJBUtil.getSessionInterface(ControloDocumentoService.class);
		for (ControloDocumento doc : listaDocumentos) {
			try {
				srvDocItem.insertControloDocumento(doc);
			} catch (Exception e) {
				log.info("nao foi possivel inserir documento, documento ja existente", e);
			}
		}
		ControloDocumentoDetalhesService srvDocDetItem = EJBUtil
				.getSessionInterface(ControloDocumentoDetalhesService.class);
		for (ControloDocumentoDet controloDocumentoDet : listDocsDetails) {
			try {
				srvDocDetItem.insertControloDocumentoDetalhe(controloDocumentoDet);
			} catch (Exception e) {
				log.info("nao foi possivel inserir detalhes do documento, detalhe ja existente", e);
			}
		}
	}

	/*******************************************************************************
	 * 
	 * NOME: carregarAdicoesDocumentos
	 * 
	 * FUNCIONALIDADE: carrega adicoes e documentos da declaracao
	 * 
	 *******************************************************************************/

	private void carregarAdicoesDocumentos(boolean needDocxs, boolean needItems) {
		try {
			TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
			ArrayList<ControloItem> listaControloItem = null;
			ArrayList<ControloDocumento> listDocs = null;
			if (needDocxs) {
				listDocs = srvInfo.getInfoDoc(ctrl.getSistema(), ctrl.getNumIdentificacao(),
						ctrl.getChave().getNumeroControlo());
			}
			if (needItems) {
				ControloItemServiceT itemSrv = EJBUtil.getSessionInterface(ControloItemServiceT.class);
				listaControloItem = itemSrv.getListaAdicoes(ctrl);
				log.info("Numero De Adicoes: " + listaControloItem.size());
				if (listaControloItem != null && listaControloItem.size() != 0) {
					Map<Short, List<String>> mapDescricao = srvInfo.getInfoAdi(ctrl.getSistema(),
							ctrl.getChave().getNumeroControlo());
					if (ctrl.getSistema().equals(SGCConstantes.SISTEMA_IMPEC)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_SIMTEMM)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_DAIN)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_TRACAU)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_NR)
							|| ctrl.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {
						for (int xx = 0; xx < listaControloItem.size(); xx++) {
							List<String> descNumAdicao = mapDescricao
									.get(listaControloItem.get(xx).getChave().getNumeroItem());

							StringBuilder strb = new StringBuilder();
							for (String entry : descNumAdicao) {
								strb.append(entry + ",");
							}
							listaControloItem.get(xx).setDescNumAdicao(StringUtils.chop(strb.toString()));

							if (listDocs != null) {
								ArrayList<ControloDocumento> docsItens = new ArrayList<ControloDocumento>();
								for (int i = 0; i < listDocs.size(); i++) {
									ControloDocumento doc = listDocs.get(i);
									log.info("Numero de Adicao: " + doc.getNumAdicao());
									log.info("xXx#2 TaskWorkPage#listaDocs:"
											+ listaControloItem.get(xx).getChave().getNumeroItem() + ";"
											+ doc.getChave().getNumeroItem() + ";");
									if (listaControloItem.get(xx).getChave().getNumeroItem()
											.equals(doc.getChave().getNumeroItem())) {
										docsItens.add(doc);
									}

									listaControloItem.get(xx).setListaControloDocumentoItem(docsItens);
									String tamanhoLista = ObjectUtil.castStringFromInteger(
											listaControloItem.get(xx).getListaControloDocumentoItem().size());
									listaControloItem.get(xx).setNumDocumentos(tamanhoLista);
								}
							}
						}
					}
				}
			} else if (listDocs != null && listDocs.size() > 0) {
				// Item Ficticia - Chave é necessária para o preenchimento dos Documentos quando
				// o sistema não possui adições.
				listaControloItem = new ArrayList<ControloItem>();

				ControloDocumentoService itemSrv2 = EJBUtil.getSessionInterface(ControloDocumentoService.class);
				log.info("TASKWORKPAGE#DOCS" + ToStringUtils.toString(itemSrv2.getListaDoc(ctrl)));
				ArrayList<ControloDocumento> lista = itemSrv2.getListaDoc(ctrl);

				ControloDocumentoDetalhesService itemDetSrv2 = EJBUtil
						.getSessionInterface(ControloDocumentoDetalhesService.class);
				ArrayList<ControloDocumentoDet> listaDetalhes = itemDetSrv2.getListaDocDet(ctrl);// REVER
																									// URGENTE

				ControloItem item = new ControloItem();
				ControloItemPK chave = new ControloItemPK();
				chave.setIndVirtual("N");
				chave.setNumeroControlo(new Long("1"));
				chave.setNumeroItem((short) 0);
				item.setChave(chave);
				ArrayList<ControloDocumento> docsItens = new ArrayList<ControloDocumento>();

				for (int i = 0; i < listDocs.size(); i++) {
					log.info("xXx#2 TaskWorkPage#listaDocsX1");
					ControloDocumento doc = listDocs.get(i);
					doc.getChave().setNumeroItem((short) 0);
					doc.setNumIdentificacao(ctrl.getNumIdentificacao());
					// altera flag documento conferido
					if (lista != null && lista.size() > 0) {
						for (int j = 0; j < lista.size(); j++) {
							ControloDocumento docRegistado = lista.get(j);
							if (StringUtils.isNotBlank(doc.getCodigoDocumento())
									&& StringUtils.isNotBlank(docRegistado.getCodigoDocumento())
									&& doc.getCodigoDocumento().equalsIgnoreCase(docRegistado.getCodigoDocumento())) {
								doc.setConferidorDocumento(docRegistado.getConferidorDocumento());
								doc.setFilenetLink(docRegistado.getFilenetLink());
							}
						}
					}
					docsItens.add(doc);
				}
				item.setListaControloDocumentoItem(docsItens);
				item.setNumIdentificacao(ctrl.getNumIdentificacao());
				listaControloItem.add(item);
				log.info("xXx#2 TaskWorkPage#listaDocsX1" + ToStringUtils.toString(listaControloItem));
			}
			ctrl.setListaControloItem(listaControloItem);
		} catch (ApplicationException e) {
			setListaErros("Erro a carregar procedimentos DLCC2");
			log.error("Erro a carregar procedimentos DLCC2 : ", e);
		}
	}

	protected abstract void continueBuildPage();
}
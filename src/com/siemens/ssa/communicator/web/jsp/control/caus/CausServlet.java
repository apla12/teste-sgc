package com.siemens.ssa.communicator.web.jsp.control.caus;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.siemens.ssa.communicator.pojo.interfaces.*;
import com.siemens.ssa.communicator.web.jsp.control.pojo.ControloSeloWeb;
import org.apache.click.control.Option;

import com.siemens.security.session.SessionManager;
import com.siemens.service.interfaces.CcncomServiceT;
import com.siemens.service.interfaces.ControloItemTipoServiceT;
import com.siemens.service.interfaces.ControloServiceT;
import com.siemens.service.interfaces.DadosGeraisService;
import com.siemens.ssa.communicator.util.ObterDadosDeclaracao;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import net.atos.at.filenet.cliente.entity.DocumentoFilenet;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;


@WebServlet(name="CausServlet", urlPatterns = "/CausServlet")
public class CausServlet extends HttpServlet{
	
	private static Log log = Log.getLogger(CausServlet.class);
	
	private static final long serialVersionUID = 1L;
	private Controlo ctr = null;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String numeroControlo = request.getParameter("idControlo");
		
		HttpSession session = request.getSession();

		ctr = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO+numeroControlo);
		
		String operacao = request.getParameter("op");
		
		if(operacao.equals("tratar_resultado_adicao")) {	
			tratarResultadoAdicao(request, session);
			
		} else if(operacao.contentEquals("carrega_controlo_tipo_item")){
			carregaControloTipoItem(request, response);
			
		} else if(operacao.contentEquals("detalha_controlo_tipo_item")){
			detalhaControloTipoItem(request, response);
			
		} else if(operacao.equals("recupera_dados_cabecalho_tabela_tipo_item")) { 
			List<String> headers = Arrays.asList("column_codigo_identificacao_tabela_controlo_item", "column_identificador_tabela_controlo_item", "column_area_risco_tabela_detalhe_item_tipo", "column_numero_frc_tabela_detalhe_item_tipo", "column_pir_tabela_controlo_item", "column_tipo_controlo_tabela_detalhe_item_tipo");
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(traducoes(headers, request, session));
			
		} else if(operacao.contentEquals("recupera_dados_cabecalho_tabela_detalhe_item_tipo")) {
			List<String> headers = Arrays.asList("column_orientacao_analise_tabela_detalhe_item_tipo", "column_observacoes_tabela_detalhe_item_tipo", "column_extracao_amostras_tabela_detalhe_item_tipo");
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(traducoes(headers, request, session));
			
		} else if (operacao.contentEquals("tratar_selecao_acl")){
			trataSelecaoACL(request, response, ctr);
			
		} else if (operacao.equals("conferir_todos_os_documentos")) {
			String identControlo = request.getParameter("idControlo");
			conferirTodosDocumentos(identControlo, session);
			
		} else if (operacao.equals("desconferir_todos_os_documentos")) {
			String identControlo = request.getParameter("idControlo");
			desconferirTodosDocumentos(identControlo, session);
		} else if (operacao.equals("conferir_todos_os_selos")) {
			String identControlo = request.getParameter("idControlo");
			conferirTodosSelos(identControlo, session);
		} else if (operacao.equals("desconferir_todos_os_selos")) {
			String identControlo = request.getParameter("idControlo");
			desconferirTodosSelos(identControlo, session);
		} else if (operacao.equals("conferir_unico_documento")) {
			marcarUnicoDocumento(request, response, session);
		} else if (operacao.equals("conferir_unico_selo")) {
			marcarUnicoSelo(request, response, session);
		}
		else if (operacao.equals("remover_documento_adicional")) {
			removerDocumentoAdicional(request, session);
			
		} else if (operacao.equals("adicionar_identificacao_selos")) {
			adicionarIdentificacaoSelos(request, response, session);
			
		} else if (operacao.equals("recuperar_lista_resultado_controlo")) {
			recuperarListaResultadoControlo(response, numeroControlo, session);
			
		} else if (operacao.equals("alterar_identificacao_selos")) {
			alterarIdentificacaoSelos(request, session);
			
		} else if (operacao.equals("remover_identificacao_selo")) {
			removerIdentificacaoSelo(request, session);
			
		} else if (operacao.equals("recupera_lista_equipamentos")){
			recuperaOpcoesEquipamentos(request, response);
			
		} else if (operacao.equals("listar_equipamentos_cadastrados")) {
			listarEquipamentosCadastrados(request, response, session);
			
		} else if (operacao.equals("listar_equipamentos_items_manuais_cadastrados")) {
			listarEquipamentosItemsManuaisCadastrados(request, response, session);
			
		} else if (operacao.equals("adicionar_equipamento")) {
			adicionarEquipamento(request, response, session);
			
		} else if (operacao.equals("adicionar_equipamento_item_manual")) {
			adicionarEquipamentoItemManual(request, response, session);
			 	
		} else if (operacao.contentEquals("remover_item_equipamento")) {
			removerItemEquipamento(request, response, session);
			
		} else if (operacao.contentEquals("remover_item_equipamento_manual")) {
			removerItemEquipamentoManual(request, response, session);
			
		} else if (operacao.equals("carregar_dados_identificadores_manuais")) { //carregar_dados_area_risco
			carregarDadosIdentificadoresManuais(request, response);
			
		} else if (operacao.equals("adicionar_controlo_item_tipo_manual")) {
			adicionarControloItemTipoManual(request, response, session);
			
		} else if (operacao.contentEquals("remover_controlo_item_tipo_manual")) {
			removerControloItemTipoManual(request, session);
		
		}
		
	}

	private void removerControloItemTipoManual(HttpServletRequest request, HttpSession session) {
		String chavePrimaria = request.getParameter("pk");
		String idAdicao = request.getParameter("form_ajaxOpId").replaceAll("\\D+", "");
		Map<String, List<ControloItemTipo>> mapControloItemTipoManual = (Map<String, List<ControloItemTipo>>) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + ctr.getChave().getNumeroControlo());
		List<ControloItemTipo> listControloItemTipoManual = mapControloItemTipoManual.get(ctr.getChave().getNumeroControlo() +""+idAdicao);
		listControloItemTipoManual.removeIf(obj -> obj.getChave().getDetItemControlo().compareTo(new Long(chavePrimaria)) == 0);
		
		//APOS REMOVER O ITEM ADICIONAL BUSCA OS EQUIPAMENTOS QUE ESTAVAM VINCULADOS A ELE E OS EXCLUI.
		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + ctr.getChave().getNumeroControlo());
		List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(chavePrimaria);
		listaDaAdicao.removeIf((s)-> s.getChave().getDetItemControlo().compareTo(new Long(chavePrimaria)) == 0 );
	}

	private void adicionarControloItemTipoManual(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		
		String chavePrimaria = request.getParameter("pk");
		String idAdicao = request.getParameter("form_ajaxOpId").replaceAll("\\D+", "");
		Map<String, List<ControloItemTipo>> mapControloItemTipoManual = (Map<String, List<ControloItemTipo>>) session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + ctr.getChave().getNumeroControlo());
		List<ControloItemTipo> listControloItemTipoManual = mapControloItemTipoManual.get(ctr.getChave().getNumeroControlo()+""+idAdicao);
		if(listControloItemTipoManual == null) {
			listControloItemTipoManual = new ArrayList<ControloItemTipo>();
		}
		
		String objetoUnificado = request.getParameter("objeto");
		String[] objetoArray = objetoUnificado.split("\\*");
		ControloItemTipoPK pk = new ControloItemTipoPK();
		long chaveAleatoria = ThreadLocalRandom.current().nextLong(1000, 10000);
		if(chavePrimaria.equals("")) {			
			pk.setDetItemControlo(chaveAleatoria);
		} else {
			listControloItemTipoManual.removeIf(obj -> obj.getChave().getDetItemControlo().compareTo(new Long(chavePrimaria)) == 0);
			pk.setDetItemControlo(new Long(chavePrimaria));
		}
		pk.setIdVirtual("F");
		pk.setNumeroControlo(ctr.getChave().getNumeroControlo());
		pk.setNumeroItem(new Short(idAdicao));
		ControloItemTipo cit = new ControloItemTipo();
		cit.setChave(pk);
		
		cit.setIdentificadorSSA(objetoArray[0]);
		cit.setAreaRisco(objetoArray[1]);
		String analiseRiscoCompleto = objetoArray[2].split("-")[0].trim();
		cit.setAnaliseRisco(analiseRiscoCompleto);
		cit.setTipoControlo(objetoArray[3]);
		cit.setResultado(objetoArray[4]);
		cit.setFlagConfirmado(SGCConstantes.FLAG_BD_FALSO);
		cit.setSsa(SGCConstantes.FLAG_BD_FALSO);
		
		boolean existe = listControloItemTipoManual.stream().anyMatch(item -> 
	    item.getIdentificadorSSA().equals(cit.getIdentificadorSSA()) &&
	    item.getAreaRisco().equals(cit.getAreaRisco()) &&
	    item.getAnaliseRisco().equals(cit.getAnaliseRisco()) &&
	    item.getTipoControlo().equals(cit.getTipoControlo()));
	    
	
		String responseString = "";
		
		if (!existe) {
			listControloItemTipoManual.add(cit);
			mapControloItemTipoManual.put(ctr.getChave().getNumeroControlo()+""+idAdicao, listControloItemTipoManual);
			session.setAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO_MANUAL + ctr.getChave().getNumeroControlo(), mapControloItemTipoManual);
			SessionManager.getInstance().setSessao(session);
			responseString = cit.getChave().getDetItemControlo().toString();
		}

		response.setContentType("application/json");
		response.getWriter().write("{\"detItemControlo\": \"" + responseString + "\"}");
	}

	private void carregarDadosIdentificadoresManuais(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//Trata a seleção de ACL's
		List<DadosGerais> options = new ArrayList<DadosGerais>();
				
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		try {
			options = dadosGeraisService.getPorValorSistema(request.getParameter("codigo"), ctr.getSistema().toUpperCase());
			StringBuilder result = new StringBuilder();
			for (DadosGerais option : options) {
				result.append(option.getCodigo()).append("=").append(option.getCodigo()).append(" - ").append(option.getDescricao()).append("\n");
			}
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(result.toString());
		} catch (ApplicationException e) {
			
		}
	}
	
	private void removerItemEquipamento( HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

		String identificadorItemTipo = request.getParameter("detItemControlo");
		String id = request.getParameter("id");
		
		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + ctr.getChave().getNumeroControlo());
		
		StringBuilder chave = new StringBuilder();
		chave.append(identificadorItemTipo);
		
		List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(chave.toString());
		
		listaDaAdicao.removeIf((s)-> s.getChave().getItemEquipamento().compareTo(new Long(id)) == 0 );
		
		listEquipamentosMap.put(chave.toString(), listaDaAdicao);
		
		session.setAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + ctr.getChave().getNumeroControlo(), listEquipamentosMap);
		SessionManager.getInstance().setSessao(session);
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("");
	}
	
	private void removerItemEquipamentoManual( HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {

		String identificadorItemTipo = request.getParameter("detItemControlo");
		String id = request.getParameter("id");

		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + ctr.getChave().getNumeroControlo());
		
		List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(identificadorItemTipo);
		
		listaDaAdicao.removeIf((s)-> s.getChave().getItemEquipamento().compareTo(new Long(id)) == 0 );
		
		listEquipamentosMap.put(identificadorItemTipo, listaDaAdicao);
		
		session.setAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + ctr.getChave().getNumeroControlo(), listEquipamentosMap);
		SessionManager.getInstance().setSessao(session);
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("");
	}
	
	private void recuperarListaResultadoControlo(HttpServletResponse response, String numeroControlo,
			HttpSession session) throws IOException {
		List<Option> options = (List<Option>) session.getAttribute(SessionConstants.ATTR_LISTA_RESULTADO_CONTROLO+numeroControlo);
		StringBuilder result = new StringBuilder();
		for (Option option : options) {
			result.append(option.getValue()).append("=").append(option.getLabel()).append("\n");
		}
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}

	private void removerIdentificacaoSelo(HttpServletRequest request, HttpSession session) {
		String[] linha = request.getParameter("idLinha").split("_");
		String id = linha[linha.length-1];
		List<IdentificacaoSelo> selos = (List<IdentificacaoSelo>) session.getAttribute("identificacaoSelos"+ctr.getChave().getNumeroControlo());
		Optional<IdentificacaoSelo> is  = selos.stream().filter(s -> s.getNumItemStr().equals(id)).findFirst();
		if(is.isPresent()) {				
			int index = selos.indexOf(is.get());
			if(index != -1) {
				selos.remove(index);
				session.setAttribute("identificacaoSelos"+ctr.getChave().getNumeroControlo(), selos);
				SessionManager.getInstance().setSessao(session);
			}
		}
	}

	private void alterarIdentificacaoSelos(HttpServletRequest request, HttpSession session) {
		String objetoUnificado = request.getParameter("objeto");
		String[] objetoArray = objetoUnificado.split("\\*");
		List<IdentificacaoSelo> selosList = (List<IdentificacaoSelo>) session.getAttribute("identificacaoSelos"+ctr.getChave().getNumeroControlo());
		Optional<IdentificacaoSelo> is  = selosList.stream().filter(s -> s.getChave().getIdSelo().toString().equals(objetoArray[0])).findFirst();
		if(is.isPresent()) {
			IdentificacaoSelo selo = is.get();

			selo.setIdentSelos(objetoArray[4]);
			selo.setSelos(objetoArray[1]);
			selo.setNumItem(objetoArray[0]);
			selo.setMotivoSelagem(objetoArray[5]);
			selo.setQuantidadeSelos(objetoArray[3]);
			selo.setSelTransp(objetoArray[2]);
			
			session.setAttribute("identificacaoSelos", selosList);
			SessionManager.getInstance().setSessao(session);
		}
	}

	private void adicionarIdentificacaoSelos(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		String objetoUnificado = request.getParameter("objeto");
		
		String[] objetoArray = objetoUnificado.split("\\*");
		
		List<IdentificacaoSelo> selosList = (List<IdentificacaoSelo>) session.getAttribute("identificacaoSelos"+ctr.getChave().getNumeroControlo());
		IdentificacaoSeloPK pk = new IdentificacaoSeloPK();
		Long sequencia = selosList.size() + 1L;
		pk.setIdSelo(sequencia);
		pk.setNumControlo(ctr.getChave().getNumeroControlo());
		IdentificacaoSelo is = new IdentificacaoSelo();
		is.setChave(pk);
		is.setIdentSelos(objetoArray[4]);
		is.setSelos(objetoArray[1]);
		is.setNumItem(objetoArray[0]);
		is.setMotivoSelagem(objetoArray[5]);
		is.setQuantidadeSelos(objetoArray[3]);
		is.setSelTransp(objetoArray[2]);
		is.setXtimest(new Timestamp(new java.util.Date().getTime()));
		selosList.add(is);
		session.setAttribute("identificacaoSelos", selosList);
		SessionManager.getInstance().setSessao(session);
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(sequencia.intValue());
	}

	private void removerDocumentoAdicional(HttpServletRequest request, HttpSession session) {
		String linha = request.getParameter("idLinha");
		List<ControloDocumentoAdicional> docsSaved = (List<ControloDocumentoAdicional>) session.getAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctr.getChave().getNumeroControlo());
		Optional<ControloDocumentoAdicional> doc  = docsSaved.stream().filter(s -> s.getChave().getId().compareTo(Short.valueOf(linha)) == 0).findFirst();
		if(doc.isPresent()) {				
			int index = docsSaved.indexOf(doc.get());
			if(index != -1) {
				docsSaved.remove(index);
				session.setAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctr.getChave().getNumeroControlo(), docsSaved);
				SessionManager.getInstance().setSessao(session);
			}
		}
	}

	private void adicionarEquipamento(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		String item = request.getParameter("item");
		String identificadorSSA = request.getParameter("identificadorItemTipo");
		String adicao = request.getParameter("adicao");
		String detItemControlo = request.getParameter("detItemControlo");
		
		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + ctr.getChave().getNumeroControlo());
		
		StringBuilder chave = new StringBuilder();
		chave.append(detItemControlo);
		
		List<ItemEquipamento> listaEquipamentosControloItemTipo = listEquipamentosMap.get(chave.toString());
		
		if(listaEquipamentosControloItemTipo == null) {
			listaEquipamentosControloItemTipo = new ArrayList<ItemEquipamento>();
		}
		
		boolean jaExiste = listaEquipamentosControloItemTipo.stream().anyMatch(it -> it.getEquipamento().equals(item));
		
		if(!jaExiste) {			
			Long itemEquipamentoId = new Long((listaEquipamentosControloItemTipo != null ? listaEquipamentosControloItemTipo.size() + 1 : 1));
			
			listaEquipamentosControloItemTipo.add(new ItemEquipamento()
					.setChave(new ItemEquipamentoPK()
							.setNumeroControlo(ctr.getChave().getNumeroControlo())
							.setDetItemControlo(new Long(detItemControlo))
							.setNumeroItem(new Short(adicao))
							.setIdVirtual("F")
							.setItemEquipamento(itemEquipamentoId))
					.setIdentificadorSSA(identificadorSSA)
					.setEquipamento(item)
					.setFlagConfirmado(SGCConstantes.FLAG_BD_FALSO));
			
			listEquipamentosMap.put(chave.toString(), listaEquipamentosControloItemTipo);
			session.setAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + ctr.getChave().getNumeroControlo(), listEquipamentosMap);
			SessionManager.getInstance().setSessao(session);
			
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(itemEquipamentoId.toString());
		} else {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("");
		}
		
	}
	
	private void adicionarEquipamentoItemManual(HttpServletRequest request, HttpServletResponse response, HttpSession session)
			throws IOException {
		String item = request.getParameter("item");
		String descricao = request.getParameter("descricao");
		String identificadorSSA = request.getParameter("identificadorItemTipo");
		String adicao = request.getParameter("adicao");
		String detItemControlo = request.getParameter("detItemControlo");
		
		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + ctr.getChave().getNumeroControlo());
		
		StringBuilder chave = new StringBuilder();
		chave.append(detItemControlo);
		
		List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(chave.toString());
		
		if(listaDaAdicao == null) {
			listaDaAdicao = new ArrayList<ItemEquipamento>();
		}
		
		boolean jaExiste = listaDaAdicao.stream().anyMatch(it -> it.getEquipamento().equals(item));
		
		if(!jaExiste) {			
			Long itemEquipamentoId = new Long((listaDaAdicao != null ? listaDaAdicao.size() + 1 : 1));
			
			listaDaAdicao.add(new ItemEquipamento()
					.setChave(new ItemEquipamentoPK()
							.setNumeroControlo(ctr.getChave().getNumeroControlo())
							.setDetItemControlo(new Long(detItemControlo))
							.setNumeroItem(new Short(adicao))
							.setIdVirtual("F")
							.setItemEquipamento(itemEquipamentoId))
					.setIdentificadorSSA(identificadorSSA)
					.setFlagConfirmado(SGCConstantes.FLAG_BD_FALSO)
					.setEquipamento(item));
			
			listEquipamentosMap.put(chave.toString(), listaDaAdicao);
			session.setAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + ctr.getChave().getNumeroControlo(), listEquipamentosMap);
			SessionManager.getInstance().setSessao(session);
			
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(itemEquipamentoId.toString());
		} else {
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("");
		}
		
	}

	private void recuperaOpcoesEquipamentos(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String tipoControlo = request.getParameter("tipoControlo");
		List<DadosGerais> lista = new ArrayList<DadosGerais>();
		
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		try {
			lista = dadosGeraisService.getPorValorSistema(tipoControlo, ctr.getSistema().toUpperCase());
		} catch (ApplicationException e) {
			
		}
		StringBuilder result = new StringBuilder();
		for (DadosGerais option : lista) {
			result.append(option.getCodigo()).append("=").append(option.getCodigo()).append(" - ").append(option.getDescricao()).append("\n");
		}
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}

	private void listarEquipamentosCadastrados(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		
		String item = request.getParameter("item");
		
		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO + ctr.getChave().getNumeroControlo());
		
		List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(item);
		
		try {
		StringBuilder jsonResponse = new StringBuilder("");
		if(listaDaAdicao != null && !listaDaAdicao.isEmpty()) {				
			jsonResponse.append("[");
			
			for (int i = 0; i < listaDaAdicao.size(); i++) {
				ItemEquipamento detalhe = listaDaAdicao.get(i);
				jsonResponse.append("{");
				jsonResponse.append("\"itemEquipamento\":\"").append(detalhe.getChave().getItemEquipamento() != null ? detalhe.getChave().getItemEquipamento() : "").append("\",");
				jsonResponse.append("\"detItemControlo\":\"").append(detalhe.getChave().getDetItemControlo() != null ? detalhe.getChave().getDetItemControlo() : "").append("\",");
				jsonResponse.append("\"numeroControlo\":\"").append(detalhe.getChave().getNumeroControlo() != null ? detalhe.getChave().getNumeroControlo() : "").append("\",");
				jsonResponse.append("\"numeroItem\":\"").append(detalhe.getChave().getNumeroItem() != null ? detalhe.getChave().getNumeroItem() : "").append("\",");
				jsonResponse.append("\"idVirtual\":\"").append(detalhe.getChave().getIdVirtual() != null ? detalhe.getChave().getIdVirtual() : "").append("\",");
				jsonResponse.append("\"identificadorSSA\":\"").append(detalhe.getIdentificadorSSA() != null ? detalhe.getIdentificadorSSA() : "").append("\",");
				DadosGerais dg;
					dg = dadosGeraisService.buscarPorCodigoSistema(detalhe.getEquipamento(), ctr.getSistema());
				jsonResponse.append("\"equipamento\":\"").append(dg != null ? dg.getCodigo().concat(" - ").concat(dg.getDescricao()) : "").append("\"");
				jsonResponse.append("}");
				
				if (i < listaDaAdicao.size() - 1) {
					jsonResponse.append(",");
				}
			}
			jsonResponse.append("]");
		}
		
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse.toString());
		} catch (ApplicationException e) {
			log.error("Erro ao recuperar dados de equipamentos");
		}
	}
	
	private void listarEquipamentosItemsManuaisCadastrados(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		
		String item = request.getParameter("item");
		String adicao = request.getParameter("adicao");
		
		StringBuilder chave = new StringBuilder();
		chave.append(item);
		
		Map<String, List<ItemEquipamento>> listEquipamentosMap = (Map) session.getAttribute(SessionConstants.ATTR_LISTA_ITEM_EQUIPAMENTO_MANUAL + ctr.getChave().getNumeroControlo());
		
		List<ItemEquipamento> listaDaAdicao = listEquipamentosMap.get(chave.toString());
		
		try {
		StringBuilder jsonResponse = new StringBuilder("");
		if(listaDaAdicao != null && !listaDaAdicao.isEmpty()) {				
			jsonResponse.append("[");
			
			for (int i = 0; i < listaDaAdicao.size(); i++) {
				ItemEquipamento detalhe = listaDaAdicao.get(i);
				jsonResponse.append("{");
				jsonResponse.append("\"itemEquipamento\":\"").append(detalhe.getChave().getItemEquipamento() != null ? detalhe.getChave().getItemEquipamento() : "").append("\",");
				jsonResponse.append("\"detItemControlo\":\"").append(detalhe.getChave().getDetItemControlo() != null ? detalhe.getChave().getDetItemControlo() : "").append("\",");
				jsonResponse.append("\"numeroControlo\":\"").append(detalhe.getChave().getNumeroControlo() != null ? detalhe.getChave().getNumeroControlo() : "").append("\",");
				jsonResponse.append("\"numeroItem\":\"").append(detalhe.getChave().getNumeroItem() != null ? detalhe.getChave().getNumeroItem() : "").append("\",");
				jsonResponse.append("\"idVirtual\":\"").append(detalhe.getChave().getIdVirtual() != null ? detalhe.getChave().getIdVirtual() : "").append("\",");
				jsonResponse.append("\"identificadorSSA\":\"").append(detalhe.getIdentificadorSSA() != null ? detalhe.getIdentificadorSSA() : "").append("\",");
				DadosGerais dg;
					dg = dadosGeraisService.buscarPorCodigoSistema(detalhe.getEquipamento(), ctr.getSistema());
				jsonResponse.append("\"equipamento\":\"").append(dg != null ? dg.getCodigo().concat(" - ").concat(dg.getDescricao()) : "").append("\"");
				jsonResponse.append("}");
				
				if (i < listaDaAdicao.size() - 1) {
					jsonResponse.append(",");
				}
			}
			jsonResponse.append("]");
		}
		
		response.setContentType("application/json");
		response.getWriter().write(jsonResponse.toString());
		} catch (ApplicationException e) {
			log.error("Erro ao recuperar dados de equipamentos");
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String operacao = request.getParameter("op");
		
		if(operacao.equals("obter_documento_filenet")) {			
			CcncomServiceT ccncomServiceT = EJBUtil.getSessionInterface(CcncomServiceT.class);
			String idFilenet = request.getParameter("idDoc");
			String numeroControlo = request.getParameter("numControlo");
			HttpSession session = request.getSession();
			ctr = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO+numeroControlo);
			
			try {
				DocumentoFilenet doc = ccncomServiceT.obterFicheiroFileNet(idFilenet, ctr.getSistema());	
				
				response.setContentType("application/octet-stream");
				response.setContentLength(doc.getConteudoFicheiro().length);
				response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
				response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getNomeFicheiro() + "\"");
				response.getOutputStream().write(doc.getConteudoFicheiro());
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} catch (Exception e) {
				log.error("Ficheiro do filenet não disponível: " + idFilenet );
			}
		}
	}
	
	private String traducoes(List<String> chaves, HttpServletRequest request, HttpSession session) {
		String numeroControlo = ctr.getChave().getNumeroControlo().toString();
		List<String> headersTraduzidos = new ArrayList<String>();
		String chave = "";
		try {
			ControloServiceT controloService = EJBUtil.getSessionInterface(ControloServiceT.class);
			Controlo controlo = controloService.getControloByPk(new ControloPK(Long.valueOf(numeroControlo)));
			ObterDadosDeclaracao obterDados = new ObterDadosDeclaracao();
			DeclarationProcessor declarationProcessor;
			declarationProcessor = obterDados.getDados(controlo, SGCConstantes.MOMENTO_CAUS_B);
			for(String s : chaves) {
				chave = s;
				headersTraduzidos.add(declarationProcessor.getWidgetLabel(s));			
			}
		} catch (org.omg.CORBA.portable.ApplicationException | NumberFormatException | ApplicationException e) {
			log.error("Erro ao tentar obter o registro de widged para a chave: " + chave);
		}
		return String.join(";", headersTraduzidos);
		
	}

	private void trataSelecaoACL(HttpServletRequest request, HttpServletResponse response, Controlo ctr) throws IOException {
		//Trata a seleção de ACL's
		List<Option> options = new ArrayList<Option>();
				
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		try {
			options = dadosGeraisService.getDescricaoPorValorSistema(request.getParameter("clSelecionada"), ctr.getSistema().toUpperCase());
		} catch (ApplicationException e) {
			
		}
		StringBuilder result = new StringBuilder();
		for (Option option : options) {
			result.append(option.getValue()).append("=").append(option.getLabel()).append("\n");
		}
		
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(result.toString());
	}

	private void detalhaControloTipoItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ControloItemTipoServiceT controloItemTipoServiceT = EJBUtil.getSessionInterface(ControloItemTipoServiceT.class);
		
		String id = request.getParameter("id");

		try {
			ControloItemTipo listaControloItemTipo = controloItemTipoServiceT.getControloItemTipo(Long.valueOf(id));
			
			StringBuilder jsonResponse = new StringBuilder();
	

	    	ControloItemTipo detalhe = listaControloItemTipo;
	        jsonResponse.append("{");
	        jsonResponse.append("\"orientacaoAnalise\":\"").append(detalhe.getOrientacaoAnalise() != null ? detalhe.getOrientacaoAnalise(): "").append("\",");
	        jsonResponse.append("\"observacoes\":\"").append(detalhe.getObservacoes() != null ? detalhe.getObservacoes() : "").append("\"");
	        //jsonResponse.append("\"extracaoAmostra\":\"").append(detalhe.getExtracaoAmostra() != null ? detalhe.getExtracaoAmostra() : "").append("\"");
	        jsonResponse.append("}");

		    response.setContentType("application/json");
		    response.getWriter().write(jsonResponse.toString());
			
		} catch (NumberFormatException e) {
			
		} catch (ApplicationException e) {
			
		}
	}

	private void carregaControloTipoItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String numAdicao = request.getParameter("item");
		String numeroControlo = request.getParameter("numeroControlo");
		
		ControloItemTipoServiceT controloItemTipoServiceT = EJBUtil.getSessionInterface(ControloItemTipoServiceT.class);
		try {
			List<ControloItemTipo> listaControloItemTipo = controloItemTipoServiceT.getListaItemTipo(Short.valueOf(numAdicao), Long.valueOf(numeroControlo));
			DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
			StringBuilder jsonResponse = new StringBuilder();
		    jsonResponse.append("[");
		    
		    for (int i = 0; i < listaControloItemTipo.size(); i++) {
		    	// Na TAB Análise de risco, na segunda tabela, não deve surgir as adições sem controlo - SC 
		    	if (!listaControloItemTipo.get(i).getTipoControlo().equals(SGCConstantes.TIPO_CONTROLO_SEM_CONTROLO_CAU_COMBO)){
		    		ControloItemTipo detalhe = listaControloItemTipo.get(i);
			        jsonResponse.append("{");
			        jsonResponse.append("\"detItemControlo\":\"").append(detalhe.getChave().getDetItemControlo() != null ? detalhe.getChave().getDetItemControlo() : "").append("\",");
			        jsonResponse.append("\"numeroControlo\":\"").append(detalhe.getChave().getNumeroControlo() != null ? detalhe.getChave().getNumeroControlo() : "").append("\",");
			        jsonResponse.append("\"numeroItem\":\"").append(detalhe.getChave().getNumeroItem() != null ? detalhe.getChave().getNumeroItem() : "").append("\",");
			        jsonResponse.append("\"identificadorSSA\":\"").append(detalhe.getIdentificadorSSA() != null ? detalhe.getIdentificadorSSA() : "").append("\",");
			        DadosGerais dgAnaliseRisco = dadosGeraisService.buscarPorCodigoSistema(detalhe.getAnaliseRisco(), ctr.getSistema());
			        jsonResponse.append("\"analiseRisco\":\"").append(detalhe.getAnaliseRisco() != null ? detalhe.getAnaliseRisco()+ " - " + dgAnaliseRisco.getDescricao() : "").append("\",");
			        DadosGerais dgAreaRisco = dadosGeraisService.buscarPorCodigoSistema(detalhe.getAreaRisco(), ctr.getSistema());
			        jsonResponse.append("\"areaRisco\":\"").append(detalhe.getAreaRisco() != null ? detalhe.getAreaRisco()+ " - " + dgAreaRisco.getDescricao() : "").append("\",");
			        jsonResponse.append("\"riscoComum\":\"").append(detalhe.getRiscoComum() != null ? detalhe.getRiscoComum() : "").append("\",");
			        jsonResponse.append("\"produtoInfoRisco\":\"").append(detalhe.getProdutoInfoRisco() != null ? detalhe.getProdutoInfoRisco() : "").append("\",");
			        DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(detalhe.getTipoControlo(), ctr.getSistema());
			        jsonResponse.append("\"tipoControlo\":\"").append(detalhe.getTipoControlo() != null ? detalhe.getTipoControlo() + " - " + dg.getDescricao() : "").append("\"");
			        jsonResponse.append("}");
			        
			        if (i < listaControloItemTipo.size() - 1) {
			            jsonResponse.append(",");
			        }
				}		    	
		    }
		    jsonResponse.append("]");
		    
		    response.setContentType("application/json");
		    response.getWriter().write(jsonResponse.toString());
			
		} catch (NumberFormatException e) {
			
		} catch (ApplicationException e) {
			
		}
	}

	private void tratarResultadoAdicao(HttpServletRequest request, HttpSession session) {
		
		Long idLinha = new Long(request.getParameter("idLinha"));
		String resultado = request.getParameter("resultado");
		String idControlo = request.getParameter("idControlo");
		ControloItemTipo cit = null;
		
		ControloItemTipoServiceT controloItemService = EJBUtil.getSessionInterface(ControloItemTipoServiceT.class);
		try {
			cit = controloItemService.getControloItemTipo(idLinha);
		} catch (ApplicationException e) {
			log.error("Erro ao tentar recuperar os resistos de controlo item tipo");
		}
		
		//o objeto em sessão é um mapa, é necessário recupear a lista de acordo com a adição desejada.
		
		Map<String, List> mapaControloItemTipo = (Map)session.getAttribute(SessionConstants.ATTR_LISTA_CONTROLO_ITEM_TIPO + idControlo);
		List<ControloItemTipo> list = mapaControloItemTipo.get(idControlo + "" + cit.getChave().getNumeroItem());
		
		if(list != null) {
			for (ControloItemTipo item : list) {
		        if (item.getChave().getDetItemControlo().toString().equals(idLinha.toString())) {
		        	if(resultado.contentEquals("---")) {
		        		item.setResultado("");
		        	} else {		        		
		        		item.setResultado(resultado); 
		        		item.setFlagConfirmado(SGCConstantes.FLAG_BD_FALSO);
		        	}
		            break; 
		        }
		    }	
		}
	}
	
	public boolean conferirTodosDocumentos(String identControlo, HttpSession session){
		
		Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO+identControlo);
		if(ctrlDec!=null 
				&& ctrlDec.getListaControloItem()!=null && ctrlDec.getListaControloItem().size()>0){
			
			for(int i=0; i<ctrlDec.getListaControloItem().size();i++){
				ControloItem adic = ctrlDec.getListaControloItem().get(i);
				
				if(adic!=null && adic.getListaControloDocumentoItem()!=null
						&& adic.getListaControloDocumentoItem().size()>0){
					
					for(int x=0; x<adic.getListaControloDocumentoItem().size();x++){
						ControloDocumento docs = adic.getListaControloDocumentoItem().get(x);
						
						if(docs!=null){
							docs.setConferidorDocumento(SGCConstantes.FLAG_BD_VERDADEIRO);
							docs.setDescricaoConferido("Sim");
						}
					}
				}
			}
		}

		session.setAttribute(SessionConstants.RES_CONTROLO+identControlo, ctrlDec);
		SessionManager.getInstance().setSessao(session);
		
		return true;
	}

	private boolean desconferirTodosDocumentos(String identControlo, HttpSession session) {
		// Obtém o objeto Controlo a partir da sessão
		Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + identControlo);
		if (ctrlDec != null && ctrlDec.getListaControloItem() != null) {
			// Percorre cada item do controlo
			for (ControloItem adic : ctrlDec.getListaControloItem()) {
				if (adic != null && adic.getListaControloDocumentoItem() != null) {
					for (ControloDocumento docs : adic.getListaControloDocumentoItem()) {
						if (docs != null) {
							// Define o campo como não conferido (ajusta com os valores corretos de FLAG_BD_FALSO e a descrição, se aplicável)
							docs.setConferidorDocumento(SGCConstantes.FLAG_BD_FALSO);
							docs.setDescricaoConferido("Não");
						}
					}
				}
			}
		}
		session.setAttribute(SessionConstants.RES_CONTROLO + identControlo, ctrlDec);
		SessionManager.getInstance().setSessao(session);
		return true;
	}

	private boolean conferirTodosSelos(String identControlo, HttpSession session) {
		log.info("CausServlet#conferirTodosSelos - Conferindo todos os selos do controlo " + identControlo);

		List<ControloSeloWeb> listaSelos = (List<ControloSeloWeb>) session.getAttribute("listaSelos" + identControlo);

		if (listaSelos != null) {
			for (ControloSeloWeb selo : listaSelos) {
				selo.setIndicadorSelo(SGCConstantes.FLAG_BD_VERDADEIRO);
			}
			session.setAttribute("listaSelos" + identControlo, listaSelos);
			SessionManager.getInstance().setSessao(session);
		}

		return true;
	}

	private boolean desconferirTodosSelos(String identControlo, HttpSession session) {
		log.info("CausServlet#desconferirTodosSelos - Desconferindo todos os selos do controlo " + identControlo);

		List<ControloSeloWeb> listaSelos = (List<ControloSeloWeb>) session.getAttribute("listaSelos" + identControlo);

		if (listaSelos != null) {
			for (ControloSeloWeb selo : listaSelos) {
				selo.setIndicadorSelo(SGCConstantes.FLAG_BD_FALSO);
			}
			session.setAttribute("listaSelos" + identControlo, listaSelos);
			SessionManager.getInstance().setSessao(session);
		}

		return true;
	}
	
	private boolean marcarUnicoDocumento(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
	    String idDocumentoParam = request.getParameter("id_documento"); // Ex: "control_doc_0_virt_F_chk_1"
	    String identControlo = request.getParameter("idControlo");
	    
	    if (idDocumentoParam == null || identControlo == null) {
	        return false;
	    }
	    
	    Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + identControlo);
	    
	    if (ctrlDec != null && ctrlDec.getListaControloItem() != null) {
	        for (ControloItem adic : ctrlDec.getListaControloItem()) {
	            if (adic != null && adic.getListaControloDocumentoItem() != null) {
	                for (ControloDocumento doc : adic.getListaControloDocumentoItem()) {
	                    if (doc != null) {
	                        String chaveDocumento = "control_doc_" 
	                                + adic.getNumAdicao() 
	                                + "_virt_" + doc.getIndVirtual() 
	                                + "_chk_" + doc.getChave().getIdentDocumento();
	                        
	                        // Compara a chave construída com o id recebido do checkbox
	                        if (chaveDocumento.equals(idDocumentoParam)) {
	                            doc.setConferidorDocumento(SGCConstantes.FLAG_BD_VERDADEIRO);
	                            doc.setDescricaoConferido("Sim");

	                        }
	                    }
	                }
	            }
	        }
	    }
	    

	    session.setAttribute(SessionConstants.RES_CONTROLO + identControlo, ctrlDec);
	    SessionManager.getInstance().setSessao(session);
	    
	    return true;
	}

	private boolean marcarUnicoSelo(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
		// Espera receber um parâmetro com o id do checkbox, ex.: "control_selo_2_chk_15"
		String idSeloParam = request.getParameter("id_selo");
		String identControlo = request.getParameter("idControlo");
		String statusParam = request.getParameter("status");

		if (idSeloParam == null || identControlo == null) {
			return false;
		}

		// Recupera a lista de selos (geralmente armazenada na sessão com chave "listaSelos" + identControlo)
		List<ControloSeloWeb> listaSelos = (List<ControloSeloWeb>) session.getAttribute("listaSelos" + identControlo);

		if (listaSelos != null) {
			for (ControloSeloWeb selo : listaSelos) {
				// Constroi a chave do checkbox conforme definido no getConferido()
				String checkboxId = "control_selo_" + selo.getChave().getNumItem() + "_chk_" + selo.getChave().getIdSelo();
				if (checkboxId.equals(idSeloParam)) {
					// Se statusParam é "true", marca como conferido; se for "false", desmarca.
					if ("true".equals(statusParam)) {
						selo.setIndicadorSelo(SGCConstantes.FLAG_BD_VERDADEIRO);
					} else {
						selo.setIndicadorSelo(SGCConstantes.FLAG_BD_FALSO);
					}
				}
			}
		}

		// Atualiza a sessão com a lista modificada
		session.setAttribute("listaSelos" + identControlo, listaSelos);
		SessionManager.getInstance().setSessao(session);

		return true;
	}

}

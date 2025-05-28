package com.siemens.ssa.communicator.web.jsp.control.caus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;

import com.siemens.ssa.communicator.util.SGCUtils;
import org.apache.click.ActionListener;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.Option;
import org.apache.commons.lang.StringUtils;

import com.siemens.security.session.SessionManager;
import com.siemens.service.interfaces.ControloDocumentoAdicionalService;
import com.siemens.service.interfaces.DadosGeraisService;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumento;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoAdicional;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumentoAdicionalPK;
import com.siemens.ssa.communicator.pojo.interfaces.DadosGerais;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;
import com.siemens.ssa.communicator.web.jsp.control.ControlResult;
import com.siemens.ssa.communicator.web.jsp.control.TabGeralDocumental;
import com.siemens.ssa.communicator.web.jsp.control.TabGeralDocumentalGeneric;
import com.siemens.ssa.communicator.web.jsp.control.pojo.ControloDocumentoWeb;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.WebConstants;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Button;
import pt.atos.web.click.controls.field.W3Label;
import pt.atos.web.click.controls.field.W3Select;
import pt.atos.web.click.controls.field.W3TextField;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.ControlFieldColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

public class TabDocumentosCAUS extends TabGeralDocumentalGeneric {

	private static final long serialVersionUID = 821290961702149287L;

	public CompleteFieldSetPanel headerPanel = new CompleteFieldSetPanel("paineldocumentos", "", "");
	
	public W3Select comboCodigosPorACL = new W3Select("comboCodigosPorACL", false);
	public DgitaTable tabelaDocumentosAdicionais = new DgitaTable("controlo_documentos_adicionais_tabela");
	public W3Select comboValoresPorSistema = new W3Select("comboValoresPorSistema", false);
	public W3TextField numeroReferencia = new W3TextField("control_add_input_numero_referencia");
	public W3TextField descricao = new W3TextField("control_add_input_descricao");
	W3Button botaoAdicionar = new W3Button("addInformacao", "Adicionar");
	W3Button botaoCancelar = new W3Button("cancelarAlteracao", "Cancelar");
	W3Button marcarTodos = new W3Button("marcarTodos", "Conferir Todos");
	W3Button desconferirTodos = new W3Button("desconferirTodos","Desconferir Todos");
	DgitaLayoutPage controlPage = null;
	private static Log log = Log.getLogger(TabGeralDocumental.class);

	public Controlo ctrl;
	public String url;

	public TabDocumentosCAUS(DgitaLayoutPage tbControl, boolean readOnly, String control,
			DeclarationProcessor declarationProcessor, Controlo ctrl) {
			
		url = this.getContextPath()+tbControl.getContext().getPagePath(ControlResult.class);
		url += "?readOnly=" + readOnly;
		
		controlPage = tbControl;
		
		headerPanel.setFieldSetLayout(new FieldSetLayout(5, new String[] { "5%", "5%", "20%", "3%", "2%", "0%", "10%", "10%", "10%", "10%"}));

		this.ctrl = ctrl;
		PhyControlDoc = new Tab("PhyControlDoc");
		identControlo = ctrl.getChave().getNumeroControlo().toString();
		log.info("TabGeralDocumental#idControlo:" + identControlo);
		docsTable.setAttribute("align", "center");

		ControlFieldColumn col1 = new ControlFieldColumn("Documento");
		col1.setHeaderTitle(declarationProcessor.getWidgetLabel(col1.getId()));
		
		ControlFieldColumn columnFicheiro = new ControlFieldColumn("ficheiro", "Ficheiro");
		columnFicheiro.setAttribute("style", "text-align:center");
		columnFicheiro.setDecorator(new Decorator() {
			
			@Override
			public String render(Object object, Context context) {
				ControloDocumentoWeb item = (ControloDocumentoWeb) object;
				String ico = "";
				if(item != null && item.getFilenetLink() != null) {
					ico = "<a href=\"#\" onclick=\"visualizarDocumento('" + item.getFilenetLink() + "'); return false;\" class=\"fa fa-file-text-o\"></a>";
				}
				return ico;
			}
		});

		ControlFieldColumn column = new ControlFieldColumn("conferido", "Conferido");
		column.setAttribute("style", "text-align:center");
		docsTable.addColumn(col1);
		docsTable.addColumn(columnFicheiro);
		docsTable.addColumn(column);
		
		docsTable.setWidth(WebConstants.POPUP_TABLE_SIZE);

		docsTable.setSelectable(false);
		docsTable.setOneRowOnly(false);
		if (!readOnly) {
			//confereDoc.setOnClick("");
			marcarTodos.setAttribute("onclick", "conferir()");
			docsTable.addButtonToTable(marcarTodos, tbControl.getForm().getName());
			desconferirTodos.setAttribute("onclick", "desconferir()");
			docsTable.addButtonToTable(desconferirTodos, tbControl.getForm().getName());
		}

		int[] flds = { 1};
		PhyControlDoc.setNumberFieldsPerLine(flds);

		headerPanel.add(docsTable, 5);

		log.info("TabGeralDocumental#readOnly:" + readOnly + ";isReadOnly:" + isReadOnly());

		W3Label containerPedidosAdicionais = new W3Label("container_pedidos_adicionais");
		containerPedidosAdicionais.setAttribute("style", "margin-top:24px");
		containerPedidosAdicionais.setLabel(declarationProcessor.getWidgetLabel("control_txtDadosAdicionais"));

		headerPanel.add(containerPedidosAdicionais,5);	
		
		comboValoresPorSistema.setLabel(declarationProcessor.getWidgetLabel("control_combo_valor"));
		comboValoresPorSistema.setLabelShown(true);	
		comboValoresPorSistema.setFocus(false);
		comboValoresPorSistema.setWidth("300px");
		comboValoresPorSistema.setAttribute("onchange", "getCodigos(); return false;");
		comboValoresPorSistema.setAttribute("style", "width: 300px");
		
		List<Option> options = new ArrayList<Option>();
		for (String option : SGCConstantes.getCL(ctrl.getSistema())){
			String description = SGCConstantes.getCLDescription(option, true);
			options.add(new Option(option, option + " - " + description));
		}
		Option neutra = new Option("----");
		options.add(0, neutra);
		comboValoresPorSistema.setOptionList(options);
		
		comboValoresPorSistema.setActionListener(new ActionListener() {
            @Override
            public boolean onAction(Control source) {
            
                onSelectChanged();
               
                return true;
            }
        });
		
		comboCodigosPorACL.setLabel(declarationProcessor.getWidgetLabel("control_combo_acl"));
		comboCodigosPorACL.setLabelShown(true);
		comboCodigosPorACL.setFocus(false);
		comboCodigosPorACL.setWidth("300px");
		comboCodigosPorACL.setAttribute("style", "width: 300px");

		numeroReferencia.setLabel(declarationProcessor.getWidgetLabel("control_label_numero_referencia"));
		numeroReferencia.setLabelShown(true);
		numeroReferencia.setAttribute("style", "width: 150px");
		numeroReferencia.setWidth("150px");
		
		descricao.setLabel(declarationProcessor.getWidgetLabel("control_label_descricao"));
		descricao.setLabelShown(true);
		descricao.setAttribute("style", "width: 424px");
		descricao.setWidth("424px");
		
		botaoAdicionar.setAttribute("onclick", "adicionarLinhaInfomacaoAdicional(); return false;");
		botaoAdicionar.setStyle("vertical-align","bottom;");
		botaoAdicionar.setStyle("margin-bottom", "5px;");
		form.add(botaoAdicionar);
		
		botaoCancelar.setAttribute("onclick", "cancelarEdicao(); return false;");
		botaoCancelar.setStyle("display", "none");
		botaoCancelar.setStyle("vertical-align","bottom");
		botaoCancelar.setStyle("margin-bottom", "5px");
		form.add(botaoCancelar);
		
		headerPanel.add(comboValoresPorSistema, 1);
		headerPanel.add(comboCodigosPorACL, 1);
		headerPanel.add(numeroReferencia, 1);
		headerPanel.add(descricao, 1);
		headerPanel.setStyle("vertical-align", "bottom");
		
		DIV buttonContainer = new DIV();
		buttonContainer.setStyle("text-align","right;");
		buttonContainer.setStyle("vertical-align", "bottom;");
		if (!readOnly) {
			buttonContainer.add(botaoAdicionar);
			buttonContainer.add(botaoCancelar);
		}

		headerPanel.add(buttonContainer, 5);

		setTabelasAdicionais(declarationProcessor, ctrl, readOnly);

		PhyControlDoc.addField(headerPanel);
		

		if (readOnly) {
			PhyControlDoc.setReadonly(readOnly);
		}

		if (SGCUtils.validaSistemaCAU(ctrl.getSistema()) && ctrl.getFlagNotificacaoDoc().equals("F")){
			comboValoresPorSistema.setDisabled(true);
			comboCodigosPorACL.setDisabled(true);
			numeroReferencia.setDisabled(true);
			descricao.setDisabled(true);
			botaoAdicionar.setDisabled(true);
		}
	}
	
	public void onSelectChanged() {
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		try {
			comboCodigosPorACL.setOptionList(dadosGeraisService.getDescricaoPorValorSistema(comboValoresPorSistema.getValue(), ctrl.getSistema()));
		} catch (ApplicationException e) {
			log.info("Erro ao recupear valores" + e.getMessage());
		}
    }

	public boolean addInformacao(String id, boolean alteracao) {
		
		StringBuilder sb = new StringBuilder();
		
		if(!StringUtils.isNotBlank(descricao.getValue())) {
			//sb.append("Informe a descrição");
		}
		if(!StringUtils.isNotBlank(numeroReferencia.getValue())) {
			//sb.append(", Informe a número de referência");
		}
		
		if(comboValoresPorSistema.getValue().equals("----")) {
			sb.append("Informe o valor da ACL");
		}
		
		if(!sb.toString().equals("")) {
			controlPage.showErrorMessage(sb.toString());
			return true;
		}
		
		HttpSession session = getSession();
		List<ControloDocumentoAdicional> docsSaved = (List<ControloDocumentoAdicional>) getSession().getAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctrl.getChave().getNumeroControlo());

		if(docsSaved == null) {
			docsSaved = new ArrayList<ControloDocumentoAdicional>();
		}
		
		if(alteracao){
			Optional<ControloDocumentoAdicional> docAlteradoOptional  = docsSaved.stream().filter(s -> s.getChave().getId().compareTo(Short.valueOf(id)) == 0).findFirst();
			if (docAlteradoOptional .isPresent()) {
		        ControloDocumentoAdicional docAlterado = docAlteradoOptional.get();
		        docAlterado.setDescricao(descricao.getValue());
		        docAlterado.setNumeroReferencia(numeroReferencia.getValue());
		        String[] codigo = comboCodigosPorACL.getValueLabel().split("-");
		        docAlterado.setTipoDocumento(codigo[0]);
		        docAlterado.setDescricaoTipoDocumento(comboCodigosPorACL.getValueLabel());
		        docAlterado.setValorDocumento(comboValoresPorSistema.getValue());       
		        int index = docsSaved.indexOf(docAlterado);
		        if(index != -1) {
		            docsSaved.set(index, docAlterado);
		        }
		    }
		} else {			
			ControloDocumentoAdicional doc = new ControloDocumentoAdicional();
			ControloDocumentoAdicionalPK pk = new ControloDocumentoAdicionalPK();
			int number = docsSaved.size() + 1;
			pk.setId(Short.decode(number+""));
			doc.setChave(pk);
			doc.setDescricao(descricao.getValue());
			doc.setNumeroReferencia(numeroReferencia.getValue());
			String[] codigo = comboCodigosPorACL.getValueLabel().split("-");
			doc.setTipoDocumento(codigo[0]);
			doc.setDescricaoTipoDocumento(comboCodigosPorACL.getValueLabel());
			doc.setValorDocumento(comboValoresPorSistema.getValue());       
			docsSaved.add(doc);
		}
		tabelaDocumentosAdicionais.setRowList(docsSaved);
		getSession().setAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS, docsSaved);
		SessionManager.getInstance().setSessao(session);
		limparFormulario();
		return false;
	}
	
	public static short generateShort() {
		Random random = new Random();
        return (short) random.nextInt(Short.MAX_VALUE + 1);
    }

	private void setTabelasAdicionais(DeclarationProcessor declarationProcessor, Controlo controlo, boolean readOnly) {
		
		String pattern = "yyyy/MM/dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		
		AnonymousCallColumn edit = new AnonymousCallColumn("editar");
		edit.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				String itemId = "";
				ControloDocumentoAdicional item = (ControloDocumentoAdicional) row;
            	if(item.getChave().getId() != null ) {            		
            		itemId = item.getChave().getId().toString();
            	}
                if(item.getDataPedido() == null) {                	
                	return "<a href=\"#\" onclick=\"editarDadosAdicional('" + itemId + "'); return false;\" class=\"fa fa-pencil fa-lg\"></a>";
                }
                return "";
			}
		}); 

		AnonymousCallColumn colunaSequencia = new AnonymousCallColumn("descNumAdicao");
		colunaSequencia.setWidth("15%");
		colunaSequencia.setHeaderTitle("Sequencia");
		colunaSequencia.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object obj, Context ctx, int index) {
				ControloDocumentoAdicional item = (ControloDocumentoAdicional) obj;
				
				return item.getChave().getId().toString();
			}
		});

		AnonymousCallColumn colunaDataPedido = new AnonymousCallColumn("dataPedido");
		colunaDataPedido.setWidth("15%");
		colunaDataPedido.setHeaderTitle("Data Pedido");
		colunaDataPedido.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object obj, Context ctx, int index) {
				ControloDocumentoAdicional item = (ControloDocumentoAdicional) obj;
				if(item.getDataPedido() != null) {
					return simpleDateFormat.format(item.getDataPedido());
				}
				return "";
			}
		});

		AnonymousCallColumn colunaDataNotificacao = new AnonymousCallColumn("dataNotificacao");
		colunaDataNotificacao.setWidth("15%");
		colunaDataNotificacao.setHeaderTitle("Data Notificação");
		colunaDataNotificacao.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object obj, Context ctx, int index) {
				ControloDocumentoAdicional item = (ControloDocumentoAdicional) obj;
				if(item.getDataNotificacao() != null) {
					return simpleDateFormat.format(item.getDataNotificacao());
				}
				return "";
			}
		});

		Column colunaTipoDocumento = new Column("descricaoTipoDocumento");
		colunaTipoDocumento.setWidth("15%");
		colunaTipoDocumento.setHeaderTitle("Tipo Documento");

		Column colunaNumeroReferencia = new Column("numeroReferencia");
		colunaNumeroReferencia.setWidth("15%");
		colunaNumeroReferencia.setHeaderTitle("N Referência");

		Column colunaDescricao = new Column("descricao");
		colunaDescricao.setWidth("25%");
		colunaDescricao.setHeaderTitle("Descrição");

		tabelaDocumentosAdicionais.addColumn(edit);
		tabelaDocumentosAdicionais.addColumn(colunaSequencia);
		tabelaDocumentosAdicionais.addColumn(colunaDataPedido);
		tabelaDocumentosAdicionais.addColumn(colunaDataNotificacao);
		tabelaDocumentosAdicionais.addColumn(colunaTipoDocumento);
		tabelaDocumentosAdicionais.addColumn(colunaNumeroReferencia);
		tabelaDocumentosAdicionais.addColumn(colunaDescricao);

		tabelaDocumentosAdicionais.setAttribute("style", "margin-top:24px");
	
		//AjaxTableActionLink lnk = new AjaxTableActionLink("Tratar", url,"tratar_edicao_documento_adicional", "main-form");
		//tabelaDocumentosAdicionais.setEditLink(lnk);
				
		AnonymousCallColumn remover = new AnonymousCallColumn("remover", "");
		remover.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				String itemId = "";
				ControloDocumentoAdicional item = (ControloDocumentoAdicional) row;
            	if(item.getChave().getId() != null ) {            		
            		itemId = item.getChave().getId().toString();
            	}
                if(item.getDataPedido() == null) {                	
                	return "<a href=\"#\" onclick=\"excluirDocumentoAdicional('" + itemId + "'); return false;\" class=\"fa fa-trash-o\"></a>";
                }
                return "";
			}
		}); 
		
		if(!readOnly) {			
			tabelaDocumentosAdicionais.addColumn(remover);
		}
		headerPanel.add(tabelaDocumentosAdicionais, 5);
		
		//Recupera dados existentes no banco de dados
		List<ControloDocumentoAdicional> docsSaved = (List<ControloDocumentoAdicional>) getSession().getAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctrl.getChave().getNumeroControlo());

		if(docsSaved == null) {
			docsSaved = new ArrayList<ControloDocumentoAdicional>();
		}
		ControloDocumentoAdicionalService documentoAdicionalServiceBean = EJBUtil.getSessionInterface(ControloDocumentoAdicionalService.class);
		try {
			DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
			
			for (ControloDocumentoAdicional item : documentoAdicionalServiceBean.getListaDocAdicional(controlo)) {

				DadosGerais dadosGerais = dadosGeraisService.buscarPorCodigoSistema(item.getTipoDocumento() , ctrl.getSistema());
				item.setDescricaoTipoDocumento(dadosGerais.getCodigo() + " - " + dadosGerais.getDescricao());
				if(!docsSaved.contains(item)) {
					docsSaved.add(item);						
				}
			}

			tabelaDocumentosAdicionais.setRowList(docsSaved);
			HttpSession session = getSession();
			session.setAttribute(SessionConstants.ATTR_LISTA_DOCUMENTOS_ADICIONAIS + ctrl.getChave().getNumeroControlo(), docsSaved);
			SessionManager.getInstance().setSessao(session);
			
		} catch (ApplicationException e) {
			log.info("Erro ao recueprar o detalhe dos dados adicionais");
		}
	}
	
	public void editarLinhaDocumentoAdicional(String idLinha) {
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		ControloDocumentoAdicional cda =  (ControloDocumentoAdicional) tabelaDocumentosAdicionais.getTableItemFromTable(idLinha);
		try {
			DadosGerais dadosGerais = dadosGeraisService.buscarPorCodigoSistema(cda.getTipoDocumento() , ctrl.getSistema());
			String valor = dadosGerais.getChave().getValor() + " - " + SGCConstantes.getCLDescription(dadosGerais.getChave().getValor(), true);
			comboValoresPorSistema.setOptionByValue(valor);
			descricao.setValue(cda.getDescricao());
			numeroReferencia.setValue(cda.getNumeroReferencia());
			onSelectChanged();
			comboCodigosPorACL.setOptionByValue( dadosGerais.getCodigo()+" - "+dadosGerais.getDescricao());
		} catch (ApplicationException e) {
			log.error("Erro ao recueprar o detalhe dos dados adicionais");
		}
		botaoAdicionar.setAttribute("onclick", "alterarLinhaInfomacaoAdicional("+idLinha+"); return false;");
		botaoAdicionar.setLabel("Alterar");
		botaoCancelar.setStyle("display", "inline-block;");
	}

	private void getDadosDocumentos() {

		ArrayList<ControloDocumento> listDocs = null;
		TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);
		listDocs = srvInfo.getInfoDoc(ctrl.getSistema(), ctrl.getNumIdentificacao(),
				ctrl.getChave().getNumeroControlo());

	}
	
	private void limparFormulario() {
		comboValoresPorSistema.setOptionByValue("----");
		descricao.setValue("");
		numeroReferencia.setValue("");
		comboCodigosPorACL.setOptionList(Arrays.asList(new Option("")));
	}
	
	
}

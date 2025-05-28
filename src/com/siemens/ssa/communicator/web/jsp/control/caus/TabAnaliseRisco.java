package com.siemens.ssa.communicator.web.jsp.control.caus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.click.Context;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;

import com.siemens.service.interfaces.ControloItemServiceT;
import com.siemens.service.interfaces.DadosGeraisService;
import com.siemens.service.interfaces.TabelasApoioServiceT;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItemTipo;
import com.siemens.ssa.communicator.pojo.interfaces.DadosGerais;
import com.siemens.ssa.communicator.util.declproc.DeclarationProcessor;

import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.ejb.EJBUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.panels.CompleteFieldSetPanel;
import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.controls.panels.FieldSetLayout;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.DgitaTable;

public class TabAnaliseRisco {
	
	protected Tab tabAnaliseRisco;
	
	public CompleteFieldSetPanel header = new CompleteFieldSetPanel("painel", "", "");
	
	public DgitaTable tabelaPrincipal = new DgitaTable("tabela_controlo_item");
	public DgitaTable tabelaControloTipoItem = new DgitaTable("tabela_controlo_tipo_item");
	public DgitaTable tabelaControloTipoItemDetalhe = new DgitaTable("tabela_controlo_tipo_item_detalhe");	
	
	public static Log log = Log.getLogger(TabAnaliseRisco.class);
	
	public TabAnaliseRisco(Controlo ctrl, DeclarationProcessor declarationProcessor) {
		tabAnaliseRisco = new Tab("TabAnaliseRisco");
		tabAnaliseRisco.setLegend("Análise de Risco");
		
		header.setFieldSetLayout(new FieldSetLayout(1, new String[] { "50%", "50%"}));
		header.setAttribute("vertical-align", "bottom");
		
		preencheTabelaPrincipal(ctrl, declarationProcessor);
		
		DIV divDetalheItemTipo = new DIV();
		divDetalheItemTipo.setId("container_detalhe_item_tipo");
		
		int[] flds = {1};
		tabAnaliseRisco.setNumberFieldsPerLine(flds);
		tabAnaliseRisco.addField(header);
		
	}
	
	public void preencheTabelaPrincipal(Controlo ctrl, DeclarationProcessor declarationProcessor) {
		
		tabelaPrincipal.setAttribute("align", "center");
		tabelaPrincipal.setId("tabela_principal");
		
		AnonymousCallColumn view = new AnonymousCallColumn("edit", "");
		view.setCall(new AnonymousCall() {
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				
				String itemId = "";
				ControloItem item = (ControloItem) row;            	
                itemId = item.getChave().getNumeroItem().toString().concat("','").concat(item.getChave().getNumeroControlo().toString());
                
                return "<a href=\"#\" onclick=\"carregaControloTipoItem('" + itemId + "'); return false;\" class=\"fa fa-eye fa-lg\"></a>";
			}
		}); 
		
		tabelaPrincipal.addColumn(view);
		
		TabelasApoioServiceT srvInfo = EJBUtil.getSessionInterface(TabelasApoioServiceT.class);

		AnonymousCallColumn colunaNumeroAdicao = new AnonymousCallColumn("descNumAdicao");
		colunaNumeroAdicao.setHeaderTitle(declarationProcessor.getWidgetLabel("column_numero_adicao_tabela_analise_risco"));
		colunaNumeroAdicao.setCall(new AnonymousCall() {
			
			@Override
			public String getDataContent(Object obj, Context ctx, int index) {
				ControloItem item = (ControloItem) obj;
				
				Map<Short, List<String>> mapDescricao = srvInfo.getInfoAdi(ctrl.getSistema(),
						ctrl.getChave().getNumeroControlo());
				
				List<String> descNumAdicao = mapDescricao
						.get(item.getChave().getNumeroItem());
				
				if(descNumAdicao != null && !descNumAdicao.isEmpty()) {
					return descNumAdicao.get(0);
				}
				
				return "";
			}
		});
		
		tabelaPrincipal.addColumn(colunaNumeroAdicao);	
		
		DadosGeraisService dadosGeraisService = EJBUtil.getSessionInterface(DadosGeraisService.class);
		
		Column codigoIdentificacaoAnaliseRisco = new Column("analiseRisco");
		codigoIdentificacaoAnaliseRisco.setHeaderTitle(declarationProcessor.getWidgetLabel("column_codigo_identificacao_tabela_analise_risco"));
		codigoIdentificacaoAnaliseRisco.setDecorator(new Decorator() {	
			@Override
			public String render(Object row, Context context) {
				ControloItem item = (ControloItem) row;
				try {
					if (item != null && item.getAnaliseRisco() != null){
						DadosGerais dg = dadosGeraisService.buscarPorCodigoSistema(item.getAnaliseRisco(), ctrl.getSistema());
						return item.getAnaliseRisco() +" - "+ dg.getDescricao();		
					}
				} catch (ApplicationException e) {
				}
				return "";
			}
		});
		tabelaPrincipal.addColumn(codigoIdentificacaoAnaliseRisco);	
		
		header.add(tabelaPrincipal, 1);
		
		
		
		Column tipoControlo = new Column("column_tipo_controlo_tabela_analise_risco");
		tipoControlo.setHeaderTitle(declarationProcessor.getWidgetLabel(tipoControlo.getId()));
		tipoControlo.setDecorator(new Decorator() {
			@Override
			public String render(Object obj, Context context) {
				ControloItem item = (ControloItem) obj;
				
				String descricao = "";
				if(item.getTipoControlo() != null && declarationProcessor.ctrl.getSistema() != null){							
					try {
						DadosGerais dadosGerais = dadosGeraisService.buscarPorCodigoSistema(item.getTipoControlo(), declarationProcessor.ctrl.getSistema());
						return dadosGerais != null ? dadosGerais.getDescricao() : "";
					} catch (ApplicationException e) {
						log.error("Erro ao tentar recuperar a descrição para o tipo controlo: " + item.getTipoControlo() + " para o Sistema: " + declarationProcessor.ctrl.getSistema() );
					}
				}
				return descricao;
			}
		});
		
		tabelaPrincipal.addColumn(tipoControlo);
		
		ControloItemServiceT controloItemService = EJBUtil.getSessionInterface(ControloItemServiceT.class);
		try {
			ArrayList<ControloItem> listaAdicoes = controloItemService.getListaAdicoes(ctrl);
			if(listaAdicoes.size() > 0) {
				listaAdicoes.remove(0);
				// Na TAB Análise de risco, na tabela, não deve surgir as adições sem controlo - SC 
				for (int i = 0; i < listaAdicoes.size(); i++) {
					if (listaAdicoes.get(i).getTipoControlo().equals(SGCConstantes.TIPO_CONTROLO_SEM_CONTROLO_CAU_COMBO)){
						listaAdicoes.remove(i);
					}
				}				
			}
			tabelaPrincipal.setRowList(listaAdicoes);
			
		} catch (ApplicationException e) {
		
		}
		
	}
	
	public Tab getTab() {
		return tabAnaliseRisco;
	}

}

package com.siemens.ssa.communicator.web.jsp.control;

import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.util.SessionConstants;
import org.apache.click.control.FieldSet;
import org.apache.commons.lang.StringUtils;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.sgccomunicator.utils.constants.RequestConstants;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.field.W3Radio;
import pt.atos.web.click.controls.field.W3RadioGroup;
import pt.atos.web.click.controls.panels.ExpandableFieldSetPanel;
import pt.atos.web.click.page.DgitaLayoutPage;

import javax.servlet.http.HttpSession;


/**
 * Página que permite a um utilizador aceder à Próxima acção na sequencia de um controlo não conforme
 */
public class InqueritoProximaAccao extends DgitaLayoutPage {

    private static Log log = Log.getLogger(InqueritoProximaAccao.class);
    private static final long serialVersionUID = -1L;

    /**
     * Variavel para controlar na jsp através de velocity o fecho da janela.
     */
    public boolean closeWindow = false;
    public String returnVal;

    public InqueritoProximaAccao() {
        super();
    }

    public boolean subm_adition() {

        // TODO obter valor correcto
        if (form.isValid()) {
            returnVal = ((W3RadioGroup) ((FieldSet) (form.getControl("geral"))).getControl("radio_grupo_accao")).getValue();
            closeWindow = true;
        } else {
            showInfoMessage("");
        }

        return form.isValid();
    }

    @Override
    public void setFormData() {
    }

    @Override
    public void getFormData() {
    }

    @SuppressWarnings("boxing")
    @Override
    protected void buildPage() {

        form.setValidate(true);

        Boolean isAutSaida = false;
        String show = "inquerito_textoInicial";
        String breadcrumb = "page.breadcrumb";

        String idControlo = getContext().getRequest().getParameter("idControlo");
        String autSaida = getContext().getRequest().getParameter("autSaida");
        if (StringUtils.isNotEmpty(autSaida))
            isAutSaida = Boolean.parseBoolean(autSaida);

        HttpSession session = getSession();
        String operacao = (String) session.getAttribute(RequestConstants.OPERACAO_PESQUISA);

        if (StringUtils.isNotBlank(operacao)) {
            show = show + operacao;
            breadcrumb = breadcrumb + operacao;
        }
        breadCrumbPath = getMessageNoException(breadcrumb);
        showInfoMessageFromKey(show);

        boolean existSaida = avaliaSaida();

        //Operacoes
        boolean isNE = false;
        boolean isRect = false;
        boolean isRev = false;
        boolean isCap = false;

        Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + idControlo);

        // Grupo Option
        W3RadioGroup grupoAccao = new W3RadioGroup("radio_grupo_accao", "Acção", true);
        W3Radio selectPropostaRect = new W3Radio(SGCConstantes.PROXIMA_ACCAO_RETIFICACACAO_PROPOSTA_ESTRUTURADA, "Elaboração de declaração rectificada");
        //W3Radio selectPropostaRev = new W3Radio(SGCConstantes.PROXIMA_ACCAO_REVISAO_PROPOSTA_ESTRUTURADA,"Elaboração de declaração revista");
        W3Radio selectPropostaNaoRect = new W3Radio(SGCConstantes.PROXIMA_ACCAO_RETFICACACAO_PROPOSTA_NAO_ESTRUTURADA, "Proposta de rectificação não estruturada");
        //W3Radio selectPropostaNaoRev = new W3Radio(SGCConstantes.PROXIMA_ACCAO_REVISAO_PROPOSTA_NAO_ESTRUTURADA,"Proposta de revisão não estruturada");
        W3Radio selectAnulacao = new W3Radio(SGCConstantes.PROXIMA_ACCAO_ANULACAO, "Anulação da declaração");

        W3Radio selectNEstruturada = new W3Radio(SGCConstantes.PROXIMA_ACCAO_N_ESTRUTURADA, "Efetuar proposta de alteração não estruturada");
        W3Radio selectEstruturada = new W3Radio(SGCConstantes.PROXIMA_ACCAO_ESTRUTURADA, "Efetuar proposta de alteração estruturada");
        W3Radio selectAnulacaoImpec = new W3Radio(SGCConstantes.PROXIMA_ACCAO_ANULACAO, "Efetuar anulação da declaração");
        W3Radio selectAnulacaoDLCC2 = new W3Radio(SGCConstantes.PROXIMA_ACCAO_ANULACAO, "Efetuar anulação da declaração.");
        W3Radio selectSaidaNaoAutorizadaDSSNR = new W3Radio(SGCConstantes.PROXIMA_ACCAO_SAIDANAUT, "Saída não Autorizada");


        if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_SFA)) {
            if (ctrlDec.getTipoControlo().toString().equals(SGCConstantes.TIPO_CONTROLO_CAP_COMBO)) {
                //if(isAutSaida){
                //grupoAccao.add(selectPropostaRev);
                //} else {
                grupoAccao.add(selectPropostaRect);
                //}
            } else {
                if (SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(ctrlDec.getResultadoControlo())) {
                    //if(isAutSaida){
                    //grupoAccao.add(selectPropostaRev);
                    //} else {
                    grupoAccao.add(selectPropostaRect);
                    //}
                }
                if (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(ctrlDec.getResultadoControlo()))
                    grupoAccao.add(selectAnulacao);
            }
        } else if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
            if (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(ctrlDec.getResultadoControlo()))
                grupoAccao.add(selectAnulacaoDLCC2);
        } else if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_IMPEC) ||
                ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DAIN) ||
                ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU) ||
                ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_TRACAU) ||
                ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST)
        ) {
            if (
                    SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_COMBO.equals(ctrlDec.getResultadoControlo())
                            || SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.equals(ctrlDec.getResultadoControlo())
                            || SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_1P_COMBO.equals(ctrlDec.getResultadoControlo())
                            || SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_CAU_COMBO.equals(ctrlDec.getResultadoControlo())
            ) {
                grupoAccao.add(selectNEstruturada);
                grupoAccao.add(selectEstruturada);
                grupoAccao.add(selectAnulacaoImpec);
            }

        } else if (ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_DSS) ||
                ctrlDec.getSistema().equals(SGCConstantes.SISTEMA_NR)) {

            if (SGCConstantes.RESULTADO_CONTROLO_NAO_CONFORME_CAU_COMBO.equals(ctrlDec.getResultadoControlo())) {
                grupoAccao.add(selectSaidaNaoAutorizadaDSSNR);
            }
            if (SGCConstantes.RESULTADO_CONTROLO_DISCREPANCIAS_CAU_COMBO.equals(ctrlDec.getResultadoControlo())) {
                grupoAccao.add(selectNEstruturada);
                grupoAccao.add(selectEstruturada);
            }
        }


        grupoAccao.setVerticalLayout(true);
        grupoAccao.setLabelShown(false);

        this.setReadOnly(false);

        ExpandableFieldSetPanel fsGeral = new ExpandableFieldSetPanel("geral", "Selecionar Próxima Ação");
        fsGeral.setLabel(null);
        fsGeral.setShowBorder(false);
        fsGeral.setForm(form);
        fsGeral.setColumns(1);

        fsGeral.add(grupoAccao);

        // serve para a pagina retornar a si mesma aquando da validação dos
        // campos obrigatorios, previne a abertura de uma
        // nova pagina.
        headMenu = null;
        form.setButtonAlign("right");

        W3Submit bt1 = new W3Submit("btnContinuar", null, this, "subm_adition");
        // Voltar fecha a página
        W3Submit bt2 = new W3Submit("btnVoltar", null, this, "subm_adition");

        bt1.setParent(this);
        bt2.setParent(this);

        form.add(fsGeral);
//		form.add(bt1);
//		form.add(bt2);
        //Limpar a sessao
//		session.setAttribute(RequestConstants.OPERACAO_PESQUISA, null);
    }

    @Override
    protected void postInit() {
        setFormData();
    }

    @Override
    public boolean onCancelar() {
        return false;
    }

    @Override
    public boolean onGravar() {
        return false;
    }

    private boolean avaliaSaida() {

//		HttpSession session = getSession();
//		Circuito circ = (Circuito) session.getAttribute(SessionConstants.ATTR_CIRCUITO);
//		if(circ==null){
//			circ = (Circuito) session.getAttribute(SessionConstants.ATTR_CIRCUITO_ANULACAO);
//		}
//		if(circ!=null){
//			try {
//				DeclaracaoService srvDau=EJBUtil.getSessionInterface(DeclaracaoService.class);
//				Declaracao dau = srvDau.getDeclaracaoPorNumeroProvisorioEano(circ.getNumProvDec(),circ.getAnoProvDec());
//			
//				if(dau!=null){
//					CircuitoService srvCircuit=EJBUtil.getSessionInterface(CircuitoService.class);
//					ArrayList<Circuito> circtList = srvCircuit.getCircuitoByNumAceitacao(null,null,dau.getNumeroAceitacaoCompleto(),
//																		new Short[]{DominioConstantes.TIPO_FASE_AUTORIZACAO_SAIDA});
//				
//					if(circtList!=null && circtList.size()>0){
//						Circuito aut = circtList.get(0);
//						if(aut!=null
//								&& !new Short(DominioConstantes.ESTADO_FASE_IGNORAR).toString().equals((aut.getEstado()))){
//							return true;	
//						}
//					}
//				}
//			} catch (STADAException e) {
//				return false;
//			}
//		}
        return false;
    }
}
package com.siemens.ssa.communicator.web.jsp.control;

import com.siemens.security.session.SessionManager;
import com.siemens.ssa.communicator.pojo.interfaces.Controlo;
import com.siemens.ssa.communicator.pojo.interfaces.ControloDocumento;
import com.siemens.ssa.communicator.pojo.interfaces.ControloItem;
import com.siemens.ssa.communicator.util.SessionConstants;
import com.siemens.ssa.communicator.web.jsp.control.pojo.ControloDocumentoWeb;
import org.apache.click.control.Form;
import org.apache.commons.lang.StringUtils;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.buttons.W3Submit;
import pt.atos.web.click.controls.form.Tab;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.page.DgitaLayoutPage;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

public class TabGeralDocumentalGeneric extends DgitaLayoutPage {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * Tabela que contem a lista de documentos a conferir
     */
    public DgitaTable docsTable = new DgitaTable("control_doc_docsTable");
    public W3Submit confereDoc = new W3Submit("Conferir Todos", null, this, "conferir");

    /**
     * Separador do controlo documental
     */
    public Tab PhyControlDoc;
    private static Log log = Log.getLogger(TabGeralDocumental.class);

    public String identControlo;

    /**
     * Funcao para fazer colocar no forumlario os dados
     * passados como parametro
     *
     * @param controlo - pojo do controlo da declaracao para o preenchimento
     * @param form     - formulario da pagina passado por parametro
     */
    public void setFormulario(Controlo controlo, Form form) {

        ArrayList<ControloDocumentoWeb> listaDoc = new ArrayList<ControloDocumentoWeb>();

        if (controlo.getListaControloItem() != null) {

            for (int x = 0; x < controlo.getListaControloItem().size(); x++) {

                ControloItem controloAdd = controlo.getListaControloItem().get(x);
                // O sistema DLCC2 não precisa de titulos na descrição da tabela
                if (!controlo.getSistema().equals(SGCConstantes.SISTEMA_DLCC2)) {
                    ControloDocumentoWeb contr = new ControloDocumentoWeb();
                    contr.setConferidorDocumento(null);

                    if (controloAdd.getChave().getNumeroItem() != null) {
                        //String descricaoInical="Adição No."+(x+1);
                        String descricaoInical = null;
                        if (controlo.getSistema().equals(SGCConstantes.SISTEMA_IMPEC) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_SIMTEMM) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_SIMTEM_VIAS) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_DAIN) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_EXPCAU) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_TRACAU) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_TRACAUDEST) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_NR) ||
                                controlo.getSistema().equals(SGCConstantes.SISTEMA_DSS)) {
                            // A descricao do primeiro Documento é do CABECALHO
                            if (x == 0) {
                                descricaoInical = "Cabe&ccedil;alho";
                            } else {
                                descricaoInical = "Adi&ccedil;&atilde;o N&deg;." + controloAdd.getChave().getNumeroItem().toString();
                            }

                        } else {

                            descricaoInical = "Adi&ccedil;&atilde;o N&deg;." + controloAdd.getChave().getNumeroItem().toString();
                        }

                        if (SGCConstantes.FLAG_BD_VERDADEIRO.equals(controloAdd.getChave().getIndVirtual())) {
                            descricaoInical = descricaoInical.concat(" - Virtual");
                        }
                        if (controloAdd.getListaControloDocumentoItem().size() > 0) {
                            contr.setDescricaoDocumento(descricaoInical);
                            log.info("Get Conferido: " + contr.getConferido());
                            contr.getConferido();
                            listaDoc.add(contr);
                        }
                    }
                }
                for (ControloDocumento ctrDocAdi : controloAdd.getListaControloDocumentoItem()) {

                    ControloDocumentoWeb ctrDoc = new ControloDocumentoWeb();
                    if (controloAdd.getChave().getNumeroItem() != null) {
                        ctrDocAdi.setNumAdicao(controloAdd.getChave().getNumeroItem().toString());
                    }
                    ctrDocAdi.setIndVirtual(controloAdd.getChave().getIndVirtual());
                    ctrDoc.toControloDocumento(ctrDocAdi);
                    ctrDoc.setFilenetLink(ctrDocAdi.getFilenetLink());
                    ctrDoc.setRead(PhyControlDoc.isReadonly());
                    log.info("Get Conferido: " + ctrDoc.getConferido());
                    listaDoc.add(ctrDoc);

                }
            }
        }
        docsTable.setRowList(listaDoc);
    }

    /**
     * Método que preenche o POJO com os dados correspondentes no FORM da página
     */
    public Controlo getFormulario(Controlo ctrlDec, Form form) {

        if (ctrlDec != null && ctrlDec.getListaControloItem() != null
                && ctrlDec.getListaControloItem().size() > 0) {

            for (int x = 0; x < ctrlDec.getListaControloItem().size(); x++) {

                ControloItem controloAd = ctrlDec.getListaControloItem().get(x);

                if (controloAd != null && controloAd.getListaControloDocumentoItem() != null
                        && controloAd.getListaControloDocumentoItem().size() > 0) {

                    ArrayList<ControloDocumento> listaDocAd = new ArrayList<ControloDocumento>();

                    for (int i = 0; i < controloAd.getListaControloDocumentoItem().size(); i++) {

                        listaDocAd.add(toControloDocumento(controloAd.getListaControloDocumentoItem().get(i), form));
                    }
                    controloAd.setListaControloDocumentoItem(listaDocAd);
                }
            }
        }
        return ctrlDec;
    }

    public void dealWithAjaxRequest(ControloItem adi_, DgitaLayoutPage tbcontrol, String index) {

    }

    private ControloDocumento toControloDocumento(ControloDocumento original, Form form) {

        String check = (String) form.getPage().getContext().getRequestParameter("control_doc_" + original.getNumAdicao() + "_virt_" + original.getIndVirtual() + "_chk_" + original.getChave().getIdentDocumento());
        if (StringUtils.isNotBlank(check)) {
            if ("on".equalsIgnoreCase(check)) {
                original.setConferidorDocumento(SGCConstantes.FLAG_BD_VERDADEIRO);
                original.setDescricaoConferido("Sim");
            } else {
                original.setConferidorDocumento(SGCConstantes.FLAG_BD_FALSO);
                original.setDescricaoConferido("Não");
            }
        } else {
            original.setConferidorDocumento(SGCConstantes.FLAG_BD_FALSO);
            original.setDescricaoConferido("Não");
        }
        return original;
    }


    public boolean conferir() {
        log.info("geralDocumental CONFERIR#idCarregar:" + idCarregar + ";" + identControlo);
        HttpSession session = getSession();
        Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + identControlo);
        if (ctrlDec != null
                && ctrlDec.getListaControloItem() != null && ctrlDec.getListaControloItem().size() > 0) {

            for (int i = 0; i < ctrlDec.getListaControloItem().size(); i++) {
                ControloItem adic = ctrlDec.getListaControloItem().get(i);

                if (adic != null && adic.getListaControloDocumentoItem() != null
                        && adic.getListaControloDocumentoItem().size() > 0) {

                    for (int x = 0; x < adic.getListaControloDocumentoItem().size(); x++) {
                        ControloDocumento docs = adic.getListaControloDocumentoItem().get(x);

                        if (docs != null) {
                            docs.setConferidorDocumento(SGCConstantes.FLAG_BD_VERDADEIRO);
                            docs.setDescricaoConferido("Sim");
                        }
                    }
                }
            }
        }
        setFormulario(ctrlDec, null);
        session.setAttribute(SessionConstants.RES_CONTROLO + identControlo, ctrlDec);
        SessionManager.getInstance().setSessao(session);

        return true;
    }

    public void saveFormulario(Form form) {
        HttpSession session = getSession();
        Controlo ctrlDec = (Controlo) session.getAttribute(SessionConstants.RES_CONTROLO + identControlo);

        if (ctrlDec != null && ctrlDec.getListaControloItem() != null) {
            for (ControloItem adic : ctrlDec.getListaControloItem()) {
                if (adic != null && adic.getListaControloDocumentoItem() != null) {
                    for (ControloDocumento doc : adic.getListaControloDocumentoItem()) {

                        String check = (String) form.getPage().getContext().getRequestParameter("control_doc_" + doc.getNumAdicao() + "_virt_" + doc.getIndVirtual() + "_chk_" + doc.getChave().getIdentDocumento());
                        if (StringUtils.isNotBlank(check) && check.equals("on")) {
                            doc.setConferidorDocumento(SGCConstantes.FLAG_BD_VERDADEIRO);
                        } else {
                            doc.setConferidorDocumento(SGCConstantes.FLAG_BD_FALSO);
                        }

                    }
                }
            }
        }

        setFormulario(ctrlDec, null);
        session.setAttribute(SessionConstants.RES_CONTROLO + identControlo, ctrlDec);
        SessionManager.getInstance().setSessao(session);
    }

    @Override
    protected void buildPage() {
    }

    @Override
    protected void getFormData() {
    }

    @Override
    public boolean onCancelar() {
        return false;
    }

    @Override
    public boolean onGravar() {
        return false;
    }

    @Override
    protected void setFormData() {
    }
}

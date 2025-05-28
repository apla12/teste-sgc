package com.siemens.ssa.communicator.web.jsp.ListaRetry;

import java.util.Date;

import pt.atos.util.presentation.TableItem;

import com.siemens.retry.entity.Interaccao;

public class InteraccaoView implements TableItem {
	private Interaccao inte ;
	
	public InteraccaoView(Interaccao inte) {
		super();
		this.inte = inte;
	}

	public boolean equals(Object obj) {
		return inte.equals(obj);
	}

	public int getCodigoErro() {
		return inte.getCodigoErro();
	}

	public Date getDataEnvio() {
		return inte.getDataEnvio();
	}

	public Date getDataRecepcao() {
		return inte.getDataRecepcao();
	}

	public String getEstadoPedido() {
		return inte.getEstadoPedido();
	}

	public String getIdentificacaoMensagem() {
		return inte.getIdentificacaoMensagem();
	}

	public Object getMensagemEnviada() {
		return inte.getMensagemEnviada();
	}

	public String getMensagemErro() {
		return inte.getMensagemErro();
	}

	public String getNumeroAceitacao() {
		return inte.getNumeroAceitacao();
	}

	public long getNumeroMensagemOriginal() {
		return inte.getNumeroMensagemOriginal();
	}

	public long getNumeroSequencial() {
		return inte.getNumeroSequencial();
	}

	public int getNumeroTentativas() {
		return inte.getNumeroTentativas();
	}

	public String getServidorDestino() {
		return inte.getServidorDestino();
	}

	public String getServidorOrigem() {
		return inte.getServidorOrigem();
	}

	public String getSistemaEmissor() {
		return inte.getSistemaEmissor();
	}

	public String getSistemaReceptor() {
		return inte.getSistemaReceptor();
	}

	public String getTipoComunicacao() {
		return inte.getTipoComunicacao();
	}

	public int hashCode() {
		return inte.hashCode();
	}

	public void setCodigoErro(int codigoErro) {
		inte.setCodigoErro(codigoErro);
	}

	public void setDataEnvio(Date dateenv) {
		inte.setDataEnvio(dateenv);
	}

	public void setDataRecepcao(Date daterec) {
		inte.setDataRecepcao(daterec);
	}

	public void setEstadoPedido(String xestped) {
		inte.setEstadoPedido(xestped);
	}

	public void setIdentificacaoMensagem(String xmessid) {
		inte.setIdentificacaoMensagem(xmessid);
	}

	public void setMensagemEnviada(Object xmsgenv) {
		inte.setMensagemEnviada(xmsgenv);
	}

	public void setMensagemErro(String nmsgerro) {
		inte.setMensagemErro(nmsgerro);
	}

	public void setNumeroAceitacao(String nnumacei) {
		inte.setNumeroAceitacao(nnumacei);
	}

	public void setNumeroMensagemOriginal(long xmsgorig) {
		inte.setNumeroMensagemOriginal(xmsgorig);
	}

	public void setNumeroSequencial(long inumseq) {
		inte.setNumeroSequencial(inumseq);
	}

	public void setNumeroTentativas(int qtenped) {
		inte.setNumeroTentativas(qtenped);
	}

	public void setServidorDestino(String xsisdst) {
		inte.setServidorDestino(xsisdst);
	}

	public void setServidorOrigem(String xsisori) {
		inte.setServidorOrigem(xsisori);
	}

	public void setSistemaEmissor(String nentemis) {
		inte.setSistemaEmissor(nentemis);
	}

	public void setSistemaReceptor(String nentrecp) {
		inte.setSistemaReceptor(nentrecp);
	}

	public void setTipoComunicacao(String ctipcom) {
		inte.setTipoComunicacao(ctipcom);
	}

	public String toString() {
		return inte.toString();
	}

	@Override
	public String getPK() {
		
		return getNumeroSequencial()+"";
	}

}

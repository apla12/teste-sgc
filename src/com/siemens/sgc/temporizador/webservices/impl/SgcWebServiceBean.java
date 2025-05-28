package com.siemens.sgc.temporizador.webservices.impl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import pt.atos.ws.client.sgc.temporizador.TimerInput;
import pt.atos.ws.client.sgc.temporizador.TimerNotify;

@WebService(serviceName = "SgcWs", targetNamespace = "http://pt.atos.sgc.ws")
public class SgcWebServiceBean {

	@WebMethod
	public String temporizador(@WebParam(name = "TimerInput") TimerInput input) throws Exception {
		TemporizadorRequestServiceImpl impl = new TemporizadorRequestServiceImpl();
		return impl.iniciarPararTemporizador(input);

	}
	
	public String notificarExpiracaoPrazo(TimerNotify input) throws Exception {
		return null;
	}


}

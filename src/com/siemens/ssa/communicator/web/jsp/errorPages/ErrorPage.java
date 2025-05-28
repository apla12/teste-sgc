package com.siemens.ssa.communicator.web.jsp.errorPages;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;

import pt.atos.util.environment.Environment;
import pt.atos.util.properties.BaseProperties;

import pt.atos.web.click.controls.panels.DIV;
import pt.atos.web.click.page.DgitaErrorPage;

public class ErrorPage extends DgitaErrorPage {

	private static final long serialVersionUID = 1L;
	public String stackTrace;

	@Override
	protected void buildPage() {
		BaseProperties props = new BaseProperties();

		DIV linkDiv = new DIV();
		linkDiv.setStyle("padding-left", "69pt");
		linkDiv.setStyle("padding-top", "10px");
		linkDiv.setStyle("font-size", "12px");
		String jsFunction = "<script type='text/javascript'>" + "function displayError() {"
				+ "	if(divErros.style.display == 'block') " + "		divErros.style.display = 'none'; " + "	else "
				+ "		divErros.style.display = 'block';}</script>";
		String texto = jsFunction;
		Throwable t = getError();
		
		if (t != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			texto += StringUtils.replace(os.toString(), "\n", "<br/>");

			String linkText = "<p>" + t.getMessage() + "</p>";
			if (!props.getCurrentEnvironment().is(Environment.PRODUCTION)) {
				linkText += "<a href='#' onclick='displayError();'>Clique para ver detalhes do Erro</a>";
			}
			linkDiv.setText(linkText);
			form.add(linkDiv);
			DIV divErro = new DIV();
			divErro.setId("divErros");
			divErro.setStyle("padding-left", "100pt");
			divErro.setStyle("display", "none");
			divErro.setStyle("color", "red");
			divErro.setStyle("font-size", "12px");

			divErro.setText(texto);
			form.add(divErro);

		} else {
			texto += "Página não encontrada";
			linkDiv.setText("<p>Página não encontrada</p>");
			form.add(linkDiv);
		}

	}
}

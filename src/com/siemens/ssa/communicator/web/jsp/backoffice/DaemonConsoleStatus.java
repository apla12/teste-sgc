package com.siemens.ssa.communicator.web.jsp.backoffice;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.click.Context;
import org.apache.click.control.Column;

import com.siemens.ssa.communicator.web.jsp.access.application.control.ApplicationControl;

import pt.atos.engines.impl.EngineManager;
import pt.atos.engines.utils.EngineManagementProperties;
import pt.atos.engines.utils.EngineStatusInfo;
import pt.atos.sgccomunicator.utils.SGCConstantes;
import pt.atos.util.date.DateUtil;
import pt.atos.util.exception.ApplicationException;
import pt.atos.util.logging.Log;
import pt.atos.web.click.controls.table.AnonymousCall;
import pt.atos.web.click.controls.table.AnonymousCallColumn;
import pt.atos.web.click.controls.table.DateFormatColumn;
import pt.atos.web.click.controls.table.DgitaTable;
import pt.atos.web.click.controls.table.MultiplePropertyColumn;
import pt.atos.web.click.page.DgitaLayoutPage;
public class DaemonConsoleStatus extends DgitaLayoutPage {
	
	private Log log = Log.getLogger(DaemonConsoleStatus.class);
	private static final long serialVersionUID = 1L;
		
	public DgitaTable motores ;
	@Override
	protected void buildPage() {
		
	motores = new DgitaTable("engines");
		
		Column column0 = new Column("name", "Nome");
		motores.addColumn(column0);
		AnonymousCallColumn column11 = new AnonymousCallColumn("server");
		column11.setCall(new AnonymousCall() {
            @Override
            public String getDataContent(Object row, Context context, int rowIndex) {
                StringBuffer buf = new StringBuffer();
                EngineManagementProperties p = new EngineManagementProperties();
                ArrayList<String> nodes = (ArrayList<String>) p.getPropertyAsList("servers.tag");
                buf.append("<select id=servidor_"+motores.getName()+"_"+rowIndex);
                if(((EngineStatusInfo)row).isShowStop()){
                    buf.append(" disabled=\"disabled\" ");
                }
                buf.append(">");
                buf.append("<option value=\"\"> -- </option>");
                for (Iterator<String> iterator = nodes.iterator(); iterator.hasNext();) {
                    String node = (String) iterator.next();
                    buf.append("<option value=\""+node+"\" ");
                    if(node.equals(((EngineStatusInfo)row).getServer()))
                        buf.append(" SELECTED ");
                    buf.append(">"+node+"</option>");
                }
                buf.append("</select>");
                return buf.toString();
            }
        });
        motores.addColumn(column11);
		motores.addColumn(new Column("status", "Estado"));
		DateFormatColumn column1 = new DateFormatColumn("lastFire", "Última Execução");
		column1.setFormat(DateUtil.DATETIME_FORMAT);
		motores.addColumn(column1);
		DateFormatColumn column2 = new DateFormatColumn("nextFire", "Próxima Execução");
		column2.setFormat(DateUtil.DATETIME_FORMAT);
		motores.addColumn(column2);
		MultiplePropertyColumn column3 = new MultiplePropertyColumn("frequencia", "Frequência");
		column3.addProperty("frequencia");
		column3.setFormat("<%frequencia%> seg.");
		motores.addColumn(column3);
		motores.setShowBanner(true);
		motores.setPageSize(10);
		AnonymousCallColumn col = new AnonymousCallColumn("controlo");
		col.setCall(new AnonymousCall() {
			
			@Override
			public String getDataContent(Object row, Context context, int rowIndex) {
				StringBuffer buf = new StringBuffer();
				if(((EngineStatusInfo)row).isShowStart()){
					
					String url= getContextPath() + getContext().getPagePath(ApplicationControl.class)+"?action=Start&idCarregar="+((EngineStatusInfo)row).getName();
					
					buf.append("");
					buf.append("<INPUT class=fb_button");
					buf.append(" id=\"Start\"");
					buf.append(" onclick=\"");
					buf.append("startStop('"+url+"',"+rowIndex+",'"+motores.getName()+"')");
					buf.append("\"");
					buf.append(" type=button value=\"Start\" >");
				}
				
				if(((EngineStatusInfo)row).isShowStop()){
					String url= getContextPath() + getContext().getPagePath(ApplicationControl.class)+"?action=Stop&idCarregar="+((EngineStatusInfo)row).getName();
					
					buf.append("");
					buf.append("<INPUT class=fb_button_red");
					buf.append(" id=\"Stop\"");
					buf.append(" onclick=\"");
					buf.append("startStop('"+url+"',"+ rowIndex	+",'"+motores.getName()+"')");
					buf.append("\"");
					buf.append(" type=button value=\"Stop\" >");
				}
				return buf.toString();
			}
		});
		col.setDataStyle("text-align", "center");
		
		motores.addColumn(col);
		motores.setTableTitle("Gestão de motores");
		
		fillTable();
	    getHeadElements().add(motores.getJsImportForTable());
	}
	private void fillTable() {
		
	    try {
			motores.setRowList(new EngineManager().checkGlobalEngineStatus(System.getProperty("weblogic.Name"), SGCConstantes.ENGINE_APPLICATION, SGCConstantes.QUARTZ_SCHEDULER_NAME));
		} catch (ApplicationException e) {
			log.error(e.getMessage());
		}
	}
	
	@Override
	protected void getFormData() {
	}
	@Override
	protected void setFormData() {
		
	}
}
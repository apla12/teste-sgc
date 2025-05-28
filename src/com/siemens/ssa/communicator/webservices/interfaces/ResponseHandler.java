package com.siemens.ssa.communicator.webservices.interfaces;

import pt.atos.util.exception.ApplicationException;

import com.siemens.ssa.communicator.webservices.response.ResultRegistControl;
import com.siemens.ssa.communicator.webservices.response.ValidateAtribControl;
import com.siemens.ssa.communicator.webservices.response.CAPList;
import com.siemens.ssa.communicator.webservices.response.ComunicationResult;
import com.siemens.ssa.communicator.webservices.response.RectificationResult;
import com.siemens.ssa.communicator.webservices.response.RenominationList;
import com.siemens.ssa.communicator.webservices.response.ReselectionResult;
import com.siemens.ssa.communicator.webservices.response.SelectionException_Exception;
import com.siemens.ssa.communicator.webservices.response.SelectionResult;

public interface ResponseHandler {
	
	public String selectionResult(SelectionResult selectionResult) throws SelectionException_Exception, ApplicationException;

	public String comunicationResult(ComunicationResult comunicationResult) throws SelectionException_Exception;
	
	public String rectificationResult(RectificationResult rectificationResult) throws SelectionException_Exception, ApplicationException;

	public String reselectionResult(ReselectionResult rectificationResult) throws SelectionException_Exception;
	
	public String renominationList(RenominationList renominationList) throws SelectionException_Exception;

	public String capList(CAPList capList) throws SelectionException_Exception;
	
	public String validateControl(ValidateAtribControl validateControl) throws SelectionException_Exception;
	
	public String resultRegistoControl(ResultRegistControl resultRegistoControl) throws SelectionException_Exception;
}


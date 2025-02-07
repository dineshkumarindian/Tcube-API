package com.tcube.api.dao;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tcube.api.model.ClientDetails;
import com.tcube.api.model.EmployeeDetails;

public interface ClientDetailsDao {

	public ClientDetails createClientDetails(ClientDetails clientdetails);

	public ClientDetails getClientById(Long id);

	public ClientDetails updateClientDetails(ClientDetails clientDetails);

	public ClientDetails deleteClientDetails(ClientDetails oldDetails);

	public List<ClientDetails> getAllClientDetails();

	public List<ClientDetails> getClientDetailsByOrgId(Long id);

	public List<ClientDetails> getAllActiveClientReportsByOrgId(Long id);

	public List<ClientDetails> getInactiveClientDetailsByOrgID(Long id);

	public JSONObject getTotalCPJByOrgId(Long id);
	
	public int bulkDelete(JSONArray ids);
	
	public int bulkDeactive(JSONArray ids,String action);
	
	public int bulkActivate(JSONArray ids,String action);

}

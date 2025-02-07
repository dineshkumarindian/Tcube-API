package com.tcube.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcube.api.dao.ClientDetailsDao;
import com.tcube.api.model.ClientDetails;

@Service
@Transactional
public class ClientDetailsServiceImpl implements ClientDetailsService{

	@Autowired
	ClientDetailsDao clientDetailsDao;

	@Override
	public ClientDetails createClientDetails(ClientDetails clientdetails) {
		return clientDetailsDao.createClientDetails(clientdetails);
	}

	@Override
	public ClientDetails getClientById(Long id) {
		return clientDetailsDao.getClientById(id);
	}

	@Override
	public ClientDetails updateClientDetails(ClientDetails clientDetails) {
		return clientDetailsDao.updateClientDetails(clientDetails);
	}

	@Override
	public ClientDetails deleteClientDetails(ClientDetails oldDetails) {
		return clientDetailsDao.deleteClientDetails(oldDetails);
	}

	@Override
	public List<ClientDetails> getAllClientDetails() {
		return clientDetailsDao.getAllClientDetails();
	}

	@Override
	public List<ClientDetails> getClientDetailsByOrgId(Long id) {
		// TODO Auto-generated method stub
		return clientDetailsDao.getClientDetailsByOrgId(id);
	}

	@Override
	public List<ClientDetails> getInactiveClientDetailsByOrgID(Long id) {
		// TODO Auto-generated method stub
		return clientDetailsDao.getInactiveClientDetailsByOrgID(id);
	}

	@Override
	public List<ClientDetails> getAllActiveClientReportsByOrgId(Long id) {
		// TODO Auto-generated method stub
		return clientDetailsDao.getAllActiveClientReportsByOrgId(id);
	}

	@Override
	public JSONObject getTotalCPJByOrgId(Long id) {
		// TODO Auto-generated method stub
		return clientDetailsDao.getTotalCPJByOrgId(id);
	}

	@Override
	public int bulkDelete(JSONArray ids) {
		// TODO Auto-generated method stub
		return clientDetailsDao.bulkDelete(ids);
	}

	@Override
	public int bulkDeactive(JSONArray ids, String action) {
		// TODO Auto-generated method stub
		return clientDetailsDao.bulkDeactive(ids, action);
	}
	
	@Override
	public int bulkActivate(JSONArray ids, String action) {
		// TODO Auto-generated method stub
		return clientDetailsDao.bulkActivate(ids, action);
	}
	
}

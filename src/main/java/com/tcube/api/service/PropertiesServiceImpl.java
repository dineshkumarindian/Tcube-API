package com.tcube.api.service;

import com.tcube.api.dao.PropertiesDao;
import com.tcube.api.model.Appproperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class PropertiesServiceImpl implements PropertiesService{

	@Autowired
	PropertiesDao propertiesDao;

	/**
	 * @param key
	 * @return
	 */
	@Override
	public Appproperties getproperties(String key) {
		return propertiesDao.getproperties(key);
	}

	/**
	 * @param data
	 * @return
	 */
	@Override
	public Appproperties createproperties(Appproperties data) {
		return propertiesDao.createproperties(data);
	}

//	@Override
//	public Appproperties updateproperties(Appproperties data) {
//		return propertiesDao.updateproperties(data);
//	}
}

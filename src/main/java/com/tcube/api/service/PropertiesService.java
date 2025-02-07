package com.tcube.api.service;


import com.tcube.api.model.Appproperties;

public interface PropertiesService {
	Appproperties getproperties (String key);

	Appproperties createproperties(Appproperties data);
}

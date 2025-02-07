package com.tcube.api.dao;

import com.tcube.api.model.Appproperties;

public interface PropertiesDao {

    Appproperties getproperties (String key);

    Appproperties createproperties(Appproperties key);
}

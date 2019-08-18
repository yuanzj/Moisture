package com.drt.moisture.data.source;

import com.drt.moisture.data.AppConfig;

public interface LocalDataService {

    AppConfig queryAppConfig();

    void setAppConfig(AppConfig appConfig);

}

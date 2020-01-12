package com.drt.moisture.data.source;

import com.drt.moisture.data.AppConfig;

import java.util.List;
import java.util.Set;

public interface LocalDataService {

    AppConfig queryAppConfig();

    void setAppConfig(AppConfig appConfig);

    List<String> queryHistory();

    void setHistory(String name);

    AppConfig queryAppConfig(int index);

    void setAppConfig(int index, AppConfig appConfig);

    List<String> queryHistory(int index);

    void setHistory(int index, String name);

    void clearAll();

}

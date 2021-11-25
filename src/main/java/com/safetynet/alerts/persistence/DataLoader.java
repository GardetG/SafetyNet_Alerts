package com.safetynet.alerts.persistence;

/**
 * Interface handling the retrieve of data from a datasource.
 */
public interface DataLoader {

  void load(String url);
  
}

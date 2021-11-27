package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.FireStation;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class interface for FireStation.
 */
@Repository
public interface FireStationRepository {

  /**
   * Find all FireStation mapping in repository.
   * 

   * @return List of FireStation mapping
   */
  List<FireStation> findAll();

  /**
   * Find all FireStation mapping of a station by its id.
   * 

   * @param station id
   * @return List of FireStation mapping
   */
  List<FireStation> findByStation(int station);
  
  /**
   * Find a FireStation mapping by an address.
   * 

   * @param address of the mapping
   * @return FireStation mapping
   */
  FireStation findByAddress(String address);
  
}

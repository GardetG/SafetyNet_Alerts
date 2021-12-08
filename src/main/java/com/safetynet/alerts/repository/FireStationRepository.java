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
   * Find all FireStation mapping by an address.
   * 

   * @param address of the mapping
   * @return List of FireStation mapping
   */
  List<FireStation> findByAddress(String address);
  
  /**
   * Add a FireStation mapping to the repository if it doesn't already exist.
   * 

   * @param fireStation mapping to add
   * @return True is operation succeed
   */
  boolean add(FireStation fireStation);

  /**
   * Update a FireStation mapping in the repository if it exists.
   * 

   * @param fireStation mapping to update
   * @return True if operation succeed
   */
  boolean update(FireStation fireStation);
  
  /**
   * Delete a FireStation mapping in the repository if it exists.
   * 

   * @param fireStation mapping to delete
   * @return True if operation succeed
   */
  boolean delete(FireStation fireStation);
  
}

package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling CRUD operations for FireStation mapping.
 */
@Service
public interface FireStationService {

  /**
   * Get list of all FireStation mapping.
   * 

   * @return list of FireStation mapping
   */
  List<FireStation> getAll();
  
  /**
   * Get list of all FireStation mapping for a station by its Id.
   * 

   * @param station Id
   * @return list of FireStation mapping
   * @throws ResourceNotFoundException when mapping for this FireStation are not found
   */
  List<FireStation> getByStation(int station) throws ResourceNotFoundException;
  
  /**
   * Get FireStation mapping for an address.
   * 

   * @param address of the mapping
   * @return FireStation mapping for this address
   * @throws ResourceNotFoundException when mapping for this address is not found
   */
  FireStation getByAddress(String address) throws ResourceNotFoundException;

}

package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.List;
import javax.validation.Valid;
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
  List<FireStationDto> getAll();
  
  /**
   * Get list of all FireStation mapping for a station by its Id.
   * 

   * @param station Id
   * @return list of FireStation mapping
   * @throws ResourceNotFoundException when mapping for this FireStation are not found
   */
  List<FireStationDto> getByStation(int station) throws ResourceNotFoundException;
  
  /**
   * Get FireStation mapping for an address.
   * 

   * @param address of the mapping
   * @return FireStation mapping for this address
   * @throws ResourceNotFoundException when mapping for this address is not found
   */
  List<FireStationDto> getByAddress(String address) throws ResourceNotFoundException;
  
  /**
   * Add a FireStation mapping if it doesn't already exists.
   * 

   * @param fireStation mapping to add
   * @return FireStation mapping added
   * @throws ResourceAlreadyExistsException when FireStation mapping already exists
   */
  FireStationDto add(@Valid FireStationDto fireStation) throws ResourceAlreadyExistsException;

  /**
   * Update a FireStation mapping if it already exists.
   * 

   * @param fireStation mapping to update
   * @return FireStation mapping updated
   * @throws ResourceNotFoundException when the FireStation mapping doesn't exists
   */
  FireStationDto update(@Valid FireStationDto fireStation) throws ResourceNotFoundException;

  /**
   * Delete all FireStation mapping for station.
   * 

   * @param station id of mapping to delete
   * @throws ResourceNotFoundException when mapping for this FireStation are not found
   */
  void deleteByStation(int station) throws ResourceNotFoundException;

  /**
   * Delete FireStation mapping for an address.
   * 

   * @param address of the mapping to delete
   * @throws ResourceNotFoundException when mapping for this address is not found
   */
  void deleteByAddress(String address) throws ResourceNotFoundException;
  
}

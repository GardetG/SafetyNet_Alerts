package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service Class implementation handling CRUD operations for FireStation.
 */
@Service
@Validated
public class FireStationServiceImpl implements FireStationService {

  @Autowired
  FireStationRepository fireStationRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStation> getAll() {
    return fireStationRepository.findAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStation> getByStation(int station) throws ResourceNotFoundException {
    
    List<FireStation> fireStations = fireStationRepository.findByStation(station);
    if (fireStations.isEmpty()) {
      String error = String.format("Station %s mapping not found", station);
      throw new ResourceNotFoundException(error);
    }
    
    return fireStations;
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStation getByAddress(String address) throws ResourceNotFoundException {
    
    FireStation fireStation = fireStationRepository.findByAddress(address);
    if (fireStation == null) {
      String error = String.format("%s mapping not found", address);
      throw new ResourceNotFoundException(error);
    }
    
    return fireStation;
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStation add(@Valid FireStation fireStation) throws ResourceAlreadyExistsException {
    
    FireStation existingFireStation = fireStationRepository.findByAddress(fireStation.getAddress());

    if (existingFireStation != null) {
      String error = String.format("%s mapping for station %s already exists", 
              fireStation.getAddress(),
              fireStation.getStation());
      throw new ResourceAlreadyExistsException(error);
    }

    fireStationRepository.add(fireStation);
    return fireStationRepository.findByAddress(fireStation.getAddress());
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStation update(@Valid FireStation fireStation) throws ResourceNotFoundException {
    
    FireStation existingFireStation = fireStationRepository.findByAddress(fireStation.getAddress());
    if (existingFireStation == null) {
      String error = String.format("%s mapping not found", fireStation.getAddress());
      throw new ResourceNotFoundException(error);
    }

    fireStationRepository.update(fireStation);
    return fireStationRepository.findByAddress(fireStation.getAddress());
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteByStation(int station) throws ResourceNotFoundException {
    
    List<FireStation> existingFireStationList = fireStationRepository.findByStation(station);
    if (existingFireStationList.isEmpty()) {
      String error = String.format("Station %s mapping not found", station);
      throw new ResourceNotFoundException(error);
    }
    
    existingFireStationList.forEach(fireStation -> {
      fireStationRepository.delete(fireStation);
    });

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteByAddress(String address) throws ResourceNotFoundException {
    
    FireStation existingFireStation = fireStationRepository.findByAddress(address);
    if (existingFireStation == null) {
      String error = String.format("%s mapping not found", address);
      throw new ResourceNotFoundException(error);
    }

    fireStationRepository.delete(existingFireStation);
  }

}

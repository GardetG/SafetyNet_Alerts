package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.util.FireStationMapper;
import java.util.List;
import java.util.Optional;
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

  private static final String NOT_FOUND = "%s mapping not found";
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStationDto> getAll() {
    return FireStationMapper.toDto(fireStationRepository.findAll());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStationDto> getByStation(int station) throws ResourceNotFoundException {
    
    List<FireStation> fireStations = fireStationRepository.findByStation(station);
    if (fireStations.isEmpty()) {
      String error = String.format("No addresses mapped for station %s found", station);
      throw new ResourceNotFoundException(error);
    }
    
    return FireStationMapper.toDto(fireStations);
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStationDto getByAddress(String address) throws ResourceNotFoundException {
    
    Optional<FireStation> fireStation = fireStationRepository.findByAddress(address);
    if (fireStation.isEmpty()) {
      String error = String.format(NOT_FOUND, address);
      throw new ResourceNotFoundException(error);
    }
    
    return FireStationMapper.toDto(fireStation.get());
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStationDto add(@Valid FireStationDto fireStation) 
          throws ResourceAlreadyExistsException {
    
    Optional<FireStation> existingFireStation = fireStationRepository
            .findByAddress(fireStation.getAddress());

    if (existingFireStation.isPresent()) {
      String error = String.format("%s mapping for station %s already exists", 
              fireStation.getAddress(),
              fireStation.getStation());
      throw new ResourceAlreadyExistsException(error);
    }

    fireStationRepository.add(FireStationMapper.toModel(fireStation));
    
    FireStation addedFireStation = fireStationRepository
            .findByAddress(fireStation.getAddress()).get();
    
    return FireStationMapper.toDto(addedFireStation);
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStationDto update(@Valid FireStationDto fireStation) throws ResourceNotFoundException {
    
    Optional<FireStation> existingFireStation = fireStationRepository
            .findByAddress(fireStation.getAddress());
    if (existingFireStation.isEmpty()) {
      String error = String.format(NOT_FOUND, fireStation.getAddress());
      throw new ResourceNotFoundException(error);
    }

    fireStationRepository.update(FireStationMapper.toModel(fireStation));
    
    FireStation updatedFireStation = fireStationRepository
            .findByAddress(fireStation.getAddress()).get();
    
    return FireStationMapper.toDto(updatedFireStation);    
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
    
    existingFireStationList.forEach(fireStation -> fireStationRepository.delete(fireStation));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteByAddress(String address) throws ResourceNotFoundException {
    
    Optional<FireStation> existingFireStation = fireStationRepository.findByAddress(address);
    if (existingFireStation.isEmpty()) {
      String error = String.format(NOT_FOUND, address);
      throw new ResourceNotFoundException(error);
    }

    fireStationRepository.delete(existingFireStation.get());
  }

}

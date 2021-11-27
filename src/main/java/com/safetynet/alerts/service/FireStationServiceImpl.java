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
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStation> getByStation(int station) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStation getByAddress(String address) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStation add(@Valid FireStation fireStation) throws ResourceAlreadyExistsException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStation update(@Valid FireStation fireStation) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteByStation(int station) throws ResourceNotFoundException {
    // TODO Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteByAddress(String address) throws ResourceNotFoundException {
    // TODO Auto-generated method stub

  }

}

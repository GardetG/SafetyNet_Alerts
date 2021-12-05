package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.FireStation;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for FireStation.
 */
@Repository
public class FireStationRepositoryImpl
        implements FireStationRepository, LoadableRepository<FireStation> {

  private List<FireStation> fireStationsList = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStation> findAll() {
    return fireStationsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStation> findByStation(int station) {
    return fireStationsList.stream()
            .filter(fireStation -> (fireStation.getStation() == station))
            .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FireStation> findByAddress(String address) {
    return fireStationsList.stream()
            .filter(fireStation -> (fireStation.getAddress().equals(address)))
            .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(FireStation fireStation) {
    if (fireStationsList.contains(fireStation)) {
      return false;
    }
    
    return fireStationsList.add(fireStation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(FireStation fireStation) {
    List<FireStation> existingMapping = findByAddress(fireStation.getAddress());
    
    if (existingMapping.isEmpty()) {
      return false;
    }
    
    if (fireStationsList.add(fireStation)) {
      existingMapping.forEach(mapping -> fireStationsList.remove(mapping));
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(FireStation fireStation) {
    return fireStationsList.remove(fireStation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupRepository(List<FireStation> resourcesList) {
    fireStationsList = resourcesList;
  }

}

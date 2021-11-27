package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.FireStation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  public Optional<FireStation> findByAddress(String address) {
    return fireStationsList.stream()
            .filter(fireStation -> (fireStation.getAddress().equals(address)))
            .findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(FireStation fireStation) {
    return fireStationsList.add(fireStation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(FireStation fireStation) {
    FireStation existingFireStation = fireStationsList.stream()
            .filter(fireStationElemnt -> 
            (fireStationElemnt.getAddress().equals(fireStation.getAddress())))
            .findFirst().orElse(null);
    
    if (existingFireStation == null) {
      return false;
    }
    
    int index = fireStationsList.indexOf(existingFireStation);
    fireStationsList.set(index, fireStation);
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

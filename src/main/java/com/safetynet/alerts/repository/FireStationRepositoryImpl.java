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
  public FireStation findByAddress(String address) {
    return fireStationsList.stream()
            .filter(fireStation -> (fireStation.getAddress().equals(address)))
            .findFirst().orElse(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(FireStation fireStation) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(FireStation fireStation) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(FireStation fireStation) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupRepository(List<FireStation> resourcesList) {
    fireStationsList = resourcesList;
  }

}

package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.MedicalRecord;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for MedicalRecord.
 */
@Repository
public class MedicalRecordRepositoryImpl
        implements LoadableRepository<MedicalRecord>, MedicalRecordRepository {

  private List<MedicalRecord> medicalRecordsList;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<MedicalRecord> findAll() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public MedicalRecord findByName(String firstName, String lastName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean add(MedicalRecord medicalRecord) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean update(MedicalRecord medicalRecord) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean delete(MedicalRecord medicalRecord) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setupRepository(List<MedicalRecord> resourcesList) {
    medicalRecordsList = resourcesList;
  }

}

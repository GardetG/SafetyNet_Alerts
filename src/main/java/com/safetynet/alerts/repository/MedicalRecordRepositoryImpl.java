package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.MedicalRecord;
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
    return medicalRecordsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord findByName(String firstName, String lastName) {
    return medicalRecordsList.stream()
            .filter(medicalRecord -> (medicalRecord.getFirstName().equals(firstName)
            && (medicalRecord.getLastName().equals(lastName))))
            .findFirst().orElse(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(MedicalRecord medicalRecord) {
    return medicalRecordsList.add(medicalRecord);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(MedicalRecord medicalRecord) {
    MedicalRecord existingMedicalRecord = medicalRecordsList.stream()
            .filter(medicalRecordElemnt -> 
                    (medicalRecordElemnt.getFirstName().equals(medicalRecord.getFirstName())
                    && (medicalRecordElemnt.getLastName().equals(medicalRecord.getLastName()))))
            .findFirst().orElse(null);
    
    if (existingMedicalRecord == null) {
      return false;
    }
    
    int index = medicalRecordsList.indexOf(existingMedicalRecord);
    medicalRecordsList.set(index, medicalRecord);
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(MedicalRecord medicalRecord) {
    return medicalRecordsList.remove(medicalRecord);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupRepository(List<MedicalRecord> resourcesList) {
    medicalRecordsList = resourcesList;
  }

}

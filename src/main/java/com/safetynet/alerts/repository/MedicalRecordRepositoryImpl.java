package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.MedicalRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for MedicalRecord.
 */
@Repository
public class MedicalRecordRepositoryImpl
        implements LoadableRepository<MedicalRecord>, MedicalRecordRepository {

  private List<MedicalRecord> medicalRecordsList = new ArrayList<>();

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
  public Optional<MedicalRecord> findByName(String firstName, String lastName) {
    return medicalRecordsList.stream()
            .filter(medicalRecord -> (medicalRecord.getFirstName().equals(firstName)
            && (medicalRecord.getLastName().equals(lastName))))
            .findFirst();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(MedicalRecord medicalRecord) {
    Optional<MedicalRecord> existingMedicalRecord = findByName(medicalRecord.getFirstName(),
            medicalRecord.getLastName());
    
    if (existingMedicalRecord.isPresent()) {
      return false;
    }
    
    return medicalRecordsList.add(medicalRecord);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(MedicalRecord medicalRecord) {
    Optional<MedicalRecord> existingMedicalRecord = findByName(medicalRecord.getFirstName(),
            medicalRecord.getLastName());
    
    if (existingMedicalRecord.isEmpty()) {
      return false;
    }
    
    int index = medicalRecordsList.indexOf(existingMedicalRecord.get());
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

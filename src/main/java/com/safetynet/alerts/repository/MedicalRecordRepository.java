package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.MedicalRecord;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class interface for MedicalRecord.
 */
@Repository
public interface MedicalRecordRepository {

  /**
   * Find all MedicalRecord in repository.
   * 

   * @return List of all MedicalRecord
   */
  List<MedicalRecord> findAll();

  /**
   * Find a MedicalRecord by its name.
   * 

   * @param firstName of the MedicalRecord
   * @param lastName of the MedicalRecord
   * @return MedicalRecord
   */
  MedicalRecord findByName(String firstName, String lastName);
  
  /**
   * Add a MedicalRecord to the repository.
   * 

   * @param medicalRecord to add
   * @return True is operation succeed
   */
  boolean add(MedicalRecord medicalRecord);

  /**
   * Update a MedicalRecord in the repository.
   * 

   * @param medicalRecord to update
   * @return True if operation succeed
   */
  boolean update(MedicalRecord medicalRecord);
  
  /**
   * Delete a MedicalRecord in the repository.
   * 

   * @param medicalRecord to delete
   * @return True if operation succeed
   */
  boolean delete(MedicalRecord medicalRecord);
  
}

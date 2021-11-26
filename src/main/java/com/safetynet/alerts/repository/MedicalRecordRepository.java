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
  
}

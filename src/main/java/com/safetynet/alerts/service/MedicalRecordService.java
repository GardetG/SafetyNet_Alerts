package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling CRUD operations for MedicalRecord.
 */
@Service
public interface MedicalRecordService {

  /**
   * Get list of all MedicalRecord.
   * 

   * @return list of MedicalRecord
   */
  List<MedicalRecord> getAll(); 

  /**
   * Get a MedicalRecord by name.
   * 

   * @param firstName of the MedicalRecord
   * @param lastName of the MedicalRecord
   * @return MedicalRecord
   * @throws ResourceNotFoundException when the MedicalRecord doesn't exist
   */
  MedicalRecord getByName(String firstName, String lastName) throws ResourceNotFoundException;
}

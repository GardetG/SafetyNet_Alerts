package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.List;
import javax.validation.Valid;
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
  List<MedicalRecordDto> getAll(); 

  /**
   * Get a MedicalRecord by name.
   * 

   * @param firstName of the MedicalRecord
   * @param lastName of the MedicalRecord
   * @return MedicalRecord
   * @throws ResourceNotFoundException when the MedicalRecord doesn't exists
   */
  MedicalRecordDto getByName(String firstName, String lastName) throws ResourceNotFoundException;
  
  /**
   * Add a MedicalRecord if it doesn't already exists.
   * 

   * @param medicalRecord to add
   * @return MedicalRecord added
   * @throws ResourceAlreadyExistsException when MedicalRecord already exists
   */
  MedicalRecordDto add(@Valid MedicalRecordDto medicalRecord) throws ResourceAlreadyExistsException;

  /**
   * Update a MedicalRecord if it already exists.
   * 

   * @param medicalRecord to update
   * @return MedicalRecord updated
   * @throws ResourceNotFoundException when the MedicalRecord doesn't exists
   */
  MedicalRecordDto update(@Valid MedicalRecordDto medicalRecord) throws ResourceNotFoundException;
  
  /**
   * Delete a MedicalRecord by its name.
   * 

   * @param firstName of the MedicalRecord to delete
   * @param lastName of the MedicalRecord to delete
   * @throws ResourceNotFoundException when the MedicalRecord doesn't exists
   */
  void delete(String firstName, String lastName) throws ResourceNotFoundException;
  
}

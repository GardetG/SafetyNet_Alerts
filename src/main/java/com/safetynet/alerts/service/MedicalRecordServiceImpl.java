package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service Class implementation handling CRUD operations for MedicalRecord.
 */
@Service
@Validated
public class MedicalRecordServiceImpl implements MedicalRecordService {

  @Autowired
  MedicalRecordRepository medicalRecordRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<MedicalRecord> getAll() {
    return medicalRecordRepository.findAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord getByName(String firstName, String lastName)
          throws ResourceNotFoundException {
    
    Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findByName(firstName, lastName);
    if (medicalRecord.isEmpty()) {
      String error = String.format("Medical record of %s %s not found", firstName, lastName);
      throw new ResourceNotFoundException(error);
    }
    return medicalRecord.get();
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord add(@Valid MedicalRecord medicalRecord) 
          throws ResourceAlreadyExistsException {
  
    Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository
          .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName());

    if (existingMedicalRecord.isPresent()) {
      String error = String.format("Medical record of %s %s already exists", 
              medicalRecord.getFirstName(), medicalRecord.getLastName());
      throw new ResourceAlreadyExistsException(error);
    }

    medicalRecordRepository.add(medicalRecord);
    return medicalRecordRepository
            .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName()).get();
  
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord update(@Valid MedicalRecord medicalRecord) throws ResourceNotFoundException {
    
    Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository
            .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName());
    if (existingMedicalRecord.isEmpty()) {
      String error = String.format("Medical record of %s %s not found", 
              medicalRecord.getFirstName(), medicalRecord.getLastName());
      throw new ResourceNotFoundException(error);
    }

    medicalRecordRepository.update(medicalRecord);
    return medicalRecordRepository
            .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName()).get();
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(String firstName, String lastName) throws ResourceNotFoundException {
    
    Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository
            .findByName(firstName, lastName);
    if (existingMedicalRecord.isEmpty()) {
      String error = String.format("Medical record of %s %s not found", firstName, lastName);
      throw new ResourceNotFoundException(error);
    }

    medicalRecordRepository.delete(existingMedicalRecord.get());
    
  }

}

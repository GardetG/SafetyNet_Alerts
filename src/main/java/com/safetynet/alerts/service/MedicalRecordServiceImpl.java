package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.util.MedicalRecordMapper;
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
  public List<MedicalRecordDto> getAll() {
    return MedicalRecordMapper.toDto(medicalRecordRepository.findAll());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecordDto getByName(String firstName, String lastName)
          throws ResourceNotFoundException {
    
    Optional<MedicalRecord> medicalRecord = medicalRecordRepository.findByName(firstName, lastName);
    if (medicalRecord.isEmpty()) {
      String error = String.format("Medical record of %s %s not found", firstName, lastName);
      throw new ResourceNotFoundException(error);
    }
    return MedicalRecordMapper.toDto(medicalRecord.get());
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecordDto add(@Valid MedicalRecordDto medicalRecord) 
          throws ResourceAlreadyExistsException {
  
    Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository
          .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName());

    if (existingMedicalRecord.isPresent()) {
      String error = String.format("Medical record of %s %s already exists", 
              medicalRecord.getFirstName(), medicalRecord.getLastName());
      throw new ResourceAlreadyExistsException(error);
    }

    medicalRecordRepository.add(MedicalRecordMapper.toModel(medicalRecord));
    
    MedicalRecord addedMedicalRecord = medicalRecordRepository
            .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName()).get();
  
    return MedicalRecordMapper.toDto(addedMedicalRecord);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecordDto update(@Valid MedicalRecordDto medicalRecord) 
          throws ResourceNotFoundException {
    
    Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository
            .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName());
    if (existingMedicalRecord.isEmpty()) {
      String error = String.format("Medical record of %s %s not found", 
              medicalRecord.getFirstName(), medicalRecord.getLastName());
      throw new ResourceNotFoundException(error);
    }

    medicalRecordRepository.update(MedicalRecordMapper.toModel(medicalRecord));
    
    MedicalRecord updatedMedicalRecord = medicalRecordRepository
            .findByName(medicalRecord.getFirstName(), medicalRecord.getLastName()).get();
    
    return MedicalRecordMapper.toDto(updatedMedicalRecord);
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

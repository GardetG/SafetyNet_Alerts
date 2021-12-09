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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service Class implementation handling CRUD operations for MedicalRecord.
 */
@Service
@Validated
public class MedicalRecordServiceImpl implements MedicalRecordService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordServiceImpl.class);
  private static final String NOT_FOUND = "Medical record of %s %s not found";
  
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
      String error = String.format(NOT_FOUND, firstName, lastName);
      LOGGER.error(error);
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
  
    boolean isSucces = medicalRecordRepository.add(MedicalRecordMapper.toModel(medicalRecord));
    
    if (!isSucces) {
      String error = String.format("Medical record of %s %s already exists", 
              medicalRecord.getFirstName(), medicalRecord.getLastName());
      LOGGER.error(error);
      throw new ResourceAlreadyExistsException(error);
    }
  
    return medicalRecord;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecordDto update(@Valid MedicalRecordDto medicalRecord) 
          throws ResourceNotFoundException {
    
    boolean isSuccess = medicalRecordRepository.update(MedicalRecordMapper.toModel(medicalRecord));
    
    if (!isSuccess) {
      String error = String.format(NOT_FOUND, 
              medicalRecord.getFirstName(), medicalRecord.getLastName());
      LOGGER.error(error);
      throw new ResourceNotFoundException(error);
    }
    
    return medicalRecord;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(String firstName, String lastName) throws ResourceNotFoundException {
    
    Optional<MedicalRecord> existingMedicalRecord = medicalRecordRepository
            .findByName(firstName, lastName);
    if (existingMedicalRecord.isEmpty()) {
      String error = String.format(NOT_FOUND, firstName, lastName);
      LOGGER.error(error);
      throw new ResourceNotFoundException(error);
    }

    medicalRecordRepository.delete(existingMedicalRecord.get()); 
  }

}

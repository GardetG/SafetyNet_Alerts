package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import java.util.List;
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
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord getByName(String firstName, String lastName)
          throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord add(@Valid MedicalRecord medicalRecord)
          throws ResourceAlreadyExistsException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public MedicalRecord update(@Valid MedicalRecord medicalRecord) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(String firstName, String lastName) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    
  }

}

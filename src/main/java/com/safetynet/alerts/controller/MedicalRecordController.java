package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.dto.MedicalRecordMapper;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.MedicalRecordService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller Class for managing medical records.
 */
@Controller
@Validated
public class MedicalRecordController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MedicalRecordController.class);

  @Autowired
  MedicalRecordService medicalRecordService;
  
  /**
   * Handle HTTP GET request on medical records collection.
   * 

   * @return HTTP 200 Response with a list of all medical records
   */
  @GetMapping("/medicalRecords")
  public ResponseEntity<List<MedicalRecordDto>> getAllMedicalRecords() {
    
    LOGGER.info("Request: Get all medical records");
    List<MedicalRecordDto> allMedicalRecords = MedicalRecordMapper.toDto(
            medicalRecordService.getAll());
    
    LOGGER.info("Response: List of all medical records sent");
    return ResponseEntity.ok(allMedicalRecords);
    
  }

  /**
   * Handle HTTP GET request on a medical record resource by its firstName and lastName.
   * 

   * @param firstName of the medical record
   * @param lastName  of the medical record
   * @return HTTP 200 Response with the medical record
   * @throws ResourceNotFoundException when the medical record is not found
   */
  
  @GetMapping("/medicalRecords/medicalRecord")
  public ResponseEntity<MedicalRecordDto> getMedicalRecord(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {
    
    LOGGER.info("Request: Get medical records with parameters: {}, {}", firstName, lastName);
    MedicalRecordDto medicalRecord = MedicalRecordMapper.toDto(
            medicalRecordService.getByName(firstName, lastName));
    
    LOGGER.info("Response: medical record sent");
    return ResponseEntity.ok(medicalRecord);
    
  }
}

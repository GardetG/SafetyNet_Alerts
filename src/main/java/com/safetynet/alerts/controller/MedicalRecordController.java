package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.dto.MedicalRecordMapper;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.MedicalRecordService;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    List<MedicalRecordDto> allMedicalRecords = MedicalRecordMapper
            .toDto(medicalRecordService.getAll());

    LOGGER.info("Response: List of all medical records sent");
    return ResponseEntity.ok(allMedicalRecords);

  }

  /**
   * Handle HTTP GET request on a medical record resource by its firstName and
   * lastName.
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
    MedicalRecordDto medicalRecord = MedicalRecordMapper
            .toDto(medicalRecordService.getByName(firstName, lastName));

    LOGGER.info("Response: medical record sent");
    return ResponseEntity.ok(medicalRecord);

  }

  /**
   * Handle HTTP POST request for a medical record resource.
   * 

   * @param medicalRecord to create
   * @return HTTP 201 Response with the medical record created
   * @throws ResourceAlreadyExistsException when the medical record already exists
   */
  @PostMapping("/medicalRecord")
  public ResponseEntity<MedicalRecordDto> postMedicalRecord(
          @Valid @RequestBody MedicalRecordDto medicalRecord)
          throws ResourceAlreadyExistsException {

    LOGGER.info("Request: Create medical record of {} {}", medicalRecord.getFirstName(),
            medicalRecord.getLastName());
    MedicalRecordDto createdMedicalRecord = MedicalRecordMapper
            .toDto(medicalRecordService.add(MedicalRecordMapper.toModel(medicalRecord)));

    URI uri = URI
            .create("/medicalRecords/medicalRecord?firstName=" + createdMedicalRecord.getFirstName()
                    + "&lastName=" + createdMedicalRecord.getLastName());
    LOGGER.info("Response: medical record created");
    return ResponseEntity.created(uri).body(createdMedicalRecord);
  }

  /**
   * Handle HTTP PUT request on a medical record resource.
   * 

   * @param medicalRecord to update
   * @return HTTP 200 Response with the medical record updated
   * @throws ResourceNotFoundException when the medical record to update is not
   * found
   */
  @PutMapping("/medicalRecord")
  public ResponseEntity<MedicalRecordDto> putMedicalRecord(
          @Valid @RequestBody MedicalRecordDto medicalRecord) throws ResourceNotFoundException {

    LOGGER.info("Request: Update medical record of {} {}", medicalRecord.getFirstName(),
            medicalRecord.getLastName());
    MedicalRecordDto updatedMedicalRecord = MedicalRecordMapper
            .toDto(medicalRecordService.update(MedicalRecordMapper.toModel(medicalRecord)));

    LOGGER.info("Response: Medical record updated");
    return ResponseEntity.ok(updatedMedicalRecord);
  }
  
  /**
   * Handle HTTP DELETE request on a medical record resource.
   * 

   * @param firstName of medical record to delete
   * @param lastName of medical record to delete
   * @return HTTP 204
   * @throws ResourceNotFoundException when the medical record to delete is not found
   */
  @DeleteMapping("/medicalRecord")
  public ResponseEntity<Void> deleteMedicalRecord(
          @RequestParam @NotBlank(message = "Firstname is mandatory") String firstName,
          @RequestParam @NotBlank(message = "LastName is mandatory") String lastName)
          throws ResourceNotFoundException {

    LOGGER.info("Request: Delete medical records with parameters: {}, {}", firstName, lastName);
    medicalRecordService.delete(firstName, lastName);
    
    LOGGER.info("Response: Medical record deleted");
    return ResponseEntity.noContent().build();

  }
  
}

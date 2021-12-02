package com.safetynet.alerts.util;

import com.safetynet.alerts.dto.MedicalRecordDto;
import com.safetynet.alerts.model.MedicalRecord;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapping Class between MedicalRecord and MedicalRecordDto.
 */
public class MedicalRecordMapper {

  private MedicalRecordMapper() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Map MedicalRecord into MedicalRecordDto.
   * 

   * @param medicalRecord to map
   * @return DTO associated
   */
  public static MedicalRecordDto toDto(MedicalRecord medicalRecord) {
    MedicalRecordDto medicalRecordDto = new MedicalRecordDto();
    medicalRecordDto.setFirstName(medicalRecord.getFirstName());
    medicalRecordDto.setLastName(medicalRecord.getLastName());
    medicalRecordDto.setBirthdate(medicalRecord.getBirthdate());
    medicalRecordDto.setMedications(medicalRecord.getMedications());
    medicalRecordDto.setAllergies(medicalRecord.getAllergies());
    return medicalRecordDto;
  }
  
  /**
   * Map list of MedicalRecord into list of MedicalRecordDto.
   * 

   * @param medicalRecords to map
   * @return DTO associated
   */
  public static List<MedicalRecordDto> toDto(List<MedicalRecord> medicalRecords) {
    return medicalRecords.stream()
            .map(MedicalRecordMapper::toDto)
            .collect(Collectors.toList());
  }
  
  /**
   * Map MedicalRecordDto into MedicalRecord.
   * 

   * @param medicalRecordDto to map
   * @return MedicalRecord associated
   */
  public static MedicalRecord toModel(MedicalRecordDto medicalRecordDto) {
    MedicalRecord medicalRecord = new MedicalRecord();
    medicalRecord.setFirstName(medicalRecordDto.getFirstName());
    medicalRecord.setLastName(medicalRecordDto.getLastName());
    medicalRecord.setBirthdate(medicalRecordDto.getBirthdate());
    medicalRecord.setMedications(medicalRecordDto.getMedications());
    medicalRecord.setAllergies(medicalRecordDto.getAllergies());
    return medicalRecord;
  }
  
}
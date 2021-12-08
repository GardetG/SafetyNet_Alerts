package com.safetynet.alerts.util;

import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import java.util.List;
import java.util.Optional;

/**
 * Factory Class for PersonIndoDto.
 */
public class PersonInfoDtoFactory {

  private static final String MISSING_INFORMATION = "Information not specified";

  private PersonInfoDtoFactory() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Create a PersonInfoDto according to person and medical record, and the type
   * of DTO that must be return.
   * 

   * @param person for creating DTO
   * @param medicalRecord for creating DTO
   * @param type of the DTO to create
   * @return an instance of PersonInfoDto
   */
  public static PersonInfoDto makeDto(Person person, Optional<MedicalRecord> medicalRecord,
          DtoType type) {

    PersonInfoDto personDto = new PersonInfoDto();

    type.getFields().forEach(arg -> {
      switch (arg) {
        case "Name" :
          personDto.setFirstName(person.getFirstName());
          personDto.setLastName(person.getLastName());
          break;
        case "Address":
          personDto.setAddress(person.getAddress());
          break;
        case "Phone":
          personDto.setPhone(person.getPhone());
          break;
        case "Email":
          personDto.setEmail(person.getEmail());
          break;
        case "Age":
          if (medicalRecord.isPresent()) {
            personDto.setAge(String.valueOf(medicalRecord.get().getAge()));
            break;
          }
          personDto.setAge(MISSING_INFORMATION);
          break;
        case "Medical":
          if (medicalRecord.isPresent()) {
            personDto.setMedications(medicalRecord.get().getMedications());
            personDto.setAllergies(medicalRecord.get().getAllergies());
            break;
          }
          personDto.setMedications(List.of(MISSING_INFORMATION));
          personDto.setAllergies(List.of(MISSING_INFORMATION));
          break;
        default:
          throw new IllegalArgumentException();
      }
    });
    return personDto;
  }
}
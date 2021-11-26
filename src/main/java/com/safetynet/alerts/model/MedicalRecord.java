package com.safetynet.alerts.model;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model Class of a medical record with the firstName and lastName of the
 * person, the birthdate and any medications or allergies. FirstName and
 * lastName are mandatory for the model to be valid.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicalRecord {

  @NotBlank(message = "Firstname is mandatory")
  private String firstName;
  @NotBlank(message = "Lastname is mandatory")
  private String lastName;
  private LocalDate birthdate;
  private List<String> medications;
  private List<String> allergies;
  
}

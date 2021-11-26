package com.safetynet.alerts.dto;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for managing medical records information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MedicalRecordDto {
  
  @NotBlank(message = "Firstname is mandatory")
  private String firstName;
  @NotBlank(message = "Lastname is mandatory")
  private String lastName;
  private LocalDate birthdate;
  private List<String> medications;
  private List<String> allergies;

}

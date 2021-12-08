package com.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for managing medical records information with the name of the person, its
 * birthdate and medical data.
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
  @Past
  @NotNull(message = "Birthdate is mandatory")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private LocalDate birthdate;
  private List<String> medications;
  private List<String> allergies;

}

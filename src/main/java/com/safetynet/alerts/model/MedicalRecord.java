package com.safetynet.alerts.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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
  
  @Override
  public int hashCode() {
    return Objects.hash(allergies, birthdate, firstName, lastName, medications);
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MedicalRecord other = (MedicalRecord) obj;
    return Objects.equals(allergies, other.allergies) && Objects.equals(birthdate, other.birthdate)
            && Objects.equals(firstName, other.firstName)
            && Objects.equals(lastName, other.lastName)
            && Objects.equals(medications, other.medications);
  }
  
  
  
}

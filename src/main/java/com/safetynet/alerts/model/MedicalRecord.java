package com.safetynet.alerts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
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
  @Past
  @NotNull(message = "Birthdate is mandatory")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
  private LocalDate birthdate;
  private List<String> medications;
  private List<String> allergies;
  
  /**
   * Calculate the age of the medical record owner or throw exception if 
   * birthdate is invalid.
   * 

   * @return Age of the medical record owner
   */
  public int getAge() {
    if (LocalDate.now().isBefore(birthdate)) {
      throw new IllegalArgumentException();
    }
    Period lifeTime = Period.between(birthdate, LocalDate.now());
    return lifeTime.getYears();
  }
  
  /**
   * Check if the medical record owner is minor or not.
   * 

   * @return True if the medical record owner is minor
   */
  public boolean isMinor() {
    return (getAge() <= 18);
  }
  
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

package com.safetynet.alerts.dto;

import java.util.Objects;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for managing persons information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonDto {

  @NotBlank(message = "Firstname is mandatory")
  private String firstName;
  @NotBlank(message = "Lastname is mandatory")
  private String lastName;
  private String address;
  private String city;
  private String zip;
  private String phone;
  private String email;
  
  @Override
  public int hashCode() {
    return Objects.hash(address, city, email, firstName, lastName, phone, zip);
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
    PersonDto other = (PersonDto) obj;
    return Objects.equals(address, other.address) && Objects.equals(city, other.city)
            && Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
            && Objects.equals(lastName, other.lastName) && Objects.equals(phone, other.phone)
            && Objects.equals(zip, other.zip);
  }
}

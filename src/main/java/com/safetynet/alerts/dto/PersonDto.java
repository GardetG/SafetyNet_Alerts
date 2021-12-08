package com.safetynet.alerts.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for managing persons information with its name and coordinates.
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
  @NotBlank(message = "Address is mandatory")
  private String address;
  @NotBlank(message = "City is mandatory")
  private String city;
  @NotBlank(message = "ZIP is mandatory")
  private String zip;
  private String phone;
  private String email;
  
}

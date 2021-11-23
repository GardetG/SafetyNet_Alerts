package com.safetynet.alerts.dto;

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

  private String firstName;
  private String lastName;
  private String address;
  private String city;
  private String zip;
  private String phone;
  private String email;
  
}

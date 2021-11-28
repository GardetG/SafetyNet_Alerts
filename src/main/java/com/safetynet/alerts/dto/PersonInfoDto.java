package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for getting person information .
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PersonInfoDto {

  private String firstName;
  private String lastName;
  private String address;
  private Integer age;
  private List<String> medications;
  private List<String> allergies;
  
}

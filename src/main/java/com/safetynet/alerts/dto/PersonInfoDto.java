package com.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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
  @JsonInclude(Include.NON_NULL)
  private String address;
  @JsonInclude(Include.NON_NULL)
  private String age;
  @JsonInclude(Include.NON_NULL)
  private List<String> medications;
  @JsonInclude(Include.NON_NULL)
  private List<String> allergies;
  @JsonInclude(Include.NON_NULL)
  private String phone;
  @JsonInclude(Include.NON_NULL)
  private String email;
  
}

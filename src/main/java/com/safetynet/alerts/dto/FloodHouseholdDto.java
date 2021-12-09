package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for managing flood alert informations about houshold residents.
 */
@AllArgsConstructor
@Getter
@Setter
public class FloodHouseholdDto {

  private String address;
  private List<PersonInfoDto> residents;
  
}

package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for managing flood alert informations about houshold residents with their
 * address and the list of residents.
 */
@AllArgsConstructor
@Getter
public class FloodHouseholdDto {

  private String address;
  private List<PersonInfoDto> residents;

}

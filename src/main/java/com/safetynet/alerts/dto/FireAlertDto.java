package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for managing fire alert informations.
 */
@AllArgsConstructor
@Getter
@Setter
public class FireAlertDto {

  private List<PersonInfoDto> residents;
  private String station;
  
}

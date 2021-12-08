package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for managing fire alert informations with the list of the residents and
 * the station associated.
 */
@AllArgsConstructor
@Getter
public class FireAlertDto {

  private List<PersonInfoDto> residents;
  private String station;

}

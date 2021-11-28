package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for managing child alert informations.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChildAlertDto {

  List<PersonInfoDto> children;
  List<PersonInfoDto> householdMembers;
  
}

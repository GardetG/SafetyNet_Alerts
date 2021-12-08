package com.safetynet.alerts.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for managing child alert informations with the list of the children and
 * the list of the other household members.
 */
@AllArgsConstructor
@Getter
public class ChildAlertDto {

  List<PersonInfoDto> children;
  List<PersonInfoDto> householdMembers;

}

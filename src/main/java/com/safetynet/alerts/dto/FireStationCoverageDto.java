package com.safetynet.alerts.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for managing firestation resident coverage informations.
 */
@AllArgsConstructor
@Getter
@Setter
public class FireStationCoverageDto {

  private List<PersonInfoDto> residents;
  private int childrenCount;
  private int adultCount;
  @JsonInclude(Include.NON_NULL)
  private Integer underterminedAgeCount;
  
}

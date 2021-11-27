package com.safetynet.alerts.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * DTO for managing fireStation mapping informations.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FireStationDto {

  @NotNull(message = "Station ID is mandatory")
  @Range(min = 1, message = "Station ID must be greater than 0")
  private int station;
  @NotBlank(message = "Address is mandatory")
  private String address;
  
}

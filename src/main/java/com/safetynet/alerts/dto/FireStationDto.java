package com.safetynet.alerts.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * DTO for managing fireStation mapping informations with the address and the
 * station mapped to it.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FireStationDto {

  @Range(min = 1, message = "Station ID must be greater than 0")
  private int station;
  @NotBlank(message = "Address is mandatory")
  private String address;

}

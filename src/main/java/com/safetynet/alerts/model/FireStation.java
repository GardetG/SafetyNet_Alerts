package com.safetynet.alerts.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

/**
 * Model Class of a fireStation mapping with the station id and the address map
 * to this station. Address is mandatory and the fireStation id must be greater
 * than 0 for the model to be valid.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FireStation {

  @Range(min = 1, message = "Station ID must be greater than 0")
  private int station;
  @NotBlank(message = "Address is mandatory")
  private String address;

}

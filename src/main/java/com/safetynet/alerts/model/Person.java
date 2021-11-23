package com.safetynet.alerts.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model Class of a person with its firstName and lastName, address, city and
 * zip, and coordinate as phone and email. FirstName and lastName are mandatory
 * for the model to be valid.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Person {

  @NotBlank(message = "Firstname is mandatory")
  private String firstName;
  @NotBlank(message = "Lastname is mandatory")
  private String lastName;
  private String address;
  private String city;
  private String zip;
  private String phone;
  private String email;

}

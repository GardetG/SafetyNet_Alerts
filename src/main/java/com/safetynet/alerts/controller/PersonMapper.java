package com.safetynet.alerts.controller;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.model.Person;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapping Class between Person and PersonDto.
 */
public class PersonMapper {

  /**
   * Map Person into PersonDto.
   * 

   * @param person to map
   * @return DTO associated
   */
  public static PersonDto toDto(Person person) {
    if (person == null) {
      return null;
    }
    PersonDto personDto = new PersonDto();
    personDto.setFirstName(person.getFirstName());
    personDto.setLastName(person.getLastName());
    personDto.setAddress(person.getAddress());
    personDto.setCity(person.getCity());
    personDto.setZip(person.getZip());
    personDto.setPhone(person.getPhone());
    personDto.setEmail(person.getEmail());
    return personDto;
  }
  
  /**
   * Map list of Person into list of PersonDto.
   * 

   * @param persons to map
   * @return DTO associated
   */
  public static List<PersonDto> toDto(List<Person> persons) {
    return persons.stream()
            .map(person -> {
              return toDto(person);
            }).collect(Collectors.toList());
  }
  
  /**
   * Map PersonDto into Person.
   * 

   * @param personDto to map
   * @return Person associated
   */
  public static Person toModel(PersonDto personDto) {
    if (personDto == null) {
      return null;
    }
    Person person = new Person();
    person.setFirstName(personDto.getFirstName());
    person.setLastName(personDto.getLastName());
    person.setAddress(personDto.getAddress());
    person.setCity(personDto.getCity());
    person.setZip(personDto.getZip());
    person.setPhone(personDto.getPhone());
    person.setEmail(personDto.getEmail());
    return person;
  }
  

  
}

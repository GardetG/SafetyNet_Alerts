package com.safetynet.alerts.util;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.model.Person;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapping Class between Person and PersonDto.
 */
public class PersonMapper {

  private PersonMapper() {
    throw new IllegalStateException("Utility class");
  }
  
  
  /**
   * Map Person into PersonDto.
   * 

   * @param person to map
   * @return DTO associated
   */
  public static PersonDto toDto(Person person) {
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
            .map(PersonMapper::toDto)
            .collect(Collectors.toList());
  }
  
  /**
   * Map PersonDto into Person.
   * 

   * @param personDto to map
   * @return Person associated
   */
  public static Person toModel(PersonDto personDto) {
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

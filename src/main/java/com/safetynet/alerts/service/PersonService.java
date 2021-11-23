package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling CRUD operations for Person.
 */
@Service
public interface PersonService {

  List<PersonDto> getAll();

  PersonDto getByName(String firstName, String lastName) throws ResourceNotFoundException;
  
  PersonDto add(PersonDto person) throws ResourceAlreadyExistsException;

  PersonDto update(PersonDto person) throws ResourceNotFoundException;

  void delete(String firstName, String lastName) throws ResourceNotFoundException;

}

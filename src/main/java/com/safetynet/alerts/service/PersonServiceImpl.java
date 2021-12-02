package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDto;
import com.safetynet.alerts.exception.ResourceAlreadyExistsException;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.PersonMapper;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service Class implementation handling CRUD operations for Person.
 */
@Service
@Validated
public class PersonServiceImpl implements PersonService {

  @Autowired
  PersonRepository personRepository;

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PersonDto> getAll() {
    return PersonMapper.toDto(personRepository.findAll());
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public PersonDto getByName(String firstName, String lastName) throws ResourceNotFoundException {
    
    Optional<Person> person = personRepository.findByName(firstName, lastName);
    if (person.isEmpty()) {
      String error = String.format("%s %s not found", firstName, lastName);
      throw new ResourceNotFoundException(error);
    }
    return PersonMapper.toDto(person.get());
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonDto add(@Valid PersonDto person) throws ResourceAlreadyExistsException {
    
    Optional<Person> existingPerson = personRepository.findByName(person.getFirstName(),
            person.getLastName());

    if (existingPerson.isPresent()) {
      String error = String.format("%s %s already exists", person.getFirstName(),
              person.getLastName());
      throw new ResourceAlreadyExistsException(error);
    }

    personRepository.add(PersonMapper.toModel(person));
    
    Person addedPerson = personRepository.findByName(
            person.getFirstName(), person.getLastName()).get();
    
    return PersonMapper.toDto(addedPerson);
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PersonDto update(@Valid PersonDto person) throws ResourceNotFoundException {
    
    Optional<Person> existingPerson = personRepository.findByName(person.getFirstName(),
            person.getLastName());
    if (existingPerson.isEmpty()) {
      String error = String.format("%s %s not found", person.getFirstName(), person.getLastName());
      throw new ResourceNotFoundException(error);
    }

    personRepository.update(PersonMapper.toModel(person));
    
    Person updatedPerson = personRepository.findByName(
            person.getFirstName(), person.getLastName()).get();
    
    return PersonMapper.toDto(updatedPerson);
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void delete(String firstName, String lastName) throws ResourceNotFoundException {
    
    Optional<Person> existingPerson = personRepository.findByName(firstName, lastName);
    if (existingPerson.isEmpty()) {
      String error = String.format("%s %s not found", firstName, lastName);
      throw new ResourceNotFoundException(error);
    }

    personRepository.delete(existingPerson.get());
    
  }

}

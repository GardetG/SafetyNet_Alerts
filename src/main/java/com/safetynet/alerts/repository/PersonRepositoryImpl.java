package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for Person.
 */
@Repository
public class PersonRepositoryImpl implements LoadableRepository<Person>, PersonRepository {

  private List<Person> personsList = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Person> findAll() {
    return personsList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Person findByName(String firstName, String lastName) {
    return personsList.stream()
            .filter(person -> (person.getFirstName().equals(firstName)
            && (person.getLastName().equals(lastName))))
            .findFirst().orElse(null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean add(Person person) {
    return personsList.add(person);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(Person person) {
    Person existingPerson = personsList.stream()
            .filter(personElemnt -> (personElemnt.getFirstName().equals(person.getFirstName())
                    && (personElemnt.getLastName().equals(person.getLastName()))))
            .findFirst().orElse(null);
    
    if (existingPerson == null) {
      return false;
    }
    
    int index = personsList.indexOf(existingPerson);
    personsList.set(index, person);
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(Person person) {
    return personsList.remove(person);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setupRepository(List<Person> resourcesList) {
    personsList = resourcesList;
  }

}

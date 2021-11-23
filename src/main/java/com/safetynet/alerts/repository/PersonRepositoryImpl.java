package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;
import java.util.List;
import lombok.Setter;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for Person.
 */
@Repository
public class PersonRepositoryImpl implements PersonRepository {

  @Setter
  private List<Person> personsList;

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
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean update(Person person) {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean delete(Person person) {
    // TODO Auto-generated method stub
    return false;
  }

}

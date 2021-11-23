package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonDto;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling CRUD operations for Person.
 */
@Service
public interface PersonService {

  List<PersonDto> getAll();

  PersonDto getByName(String firstName, String lastName);

}

package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service Class implementation handling SafetyNet Alerts requests.
 */
@Service
public class AlertsServiceImpl implements AlertsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertsServiceImpl.class);
  
  @Autowired
  PersonService personService;
  
  @Autowired
  FireStationService fireStationService;
  
  @Autowired
  MedicalRecordService medicalRecordService;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCommunityEmail(String city) throws ResourceNotFoundException {
    
    // Fetch all resident of the city, throw an exception if no resident found
    return personService.getByCity(city).stream()
            // Retrieved their email
            .map(Person::getEmail)
            // Filter out any duplicate or empty field
            .distinct()
            .filter(email -> (email != null && !email.isBlank()))
            .collect(Collectors.toList());
    
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getPhoneAlert(int station) throws ResourceNotFoundException {
    
    // Fetch all mapping of this station, throw an exception if no mapping found
    List<String> addressesList = fireStationService.getByStation(station)
            .stream()
            // Retrieve addresses covered by the station
            .map(FireStation::getAddress)
            .collect(Collectors.toList());
    
    return addressesList
            .stream()
            // Retrieve all resident for each address
            .flatMap(address -> {
              List<Person> persons = new ArrayList<>();
              try {
                persons = personService.getByAddress(address);
              } catch (ResourceNotFoundException ex) {
                LOGGER.warn(ex.getMessage());
              }
              return persons.stream();
            })
            //retrieve their phone number
            .map(Person::getPhone)
            // Filter out any duplicate or empty field
            .distinct()
            .filter(phone -> (phone != null && !phone.isBlank()))
            .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PersonInfoDto> getPersonInfo(String firstName, String lastName)
          throws ResourceNotFoundException {
    
    PersonInfoDto personInfo = new PersonInfoDto();
    
    // Retrieve person information, throw an exception if not found
    Person person = personService.getByName(firstName, lastName);
    personInfo.setFirstName(person.getFirstName());
    personInfo.setLastName(person.getLastName());
    personInfo.setAddress(person.getAddress());
    
    // Try retrieve medical record data if it exists, else log warning and let fields null
    try {
      MedicalRecord medicalRecord = medicalRecordService.getByName(firstName, lastName);
      Period lifeTime = Period.between(medicalRecord.getBirthdate(), LocalDate.now());
      personInfo.setAge(String.valueOf(lifeTime.getYears()));
      personInfo.setMedications(medicalRecord.getMedications());
      personInfo.setAllergies(medicalRecord.getAllergies());
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
      personInfo.setAge("Information not specified");
      personInfo.setMedications(List.of("Information not specified"));
      personInfo.setAllergies(List.of("Information not specified"));
    }

    return List.of(personInfo);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChildAlertDto childAlert(String address) throws ResourceNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

}

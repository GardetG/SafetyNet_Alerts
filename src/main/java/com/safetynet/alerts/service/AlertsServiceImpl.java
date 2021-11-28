package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
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
    List<String> emailsList = personService.getByCity(city).stream()
            // Retrieved their email
            .map(person -> { 
              return person.getEmail(); 
            })
            // Filter out any duplicate or empty field
            .distinct()
            .filter(email -> (email != null && !email.isBlank()))
            .collect(Collectors.toList());
    
    return emailsList;
    
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
            .map(fireStation -> {
              return fireStation.getAddress();
            }).collect(Collectors.toList());
    
    List<String> phoneNumbersList = addressesList
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
            .map(person -> {
              return person.getPhone();
            })
            // Filter out any duplicate or empty field
            .distinct()
            .filter(phone -> (phone != null && !phone.isBlank()))
            .collect(Collectors.toList());
    
    return phoneNumbersList;
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
      personInfo.setAge(lifeTime.getYears());
      personInfo.setMedications(medicalRecord.getMedications());
      personInfo.setAllergies(medicalRecord.getAllergies());
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
    }

    return List.of(personInfo);
  }

}

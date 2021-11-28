package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
    personInfo.setAge("Information not specified");
    personInfo.setMedications(List.of("Information not specified"));
    personInfo.setAllergies(List.of("Information not specified"));
    
    // retrieve medical record data if it exists
    Optional<MedicalRecord> medicalRecord = getMedicalRecord(person);
    if (medicalRecord.isPresent()) {
      personInfo.setAge(String.valueOf(medicalRecord.get().getAge()));
      personInfo.setMedications(medicalRecord.get().getMedications());
      personInfo.setAllergies(medicalRecord.get().getAllergies());
    }

    return List.of(personInfo);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChildAlertDto childAlert(String address) throws ResourceNotFoundException {
    
    // Fetch resident at the address and map them with optional medical record
    Map<Person, Optional<MedicalRecord>> household = personService.getByAddress(address)
            .stream()
            .collect(Collectors.toMap(Function.identity(), this::getMedicalRecord));
    
    List<PersonInfoDto> childrenList = household.entrySet().stream()
            // Filter resident with medical record and who are minor
            .filter(entry -> entry.getValue().isPresent() && entry.getValue().get().isMinor())
            .map(entry -> {
              PersonInfoDto childDto = new PersonInfoDto();
              childDto.setFirstName(entry.getKey().getFirstName());
              childDto.setLastName(entry.getKey().getLastName());
              childDto.setAge(String.valueOf(entry.getValue().get().getAge()));
              return childDto;
            }).collect(Collectors.toList());
    
    List<PersonInfoDto> householdMembers = household.entrySet().stream()
            // Filter out minor
            .filter(entry -> !(entry.getValue().isPresent() && entry.getValue().get().isMinor()))
            .map(entry -> {
              PersonInfoDto childDto = new PersonInfoDto();
              childDto.setFirstName(entry.getKey().getFirstName());
              childDto.setLastName(entry.getKey().getLastName());
              if (entry.getValue().isEmpty()) {
                // If they don't have medical record, precise the lack of informations
                childDto.setAge("Information not specified");
              }
              return childDto;
            }).collect(Collectors.toList());
    
    
    ChildAlertDto childAlertDto = new ChildAlertDto();
    childAlertDto.setChildren(childrenList);
    childAlertDto.setHouseholdMembers(householdMembers);
    
    return childAlertDto;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public FireAlertDto fireAlert(String address) throws ResourceNotFoundException {
    
    // Fetch resident at the address and map them with optional medical record
    Map<Person, Optional<MedicalRecord>> residents = personService.getByAddress(address)
            .stream()
            .collect(Collectors.toMap(Function.identity(), this::getMedicalRecord));
    
    List<PersonInfoDto> residentsList = residents.entrySet().stream()
            .map(entry -> {
              PersonInfoDto personInfo = new PersonInfoDto();
              personInfo.setFirstName(entry.getKey().getFirstName());
              personInfo.setLastName(entry.getKey().getLastName());
              personInfo.setPhone(entry.getKey().getPhone());
              
              personInfo.setAge("Information not specified");
              personInfo.setMedications(List.of("Information not specified"));
              personInfo.setAllergies(List.of("Information not specified"));
              if (entry.getValue().isPresent()) {
                personInfo.setAge(String.valueOf(entry.getValue().get().getAge()));
                personInfo.setMedications(entry.getValue().get().getMedications());
                personInfo.setAllergies(entry.getValue().get().getAllergies());
              }
              return personInfo;
            }).collect(Collectors.toList());
    
    String station = "No station mapped for this address";
    try {
      station = String.valueOf(fireStationService.getByAddress(address).getStation());
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
    }
    
    FireAlertDto fireAlertDto = new FireAlertDto();
    fireAlertDto.setResidents(residentsList);
    fireAlertDto.setStation(station);
    
    return fireAlertDto;
  }
  
  private Optional<MedicalRecord> getMedicalRecord(Person person) {
    Optional<MedicalRecord> medicalRecord = Optional.empty();
    try {
      medicalRecord = Optional.of(medicalRecordService.getByName(
               person.getFirstName(), person.getLastName()));
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
    }
    return medicalRecord;
  }

}

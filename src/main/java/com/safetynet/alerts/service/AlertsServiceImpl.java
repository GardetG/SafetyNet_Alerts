package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.FloodHouseholdDto;
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

    List<String> emailList = new ArrayList<>();

    try {
      // Fetch all resident of the city, throw an exception if no resident found
      emailList = personService.getByCity(city).stream()
              // Retrieved their email
              .map(Person::getEmail)
              // Filter out any duplicate or empty field
              .distinct().filter(email -> (email != null && !email.isBlank()))
              .collect(Collectors.toList());
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
    }

    if (emailList.isEmpty()) {
      String error = String.format("No resident emails found for %s", city);
      throw new ResourceNotFoundException(error);
    }

    return emailList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getPhoneAlert(int station) throws ResourceNotFoundException {

    List<String> phoneList = new ArrayList<>();

    try {
      // Fetch all mapping of this station, throw an exception if no mapping found
      phoneList = fireStationService.getByStation(station).stream()
              // Retrieve addresses covered by the station
              .map(FireStation::getAddress)
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
              // retrieve their phone number
              .map(Person::getPhone)
              // Filter out any duplicate or empty field
              .distinct().filter(phone -> (phone != null && !phone.isBlank()))
              .collect(Collectors.toList());
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
    }

    if (phoneList.isEmpty()) {
      String error = String.format("No resident phone number found for station %s", station);
      throw new ResourceNotFoundException(error);
    }

    return phoneList;
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

    // retrieve medical record data if it exists
    Optional<MedicalRecord> medicalRecord = getMedicalRecord(person);
    mapMedicalRecordData(personInfo, medicalRecord);

    return List.of(personInfo);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChildAlertDto childAlert(String address) throws ResourceNotFoundException {

    // Fetch resident at the address and map them with optional medical record
    Map<Person, Optional<MedicalRecord>> household = personService.getByAddress(address).stream()
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

    // Fetch resident at the address and medical record data if it exists
    List<PersonInfoDto> residentsList = getPersonInfoFromAddress(address);

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

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FloodHouseholdDto> floodAlert(List<Integer> stations)
          throws ResourceNotFoundException {
    
    List<FloodHouseholdDto> floodlist = stations.stream()
            // Fetch all addresses cover by the stations
            .flatMap(station -> {
              List<String> addressList = new ArrayList<>();
              try {
                addressList = fireStationService
                    .getByStation(station.intValue())
                    .stream()
                    .map(FireStation::getAddress)
                    .collect(Collectors.toList());
              } catch (ResourceNotFoundException ex) {
                LOGGER.warn(ex.getMessage());
              }
              return addressList.stream();
            })
            .map(address -> {
              List<PersonInfoDto> residentsList = new ArrayList<>();
              // Fetch resident at the address and medical record data if it exists
              try {
                residentsList = getPersonInfoFromAddress(address);
              } catch (ResourceNotFoundException ex) {
                LOGGER.warn(ex.getMessage());
              }
              FloodHouseholdDto floodDto = new FloodHouseholdDto();
              floodDto.setAddress(address);
              floodDto.setResidents(residentsList);
              return floodDto;
            })
            .filter(household -> !household.getResidents().isEmpty())
            .collect(Collectors.toList());

    if (floodlist.isEmpty()) {
      String error = String.format("No resident coverd by stations %s", stations);
      throw new ResourceNotFoundException(error);
    }
    
    return floodlist;
  }

  private List<PersonInfoDto> getPersonInfoFromAddress(String address)
          throws ResourceNotFoundException {
    // Fetch resident at the address and map them with optional medical record
    Map<Person, Optional<MedicalRecord>> residents = personService
            .getByAddress(address)
            .stream()
            .collect(Collectors.toMap(Function.identity(), this::getMedicalRecord));

    return residents.entrySet().stream().map(entry -> {
      PersonInfoDto personInfo = new PersonInfoDto();
      personInfo.setFirstName(entry.getKey().getFirstName());
      personInfo.setLastName(entry.getKey().getLastName());
      personInfo.setPhone(entry.getKey().getPhone());
      mapMedicalRecordData(personInfo, entry.getValue());
      return personInfo;
    }).collect(Collectors.toList());
  }

  private Optional<MedicalRecord> getMedicalRecord(Person person) {
    Optional<MedicalRecord> medicalRecord = Optional.empty();
    try {
      medicalRecord = Optional
              .of(medicalRecordService.getByName(person.getFirstName(), person.getLastName()));
    } catch (ResourceNotFoundException ex) {
      LOGGER.warn(ex.getMessage());
    }
    return medicalRecord;
  }

  private void mapMedicalRecordData(PersonInfoDto personInfo,
          Optional<MedicalRecord> medicalRecord) {

    if (medicalRecord.isEmpty()) {
      personInfo.setAge("Information not specified");
      personInfo.setMedications(List.of("Information not specified"));
      personInfo.setAllergies(List.of("Information not specified"));
      return;
    }
    personInfo.setAge(String.valueOf(medicalRecord.get().getAge()));
    personInfo.setMedications(medicalRecord.get().getMedications());
    personInfo.setAllergies(medicalRecord.get().getAllergies());
    return;

  }
}

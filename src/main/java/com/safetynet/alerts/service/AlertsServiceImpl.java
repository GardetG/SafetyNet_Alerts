package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.FireStationCoverageDto;
import com.safetynet.alerts.dto.FloodHouseholdDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.FireStationRepository;
import com.safetynet.alerts.repository.MedicalRecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import com.safetynet.alerts.util.DtoType;
import com.safetynet.alerts.util.PersonInfoDtoFactory;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
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
  PersonRepository personRepository;

  @Autowired
  FireStationRepository fireStationRepository;

  @Autowired
  MedicalRecordRepository medicalRecordRepository;

  private enum Count { 
    UNKNOW, CHILD, ADULT 
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getCommunityEmail(String city) throws ResourceNotFoundException {

    // Fetch all resident of the city an retrieve email
    List<String> emailList = personRepository.findByCity(city).stream()
            .map(Person::getEmail)
            .distinct()
            .filter(email -> (email != null && !email.isBlank()))
            .collect(Collectors.toList());

    if (emailList.isEmpty()) {
      String error = String.format("No resident emails found for %s", city);
      LOGGER.error(error);
      throw new ResourceNotFoundException("");
    }

    return emailList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<String> getPhoneAlert(int station) throws ResourceNotFoundException {

    // Fetch all addresses covered by this station
    List<String> phoneList = fireStationRepository.findByStation(station).stream()
            .map(FireStation::getAddress)
            .flatMap(address -> personRepository.findByAddress(address).stream())
            .map(Person::getPhone)
            .distinct()
            .filter(phone -> (phone != null && !phone.isBlank()))
            .collect(Collectors.toList());

    if (phoneList.isEmpty()) {
      String error = String.format("No resident phone number found for station %s", station);
      LOGGER.error(error);
      throw new ResourceNotFoundException("");
    }

    return phoneList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PersonInfoDto> getPersonInfo(String firstName, String lastName)
          throws ResourceNotFoundException {

    // Retrieve person information, throw an exception if not found
    Optional<Person> personInfo = personRepository.findByName(firstName, lastName);
    if (personInfo.isEmpty()) {
      String error = String.format("%s %s not found", firstName, lastName);
      LOGGER.error(error);
      throw new ResourceNotFoundException(error);
    }

    return List.of(personInfo.get()).stream()
            .map(person -> {
              Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
              return PersonInfoDtoFactory.makeDto(person, medicalRecord, DtoType.PERSONINFO);
            })
            .collect(Collectors.toList());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChildAlertDto childAlert(String address) throws ResourceNotFoundException {

    // Fetch resident at the address
    List<Person> residents = personRepository.findByAddress(address);

    List<PersonInfoDto> children = new ArrayList<>();
    List<PersonInfoDto> householdMembers = new ArrayList<>();

    // Separate adult and minor and add to respective list
    residents.forEach(person -> {
      Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
      if (medicalRecord.isEmpty()) {
        householdMembers.add(PersonInfoDtoFactory.makeDto(person, medicalRecord, DtoType.AGE));
        
        return;
      }
      if (medicalRecord.get().isMinor()) {
        children.add(PersonInfoDtoFactory.makeDto(person, medicalRecord, DtoType.AGE));
      } else {
        householdMembers.add(PersonInfoDtoFactory.makeDto(person, medicalRecord, DtoType.NAME));
      }
    });
    
    if (children.isEmpty()) {
      String error = String.format("No children found living at %s", address);
      LOGGER.error(error);
      throw new ResourceNotFoundException("");
    }
    
    return new ChildAlertDto(children, householdMembers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireAlertDto fireAlert(String address) throws ResourceNotFoundException {

    // Fetch resident at the address and create DTO
    List<PersonInfoDto> residentsList = personRepository.findByAddress(address).stream()
            .map(person -> {
              Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
              return PersonInfoDtoFactory.makeDto(person, medicalRecord, DtoType.ALERT);
            })
            .collect(Collectors.toList());

    if (residentsList.isEmpty()) {
      String error = String.format("No residents found living at %s", address);
      LOGGER.error(error);
      throw new ResourceNotFoundException("");
    }

    // Fetch station covering this address
    String station = "No station mapped for this address";
    List<FireStation> fireStations = fireStationRepository.findByAddress(address);
    if (!fireStations.isEmpty()) {
      station = fireStations.stream()
              .map(FireStation::getStation)
              .map(String::valueOf)
              .collect(Collectors.toList()).toString();
    }

    return new FireAlertDto(residentsList, station);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<FloodHouseholdDto> floodAlert(List<Integer> stations)
          throws ResourceNotFoundException {

    // Fetch addresses covered by the stations
    List<FloodHouseholdDto> floodlist = stations.stream()
            .flatMap(station -> fireStationRepository.findByStation(station).stream())
            .map(FireStation::getAddress)
            .distinct()
            .map(address -> {
              // Fetch resident at the address and medical record data if it exists
              List<PersonInfoDto> residentsList = personRepository.findByAddress(address).stream()
                      .map(person -> {
                        Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
                        return PersonInfoDtoFactory.makeDto(person, medicalRecord, DtoType.ALERT);
                      })
                      .collect(Collectors.toList());
              return new FloodHouseholdDto(address, residentsList);
            }).filter(household -> !household.getResidents().isEmpty())
            .collect(Collectors.toList());

    if (floodlist.isEmpty()) {
      String error = String.format("No residents covered found for stations %s", stations);
      LOGGER.error(error);
      throw new ResourceNotFoundException("");
    }

    return floodlist;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public FireStationCoverageDto fireStationCoverage(int station) throws ResourceNotFoundException {

    // Fetch all resident covered by station
    List<Person> residents = fireStationRepository.findByStation(station).stream()
            .distinct()
            .map(FireStation::getAddress)
            .flatMap(address -> personRepository.findByAddress(address).stream())
            .collect(Collectors.toList());

    if (residents.isEmpty()) {
      String error = String.format("No residents covered found for station %s", station);
      LOGGER.error(error);
      throw new ResourceNotFoundException("");
    }

    EnumMap<Count, Integer> counter = new EnumMap<>(Count.class);
    counter.put(Count.UNKNOW, 0);
    counter.put(Count.CHILD, 0);
    counter.put(Count.ADULT, 0);
    List<PersonInfoDto> residentsList = new ArrayList<>();

    // Fetch medical record for each resident and count adult and minor
    residents.forEach(person -> {
      Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
      residentsList.add(PersonInfoDtoFactory
              .makeDto(person, medicalRecord, DtoType.STATIONCOVERAGE));
      if (medicalRecord.isEmpty()) {
        counter.put(Count.UNKNOW, counter.get(Count.UNKNOW) + 1);
      } else if (medicalRecord.get().isMinor()) {
        counter.put(Count.CHILD, counter.get(Count.CHILD) + 1);
      } else {
        counter.put(Count.ADULT, counter.get(Count.ADULT) + 1);
      }
    });

    return new FireStationCoverageDto(
            residentsList, counter.get(Count.CHILD), counter.get(Count.ADULT), 
            counter.get(Count.UNKNOW) == 0 ? null : counter.get(Count.UNKNOW));
  }

  private Optional<MedicalRecord> getAsssociateMedicalRecord(Person person) {
    Optional<MedicalRecord> medicalRecord = medicalRecordRepository
            .findByName(person.getFirstName(), person.getLastName());
    if (medicalRecord.isEmpty()) {
      LOGGER.debug("Can't associate any medical record to {} {}", person.getFirstName(),
              person.getLastName());
    }
    return medicalRecord;
  }

}

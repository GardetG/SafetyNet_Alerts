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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      throw new ResourceNotFoundException(error);
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

    // Retrieve person information, throw an exception if not found
    Optional<Person> personInfo = personRepository.findByName(firstName, lastName);
    if (personInfo.isEmpty()) {
      String error = String.format("%s %s not found", firstName, lastName);
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

    if (residents.isEmpty()) {
      String error = String.format("No residents found living at %s", address);
      throw new ResourceNotFoundException(error);
    }

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

    ChildAlertDto childAlertDto = new ChildAlertDto(children, householdMembers);

    return childAlertDto;
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
      throw new ResourceNotFoundException(error);
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

    FireAlertDto fireAlertDto = new FireAlertDto(residentsList, station);

    return fireAlertDto;
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
              FloodHouseholdDto floodDto = new FloodHouseholdDto(address, residentsList);
              return floodDto;
            }).filter(household -> !household.getResidents().isEmpty())
            .collect(Collectors.toList());

    if (floodlist.isEmpty()) {
      String error = String.format("No residents covered found for stations %s", stations);
      throw new ResourceNotFoundException(error);
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
      throw new ResourceNotFoundException(error);
    }

    Map<String, Integer> counter = new HashMap<>();
    counter.put("unknow", 0);
    counter.put("children", 0);
    counter.put("adult", 0);
    List<PersonInfoDto> residentsList = new ArrayList<>();

    // Fetch medical record for each resident and count adult and minor
    residents.forEach(person -> {
      Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
      residentsList.add(PersonInfoDtoFactory
              .makeDto(person, medicalRecord, DtoType.STATIONCOVERAGE));
      if (medicalRecord.isEmpty()) {
        counter.put("unknow", counter.get("unknow") + 1);
      } else if (medicalRecord.get().isMinor()) {
        counter.put("children", counter.get("children") + 1);
      } else {
        counter.put("adult", counter.get("adult") + 1);
      }
    });

    FireStationCoverageDto fireStationCoverageDto = new FireStationCoverageDto(
            residentsList, counter.get("children"), counter.get("adult"), 
            counter.get("unknow") == 0 ? null : counter.get("unknow"));
    return fireStationCoverageDto;
  }



  private Optional<MedicalRecord> getAsssociateMedicalRecord(Person person) {
    Optional<MedicalRecord> medicalRecord = medicalRecordRepository
            .findByName(person.getFirstName(), person.getLastName());
    if (medicalRecord.isEmpty()) {
      LOGGER.warn("Can't associate any medical record to {} {}", person.getFirstName(),
              person.getLastName());
    }
    return medicalRecord;
  }

}

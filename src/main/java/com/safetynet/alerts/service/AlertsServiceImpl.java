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
import java.util.ArrayList;
import java.util.Collections;
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
            .distinct().filter(email -> (email != null && !email.isBlank()))
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
    List<String> phoneList = fireStationRepository.findByStation(station)
            .stream()
            .map(FireStation::getAddress)
            .flatMap(address -> personRepository.findByAddress(address).stream())
            .map(Person::getPhone).distinct()
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
              return createPersonInfoDto(person, medicalRecord,
                      List.of("address", "email", "age", "medical"));
            })
            .collect(Collectors.toList());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ChildAlertDto childAlert(String address) throws ResourceNotFoundException {

    // Fetch resident at the address and map them with optional medical record
    List<Person> residents = personRepository.findByAddress(address);

    if (residents.isEmpty()) {
      String error = String.format("No residents found living at %s", address);
      throw new ResourceNotFoundException(error);
    }

    List<PersonInfoDto> children = new ArrayList<>();
    List<PersonInfoDto> householdMembers = new ArrayList<>();

    residents.forEach(person -> {
      Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
      if (medicalRecord.isEmpty()) {
        householdMembers.add(createPersonInfoDto(person, medicalRecord, List.of("age")));
        return;
      }
      if (medicalRecord.get().isMinor()) {
        children.add(createPersonInfoDto(person, medicalRecord, List.of("age")));
      } else {
        householdMembers.add(createPersonInfoDto(person, medicalRecord, Collections.emptyList()));
      }
    });

    ChildAlertDto childAlertDto = new ChildAlertDto();
    childAlertDto.setChildren(children);
    childAlertDto.setHouseholdMembers(householdMembers);

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
              return createPersonInfoDto(person, medicalRecord, List.of("phone", "age", "medical"));
            })
            .collect(Collectors.toList());

    if (residentsList.isEmpty()) {
      String error = String.format("No residents found living at %s", address);
      throw new ResourceNotFoundException(error);
    }

    String station = "No station mapped for this address";
    Optional<FireStation> fireStation = fireStationRepository.findByAddress(address);
    if (fireStation.isPresent()) {
      station = String.valueOf(fireStation.get().getStation());
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
            .flatMap(station -> fireStationRepository.findByStation(station).stream())
            .map(FireStation::getAddress)
            .map(address -> {
              // Fetch resident at the address and medical record data if it exists
              List<PersonInfoDto> residentsList = personRepository.findByAddress(address).stream()
                      .map(person -> {
                        Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
                        return createPersonInfoDto(person, medicalRecord,
                                List.of("phone", "age", "medical"));
                      })
                      .collect(Collectors.toList());
              FloodHouseholdDto floodDto = new FloodHouseholdDto();
              floodDto.setAddress(address);
              floodDto.setResidents(residentsList);
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

    // Fetch all resident with optional medical record covered by station
    List<Person> residents = fireStationRepository.findByStation(station).stream()
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

    residents.forEach(person -> {
      Optional<MedicalRecord> medicalRecord = getAsssociateMedicalRecord(person);
      residentsList.add(createPersonInfoDto(person, medicalRecord, List.of("address", "phone")));
      if (medicalRecord.isEmpty()) {
        counter.put("unknow", counter.get("unknow") + 1);
      } else if (medicalRecord.get().isMinor()) {
        counter.put("children", counter.get("children") + 1);
      } else {
        counter.put("adult", counter.get("adult") + 1);
      }
    });

    FireStationCoverageDto fireStationCoverageDto = new FireStationCoverageDto();
    fireStationCoverageDto.setResidents(residentsList);
    fireStationCoverageDto.setChildrenCount(counter.get("children"));
    fireStationCoverageDto.setAdultCount(counter.get("adult"));
    fireStationCoverageDto.setUnderterminedAgeCount(
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

  private PersonInfoDto createPersonInfoDto(Person person, Optional<MedicalRecord> medicalRecord,
          List<String> args) {

    PersonInfoDto personDto = new PersonInfoDto();
    personDto.setFirstName(person.getFirstName());
    personDto.setLastName(person.getLastName());
    args.forEach(arg -> {
      switch (arg) {
        case "address":
          personDto.setAddress(person.getAddress());
          break;
        case "phone":
          personDto.setPhone(person.getPhone());
          break;
        case "email":
          personDto.setEmail(person.getEmail());
          break;
        case "age":
          if (medicalRecord.isPresent()) {
            personDto.setAge(String.valueOf(medicalRecord.get().getAge()));
            break;
          }
          personDto.setAge("Information not specified");
          break;
        case "medical":
          if (medicalRecord.isPresent()) {
            personDto.setMedications(medicalRecord.get().getMedications());
            personDto.setAllergies(medicalRecord.get().getAllergies());
            break;
          }
          personDto.setMedications(List.of("Information not specified"));
          personDto.setAllergies(List.of("Information not specified"));
          break;
        default:
          break;
      }
    });
    return personDto;
  }

}

package com.safetynet.alerts.service;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.FireStationCoverageDto;
import com.safetynet.alerts.dto.FloodHouseholdDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service Class interface handling SafetyNet Alerts requests.
 */
@Service
public interface AlertsService {

  /**
   * Return a list of all residents' email of the city without duplicate.
   * 

   * @param city of the residents
   * @return list of emails
   * @throws ResourceNotFoundException when no residents found for this city
   */
  List<String> getCommunityEmail(String city) throws ResourceNotFoundException;

  /**
   * Return a list of all residents' phone numbers of the fireStation without
   * duplicate.
   * 

   * @param station of the residents
   * @return list of phone numbers
   * @throws ResourceNotFoundException when no residents covered found for this
   *                                   fireStation
   */
  List<String> getPhoneAlert(int station) throws ResourceNotFoundException;

  /**
   * Return person informations with address, age, and medical data.
   * 

   * @param firstName of the person
   * @param lastName  of the person
   * @return person informations
   * @throws ResourceNotFoundException when person is not found
   */
  List<PersonInfoDto> getPersonInfo(String firstName, String lastName)
          throws ResourceNotFoundException;

  /**
   * Return child alert informations with list of children and list of others
   * household members.
   * 

   * @param address of the household
   * @return child alert information
   * @throws ResourceNotFoundException when no residents found at this address
   */
  ChildAlertDto childAlert(String address) throws ResourceNotFoundException;

  /**
   * Return fire alert informations with list of residents and associated
   * station.
   * 

   * @param address of the residents
   * @return fire alert information
   * @throws ResourceNotFoundException when no residents found at this address
   */
  FireAlertDto fireAlert(String address) throws ResourceNotFoundException;

  /**
   * Return flood alert informations with the list of all residents covered by
   * the stations grouped by address.
   * 

   * @param stations of the residents
   * @return flood alert informations
   * @throws ResourceNotFoundException when no resident covered found for these stations
   */
  List<FloodHouseholdDto> floodAlert(List<Integer> stations) throws ResourceNotFoundException;

  /**
   * Return firestation coverage informations with the list of all residents covered and
   * children and adult count.
   * 

   * @param station of the residents
   * @return firestation coverage informations
   * @throws ResourceNotFoundException when no resident covered found for this stations
   */
  FireStationCoverageDto fireStationCoverage(int station) throws ResourceNotFoundException;

}

package com.safetynet.alerts.util;

import com.safetynet.alerts.dto.FireStationDto;
import com.safetynet.alerts.model.FireStation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapping Class between FireStation and FireStationDto.
 */
public class FireStationMapper {

  private FireStationMapper() {
    throw new IllegalStateException("Utility class");
  }
  
  /**
   * Map FireStation into FireStationDto.
   * 

   * @param fireStation to map
   * @return DTO associated
   */
  public static FireStationDto toDto(FireStation fireStation) {
    FireStationDto fireStationDto = new FireStationDto();
    fireStationDto.setStation(fireStation.getStation());
    fireStationDto.setAddress(fireStation.getAddress());
    return fireStationDto;
  }
  
  /**
   * Map list of FireStation into list of FireStationDto.
   * 

   * @param fireStations to map
   * @return DTO associated
   */
  public static List<FireStationDto> toDto(List<FireStation> fireStations) {
    return fireStations.stream()
            .map(FireStationMapper::toDto)
            .collect(Collectors.toList());
  }
  
  /**
   * Map FireStationDto into FireStation.
   * 

   * @param fireStationDto to map
   * @return FireStation associated
   */
  public static FireStation toModel(FireStationDto fireStationDto) {
    FireStation fireStation = new FireStation();
    fireStation.setStation(fireStationDto.getStation());
    fireStation.setAddress(fireStationDto.getAddress());

    return fireStation;
  }
  
}

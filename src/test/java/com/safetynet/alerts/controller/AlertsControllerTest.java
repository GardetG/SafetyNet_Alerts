package com.safetynet.alerts.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.safetynet.alerts.dto.ChildAlertDto;
import com.safetynet.alerts.dto.FireAlertDto;
import com.safetynet.alerts.dto.FireStationCoverageDto;
import com.safetynet.alerts.dto.FloodHouseholdDto;
import com.safetynet.alerts.dto.PersonInfoDto;
import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.AlertsService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("UnitTests")
@WebMvcTest(AlertsController.class)
@AutoConfigureRestDocs
class AlertsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AlertsService alertsService;

  @Test
  void getCommunityEmailTest() throws Exception {
    // GIVEN
    when(alertsService.getCommunityEmail(anyString())).thenReturn(List.of(
            "email@test.fr", "email2@test.fr"));

    // WHEN
    mockMvc.perform(get("/communityEmail?city=city"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", is("email@test.fr")))
            .andExpect(jsonPath("$[1]", is("email2@test.fr")))
            .andDo(document("GetCommunityEmail",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("city").description(
                    "The city from which we want to retrieve the residents' emails."
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).getCommunityEmail("city");
  }
  
  @Test
  void getCommunityEmailNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.getCommunityEmail(anyString())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/communityEmail?city=city"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetCommunityEmailNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).getCommunityEmail("city");
  }
  
  @Test
  void getCommunityEmailInvalidTest() throws Exception {
    // GIVEN
    when(alertsService.getCommunityEmail(anyString())).thenReturn(List.of(
            "email@test.fr", "email2@test.fr"));

    // WHEN
    mockMvc.perform(get("/communityEmail?city="))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("City is mandatory")));
    verify(alertsService, times(0)).getCommunityEmail(anyString());
  }
  
  @Test
  void getPhoneAlertTest() throws Exception {
    // GIVEN
    when(alertsService.getPhoneAlert(anyInt())).thenReturn(List.of(
            "000-000-0001", "000-000-0002"));

    // WHEN
    mockMvc.perform(get("/phoneAlert?firestation=1"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0]", is("000-000-0001")))
            .andExpect(jsonPath("$[1]", is("000-000-0002")))
            .andDo(document("GetPhoneAlert",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("firestation").description(
                    "The fireStation id from which we want to retrieve covered residents' phone."
                    + "This parameter *must be greater than 0*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).getPhoneAlert(1);
  }
  
  @Test
  void getPhoneAlertFoundTest() throws Exception {
    // GIVEN
    when(alertsService.getPhoneAlert(anyInt())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/phoneAlert?firestation=9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetPhoneAlertNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).getPhoneAlert(9);
  }
  
  @Test
  void getPhoneAlertInvalidTest() throws Exception {
    // GIVEN
    when(alertsService.getPhoneAlert(anyInt())).thenReturn(List.of(
            "000-000-0001", "000-000-0002"));

    // WHEN
    mockMvc.perform(get("/phoneAlert?firestation=0"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Station Id must be greater than 0")));
    verify(alertsService, times(0)).getPhoneAlert(anyInt());
  }
  
  @Test
  void getPersonInfoTest() throws Exception {
    // GIVEN
    PersonInfoDto personTest = new PersonInfoDto("firstName", "lastName", "address", 
            "18", List.of("med1", "med2"), Collections.emptyList(), null, null);
    when(alertsService.getPersonInfo(anyString(), anyString())).thenReturn(List.of(personTest));

    // WHEN
    mockMvc.perform(get("/personInfo?firstName=firstName&lastName=lastName"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("firstName")))
            .andExpect(jsonPath("$[0].lastName", is("lastName")))
            .andExpect(jsonPath("$[0].age", is("18")))
            .andDo(document("GetPersonInfo",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("firstName").description(
                    "The firstName of the person."
                    + "This parameter *must be greater than 0*.")
                            .optional(),
                            parameterWithName("lastName").description(
                    "The lastName of the person."
                    + "This parameter *must be greater than 0*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).getPersonInfo("firstName", "lastName");
  }
  
  @Test
  void getPersonInfoNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.getPersonInfo(anyString(), anyString())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/personInfo?firstName=firstName9&lastName=lastName"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetPersonInfoNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).getPersonInfo("firstName9", "lastName");
  }
  
  @Test
  void getPersonInfoInvalidTest() throws Exception {
    // GIVEN
    when(alertsService.getPhoneAlert(anyInt())).thenReturn(List.of(
            "000-000-0001", "000-000-0002"));

    // WHEN
    mockMvc.perform(get("/personInfo?firstName= &lastName=lastName"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Firstname is mandatory")));
    verify(alertsService, times(0)).getPersonInfo(anyString(), anyString());
  }
  
  @Test
  void getChildAlertTest() throws Exception {
    // GIVEN
    ChildAlertDto childAlertDto = new ChildAlertDto(List.of(
              new PersonInfoDto("FirstNameA", "LastName", null, "10", null, null, null, null)),
            List.of(
              new PersonInfoDto("FirstNameB", "LastName", null, null, null, null, null, null),
              new PersonInfoDto("FirstNameC", "LastName", null, null, null, null, null, null))); 
    when(alertsService.childAlert(anyString())).thenReturn(childAlertDto);

    // WHEN
    mockMvc.perform(get("/childAlert?address=address"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.children", hasSize(1)))
            .andExpect(jsonPath("$.householdMembers", hasSize(2)))
            .andDo(document("GetChildAlert",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("address").description(
                    "Household address for which we want to retrieve child alert informations."
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).childAlert("address");
  }
  
  @Test
  void getChildAlertNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.childAlert(anyString())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/childAlert?address=address"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetChildAlertNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).childAlert("address");
  }
  
  @Test
  void getChildAlertInvalidTest() throws Exception {
    // GIVEN
    ChildAlertDto childAlertDto = new ChildAlertDto(
            Collections.emptyList(), Collections.emptyList()); 
    when(alertsService.childAlert(anyString())).thenReturn(childAlertDto);

    // WHEN
    mockMvc.perform(get("/childAlert?address= "))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Address is mandatory")));
    verify(alertsService, times(0)).childAlert(anyString());
  }
  
  @Test
  void getFireAlertTest() throws Exception {
    // GIVEN
    FireAlertDto fireAlertDto = new FireAlertDto(List.of(
              new PersonInfoDto("FirstNameA", "LastName", null, "10", Collections.emptyList(),
                      List.of("allg1"), "000-000-0001", null),
              new PersonInfoDto("FirstNameB", "LastName", null, "40", List.of("med1", "med2"),
                      Collections.emptyList(), "000-000-0002", null)), "1"); 
    when(alertsService.fireAlert(anyString())).thenReturn(fireAlertDto);

    // WHEN
    mockMvc.perform(get("/fire?address=address"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.residents", hasSize(2)))
            .andExpect(jsonPath("$.station", is("1")))
            .andDo(document("GetFireAlert",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("address").description(
                    "Address for which we want to retrieve fire alert informations."
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).fireAlert("address");
  }
  
  @Test
  void getFireAlertNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.fireAlert(anyString())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/fire?address=address"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetFireAlertNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).fireAlert("address");
  }
  
  @Test
  void getFireAlertInvalidTest() throws Exception {
    // GIVEN
    FireAlertDto fireAlertDto = new FireAlertDto(Collections.emptyList(), null); 
    when(alertsService.fireAlert(anyString())).thenReturn(fireAlertDto);

    // WHEN
    mockMvc.perform(get("/childAlert?address= "))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Address is mandatory")));
    verify(alertsService, times(0)).fireAlert(anyString());
  }
  
  @Test
  void getFloodAlertTest() throws Exception {
    // GIVEN
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto("address", List.of(
              new PersonInfoDto("FirstNameA", "LastName", null, "10", Collections.emptyList(),
                      List.of("allg1"), "000-000-0001", null),
              new PersonInfoDto("FirstNameB", "LastName", null, "40", List.of("med1", "med2"),
                      Collections.emptyList(), "000-000-0002", null))); 
    FloodHouseholdDto floodHousehold2Dto = new FloodHouseholdDto("address2", List.of(
              new PersonInfoDto("FirstNameC", "LastName", null, "30", List.of("med1"),
                      List.of("allg1"), "000-000-0002", null))); 
    
    when(alertsService.floodAlert(anyList()))
            .thenReturn(List.of(floodHouseholdDto, floodHousehold2Dto));

    // WHEN
    mockMvc.perform(get("/flood/stations?stations=1,2"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].address", is("address")))
            .andExpect(jsonPath("$[0].residents", hasSize(2)))
            .andExpect(jsonPath("$[1].address", is("address2")))
            .andExpect(jsonPath("$[1].residents", hasSize(1)))
            .andDo(document("GetFloodAlert",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("stations").description(
                    "List of stations for which we want to retrieve flood alert informations."
                    + "This parameter *must not be empty*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).floodAlert(List.of(1, 2));
  }
  
  @Test
  void getFloodAlertNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.floodAlert(anyList())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/flood/stations?stations=9,10"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetFloodAlertNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).floodAlert(List.of(9, 10));
  }
  
  @Test
  void getFloodAlertInvalidTest() throws Exception {
    // GIVEN
    FloodHouseholdDto floodHouseholdDto = new FloodHouseholdDto(null, Collections.emptyList()); 
    when(alertsService.floodAlert(anyList())).thenReturn(List.of(floodHouseholdDto));

    // WHEN
    mockMvc.perform(get("/flood/stations?stations="))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Stations list is mandatory")));
    verify(alertsService, times(0)).floodAlert(anyList());
  }
  
  @Test
  void getFireStationTest() throws Exception {
    // GIVEN
    FireStationCoverageDto fireStationCoverageDto = new FireStationCoverageDto(List.of(
              new PersonInfoDto("FirstNameA", "LastName", null, "10", Collections.emptyList(),
                      List.of("allg1"), "000-000-0001", null),
              new PersonInfoDto("FirstNameB", "LastName", null, "40", List.of("med1", "med2"),
                      Collections.emptyList(), "000-000-0002", null)), 1, 1, null);
    
    when(alertsService.fireStationCoverage(anyInt())).thenReturn(fireStationCoverageDto);

    // WHEN
    mockMvc.perform(get("/firestation?stationNumber=1"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.residents", hasSize(2)))
            .andExpect(jsonPath("$.residents[0].firstName", is("FirstNameA")))
            .andExpect(jsonPath("$.residents[0].lastName", is("LastName")))
            .andExpect(jsonPath("$.residents[1].firstName", is("FirstNameB")))
            .andExpect(jsonPath("$.residents[1].lastName", is("LastName")))
            .andExpect(jsonPath("$.childrenCount", is(1)))
            .andExpect(jsonPath("$.adultCount", is(1)))
            .andDo(document("GetFireStationAlert",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("stationNumber").description(
                    "Station Id for which we want to retrieve covered residents informations."
                    + "This parameter *must be greater than 0*.")
                            .optional()
                        )));
    verify(alertsService, times(1)).fireStationCoverage(1);
  }
  
  @Test
  void getFireStationNotFoundTest() throws Exception {
    // GIVEN
    when(alertsService.fireStationCoverage(anyInt())).thenThrow(
            new ResourceNotFoundException(""));

    // WHEN
    mockMvc.perform(get("/firestation?stationNumber=9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$").doesNotExist())
            .andDo(document("GetFireStationAlertNotFound",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(alertsService, times(1)).fireStationCoverage(9);
  }
  
  @Test
  void getFireStationInvalidTest() throws Exception {
    // GIVEN
    FireStationCoverageDto fireStationCoverageDto = new FireStationCoverageDto(
            Collections.emptyList(), 0, 0, null); 
    when(alertsService.fireStationCoverage(anyInt())).thenReturn(fireStationCoverageDto);

    // WHEN
    mockMvc.perform(get("/firestation?stationNumber=0"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Station Id must be greater than 0")));
    verify(alertsService, times(0)).floodAlert(anyList());
  }
  
}

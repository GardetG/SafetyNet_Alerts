package com.safetynet.alerts.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
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

import com.safetynet.alerts.exception.ResourceNotFoundException;
import com.safetynet.alerts.service.AlertsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
            new ResourceNotFoundException("No residents for city found"));

    // WHEN
    mockMvc.perform(get("/communityEmail?city=city"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("No residents for city found")))
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
            .andDo(document("GetCommunityEmail",
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
            new ResourceNotFoundException("No residents for station 9 found"));

    // WHEN
    mockMvc.perform(get("/phoneAlert?firestation=9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("No residents for station 9 found")))
            .andDo(document("GetCommunityEmailNotFound",
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
}

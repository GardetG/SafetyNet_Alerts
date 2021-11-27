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
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.service.FireStationService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest(FireStationController.class)
@AutoConfigureRestDocs
class FireStationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private FireStationService fireStationService;

  private FireStation fireStationTest;
  private FireStation fireStationTest2;
  private FireStation fireStationTest3;

  @BeforeEach
  void setUp() throws Exception {
    fireStationTest = new FireStation(1, "address");
    fireStationTest2 = new FireStation(1, "address2");
    fireStationTest3 = new FireStation(2, "address3");
  }

  @Test
  void getAllFireStationsTest() throws Exception {
    // GIVEN
    when(fireStationService.getAll()).thenReturn(List.of(
            fireStationTest, fireStationTest2, fireStationTest3));

    // WHEN
    mockMvc.perform(get("/fireStations"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].station", is(1)))
            .andExpect(jsonPath("$[1].station", is(1)))
            .andExpect(jsonPath("$[2].station", is(2)))
            .andDo(document("getAllFireStation",
                    preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).getAll();
  }

  @Test
  void getAllFireStationsWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getAll()).thenReturn(Collections.emptyList());

    // WHEN
    mockMvc.perform(get("/fireStations"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    verify(fireStationService, times(1)).getAll();
  }
   
  @Test
  void getFireStationByStationTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            fireStationTest, fireStationTest2));

    // WHEN
    mockMvc.perform(get("/fireStations/1"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].address", is("address")))
            .andExpect(jsonPath("$[1].address", is("address2")))
            .andDo(document("getFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("id").description(
                    "Station Id of the fireStation mapping to retrieve. "
                    + "This parameter *must be greater than 0*.")
                            .optional()
                        )));
    verify(fireStationService, times(1)).getByStation(1);
  }
  
  @Test
  void getFireStationWithInvalidArgumentsTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenReturn(List.of(
            fireStationTest, fireStationTest2));

    // WHEN
    mockMvc.perform(get("/fireStations/0"))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Station Id must be greater than 0")))
            .andDo(document("getInvalidFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(0)).getByStation(1);
  }

  @Test
  void getFireStationWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getByStation(anyInt())).thenThrow(
            new ResourceNotFoundException("Station 9 mapping not found"));

    // WHEN
    mockMvc.perform(get("/fireStations/9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Station 9 mapping not found")))
            .andDo(document("getNotFoundFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).getByStation(9);
  }
  
  @Test
  void getFireStationByAddressTest() throws Exception {
    // GIVEN
    when(fireStationService.getByAddress(anyString())).thenReturn(fireStationTest);

    // WHEN
    mockMvc.perform(get("/fireStations/fireStation?address=address"))

            // THEN
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.address", is("address")))
            .andDo(document("getFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint()),
                    requestParameters(
                            parameterWithName("address").description(
                    "Address of the fireStation mapping to retrieve. "
                    + "This parameter *must not be blank*.")
                            .optional()
                        )));
    verify(fireStationService, times(1)).getByAddress("address");
  }
  
  @Test
  void getFireStationByAddressWithInvalidArgumentsTest() throws Exception {
    // GIVEN
    when(fireStationService.getByAddress(anyString())).thenReturn(fireStationTest);

    // WHEN
    mockMvc.perform(get("/fireStations/fireStation?address="))

            // THEN
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$[0]", is("Address is mandatory")))
            .andDo(document("getInvalidFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(0)).getByAddress(anyString());
  }

  @Test
  void getFireStationByAddressWhenNotFoundTest() throws Exception {
    // GIVEN
    when(fireStationService.getByAddress(anyString())).thenThrow(
            new ResourceNotFoundException("Address9 mapping not found"));

    // WHEN
    mockMvc.perform(get("/fireStations/fireStation?address=address9"))

            // THEN
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$", is("Address9 mapping not found")))
            .andDo(document("getNotFoundFireStation",
                    preprocessRequest(prettyPrint()), 
                    preprocessResponse(prettyPrint())));
    verify(fireStationService, times(1)).getByAddress("address9");
  }
  
}

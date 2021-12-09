package com.safetynet.alerts.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.configuration.JsonUrlProperty;
import com.safetynet.alerts.model.FireStation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.LoadableRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Class Implementation handling the retrieve of data from a json file.
 */
@Profile("!UnitTests")
@Component
public class JsonLoader implements DataLoader, CommandLineRunner {

  @Autowired
  private JsonUrlProperty property;

  @Autowired
  LoadableRepository<Person> personRepository;
  
  @Autowired
  LoadableRepository<MedicalRecord> medicalRecordRepository;
  
  @Autowired
  LoadableRepository<FireStation> fireStationRepository;

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonLoader.class);

  /**
   * Call at application start the loading of data from the JSON file indicate in 
   * properties.
   */
  @Override
  public void run(String... args) throws Exception {

    load(property.getJsonUrl());

  }

  /**
   * Load and deserialize the data from the json file locate by the given url.
   */
  @Override
  public void load(String url) {
    LOGGER.info("Attempt to load : {}", url);
    try (InputStream inputStream = JsonLoader.class.getResourceAsStream(url)) {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode sourceNode = mapper.readTree(inputStream);

      personRepository.setupRepository(
              loadRessources(sourceNode, "persons", 
                      new TypeReference<List<Person>>() {}));
      medicalRecordRepository.setupRepository(
              loadRessources(sourceNode, "medicalrecords", 
                      new TypeReference<List<MedicalRecord>>() {}));
      fireStationRepository.setupRepository(
              loadRessources(sourceNode, "firestations", 
                      new TypeReference<List<FireStation>>() {}));

    } catch (IOException e) {
      LOGGER.error("Error while loading : {}", url);
    }
  }

  /**
   * Parse the data under the child node key from the given main node and deserialize
   * into a list according to the give TypeReference.
   * 

   * @param <T> Type of the deserialized resource
   * @param node source node of the data
   * @param key child node to parse
   * @param type for deserializing
   * @return List of resource deserialized
   */
  private <T> List<T> loadRessources(JsonNode node, String key, TypeReference<List<T>> type) {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    JsonNode ressourceNode = node.get(key);
    List<T> ressourcesList = mapper.convertValue(ressourceNode, type);
    LOGGER.info("{} loaded", key);
    return ressourcesList;
  }

}

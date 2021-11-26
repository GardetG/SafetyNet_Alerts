package com.safetynet.alerts.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.safetynet.alerts.configuration.JsonUrlProperty;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonLoader.class);

  @Override
  public void run(String... args) throws Exception {

    load(property.getJsonUrl());

  }

  @Override
  public void load(String url) {
    LOGGER.info("Attempt to load : {}", url);
    try (InputStream inputStream = JsonLoader.class.getResourceAsStream(url)) {

      ObjectMapper mapper = new ObjectMapper();
      JsonNode sourceNode = mapper.readTree(inputStream);

      personRepository.setupRepository(
              loadRessources(sourceNode, "persons", new TypeReference<List<Person>>() {}));
      medicalRecordRepository.setupRepository(
              loadRessources(sourceNode, "medicalrecords", new TypeReference<List<MedicalRecord>>() {}));

    } catch (IOException e) {
      LOGGER.error("Error while loading : {}", url);
    }
  }

  private <T> List<T> loadRessources(JsonNode node, String key, TypeReference<List<T>> type) {
    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    JsonNode ressourceNode = node.get(key);
    List<T> ressourcesList = mapper.convertValue(ressourceNode, type);
    LOGGER.info("{} loaded", key);
    return ressourcesList;
  }

}

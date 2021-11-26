package com.safetynet.alerts.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Property Class to define the json data source url.
 */
@Configuration
@ConfigurationProperties(prefix = "com.safetynet.alerts")
public class JsonUrlProperty {

  @Getter @Setter
  private String jsonUrl;

}
package com.safetynet.alerts.util;

import java.util.List;

/**
 * Enum of the different type of PersonInfoDto and the field to map for each.
 */
public enum DtoType {
  NAME(List.of("Name")),
  AGE(List.of("Name", "Age")),
  STATIONCOVERAGE(List.of("Name", "Address", "Phone")),
  ALERT(List.of("Name", "Phone", "Age", "Medical")),
  PERSONINFO(List.of("Name", "Address", "Email", "Age", "Medical"));
  
  private List<String> fields;
  
  private DtoType(List<String> fields) {
    this.fields = fields;
  }
  
  public List<String> getFields() {
    return this.fields;
  }
  
}
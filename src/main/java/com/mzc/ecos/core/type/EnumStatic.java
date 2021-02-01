package com.mzc.ecos.core.type;

public class EnumStatic {

  public static enum Elasticsearch {
    TOPOLOGY_ALLINONE("ALLINONE"),
    TOPOLOGY_MASTER("MASTER"),
    TOPOLOGY_DATA("DATA"),
    TOPOLOGY_COORDINATOR("COORDINATOR"),
    TOPOLOGY_INGEST("INGEST"),
    TOPOLOGY_TRANSFORM("TRANSFORM");

    final private String name;

    public String getName() {
      return name;
    }

    private Elasticsearch(String name) {
      this.name = name;

    }
  }
}
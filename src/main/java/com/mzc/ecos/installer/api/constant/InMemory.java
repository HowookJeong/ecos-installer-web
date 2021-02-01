package com.mzc.ecos.installer.api.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class InMemory {
    public static HashSet<String> TERRAFORM_PROJECTS = new HashSet<>();

    public static Map<String, String> getElasticsearchNodeType() {
        Map<String, String> nodeType = new HashMap<>();

        nodeType.put("allinone.master", "true");
        nodeType.put("allinone.data", "true");

        nodeType.put("master.master", "true");
        nodeType.put("master.data", "false");

        nodeType.put("data.master", "false");
        nodeType.put("data.data", "true");

        nodeType.put("coordinator.master", "false");
        nodeType.put("coordinator.data", "false");

        return nodeType;
    }

}

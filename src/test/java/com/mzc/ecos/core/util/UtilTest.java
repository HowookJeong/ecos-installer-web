package com.mzc.ecos.core.util;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

@Log4j2
public class UtilTest {
  @Test
  public void testListToSting() {
    String privateIps = "";
    List<String> ips = new ArrayList<>();
    ips.add("1.1.1.1");
    ips.add("2.2.2.2");
    ips.add("3.3.3.3");
    privateIps = String.join(System.getProperty("line.separator"), ips);

    log.debug("{}", privateIps);
  }
}

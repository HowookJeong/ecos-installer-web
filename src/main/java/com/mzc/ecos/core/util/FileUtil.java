package com.mzc.ecos.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Log4j2
public class FileUtil {

  public static void writeTemplateFile(String file, String content) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(file));
      writer.write(content);
      writer.close();
    } catch (Exception e) {
      log.error("{}", e.getMessage());
    }
  }

  public static String readResourceFile(String resourceFile) {
    StringBuilder builder = new StringBuilder();
    Resource resource = new ClassPathResource(resourceFile);

    synchronized (builder) {
      try {
        try (
          BufferedReader br = new BufferedReader(
            new InputStreamReader(
              resource.getInputStream(),
              Charset.forName(StandardCharsets.UTF_8.name())
            )
          )
        ) {
          String line = "";

          while ((line = br.readLine()) != null) {
            builder.append(line).append('\n');
          }
        }
      } catch (IOException e) {
        log.error("", e);
      }
    }

    return builder.toString();
  }

  public static String readClassPathResourceFile(String resourceFile) {
    StringBuilder builder = new StringBuilder();
    ClassPathResource resource = new ClassPathResource(resourceFile);

    synchronized (builder) {
      try {
        try (
          Reader reader = new BufferedReader(
            new InputStreamReader(
              resource.getInputStream(),
              Charset.forName(StandardCharsets.UTF_8.name())
            )
          )
        ) {
          int c = 0;

          while ((c = reader.read()) != -1) {
            builder.append((char) c);
          }
        }
      } catch (IOException e) {
        log.error("", e);
      }
    }

    return builder.toString();
  }

  public static void makeDirectory(String dir) {
    makeDirectory(dir, true);
  }

  public static void makeDirectory(String dir, boolean isDelete) {
    File file;

    file = new File(dir);

    if ( file.isDirectory() && isDelete ) {
      file.delete();
    }

    file.mkdirs();
  }
}

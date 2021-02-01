package com.mzc.ecos.core.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class StringUtil {

  public static boolean isEmpty(String str) {
    if (str == null || str.isEmpty()) {
      return true;
    }
    return false;
  }

  public static String getMD5(String str) {
    String result;
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");

      md5.update(str.getBytes(Charset.forName("UTF-8")), 0, str.length());
      BigInteger n = new BigInteger(1, md5.digest());
      result = n.toString(16);
    } catch (NoSuchAlgorithmException e) {
      log.warn("Algorithm fail - {}", e);
      result = null;
    }
    return result;
  }

  public static String sha256(String str) {
    String result;
    try {
      MessageDigest sha256 = MessageDigest.getInstance("SHA-256");

      sha256.update(str.getBytes(Charset.forName("UTF-8")), 0, str.length());
      BigInteger n = new BigInteger(1, sha256.digest());
      result = n.toString(16);
    } catch (NoSuchAlgorithmException e) {
      log.warn("Algorithm fail - {}", e);
      result = null;
    }
    return result;
  }

}

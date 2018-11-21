package utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.util.encoders.Hex;

public final class Hashing {
  public String salt = "Capper";

  // TODO: You should add a salt and make this secure (FIXED)
  public static String md5(String rawString) {
    try {

      // We load the hashing algoritm we wish to use.
      MessageDigest md = MessageDigest.getInstance("MD5");
      // Henter parameteret rawString og tilføjer grgioujgjio til password. Vi tilføjer altså noget salt til vores password.
      // MD5 er en kendt xxxxx og den kan dekrypteres lettere end min SHA-256.
      rawString = rawString + Config.getSaltKey();

      // We convert to byte array
      byte[] byteArray = md.digest(rawString.getBytes());

      // Initialize a string buffer
      StringBuffer sb = new StringBuffer();

      // Run through byteArray one element at a time and append the value to our stringBuffer
      for (int i = 0; i < byteArray.length; ++i) {
        sb.append(Integer.toHexString((byteArray[i] & 0xFF) | 0x100).substring(1, 3));
      }

      //Convert back to a single string and return
      return sb.toString();

    } catch (java.security.NoSuchAlgorithmException e) {

      //If somethings breaks
      System.out.println("Could not hash string");
    }

    return null;
  }

  // TODO: You should add a salt and make this secure (FIXED)
  public static String sha(String rawString) {
    try {
      // We load the hashing algoritm we wish to use.
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      // Henter parameteret rawString og tilføjer grgioujgjio til password. Vi tilføjer altså noget salt til vores password.
      // SHA-256 går langsommere end ovenstående, men den er svære at bryde ned og derfor mere sikker.
      rawString = rawString + Config.getSaltKey();

      // We convert to byte array
      byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));

      // We create the hashed string
      String sha256hex = new String(Hex.encode(hash));

      // And return the string
      return sha256hex;

    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return rawString;
  }

  public String hashWithSalt (String str) {
    String salt = str + this.salt;
            return sha(salt);
  }
}
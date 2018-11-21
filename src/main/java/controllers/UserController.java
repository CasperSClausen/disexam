package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;
import utils.Hashing;
import utils.Log;

import javax.sound.midi.Soundbank;

public class UserController {

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Check for connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build the query for DB
    String sql = "SELECT * FROM user where id=" + id;

    // Actually do the query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"));

        // return the create object
        return user;
      } else {
        System.out.println("Kan ikke finde bruger");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }


//Forstå den her
  // Laver en string med loginUser, og bruger user som parameter.
    public static String loginUser(User user) {

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // laver et SQL kald, hvor vi først tjekker for om emailen er korrekt og herefter om password er korrekt.
        // 'afgrænser email, altså bestemmer hvor langt den læser
        String sql = "SELECT * FROM user where email='" + user.getEmail() + "'AND password ='" + user.getPassword() + "'";

        dbCon.insert(sql);

        // Actually do the query
        ResultSet resultSet = dbCon.query(sql);

        // Deklerere den uden nogen værdi.
        User userlogin;
        String token;

        try {
            // Get first object, since we only have one
            if (resultSet.next()) {
                userlogin = new User (
                                resultSet.getInt("id"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getString("password"),
                                resultSet.getString("email"));

                if (userlogin != null) {
                    try {
                        Algorithm algorithm = Algorithm.HMAC256("secret");
                        token = JWT.create()
                                .withClaim("userid", userlogin.getId())
                                .withIssuer("cbsexam")
                                .sign(algorithm);
                        return token;
                    } catch (JWTCreationException exception) {
                        //Invalid Signing configuration / Couldn't convert Claims.
                        System.out.println(exception.getMessage());
                        return "";
                    }
                    }
            } else {
                System.out.println("Kan ikke finde bruger");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "";
        }

        // Return null
        return "";

    }

   // Get all users in database
   // @return
  public static ArrayList<User> getUsers() {

    // Check for DB connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Build SQL
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
            new User(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("password"),
                rs.getString("email"));

        // Add element to list
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return the list of users
    return users;
  }

  public static User createUser(User user) {

    // Write in log that we've reach this step
    Log.writeLog(UserController.class.getName(), user, "Actually creating a user in DB", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Check for DB Connection
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Insert the user in the DB
    // TODO: Hash the user password before saving it. (FIXED)
    int userID = dbCon.insert(
        "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
            + user.getFirstname()
            + "', '"
            + user.getLastname()
            + "', '"
                //Der henvises til Hashing klassen, og henter instansen vi har kaldt sha.
            + Hashing.sha(user.getPassword())
            + "', '"
            + user.getEmail()
            + "', "
            + user.getCreatedTime()
            + ")");

    if (userID != 0) {
      //Update the userid of the user before returning
      user.setId(userID);
    } else{
      // Return null if user has not been inserted into database
      return null;
    }

    // Return user
    return user;
  }

    public static boolean deleteUser(String token) {

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }
    //Laver det om til noget man forstår
        DecodedJWT jwt = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("cbsexam")
                    .build(); //Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            //Invalid signature/claims
        }

        String sql = "DELETE FROM user WHERE id = " + jwt.getClaim("userid").asInt();

        return dbCon.insert(sql) == 1;
    }

    public static boolean updateUser(User user, String token) {

        // Check for DB Connection
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Kan tjekke decoded her --> https://jwt.io
        // JSON Web Token (JWT) is a compact URL-safe means of representing claims to be transferred between two parties.
        DecodedJWT jwt = null;

        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("cbsexam")
                    .build(); //Reusable verifier instance
            jwt = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            System.out.println(exception.getMessage());
        }

        String sql =
                "UPDATE user SET first_name = '" + user.getFirstname() + "', last_name ='" + user.getLastname() + "', password = '" + Hashing.sha(user.getPassword()) + "', email ='" + user.getEmail()
                        + "' WHERE id = " + jwt.getClaim("userid").asInt();

// Return user/token
        return dbCon.insert(sql) == 1;
}}
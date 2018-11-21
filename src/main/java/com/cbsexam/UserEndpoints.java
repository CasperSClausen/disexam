package com.cbsexam;

import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;

@Path("user")
public class UserEndpoints {

   // @param idUser
   // @return Responses
  @GET
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Use the ID to get the user from the controller.
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON (FIXED)

    // Convert the user object to json in order to return the object
    String json = new Gson().toJson(user);

    // json bruges til dataudvikling, og gør at vi mennesker kan læse det. Derfor json =.
    // Encryption er klassen hvor kryptering finder sted
    // XOR laver værdierne om til binære tal.
    json = Encryption.encryptDecryptXOR(json);

    // TODO: What should happen if something breaks down? (FIXED)

    if (user != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      // Return a response with status 400
      return Response.status(400).entity("Kan ikke finde bruger").build();
    }
  }


  // @return Responses
  @GET
  @Path("/")
  public Response getUsers() {

    // Write to log that we are here
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON (FIXED)

    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    // json bruges til dataudvikling, og gør at vi mennesker kan læse det. Derfor json =.
    // Encryption er klassen hvor kryptering finder sted
    // XOR laver værdierne om til binære tal.
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  // Jeg opretter her et objekt af klassen UserCache, så klassen kan kaldes. Så getUsers nu bliver brugt.
  // Ligger den udenfor ovenstående metode, så den kan benyttes i andre klasser.
  //Static gør den kan hentes en gang
  static UserCache userCache = new UserCache();

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      // Return a response with status 400
      return Response.status(400).entity("Kan ikke oprette bruger").build();
    }
  }


  // TODO: Make the system able to login users and assign them a token to use throughout the system. (FIXED)
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {


    // Read the json from body and transfer it to a user class
    User user = new Gson().fromJson(body, User.class);

    // Get the user back with the added ID and return it to the user
    String token = UserController.loginUser(user);

    /// Return the data to the user
    if (token != "") {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else {
      return Response.status(400).entity("Kan ikke logge ind med den ønskede email og password.").build();
    }
  }


  @DELETE
  @Path("/delete")
// TODO: Make the system able to delete users (FIXED)
  //body er attributter fra User klassen
  public Response deleteUser(String body) {

    User user = new Gson().fromJson(body, User.class);

    // Token, da vi vil sikre os at vi sletter den rigtige bruger
    if (UserController.deleteUser(user.getToken())) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).entity("Bruger er slettet fra systemet").build();
    } else {
      // Return a response with status 400
      return Response.status(400).entity("Brugeren kan ikke findes i systemet").build();
    }

  }
  @POST
  @Path("/update")
  // TODO: Make the system able to update users (FIXED)
  public Response updateUser(String body) {
    User user = new Gson().fromJson(body, User.class);

    if (UserController.updateUser(user, user.getToken())) {
      userCache.getUsers(true);
      return Response.status(200).entity("Brugeren blev opdateret").build();

    }else {
    // Return a response with status 400 and JSON as type
    return Response.status(400).entity("Brugeren findes ikke i systemet").build();
  }
}}

package com.j148.backend.resources;

import com.j148.backend.user.model.User;
import com.j148.backend.user.service.UserService;
import com.j148.backend.user.service.UserServiceImpl;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("user")
public class UserResource {

    private UserService userService = new UserServiceImpl();
    private static final Logger LOG = Logger.getLogger(UserResource.class.getName());

    @GET
    @Path("ping")
    public Response pingUserResource() {
        return Response.ok("Successfully pinged User Resource").build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("/login")
    public Response login(User user) {
        try {
            return Response.ok(this.userService.LogIn(user)).build();
        } catch (IllegalArgumentException e) {
            LOG.log(Level.SEVERE, "the user model passed to the server is invalid", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "unable to process user login", e);
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }
    }
}

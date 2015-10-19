package se.eldebabe.taskboard.web.services;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.data.services.UserService;

@Path("login")
@Consumes(MediaType.APPLICATION_JSON)
public final class LoginWebService {

	private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	private static UserService userService;
	
	public final static HashSet<String> tokens = new HashSet<>();

	static {
		context.scan("se.eldebabe.taskboard.data.configs");
		context.refresh();
		userService = context.getBean(UserService.class);
	}
	
	@POST
	public final Response login(final String json) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		final JsonObject jobj = new Gson().fromJson(json, JsonObject.class);

		final String username = jobj.get("username").getAsString();
		final String password = jobj.get("password").getAsString();
		
		final User user = userService.loginUser(username, password);
		
		if(user != null){
			final String token = UUID.randomUUID().toString();
			tokens.add(token);
			return Response.accepted(token).build();
		}else{
			return Response.status(Status.UNAUTHORIZED).build();
		}
			
	}
	
}

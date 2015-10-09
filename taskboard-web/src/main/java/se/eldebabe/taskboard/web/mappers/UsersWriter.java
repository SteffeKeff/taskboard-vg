package se.eldebabe.taskboard.web.mappers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.web.mappers.UserMapper.UserAdapter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UsersWriter implements MessageBodyWriter<ArrayList<User>> {
	private Gson gson;

	public UsersWriter() {
		gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new UsersAdapter()).create();
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> users, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		
		if(users.isAssignableFrom(ArrayList.class) && genericType instanceof ParameterizedType){
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] actualTypesArgue = type.getActualTypeArguments();
			if(actualTypesArgue[0].equals(User.class)){
				return true;
			}
		}
		return false;
	}

	@Override
	public long getSize(ArrayList<User> Users, Class<?> User, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(ArrayList<User> Users, Class<?> User, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(Users, ArrayList.class, writer);
		}
	}

	protected static final class UsersAdapter implements JsonSerializer<ArrayList<User>> {

		@Override
		public JsonElement serialize(ArrayList<User> users, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			
			UserAdapter adapter = new UserAdapter();
			
			final JsonObject jsonToReturn = new JsonObject();
			final JsonArray jsonArrayForUsers = new JsonArray();
			for(User user: users){
				JsonObject json = adapter.serialize(user, typeOfSrc, context).getAsJsonObject();
				jsonArrayForUsers.add(json);
			}
			
			jsonToReturn.add("users", jsonArrayForUsers);
			
			return jsonToReturn;
		}
	}
}
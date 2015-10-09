package se.eldebabe.taskboard.web.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import se.eldebabe.taskboard.data.models.User;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class UserMapper implements MessageBodyReader<User>, MessageBodyWriter<User> {
	private Gson gson;

	public UserMapper() {
		gson = new GsonBuilder().registerTypeAdapter(User.class, new UserAdapter()).create();
	}

	// MessageBodyReader
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(User.class);
	}

	@Override
	public User readFrom(Class<User> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		final User user = gson.fromJson(new InputStreamReader(entityStream), User.class);

		return user;
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(User.class);
	}

	@Override
	public long getSize(User t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(User user, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(user, User.class, writer);
		}
	}

	protected static final class UserAdapter implements JsonDeserializer<User>, JsonSerializer<User> {
		@Override
		public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			final JsonObject userJson = json.getAsJsonObject();
			final String userId = userJson.get("userid").getAsString();
			final String userName = userJson.get("username").getAsString();
			final String firstName = userJson.get("firstname").getAsString();
			final String lastName = userJson.get("lastname").getAsString();

			return new User(userId, userName, firstName, lastName);
		}

		@Override
		public JsonElement serialize(User user, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject userJson = new JsonObject();
			userJson.add("id", new JsonPrimitive(user.getId()));
			userJson.add("userid", new JsonPrimitive(user.getUserId()));
			userJson.add("username", new JsonPrimitive(user.getUserName()));
			userJson.add("firstname", new JsonPrimitive(user.getFirstName()));
			userJson.add("lastname", new JsonPrimitive(user.getLastName()));

			return userJson;
		}

	}

}
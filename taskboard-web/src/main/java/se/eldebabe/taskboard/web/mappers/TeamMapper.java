package se.eldebabe.taskboard.web.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;

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

import se.eldebabe.taskboard.data.models.Team;
import se.eldebabe.taskboard.data.models.User;
import se.eldebabe.taskboard.web.mappers.UsersWriter.UsersAdapter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class TeamMapper implements MessageBodyReader<Team>, MessageBodyWriter<Team> {
	private Gson gson;

	public TeamMapper() {
		gson = new GsonBuilder().registerTypeAdapter(Team.class, new TeamAdapter()).create();
	}

	// MessageBodyReader
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(Team.class);
	}

	@Override
	public Team readFrom(Class<Team> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		final Team Team = gson.fromJson(new InputStreamReader(entityStream), Team.class);

		return Team;
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(Team.class);
	}

	@Override
	public long getSize(Team t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(Team Team, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(Team, Team.class, writer);
		}
	}

	protected static final class TeamAdapter implements JsonDeserializer<Team>, JsonSerializer<Team> {
		@Override
		public Team deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			final JsonObject teamJson = json.getAsJsonObject();
			final String description = teamJson.get("description").getAsString();

			return new Team(description);
		}

		@Override
		public JsonElement serialize(Team team, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject teamJson = new JsonObject();
			
			teamJson.add("id", new JsonPrimitive(team.getId()));
			teamJson.add("description", new JsonPrimitive(team.getName()));
			
			UsersAdapter usersAdapter = new UsersAdapter();
			ArrayList<User> usersList = new ArrayList<>(team.getUsers());
			JsonElement users = usersAdapter.serialize(usersList, typeOfSrc, context);
			
			teamJson.add("users", users);

			return teamJson;
		}

	}

}
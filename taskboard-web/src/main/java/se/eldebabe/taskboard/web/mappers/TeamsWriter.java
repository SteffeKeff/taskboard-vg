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

import se.eldebabe.taskboard.data.models.Team;
import se.eldebabe.taskboard.web.mappers.TeamMapper.TeamAdapter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class TeamsWriter implements MessageBodyWriter<ArrayList<Team>> {
	private Gson gson;

	public TeamsWriter() {
		gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new TeamsAdapter()).create();
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> teams, Type genericType, Annotation[] annotations, MediaType mediaType) {

		if (teams.isAssignableFrom(ArrayList.class) && genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] actualTypesArgue = type.getActualTypeArguments();
			if (actualTypesArgue[0].equals(Team.class)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long getSize(ArrayList<Team> Teams, Class<?> Team, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(ArrayList<Team> Teams, Class<?> Team, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(Teams, ArrayList.class, writer);
		}
	}

	private static final class TeamsAdapter implements JsonSerializer<ArrayList<Team>> {

		@Override
		public JsonElement serialize(ArrayList<Team> teams, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			
			TeamAdapter adapter = new TeamAdapter();

			final JsonObject jsonToReturn = new JsonObject();
			final JsonArray jsonArrayForTeams = new JsonArray();
			for (Team team: teams) {
				JsonObject json = adapter.serialize(team, typeOfSrc, context).getAsJsonObject();
				jsonArrayForTeams.add(json);
			}

			jsonToReturn.add("teams", jsonArrayForTeams);

			return jsonToReturn;
		}
	}
}
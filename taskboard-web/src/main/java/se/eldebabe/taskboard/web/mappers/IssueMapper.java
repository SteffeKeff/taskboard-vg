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

import se.eldebabe.taskboard.data.models.Issue;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class IssueMapper implements MessageBodyReader<Issue>, MessageBodyWriter<Issue> {
	private Gson gson;

	public IssueMapper() {
		gson = new GsonBuilder().registerTypeAdapter(Issue.class, new IssueAdapter()).create();
	}

	// MessageBodyReader
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(Issue.class);
	}

	@Override
	public Issue readFrom(Class<Issue> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		final Issue Issue = gson.fromJson(new InputStreamReader(entityStream), Issue.class);

		return Issue;
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(Issue.class);
	}

	@Override
	public long getSize(Issue t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(Issue Issue, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(Issue, Issue.class, writer);
		}
	}

	protected static final class IssueAdapter implements JsonDeserializer<Issue>, JsonSerializer<Issue> {
		@Override
		public Issue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			final JsonObject issueJson = json.getAsJsonObject();
			final String description = issueJson.get("description").getAsString();

			return new Issue(description);
		}

		@Override
		public JsonElement serialize(Issue Issue, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject IssueJson = new JsonObject();
			
			IssueJson.add("id", new JsonPrimitive(Issue.getId()));
			IssueJson.add("description", new JsonPrimitive(Issue.getDescription()));

			return IssueJson;
		}

	}

}
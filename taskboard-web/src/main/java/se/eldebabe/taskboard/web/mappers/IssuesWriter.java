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

import se.eldebabe.taskboard.data.models.Issue;
import se.eldebabe.taskboard.web.mappers.IssueMapper.IssueAdapter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class IssuesWriter implements MessageBodyWriter<ArrayList<Issue>> {
	private Gson gson;

	public IssuesWriter() {
		gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new IssuesAdapter()).create();
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> issues, Type genericType, Annotation[] annotations, MediaType mediaType) {

		if (issues.isAssignableFrom(ArrayList.class) && genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] actualTypeArgue = type.getActualTypeArguments();
			if (actualTypeArgue[0].equals(Issue.class)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long getSize(ArrayList<Issue> Issues, Class<?> Issue, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(ArrayList<Issue> Issues, Class<?> Issue, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(Issues, ArrayList.class, writer);
		}
	}

	private static final class IssuesAdapter implements JsonSerializer<ArrayList<Issue>> {

		@Override
		public JsonElement serialize(ArrayList<Issue> issues, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {

			IssueAdapter adapter = new IssueAdapter();

			final JsonObject jsonToReturn = new JsonObject();
			final JsonArray jsonArrayForIssues = new JsonArray();
			for (Issue issue : issues) {
				JsonObject json = adapter.serialize(issue, typeOfSrc, context).getAsJsonObject();
				jsonArrayForIssues.add(json);
			}

			jsonToReturn.add("issues", jsonArrayForIssues);

			return jsonToReturn;
		}
	}
}
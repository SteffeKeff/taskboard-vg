package se.eldebabe.taskboard.web.mappers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.joda.time.DateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonWriter;

import se.eldebabe.taskboard.data.models.WorkItem;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WorkItemMapper implements MessageBodyReader<WorkItem>, MessageBodyWriter<WorkItem> {
	private Gson gson;

	public WorkItemMapper() {
		gson = new GsonBuilder().registerTypeAdapter(WorkItem.class, new WorkItemAdapter()).create();
	}

	// MessageBodyReader
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(WorkItem.class);
	}

	@Override
	public WorkItem readFrom(Class<WorkItem> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		final WorkItem WorkItem = gson.fromJson(new InputStreamReader(entityStream), WorkItem.class);

		return WorkItem;
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(WorkItem.class);
	}

	@Override
	public long getSize(WorkItem t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(WorkItem WorkItem, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(WorkItem, WorkItem.class, writer);
		}
	}

	protected static final class WorkItemAdapter implements JsonDeserializer<WorkItem>, JsonSerializer<WorkItem> {
		@Override
		public WorkItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			final JsonObject WorkItemJson = json.getAsJsonObject();
			final String title = WorkItemJson.get("title").getAsString();
			final String description = WorkItemJson.get("description").getAsString();

			return new WorkItem(title, description);
		}

		@Override
		public JsonElement serialize(WorkItem workItem, Type typeOfSrc, JsonSerializationContext context) {
			final JsonObject workItemJson = new JsonObject();
			workItemJson.add("id", new JsonPrimitive(workItem.getId()));
			workItemJson.add("title", new JsonPrimitive(workItem.getTitle()));
			workItemJson.add("description", new JsonPrimitive(workItem.getDescription()));
			workItemJson.add("status", new JsonPrimitive(workItem.getStatus().toString()));

			DateTime workItemDateTime = new DateTime(workItem.getLastModifiedDate());
			Date date = workItemDateTime.toDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			workItemJson.add("last_modified", new JsonPrimitive(sdf.format(date)));

			if (workItem.getIssue() != null) {
				final JsonObject jsonObjectForIssue = new JsonObject();
				jsonObjectForIssue.add("id", new JsonPrimitive(workItem.getIssue().getId()));
				jsonObjectForIssue.add("description", new JsonPrimitive(workItem.getIssue().getDescription()));
				workItemJson.add("issue", jsonObjectForIssue);
			} else {
				workItemJson.add("issue", JsonNull.INSTANCE);
			}

			return workItemJson;
		}

	}

}
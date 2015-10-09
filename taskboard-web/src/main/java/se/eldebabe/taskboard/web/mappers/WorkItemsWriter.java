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

import se.eldebabe.taskboard.data.models.WorkItem;
import se.eldebabe.taskboard.web.mappers.WorkItemMapper.WorkItemAdapter;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WorkItemsWriter implements MessageBodyWriter<ArrayList<WorkItem>> {
	private Gson gson;

	public WorkItemsWriter() {
		gson = new GsonBuilder().registerTypeAdapter(ArrayList.class, new WorkItemsAdapter()).create();
	}

	// MessageBodyWriter
	@Override
	public boolean isWriteable(Class<?> workItems, Type genericType, Annotation[] annotations, MediaType mediaType) {

		if (workItems.isAssignableFrom(ArrayList.class) && genericType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) genericType;
			Type[] actualTypeArgue = type.getActualTypeArguments();
			if (actualTypeArgue[0].equals(WorkItem.class)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public long getSize(ArrayList<WorkItem> workItems, Class<?> workItem, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return 0;
	}

	@Override
	public void writeTo(ArrayList<WorkItem> workItems, Class<?> workItem, java.lang.reflect.Type genericType,
			Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		try (final JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream))) {
			gson.toJson(workItems, ArrayList.class, writer);
		}
	}

	private static final class WorkItemsAdapter implements JsonSerializer<ArrayList<WorkItem>> {

		@Override
		public JsonElement serialize(ArrayList<WorkItem> workItems, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			WorkItemAdapter adapter = new WorkItemAdapter();

			final JsonObject jsonToReturn = new JsonObject();
			final JsonArray jsonArrayForWorkItems = new JsonArray();
			for (WorkItem workItem : workItems) {
				JsonObject json = adapter.serialize(workItem, typeOfSrc, context).getAsJsonObject();
				jsonArrayForWorkItems.add(json);
			}

			jsonToReturn.add("workitems", jsonArrayForWorkItems);

			return jsonToReturn;

		}
	}
}
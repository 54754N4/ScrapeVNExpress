package scrape;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

public class Json {
	private static final Gson GSON = new GsonBuilder()
		// Date parsing
		.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
		.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
		.create();
	
	public static <T> String of(T object) {
		return GSON.toJson(object);
	}
	
	public static <T> T to(String json, Class<T> cls) {
		return GSON.fromJson(json, cls);
	}
	
	public static <T> List<T> toList(String json, Class<T> list) {
		Type type = new TypeToken<Collection<T>>(){}.getType();
		return GSON.fromJson(json, type);
	}
	
	/* Custom local date time serialisation */
	
	public static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
		@Override
		public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
	}
	
	public static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
		@Override
		public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
			return LocalDateTime.parse(json.getAsString());
		}
	}
}

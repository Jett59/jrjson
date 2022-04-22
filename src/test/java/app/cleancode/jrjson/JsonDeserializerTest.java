package app.cleancode.jrjson;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class JsonDeserializerTest {
  @Test
  void deserialize_acceptsValidJsonTest() throws InvalidJsonException {
    String json = """
        {"id":"6d9a0ab9-4474-4c58-98f1-479c942edfc8","age":32,"good":false}
        """;
    JsonDeserializer deserializer = new JsonDeserializer();
    SampleRecord output = null;
    try {
      output = deserializer.deserialize(json, SampleRecord.class);
    } catch (InvalidJsonException e) {
      fail(e);
    }
    assertNotNull(output);
  }
}

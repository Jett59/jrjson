package app.cleancode.jrjson;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class ConstructorParameters {
  private final Map<String, Integer> nameToIndex;
  private final Map<String, Class<?>> nameToType;
  private final Constructor<?> constructor;
  private Object[] parameters;

  public ConstructorParameters(Constructor<?> constructor) {
    parameters = new Object[constructor.getParameterCount()];
    nameToIndex = new HashMap<>();
    nameToType = new HashMap<>();
    this.constructor = constructor;
    for (Parameter parameter : constructor.getParameters()) {
      nameToIndex.put(parameter.getName(), nameToIndex.size());
      nameToType.put(parameter.getName(), parameter.getType());
    }
  }

  public void set(String name, Object value) {
    parameters[nameToIndex.get(name)] = value;
  }

  public Class<?> getType(String name) {
    return nameToType.get(name);
  }

  public Object construct() throws Exception {
    return constructor.newInstance(parameters);
  }
}

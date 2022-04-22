package app.cleancode.jrjson;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class JsonDeserializer {

  /**
   * Get the best (least parameters) constructor
   * 
   * @param type the class to get the constructor from
   * @return the constructor with the fewest parameters
   * @throws NoSuchElementException if there are no constructors
   */
  private ConstructorParameters getBestConstructor(Class<?> type) {
    return Arrays.stream(type.getDeclaredConstructors())
        .min(Comparator.comparingInt(Constructor::getParameterCount))
        .map(ConstructorParameters::new).orElseThrow();
  }

  private static class ParserState {
    ConstructorParameters constructor;
    String currentPropertyName;
    List<Object> children; // For arrays
  };

  @SuppressWarnings("unchecked")
  public <T extends Record> T deserialize(String json, Class<T> type) throws InvalidJsonException {
    if (json == null) {
      throw new NullPointerException("Null json");
    }
    if (type == null) {
      throw new NullPointerException("Null type");
    }
    Stack<ParserState> stateStack = new Stack<>();
    ParserState currentState = new ParserState();
    currentState.constructor = getBestConstructor(type);
    for (int i = 0; i < json.length(); i++) {
      char c = json.charAt(i);
      if (c == '{') {
        continue; // Start of value
      } else if (c == '"') {
        // Name of field
        int nameEndIndex = json.indexOf('"', i + 1);
        currentState.currentPropertyName = json.substring(i + 1, nameEndIndex);
        i = nameEndIndex + 1;
        c = json.charAt(i);
        if (c != ':') {
          throw new InvalidJsonException("Expected ':', but got '%c'".formatted(c));
        }
        do {
          c = json.charAt(++i);
        } while (Character.isWhitespace(c));
        if (Character.isAlphabetic(c)) {
          // boolean
          if (json.startsWith("true", i)) {
            currentState.constructor.set(currentState.currentPropertyName, true);
            i += "true".length();
          } else if (json.startsWith("false", i)) {
            currentState.constructor.set(currentState.currentPropertyName, false);
            i += "false".length();
          } else {
            throw new InvalidJsonException("Invalid value starting with character %c".formatted(c));
          }
        } else if (Character.isDigit(c) || c == '-') {
          String numericalString = ParseUtils.getNumericalString(json.substring(i));
          i += numericalString.length();
          currentState.constructor.set(currentState.currentPropertyName,
              Integer.parseInt(numericalString));
        } else if (c == '"') {
          int valueEndIndex = json.indexOf('"', i + 1);
          currentState.constructor.set(currentState.currentPropertyName,
              json.substring(i + 1, valueEndIndex));
          i = valueEndIndex + 1;
        } else {
          throw new InvalidJsonException("Invalid value with character %c".formatted(c));
        }
        c = json.charAt(i);
        if (c == '}') {
          if (stateStack.isEmpty()) {
            try {
              return (T) currentState.constructor.construct();
            } catch (Exception e) {
              throw new IllegalArgumentException("Could not construct record", e);
            }
          }
        }
      } else {
        throw new InvalidJsonException("Unexpected %c at offset %d".formatted(c, i));
      }
    }
    throw new InvalidJsonException("Unterminated json");
  }

}

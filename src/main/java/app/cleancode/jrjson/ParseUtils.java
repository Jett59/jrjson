package app.cleancode.jrjson;

public class ParseUtils {
  public static String getNumericalString(String s) {
    int i = 0;
    char c = s.charAt(i);
    if (c == '-') {
      c = s.charAt(++i);
    }
    while (Character.isDigit(c)) {
      c = s.charAt(++i);
    }
    return s.substring(0, i);
  }
}

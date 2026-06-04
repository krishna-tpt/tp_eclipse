package zr.org.apache.commons.csv;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class CSVUtils {
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final String[][] EMPTY_DOUBLE_STRING_ARRAY = new String[0][0];

   public static String printLine(String[] values) {
      StringWriter stringWriter = new StringWriter();
      CSVPrinter csvPrinter = new CSVPrinter(stringWriter);

      for(int i = 0; i < values.length; ++i) {
         if (values[i] == null) {
            values[i] = "null";
         } else if (values[i].equals("null")) {
            values[i] = "\"null\"";
         }
      }

      csvPrinter.println(values);
      return stringWriter.toString().trim();
   }

   public static String[][] parse(String s) throws IOException {
      if (s == null) {
         throw new IllegalArgumentException("Null argument not allowed.");
      } else {
         String[][] result = (new CSVParser(new StringReader(s))).getAllValues();
         if (result == null) {
            result = EMPTY_DOUBLE_STRING_ARRAY;
         }

         return result;
      }
   }

   public static String[] parseLine(String s) throws IOException {
      if (s == null) {
         throw new IllegalArgumentException("Null argument not allowed.");
      } else {
         return s.length() == 0 ? EMPTY_STRING_ARRAY : (new CSVParser(new StringReader(s))).getLine();
      }
   }
}

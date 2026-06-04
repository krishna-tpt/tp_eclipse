package zr.org.apache.commons.csv;

import java.io.Serializable;

public class CSVStrategy implements Cloneable, Serializable {
   private char delimiter;
   private char encapsulator;
   private char commentStart;
   private boolean ignoreLeadingWhitespaces;
   private boolean interpretUnicodeEscapes;
   private boolean ignoreEmptyLines;
   public static char COMMENTS_DISABLED = 0;
   public static CSVStrategy DEFAULT_STRATEGY;
   public static CSVStrategy EXCEL_STRATEGY;
   public static CSVStrategy TDF_STRATEGY;

   public CSVStrategy(char delimiter, char encapsulator, char commentStart) {
      this(delimiter, encapsulator, commentStart, true, false, true);
   }

   public CSVStrategy(char delimiter, char encapsulator, char commentStart, boolean ignoreLeadingWhitespace, boolean interpretUnicodeEscapes, boolean ignoreEmptyLines) {
      this.setDelimiter(delimiter);
      this.setEncapsulator(encapsulator);
      this.setCommentStart(commentStart);
      this.setIgnoreLeadingWhitespaces(ignoreLeadingWhitespace);
      this.setUnicodeEscapeInterpretation(interpretUnicodeEscapes);
      this.setIgnoreEmptyLines(ignoreEmptyLines);
   }

   public void setDelimiter(char delimiter) {
      this.delimiter = delimiter;
   }

   public char getDelimiter() {
      return this.delimiter;
   }

   public void setEncapsulator(char encapsulator) {
      this.encapsulator = encapsulator;
   }

   public char getEncapsulator() {
      return this.encapsulator;
   }

   public void setCommentStart(char commentStart) {
      this.commentStart = commentStart;
   }

   public char getCommentStart() {
      return this.commentStart;
   }

   public boolean isCommentingDisabled() {
      return this.commentStart == COMMENTS_DISABLED;
   }

   public void setIgnoreLeadingWhitespaces(boolean ignoreLeadingWhitespaces) {
      this.ignoreLeadingWhitespaces = ignoreLeadingWhitespaces;
   }

   public boolean getIgnoreLeadingWhitespaces() {
      return this.ignoreLeadingWhitespaces;
   }

   public void setUnicodeEscapeInterpretation(boolean interpretUnicodeEscapes) {
      this.interpretUnicodeEscapes = interpretUnicodeEscapes;
   }

   public boolean getUnicodeEscapeInterpretation() {
      return this.interpretUnicodeEscapes;
   }

   public void setIgnoreEmptyLines(boolean ignoreEmptyLines) {
      this.ignoreEmptyLines = ignoreEmptyLines;
   }

   public boolean getIgnoreEmptyLines() {
      return this.ignoreEmptyLines;
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException(var2);
      }
   }

   static {
      DEFAULT_STRATEGY = new CSVStrategy(',', '"', COMMENTS_DISABLED, true, false, true);
      EXCEL_STRATEGY = new CSVStrategy(',', '"', COMMENTS_DISABLED, false, false, false);
      TDF_STRATEGY = new CSVStrategy('\t', '"', COMMENTS_DISABLED, true, false, true);
   }
}

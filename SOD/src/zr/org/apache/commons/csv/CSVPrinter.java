package zr.org.apache.commons.csv;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

public class CSVPrinter {
   protected PrintWriter out;
   protected boolean newLine = true;
   protected String newLineChar = "\r\n";
   private CSVStrategy strategy;

   public void setNewLine(String newLineChar) {
      if (newLineChar != null) {
         this.newLineChar = newLineChar;
      }

   }

   public CSVPrinter(OutputStream out) {
      this.strategy = CSVStrategy.DEFAULT_STRATEGY;
      this.out = new PrintWriter(out);
   }

   public CSVPrinter(Writer out) {
      this.strategy = CSVStrategy.DEFAULT_STRATEGY;
      if (out instanceof PrintWriter) {
         this.out = (PrintWriter)out;
      } else {
         this.out = new PrintWriter(out);
      }

   }

   public CSVPrinter setStrategy(CSVStrategy strategy) {
      this.strategy = strategy;
      return this;
   }

   public CSVStrategy getStrategy() {
      return this.strategy;
   }

   public void println(String value) {
      this.print(value);
      this.out.print(this.newLineChar);
      this.out.flush();
      this.newLine = true;
   }

   public void println() {
      this.out.print(this.newLineChar);
      this.out.flush();
      this.newLine = true;
   }

   public void println(String[] values) {
      for(int i = 0; i < values.length; ++i) {
         this.print(values[i]);
      }

      this.out.print(this.newLineChar);
      this.out.flush();
      this.newLine = true;
   }

   public void println(String[][] values) {
      for(int i = 0; i < values.length; ++i) {
         this.println(values[i]);
      }

      if (values.length == 0) {
         this.out.print(this.newLineChar);
      }

      this.out.flush();
      this.newLine = true;
   }

   public void printlnComment(String comment) {
      if (!this.strategy.isCommentingDisabled()) {
         if (!this.newLine) {
            this.out.print(this.newLineChar);
         }

         this.out.print(this.strategy.getCommentStart());
         this.out.print(' ');

         for(int i = 0; i < comment.length(); ++i) {
            char c = comment.charAt(i);
            switch(c) {
            case '\r':
               if (i + 1 < comment.length() && comment.charAt(i + 1) == '\n') {
                  ++i;
               }
            case '\n':
               this.out.print(this.newLineChar);
               this.out.print(this.strategy.getCommentStart());
               this.out.print(' ');
               break;
            default:
               this.out.print(c);
            }
         }

         this.out.print(this.newLineChar);
         this.out.flush();
         this.newLine = true;
      }
   }

   public void print(String value) {
      boolean quote = false;
      if (value.length() > 0) {
         char c = value.charAt(0);
         if (this.newLine && (c < '0' || c > '9' && c < 'A' || c > 'Z' && c < 'a' || c > 'z')) {
            quote = true;
         }

         if (c == ' ' || c == '\f' || c == '\t') {
            quote = true;
         }

         for(int i = 0; i < value.length(); ++i) {
            c = value.charAt(i);
            if (c == '"' || c == this.strategy.getDelimiter() || c == '\n' || c == '\r') {
               quote = true;
               c = value.charAt(value.length() - 1);
               break;
            }
         }

         if (c == ' ' || c == '\f' || c == '\t') {
            quote = true;
         }
      } else if (this.newLine) {
         quote = true;
      }

      if (this.newLine) {
         this.newLine = false;
      } else {
         this.out.print(this.strategy.getDelimiter());
      }

      if (quote) {
         this.out.print(escapeAndQuote(value, this.strategy.getEncapsulator()));
      } else {
         this.out.print(value);
      }

      this.out.flush();
   }

   private static String escapeAndQuote(String value, char encapsulator) {
      int count = 2;
      int i = 0;

      while(i < value.length()) {
         switch(value.charAt(i)) {
         case '\n':
         case '\r':
         case '"':
         case '\\':
            ++count;
         default:
            ++i;
         }
      }

      StringBuffer sb = new StringBuffer(value.length() + count);
      sb.append(encapsulator);

      for(int i1 = 0; i1 < value.length(); ++i1) {
         char c = value.charAt(i1);
         switch(c) {
         case '"':
            sb.append("\"\"");
            break;
         default:
            sb.append(c);
         }
      }

      sb.append(encapsulator);
      return sb.toString();
   }
}

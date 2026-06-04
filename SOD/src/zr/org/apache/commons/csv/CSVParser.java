package zr.org.apache.commons.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class CSVParser {
   private static final int INITIAL_TOKEN_LENGTH = 50;
   protected static final int TT_INVALID = -1;
   protected static final int TT_TOKEN = 0;
   protected static final int TT_EOF = 1;
   protected static final int TT_EORECORD = 2;
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private final ExtendedBufferedReader in;
   private CSVStrategy strategy;
   private static boolean isEndOfStream = false;
   private final ArrayList record;
   private final CSVParser.Token reusableToken;
   private final CharBuffer wsBuf;
   private final CharBuffer code;

   public static boolean isEOF() {
      return isEndOfStream;
   }

   /** @deprecated */
   public CSVParser(InputStream input) {
      this((Reader)(new InputStreamReader(input)));
   }

   public CSVParser(Reader input) {
      this(input, ',');
   }

   /** @deprecated */
   public CSVParser(Reader input, char delimiter) {
      this(input, delimiter, '"', '\u0000');
   }

   /** @deprecated */
   public CSVParser(Reader input, char delimiter, char encapsulator, char commentStart) {
      this(input, new CSVStrategy(delimiter, encapsulator, commentStart));
   }

   public CSVParser(Reader input, CSVStrategy strategy) {
      this.record = new ArrayList();
      this.reusableToken = new CSVParser.Token();
      this.wsBuf = new CharBuffer();
      this.code = new CharBuffer(4);
      this.in = new ExtendedBufferedReader(input);
      this.strategy = strategy;
   }

   public String[][] getAllValues() throws IOException {
      ArrayList records = new ArrayList();
      String[][] ret = (String[][])null;

      String[] values;
      while((values = this.getLine()) != null) {
         records.add(values);
      }

      if (records.size() > 0) {
         ret = new String[records.size()][];
         records.toArray(ret);
      }

      return ret;
   }

   public String nextValue() throws IOException {
      CSVParser.Token tkn = this.nextToken();
      String ret = null;
      switch(tkn.type) {
      case -1:
      default:
         isEndOfStream = false;
         throw new IOException("(line " + this.getLineNumber() + ") invalid parse sequence");
      case 0:
      case 2:
         ret = tkn.content.toString();
         break;
      case 1:
         isEndOfStream = true;
         ret = null;
      }

      return ret;
   }

   public String[] getLine() throws IOException {
      String[] ret = EMPTY_STRING_ARRAY;
      this.record.clear();

      do {
         this.reusableToken.reset();
         this.nextToken(this.reusableToken);
         switch(this.reusableToken.type) {
         case -1:
         default:
            isEndOfStream = false;
            throw new IOException("(line " + this.getLineNumber() + ") invalid parse sequence");
         case 0:
            this.record.add(this.reusableToken.content.toString());
            break;
         case 1:
            isEndOfStream = true;
            if (this.reusableToken.isReady) {
               this.record.add(this.reusableToken.content.toString());
            } else {
               ret = null;
            }
            break;
         case 2:
            this.record.add(this.reusableToken.content.toString());
         }
      } while(this.reusableToken.type == 0);

      if (!this.record.isEmpty()) {
         ret = (String[])((String[])this.record.toArray(new String[this.record.size()]));
      }

      return ret;
   }

   public int getLineNumber() {
      return this.in.getLineNumber();
   }

   protected CSVParser.Token nextToken() throws IOException {
      return this.nextToken(new CSVParser.Token());
   }

   protected CSVParser.Token nextToken(CSVParser.Token tkn) throws IOException {
      this.wsBuf.clear();
      int lastChar = this.in.readAgain();
      int c = this.in.read();
      boolean eol = this.isEndOfLine(c);
      if (eol) {
         tkn.content.append(this.wsBuf.toString());
         tkn.type = 2;
         tkn.isReady = true;
         if (this.in.lookAhead() == 10) {
            this.in.read();
         }

         return tkn;
      } else {
         c = this.in.readAgain();
         if (this.strategy.getIgnoreEmptyLines()) {
            while(eol && !this.isEndOfFile(lastChar)) {
               lastChar = c;
               c = this.in.read();
               eol = this.isEndOfLine(c);
               c = this.in.readAgain();
               if (this.isEndOfFile(c)) {
                  tkn.type = 1;
                  return tkn;
               }
            }
         }

         if (!this.isEndOfFile(lastChar) && (lastChar == this.strategy.getDelimiter() || !this.isEndOfFile(c))) {
            label63:
            do {
               while(!tkn.isReady) {
                  while(this.isWhitespace(c) && !eol) {
                     this.wsBuf.append((char)c);
                     c = this.in.read();
                     eol = this.isEndOfLine(c);
                  }

                  if (!this.strategy.isCommentingDisabled() && c == this.strategy.getCommentStart()) {
                     this.in.readLine();
                     tkn = this.nextToken(tkn.reset());
                     continue label63;
                  }

                  if (c == this.strategy.getDelimiter()) {
                     tkn.type = 0;
                     tkn.isReady = true;
                  } else if (eol) {
                     tkn.type = 2;
                     tkn.isReady = true;
                  } else if (c == this.strategy.getEncapsulator()) {
                     this.encapsulatedTokenLexer(tkn, c);
                  } else if (this.isEndOfFile(c)) {
                     tkn.type = 1;
                     isEndOfStream = false;
                     tkn.isReady = true;
                  } else {
                     if (!this.strategy.getIgnoreLeadingWhitespaces()) {
                        tkn.content.append(this.wsBuf);
                     }

                     this.simpleTokenLexer(tkn, c);
                  }
               }

               return tkn;
            } while(tkn.type != 1);

            return tkn;
         } else {
            tkn.type = 1;
            return tkn;
         }
      }
   }

   private CSVParser.Token simpleTokenLexer(CSVParser.Token tkn, int c) throws IOException {
      this.wsBuf.clear();

      while(!tkn.isReady) {
         if (this.isEndOfLine(c)) {
            tkn.type = 2;
            tkn.isReady = true;
         } else if (this.isEndOfFile(c)) {
            tkn.type = 1;
            tkn.isReady = true;
         } else if (c == this.strategy.getDelimiter()) {
            tkn.type = 0;
            tkn.isReady = true;
         } else if (c == 92 && this.strategy.getUnicodeEscapeInterpretation() && this.in.lookAhead() == 117) {
            tkn.content.append((char)this.unicodeEscapeLexer(c));
         } else if (this.isWhitespace(c)) {
            if (tkn.content.length() > 0) {
               this.wsBuf.append((char)c);
            }
         } else {
            if (this.wsBuf.length() > 0) {
               tkn.content.append(this.wsBuf);
               this.wsBuf.clear();
            }

            tkn.content.append((char)c);
         }

         if (!tkn.isReady) {
            c = this.in.read();
         }
      }

      return tkn;
   }

   private CSVParser.Token encapsulatedTokenLexer(CSVParser.Token tkn, int c) throws IOException {
      int startLineNumber = this.getLineNumber();
      c = this.in.read();

      while(!tkn.isReady) {
         if (c == this.strategy.getEncapsulator()) {
            if (this.in.lookAhead() == this.strategy.getEncapsulator()) {
               c = this.in.read();
               tkn.content.append((char)c);
            } else if (this.strategy.getUnicodeEscapeInterpretation() && c == 92 && this.in.lookAhead() == 117) {
               tkn.content.append((char)this.unicodeEscapeLexer(c));
            } else {
               for(; !tkn.isReady; c = this.in.read()) {
                  int n = this.in.lookAhead();
                  if (n == 13) {
                     this.in.read();
                     n = this.in.lookAhead();
                     if (n != 10) {
                        n = 13;
                     }
                  }

                  if (n == this.strategy.getDelimiter()) {
                     tkn.type = 0;
                     tkn.isReady = true;
                  } else if (this.isEndOfLine(n)) {
                     tkn.type = 2;
                     tkn.isReady = true;
                  } else if (this.isEndOfFile(n)) {
                     tkn.type = 1;
                     tkn.isReady = true;
                     break;
                  }
               }
            }
         } else {
            if (this.isEndOfFile(c)) {
               tkn.type = 1;
               tkn.isReady = true;
               break;
            }

            tkn.content.append((char)c);
         }

         if (!tkn.isReady) {
            c = this.in.read();
         }
      }

      return tkn;
   }

   protected int unicodeEscapeLexer(int c) throws IOException {
      int ret = 0;
      c = this.in.read();
      this.code.clear();

      try {
         for(int i = 0; i < 4; ++i) {
            c = this.in.read();
            if (this.isEndOfFile(c) || this.isEndOfLine(c)) {
               throw new NumberFormatException("number too short");
            }

            this.code.append((char)c);
         }

         ret = Integer.parseInt(this.code.toString(), 16);
         return ret;
      } catch (NumberFormatException var4) {
         throw new IOException("(line " + this.getLineNumber() + ") Wrong unicode escape sequence found '" + this.code.toString() + "'" + var4.toString());
      }
   }

   /** @deprecated */
   public CSVParser setStrategy(CSVStrategy strategy) {
      this.strategy = strategy;
      return this;
   }

   public CSVStrategy getStrategy() {
      return this.strategy;
   }

   private boolean isWhitespace(int c) {
      return Character.isWhitespace((char)c) && c != this.strategy.getDelimiter();
   }

   private boolean isEndOfLine(int c) throws IOException {
      if (c == 13) {
         if (this.in.lookAhead() != 10) {
            return true;
         }

         c = this.in.read();
      }

      return c == 10;
   }

   private boolean isEndOfFile(int c) {
      return c == -1;
   }

   static class Token {
      int type = -1;
      CharBuffer content = new CharBuffer(50);
      boolean isReady;

      CSVParser.Token reset() {
         this.content.clear();
         this.type = -1;
         this.isReady = false;
         return this;
      }
   }
}

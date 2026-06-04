package zr.org.apache.commons.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class ExtendedBufferedReader extends BufferedReader {
   public static final int END_OF_STREAM = -1;
   public static final int UNDEFINED = -2;
   private int lookaheadChar = -2;
   private int lastChar = -2;
   private int secondLastChar = -2;
   private int lineCounter = 0;
   private CharBuffer line = new CharBuffer();

   public ExtendedBufferedReader(Reader r) {
      super(r);
   }

   public ExtendedBufferedReader(Reader r, int bufSize) {
      super(r, bufSize);
   }

   public int read() throws IOException {
      if (this.lookaheadChar == -2) {
         this.lookaheadChar = super.read();
      }

      this.secondLastChar = this.lastChar;
      this.lastChar = this.lookaheadChar;
      if (super.ready()) {
         this.lookaheadChar = super.read();
      } else {
         this.lookaheadChar = -2;
      }

      this.incrementLineCounter();
      return this.lastChar;
   }

   public int readAgain() {
      return this.lastChar;
   }

   public int read(char[] buf, int off, int len) throws IOException {
      if (len == 0) {
         return 0;
      } else {
         if (this.lookaheadChar == -2) {
            if (!this.ready()) {
               return -1;
            }

            this.lookaheadChar = super.read();
         }

         if (this.lookaheadChar == -1) {
            return -1;
         } else {
            int cOff;
            for(cOff = off; len > 0 && this.ready(); --len) {
               if (this.lookaheadChar == -1) {
                  return cOff - off;
               }

               buf[cOff++] = (char)this.lookaheadChar;
               this.incrementLineCounter();
               this.secondLastChar = this.lastChar;
               this.lastChar = this.lookaheadChar;
               this.lookaheadChar = super.read();
            }

            return cOff - off;
         }
      }
   }

   public String readUntil(char c) throws IOException {
      if (this.lookaheadChar == -2) {
         this.lookaheadChar = super.read();
      }

      this.line.clear();

      while(this.lookaheadChar != c && this.lookaheadChar != -1) {
         this.line.append((char)this.lookaheadChar);
         this.incrementLineCounter();
         this.secondLastChar = this.lookaheadChar;
         this.lastChar = this.lookaheadChar;
         this.lookaheadChar = super.read();
      }

      return this.line.toString();
   }

   public String readLine() throws IOException {
      if (this.lookaheadChar == -2) {
         this.lookaheadChar = super.read();
      }

      this.line.clear();
      if (this.lookaheadChar == -1) {
         return null;
      } else {
         char laChar = (char)this.lookaheadChar;
         if (laChar != '\n' && laChar != '\r') {
            this.line.append(laChar);
            String restOfLine = super.readLine();
            this.lastChar = this.lookaheadChar;
            this.lookaheadChar = super.read();
            if (restOfLine != null) {
               this.line.append(restOfLine);
            }

            this.incrementLineCounter();
            return this.line.toString();
         } else {
            this.lastChar = this.lookaheadChar;
            this.lookaheadChar = super.read();
            if ((char)this.lookaheadChar == '\n') {
               this.lastChar = this.lookaheadChar;
               this.lookaheadChar = super.read();
            }

            this.incrementLineCounter();
            return this.line.toString();
         }
      }
   }

   public long skip(long n) throws IllegalArgumentException, IOException {
      if (this.lookaheadChar == -2) {
         this.lookaheadChar = super.read();
      }

      if (n < 0L) {
         throw new IllegalArgumentException("negative argument not supported");
      } else if (n != 0L && this.lookaheadChar != -1) {
         long skiped = 0L;
         if (n > 1L) {
            skiped = super.skip(n - 1L);
         }

         this.lookaheadChar = super.read();
         this.lineCounter = Integer.MIN_VALUE;
         return skiped + 1L;
      } else {
         return 0L;
      }
   }

   public long skipUntil(char c) throws IllegalArgumentException, IOException {
      if (this.lookaheadChar == -2) {
         this.lookaheadChar = super.read();
      }

      long counter;
      for(counter = 0L; this.lookaheadChar != c && this.lookaheadChar != -1; ++counter) {
         this.incrementLineCounter();
         this.lookaheadChar = super.read();
      }

      return counter;
   }

   public int lookAhead() throws IOException {
      if (this.lookaheadChar == -2) {
         this.lookaheadChar = super.read();
      }

      return this.lookaheadChar;
   }

   public int getLineNumber() {
      return this.lineCounter > -1 ? this.lineCounter : -1;
   }

   public boolean markSupported() {
      return false;
   }

   private void incrementLineCounter() throws IOException {
      if (this.lastChar == 13 && this.lookAhead() != 10 || this.lastChar == 10) {
         ++this.lineCounter;
      }

   }
}

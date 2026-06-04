package zr.org.apache.commons.csv;

public class CharBuffer {
   private char[] c;
   private int length;

   public CharBuffer() {
      this(32);
   }

   public CharBuffer(int length) {
      if (length == 0) {
         throw new IllegalArgumentException("Can't create an empty CharBuffer");
      } else {
         this.c = new char[length];
      }
   }

   public void clear() {
      this.length = 0;
   }

   public int length() {
      return this.length;
   }

   public int capacity() {
      return this.c.length;
   }

   public void append(CharBuffer cb) {
      if (cb != null) {
         this.provideCapacity(this.length + cb.length);
         System.arraycopy(cb.c, 0, this.c, this.length, cb.length);
         this.length += cb.length;
      }
   }

   public void append(String s) {
      if (s != null) {
         this.append(s.toCharArray());
      }
   }

   public void append(StringBuffer sb) {
      if (sb != null) {
         this.provideCapacity(this.length + sb.length());
         sb.getChars(0, sb.length(), this.c, this.length);
         this.length += sb.length();
      }
   }

   public void append(char[] data) {
      if (data != null) {
         this.provideCapacity(this.length + data.length);
         System.arraycopy(data, 0, this.c, this.length, data.length);
         this.length += data.length;
      }
   }

   public void append(char data) {
      this.provideCapacity(this.length + 1);
      this.c[this.length] = data;
      ++this.length;
   }

   public void shrink() {
      if (this.c.length != this.length) {
         char[] newc = new char[this.length];
         System.arraycopy(this.c, 0, newc, 0, this.length);
         this.c = newc;
      }
   }

   public char[] getCharacters() {
      if (this.c.length == this.length) {
         return this.c;
      } else {
         char[] chars = new char[this.length];
         System.arraycopy(this.c, 0, chars, 0, this.length);
         return chars;
      }
   }

   public StringBuffer toStringBuffer() {
      StringBuffer sb = new StringBuffer(this.length);
      sb.append(this.c, 0, this.length);
      return sb;
   }

   public String toString() {
      return new String(this.c, 0, this.length);
   }

   public void provideCapacity(int capacity) {
      if (this.c.length < capacity) {
         int newCapacity = (this.c.length + 1) * 2;
         if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
         }

         if (newCapacity < capacity) {
            newCapacity = capacity;
         }

         char[] newc = new char[newCapacity];
         System.arraycopy(this.c, 0, newc, 0, this.length);
         this.c = newc;
      }
   }
}

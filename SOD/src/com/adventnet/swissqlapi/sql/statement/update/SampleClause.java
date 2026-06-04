package com.adventnet.swissqlapi.sql.statement.update;

import java.util.ArrayList;

public class SampleClause {
   private String sampleBlock = new String();
   private String sample = new String();
   public ArrayList samplePercentList = new ArrayList();

   public void setSample(String s) {
      this.sample = s;
   }

   public void setBlock(String s) {
      this.sampleBlock = s;
   }

   public void setSamplePercentList(ArrayList list) {
      this.samplePercentList = list;
   }

   public String getSample() {
      return this.sample;
   }

   public String getBlock() {
      return this.sampleBlock;
   }

   public ArrayList getSamplePercentList() {
      return this.samplePercentList;
   }

   public String toString() {
      StringBuffer stringbuffer = new StringBuffer();
      stringbuffer.append(this.sample.toUpperCase());
      stringbuffer.append(this.sampleBlock);
      int i = 0;

      for(int size = this.samplePercentList.size(); i < size; ++i) {
         stringbuffer.append(this.samplePercentList.get(i).toString());
      }

      return stringbuffer.toString();
   }
}

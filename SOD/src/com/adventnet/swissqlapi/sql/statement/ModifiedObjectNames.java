package com.adventnet.swissqlapi.sql.statement;

import java.util.ArrayList;

public class ModifiedObjectNames {
   private ArrayList modifiedTableAttrList = new ArrayList();
   private ArrayList modifiedColAttrList = new ArrayList();
   private ArrayList modifiedConstrAttrList = new ArrayList();

   public void addModifiedObjectName(ModifiedObjectAttr modifiedTableAttr) {
      this.modifiedTableAttrList.add(modifiedTableAttr);
   }

   public void addModifiedColumns(ModifiedObjectAttr modifiedColAttr) {
      this.modifiedColAttrList.add(modifiedColAttr);
   }

   public void addModifiedConstraints(ModifiedObjectAttr modifiedConstrAttr) {
      this.modifiedConstrAttrList.add(modifiedConstrAttr);
   }

   public ArrayList getModifiedObjectName() {
      return this.modifiedTableAttrList;
   }

   public ArrayList getModifiedColumns() {
      return this.modifiedColAttrList;
   }

   public ArrayList getModifiedConstraints() {
      return this.modifiedColAttrList;
   }
}

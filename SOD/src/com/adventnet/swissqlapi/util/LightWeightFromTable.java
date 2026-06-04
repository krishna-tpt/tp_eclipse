package com.adventnet.swissqlapi.util;

import java.util.Vector;

public class LightWeightFromTable {
   private Vector invlovedTables;
   private Vector relation;
   private Vector associatedTableRelation;
   private LightWeightFromTable associatedTable;

   public LightWeightFromTable getAssociatedTable() {
      return this.associatedTable;
   }

   public void setAssociatedTable(LightWeightFromTable associatedTable) {
      if (this.getAssociatedTable() != null) {
         LightWeightFromTable child = this.getAssociatedTable();
         child.setAssociatedTable(associatedTable);
      } else {
         this.associatedTable = associatedTable;
      }

   }

   public Vector getAssociatedTableRelation() {
      return this.associatedTableRelation;
   }

   public void setAssociatedTableRelation(Vector associatedTableRelation) {
      if (this.getAssociatedTableRelation() != null) {
         LightWeightFromTable child = this.getAssociatedTable();
         child.setAssociatedTableRelation(associatedTableRelation);
      } else {
         this.associatedTableRelation = associatedTableRelation;
      }

   }

   public Vector getRelation() {
      return this.relation;
   }

   public void setRelation(Vector relation) {
      this.relation = relation;
   }

   public Vector getInvlovedTables() {
      return this.invlovedTables;
   }

   public void setInvlovedTables(Vector invlovedTables) {
      this.invlovedTables = invlovedTables;
   }

   public LightWeightFromTable clone() {
      LightWeightFromTable lwft = new LightWeightFromTable();
      if (this.associatedTable == null) {
         lwft.setAssociatedTableRelation(this.associatedTableRelation);
      } else {
         lwft.setAssociatedTableRelation((Vector)this.associatedTableRelation.clone());
      }

      if (this.associatedTable == null) {
         lwft.setAssociatedTable(this.associatedTable);
      } else {
         lwft.setAssociatedTable(this.associatedTable.clone());
      }

      if (this.invlovedTables == null) {
         lwft.setInvlovedTables(this.invlovedTables);
      } else {
         lwft.setInvlovedTables((Vector)this.invlovedTables.clone());
      }

      if (this.relation == null) {
         lwft.setRelation(this.relation);
      } else {
         lwft.setRelation((Vector)this.relation.clone());
      }

      return lwft;
   }

   public void clear() {
      this.setInvlovedTables((Vector)null);
      this.setRelation((Vector)null);
      this.setAssociatedTable((LightWeightFromTable)null);
      this.setAssociatedTableRelation((Vector)null);
   }
}

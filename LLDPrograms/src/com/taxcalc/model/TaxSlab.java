package com.taxcalc.model;

import java.io.Serializable;

/**
 * Represents a single income tax bracket.
 *
 * Example:
 *   minIncome = 0,       maxIncome = 250000,  rate = 0.0  → 0% on first 2.5L
 *   minIncome = 250001,  maxIncome = 500000,  rate = 0.05 → 5% on 2.5L–5L
 *   minIncome = 500001,  maxIncome = MAX,      rate = 0.20 → 20% on 5L+
 *
 * maxIncome = Double.MAX_VALUE means "no upper limit" (the topmost slab).
 */
public class TaxSlab implements Serializable, Comparable<TaxSlab> {
	private static final long serialVersionUID = 1L;

	private double minIncome;
	private double maxIncome;
	private double taxRate; // 0.0 – 1.0 (e.g. 0.05 = 5%)

	public TaxSlab(double minIncome, double maxIncome, double taxRate) {
		this.minIncome = minIncome;
		this.maxIncome = maxIncome;
		this.taxRate = taxRate;
	}

	public double getMinIncome() {
		return minIncome;
	}

	public double getMaxIncome() {
		return maxIncome;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setMinIncome(double minIncome) {
		this.minIncome = minIncome;
	}

	public void setMaxIncome(double maxIncome) {
		this.maxIncome = maxIncome;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	@Override
	public int compareTo(TaxSlab other) {
		return Double.compare(this.minIncome, other.minIncome);
	}

	@Override
	public String toString() {
		String maxStr = (maxIncome == Double.MAX_VALUE) ? "Above" : String.format("₹%.0f", maxIncome);
		return String.format("₹%.0f – %s  →  %.1f%%", minIncome, maxStr, taxRate * 100);
	}
}

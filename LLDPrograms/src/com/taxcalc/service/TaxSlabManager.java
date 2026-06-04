package com.taxcalc.service;

import com.taxcalc.model.TaxSlab;
import com.taxcalc.repository.TaxSlabRepository;

import java.util.*;

/**
 * SINGLETON — Manages tax slabs.
 * Only one instance exists throughout the application (as required in interview).
 *
 * TAX CALCULATION — The critical correct logic:
 * -----------------------------------------------
 * Tax is calculated PROGRESSIVELY (like a marginal tax system).
 * Each slab rate applies ONLY to the income within that bracket,
 * NOT to the total income.
 *
 * Example: Income = ₹8,00,000
 *   Slab 1: ₹0–2,50,000      → 0%   → ₹0
 *   Slab 2: ₹2,50,001–5,00,000 → 5% → ₹12,500    (on ₹2,50,000)
 *   Slab 3: ₹5,00,001–10,00,000→ 20% → ₹60,000   (on ₹3,00,000)
 *   Total Tax = ₹72,500
 *
 * WRONG approach (what cost the candidate): applying the highest slab rate to TOTAL income.
 */
public class TaxSlabManager {

	private static TaxSlabManager instance;

	private final TaxSlabRepository repository;

	private TaxSlabManager() {
		this.repository = new TaxSlabRepository();
	}

	public static synchronized TaxSlabManager getInstance() {
		if (instance == null) {
			instance = new TaxSlabManager();
		}
		return instance;
	}

	public List<TaxSlab> getAllSlabs() {
		return repository.findAll(); // already sorted by minIncome
	}

	public void addSlab(TaxSlab slab) {
		repository.addSlab(slab);
	}

	public void replaceAllSlabs(List<TaxSlab> slabs) {
		repository.saveAll(slabs);
	}

	public void clearAllSlabs() {
		repository.clearAll();
	}

	/**
	 * CORRECT progressive (marginal) tax calculation.
	 *
	 * @param income Annual gross income in ₹
	 * @return TaxResult containing breakdown and total
	 */
	public TaxResult calculateTax(double income) {
		List<TaxSlab> slabs = getAllSlabs();
		Collections.sort(slabs); // ensure ascending order

		TaxResult result = new TaxResult(income);

		for (TaxSlab slab : slabs) {
			double slabMin = slab.getMinIncome();
			double slabMax = slab.getMaxIncome();

			// If income doesn't reach this slab at all, skip
			if (income <= slabMin)
				continue;

			// Taxable amount in this slab = min(income, slabMax) - slabMin
			double upperBound = (slabMax == Double.MAX_VALUE) ? income : Math.min(income, slabMax);
			double taxableInThisSlab = upperBound - slabMin;

			if (taxableInThisSlab <= 0)
				continue;

			double taxForSlab = taxableInThisSlab * slab.getTaxRate();
			result.addBreakdown(slab, taxableInThisSlab, taxForSlab);
		}

		return result;
	}

	// ─────────────────────────────────────────────────────────────────
	// Inner class: holds calculation breakdown
	// ─────────────────────────────────────────────────────────────────
	public static class TaxResult {
		private final double grossIncome;
		private final List<SlabBreakdown> breakdowns = new ArrayList<>();
		private double totalTax = 0;

		public TaxResult(double grossIncome) {
			this.grossIncome = grossIncome;
		}

		public void addBreakdown(TaxSlab slab, double taxableAmount, double tax) {
			breakdowns.add(new SlabBreakdown(slab, taxableAmount, tax));
			totalTax += tax;
		}

		public double getGrossIncome() {
			return grossIncome;
		}

		public double getTotalTax() {
			return totalTax;
		}

		public double getNetIncome() {
			return grossIncome - totalTax;
		}

		public double getEffectiveRate() {
			return grossIncome == 0 ? 0 : (totalTax / grossIncome) * 100;
		}

		public List<SlabBreakdown> getBreakdowns() {
			return Collections.unmodifiableList(breakdowns);
		}
	}

	public static class SlabBreakdown {
		public final TaxSlab slab;
		public final double taxableAmount;
		public final double tax;

		public SlabBreakdown(TaxSlab slab, double taxableAmount, double tax) {
			this.slab = slab;
			this.taxableAmount = taxableAmount;
			this.tax = tax;
		}
	}
}

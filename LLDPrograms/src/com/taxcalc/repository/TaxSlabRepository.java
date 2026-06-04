package com.taxcalc.repository;

import com.taxcalc.model.TaxSlab;

import java.io.*;
import java.util.*;

/**
 * Persists TaxSlab list to file.
 * File: data/taxslabs.dat
 */
public class TaxSlabRepository {

	private static final String DATA_DIR = "data";
	private static final String FILE_PATH = DATA_DIR + File.separator + "taxslabs.dat";

	private List<TaxSlab> slabs = new ArrayList<>();

	public TaxSlabRepository() {
		ensureDataDir();
		load();
		if (slabs.isEmpty())
			seedDefaults();
	}

	private void ensureDataDir() {
		File dir = new File(DATA_DIR);
		if (!dir.exists())
			dir.mkdirs();
	}

	/** Seed Indian Income Tax 2024 Old Regime as defaults */
	private void seedDefaults() {
		slabs.add(new TaxSlab(0, 250000, 0.00));
		slabs.add(new TaxSlab(250000, 500000, 0.05));
		slabs.add(new TaxSlab(500000, 1000000, 0.20));
		slabs.add(new TaxSlab(1000000, Double.MAX_VALUE, 0.30));
		persist();
		System.out.println("  [INFO] Default Indian tax slabs loaded (Old Regime).");
	}

	public List<TaxSlab> findAll() {
		List<TaxSlab> sorted = new ArrayList<>(slabs);
		Collections.sort(sorted);
		return sorted;
	}

	public void saveAll(List<TaxSlab> newSlabs) {
		this.slabs = new ArrayList<>(newSlabs);
		persist();
	}

	public void addSlab(TaxSlab slab) {
		slabs.add(slab);
		persist();
	}

	public void clearAll() {
		slabs.clear();
		persist();
	}

	@SuppressWarnings("unchecked")
	private void load() {
		File file = new File(FILE_PATH);
		if (!file.exists())
			return;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
			slabs = (List<TaxSlab>) ois.readObject();
		} catch (Exception e) {
			System.out.println("  [WARN] Could not load tax slabs — seeding defaults. (" + e.getMessage() + ")");
			slabs = new ArrayList<>();
		}
	}

	private void persist() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
			oos.writeObject(slabs);
		} catch (IOException e) {
			System.out.println("  [ERROR] Failed to save tax slabs: " + e.getMessage());
		}
	}
}

package tptTest;

import java.util.Scanner;

public class MonsterSolution {
	    public static void main(String[] args) {
	        Scanner sc = new Scanner(System.in);
	        
	        // Read dimensions
	        System.out.println("Enter Total Rows and cols:");
	        int rows = sc.nextInt();
	        int cols = sc.nextInt();
	        
	        
	        // Read positions
	        System.out.println("Enter Adventure Rows and cols:");
	        int aRow = sc.nextInt();
	        int aCol = sc.nextInt();
	        
	        System.out.println("Enter Monster Rows and cols:");
	        int mRow = sc.nextInt();
	        int mCol = sc.nextInt();
	        
	        System.out.println("Enter Gold Rows and cols:");
	        int gRow = sc.nextInt();
	        int gCol = sc.nextInt();
	        
	        // Calculate Manhattan distances
	        int distanceA = Math.abs(aRow - gRow) + Math.abs(aCol - gCol);
	        int distanceM = Math.abs(mRow - gRow) + Math.abs(mCol - gCol);
	        
	        // Check if A can reach gold before or at the same time as M
	        if (distanceA <= distanceM) {
	            System.out.println(distanceA);
	        } else {
	            System.out.println("No possible solution");
	        }
	        
	        sc.close();
	    }
	}
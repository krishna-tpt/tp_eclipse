package tptTest;

import java.util.Scanner;

public class DungeonGame {
	    public static void main(String[] args) {
	        Scanner sc = new Scanner(System.in);
	        
	        // Read dimensions
	        System.out.println("Enter Total Rows and cols:");
	        int rows = sc.nextInt();//5
	        int cols = sc.nextInt(); //5
	        
	        // Read adventurer position
	        System.out.println("Enter Adventure Rows and cols:");
	        int aRow = sc.nextInt(); //5
	        int aCol = sc.nextInt(); //0
	        
	        // Read gold position
	        System.out.println("Enter Gold Rows and cols:");
	        int gRow = sc.nextInt(); //1
	        int gCol = sc.nextInt(); //4
	        
	        // Calculate Manhattan distance (minimum moves)
	        int minMoves = Math.abs(aRow - gRow) + Math.abs(aCol - gCol);
	        //(5-1)+(1-4) = 4+3
	        
	        System.out.println(minMoves);
	        
	        sc.close();
	    }
	}
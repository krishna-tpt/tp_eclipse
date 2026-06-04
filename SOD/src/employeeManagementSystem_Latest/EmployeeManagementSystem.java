package employeeManagementSystem_Latest;

import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

// Main class for console interaction
public class EmployeeManagementSystem {
	private static EmployeeManager manager = new EmployeeManager();
	private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		boolean exit = false;
		while (!exit) {
			System.out.println("\nMenu:");
			System.out.println("1. Show all records");
			System.out.println("2. Search, update and add records");
			System.out.println("3. Remove, get manager or replace his manager");
			System.out.println("4. Get records based on manager");
			System.out.println("5. Show designation and count");
			System.out.println("6. Show department and count");
			System.out.println("7. Show manager and count his employee");
			System.out.println("8. Manager -> his employee -> his employee -> his employee");
			System.out.println("Exit");
			System.out.print("Choose option: ");
			String choice = scanner.nextLine().trim();

			switch (choice) {
			case "1":
				manager.showAll();
				break;
			case "2":
				handleOption2();
				break;
			case "3":
				handleOption3();
				break;
			case "4":
				System.out.print("Enter manager ID: ");
				int mgrId4 = Integer.parseInt(scanner.nextLine());
				List<Employee> emps = manager.getEmployeesByManager(mgrId4);
				emps.forEach(System.out::println);
				break;
			case "5":
				manager.showDesignationCount();
				break;
			case "6":
				manager.showDepartmentCount();
				break;
			case "7":
				manager.showManagerEmployeeCount();
				break;
			case "8":
				System.out.print("Enter starting manager ID: ");
				int mgrId8 = Integer.parseInt(scanner.nextLine());
				manager.showHierarchy(mgrId8, 1);
				break;
			case "Exit":
			case "exit":
				exit = true;
				break;
			default:
				System.out.println("Invalid option.");
			}
		}
		scanner.close();
	}

	// Handle add, search, update
	private static void handleOption2() {
		System.out.println("Sub-menu: Add, Search, Update");
		System.out.print("Choose (add/search/update): ");
		String sub = scanner.nextLine().trim().toLowerCase();
		if (sub.equals("add")) {
			System.out.print("Name: ");
			String name = scanner.nextLine();
			System.out.print("Designation: ");
			String des = scanner.nextLine();
			System.out.print("Department: ");
			String dept = scanner.nextLine();
			System.out.print("Salary: ");
			double sal = Double.parseDouble(scanner.nextLine());
			System.out.print("Manager ID (0 for none): ");
			int mgr = Integer.parseInt(scanner.nextLine());
			manager.addEmployee(name, des, dept, sal, mgr);
		} else if (sub.equals("update")) {
			System.out.print("Enter employee ID: ");
			int id = Integer.parseInt(scanner.nextLine());
			manager.updateEmployee(id, scanner);
		} else if (sub.equals("search")) {
			Predicate<Employee> predicate = buildSearchPredicate();
			List<Employee> results = manager.search(predicate);
			if (results.isEmpty()) {
				System.out.println("No results found.");
			} else {
				results.forEach(System.out::println);
			}
		} else {
			System.out.println("Invalid sub-option.");
		}
	}

	// Handle remove, get manager, replace manager
	private static void handleOption3() {
		System.out.println("Sub-menu: Remove, Get Manager, Replace Manager");
		System.out.print("Choose (remove/get/replace): ");
		String sub = scanner.nextLine().trim().toLowerCase();
		if (sub.equals("remove")) {
			System.out.print("Enter employee ID: ");
			int id = Integer.parseInt(scanner.nextLine());
			manager.removeEmployee(id);
		} else if (sub.equals("get")) {
			System.out.print("Enter employee ID: ");
			int id = Integer.parseInt(scanner.nextLine());
			Employee mgr = manager.getManager(id);
			if (mgr != null) {
				System.out.println("Manager: " + mgr);
			} else {
				System.out.println("No manager found.");
			}
		} else if (sub.equals("replace")) {
			System.out.print("Enter employee ID: ");
			int id = Integer.parseInt(scanner.nextLine());
			System.out.print("Enter new manager ID (0 for none): ");
			int newMgr = Integer.parseInt(scanner.nextLine());
			manager.replaceManager(id, newMgr);
		} else {
			System.out.println("Invalid sub-option.");
		}
	}

	// Build predicate for search based on user input
	private static Predicate<Employee> buildSearchPredicate() {
		System.out.println("Search by field: id, name, designation, department, salary, managerId");
		System.out.print("Field: ");
		String field = scanner.nextLine().trim().toLowerCase();
		System.out.print("Operator: ");
		String op = scanner.nextLine().trim().toLowerCase();
		System.out.print("Value: ");
		String val = scanner.nextLine().trim();
		String temp=null;
		if (op.equals("between")) {
			System.out.print("Second Value: ");
			temp = scanner.nextLine().trim();
		}
		final String val2 = temp ;// For between

		return emp -> {
			switch (field) {
			case "id":
				int idVal = Integer.parseInt(val);
				int id = emp.getId();
				return numberCondition(id, op, idVal, val2.isEmpty() ? 0 : Integer.parseInt(val2));
			case "name":
				return stringCondition(emp.getName(), op, val);
			case "designation":
				return stringCondition(emp.getDesignation(), op, val);
			case "department":
				return stringCondition(emp.getDepartment(), op, val);
			case "salary":
				double salVal = Double.parseDouble(val);
				double sal = emp.getSalary();
				return numberCondition(sal, op, salVal, val2.isEmpty() ? 0 : Double.parseDouble(val2));
			case "managerid":
				int mgrVal = Integer.parseInt(val);
				int mgr = emp.getManagerId();
				return numberCondition(mgr, op, mgrVal, val2.isEmpty() ? 0 : Integer.parseInt(val2));
			default:
				return false;
			}
		};
	}

	// Helper for string conditions
	private static boolean stringCondition(String str, String op, String val) {
		switch (op) {
		case "equals":
			return str.equals(val);
		case "not equals":
			return !str.equals(val);
		case "contains":
			return str.contains(val);
		case "not contains":
			return !str.contains(val);
		case "starts with":
			return str.startsWith(val);
		case "ends with":
			return str.endsWith(val);
		default:
			return false;
		}
	}

	// Helper for number conditions (overloaded for int and double)
	private static boolean numberCondition(int num, String op, int val, int val2) {
		switch (op) {
		case ">":
			return num > val;
		case "<":
			return num < val;
		case "=":
		case "equals":
			return num == val;
		case "!=":
		case "not equals":
			return num != val;
		case "between":
			return num >= val && num <= val2;
		default:
			return false;
		}
	}

	private static boolean numberCondition(double num, String op, double val, double val2) {
		switch (op) {
		case ">":
			return num > val;
		case "<":
			return num < val;
		case "=":
		case "equals":
			return num == val;
		case "!=":
		case "not equals":
			return num != val;
		case "between":
			return num >= val && num <= val2;
		default:
			return false;
		}
	}
}
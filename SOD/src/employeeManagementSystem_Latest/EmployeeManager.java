package employeeManagementSystem_Latest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public // EmployeeManager class handling all operations (Repository + Service layer in
		// LLD)
class EmployeeManager {
	private Map<Integer, Employee> employees = new HashMap<>(); // Key: ID, Value: Employee
	private Map<Integer, List<Integer>> reports = new HashMap<>(); // Key: ManagerID, Value: List of direct report IDs
	private int nextId = 1; // Auto-increment ID

	// Add a new employee
	public void addEmployee(String name, String designation, String department, double salary, int managerId) {
		if (managerId != 0 && !employees.containsKey(managerId)) {
			System.out.println("Manager ID " + managerId + " does not exist.");
			return;
		}
		Employee emp = new Employee(nextId, name, designation, department, salary, managerId);
		employees.put(nextId, emp);
		if (managerId != 0) {
			reports.computeIfAbsent(managerId, k -> new ArrayList<>()).add(nextId);
		}
		nextId++;
		System.out.println("Employee added successfully.");
	}

	// Show all records
	public void showAll() {
		if (employees.isEmpty()) {
			System.out.println("No employees found.");
			return;
		}
		employees.values().forEach(System.out::println);
	}

	// Search employees based on predicates
	public List<Employee> search(Predicate<Employee> predicate) {
		return employees.values().stream().filter(predicate).collect(Collectors.toList());
	}

	// Update employee by ID
	public void updateEmployee(int id, Scanner scanner) {
		Employee emp = employees.get(id);
		if (emp == null) {
			System.out.println("Employee ID " + id + " not found.");
			return;
		}
		System.out.println("Current: " + emp);
		System.out.print("New Name (enter to skip): ");
		String name = scanner.nextLine();
		if (!name.isEmpty())
			emp.setName(name);
		System.out.print("New Designation (enter to skip): ");
		String des = scanner.nextLine();
		if (!des.isEmpty())
			emp.setDesignation(des);
		System.out.print("New Department (enter to skip): ");
		String dept = scanner.nextLine();
		if (!dept.isEmpty())
			emp.setDepartment(dept);
		System.out.print("New Salary (enter 0 to skip): ");
		double sal = scanner.nextDouble();
		if (sal != 0)
			emp.setSalary(sal);
		scanner.nextLine(); // Consume newline
		System.out.print("New Manager ID (enter 0 to skip): ");
		int mgr = scanner.nextInt();
		if (mgr != 0) {
			if (!employees.containsKey(mgr)) {
				System.out.println("Manager ID " + mgr + " does not exist.");
			} else {
				// Remove from old manager's reports
				if (emp.getManagerId() != 0) {
					reports.get(emp.getManagerId()).remove(Integer.valueOf(id));
				}
				emp.setManagerId(mgr);
				reports.computeIfAbsent(mgr, k -> new ArrayList<>()).add(id);
			}
		}
		scanner.nextLine(); // Consume newline
		System.out.println("Employee updated.");
	}

	// Remove employee by ID
	public void removeEmployee(int id) {
		Employee emp = employees.remove(id);
		if (emp == null) {
			System.out.println("Employee ID " + id + " not found.");
			return;
		}
		// Remove from manager's reports
		if (emp.getManagerId() != 0) {
			reports.get(emp.getManagerId()).remove(Integer.valueOf(id));
		}
		// Reassign direct reports to the removed employee's manager
		List<Integer> directReports = reports.remove(id);
		if (directReports != null) {
			for (int reportId : directReports) {
				Employee report = employees.get(reportId);
				report.setManagerId(emp.getManagerId());
				if (emp.getManagerId() != 0) {
					reports.computeIfAbsent(emp.getManagerId(), k -> new ArrayList<>()).add(reportId);
				}
			}
		}
		System.out.println("Employee removed and reports reassigned.");
	}

	// Get manager of an employee
	public Employee getManager(int empId) {
		Employee emp = employees.get(empId);
		if (emp == null || emp.getManagerId() == 0)
			return null;
		return employees.get(emp.getManagerId());
	}

	// Replace manager for an employee
	public void replaceManager(int empId, int newMgrId) {
		Employee emp = employees.get(empId);
		if (emp == null) {
			System.out.println("Employee ID " + empId + " not found.");
			return;
		}
		if (newMgrId != 0 && !employees.containsKey(newMgrId)) {
			System.out.println("New Manager ID " + newMgrId + " does not exist.");
			return;
		}
		// Remove from old manager
		if (emp.getManagerId() != 0) {
			reports.get(emp.getManagerId()).remove(Integer.valueOf(empId));
		}
		emp.setManagerId(newMgrId);
		if (newMgrId != 0) {
			reports.computeIfAbsent(newMgrId, k -> new ArrayList<>()).add(empId);
		}
		System.out.println("Manager replaced.");
	}

	// Get employees under a manager (direct)
	public List<Employee> getEmployeesByManager(int mgrId) {
		List<Integer> reportIds = reports.getOrDefault(mgrId, new ArrayList<>());
		return reportIds.stream().map(employees::get).collect(Collectors.toList());
	}

	// Show designation and count
	public void showDesignationCount() {
		Map<String, Long> counts = employees.values().stream()
				.collect(Collectors.groupingBy(Employee::getDesignation, Collectors.counting()));
		counts.forEach((des, count) -> System.out.println(des + ": " + count));
	}

	// Show department and count
	public void showDepartmentCount() {
		Map<String, Long> counts = employees.values().stream()
				.collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));
		counts.forEach((dept, count) -> System.out.println(dept + ": " + count));
	}

	// Show manager and count of his direct employees
	public void showManagerEmployeeCount() {
		reports.forEach((mgrId, reps) -> {
			Employee mgr = employees.get(mgrId);
			if (mgr != null) {
				System.out.println(mgr.getName() + " (ID: " + mgrId + "): " + reps.size());
			}
		});
	}

	// Show hierarchy starting from a manager (up to 3 levels deep)
	public void showHierarchy(int mgrId, int level) {
		if (level > 3)
			return; // Limit to 3 levels
		Employee mgr = employees.get(mgrId);
		if (mgr == null) {
			System.out.println("Manager ID " + mgrId + " not found.");
			return;
		}
		System.out.println("  ".repeat(level - 1) + mgr.getName() + " (ID: " + mgrId + ")");
		List<Integer> direct = reports.getOrDefault(mgrId, new ArrayList<>());
		for (int repId : direct) {
			showHierarchy(repId, level + 1);
		}
	}
}

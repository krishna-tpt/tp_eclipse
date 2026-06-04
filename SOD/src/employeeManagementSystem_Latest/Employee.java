package employeeManagementSystem_Latest;

public class Employee {
	// Employee class representing an employee record
	private int id;
	private String name;
	private String designation;
	private String department;
	private double salary;
	private int managerId; // 0 if no manager (top-level)

	public Employee(int id, String name, String designation, String department, double salary, int managerId) {
		this.id = id;
		this.name = name;
		this.designation = designation;
		this.department = department;
		this.salary = salary;
		this.managerId = managerId;
	}

	// Getters and Setters
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public int getManagerId() {
		return managerId;
	}

	public void setManagerId(int managerId) {
		this.managerId = managerId;
	}

	@Override
	public String toString() {
		return "Employee [ID=" + id + ", Name=" + name + ", Designation=" + designation + ", Department=" + department
				+ ", Salary=" + salary + ", ManagerID=" + managerId + "]";
	}
}
package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {

	public List<Department> findAll() {
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "Inform�tica"));
		list.add(new Department(2, "Eletr�nica"));
		list.add(new Department(3, "Software"));
		return list;
	}
}

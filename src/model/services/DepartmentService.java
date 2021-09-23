package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao dao = DaoFactory.createDepartmentDao();

	public List<Department> findAll() {
		return dao.findAll();
		// Dados Mokados para testes
		// List<Department> list = new ArrayList<>();
		// list.add(new Department(1, "Informática"));
		// list.add(new Department(2, "Eletrônica"));
		// list.add(new Department(3, "Software"));
		// return list;
	}
}

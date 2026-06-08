package com.expensemanager.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import com.expensemanager.dao.CategoryDAO;
import com.expensemanager.dao.ColumnDefinitionDAO;
import com.expensemanager.dao.SubCategoryDAO;
import com.expensemanager.model.Category;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * /settings GET → settings page (categories, subcategories, custom columns)
 * /settings POST → add/delete category, subcategory, column
 */
@WebServlet("/settings")
public class SettingsServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			CategoryDAO catDao = new CategoryDAO();
			SubCategoryDAO scDao = new SubCategoryDAO();
			ColumnDefinitionDAO colDao = new ColumnDefinitionDAO();

			req.setAttribute("incomeCategories", catDao.findByType("INCOME"));
			req.setAttribute("expenseCategories", catDao.findByType("EXPENSE"));
			req.setAttribute("allSubCategories", scDao.findAll());
			req.setAttribute("allCategories", catDao.findByType("INCOME").stream().map(c -> c) // combine both
					.collect(java.util.stream.Collectors.toList()));
			req.setAttribute("allCategoriesCombined", getAllCategories(catDao));
			req.setAttribute("incomeColumns", colDao.findByType("INCOME"));
			req.setAttribute("expenseColumns", colDao.findByType("EXPENSE"));
		} catch (Exception e) {
			req.setAttribute("dbError", e.getMessage());
		}
		req.getRequestDispatcher("/WEB-INF/views/settings.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		String action = req.getParameter("action");

		System.out.println("Action: "+action);
		try {
			switch (action != null ? action : "") {
			case "addCategory" :{
				String name = req.getParameter("name");
				String type = req.getParameter("type");
				if (name != null && !name.isBlank())
					new CategoryDAO().insert(name.trim(), type);
				break;
			}
			case "deleteCategory" : {
				int id = Integer.parseInt(req.getParameter("id"));
				new CategoryDAO().delete(id);
				break;
			}
			case "addSubCategory" : {
				System.out.println("Inside addSubCategory:");
				
				Enumeration<String> paramNames = req.getParameterNames();

			/**	while (paramNames.hasMoreElements()) {
				    String key = paramNames.nextElement();
				    String value = req.getParameter(key);

				    System.out.println("Key: " + key + ", Value: " + value);
				} */
				
				String name = req.getParameter("name");
				int catId = Integer.parseInt(req.getParameter("categoryId"));
				if (name != null && !name.isBlank())
					new SubCategoryDAO().insert(name.trim(), catId);
				break;
			}
			case "deleteSubCategory" : {
				int id = Integer.parseInt(req.getParameter("id"));
				new SubCategoryDAO().delete(id);
				break;
			}
			case "addColumn" : {
				String colName = req.getParameter("colName");
				String type = req.getParameter("type");
				if (colName != null && !colName.isBlank())
					new ColumnDefinitionDAO().insert(colName.trim(), type);
				break;
			}
			case "deleteColumn" : {
				int id = Integer.parseInt(req.getParameter("id"));
				new ColumnDefinitionDAO().delete(id);
				break;
			}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			/* ignore */ }

		resp.sendRedirect(req.getContextPath() + "/settings?msg=saved");
	}

	private List<Category> getAllCategories(CategoryDAO dao) throws Exception {
		List<Category> all = new java.util.ArrayList<>();
		all.addAll(dao.findByType("INCOME"));
		all.addAll(dao.findByType("EXPENSE"));
		return all;
	}
}
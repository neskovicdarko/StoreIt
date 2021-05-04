package com.example.demo.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.repository.CategoryRepository;

import model.Category;

@Controller
@RequestMapping(value = "/category")
public class CategoryController {

	@Autowired
	CategoryRepository cr;

	@RequestMapping(value = "/saveCategory", method = RequestMethod.POST)
	public String saveCategory(@Valid Category cat, HttpServletRequest request, Errors e) {

		boolean ok = true;
		
		if (cat.getCatMultiplicator() <= 0) {
			request.setAttribute("catMultiplicatorErrorMsg", "Must not be zero or negative value");
			ok = false;
		}
		
		if (cat.getCatName().isBlank() || cat.getCatName() == null) {
			request.setAttribute("catNameErrorMsg", "Must not be left blank");
			ok = false;
		}
		
		if (!ok) {
			return "Admin/homepage";
		}
		
		if (e.hasErrors())
			return "Admin/homepage";

		cr.save(cat);
		
		List<Category> cats = cr.findAll();
		request.getSession().setAttribute("categories", cats);
		
		return "Admin/homepage";
	}

	@RequestMapping(value = "/modifyRouter", method = RequestMethod.GET)
	public String modifyRouter(Integer catId, HttpServletRequest request) {
		Category cat = cr.findById(catId).get();
		request.getSession().setAttribute("category", cat);
		return "/Admin/modifyCategory";
	}

	@RequestMapping(value = "/modifyCategory", method = RequestMethod.POST)
	public String modifyCategory(String newName, Float newMultiplicator, HttpServletRequest request) {

		Category cat = (Category) request.getSession().getAttribute("category");
		cat = cr.findById(cat.getCatId()).get();

		if (newName != null && (!cat.getCatName().equals(newName)))
			cat.setCatName(newName);

		if (newMultiplicator != null && newMultiplicator > 0) {
			cat.setCatMultiplicator(newMultiplicator);
		}

		cr.persistCategory(cat);

		List<Category> categories = cr.findAll();
		request.getSession().setAttribute("categories", categories);

		return "/Admin/homepage";
	}
	
	@RequestMapping(value = "/sortByCatName", method = RequestMethod.GET)
	public String sortByCatName(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Category> categories = (List<Category>) request.getSession().getAttribute("categories");
		
		Collections.sort(categories, new Comparator<Category>() {
			public int compare(Category l, Category r) {
					return l.getCatName().toUpperCase().compareTo(r.getCatName().toUpperCase());
				}
		});
		
		request.getSession().setAttribute("categories", categories);
		return "/Admin/homepage";
	}
	
	@RequestMapping(value = "/sortByCatMultiplicator", method = RequestMethod.GET)
	public String sortByCatMultiplicator(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Category> categories = (List<Category>) request.getSession().getAttribute("categories");
		
		Collections.sort(categories, new Comparator<Category>() {
			public int compare(Category l, Category r) {
					if (l.getCatMultiplicator() - r.getCatMultiplicator() < 0)
						return -1;
					else if (l.getCatMultiplicator() - r.getCatMultiplicator() > 0)
						return 1;
					else 
						return 0;
				}
		});
		
		request.getSession().setAttribute("categories", categories);
		return "/Admin/homepage";
	}

}

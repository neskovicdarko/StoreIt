package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.LotRepository;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import model.Admin;
import model.Category;
import model.Lot;
import model.Message;
import model.Reservation;
import model.User;

@Controller
@RequestMapping(value = "/user")
public class UserController {

	@Autowired
	UserRepository ur;

	@Autowired
	AdminRepository ar;

	@Autowired
	LotRepository lr;

	@Autowired
	CategoryRepository cr;

	@Autowired
	ReservationRepository rr;

	@Autowired
	MessageRepository mr;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String register(@Valid User u, Errors e, Model m, HttpServletRequest request) {

		boolean success = true;

		if (e.hasErrors()) {
			request.setAttribute("errorMessage", "Error has been noted while trying to register new user");
			success = false;
		}

		String expression = "^[\\w.+\\-]+@[a-z]+\\.[a-zA-Z]+$";
		CharSequence inputStr = u.getGmail();
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (!matcher.matches()) {
			request.setAttribute("emailErrorMessage", "Email is not valid.");
			success = false;
		} else {
			request.setAttribute("emailPH", u.getGmail());
		}

		if (u.getFirstName().isEmpty()) {
			request.setAttribute("firstNameErrorMessage", "First name must not be empty.");
			success = false;
		} else {
			request.setAttribute("firstNamePH", u.getFirstName());
		}

		if (u.getLastName().isEmpty()) {
			request.setAttribute("lastNameErrorMessage", "Last name must not be empty.");
			success = false;
		} else {
			request.setAttribute("lastNamePH", u.getLastName());
		}

		if (u.getPassword().length() < 3) {
			request.setAttribute("passwordErrorMessage", "Password must be at least 5 characters long");
			success = false;
		}

		List<User> registeredUsers = (List<User>) ur.findAll();
		for (User user : registeredUsers)
			if (user.getGmail().equals(u.getGmail())) {
				request.setAttribute("emailErrorMessage", "User with this email already exists");
				success = false;
			}

		if (success) {

			u.setPassword(MD5(u.getPassword()));
			ur.save(u);
			request.getSession().setAttribute("user", u);
			m.addAttribute("poruka", "Registration successful");
			return "index";

		}

		return "/Login/register";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(String gmail, String password, HttpServletRequest request) {

		password = MD5(password);

		List<Lot> lots = (List<Lot>) lr.findAll();
		request.getSession().setAttribute("lots", lots);
		List<Category> categories = cr.findAll();
		request.getSession().setAttribute("categories", categories);

		String expression = "^[\\w.+\\-]+@[a-z]+\\.[a-zA-Z]+$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(gmail);

		if (!matcher.matches()) {
			request.setAttribute("emailErrorMessage", "Email is not valid.");
			return "index";
		}

		List<User> users = (List<User>) ur.findAll();
		User user = null;
		for (User u : users)
			if (u.getGmail().equals(gmail)) {
				if (!u.getPassword().equals(password)) {
					request.setAttribute("passwordErrorMessage", "Password is incorrect!");
					request.setAttribute("emailPH", gmail);
					return "index";
				}
				user = u;
				break;
			}

		if (user != null) {

			List<Message> messages = mr.findAll();
			List<Message> userMessages = new ArrayList<>();

			for (Message msg : messages)
				if (msg.getUser().getUserId() == user.getUserId())
					userMessages.add(msg);

			request.getSession().setAttribute("messages", userMessages);

			List<Reservation> reservations = rr.findAll();
			List<Reservation> usersReservations = new ArrayList<>();

			for (Reservation r : reservations)
				if (r.getUser().getUserId() == user.getUserId())
					usersReservations.add(r);

			request.getSession().setAttribute("reservations", usersReservations);
			request.getSession().setAttribute("user", user);

			return "/User/homepage";
		}

		List<Admin> admins = (List<Admin>) ar.findAll();
		Admin admin = null;
		for (Admin a : admins)
			if (a.getGmail().equals(gmail)) {
				if (!a.getPassword().equals(password)) {
					request.setAttribute("passwordErrorMessage", "Password is incorrect!");
					request.setAttribute("emailPH", gmail);
					return "index";
				}
				admin = a;
				break;
			}

		if (admin != null) {

			List<Message> messages = mr.findAll();
			List<Message> userMessages = new ArrayList<>();

			for (Message msg : messages)
				if (msg.getAdmin().getAdminId() == admin.getAdminId())
					userMessages.add(msg);

			request.getSession().setAttribute("messages", userMessages);
			request.getSession().setAttribute("admin", admin);
			return "/Admin/homepage";
		}

		request.setAttribute("contextMessage", "Data does not match any user in the database");
		return "index";
	}

	@RequestMapping(value = "/updateAdminCredentials", method = RequestMethod.POST)
	public String updateAdminCredentials(String firstName, String lastName, String gmail, String newPassword,
			String password, HttpServletRequest request) {

		password = MD5(password);
		newPassword = MD5(newPassword);

		Admin admin = (Admin) request.getSession().getAttribute("admin");

		String action = (String) request.getParameter("submitAction");

		if (action == null) {
			request.setAttribute("errorMsg", "Action is null in userController");
			return "/Admin/options";
		}

		if (password.isBlank()) {
			request.setAttribute("passErrorMsg", "You must confirm your identity with password");
			return "/Admin/options";
		}
		if (!admin.getPassword().equals(password)) {
			request.setAttribute("errorMsg", "Password does not match with admin password.");
			return "/Admin/options";
		}

		if (action.equals("Delete")) {
			List<Message> msgs = mr.findAll();

			for (Message m : msgs)
				if (m.getAdmin().getAdminId() == admin.getAdminId())
					mr.delete(m);

			ar.delete(admin);
			return "index";
		}

		// else action.equals("Update")

		if (!gmail.isBlank())
			admin.setGmail(gmail);

		if (!firstName.isBlank())
			admin.setFirstName(firstName);

		if (!lastName.isBlank())
			admin.setLastName(lastName);

		if (!newPassword.isBlank())
			admin.setPassword(newPassword);

		ar.save(admin);

		return "Admin/homepage";
	}

	@RequestMapping(value = "/updateUserCredentials", method = RequestMethod.POST)
	public String updateUserCredentials(String firstName, String lastName, String gmail, String newPassword,
			String password, HttpServletRequest request) {
		
		password = MD5(password);
		newPassword = MD5(newPassword);
		
		User user = (User) request.getSession().getAttribute("user");

		String action = (String) request.getParameter("submitAction");

		if (action == null) {
			request.setAttribute("errorMsg", "Action is null in userController");
			return "/User/options";
		}

		if (password.isBlank()) {
			request.setAttribute("passErrorMsg", "You must confirm your identity with password");
			return "/User/options";
		}
		if (!user.getPassword().equals(password)) {
			request.setAttribute("errorMsg", "Password does not match with user password.");
			return "/User/options";
		}

		if (action.equals("Delete")) {
			List<Message> msgs = mr.findAll();
			List<Reservation> ress = rr.findAll();

			for (Reservation r : ress)
				if (r.getUser().getUserId() == user.getUserId()) {
					request.setAttribute("errorMsg",
							"You must conclude all your reservations before deleting account.");
					return "/User/options";
				}

			for (Message m : msgs)
				if (m.getUser().getUserId() == user.getUserId())
					mr.delete(m);

			ur.delete(user);
			return "index";
		}

		// else action.equals("Update")

		if (!gmail.isBlank())
			user.setGmail(gmail);

		if (!firstName.isBlank())
			user.setFirstName(firstName);

		if (!lastName.isBlank())
			user.setLastName(lastName);

		if (!newPassword.isBlank())
			user.setPassword(newPassword);

		ur.save(user);

		return "User/homepage";
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request) {

		Enumeration<String> attributes = request.getSession().getAttributeNames();

		while (attributes.hasMoreElements())
			request.getSession().setAttribute(attributes.nextElement(), null);
		return "index";
	}

	private String MD5(String text) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(text.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return (hashtext);
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e1) {
			throw new RuntimeException(e1);
		}
	}
	
//	@RequestMapping(value = "/justDoIt", method = RequestMethod.POST)
//	public void JustDoIt() {
//		List<Admin> admins = ar.findAll();
//		for (Admin a: admins) {
//			a.setPassword(MD5(a.getPassword()));
//			ar.save(a);
//		}
//		
//		List<User> users = ur.findAll();
//		for (User u: users) {
//			u.setPassword(MD5(u.getPassword()));
//			ur.save(u);
//		}
//	}
	
	
}

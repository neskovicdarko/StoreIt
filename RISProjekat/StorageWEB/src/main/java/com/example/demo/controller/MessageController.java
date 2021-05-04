package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.repository.AdminRepository;
import com.example.demo.repository.MessageRepository;

import model.Admin;
import model.Message;
import model.User;

@Controller
@RequestMapping(value = "/message")
public class MessageController {

	@Autowired
	MessageRepository mr;

	@Autowired
	AdminRepository ar;

	@RequestMapping(value = "/sendMessageToAdmins", method = RequestMethod.POST)
	public String sendMessageToAdmins(String msgSubject, String msgContent, HttpServletRequest request) {

		if (msgSubject == null || msgContent == null || msgSubject.isBlank() || msgContent.isBlank()) {
			request.setAttribute("errorMsg", "You must have both subject and content filled.");
			return "/User/mail";
		}

		Message m = new Message();

		User u = (User) request.getSession().getAttribute("user");
		m.setUser(u);

		m.setMsgSubject(msgSubject);
		m.setMsgContent(msgContent);
		m.setSentDate(new Date());

		byte b = 1;
		m.setSentByUser(b);

		List<Admin> admins = ar.findAll();

		Random rand = new Random();
		Admin admin = admins.get(rand.nextInt(admins.size()));

		m.setAdmin(admin);

		mr.save(m);

		List<Message> messages = mr.findAll();
		List<Message> myMessages = new ArrayList<>();

		for (Message msg : messages)
			if (msg.getUser().getUserId() == u.getUserId())
				myMessages.add(msg);

		request.getSession().setAttribute("messages", myMessages);

		return "/User/mail";
	}

	@RequestMapping(value = "/sendResponseToAdmin", method = RequestMethod.POST)
	public String sendResponseToAdmin(String msgSubject, String msgContent, HttpServletRequest request) {

		if (msgSubject == null || msgContent == null || msgSubject.isBlank() || msgContent.isBlank()) {
			request.setAttribute("errorMsg", "You must have both subject and content filled.");
			return "/User/readMessage";
		}

		Message m = new Message();

		User u = (User) request.getSession().getAttribute("user");
		m.setUser(u);

		m.setMsgSubject(msgSubject);
		m.setMsgContent(msgContent);
		m.setSentDate(new Date());

		byte b = 1;
		m.setSentByUser(b);

		Message adminMessage = (Message) request.getSession().getAttribute("message");

		m.setAdmin(adminMessage.getAdmin());

		mr.save(m);

		List<Message> messages = mr.findAll();
		List<Message> myMessages = new ArrayList<>();

		for (Message msg : messages)
			if (msg.getUser().getUserId() == u.getUserId())
				myMessages.add(msg);

		request.getSession().setAttribute("messages", myMessages);

		return "/User/mail";
	}

	@RequestMapping(value = "/sendResponseToUser", method = RequestMethod.POST)
	public String sendResponseToUser(String msgContent, HttpServletRequest request) {

		if (msgContent == null || msgContent.isBlank()) {
			request.setAttribute("errorMsg", "You must have message content filled.");
			return "/Admin/readMessage";
		}

		Message m = new Message();

		Message userMessage = (Message) request.getSession().getAttribute("message");
		m.setUser(userMessage.getUser());

		m.setMsgSubject("[Response] " + userMessage.getMsgSubject());
		m.setMsgContent(msgContent);
		m.setSentDate(new Date());

		byte b = 0;
		m.setSentByUser(b);

		Admin a = (Admin) request.getSession().getAttribute("admin");
		m.setAdmin(a);

		mr.save(m);

		List<Message> messages = mr.findAll();
		List<Message> myMessages = new ArrayList<>();

		for (Message msg : messages)
			if (msg.getAdmin().getAdminId() == a.getAdminId())
				myMessages.add(msg);

		request.getSession().setAttribute("messages", myMessages);

		return "/Admin/mail";
	}

	@RequestMapping(value = "/deleteMessage", method = RequestMethod.GET)
	public String deleteMessage(Integer msgId, HttpServletRequest request) {

		Message m = mr.findById(msgId).get();
		mr.delete(m);

		Admin a = (Admin) request.getSession().getAttribute("admin");
		User u = (User) request.getSession().getAttribute("user");

		if (u != null) {
			List<Message> messages = mr.findAll();
			List<Message> myMessages = new ArrayList<>();

			for (Message msg : messages)
				if (msg.getUser().getUserId() == u.getUserId())
					myMessages.add(msg);

			request.getSession().setAttribute("messages", myMessages);
			return "/User/mail";
		} else {

			List<Message> messages = mr.findAll();
			List<Message> myMessages = new ArrayList<>();

			for (Message msg : messages)
				if (msg.getAdmin().getAdminId() == a.getAdminId())
					myMessages.add(msg);

			request.getSession().setAttribute("messages", myMessages);

			return "/Admin/mail";
		}
	}

	
	@RequestMapping(value = "/refreshMessages", method = RequestMethod.GET)
	public String refreshMessages(HttpServletRequest request) {

		Admin a = (Admin) request.getSession().getAttribute("admin");
		User u = (User) request.getSession().getAttribute("user");

		if (u != null) {
			List<Message> messages = mr.findAll();
			List<Message> myMessages = new ArrayList<>();

			for (Message msg : messages)
				if (msg.getUser().getUserId() == u.getUserId())
					myMessages.add(msg);

			request.getSession().setAttribute("messages", myMessages);
			return "/User/mail";
		
		} else {

			List<Message> messages = mr.findAll();
			List<Message> myMessages = new ArrayList<>();

			for (Message msg : messages)
				if (msg.getAdmin().getAdminId() == a.getAdminId())
					myMessages.add(msg);

			request.getSession().setAttribute("messages", myMessages);

			return "/Admin/mail";
		}
	}
	
	@RequestMapping(value = "/routerReadByUser", method = RequestMethod.GET)
	public String routerReadByUser(Integer msgId, HttpServletRequest request) {
		Message msg = mr.findById(msgId).get();
		request.getSession().setAttribute("message", msg);
		return "/User/readMessage";
	}

	@RequestMapping(value = "/routerReadByAdmin", method = RequestMethod.GET)
	public String routerReadByAdmin(Integer msgId, HttpServletRequest request) {
		Message msg = mr.findById(msgId).get();
		request.getSession().setAttribute("message", msg);
		return "/Admin/readMessage";
	}
}

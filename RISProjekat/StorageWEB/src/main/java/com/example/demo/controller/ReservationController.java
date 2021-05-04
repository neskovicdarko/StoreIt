package com.example.demo.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.LotRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.StorageUnitRepository;
import com.example.demo.repository.UserRepository;

import model.Category;
import model.Lot;
import model.Reservation;
import model.StorageUnit;
import model.User;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Controller
@RequestMapping(value = "/reservation")
public class ReservationController {

	@Autowired
	ReservationRepository rr;

	@Autowired
	CategoryRepository cr;

	@Autowired
	UserRepository ur;

	@Autowired
	LotRepository lr;

	@Autowired
	StorageUnitRepository sr;

	@RequestMapping(value = "/unloadUnitGET", method = RequestMethod.GET)
	public String unloadUnitGET(Integer reservationId,
			HttpServletRequest request) {
		
		Reservation res = rr.findById(reservationId).get();
		
		int daysInUnit = daysBetween (res.getInDate(), new Date());
		
		if (daysInUnit < 0)
			daysInUnit = 0;
		
		int unusedDays = Math.abs(daysBetween(res.getOutDate(), new Date()));
		if (res.getOutDate().before(new Date()))
			unusedDays = 0;
		
		float usedDaysPrice =  (daysInUnit * res.getPricePerDay());
		float unusedDaysPrice = unusedDays * (res.getPricePerDay() / 2);
		float price = usedDaysPrice + unusedDaysPrice;
		
		request.getSession().setAttribute("pricePerDay", res.getPricePerDay());
		request.getSession().setAttribute("usedDaysPrice", usedDaysPrice);
		request.getSession().setAttribute("unusedDaysPrice", unusedDaysPrice);
		request.getSession().setAttribute("reservation", res);
		request.getSession().setAttribute("totalPrice", price);
		request.getSession().setAttribute("daysInUnit", daysInUnit);
		request.getSession().setAttribute("unusedDays", unusedDays);
		request.getSession().setAttribute("today", niceDate(new Date()));
		request.getSession().setAttribute("inDate", niceDate(res.getInDate()));
		request.getSession().setAttribute("outDate", niceDate(res.getOutDate()));
		
		return "/Admin/unloadUnit";
	}
	
	
	
	@RequestMapping(value = "/saveReservation", method = RequestMethod.POST)
	public String saveReservation(Integer lotId, Integer catId, String description, Date inDate, Date outDate,
			HttpServletRequest request) {

		boolean errorInd = false;

		if (description.isBlank()) {
			request.setAttribute("descriptionError", "Description cannot be blank.");
			errorInd = true;
		}

		if (inDate == null) {
			request.setAttribute("inDateError", "In date cannot be undeclared.");
			errorInd = true;
		}

		if (outDate == null) {
			request.setAttribute("outDateError", "Out date cannot be undeclared.");
			errorInd = true;
		}

		if (inDate != null && outDate != null)
			if (inDate.after(outDate) || inDate.before(new Date())) {
				request.setAttribute("saveReservationMessage",
						"Date of loading cannot be before date of emptying the storage or before today (" 
										+ niceDate(new Date()) + ")");
				errorInd = true;
			}

		if (errorInd)
			return "/User/homepage";

		User u = (User) request.getSession().getAttribute("user");

		if (u == null) {
			request.setAttribute("saveReservationMessage",
					"You are not connected to your user account anymore for some reason.");
			return "/User/homepage";
		}

		Reservation r = new Reservation();
		Category cat = cr.findById(catId).get();
		Lot lot = lr.findById(lotId).get();

		r.setUser(u);
		r.setLot(lot);
		r.setCategory(cat);
		r.setDescription(description);
		r.setInDate(inDate);
		r.setOutDate(outDate);
		r.setPricePerDay(lot.getStoragePrice() * cat.getCatMultiplicator());

		// Removing all storage units that are not from required lot
		List<StorageUnit> storageUnits = sr.findAll();

		List<StorageUnit> ourUnits = new ArrayList<>();
		for (StorageUnit s : storageUnits)
			if (s.getLot().getLotId() == lot.getLotId())
				ourUnits.add(s);

		StorageUnit choosenStorage = null;
		List<Reservation> reservations = rr.findAll();

		for (StorageUnit s : ourUnits) {
			boolean empty = true;
			for (Reservation res : reservations)
				if (res.getStorageUnit().getUnitId() == s.getUnitId() && datesOverlap(res, inDate, outDate)) {
					empty = false;
					break;
				}

			if (empty) {
				choosenStorage = s;
				break;
			}
		}

		if (choosenStorage == null) {
			request.setAttribute("saveReservationMessage",
					"Choosen storage has no free storage units at this period, please look for another storage");
			return "/User/homepage";
		}

		r.setStorageUnit(choosenStorage);
		rr.save(r);

		reservations = rr.findAll();
		List<Reservation> ourReservations = new ArrayList<Reservation>();

		for (Reservation res : reservations)
			if (res.getUser().getUserId() == u.getUserId())
				ourReservations.add(res);

		request.getSession().setAttribute("reservations", ourReservations);
		return "/User/homepage";
	}

	private boolean datesOverlap(Reservation r, Date fromDate, Date toDate) {
		
		if (fromDate == null || toDate == null || r == null)
			return true;

		if ((r.getInDate().before(fromDate) && r.getOutDate().after(fromDate))
				|| (r.getInDate().after(fromDate) && r.getOutDate().before(toDate))
				|| (r.getInDate().after(fromDate) && r.getOutDate().after(toDate))
				|| (r.getInDate().before(fromDate) && r.getOutDate().after(toDate)))
			return true;

		return false;
	}

	
	@RequestMapping(value = "/getReservationBill.pdf", method = RequestMethod.GET)
	public void getReservationBill(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Reservation> r =  new ArrayList<>();
		Reservation res = (Reservation) request.getSession().getAttribute("reservation");
		res.setAcctualOutDate(new Date());
		rr.save(res);
		r.add(res);
		
		
		response.setContentType("text/html");
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(r);
		InputStream inputStream = this.getClass().getResourceAsStream("/jasperreports/reservationBill.jrxml");
		JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
//		Date inDate = (Date) request.getSession().getAttribute("inDate");
//		Date outDate = (Date) request.getSession().getAttribute("outDate");
//		String inDateStr = niceDate(inDate);
//		String outDateStr = niceDate(outDate);
//		String actualDateStr = niceDate(new Date());
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("todayDate", niceDate(new Date()));
		params.put("daysInUnit", request.getSession().getAttribute("daysInUnit"));
		params.put("unusedDays", request.getSession().getAttribute("unusedDays"));
		params.put("pricePerDay", request.getSession().getAttribute("pricePerDay"));
		params.put("usedDaysPrice", request.getSession().getAttribute("usedDaysPrice"));
		params.put("unusedDaysPrice", request.getSession().getAttribute("unusedDaysPrice"));
		params.put("totalPrice",request.getSession().getAttribute("totalPrice"));
		params.put("loadingDatum", request.getSession().getAttribute("inDate"));
		params.put("loadingDatum", request.getSession().getAttribute("outDate"));
//		params.put("inDateStr", inDateStr);
//		params.put("outDateStr", outDateStr);
//		params.put("actualDateStr", actualDateStr);
		
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
		inputStream.close();
		response.setContentType("application/x-download");
		response.addHeader("Content-disposition", "attachment; filename = ReservationBill.pdf");
		java.io.OutputStream out = response.getOutputStream();
		JasperExportManager.exportReportToPdfStream(jasperPrint, out);
	}
	
	
	@RequestMapping(value = "/getAllReservationsForUser", method = RequestMethod.GET)
	public String getAllReservationsForUser(Integer userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Reservation> reservations = rr.findAll();
		List<Reservation> userReservations = new ArrayList<>();
		for (Reservation r: reservations)
			if (r.getUser().getUserId() == userId)
				userReservations.add(r);
		
		request.getSession().setAttribute("reservations", userReservations);		
				
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/getActiveReservationsForUser", method = RequestMethod.GET)
	public String getActiveReservationsForUser(Integer userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Reservation> reservations = rr.findAll();
		List<Reservation> active = new ArrayList<>();
		for (Reservation r: reservations)
			if (r.getUser().getUserId() == userId && r.getAcctualOutDate() == null)
				active.add(r);
		
		request.getSession().setAttribute("reservations", active);		
				
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/getConcludedReservationsForUser", method = RequestMethod.GET)
	public String getConcludedReservationsForUser(Integer userId, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Reservation> reservations = rr.findAll();
		List<Reservation> concluded = new ArrayList<>();
		for (Reservation r: reservations)
			if (r.getUser().getUserId() == userId && r.getAcctualOutDate() != null)
				concluded.add(r);
		
		request.getSession().setAttribute("reservations", concluded);		
				
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/sortByCategory", method = RequestMethod.GET)
	public String sortByCategory(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				return l.getCategory().getCatId() - r.getCategory().getCatId();
			}
		});
		
		request.getSession().setAttribute("reservations", reservations);
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/sortByInDate", method = RequestMethod.GET)
	public String sortByInDate(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				if (r.getInDate().after(l.getInDate()))
					return -1;
				else if (r.getInDate().before(l.getInDate()))
					return 1;
				else 
					return 0;
			}
		});
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/sortByOutDate", method = RequestMethod.GET)
	public String sortByOutDate(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				if (r.getOutDate().after(l.getOutDate()))
					return -1;
				else if (r.getOutDate().before(l.getOutDate()))
					return 1;
				else 
					return 0;
			}
		});
		
		request.getSession().setAttribute("reservations", reservations);
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/sortByPricePerDay", method = RequestMethod.GET)
	public String sortByPricePerDay(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				if (l.getPricePerDay() - r.getPricePerDay() < 0)
					return -1;
				else if (l.getPricePerDay() - r.getPricePerDay() > 0)
					return 1;
				else 
					return 0;
			}
		});
		
		request.getSession().setAttribute("reservations", reservations);
		return "/User/homepage";
	}
	
	@RequestMapping(value = "/sortByEmptiedDate", method = RequestMethod.GET)
	public String sortByEmptiedDate(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				if (r.getAcctualOutDate()==null && l.getAcctualOutDate()==null)
					return 0;
				else if (r.getAcctualOutDate() == null && r.getAcctualOutDate() != null)
					return 1;
				else if (r.getAcctualOutDate() != null && l.getAcctualOutDate() == null)
					return -1;
				else if (r.getAcctualOutDate()!=null && l.getAcctualOutDate() != null){
					if (r.getAcctualOutDate().before(l.getAcctualOutDate()))
						return -1;
					else if (r.getAcctualOutDate().after(l.getAcctualOutDate()))
						return 1;
					else 
						return 0;
				}
				else 
					return 0;
			}
		});
		
		request.getSession().setAttribute("reservations", reservations);
		return "/User/homepage";
	}
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}
	
	@SuppressWarnings("deprecation")
	private String niceDate(Date d) {
		return (d.getMonth() + 1) + "." + d.getDate() + "." + (d.getYear() + 1900);
	}
	
	private int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
}
	
}
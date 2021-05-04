package com.example.demo.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.repository.StorageUnitRepository;
import com.example.demo.repository.LotRepository;
import com.example.demo.repository.ReservationRepository;

import model.Lot;
import model.Reservation;
import model.StorageUnit;

@Controller
@RequestMapping(value = "/lot")
public class LotController {

	@Autowired
	LotRepository lr;

	@Autowired
	ReservationRepository rr;

	@Autowired
	StorageUnitRepository sr;

	@RequestMapping(value = "/saveLot", method = RequestMethod.POST)
	public String saveLot(@Valid Lot l, HttpServletRequest request, Errors e) {

		boolean ok = true;
		
		if (l.getLotName().isBlank() || l.getLotName() == null) {
			request.setAttribute("lotNameErrorMsg", "Lot name must not be left blank");
			ok = false;
		}
		
		if (l.getNumberOfStorageUnits() <= 0) {
			request.setAttribute("numberOfStorageUnitsErrorMsg", "Number of storage units cannot be zero or negative value");
			ok = false;		
		}
		
		if (l.getStoragePrice() <= 0) {
			request.setAttribute("storagePriceErrorMsg", "Price cannot be zero or negative number");
			ok = false;		
		}
		
		if (!ok)
			return "/Admin/homepage";
		
		if (e.hasErrors()) {
			request.setAttribute("saveLotErrorMsg", "All fields must be filled.");
			return "/Admin/homepage";
		}
		lr.save(l);
		ok = sr.addUnits(l);

		if (!ok) {
			lr.delete(l);
			request.setAttribute("errorMessage",
					"Lot has not been added because it was not possible to add storageUnits");
			return "error";
		}

		List<Lot> lots = lr.findAll();
		request.getSession().setAttribute("lots", lots);

		return "/Admin/homepage";
	}

	@RequestMapping(value = "/checkLot", method = RequestMethod.GET)
	public String checkLot(Integer lotId, HttpServletRequest request) {

		Lot l = lr.findById(lotId).get();
		List<Reservation> reservations = rr.findAll();

		List<Reservation> ourReservations = new ArrayList<>();

		for (Reservation r : reservations)
			if (r.getLot().getLotId() == l.getLotId())
				ourReservations.add(r);

		request.getSession().setAttribute("lot", l);
		request.getSession().setAttribute("reservations", ourReservations);

		return "/Admin/checkLots";
	}

	@RequestMapping(value = "/checkActiveReservations", method = RequestMethod.GET)
	public String checkActiveReservations(HttpServletRequest request) {

		@SuppressWarnings("unchecked")
		List<Reservation> activeReservations = (List<Reservation>) request.getSession()
				.getAttribute("activeReservations");

		request.getSession().setAttribute("reservations", activeReservations);

		return "/Admin/checkLots";

	}

	@RequestMapping(value = "/refreshReservationsForLot", method = RequestMethod.GET)
	public String refreshReservationsForLot(HttpServletRequest request) {

		List<Reservation> reservations = rr.findAll();
		List<Reservation> myReservations = new ArrayList<>();
		Lot lot = (Lot) request.getSession().getAttribute("lot");

		for (Reservation r : reservations)
			if (r.getLot().getLotId() == lot.getLotId())
				myReservations.add(r);

		request.getSession().setAttribute("reservations", myReservations);

		return "Admin/checkLots";
	}

	@RequestMapping(value = "/modifyRouter", method = RequestMethod.GET)
	public String modifyRouter(Integer lotId, HttpServletRequest request) {
		Lot l = lr.findById(lotId).get();
		request.getSession().setAttribute("lot", l);
		return "/Admin/modifyLot";
	}

	@RequestMapping(value = "/modifyLot", method = RequestMethod.POST)
	public String modifyLot(String newName, Integer numberOfNewUnits, Integer newPrice, HttpServletRequest request) {

		Lot l = (Lot) request.getSession().getAttribute("lot");
		l = lr.findById(l.getLotId()).get();

		if (newName != null && (!l.getLotName().equals(newName)))
			l.setLotName(newName);

		if (newPrice != null && newPrice > 0) {
			l.setStoragePrice(newPrice);
		}

		lr.persistLot(l);

		if (numberOfNewUnits != null && numberOfNewUnits > 0) {
			sr.addAdditionalUnits(l, numberOfNewUnits);
		}

		List<Lot> lots = lr.findAll();
		request.getSession().setAttribute("lots", lots);

		return "/Admin/homepage";
	}

	@RequestMapping(value = "/browseLots", method = RequestMethod.POST)
	public String browseLots(Date fromDate, Date toDate, String kwds, HttpServletRequest request) {

		Lot lot = (Lot) request.getSession().getAttribute("lot");
		List<Reservation> reservations = rr.findAll();
		List<Reservation> ourReservations = new ArrayList<>();
		
		request.getSession().setAttribute("fromDate", fromDate);
		request.getSession().setAttribute("fromDate", toDate);
		
		for (Reservation r : reservations) {

			if (lot.getLotId() != r.getLot().getLotId())
				continue;

			if (fromDate != null)
				if (r.getOutDate().before(fromDate))
					continue;

			if (toDate != null)
				if (r.getInDate().after(toDate))
					continue;
			ourReservations.add(r);
		}

		int occupiedUnits = 0;
		int freeUnits = 0;
		List<StorageUnit> ourStorageUnits = new ArrayList<>();
		for (StorageUnit u : sr.findAll())
			if (u.getLot().getLotId() == lot.getLotId())
				ourStorageUnits.add(u);

		List<Reservation> activeReservations = new ArrayList<>();

		for (StorageUnit u : ourStorageUnits) {
			boolean ok = true;

			for (Reservation r : ourReservations) {
				if (r.getStorageUnit().getUnitId() == u.getUnitId())
					if (isBetweenDates(r, fromDate, toDate)) {
						ok = false;
						occupiedUnits++;
						activeReservations.add(r);
					}
			}

			if (ok)
				freeUnits++;
		}

		String freeUnitsMsg = null;
		if (fromDate != null && toDate != null)
			freeUnitsMsg = "Number of free units between " + niceDate(fromDate) + " and " + niceDate(toDate) + " is "
					+ freeUnits;
		else if (fromDate == null && toDate != null)
			freeUnitsMsg = "Number of unusued units up until " + niceDate(toDate) + " is " + freeUnits;
		else if (fromDate != null && toDate == null)
			freeUnitsMsg = "Number of unused free units after " + niceDate(fromDate) + " is " + freeUnits;
		else
			freeUnitsMsg = "Number of units that have no reservations at all is " + freeUnits;

		String occupiedUnitsMsg = "Number of active reservations in this period is " + occupiedUnits;

		request.getSession().setAttribute("activeReservations", activeReservations);

		request.setAttribute("messageAboutFreeUnits", freeUnitsMsg);
		request.setAttribute("messageAboutOccupiedUnits", occupiedUnitsMsg);

		// At last we filter it with kwds
		List<Reservation> finalReservations = new ArrayList<>();
		if (kwds != null)
			for (Reservation r : ourReservations)
				if (r.getDescription().toUpperCase().contains(kwds.toUpperCase()))
					finalReservations.add(r);

		request.getSession().setAttribute("reservations", finalReservations);
		return "Admin/checkLots";
	}

	private boolean isBetweenDates(Reservation r, Date fromDate, Date toDate) {
		if (fromDate != null && toDate != null)
			if ((r.getInDate().before(fromDate) && r.getOutDate().after(fromDate))
					|| (r.getInDate().after(fromDate) && r.getOutDate().before(toDate))
					|| (r.getInDate().after(fromDate) && r.getOutDate().after(toDate))
					|| (r.getInDate().before(fromDate) && r.getOutDate().after(toDate)))
				return true;

		if (fromDate == null && toDate != null) {
			if (r.getInDate().before(toDate))
				return true;
		}
		if (toDate == null && fromDate != null)
			if (r.getOutDate().after(fromDate))
				return true;

		return false;

	}

	@SuppressWarnings("deprecation")
	private String niceDate(Date d) {
		return (d.getMonth() + 1) + "." + d.getDate() + "." + (d.getYear() + 1900);
	}

	@RequestMapping(value = "/getActiveReservations", method = RequestMethod.GET)
	public String getActive(int lotId, HttpServletRequest request) {
		
		List<Reservation> reservations = rr.findAll();
		List<Reservation> active = new ArrayList<>();
		
		for (Reservation r: reservations)
			if (r.getLot().getLotId() == lotId && r.getAcctualOutDate() == null)
				active.add(r);
		
		request.getSession().setAttribute("reservations", active);
		return "/Admin/checkLots";
	}
	
	@RequestMapping(value = "/getConcludedReservations", method = RequestMethod.GET)
	public String getConcluded(int lotId, HttpServletRequest request) {
		
		List<Reservation> reservations = rr.findAll();
		List<Reservation> concluded = new ArrayList<>();
		
		for (Reservation r: reservations)
			if (r.getLot().getLotId() == lotId && r.getAcctualOutDate() != null)
				concluded.add(r);
		
		request.getSession().setAttribute("reservations", concluded);
		return "/Admin/checkLots";
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
		return "/Admin/checkLots";
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
		
		request.getSession().setAttribute("reservations", reservations);
		return "/Admin/checkLots";
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
		return "/Admin/checkLots";
	}
	
	@RequestMapping(value = "/sortByOwner", method = RequestMethod.GET)
	public String sortByOwner(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				int cmp = l.getUser().getLastName().compareTo(r.getUser().getLastName());
				
				if (cmp < 0) return cmp * (-1);
				else if (cmp > 0) return cmp * (-1);
				else {
					cmp = l.getUser().getFirstName().compareTo(r.getUser().getFirstName());
					return cmp * (-1);
				}
			}
		});
		
		request.getSession().setAttribute("reservations", reservations);
		return "/Admin/checkLots";
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
		return "/Admin/checkLots";
	}
	
	
	@RequestMapping(value = "/sortByStatus", method = RequestMethod.GET)
	public String sortByStatus(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Reservation> reservations = (List<Reservation>) request.getSession().getAttribute("reservations");
		
		Collections.sort(reservations, new Comparator<Reservation>() {
			public int compare(Reservation l, Reservation r) {
				
				if (r.getAcctualOutDate()!= null && l.getAcctualOutDate() == null)
					return -1;
				else if (r.getAcctualOutDate() == null && l.getAcctualOutDate() != null)
					return 1;
				else 
					return 0;
			}
		});
		
		request.getSession().setAttribute("reservations", reservations);
		return "/Admin/checkLots";
	}
	
	@RequestMapping(value = "/sortByLotName", method = RequestMethod.GET)
	public String sortByLotName(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Lot> lots = (List<Lot>) request.getSession().getAttribute("lots");
		
		Collections.sort(lots, new Comparator<Lot>() {
			public int compare(Lot l, Lot r) {
					return l.getLotName().compareTo(r.getLotName());
				}
		});
		
		request.getSession().setAttribute("lots", lots);
		return "/Admin/homepage";
	}

	
	@RequestMapping(value = "/sortByStoragePrice", method = RequestMethod.GET)
	public String sortByStoragePrice(HttpServletRequest request) {
		
		@SuppressWarnings("unchecked")
		List<Lot> lots = (List<Lot>) request.getSession().getAttribute("lots");
		
		Collections.sort(lots, new Comparator<Lot>() {
			public int compare(Lot l, Lot r) {
					return l.getStoragePrice() - r.getStoragePrice();
				}
		});
		
		request.getSession().setAttribute("lots", lots);
		return "/Admin/homepage";
	}
	
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(true);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
	}
}

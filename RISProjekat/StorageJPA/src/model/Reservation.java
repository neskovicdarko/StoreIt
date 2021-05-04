package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the Reservations database table.
 * 
 */
@Entity
@Table(name="Reservations")
@NamedQuery(name="Reservation.findAll", query="SELECT r FROM Reservation r")
public class Reservation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int reservationId;

	@Temporal(TemporalType.DATE)
	private Date acctualOutDate;

	private String description;

	@Temporal(TemporalType.DATE)
	private Date inDate;

	@Temporal(TemporalType.DATE)
	private Date outDate;

	private float pricePerDay;

	//bi-directional many-to-one association to Category
	@ManyToOne
	@JoinColumn(name="catId")
	private Category category;

	//bi-directional many-to-one association to Lot
	@ManyToOne
	@JoinColumn(name="lotId")
	private Lot lot;

	//bi-directional many-to-one association to StorageUnit
	@ManyToOne
	@JoinColumn(name="unitId")
	private StorageUnit storageUnit;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="userId")
	private User user;

	public Reservation() {
	}

	public int getReservationId() {
		return this.reservationId;
	}

	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	public Date getAcctualOutDate() {
		return this.acctualOutDate;
	}

	public void setAcctualOutDate(Date acctualOutDate) {
		this.acctualOutDate = acctualOutDate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getInDate() {
		return this.inDate;
	}

	public void setInDate(Date inDate) {
		this.inDate = inDate;
	}

	public Date getOutDate() {
		return this.outDate;
	}

	public void setOutDate(Date outDate) {
		this.outDate = outDate;
	}

	public float getPricePerDay() {
		return this.pricePerDay;
	}

	public void setPricePerDay(float pricePerDay) {
		this.pricePerDay = pricePerDay;
	}

	public Category getCategory() {
		return this.category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Lot getLot() {
		return this.lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

	public StorageUnit getStorageUnit() {
		return this.storageUnit;
	}

	public void setStorageUnit(StorageUnit storageUnit) {
		this.storageUnit = storageUnit;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
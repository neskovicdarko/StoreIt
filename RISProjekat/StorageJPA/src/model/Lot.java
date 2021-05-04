package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the Lot database table.
 * 
 */
@Entity
@NamedQuery(name="Lot.findAll", query="SELECT l FROM Lot l")
public class Lot implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int lotId;

	private String lotName;

	private int numberOfStorageUnits;

	private int storagePrice;

	//bi-directional many-to-one association to Reservation
	@OneToMany(mappedBy="lot")
	private List<Reservation> reservations;

	//bi-directional many-to-one association to StorageUnit
	@OneToMany(mappedBy="lot")
	private List<StorageUnit> storageUnits;

	public Lot() {
	}

	public int getLotId() {
		return this.lotId;
	}

	public void setLotId(int lotId) {
		this.lotId = lotId;
	}

	public String getLotName() {
		return this.lotName;
	}

	public void setLotName(String lotName) {
		this.lotName = lotName;
	}

	public int getNumberOfStorageUnits() {
		return this.numberOfStorageUnits;
	}

	public void setNumberOfStorageUnits(int numberOfStorageUnits) {
		this.numberOfStorageUnits = numberOfStorageUnits;
	}

	public int getStoragePrice() {
		return this.storagePrice;
	}

	public void setStoragePrice(int storagePrice) {
		this.storagePrice = storagePrice;
	}

	public List<Reservation> getReservations() {
		return this.reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	public Reservation addReservation(Reservation reservation) {
		getReservations().add(reservation);
		reservation.setLot(this);

		return reservation;
	}

	public Reservation removeReservation(Reservation reservation) {
		getReservations().remove(reservation);
		reservation.setLot(null);

		return reservation;
	}

	public List<StorageUnit> getStorageUnits() {
		return this.storageUnits;
	}

	public void setStorageUnits(List<StorageUnit> storageUnits) {
		this.storageUnits = storageUnits;
	}

	public StorageUnit addStorageUnit(StorageUnit storageUnit) {
		getStorageUnits().add(storageUnit);
		storageUnit.setLot(this);

		return storageUnit;
	}

	public StorageUnit removeStorageUnit(StorageUnit storageUnit) {
		getStorageUnits().remove(storageUnit);
		storageUnit.setLot(null);

		return storageUnit;
	}

}
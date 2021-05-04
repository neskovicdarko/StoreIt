package model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the StorageUnits database table.
 * 
 */
@Entity
@Table(name="StorageUnits")
@NamedQuery(name="StorageUnit.findAll", query="SELECT s FROM StorageUnit s")
public class StorageUnit implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int unitId;

	private int unitNum;

	//bi-directional many-to-one association to Reservation
	@OneToMany(mappedBy="storageUnit")
	private List<Reservation> reservations;

	//bi-directional many-to-one association to Lot
	@ManyToOne
	@JoinColumn(name="lotId")
	private Lot lot;

	public StorageUnit() {
	}

	public int getUnitId() {
		return this.unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public int getUnitNum() {
		return this.unitNum;
	}

	public void setUnitNum(int unitNum) {
		this.unitNum = unitNum;
	}

	public List<Reservation> getReservations() {
		return this.reservations;
	}

	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

	public Reservation addReservation(Reservation reservation) {
		getReservations().add(reservation);
		reservation.setStorageUnit(this);

		return reservation;
	}

	public Reservation removeReservation(Reservation reservation) {
		getReservations().remove(reservation);
		reservation.setStorageUnit(null);

		return reservation;
	}

	public Lot getLot() {
		return this.lot;
	}

	public void setLot(Lot lot) {
		this.lot = lot;
	}

}
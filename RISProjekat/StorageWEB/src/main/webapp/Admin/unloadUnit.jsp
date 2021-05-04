<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/Storage/Resources/Css/mainCss.css">
<meta charset="UTF-8">
<title>Unload unit</title>
<link rel = "icon" href =  
"/Storage/Resources/Images/box.png" 
        type = "image/x-icon"> 
</head>
<body>
	<table>
		<tr>
			<td>
				<table id="tabela">
					<tr id="tabela">
						<td id="tabela">Unit lot</td>
						<td id="tabela">${reservation.lot.lotName}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Unit number</td>
						<td id="tabela">${reservation.storageUnit.unitNum}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Date of loading</td>
						<td id="tabela">${inDate}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Unloading Date</td>
						<td id="tabela">${outDate}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Today</td>
						<td id="tabela">${today}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Days in unit</td>
						<td id="tabela">${daysInUnit}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Unused days</td>
						<td id="tabela">${unusedDays}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Price per day</td>
						<td id="tabela">${pricePerDay}</td>
					</tr>

				</table>
			</td>
			<td>
				<table id="tabela">
					<tr id="tabela">
						<td id="tabela">Price for used days (${daysInUnit})</td>
						<td id="tabela">${daysInUnit}*${reservation.pricePerDay}</td>
						<td id="tabela">${usedDaysPrice}$</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Compensation for unused days (${unusedDays})</td>
						<td id="tabela">${unusedDays}*${reservation.pricePerDay}/2</td>
						<td id="tabela">${unusedDaysPrice}$</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">Multiplicator for category</td>
						<td id="tabela">${reservation.category.catName}</td>
						<td id="tabela">* ${reservation.category.catMultiplicator}</td>
					</tr>
					<tr id="tabela">
						<td id="tabela">TotalPrice</td>
						<td id="tabela">${unusedDaysPrice}+${usedDaysPrice}</td>
						<td id="tabela">${totalPrice}$</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>

	<hr>
	<br>
	<br>
	<table>
		<tr>
			<td><a href="/Storage/reservation/getReservationBill.pdf">
					Unload </a></td>
			<td><a
				href="/Storage/lot/checkLot?lotId=${reservation.lot.lotId}">Back</a>
			</td>
		</tr>
	</table>
</body>
</html>
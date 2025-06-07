DROP INDEX IF EXISTS username_and_password, flightdate, flightnumber_and_flightdate, flight_departure_arrival, flightnumber, reservation_customerid, customer_customerid, repair_planeid_repairdate, technicianid, maintenance_pilotid, schedule_dayofweek, reservation_customerid;

CREATE INDEX username_and_password ON Users(username, password);
CREATE INDEX flightdate ON FlightInstance(FlightDate);
CREATE INDEX flightnumber_and_flightdate ON FlightInstance(FlightNumber, FlightDate);
CREATE INDEX flight_departure_arrival ON Flight(DepartureCity, ArrivalCity);
CREATE INDEX schedule_dayofweek ON Schedule(DayOfWeek, FlightNumber);
CREATE INDEX reservationID ON Reservation(ReservationID);

CREATE INDEX flightnumber ON Flight(FlightNumber);
CREATE INDEX reservation_customerid ON Reservation(CustomerID);
CREATE INDEX customer_customerid ON Customer(CustomerID);
CREATE INDEX repair_planeid_repairdate ON Repair(PlaneID, RepairDate);
CREATE INDEX technicianid ON Repair(TechnicianID);
CREATE INDEX maintenance_pilotid ON MaintenanceRequest(PilotID);







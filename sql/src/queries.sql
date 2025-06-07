SELECT Users.role From Users Where username = 'alex.murphy' AND password = 'sunset42';

SELECT * FROM FlightInstance WHERE FlightDate = '2025-05-05';

SELECT s.FlightNumber, s.DepartureTime, s.ArrivalTime, i.NumOfStops, (COUNT(CASE WHEN ArrivedOnTime = True THEN 1 END) * 1.0 / COUNT(*))*100 AS onTimePercentage FROM Flight f join Schedule s on s.FlightNumber = f.FlightNumber join FlightInstance i on i.FlightNumber = f.FlightNumber WHERE f.DepartureCity = 'New York'  AND f.ArrivalCity = 'Miami'  AND i.FlightDate = '2025-05-05' AND s.DayOfWeek = 'Monday' GROUP BY s.FlightNumber, s.DepartureTime, s.ArrivalTime, i.NumOfStops;

SELECT * FROM Customer c join Reservation r on c.CustomerID = r.customerID WHERE r.ReservationID = 'R0001';

SELECT * FROM FlightInstance WHERE FlightDate = '2025-05-05';
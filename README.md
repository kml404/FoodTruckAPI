# FoodTruckAPI

This project is a REST API that can perform various basic operations on the food truck data set. Upon creation it fetches all the data into a Hibernate data set (presently in-memory) and can perform some basic functions. The number of available endpoints is limited since there were no specifications regarding what kind of operations it needs to be optimized for, but it wouldn't be difficult to add features (e.g. an endpoint to return all trucks whose expiration date is less than a month away).

The data for each vehicle (be it a truck, push cart, or of unspecified type) are stored in the `Truck` class, which is equipped with standard getters and setters via Lombok, but has no methods at this time. Specific methods could be added if the specs required it. All the truck objects are stored in a JPA repository supported by the `TruckRepository` class.

Below is a summary of the endpoints:

*api/details/{id}* (GET)

Return a POJO object with the details about a vehicle with a given `id` (or a 404 status if it isn't found).

*api/list/{vehicleType/{status}* (GET)

Returns a JSON-based list of vehicles based on type and status. Both parameters are optional - omitting one or using the "all" flag will return all vehicles without regard to the parameter's value. For `vehicleType` one can use the flags "truck" and "cart", while for `status` there are "requested", "approved", and "expired."

*api/list/{vehicleType/{status}* (POST)

Absent a post body, this endpoint functions like its "GET" equivalent. However, one can post a JSON object with certain predefined flags to add more granularity to the results. All strings are case-insensitive

 `hasSchedule` (boolean) - return one those that have a schedule link (default: false)  
 `blocks` (list of strings) - return only those on specified blocks  
 `lots` (list of strings) - return only those on specified lots  
 `applicant` (string) - query for applicant names  
 `food` (string) - query for food items  
 `location` (string) - query for address or location description fields

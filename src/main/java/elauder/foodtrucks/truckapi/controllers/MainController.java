package elauder.foodtrucks.truckapi.controllers;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import elauder.foodtrucks.truckapi.entities.Truck;
import elauder.foodtrucks.truckapi.enums.FacilityType;
import elauder.foodtrucks.truckapi.enums.PermitStatus;
import elauder.foodtrucks.truckapi.repositories.TruckRepository;

@CrossOrigin(origins = "*")
@RestController
@ResponseBody
@RequestMapping("/api")
public class MainController {
    private static final String DATASET_URL = "https://data.sfgov.org/api/views/rqzj-sfat/rows.csv?accessType=DOWNLOAD";

    @Autowired
    private TruckRepository truckRepo;

    public MainController(TruckRepository trucks) {

        this.truckRepo = trucks;

        DateTimeFormatter dateFormatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss a"))
                .appendOptional(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"))
                .appendOptional(DateTimeFormatter.ofPattern("yyyyMMdd"))
                .toFormatter();

        try (CSVParser csvParser = CSVParser.parse((new URL(DATASET_URL)),
                StandardCharsets.UTF_8, CSVFormat.DEFAULT.builder().setHeader().build())) {
            csvParser.getRecords().forEach(entry -> {
                Truck currentTruck = Truck.builder().locationId(Long.parseLong(entry.get("locationid")))
                        .applicant(entry.get("Applicant"))
                        .cnn(Integer.parseInt(entry.get("cnn")))
                        .locationDescription(entry.get("LocationDescription"))
                        .address(entry.get("Address")).blocklot(entry.get("blocklot")).block(entry.get("block"))
                        .lot(entry.get("lot"))
                        .permit(entry.get("permit"))
                        .status(PermitStatus.valueOf(entry.get("Status").toUpperCase()))
                        .foodItems(entry.get("FoodItems"))
                        .latitude(Double.parseDouble(entry.get("Latitude")))
                        .longitude(Double.parseDouble(entry.get("Longitude")))
                        .schedule(entry.get("Schedule")).daysHours(entry.get("dayshours"))
                        .priorPermit(Integer.parseInt(entry.get("PriorPermit")))
                        .location(entry.get("Location"))
                        .zipCodes(entry.get("Zip Codes")).build();

                if (!entry.get("FacilityType").isBlank()) {
                    currentTruck.setFacilityType(FacilityType.valueOf(
                            String.join("_", entry.get("FacilityType").toUpperCase().split(" "))));
                }

                if (!entry.get("X").isBlank()) {
                    currentTruck.setX(Double.parseDouble(entry.get("X")));
                }
                if (!entry.get("Y").isBlank()) {
                    currentTruck.setY(Double.parseDouble(entry.get("Y")));
                }

                if (!entry.get("Fire Prevention Districts").isBlank()) {
                    currentTruck.setFirePreventionDistricts(Integer.parseInt(entry.get("Fire Prevention Districts")));
                }
                if (!entry.get("Police Districts").isBlank()) {
                    currentTruck.setPoliceDistricts(Integer.parseInt(entry.get("Police Districts")));
                }
                if (!entry.get("Supervisor Districts").isBlank()) {
                    currentTruck.setSupervisorDistricts(Integer.parseInt(entry.get("Supervisor Districts")));
                }
                if (!entry.get("Neighborhoods (old)").isBlank()) {
                    currentTruck.setNeighborhoods(Integer.parseInt(entry.get("Neighborhoods (old)")));
                }

                try {
                    currentTruck.setNoiSent(Date.from(LocalDate.parse(entry.get("NOISent"), dateFormatter)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
                try {
                    currentTruck.setApproved(Date.from(LocalDate.parse(entry.get("Approved"), dateFormatter)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
                try {
                    currentTruck.setReceived(Date.from(LocalDate.parse(entry.get("Received"), dateFormatter)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }
                try {
                    currentTruck.setExpirationDate(Date.from(LocalDate.parse(entry.get("ExpirationDate"), dateFormatter)
                            .atStartOfDay(ZoneId.systemDefault()).toInstant()));
                } catch (DateTimeParseException e) {
                    e.printStackTrace();
                }

                try {
                    truckRepo.save(currentTruck);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Get the details of a truck by its ID or return not found */
    @GetMapping(value = "/details/{id}")
    public ResponseEntity<Truck> truckDetails(@PathVariable(name = "id") Long locationId) {
        return ResponseEntity.of(truckRepo.findById(locationId));
    }

    /*
     * Returns a list of all vehicles, possibly filtered by type ("all" for all,
     * "truck" for trucks, and "cart" for pushcarts),
     * and statuses ("all" denotes all; "approved", "requested", and "expired"
     * denote specific values)
     */
    @GetMapping(value = { "/list", "/list/{vehicleType}", "/list/{vehicleType}/{status}" })
    public ResponseEntity<List<Truck>> listVehicles(@PathVariable(name = "vehicleType") Optional<String> vehicleType,
            @PathVariable(name = "status") Optional<String> status) {
        Truck boilerPlateTruck = Truck.builder().build();

        switch (vehicleType.orElse("all").toLowerCase()) {
            case "all":
                break;
            case "truck":
                boilerPlateTruck.setFacilityType(FacilityType.TRUCK);
                break;
            case "cart":
                boilerPlateTruck.setFacilityType(FacilityType.PUSH_CART);
                break;
            default:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!status.orElse("all").equalsIgnoreCase("all")) {
            try {
                boilerPlateTruck.setStatus(PermitStatus.valueOf(status.orElse("all").toUpperCase()));
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }

        return ResponseEntity.of(Optional.of(truckRepo.findAll(Example.of(boilerPlateTruck))));
    }

    /*
     * filters is a JSON object supporting the following filters:
     * hasSchedule (Boolean) - return only those that have a schedule link (default:
     * false)
     * blocks (List<String>) - return only those on specified blocks
     * lots (List<String>) - return only those on specified blocks
     * applicant (String) - query for applicant names (case-insensitive)
     * food (String) - query for food item names (case-insensitive)
     * location (String) - query for address or location description
     * (case-insensitive)
     */
    @PostMapping(value = { "/list", "/list/{vehicleType}", "/list/{vehicleType}/{status}" })
    public ResponseEntity<List<Truck>> listVehiclesFiltered(
            @PathVariable(name = "vehicleType") Optional<String> vehicleType,
            @PathVariable(name = "status") Optional<String> status, @RequestBody JSONObject filters) {
        ResponseEntity<List<Truck>> rawListEntity = listVehicles(vehicleType, status);

        List<Truck> filteredList = rawListEntity.getBody();

        // If there are problems with the unfiltered list, return the problem response
        if ((rawListEntity.getStatusCode() != HttpStatus.OK) || (filteredList == null)) {
            return rawListEntity;
        }

        // Process the filters
        if (filters.optBoolean("hasSchedule", false)) {
            filteredList = filteredList.stream().filter(item -> !item.getSchedule().isBlank())
                    .collect(Collectors.toList());
        }

        JSONArray rawBlockList = filters.optJSONArray("blocks");
        JSONArray rawLotList = filters.optJSONArray("lots");

        List<String> blockList = Collections.emptyList();
        List<String> lotList = Collections.emptyList();

        for (int i = 0; i < rawBlockList.length(); i++) {
            blockList.add(rawBlockList.optString(i, ""));
        }
        for (int i = 0; i < rawLotList.length(); i++) {
            lotList.add(rawLotList.optString(i, ""));
        }

        if (!blockList.isEmpty()) {
            filteredList = filteredList.stream().filter(item -> blockList.contains(item.getBlock()))
                    .collect(Collectors.toList());
        }
        if (!lotList.isEmpty()) {
            filteredList = filteredList.stream().filter(item -> lotList.contains(item.getLot()))
                    .collect(Collectors.toList());
        }

        String applicantQuery = filters.optString("applicant", "").trim();
        String foodQuery = filters.optString("food", "").trim();
        String locationQuery = filters.optString("location").trim();

        filteredList = filteredList.stream().filter(item -> (item.getApplicant().indexOf(applicantQuery) > -1) &&
                (item.getFoodItems().indexOf(foodQuery) > -1) &&
                ((item.getAddress().indexOf(locationQuery) > -1) ||
                        (item.getLocationDescription().indexOf(locationQuery) > -1)))
                .collect(Collectors.toList());

        return ResponseEntity.of(Optional.of(filteredList));
    }
}

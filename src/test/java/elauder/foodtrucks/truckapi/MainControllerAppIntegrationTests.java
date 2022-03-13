package elauder.foodtrucks.truckapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.text.SimpleDateFormat;

import javax.transaction.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import elauder.foodtrucks.truckapi.entities.Truck;
import elauder.foodtrucks.truckapi.enums.FacilityType;
import elauder.foodtrucks.truckapi.repositories.TruckRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MainControllerAppIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TruckRepository truckRepository;

	private ObjectMapper objectMapper;

	private MainControllerAppIntegrationTests() {
		objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
	}

	@Test
	void listAll() throws Exception {
		mockMvc.perform(get("/api/list"))
				.andExpect(content()
						.json(objectMapper.writeValueAsString(truckRepository.findAll())));
	}

	@Test
	void listAllTrucks() throws Exception {
		mockMvc.perform(get("/api/list/truck"))
				.andExpect(content()
						.json(objectMapper.writeValueAsString(truckRepository.findAll(Example.of(Truck.builder()
								.facilityType(FacilityType.TRUCK).build())))));
	}

	@Test
	void listAllCarts() throws Exception {
		mockMvc.perform(get("/api/list/cart"))
				.andExpect(content()
						.json(objectMapper.writeValueAsString(truckRepository.findAll(Example.of(Truck.builder()
								.facilityType(FacilityType.PUSH_CART).build())))));
	}

	@Test
	void getExistingVehicle() throws Exception {
		mockMvc.perform(get("/api/details/{id}", 1565414)).andExpectAll(status().isOk(),
				content().json(objectMapper.writeValueAsString(truckRepository.getById(1565414L))));
	}

	@Test
	void failOnNonexistingVehicle() throws Exception {
		mockMvc.perform(get("/api/details/{id}", 666)).andExpect(status().isNotFound());
	}
}

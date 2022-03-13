package elauder.foodtrucks.truckapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import elauder.foodtrucks.truckapi.entities.Truck;

@Repository
public interface TruckRepository extends JpaRepository<Truck, Long> {

}

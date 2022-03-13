package elauder.foodtrucks.truckapi.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import elauder.foodtrucks.truckapi.enums.FacilityType;
import elauder.foodtrucks.truckapi.enums.PermitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer" })
@Table(name = "trucks")
public class Truck {
    @Id
    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "applicant")
    private String applicant;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FacilityType facilityType;

    @Column(name = "cnn")
    private Integer cnn;

    @Column(name = "location_description")
    private String locationDescription;

    @Column(name = "address")
    private String address;

    @Column(name = "blocklot")
    private String blocklot;

    @Column(name = "block")
    private String block;

    @Column(name = "lot")
    private String lot;

    @Column(name = "permit")
    private String permit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PermitStatus status;

    @Column(name = "food")
    private String foodItems;

    @Column(name = "x")
    private Double x;

    @Column(name = "y")
    private Double y;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "schedule")
    private String schedule;

    @Column(name = "days_hours")
    private String daysHours;

    @Temporal(TemporalType.DATE)
    @Column(name = "noi_sent")
    private Date noiSent;

    @Temporal(TemporalType.DATE)
    @Column(name = "approved")
    private Date approved;

    @Temporal(TemporalType.DATE)
    @Column(name = "received")
    private Date received;

    @Column(name = "prior_permit")
    private Integer priorPermit;

    @Temporal(TemporalType.DATE)
    @Column(name = "exp_dat")
    private Date expirationDate;

    @Column(name = "location")
    private String location;

    @Column(name = "fire_district")
    private Integer firePreventionDistricts;

    @Column(name = "police_district")
    private Integer policeDistricts;

    @Column(name = "supervisor_district")
    private Integer supervisorDistricts;

    @Column(name = "zip_codes")
    private String zipCodes;

    @Column(name = "neighborhoods")
    private Integer neighborhoods;
}

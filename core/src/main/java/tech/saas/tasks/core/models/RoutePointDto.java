package tech.saas.tasks.core.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoutePointDto implements TaskPayload {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("car_supply_ranges")
    private List<CarSupplyRange> carSupplyRanges;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("cargo_height")
    private Float cargoHeight;

    @JsonProperty("cargo_length")
    private Float cargoLength;

    @JsonProperty("cargo_places")
    private Integer cargoPlaces;

    @JsonProperty("cargo_volume")
    private Float cargoVolume;

    @JsonProperty("cargo_tonnage")
    private Float cargoTonnage;

    @JsonProperty("cargo_packing")
    private String cargoPacking;

    @JsonProperty("cargo_description")
    private String cargoDescription;

    @JsonProperty("additional_services")
    private List<AdditionalService> additionalServices;

    @JsonProperty("contacts")
    private List<Contact> contacts;

    @JsonProperty("counter_agent")
    private CounterAgent counterAgent;

    @JsonProperty("cargo_receiver_sender_address")
    private String cargoReceiverSenderAddress;

    @JsonProperty("cargo_receiver_sender_info")
    private CargoReceiverSenderInfo cargoReceiverSenderInfo;


    @Getter
    @Setter
    public static class CarSupplyRange {
        @JsonProperty("from")
        private OffsetDateTime from;

        @JsonProperty("till")
        private OffsetDateTime till;
    }

    @Getter
    @Setter
    public static class AdditionalService {
        @JsonProperty("id")
        private UUID id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("count")
        private Integer count;

        @JsonProperty("price")
        private Integer price;
    }

    @Getter
    @Setter
    public static class Contact {
        @JsonProperty("full_name")
        private String fullName;

        @JsonProperty("phone")
        private String phone;

        @JsonProperty("extension_number")
        private String extensionNumber;

        @JsonProperty("passport_number")
        private String passportNumber;

        @JsonProperty("passport_issued_at")
        private OffsetDateTime passportIssuedAt;

        @JsonProperty("passport_who_issued")
        private String passportWhoIssued;
    }

    @Getter
    @Setter
    public static class CounterAgent {

        @JsonProperty("private_person")
        private Boolean privatePerson;
        @JsonProperty("legal_entity_title")
        private String legalEntityTitle;
        @JsonProperty("legal_entity_inn")
        private String legalEntityInn;
        @JsonProperty("legal_entity_kpp")
        private String legalEntityKpp;
        @JsonProperty("legal_entity_opf_name")
        private String legalEntityOpfName;

        @JsonProperty("private_person_full_name")
        private String privatePersonFullName;
        @JsonProperty("private_person_passport_type")
        private String privatePersonPassportType;
        @JsonProperty("private_person_passport_number")
        private String privatePersonPassportNumber;
        @JsonProperty("private_person_passport_issuer")
        private String privatePersonPassportIssuer;
        @JsonProperty("private_person_passport_issued_at")
        private LocalDate privatePersonPassportIssuedAt;
        @JsonProperty("private_person_passport_info")
        private String privatePersonPassportInfo;
    }

    @Getter
    @Setter
    public static class Location {
        @JsonProperty("country")
        private String country;
        @JsonProperty("country_iso_code")
        private String countryIsoCode;
        @JsonProperty("federal_district")
        private String federalDistrict;
        @JsonProperty("region")
        private String region;
        @JsonProperty("region_type")
        private String regionType;
        @JsonProperty("region_with_type")
        private String regionWithType;
        @JsonProperty("region_fias_id")
        private String regionFiasId;
        @JsonProperty("region_kladr_id")
        private String regionKladrId;
        @JsonProperty("region_iso_code")
        private String regionIsoCode;

        @JsonProperty("area")
        private String area;
        @JsonProperty("area_type")
        private String areaType;
        @JsonProperty("area_with_type")
        private String areaWithType;
        @JsonProperty("area_fias_id")
        private String areaFiasId;
        @JsonProperty("area_kladr_id")
        private String areaKladrId;

        @JsonProperty("city")
        private String city;
        @JsonProperty("city_type")
        private String cityType;
        @JsonProperty("city_with_type")
        private String cityWithType;
        @JsonProperty("city_fias_id")
        private String cityFiasId;
        @JsonProperty("city_kladr_id")
        private String cityKladrId;

        @JsonProperty("settlement")
        private String settlement;
        @JsonProperty("settlement_type")
        private String settlementType;
        @JsonProperty("settlement_with_type")
        private String settlementWithType;
        @JsonProperty("settlement_fias_id")
        private String settlementFiasId;
        @JsonProperty("settlement_kladr_id")
        private String settlementKladrId;

        @JsonProperty("street")
        private String street;
        @JsonProperty("street_type")
        private String streetType;
        @JsonProperty("street_with_type")
        private String streetWithType;
        @JsonProperty("street_fias_id")
        private String streetFiasId;
        @JsonProperty("street_kladr_id")
        private String streetKladrId;

        @JsonProperty("house")
        private String house;
        @JsonProperty("house_fias_id")
        private String houseFiasId;
        @JsonProperty("house_kladr_id")
        private String houseKladrId;

        @JsonProperty("office")
        private String office;
        @JsonProperty("fias_id")
        private String fiasId;
        @JsonProperty("kladr_id")
        private String kladrId;

        @JsonProperty("lat")
        private Float lat;
        @JsonProperty("lng")
        private Float lng;

        @JsonProperty("zip_code")
        private String zipCode;
        @JsonProperty("timezone_name")
        private String timezoneName;
        @JsonProperty("level")
        private String level;
        @JsonProperty("timezone")
        private String timezone;
    }

    @Getter
    @Setter
    public static class CargoReceiverSenderInfo {

        @JsonProperty("phone")
        private String phone;
        @JsonProperty("extension_number")
        private String extensionNumber;
        @JsonProperty("company_name")
        private String companyName;
        @JsonProperty("contact_name")
        private String contactName;
        @JsonProperty("contact_inn")
        private String contactInn;
        @JsonProperty("contact_kpp")
        private String contactKpp;
        @JsonProperty("contact_opf_name")
        private String contactOpfName;
        @JsonProperty("passport_number")
        private String passportNumber;

        @JsonProperty("passport_issued_at")
        private LocalDate passportIssuedAt;
        @JsonProperty("passport_who_issued")
        private String passportWhoIssued;
        @JsonProperty("address")
        private String address;

    }

}

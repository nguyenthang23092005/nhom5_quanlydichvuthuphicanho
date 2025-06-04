package com.apartmentservice.model;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "building")
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildingSummary {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "address")
    private String address;

    @XmlElement(name = "floorCount")
    private int floorCount;

    @XmlElement(name = "apartmentCount")
    private int apartmentCount;

    @XmlElement(name = "managerName")
    private String managerName;

    @XmlElement(name = "status")
    private String status;

    // No-arg constructor REQUIRED for JAXB
    public BuildingSummary() {
    }

    public BuildingSummary(String name, String address, int floorCount, int apartmentCount, String managerName, String status) {
        this.name = name;
        this.address = address;
        this.floorCount = floorCount;
        this.apartmentCount = apartmentCount;
        this.managerName = managerName;
        this.status = status;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getFloorCount() { return floorCount; }
    public void setFloorCount(int floorCount) { this.floorCount = floorCount; }

    public int getApartmentCount() { return apartmentCount; }
    public void setApartmentCount(int apartmentCount) { this.apartmentCount = apartmentCount; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
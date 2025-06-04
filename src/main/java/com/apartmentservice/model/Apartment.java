package com.apartmentservice.model;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "apartment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Apartment implements Serializable {
    @XmlElement(name = "apartmentID")
    private String apartmentID;

    @XmlElement(name = "building")
    private String building;

    @XmlElement(name = "floor")
    private int floor;

    @XmlElement(name = "acreage")
    private double acreage;

    @XmlElement(name = "status")
    private String status;

    @XmlElement(name = "ownerName")
    private String ownerName;

    @XmlElement(name = "memberNumber")
    private int memberNumber;
    
    @XmlElement(name = "dichVu")
    private String dichVu;

    @XmlElement(name = "moveInDate")
    private String moveInDate;

    public Apartment() {
    }

    public Apartment(String apartmentID, String building, int floor, double acreage,
                     String status, String ownerName, int memberNumber,String dichVu, String moveInDate) {
        this.apartmentID = apartmentID;
        this.building = building;
        this.floor = floor;
        this.acreage = acreage;
        this.status = status;
        this.ownerName = ownerName;
        this.memberNumber = memberNumber;
        this.dichVu = dichVu;
        this.moveInDate = moveInDate;
    }

    public String getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(String apartmentID) {
        this.apartmentID = apartmentID;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getAcreage() {
        return acreage;
    }

    public void setAcreage(double acreage) {
        this.acreage = acreage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if ("Đã ở".equals(status) || "Bỏ trống".equals(status)) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Trạng thái chỉ có thể là 'Đã ở' hoặc 'Bỏ trống'.");
        }
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(int memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getDichVu() {
        return dichVu;
    }

    public void setDichVu(String dichVu) {
        this.dichVu = dichVu;
    }
    
    public String getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(String moveInDate) {
        this.moveInDate = moveInDate;
    }
    
    public String getServiceNames() {
        return (dichVu != null && !dichVu.isBlank()) ? dichVu : "";
    }
    public double getTotalServiceCost(List<Service> allServices) {
        double total = 0;
        if (dichVu == null || dichVu.trim().isEmpty()) {
            return 0;
        }
        String[] serviceNames = dichVu.split(",");
        for (String name : serviceNames) {
            String trimmed = name.trim();
            Service matched = allServices.stream()
                    .filter(s -> s.getServiceName().equalsIgnoreCase(trimmed))
                    .findFirst()
                    .orElse(null);
            if (matched != null) {
                total += matched.getUnitPrice();
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Mã Căn Hộ: %s, Số tòa: %s, Tầng: %d, Diện tích: %.2f m2, Trạng thái: %s, Chủ hộ: %s, Số thành viên: %d, Ngày vào ở: %s.",
                apartmentID, building, floor, acreage, status, ownerName, memberNumber, moveInDate);
    }
}
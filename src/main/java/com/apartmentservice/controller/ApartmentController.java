package com.apartmentservice.controller;

/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.ApartmentManager;
import com.apartmentservice.model.Apartment;
import com.apartmentservice.utils.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

public class ApartmentController {
    private final ApartmentManager manager = new ApartmentManager();

    public List<Apartment> getAllApartments() {
        return manager.getAll();
    }

    public String generateNewApartmentID() {
        return IDGenerator.getInstance().generateApartmentID();
    }

    public void addApartment(String building, int floor, double acreage, String status,
                             String ownerName, int memberNumber,String dichVu, String moveInDate) {
        String id = generateNewApartmentID();
        Apartment apartment = new Apartment(id, building, floor, acreage, status, ownerName, memberNumber,dichVu, moveInDate);
        manager.add(apartment);
    }

    public void updateApartment(String apartmentID, String building, int floor, double acreage, String status,
                                String ownerName, int memberNumber,String dichVu, String moveInDate) {
        Apartment apartment = new Apartment(apartmentID, building, floor, acreage, status, ownerName, memberNumber,dichVu, moveInDate);
        manager.update(apartment);
    }

    public boolean deleteApartment(String apartmentID) {
        IDGenerator.getInstance().decreaseApartmentID();
        return manager.delete(apartmentID);
    }

    public List<Apartment> findByApartmentID(String id) {
        return manager.findByApartmentID(id);
    }

    public List<Apartment> findByOwnerName(String ownerName) {
        return manager.findByOwnerName(ownerName);
    }

    public List<Apartment> findByBuilding(String building) {
        return manager.findByBuilding(building);
    }

    public List<Apartment> findByStatus(String status) {
        return manager.findByStatus(status);
    }

    public List<Apartment> findByMoveInDate(String moveInDate) {
        return manager.findByMoveInDate(moveInDate);
    }

    public List<Apartment> sort(String criteria, boolean ascending) {
        switch (criteria) {
            case "Mã căn hộ":
                return ascending ? manager.sortByApartmentIDAsc() : manager.sortByApartmentIDDesc();
            case "Tòa":
                return ascending ? manager.sortByBuildingAsc() : manager.sortByBuildingDesc();
            case "Tầng":
                return ascending ? manager.sortByFloorAsc() : manager.sortByFloorDesc();
            case "Diện tích":
                return ascending ? manager.sortByAcreageAsc() : manager.sortByAcreageDesc();
            case "Chủ hộ":
                return ascending ? manager.sortByOwnerNameAsc() : manager.sortByOwnerNameDesc();
            case "Số người":
                return ascending ? manager.sortByMemberNumberAsc() : manager.sortByMemberNumberDesc();
            default:
                return manager.getAll(); // fallback
        }
    }
    public List<Apartment> searchBy(String field, String keyword) {
        return manager.getAll().stream()
            .filter(a -> {
                switch (field) {
                    case "Tòa":
                        return a.getBuilding().toLowerCase().contains(keyword.toLowerCase());
                    case "Trạng thái":
                        return a.getStatus().toLowerCase().contains(keyword.toLowerCase());
                    default:
                        return false;
                }
            })
            .collect(Collectors.toList());
    }
}
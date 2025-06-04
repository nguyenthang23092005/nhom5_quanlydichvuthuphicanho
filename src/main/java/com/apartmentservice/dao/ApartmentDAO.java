package com.apartmentservice.dao;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Apartment;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.ApartmentXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ApartmentDAO {
    private static final String FILE_PATH = "data/apartments.xml";
    private List<Apartment> apartments;

    public ApartmentDAO() {
        ApartmentXML wrapper = XMLUtil.readFromXML(new File(FILE_PATH), ApartmentXML.class);
        apartments = (wrapper != null) ? wrapper.getApartment() : new ArrayList<>();
    }
    
    public void save() {
        XMLUtil.writeToXML(new File(FILE_PATH), new ApartmentXML(apartments), ApartmentXML.class);
    }

    public void add(Apartment apartment) {
        apartments.add(apartment);
        save();
    }

    public boolean delete(String apartmentID) {
        boolean removed = apartments.removeIf(a -> a.getApartmentID().equals(apartmentID));
        if (removed) save();
        return removed;
    }

    public void update(Apartment updated) {
        for (int i = 0; i < apartments.size(); i++) {
            if (apartments.get(i).getApartmentID().equals(updated.getApartmentID())) {
                apartments.set(i, updated);
                save();
                break;
            }
        }
    }

    public List<Apartment> findByApartmentID(String id) {
        return apartments.stream()
                .filter(a -> a.getApartmentID() != null && a.getApartmentID().toLowerCase().contains(id.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Tìm theo tên chủ hộ (đã có)
    public List<Apartment> findByOwnerName(String keyword) {
        return apartments.stream()
                .filter(a -> a.getOwnerName() != null && a.getOwnerName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Tìm theo tên tòa
    public List<Apartment> findByBuilding(String building) {
        return apartments.stream()
                .filter(a -> a.getBuilding() != null && a.getBuilding().toLowerCase().contains(building.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Tìm theo trạng thái
    public List<Apartment> findByStatus(String status) {
        return apartments.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().toLowerCase().contains(status.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Tìm theo ngày vào (dạng chuỗi, ví dụ dd/MM/yyyy)
    public List<Apartment> findByMoveInDate(String moveInDate) {
        return apartments.stream()
                .filter(a -> a.getMoveInDate() != null && a.getMoveInDate().toLowerCase().contains(moveInDate.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Apartment> getAll() {
        return new ArrayList<>(apartments);
    }

    // Sắp xếp chung
    private List<Apartment> sortBy(Comparator<Apartment> comparator) {
        List<Apartment> sorted = new ArrayList<>(apartments);
        sorted.sort(comparator);
        return sorted;
    }

    public List<Apartment> sortByApartmentIDAsc()     { return sortBy(Comparator.comparing(Apartment::getApartmentID)); }
    public List<Apartment> sortByApartmentIDDesc()    { return sortBy(Comparator.comparing(Apartment::getApartmentID).reversed()); }

    public List<Apartment> sortByBuildingAsc()        { return sortBy(Comparator.comparing(Apartment::getBuilding)); }
    public List<Apartment> sortByBuildingDesc()       { return sortBy(Comparator.comparing(Apartment::getBuilding).reversed()); }

    public List<Apartment> sortByFloorAsc()           { return sortBy(Comparator.comparingInt(Apartment::getFloor)); }
    public List<Apartment> sortByFloorDesc()          { return sortBy(Comparator.comparingInt(Apartment::getFloor).reversed()); }

    public List<Apartment> sortByAcreageAsc()         { return sortBy(Comparator.comparingDouble(Apartment::getAcreage)); }
    public List<Apartment> sortByAcreageDesc()        { return sortBy(Comparator.comparingDouble(Apartment::getAcreage).reversed()); }

    public List<Apartment> sortByOwnerNameAsc()       { return sortBy(Comparator.comparing(Apartment::getOwnerName)); }
    public List<Apartment> sortByOwnerNameDesc()      { return sortBy(Comparator.comparing(Apartment::getOwnerName).reversed()); }

    public List<Apartment> sortByMemberNumberAsc()    { return sortBy(Comparator.comparingInt(Apartment::getMemberNumber)); }
    public List<Apartment> sortByMemberNumberDesc()   { return sortBy(Comparator.comparingInt(Apartment::getMemberNumber).reversed()); }
}
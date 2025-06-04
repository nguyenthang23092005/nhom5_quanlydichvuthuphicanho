package com.apartmentservice.controller;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.ResidentManager;
import com.apartmentservice.model.Resident;
import com.apartmentservice.utils.Validator;
import java.text.SimpleDateFormat;
import java.util.List;

public class ResidentController {
    private final ResidentManager manager = new ResidentManager();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public List<Resident> getAllResidents() {
        return manager.getAll();
    }
    public List<String> getAllCCCD() {
        // Lấy tất cả cư dân, sau đó map sang CCCD
        List<Resident> residents = getAllResidents();
        return residents.stream()
                .map(Resident::getCccd)
                .toList();
    }
    public boolean addResident(String cccd, String name, String birthday, String address,
                                String IDFamily, String sex, String role, String birthPlace,
                                String phoneNumber, String apartmentID) {
        if (!Validator.isValidCCCD(cccd) || !Validator.isValidName(name)
            || !Validator.isValidPhoneNumber(phoneNumber) || birthday == null) {
            return false;
        }
        Resident resident = new Resident(cccd, name, birthday, address, IDFamily, sex, role, birthPlace, phoneNumber, apartmentID);
        manager.add(resident);
        return true;
    }

    public void updateResident(String cccd, String name, String birthday, String address,
                               String IDFamily, String sex, String role, String birthPlace,
                               String phoneNumber, String apartmentID) {
        Resident resident = new Resident(cccd, name, birthday, address, IDFamily, sex, role, birthPlace, phoneNumber, apartmentID);
        manager.update(resident);
    }

    public void deleteResident(String cccd) {
        manager.delete(cccd);
    }

    public Resident findByCCCD(String cccd) {
        return manager.findByCccd(cccd);
    }

    public List<Resident> searchByApartmentID(String apartmentID) {
        return manager.findByApartmentID(apartmentID);
    }

    public List<Resident> searchByIDFamily(String idFamily) {
        return manager.findByIDFamily(idFamily);
    }

    public List<Resident> sort(String field, boolean ascending) {
        switch (field) {
            case "Họ tên":
                return ascending ? manager.sortByNameAsc() : manager.sortByNameDesc();
            case "Quê quán":
                return ascending ? manager.sortByBirthPlaceAsc() : manager.sortByBirthPlaceDesc();
            case "Địa chỉ":
                return ascending ? manager.sortByAddressAsc() : manager.sortByAddressDesc();
            case "Mã căn hộ":
                return ascending ? manager.sortByApartmentIDAsc() : manager.sortByApartmentIDDesc();
            case "Sổ hộ khẩu":
                return ascending ? manager.sortByIDFamilyAsc() : manager.sortByIDFamilyDesc();
            default:
                return manager.getAll();
        }
    }
}
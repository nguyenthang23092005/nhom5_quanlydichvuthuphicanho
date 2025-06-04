package com.apartmentservice.manager;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.dao.ResidentDAO;
import com.apartmentservice.model.Resident;

import java.util.List;

public class ResidentManager {
    private final ResidentDAO dao = new ResidentDAO();

    public void add(Resident r) {
        dao.add(r);
    }

    public boolean delete(String cccd) {
        return dao.delete(cccd);
    }

    public void update(Resident r) {
        dao.update(r);
    }

    public Resident findByCccd(String cccd) {
        return dao.findByCccd(cccd);
    }

    public List<Resident> findByApartmentID(String apartmentID) {
        return dao.findByApartmentID(apartmentID);
    }

    public List<Resident> findByIDFamily(String idFamily) {
        return dao.findByIDFamily(idFamily);
    }

    public List<Resident> getAll() {
        return dao.getAll();
    }

    public List<Resident> sortByNameAsc() {
        return dao.sortByNameAsc();
    }

    public List<Resident> sortByNameDesc() {
        return dao.sortByNameDesc();
    }

    public List<Resident> sortByAddressAsc() {
        return dao.sortByAddressAsc();
    }

    public List<Resident> sortByAddressDesc() {
        return dao.sortByAddressDesc();
    }

    public List<Resident> sortByBirthPlaceAsc() {
        return dao.sortByBirthPlaceAsc();
    }

    public List<Resident> sortByBirthPlaceDesc() {
        return dao.sortByBirthPlaceDesc();
    }

    public List<Resident> sortByApartmentIDAsc() {
        return dao.sortByApartmentIDAsc();
    }

    public List<Resident> sortByApartmentIDDesc() {
        return dao.sortByApartmentIDDesc();
    }

    public List<Resident> sortByIDFamilyAsc() {
        return dao.sortByIDFamilyAsc();
    }

    public List<Resident> sortByIDFamilyDesc() {
        return dao.sortByIDFamilyDesc();
    }
}
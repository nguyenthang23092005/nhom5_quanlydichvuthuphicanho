/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
package com.apartmentservice.manager;
import com.apartmentservice.dao.ApartmentDAO;
import com.apartmentservice.model.Apartment;
import java.util.List;

public class ApartmentManager {
    private final ApartmentDAO dao = new ApartmentDAO();

    public void add(Apartment apartment) {
        dao.add(apartment);
    }

    public boolean delete(String apartmentID) {
        return dao.delete(apartmentID);
    }

    public void update(Apartment apartment) {
        dao.update(apartment);
    }

    public List<Apartment> findByApartmentID(String id) {
        return dao.findByApartmentID(id);
    }

    public List<Apartment> findByOwnerName(String keyword) {
        return dao.findByOwnerName(keyword);
    }

    // Bổ sung các hàm tìm kiếm mới cho đủ tiêu chí
    public List<Apartment> findByBuilding(String building) {
        return dao.findByBuilding(building);
    }

    public List<Apartment> findByStatus(String status) {
        return dao.findByStatus(status);
    }

    public List<Apartment> findByMoveInDate(String moveInDate) {
        return dao.findByMoveInDate(moveInDate);
    }

    public List<Apartment> getAll() {
        return dao.getAll();
    }

    // Các hàm sắp xếp giữ nguyên
    public List<Apartment> sortByApartmentIDAsc()      { return dao.sortByApartmentIDAsc(); }
    public List<Apartment> sortByApartmentIDDesc()     { return dao.sortByApartmentIDDesc(); }
    public List<Apartment> sortByBuildingAsc()         { return dao.sortByBuildingAsc(); }
    public List<Apartment> sortByBuildingDesc()        { return dao.sortByBuildingDesc(); }
    public List<Apartment> sortByFloorAsc()            { return dao.sortByFloorAsc(); }
    public List<Apartment> sortByFloorDesc()           { return dao.sortByFloorDesc(); }
    public List<Apartment> sortByAcreageAsc()          { return dao.sortByAcreageAsc(); }
    public List<Apartment> sortByAcreageDesc()         { return dao.sortByAcreageDesc(); }
    public List<Apartment> sortByOwnerNameAsc()        { return dao.sortByOwnerNameAsc(); }
    public List<Apartment> sortByOwnerNameDesc()       { return dao.sortByOwnerNameDesc(); }
    public List<Apartment> sortByMemberNumberAsc()     { return dao.sortByMemberNumberAsc(); }
    public List<Apartment> sortByMemberNumberDesc()    { return dao.sortByMemberNumberDesc(); }
}
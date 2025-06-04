package com.apartmentservice.manager;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.dao.BuildingDAO;
import com.apartmentservice.model.BuildingSummary;

import java.util.List;

public class BuildingManager {

    private final BuildingDAO dao;

    public BuildingManager() {
        dao = new BuildingDAO();
    }

    // Chỉ gọi danh sách đã sắp xếp theo tên tòa
    public List<BuildingSummary> getAllSortedByName() {
        return dao.getAllSortedByName();
    }

    public void add(BuildingSummary building) {
        dao.add(building);
    }

    public boolean update(BuildingSummary building) {
        return dao.update(building);
    }

    public boolean delete(String buildingName) {
        return dao.delete(buildingName);
    }

    public List<BuildingSummary> findByName(String keyword) {
        return dao.findByName(keyword);
    }

    public BuildingSummary findByExactName(String name) {
        return dao.findByExactName(name);
    }
}
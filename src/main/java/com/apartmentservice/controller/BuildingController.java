package com.apartmentservice.controller;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.BuildingManager;
import com.apartmentservice.model.BuildingSummary;

import java.util.List;

public class BuildingController {

    private final BuildingManager manager;

    public BuildingController() {
        manager = new BuildingManager();
    }

    // Hiển thị danh sách đã có trong XML theo thứ tự tăng dần tên tòa
    public List<BuildingSummary> getAllBuildingsSortedByName() {
        return manager.getAllSortedByName();
    }

    public void addBuilding(BuildingSummary building) {
        manager.add(building);
    }

    public boolean updateBuilding(BuildingSummary building) {
        return manager.update(building);
    }

    public boolean deleteBuilding(String buildingName) {
        return manager.delete(buildingName);
    }

    public List<BuildingSummary> findByName(String keyword) {
        return manager.findByName(keyword);
    }

    public BuildingSummary findExactByName(String name) {
        return manager.findByExactName(name);
    }
}
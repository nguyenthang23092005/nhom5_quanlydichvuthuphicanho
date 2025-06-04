package com.apartmentservice.dao;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.BuildingSummary;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.BuildingXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BuildingDAO {

    private static final String FILE_PATH = "data/buildings.xml";
    private List<BuildingSummary> buildings;

    public BuildingDAO() {
        BuildingXML wrapper = XMLUtil.readFromXML(new File(FILE_PATH), BuildingXML.class);
        buildings = (wrapper != null) ? wrapper.getBuildings() : new ArrayList<>();
    }

    // Trả về danh sách đã sắp xếp theo tên tòa tăng dần
    public List<BuildingSummary> getAllSortedByName() {
        List<BuildingSummary> sorted = new ArrayList<>(buildings);
        sorted.sort(Comparator.comparing(BuildingSummary::getName));
        return sorted;
    }

    public void save() {
        XMLUtil.writeToXML(new File(FILE_PATH), new BuildingXML(buildings), BuildingXML.class);
    }

    public void add(BuildingSummary building) {
        buildings.add(building);
        save();
    }

    public boolean update(BuildingSummary updated) {
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i).getName().equalsIgnoreCase(updated.getName())) {
                buildings.set(i, updated);
                save();
                return true;
            }
        }
        return false;
    }

    public boolean delete(String buildingName) {
        boolean removed = buildings.removeIf(b ->
            b.getName().equalsIgnoreCase(buildingName));
        if (removed) save();
        return removed;
    }

    public List<BuildingSummary> findByName(String keyword) {
        List<BuildingSummary> result = new ArrayList<>();
        for (BuildingSummary b : buildings) {
            if (b.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(b);
            }
        }
        return result;
    }

    public BuildingSummary findByExactName(String name) {
        for (BuildingSummary b : buildings) {
            if (b.getName().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }
}
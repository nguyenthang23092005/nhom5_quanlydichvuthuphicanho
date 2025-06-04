package com.apartmentservice.wrapper;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.model.BuildingSummary;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "buildings")
@XmlAccessorType(XmlAccessType.FIELD)
public class BuildingXML {

    @XmlElement(name = "building")
    private List<BuildingSummary> buildings;

    public BuildingXML() {}

    public BuildingXML(List<BuildingSummary> buildings) {
        this.buildings = buildings;
    }

    public List<BuildingSummary> getBuildings() {
        return buildings;
    }

    public void setBuildings(List<BuildingSummary> buildings) {
        this.buildings = buildings;
    }
}
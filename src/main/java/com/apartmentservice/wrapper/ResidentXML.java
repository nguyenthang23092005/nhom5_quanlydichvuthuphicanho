package com.apartmentservice.wrapper;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.model.Resident;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "residents")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResidentXML {

    @XmlElement(name = "resident")
    private List<Resident> residents;

    public ResidentXML() {
        this.residents = new ArrayList<>(); // Đảm bảo không bị null
    }

    public ResidentXML(List<Resident> residents) {
        this.residents = residents;
    }

    public List<Resident> getResidents() {
        return residents;
    }

    public void setResidents(List<Resident> residents) {
        this.residents = residents;
    }
}
package com.apartmentservice.wrapper;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.model.Apartment;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "apartments")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class ApartmentXML {

    @XmlElement(name = "apartment")
    private List<Apartment> apartment;

    public ApartmentXML() {
    }

    public ApartmentXML(List<Apartment> apartment) {
        this.apartment = apartment;
    }

    public List<Apartment> getApartment() {
        return apartment;
    }

    public void setApartment(List<Apartment> apartment) {
        this.apartment = apartment;
    }
}
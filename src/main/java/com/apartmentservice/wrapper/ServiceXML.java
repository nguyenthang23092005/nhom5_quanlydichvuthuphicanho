package com.apartmentservice.wrapper;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Service;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import java.util.List;

@XmlRootElement(name = "services")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceXML {
    
    @XmlElement(name = "service")
    private List<Service> services;

    public ServiceXML() {
    }

    public ServiceXML(List<Service> services) {
        this.services = services;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}

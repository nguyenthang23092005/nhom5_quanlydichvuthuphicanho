package com.apartmentservice.model;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

@XmlRootElement(name = "service")
@XmlAccessorType(XmlAccessType.FIELD)
public class Service implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "serviceID")
    private String serviceID;

    @XmlElement(name = "serviceName")
    private String serviceName;

    @XmlElement(name = "unitPrice")
    private double unitPrice;

    @XmlElement(name = "unit")
    private String unit;

    @XmlElement(name = "note")
    private String note;

    public Service() {
    }

    public Service(String serviceID, String serviceName, double unitPrice, String unit, String note) {
        this.serviceID = serviceID;
        this.serviceName = serviceName;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.note = note;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public String getFormattedUnitPrice() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(true);
        String formatted = nf.format(unitPrice);
        return formatted.replace('.', ',');
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return String.format("Mã: %s | Tên: %s | Đơn giá: %.0f VND/%s | Ghi chú: %s",
                serviceID, serviceName, unitPrice, unit, note);
    }
}
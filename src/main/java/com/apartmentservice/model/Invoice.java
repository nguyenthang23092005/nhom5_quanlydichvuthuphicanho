package com.apartmentservice.model;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@XmlRootElement(name = "invoice")
@XmlAccessorType(XmlAccessType.FIELD)
public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "invoiceID", required = true)
    private String invoiceID;

    @XmlElement(name = "apartmentID", required = true)
    private String apartmentID;

    @XmlElement(name = "customerName", required = true)
    private String customerName;

    @XmlElement(name = "cashierName", required = true)
    private String cashierName;

    @XmlElement(name = "paid")
    private String paid;

    @XmlElement(name = "invoiceDate", required = true)
    private String invoiceDate;

    @XmlElementWrapper(name = "serviceFees")
    @XmlElement(name = "serviceFee")
    private List<Service> service;

    public Invoice() {}

    public Invoice(String invoiceID, String apartmentID, String customerName,
                   String cashierName, String invoiceDate,
                   String paid, List<Service> service) {
        this.invoiceID = invoiceID;
        this.apartmentID = apartmentID;
        this.customerName = customerName;
        this.cashierName = cashierName;
        this.invoiceDate = invoiceDate;
        this.paid = paid;
        this.service = service;
    }

    // Getters & Setters
    public String getInvoiceID() { return invoiceID; }
    public void setInvoiceID(String invoiceID) { this.invoiceID = invoiceID; }

    public String getApartmentID() { return apartmentID; }
    public void setApartmentID(String apartmentID) { this.apartmentID = apartmentID; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCashierName() { return cashierName; }
    public void setCashierName(String cashierName) { this.cashierName = cashierName; }

    public String getPaid() { return paid; }
    public void setPaid(String paid) { this.paid = paid; }

    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }

    public List<Service> getService() { return service; }
    public void setService(List<Service> service) { this.service = service; }

    // Hàm static để tính tổng tiền của một căn hộ từ dịch vụ (áp dụng 1 lần/tháng mỗi dịch vụ)
    public static double getTotalAmountRaw(Apartment apartment, List<Service> allServices) {
        double total = 0;
        String dichVuStr = apartment.getDichVu();
        System.out.println("Dịch vụ căn hộ: " + dichVuStr);
        if (dichVuStr == null || dichVuStr.trim().isEmpty()) return 0;
        String[] dichVuNames = dichVuStr.split(",");
        for (String name : dichVuNames) {
            String trimmed = name.trim();
            Service matched = allServices.stream()
                .filter(s -> s.getServiceName().equalsIgnoreCase(trimmed))
                .findFirst()
                .orElse(null);
            System.out.println("  - Dịch vụ: " + trimmed + " => matched: " + (matched != null ? matched.getUnitPrice() : "null"));
            if (matched != null) {
                total += matched.getUnitPrice();
            }
        }
        System.out.println("==> Tổng tiền: " + total);
        return total;
    }

    // Hàm định dạng tổng tiền ra chuỗi kiểu VNĐ
    public static String getTotalAmountFormatted(Apartment apartment, List<Service> allServices) {
        double total = getTotalAmountRaw(apartment, allServices);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(0);
        return nf.format(total).replace('.', ',');
    }


    public boolean isPaidAsBoolean() {
        return paid != null && paid.equalsIgnoreCase("Đã thanh toán");
    }
}
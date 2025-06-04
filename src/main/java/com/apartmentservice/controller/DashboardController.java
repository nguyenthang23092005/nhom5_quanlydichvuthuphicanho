package com.apartmentservice.controller;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.*;
import com.apartmentservice.model.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class DashboardController {
    
    private final ApartmentManager apartmentManager = new ApartmentManager();
    private final ResidentManager residentManager = new ResidentManager();
    private final InvoiceManager invoiceManager = new InvoiceManager();
    private final ServiceManager serviceManager = new ServiceManager();

    // Tổng số căn hộ
    public int getTotalApartments() {
        return apartmentManager.getAll().size();
    }

    // Tổng số cư dân
    public int getTotalResidents() {
        return residentManager.getAll().size();
    }

    // Tổng số hóa đơn
    public int getTotalInvoices() {
        return invoiceManager.getAll().size();
    }

    // Tổng số dịch vụ
    public int getTotalServices() {
        return serviceManager.getAll().size();
    }

    // Số hóa đơn chưa thanh toán
    public long getUnpaidInvoicesCount() {
        return invoiceManager.getAll().stream()
                .filter(i -> !i.isPaidAsBoolean())
                .count();
    }

    // Tổng tiền của tất cả hóa đơn đã thanh toán (kiểu double)
    public double getTotalRevenueRaw(List<Service> services, List<Apartment> apartments) {
        return invoiceManager.getAll().stream()
                .filter(Invoice::isPaidAsBoolean)
                .mapToDouble(invoice -> {
                    // Tìm căn hộ ứng với mã căn hộ trong hóa đơn
                    Apartment matchedApartment = apartments.stream()
                            .filter(a -> a.getApartmentID().equals(invoice.getApartmentID()))
                            .findFirst()
                            .orElse(null);
                    // Nếu tìm thấy căn hộ thì tính tổng, không thì coi như 0
                    return (matchedApartment != null) ? Invoice.getTotalAmountRaw(matchedApartment, services) : 0;
                })
                .sum();
    }

    // Tổng tiền đã thanh toán (kiểu chuỗi định dạng tiền Việt)
    public String getTotalRevenueFormatted(List<Service> services, List<Apartment> apartments) {
        double total = getTotalRevenueRaw(services, apartments);
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setGroupingUsed(true);
        nf.setMaximumFractionDigits(0);
        return nf.format(total).replace('.', ',');
    }
}
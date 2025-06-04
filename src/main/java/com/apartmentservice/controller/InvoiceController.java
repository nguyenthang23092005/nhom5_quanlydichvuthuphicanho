package com.apartmentservice.controller;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.InvoiceManager;
import com.apartmentservice.model.Invoice;
import java.util.List;

public class InvoiceController {
    private final InvoiceManager manager = new InvoiceManager();

    public List<Invoice> getAllInvoices() {
        return manager.getAll();
    }

    public void addInvoice(Invoice invoice) {
        manager.add(invoice);
    }

    public void updateInvoice(Invoice invoice) {
        manager.update(invoice);
    }

    public boolean deleteInvoice(String invoiceID) {
        return manager.delete(invoiceID);
    }

    public Invoice findByInvoiceID(String invoiceID) {
        return manager.findByInvoiceID(invoiceID);
    }

    public List<Invoice> findByApartmentID(String apartmentID) {
        return manager.findByApartmentID(apartmentID);
    }

    public List<Invoice> findByCustomerName(String name) {
        return manager.findByCustomerName(name);
    }

    public List<Invoice> findByInvoiceDate(String invoiceDate) {
        return manager.findByInvoiceDate(invoiceDate);
    }
    
    public List<Invoice> sort(String criteria, boolean ascending) {
        switch (criteria) {
            case "Mã hóa đơn":
                return ascending ? manager.sortByInvoiceIDAsc() : manager.sortByInvoiceIDDesc();
            case "Mã căn hộ":
                return ascending ? manager.sortByApartmentIDAsc() : manager.sortByApartmentIDDesc();
            case "Khách hàng":
                return ascending ? manager.sortByCustomerNameAsc() : manager.sortByCustomerNameDesc();
            case "Ngày thanh toán":
                return ascending ? manager.sortByInvoiceDateAsc() : manager.sortByInvoiceDateDesc();
            default:
                return getAllInvoices();
        }
    }
}
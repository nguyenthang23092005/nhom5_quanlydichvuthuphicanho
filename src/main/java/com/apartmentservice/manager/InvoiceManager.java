package com.apartmentservice.manager;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.dao.InvoiceDAO;
import com.apartmentservice.model.Invoice;

import java.util.List;

public class InvoiceManager {
    private final InvoiceDAO dao = new InvoiceDAO();

    public void add(Invoice invoice) {
        dao.add(invoice);
    }

    public void update(Invoice invoice) {
        dao.update(invoice);
    }

    public boolean delete(String invoiceID) {
        return dao.delete(invoiceID);
    }

    public Invoice findByInvoiceID(String invoiceID) {
        return dao.findByInvoiceID(invoiceID);
    }

    public List<Invoice> findByApartmentID(String apartmentID) {
        return dao.findByApartmentID(apartmentID);
    }

    public List<Invoice> findByCustomerName(String name) {
        return dao.findByCustomerName(name);
    }

    public List<Invoice> findByInvoiceDate(String invoiceDate) {
        return dao.findByInvoiceDate(invoiceDate);
    }
    
    public List<Invoice> getAll() {
        return dao.getAll();
    }

    public List<Invoice> sortByInvoiceIDAsc() {
        return dao.sortByInvoiceIDAsc();
    }

    public List<Invoice> sortByInvoiceIDDesc() {
        return dao.sortByInvoiceIDDesc();
    }

    public List<Invoice> sortByApartmentIDAsc() {
        return dao.sortByApartmentIDAsc();
    }

    public List<Invoice> sortByApartmentIDDesc() {
        return dao.sortByApartmentIDDesc();
    }

    public List<Invoice> sortByCustomerNameAsc() {
        return dao.sortByCustomerNameAsc();
    }

    public List<Invoice> sortByCustomerNameDesc() {
        return dao.sortByCustomerNameDesc();
    }

    public List<Invoice> sortByInvoiceDateAsc() {
        return dao.sortByInvoiceDateAsc();
    }

    public List<Invoice> sortByInvoiceDateDesc() {
        return dao.sortByInvoiceDateDesc();
    }
}
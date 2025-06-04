package com.apartmentservice.dao;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Invoice;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.InvoiceXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceDAO {
    private static final String FILE_PATH = "data/invoices.xml";
    private List<Invoice> invoices;

    public InvoiceDAO() {
        InvoiceXML wrapper = XMLUtil.readFromXML(new File(FILE_PATH), InvoiceXML.class);
        invoices = (wrapper != null) ? wrapper.getInvoices() : new ArrayList<>();
    }

    public void save() {
        XMLUtil.writeToXML(new File(FILE_PATH), new InvoiceXML(invoices), InvoiceXML.class);
    }

    public void add(Invoice invoice) {
        invoices.add(invoice);
        save();
    }

    public void update(Invoice updated) {
        for (int i = 0; i < invoices.size(); i++) {
            if (invoices.get(i).getInvoiceID().equalsIgnoreCase(updated.getInvoiceID())) {
                invoices.set(i, updated);
                save();
                break;
            }
        }
    }

    public boolean delete(String invoiceID) {
        boolean removed = invoices.removeIf(i -> i.getInvoiceID().equalsIgnoreCase(invoiceID));
        if (removed) save();
        return removed;
    }

    public Invoice findByInvoiceID(String invoiceID) {
        return invoices.stream()
                .filter(i -> i.getInvoiceID().equalsIgnoreCase(invoiceID))
                .findFirst()
                .orElse(null);
    }

    public List<Invoice> findByApartmentID(String apartmentID) {
        return invoices.stream()
                .filter(i -> i.getApartmentID().equalsIgnoreCase(apartmentID))
                .collect(Collectors.toList());
    }

    public List<Invoice> findByCustomerName(String name) {
        return invoices.stream()
                .filter(i -> i.getCustomerName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Invoice> findByInvoiceDate(String invoiceDate) {
        return invoices.stream()
                .filter(i -> i.getInvoiceDate().toLowerCase().contains(invoiceDate.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Invoice> getAll() {
        return new ArrayList<>(invoices);
    }

    // --- Sắp xếp ---
    private List<Invoice> sortBy(Comparator<Invoice> comparator) {
        List<Invoice> sorted = new ArrayList<>(invoices);
        sorted.sort(comparator);
        return sorted;
    }

    public List<Invoice> sortByInvoiceIDAsc() {
        return sortBy(Comparator.comparing(Invoice::getInvoiceID));
    }

    public List<Invoice> sortByInvoiceIDDesc() {
        return sortBy(Comparator.comparing(Invoice::getInvoiceID).reversed());
    }

    public List<Invoice> sortByApartmentIDAsc() {
        return sortBy(Comparator.comparing(Invoice::getApartmentID));
    }

    public List<Invoice> sortByApartmentIDDesc() {
        return sortBy(Comparator.comparing(Invoice::getApartmentID).reversed());
    }

    public List<Invoice> sortByCustomerNameAsc() {
        return sortBy(Comparator.comparing(Invoice::getCustomerName));
    }

    public List<Invoice> sortByCustomerNameDesc() {
        return sortBy(Comparator.comparing(Invoice::getCustomerName).reversed());
    }

    public List<Invoice> sortByInvoiceDateAsc() {
        return sortBy(Comparator.comparing(Invoice::getInvoiceDate));
    }

    public List<Invoice> sortByInvoiceDateDesc() {
        return sortBy(Comparator.comparing(Invoice::getInvoiceDate).reversed());
    }
}
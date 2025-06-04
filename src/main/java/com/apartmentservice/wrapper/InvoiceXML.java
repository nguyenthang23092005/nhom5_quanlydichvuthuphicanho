package com.apartmentservice.wrapper;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Invoice;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "invoices")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceXML {

    @XmlElement(name = "invoice")
    private List<Invoice> invoices;

    public InvoiceXML() {
        this.invoices = new ArrayList<>(); 
    }

    public InvoiceXML(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
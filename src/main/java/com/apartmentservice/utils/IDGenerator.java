package com.apartmentservice.utils;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.*;

import java.io.File;
import java.io.Serializable;

@XmlRootElement(name = "idCounter")
@XmlAccessorType(XmlAccessType.FIELD)
public class IDGenerator implements Serializable {

    private static final String FILE_PATH = "data/id_counter.xml";

    @XmlElement
    private int serviceCounter = 0;

    @XmlElement
    private int invoiceCounter = 0;

    @XmlElement
    private int apartmentCounter = 0;

    @XmlElement
    private int accountCounter = 0;


    private static IDGenerator instance;

    public static IDGenerator getInstance() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    public String generateServiceID() {
        serviceCounter++;
        save();
        return String.format("DV%03d", serviceCounter);
    }

    public String generateInvoiceID() {
        invoiceCounter++;
        save();
        return String.format("HD%03d", invoiceCounter);
    }

    public String generateApartmentID() {
        apartmentCounter++;
        save();
        return String.format("CH%03d", apartmentCounter);
    }

    public String generateAccountID() {
        accountCounter++;
        save();
        return String.format("AC%03d", accountCounter);
    }

    private static IDGenerator load() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                JAXBContext context = JAXBContext.newInstance(IDGenerator.class);
                Unmarshaller um = context.createUnmarshaller();
                return (IDGenerator) um.unmarshal(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new IDGenerator();
    }

    private void save() {
        try {
            JAXBContext context = JAXBContext.newInstance(IDGenerator.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(this, new File(FILE_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
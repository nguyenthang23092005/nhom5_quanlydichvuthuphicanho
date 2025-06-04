package com.apartmentservice.dao;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Service;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.ServiceXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceDAO {
    private static final String FILE_PATH = "data/services.xml";
    private List<Service> services;

    public ServiceDAO() {
        ServiceXML wrapper = XMLUtil.readFromXML(new File(FILE_PATH), ServiceXML.class);
        services = (wrapper != null) ? wrapper.getServices() : new ArrayList<>();
    }

    public void save() {
        XMLUtil.writeToXML(new File(FILE_PATH), new ServiceXML(services), ServiceXML.class);
    }

    public void add(Service s) {
        services.add(s);
        save();
    }

    public boolean delete(String serviceID) {
        boolean removed = services.removeIf(s -> s.getServiceID().equalsIgnoreCase(serviceID));
        if (removed) save();
        return removed;
    }

    public void update(Service updated) {
        for (int i = 0; i < services.size(); i++) {
            if (services.get(i).getServiceID().equalsIgnoreCase(updated.getServiceID())) {
                services.set(i, updated);
                save();
                break;
            }
        }
    }

    public Service findByID(String serviceID) {
        return services.stream()
                .filter(s -> s.getServiceID().equalsIgnoreCase(serviceID))
                .findFirst().orElse(null);
    }

    public List<Service> findByName(String keyword) {
        return services.stream()
                .filter(s -> s.getServiceName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Service> getAll() {
        return new ArrayList<>(services);
    }

    // --- Sắp xếp ---
    private List<Service> sortBy(Comparator<Service> comparator) {
        List<Service> sorted = new ArrayList<>(services);
        sorted.sort(comparator);
        return sorted;
    }

    public List<Service> sortByServiceIDAsc() {
        return sortBy(Comparator.comparing(Service::getServiceID));
    }

    public List<Service> sortByServiceIDDesc() {
        return sortBy(Comparator.comparing(Service::getServiceID).reversed());
    }

    public List<Service> sortByServiceNameAsc() {
        return sortBy(Comparator.comparing(Service::getServiceName));
    }

    public List<Service> sortByServiceNameDesc() {
        return sortBy(Comparator.comparing(Service::getServiceName).reversed());
    }
}
package com.apartmentservice.controller;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.ServiceManager;
import com.apartmentservice.model.Service;
import com.apartmentservice.utils.IDGenerator;
import java.util.List;

public class ServiceController {
    private final ServiceManager manager = new ServiceManager();

    public List<Service> getAllServices() {
        return manager.getAll();
    }

    public String generateNewID() {
        return IDGenerator.getInstance().generateServiceID();
    }
    
    public List<String> getAllServiceIDs() {
        List<Service> services = getAllServices();
        return services.stream()
            .map(Service::getServiceID)
            .toList();
    }
    
    public void addService(String name, double price, String unit, String note) {
        String id = generateNewID();
        Service s = new Service(id, name, price, unit, note);
        manager.add(s);
    }

    public void updateService(String id, String name, double price, String unit, String note) {
        Service s = new Service(id, name, price, unit, note);
        manager.update(s);
    }

    public boolean deleteService(String id) {
        IDGenerator.getInstance().decreaseServiceID();
        return manager.delete(id);
    }

    public Service findByID(String id) {
        return manager.findByID(id);
    }

    public List<Service> findByName(String keyword) {
        return manager.findByName(keyword);
    }

    public List<Service> sort(String criteria, boolean ascending) {
        switch (criteria) {
            case "Mã dịch vụ":
                return ascending ? manager.sortByServiceIDAsc() : manager.sortByServiceIDDesc();
            case "Tên dịch vụ":
                return ascending ? manager.sortByServiceNameAsc() : manager.sortByServiceNameDesc();
            default:
                return getAllServices();
        }
    }
}
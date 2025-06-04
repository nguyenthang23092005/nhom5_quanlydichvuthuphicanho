package com.apartmentservice.manager;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import com.apartmentservice.dao.ServiceDAO;
import com.apartmentservice.model.Service;

import java.util.List;

public class ServiceManager {
    private final ServiceDAO dao = new ServiceDAO();

    public void add(Service s) {
        dao.add(s);
    }

    public void update(Service s) {
        dao.update(s);
    }

    public boolean delete(String serviceID) {
        return dao.delete(serviceID);
    }

    public Service findByID(String serviceID) {
        return dao.findByID(serviceID);
    }

    public List<Service> findByName(String keyword) {
        return dao.findByName(keyword);
    }

    public List<Service> getAll() {
        return dao.getAll();
    }

    public List<Service> sortByServiceIDAsc() {
        return dao.sortByServiceIDAsc();
    }

    public List<Service> sortByServiceIDDesc() {
        return dao.sortByServiceIDDesc();
    }

    public List<Service> sortByServiceNameAsc() {
        return dao.sortByServiceNameAsc();
    }

    public List<Service> sortByServiceNameDesc() {
        return dao.sortByServiceNameDesc();
    }
}
package com.apartmentservice.dao;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Resident;
import com.apartmentservice.utils.XMLUtil;
import com.apartmentservice.wrapper.ResidentXML;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResidentDAO {
    private static final String FILE_PATH = "data/residents.xml";
    private List<Resident> residents;

    public ResidentDAO() {
        ResidentXML wrapper = XMLUtil.readFromXML(new File(FILE_PATH), ResidentXML.class);
        residents = (wrapper != null) ? wrapper.getResidents() : new ArrayList<>();
    }

    public void save() {
        XMLUtil.writeToXML(new File(FILE_PATH), new ResidentXML(residents), ResidentXML.class);
    }

    public void add(Resident r) {
        residents.add(r);
        save();
    }

    public boolean delete(String cccd) {
        boolean removed = residents.removeIf(r -> r.getCccd().equals(cccd));
        if (removed) save();
        return removed;
    }

    public void update(Resident updated) {
        for (int i = 0; i < residents.size(); i++) {
            if (residents.get(i).getCccd().equals(updated.getCccd())) {
                residents.set(i, updated);
                save();
                break;
            }
        }
    }

    public Resident findByCccd(String cccd) {
        return residents.stream()
                .filter(r -> r.getCccd().equals(cccd))
                .findFirst().orElse(null);
    }

    public List<Resident> findByApartmentID(String apartmentID) {
        return residents.stream()
                .filter(r -> r.getApartmentID().equalsIgnoreCase(apartmentID))
                .collect(Collectors.toList());
    }

    public List<Resident> findByIDFamily(String idFamily) {
        return residents.stream()
                .filter(r -> r.getIDFamily().equalsIgnoreCase(idFamily))
                .collect(Collectors.toList());
    }

    public List<Resident> getAll() {
        return new ArrayList<>(residents);
    }

    // Sắp xếp theo từng thuộc tính (tăng & giảm)
    private List<Resident> sortBy(Comparator<Resident> comparator) {
        List<Resident> sorted = new ArrayList<>(residents);
        sorted.sort(comparator);
        return sorted;
    }

    public List<Resident> sortByNameAsc()         { return sortBy(Comparator.comparing(Resident::getName)); }
    public List<Resident> sortByNameDesc()        { return sortBy(Comparator.comparing(Resident::getName).reversed()); }

    public List<Resident> sortByAddressAsc()      { return sortBy(Comparator.comparing(Resident::getAddress)); }
    public List<Resident> sortByAddressDesc()     { return sortBy(Comparator.comparing(Resident::getAddress).reversed()); }

    public List<Resident> sortByBirthPlaceAsc()   { return sortBy(Comparator.comparing(Resident::getBirthPlace)); }
    public List<Resident> sortByBirthPlaceDesc()  { return sortBy(Comparator.comparing(Resident::getBirthPlace).reversed()); }

    public List<Resident> sortByApartmentIDAsc()  { return sortBy(Comparator.comparing(Resident::getApartmentID)); }
    public List<Resident> sortByApartmentIDDesc() { return sortBy(Comparator.comparing(Resident::getApartmentID).reversed()); }

    public List<Resident> sortByIDFamilyAsc()     { return sortBy(Comparator.comparing(Resident::getIDFamily)); }
    public List<Resident> sortByIDFamilyDesc()    { return sortBy(Comparator.comparing(Resident::getIDFamily).reversed()); }
}
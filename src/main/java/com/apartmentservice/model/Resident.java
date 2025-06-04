package com.apartmentservice.model;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlElement;
import java.io.Serializable;

@XmlRootElement(name = "resident")
@XmlAccessorType(XmlAccessType.FIELD)
public class Resident extends Person implements Serializable{

    @XmlElement(name = "IDFamily")
    private String IDFamily;

    @XmlElement(name = "sex")
    private String sex;

    @XmlElement(name = "role")
    private String role;

    @XmlElement(name = "birthPlace")
    private String birthPlace;

    @XmlElement(name = "phoneNumber")
    private String phoneNumber;

    @XmlElement(name = "apartmentID")
    private String apartmentID;

    public Resident() {
        super();
    }

    public Resident(String cccd, String name, String birthday, String address,
                    String IDFamily, String sex, String role, String birthPlace,
                    String phoneNumber, String apartmentID) {
        super(cccd, name, birthday, address);
        this.IDFamily = IDFamily;
        this.sex = sex;
        this.role = role;
        this.birthPlace = birthPlace;
        this.phoneNumber = phoneNumber;
        this.apartmentID = apartmentID;
    }

    public String getIDFamily() {
        return IDFamily;
    }

    public void setIDFamily(String IDFamily) {
        this.IDFamily = IDFamily;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBirthPlace() {
        return birthPlace;
    }

    public void setBirthPlace(String birthPlace) {
        this.birthPlace = birthPlace;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getApartmentID() {
        return apartmentID;
    }

    public void setApartmentID(String apartmentID) {
        this.apartmentID = apartmentID;
    }

    @Override
    public String toString() {
        return String.format(
            "Họ tên: %s, CCCD: %s, Ngày sinh: %s, Nơi sinh: %s, Giới tính: %s, Vai trò: %s, SĐT: %s, Địa chỉ: %s, Mã gia đình: %s, Căn hộ: %s",
            getName(), getCccd(), getBirthday(), birthPlace, sex, role, phoneNumber, getAddress(), IDFamily, apartmentID
        );
    }
}
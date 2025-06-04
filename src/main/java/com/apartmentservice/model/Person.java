package com.apartmentservice.model;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;


@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Person {

    @XmlElement(name = "cccd")
    protected String cccd;

    @XmlElement(name = "name")
    protected String name;

    @XmlElement(name = "birthday")
    protected String birthday;

    @XmlElement(name = "address")
    protected String address;

    public Person() {
    }

    public Person(String cccd, String name, String birthday, String address) {
        this.cccd = cccd;
        this.name = name;
        this.birthday = birthday;
        this.address = address;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public String getLastName() {
        if (name == null || name.trim().isEmpty()) return "";
        int lastSpaceIndex = name.trim().lastIndexOf(" ");
        return lastSpaceIndex >= 0 ? name.trim().substring(lastSpaceIndex + 1) : name.trim();
    }

    public String getFirstName() {
        if (name == null || name.trim().isEmpty()) return "";
        int lastSpaceIndex = name.trim().lastIndexOf(" ");
        return lastSpaceIndex >= 0 ? name.trim().substring(0, lastSpaceIndex).trim() : "";
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("CCCD: %s, Họ tên: %s, Ngày sinh: %s, Địa chỉ: %s",
                cccd, name, birthday, address);
    }
}
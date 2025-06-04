package com.apartmentservice.utils;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import java.io.File;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class XMLUtil {

    public static <T> void writeToXML(File file, T object, Class<T> clazz) {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // Thêm dòng này
            // Ghi qua OutputStreamWriter để chắc chắn encoding
            try (java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {
                marshaller.marshal(object, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T readFromXML(File file, Class<T> clazz) {
        try {
            if (!file.exists()) return null;
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // Đọc qua InputStreamReader để đảm bảo UTF-8
            try (java.io.InputStreamReader reader = new java.io.InputStreamReader(
                    new java.io.FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {
                return (T) unmarshaller.unmarshal(reader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
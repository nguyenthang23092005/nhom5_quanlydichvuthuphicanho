package com.apartmentservice.manager;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.model.Account;
import com.apartmentservice.wrapper.AccountListWrapper;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private final String FILE_PATH = "data/account.xml";
    private List<Account> accounts;

    public AccountManager() {
        load();
    }

    private void load() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                accounts = new ArrayList<>();
                accounts.add(new Account("AC1", "ngthang", "ngthang", "ngthang@example.com"));
                save();
            } else {
                JAXBContext context = JAXBContext.newInstance(AccountListWrapper.class);
                Unmarshaller um = context.createUnmarshaller();
                AccountListWrapper wrapper = (AccountListWrapper) um.unmarshal(file);
                accounts = wrapper.getAccounts();
            }
        } catch (JAXBException e) {
            accounts = new ArrayList<>();
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            JAXBContext context = JAXBContext.newInstance(AccountListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            AccountListWrapper wrapper = new AccountListWrapper();
            wrapper.setAccounts(accounts);
            m.marshal(wrapper, new File(FILE_PATH));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public List<Account> getAll() {
        return accounts;
    }

    public Account findByUsername(String username) {
        for (Account acc : accounts) {
            if (acc.getUsername().equals(username)) {
                return acc;
            }
        }
        return null;
    }

    public void add(Account acc) {
        accounts.add(acc);
        save();
    }
}

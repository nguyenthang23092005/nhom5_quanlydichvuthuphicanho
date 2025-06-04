package com.apartmentservice.controller;
/**
 *
 * @author Nguyen Van Thang, Pham Van Su, Dang Anh Tuyen
 */
import com.apartmentservice.manager.AccountManager;
import com.apartmentservice.model.Account;

public class AuthController {
    private final AccountManager manager = new AccountManager();

    public Account login(String username, String password) {
        Account acc = manager.findByUsername(username);
        if (acc != null && acc.getPassword().equals(password)) {
            return acc;
        }
        return null;
    }

    public boolean register(String username, String email, String password) {
        if (manager.findByUsername(username) != null) {
            return false;
        }
        String id = "AC" + (manager.getAll().size() + 1);
        Account acc = new Account(id, username, password, email);
        manager.add(acc);
        return true;
    }
}
package com.example.librarymanagement;

import com.example.librarymanagement.dao.BookDAO;

public class TestDB {
    public static void main(String[] args) {
        // Updated to the new method name!
        BookDAO.setupFreshDatabase();
        System.out.println("Advanced Schema wiped and re-verified.");
    }
}
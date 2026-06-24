package com.example.librarymanagement.model;

public class Book {
    private int id;
    private String title;
    private String author;
    private boolean isAvailable;
    private int daysOverdue;
    private double fine;

    public Book(int id, String title, String author, boolean isAvailable, int daysOverdue, double fine) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
        this.daysOverdue = daysOverdue;
        this.fine = fine;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public int getDaysOverdue() { return daysOverdue; }
    public double getFine() { return fine; }
}
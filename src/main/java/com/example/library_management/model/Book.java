package com.example.library_management.model;

import java.util.Objects;

public class Book {
    private Integer id;
    private String serialNumber;
    private String name;
    private String authorName;
    private String genre;
    private boolean isAvailable;

    public Book() {}

    public Book(Integer id, String serialNumber, String name, String authorName, String genre, boolean isAvailable) {
        this.id = id;
        this.serialNumber = serialNumber;
        this.name = name;
        this.authorName = authorName;
        this.genre = genre;
        this.isAvailable = isAvailable;
    }
    
    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, serialNumber, name, authorName, genre, isAvailable);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Book other = (Book) obj;
        return Objects.equals(id, other.id) &&
               Objects.equals(serialNumber, other.serialNumber) &&
               Objects.equals(name, other.name) &&
               Objects.equals(authorName, other.authorName) &&
               Objects.equals(genre, other.genre) &&
               isAvailable == other.isAvailable;
    }

    @Override
    public String toString() {
        return "Book [id=" + id + ", serialNumber=" + serialNumber + ", name=" + name +
               ", authorName=" + authorName + ", genre=" + genre + ", available=" + isAvailable + "]";
    }

}
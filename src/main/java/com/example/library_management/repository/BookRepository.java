package com.example.library_management.repository;

import java.util.List;

import com.example.library_management.model.Book;

public interface BookRepository {

	public List<Book> findAll();

	public Book findBySerialNumber(String serialNumber);

	public void save(Book book);

	public void delete(String serialNumber);

}

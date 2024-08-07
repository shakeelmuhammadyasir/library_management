package com.example.library_management.repository.mongo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class BookMongoRepository implements BookRepository {

    private MongoCollection<Document> bookCollection;

    public BookMongoRepository(MongoClient client, String databaseName, String collectionName) {
        bookCollection = client
            .getDatabase(databaseName)
            .getCollection(collectionName);
    }

    @Override
    public List<Book> findAll() {
        return StreamSupport.stream(bookCollection.find().spliterator(), false)
                .map(this::fromDocumentToBook)
                .collect(Collectors.toList());
    }

    public List<Book> findByName(String name) {
        return StreamSupport.stream(bookCollection.find().spliterator(), false)
                .map(this::fromDocumentToBook)
                .filter(b -> b.getName().contains(name))
                .collect(Collectors.toList());
    }

    public Book findBySerialNumber(String serialNumber) {
        return StreamSupport.stream(bookCollection.find(Filters.eq("serialNumber", serialNumber)).spliterator(), false)
                .map(this::fromDocumentToBook)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(Book book) {
        bookCollection.insertOne(
                new Document()
                    .append("id", book.getId())
                    .append("serialNumber", book.getSerialNumber())
                    .append("name", book.getName())
                    .append("authorName", book.getAuthorName())
                    .append("genre", book.getGenre())
                    .append("isAvailable", book.isAvailable()));
    }

    @Override
    public void delete(String serialNumber) {
        bookCollection.deleteOne(Filters.eq("serialNumber", serialNumber));
    }

    private Book fromDocumentToBook(Document d) {
        return new Book(
            d.getInteger("id"),
            "" + d.get("serialNumber"),
            "" + d.get("name"),
            "" + d.get("authorName"),
            "" + d.get("genre"),
            d.getBoolean("isAvailable", true)
        );
    }

}

package com.example.library_management.repository.mongo;

import java.util.List;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.example.library_management.model.Book;
import com.example.library_management.repository.BookRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import java.util.stream.Collectors;

public class BookMongoRepository implements BookRepository {

	private static final String SERIAL_NUMBER_FIELD = "serialNumber";
	private static final String ID_FIELD = "id";
	private static final String NAME_FIELD = "name";
	private static final String AUTHOR_NAME_FIELD = "authorName";
	private static final String GENRE_FIELD = "genre";
	private static final String IS_AVAILABLE_FIELD = "isAvailable";

	private MongoCollection<Document> bookCollection;

	public BookMongoRepository(MongoClient client, String databaseName, String collectionName) {
		bookCollection = client.getDatabase(databaseName).getCollection(collectionName);
	}

	@Override
	public List<Book> findAll() {
		return StreamSupport.stream(bookCollection.find().spliterator(), false).map(this::fromDocumentToBook)
				.collect(Collectors.toList()); // Java 8 compatible
	}

	@Override
	public Book findBySerialNumber(String serialNumber) {
		return StreamSupport
				.stream(bookCollection.find(Filters.eq(SERIAL_NUMBER_FIELD, serialNumber)).spliterator(), false)
				.map(this::fromDocumentToBook).findFirst().orElse(null);
	}

	@Override
	public void save(Book book) {
		bookCollection.insertOne(
				new Document().append(ID_FIELD, book.getId()).append(SERIAL_NUMBER_FIELD, book.getSerialNumber())
						.append(NAME_FIELD, book.getName()).append(AUTHOR_NAME_FIELD, book.getAuthorName())
						.append(GENRE_FIELD, book.getGenre()).append(IS_AVAILABLE_FIELD, book.isAvailable()));
	}

	@Override
	public void delete(String serialNumber) {
		bookCollection.deleteOne(Filters.eq(SERIAL_NUMBER_FIELD, serialNumber));
	}

	private Book fromDocumentToBook(Document d) {
		return new Book(d.getInteger(ID_FIELD), "" + d.get(SERIAL_NUMBER_FIELD), "" + d.get(NAME_FIELD),
				"" + d.get(AUTHOR_NAME_FIELD), "" + d.get(GENRE_FIELD), d.getBoolean(IS_AVAILABLE_FIELD, true));
	}
}

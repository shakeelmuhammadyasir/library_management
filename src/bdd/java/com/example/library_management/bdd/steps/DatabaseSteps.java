package com.example.library_management.bdd.steps;

import org.bson.Document;
import com.example.library_management.bdd.LibrarySwingAppBDD;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;

public class DatabaseSteps {

	static final String DB_NAME = "library";
	static final String COLLECTION_NAME = "book";

	static final String BOOK_1_ID = "1";
	static final String BOOK_1_SERIAL_NUMBER = "SN123";
	static final String BOOK_1_NAME = "Book Name";
	static final String BOOK_1_AUTHOR_NAME = "Author";
	static final String BOOK_1_GENRE = "Genre";
	static final boolean BOOK_1_AVAILABLE = true;

	static final String BOOK_2_ID = "2";
	static final String BOOK_2_SERIAL_NUMBER = "SN456";
	static final String BOOK_2_NAME = "Second Book";
	static final String BOOK_2_AUTHOR_NAME = "Second Author";
	static final String BOOK_2_GENRE = "Another Genre";
	static final boolean BOOK_2_AVAILABLE = true;

	private MongoClient mongoClient;

	@Before
	public void setUp() {
		mongoClient = new MongoClient("localhost", LibrarySwingAppBDD.mongoPort);
		mongoClient.getDatabase(DB_NAME).drop();
	}

	@After
	public void tearDown() {
		mongoClient.close();
	}

	@Given("The database contains a few books")
	public void the_database_contains_a_few_books() {
		addTestBookToDatabase(BOOK_1_ID, BOOK_1_SERIAL_NUMBER, BOOK_1_NAME, BOOK_1_AUTHOR_NAME, BOOK_1_GENRE,
				BOOK_1_AVAILABLE);
		addTestBookToDatabase(BOOK_2_ID, BOOK_2_SERIAL_NUMBER, BOOK_2_NAME, BOOK_2_AUTHOR_NAME, BOOK_2_GENRE,
				BOOK_2_AVAILABLE);
	}

	@Given("The book is in the meantime removed from the database")
	public void the_book_is_in_the_meantime_removed_from_the_database() {
		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME)
				.deleteOne(Filters.eq("id", Integer.parseInt(BOOK_1_ID)));
	}

	private void addTestBookToDatabase(String id, String serial, String name, String author, String genre,
			boolean available) {

		Document bookDocument = new Document().append("id", Integer.parseInt(id)).append("serialNumber", serial)
				.append("name", name).append("authorName", author).append("genre", genre)
				.append("available", available);

		mongoClient.getDatabase(DB_NAME).getCollection(COLLECTION_NAME).insertOne(bookDocument);
	}

}

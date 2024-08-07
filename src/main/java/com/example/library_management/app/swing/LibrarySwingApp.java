package com.example.library_management.app.swing;

import java.awt.EventQueue;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.library_management.controller.LibraryController;
import com.example.library_management.repository.mongo.BookMongoRepository;
import com.example.library_management.view.swing.BookSwingView;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class LibrarySwingApp implements Callable<Void> {

    @Option(names = { "--mongo-host" }, description = "MongoDB host address", defaultValue = "localhost")
    private String mongoHost;

    @Option(names = { "--mongo-port" }, description = "MongoDB host port", defaultValue = "27017")
    private int mongoPort;

    @Option(names = { "--db-name" }, description = "Database name", defaultValue = "test")
    private String databaseName;

    @Option(names = { "--db-collection" }, description = "Collection name", defaultValue = "book")
    private String collectionName;

    public static void main(String[] args) {
        new CommandLine(new LibrarySwingApp()).execute(args);
    }

    @Override
    public Void call() throws Exception {
        EventQueue.invokeLater(() -> {
            try {
                BookMongoRepository bookRepository = new BookMongoRepository(
                        new MongoClient(new ServerAddress(mongoHost, mongoPort)), databaseName, collectionName);
                BookSwingView bookView = new BookSwingView();
                LibraryController libraryController = new LibraryController(bookView, bookRepository);
                bookView.setLibraryController(libraryController);
                bookView.setVisible(true);
                libraryController.allBooks();
            } catch (Exception e) {
                Logger.getLogger(getClass().getName())
                    .log(Level.SEVERE, "Exception", e);
            }
        });
        return null;
    }
}

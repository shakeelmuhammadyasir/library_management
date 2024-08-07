package com.example.library_management.view.swing;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.example.library_management.controller.LibraryController;
import com.example.library_management.model.Book;
import com.example.library_management.view.BookView;

public class BookSwingView extends JFrame implements BookView {
	private static final long serialVersionUID = 1L;
	private JTextField idTextField;
	private JTextField serialNumberTextField;
	private JTextField nameTextField;
	private JTextField authorNameTextField;
	private JTextField genreTextField;
	private JCheckBox availableCheckBox;
	private JButton addButton;
	private JButton deleteButton;
	private JButton searchButton;
	private JButton clearButton;
	private JComboBox<String> serialNumberComboBox;
	private JTable bookTable;
	private DefaultTableModel tableModel;
	private LibraryController libraryController;
	private JLabel errorLabel;

	public BookSwingView() {
		setTitle("Library Management System");
		setSize(900, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		// Main Panel Layout
		setLayout(new BorderLayout());

		// Input Panel
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		add(inputPanel, BorderLayout.NORTH);

		// Input Fields
		idTextField = createLabeledTextField("ID", inputPanel, gbc, 0);
		idTextField.setName("ID");

		serialNumberTextField = createLabeledTextField("Serial Number", inputPanel, gbc, 1);
		serialNumberTextField.setName("Serial Number");

		nameTextField = createLabeledTextField("Name", inputPanel, gbc, 2);
		nameTextField.setName("Name");

		authorNameTextField = createLabeledTextField("Author Name", inputPanel, gbc, 3);
		authorNameTextField.setName("Author Name");

		genreTextField = createLabeledTextField("Genre", inputPanel, gbc, 4);
		genreTextField.setName("Genre");

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		availableCheckBox = new JCheckBox("Available");
		availableCheckBox.setName("Available");
		inputPanel.add(availableCheckBox, gbc);

		// Error Label
		gbc.gridy = 6;
		gbc.weighty = 0;
		errorLabel = new JLabel(" ");
		errorLabel.setForeground(Color.RED);
		inputPanel.add(errorLabel, gbc);

		// Buttons Panel
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());
		add(buttonsPanel, BorderLayout.SOUTH);

		addButton = new JButton("Add Book");
		addButton.setName("Add Book");
		buttonsPanel.add(addButton);

		deleteButton = new JButton("Delete Selected Book");
		deleteButton.setName("Delete Selected Book");
		buttonsPanel.add(deleteButton);

		serialNumberComboBox = new JComboBox<>();
		serialNumberComboBox.setName("Serial Number ComboBox");
		buttonsPanel.add(serialNumberComboBox);

		searchButton = new JButton("Search");
		searchButton.setName("Search");
		buttonsPanel.add(searchButton);

		clearButton = new JButton("Clear Fields");
		clearButton.setName("Clear Fields");
		buttonsPanel.add(clearButton);

		// Table
		tableModel = new DefaultTableModel(
				new Object[] { "ID", "Serial Number", "Name", "Author Name", "Genre", "Available" }, 0);
		bookTable = new JTable(tableModel);
		add(new JScrollPane(bookTable), BorderLayout.CENTER);

		// Action Listeners
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Integer id = Integer.parseInt(idTextField.getText());
					Book book = new Book(id, serialNumberTextField.getText(), nameTextField.getText(),
							authorNameTextField.getText(), genreTextField.getText(), availableCheckBox.isSelected());
					libraryController.newBook(book);
				} catch (NumberFormatException ex) {
					showError("ID must be an integer");
				}
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = bookTable.getSelectedRow();
				if (selectedRow != -1) {
					String serialNumber = (String) tableModel.getValueAt(selectedRow, 1);
					Book book = new Book((Integer) tableModel.getValueAt(selectedRow, 0), serialNumber,
							(String) tableModel.getValueAt(selectedRow, 2),
							(String) tableModel.getValueAt(selectedRow, 3),
							(String) tableModel.getValueAt(selectedRow, 4),
							(Boolean) tableModel.getValueAt(selectedRow, 5));
					libraryController.deleteBook(book);
				} else {
					showError("Please select a book to delete");
				}
			}
		});

		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedSerialNumber = (String) serialNumberComboBox.getSelectedItem();
				if (selectedSerialNumber != null) {
					libraryController.searchBook(selectedSerialNumber);
				} else {
					showError("Please select a serial number from the drop-down");
				}
			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearFields();
			}
		});
	}

	private JTextField createLabeledTextField(String label, JPanel panel, GridBagConstraints gbc, int row) {
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		JLabel jLabel = new JLabel(label);
		panel.add(jLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		JTextField textField = new JTextField();
		textField.setColumns(20);
		panel.add(textField, gbc);

		return textField;
	}

	public void setLibraryController(LibraryController libraryController) {
		this.libraryController = libraryController;
	}

	@Override
	public void showAllBooks(List<Book> books) {
		tableModel.setRowCount(0);
		serialNumberComboBox.removeAllItems();
		for (Book book : books) {
			tableModel.addRow(new Object[] { book.getId(), book.getSerialNumber(), book.getName(), book.getAuthorName(),
					book.getGenre(), book.isAvailable() });
			serialNumberComboBox.addItem(book.getSerialNumber());
		}
	}

	@Override
	public void bookAdded(Book book) {
		tableModel.addRow(new Object[] { book.getId(), book.getSerialNumber(), book.getName(), book.getAuthorName(),
				book.getGenre(), book.isAvailable() });
		serialNumberComboBox.addItem(book.getSerialNumber());
		clearFields();
	}

	@Override
	public void bookRemoved(Book book) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (tableModel.getValueAt(i, 1).equals(book.getSerialNumber())) {
				tableModel.removeRow(i);
				serialNumberComboBox.removeItem(book.getSerialNumber());
				break;
			}
		}
		clearFields();
	}

	@Override
	public void showSearchedBooks(Book book) {
		tableModel.setRowCount(0);
		if (book != null) {
			tableModel.addRow(new Object[] { book.getId(), book.getSerialNumber(), book.getName(), book.getAuthorName(),
					book.getGenre(), book.isAvailable() });
		}
	}

	@Override
    public void showError(String message, Book book) {
        showError(message);
    }

    @Override
    public void showErrorBookNotFound(String message) {
        showError(message);
    }

    @Override
    public void showErrorBookNotFound(String message, Book book) {
        showError(message);
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

	private void clearFields() {
		idTextField.setText("");
		serialNumberTextField.setText("");
		nameTextField.setText("");
		authorNameTextField.setText("");
		genreTextField.setText("");
		availableCheckBox.setSelected(false);
	}
	
	public JLabel getErrorLabel() {
        return errorLabel;
    }
}

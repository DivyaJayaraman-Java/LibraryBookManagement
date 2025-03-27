package org.example;

import java.sql.*;
import java.util.Scanner;

public class LibraryBookManagementJava {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";  // Change to your MySQL username
    private static final String PASSWORD = "Temp!1234";  // Change to your MySQL password

    private static Connection conn;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to Database!");

            while (true) {
                System.out.println("\nLibrary Management System");
                System.out.println("1. Add Book");
                System.out.println("2. View All Books");
                System.out.println("3. Search Book");
                System.out.println("4. Update Book Details");
                System.out.println("5. Delete Book");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> viewBooks();
                    case 3 -> searchBook();
                    case 4 -> updateBook();
                    case 5 -> deleteBook();
                    case 6 -> {
                        System.out.println("Exiting system...");
                        conn.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Add Book
    private static void addBook() {
        try {
            System.out.print("Enter BookId: ");
            String bookId = scanner.nextLine();
            System.out.print("Enter Title: ");
            String title = scanner.nextLine();
            System.out.print("Enter Author: ");
            String author = scanner.nextLine();
            System.out.print("Enter Genre: ");
            String genre = scanner.nextLine();
            System.out.print("Enter Availability (Available/Checked Out): ");
            String availability = scanner.nextLine();

            String query = "INSERT INTO books (bookId,title, author, genre, availability) VALUES (?, ?, ?, ? , ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setString(1, bookId);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setString(4, genre);
            stmt.setString(5, availability);

            stmt.executeUpdate();
            System.out.println("Book added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //View book
    private static void viewBooks() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            System.out.println("\nBook List:");
            while (rs.next()) {
                System.out.printf(" BookId: %s| Title: %s | Author: %s | Genre: %s | Availability: %s\n",
                        rs.getInt("bookid"), rs.getString("title"),
                        rs.getString("author"), rs.getString("genre"), rs.getString("availability"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Search book
    private static void searchBook() {
        try {
            System.out.print("Enter Book ID or Title: ");
            String search = scanner.nextLine();

            String query = "SELECT * FROM books WHERE bookid = ? OR title LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, search);
            stmt.setString(2, "%" + search + "%");

            ResultSet rs = stmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No book found.");
                return;
            }

            while (rs.next()) {
                System.out.printf("BookId: %d | Title: %s | Author: %s | Genre: %s | Availability: %s\n",
                        rs.getInt("bookid"), rs.getString("title"),
                        rs.getString("author"), rs.getString("genre"), rs.getString("availability"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //updatebook
    private static void updateBook() {
        try {
            System.out.print("Enter Book ID to update: ");
            int bookId = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Enter new Title (leave blank to keep unchanged): ");
            String title = scanner.nextLine();
            System.out.print("Enter new Author (leave blank to keep unchanged): ");
            String author = scanner.nextLine();
            System.out.print("Enter new Genre (leave blank to keep unchanged): ");
            String genre = scanner.nextLine();
            System.out.print("Enter new Availability (Available/Checked Out, leave blank to keep unchanged): ");
            String availability = scanner.nextLine();

            StringBuilder query = new StringBuilder("UPDATE books SET ");
            boolean needsComma = false;

            if (!title.isEmpty()) {
                query.append("title = '").append(title).append("'");
                needsComma = true;
            }
            if (!author.isEmpty()) {
                if (needsComma) query.append(", ");
                query.append("author = '").append(author).append("'");
                needsComma = true;
            }
            if (!genre.isEmpty()) {
                if (needsComma) query.append(", ");
                query.append("genre = '").append(genre).append("'");
                needsComma = true;
            }
            if (!availability.isEmpty()) {
                if (needsComma) query.append(", ");
                query.append("availability = '").append(availability).append("'");
            }

            query.append(" WHERE bookid = ").append(bookId);

            Statement stmt = conn.createStatement();
            int rowsUpdated = stmt.executeUpdate(query.toString());

            if (rowsUpdated > 0) {
                System.out.println("Book updated successfully.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //deletebook
    private static void deleteBook() {
        try {
            System.out.print("Enter Book ID to delete: ");
            int bookId = scanner.nextInt();
            scanner.nextLine();

            String query = "DELETE FROM books WHERE bookid = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, bookId);

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

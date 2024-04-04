package dev.profitsoft.intership;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Book {
    private static final int DEFAULT_PUB_YEAR = -1;
    private String title;
    private int publishYear;
    private ArrayList<Author> authors = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();

    public Book() {
        this("", DEFAULT_PUB_YEAR, new ArrayList<>(), new ArrayList<>());
    }

    public Book(String title, int publishYear, ArrayList<Author> authors, ArrayList<String> categories) {
        this.title = title;
        this.publishYear = publishYear;
        this.authors = authors;
        this.categories = categories;
    }
    public void addAuthor(Author author) { authors.add(author); }
    public void addCategory(String category) { categories.add(category); }

    public void setTitle(String title) { this.title = title; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }
    public void setAuthors(ArrayList<Author> authors) { this.authors = authors; }
    public void setCategories(ArrayList<String> categories) { this.categories = categories; }

    public String getTitle() { return title; }
    public int getPublishYear() { return publishYear; }
    public ArrayList<Author> getAuthors() { return authors; }
    public ArrayList<String> getCategories() { return categories; }

    public boolean isValidObject() {
        return title != null && !title.isEmpty()
                && publishYear != DEFAULT_PUB_YEAR
                && authors != null && !authors.isEmpty()
                && categories != null && !categories.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Title: %s\nPublish year: %d\nAuthor(s): [%s]\nCategories: [%s]\n",
                title,
                publishYear,
                authors.stream().map(Author::toString).collect(Collectors.joining("; ")),
                String.join("; ", categories)
        );
    }
}

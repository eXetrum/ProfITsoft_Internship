package dev.profitsoft.intership;

import java.util.ArrayList;

public class Book {
    private static final int DEFAULT_PUB_YEAR = -1;
    private String title;
    private int publishYear;
    private ArrayList<String> authors;
    private ArrayList<String> subjects;

    public Book() {
        this("", DEFAULT_PUB_YEAR, new ArrayList<>(), new ArrayList<>());
    }

    public Book(String title, int publishYear, ArrayList<String> authors, ArrayList<String> subjects) {
        this.title = title;
        this.publishYear = publishYear;
        this.authors = authors;
        this.subjects = subjects;
    }
    public void addAuthor(String author) { authors.add(author); }
    public void addCategory(String category) { subjects.add(category); }

    public void setTitle(String title) { this.title = title; }
    public void setPublishYear(int publishYear) { this.publishYear = publishYear; }
    public void setAuthors(ArrayList<String> authors) { this.authors = authors; }
    public void setSubjects(ArrayList<String> subjects) { this.subjects = subjects; }

    public String getTitle() { return title; }
    public int getPublishYear() { return publishYear; }
    public ArrayList<String> getAuthors() { return authors; }
    public ArrayList<String> getSubjects() { return subjects; }

    public boolean isValidObject() {
        return title != null && !title.isEmpty()
                && publishYear != DEFAULT_PUB_YEAR
                && authors != null && !authors.isEmpty()
                && subjects != null && !subjects.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Title: %s\nPublish year: %d\nAuthor(s): [%s]\nSubject(s): [%s]\n",
                title,
                publishYear,
                String.join("; ", authors),
                String.join("; ", subjects)
        );
    }
}

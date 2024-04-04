package dev.profitsoft.intership;

/*
 * Book writer
 */
public class Author {
    private String name;
    public Author(String name) {
        this.name = name;
    }
    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    /*
     * Returns author name
     */
    @Override
    public String toString() { return name; }
}

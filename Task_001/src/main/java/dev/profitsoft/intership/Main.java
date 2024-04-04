package dev.profitsoft.intership;

public class Main {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: java -jar BooksAnalyzer.jar <json folder path> <attribute name>");
            System.out.println("\t<attribute name>: title, subject, author, publish_year");
            return;
        }

        BooksAnalyzer.run(args[0], args[1]);
    }
}
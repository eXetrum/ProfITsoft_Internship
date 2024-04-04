package dev.profitsoft.intership;

public class Main {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: java -jar BooksAnalyzer.jar <json folder path> <attribute name>");
            return;
        }

        BooksAnalyzer.run(args[0], args[1]);
    }
}
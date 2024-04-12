package dev.profitsoft.intership;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if(args.length != 2) {
            System.out.println("Usage: java -jar BooksAnalyzer.jar <json folder path> <attribute name>");
            Class<?> bookClass = Book.class;
            System.out.println("\t<attribute name>: " +
                    Arrays.stream(bookClass.getDeclaredFields())
                            .filter(e -> !Modifier.isStatic(e.getModifiers()))
                            .map(Field::getName)
                            .collect(Collectors.joining(", "))
            );
            return;
        }

        BooksAnalyzer.run(args[0], args[1]);
    }
}
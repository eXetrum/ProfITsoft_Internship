package dev.profitsoft.intership;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Usage: java -jar BooksAnalyzer.jar <json folder path> <attribute name> [-verbose] [-pool <size>]");
            Class<?> bookClass = Book.class;
            System.out.println("\t<attribute name>: " +
                    Arrays.stream(bookClass.getDeclaredFields())
                            .filter(e -> !Modifier.isStatic(e.getModifiers()))
                            .map(Field::getName)
                            .collect(Collectors.joining(", "))
                    + "\t-verbose: (optional) print debug info"
                    + "\t-pool <size>: (optional) set thread pool size"
            );
            return;
        }

        boolean verbose = args.length > 2 && Arrays.asList(args).subList(2, args.length)
                .stream()
                .anyMatch(x -> x.equalsIgnoreCase("-verbose"));

        int threadPoolSize = BooksAnalyzer.DEFAULT_THREAD_POOL_SIZE;
        if(args.length > 2) {
            for(int idx = 0; idx < args.length; ++idx) {
                if("-pool".equalsIgnoreCase(args[idx]) && idx + 1 < args.length) {
                    try { threadPoolSize = Integer.parseInt(args[idx + 1]); }
                    catch (Exception e) {}
                }
            }
        }

        BooksAnalyzer.run(args[0], args[1], threadPoolSize, verbose);
    }
}
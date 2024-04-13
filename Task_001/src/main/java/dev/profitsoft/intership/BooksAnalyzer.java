package dev.profitsoft.intership;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

public class BooksAnalyzer {
    private static final int THREAD_POOL_SIZE = 1;
    private static boolean verbose = false;

    public static void run(String dataFolderPath, String attributeName) {
        run(dataFolderPath, attributeName, false);
    }

    public static void run(String dataFolderPath, String attributeName, boolean verbose) {
        BooksAnalyzer.verbose = verbose;

        long startTime = System.nanoTime();
        try {
            File folder = new File(dataFolderPath);

            if(!folder.exists()) {
                System.err.printf("Folder: \"%s\" not found\n", dataFolderPath);
                return;
            }

            if(!folder.isDirectory()) {
                System.err.printf("Specified path \"%s\" is not a folder\n", dataFolderPath);
                return;
            }

            Class<?> bookClass = Book.class;
            Optional<Field> bookField = Arrays.stream(bookClass.getDeclaredFields())
                    .filter(e -> e.getName().equalsIgnoreCase(attributeName))
                    .findFirst();

            if(bookField.isEmpty()) {
                System.err.printf("Attribute \"%s\" not found\n", attributeName);
                return;
            }
            bookField.get().setAccessible(true);

            // Get directory content, json files only
            File[] listOfFiles = folder.listFiles(e -> e.isFile() && e.getName().endsWith(".json"));
            if(listOfFiles == null) {
                System.err.printf("Folder \"%s\" is empty.\n", dataFolderPath);
                return;
            }

            System.out.printf("Processing folder: %s\n", dataFolderPath);
            System.out.printf("Attribute name: %s\n", attributeName);
            System.out.printf("Thread pool size: %d\n", THREAD_POOL_SIZE);

            Statistic statistic = new Statistic();

            // Process files via thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            List<Future<Statistic>> results = new ArrayList<>();
            for(File file: listOfFiles) {
                results.add(executorService.submit(() -> processFile(file, bookField.get())));
            }

            // Collect and combine results
            results.forEach(partialResult -> {
                try {
                    statistic.merge(partialResult.get());
                } catch (InterruptedException | ExecutionException e) {
                    if(verbose) System.err.println(e.getMessage());
                }
            });

            if(verbose) System.out.println("Statistic: \n" + statistic);

            // Save statistic
            statistic.save("statistic_by_" + attributeName + ".xml");

            // Cleanup
            executorService.shutdown();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        long elapsedTime = System.nanoTime() - startTime;
        System.out.printf("Elapsed time: %d ms\n", elapsedTime / 1_000_000);
    }

    private static Statistic processFile(File file, Field entityField) {
        Statistic statistic = new Statistic();

        StringBuilder sb = new StringBuilder();
        if(verbose) sb.append(String.format("Processing file: %s\n", file.getName()));

        try (Parser parser = new Parser(file)) {
            Book book;
            while((book = parser.next()) != null) {

                //if(verbose) sb.append(book).append("\n");

                if(!book.isValidObject()) {
                    if(verbose) sb.append("\tWarning: Incomplete book object. Skip.\n");
                    continue;
                }

                try {
                    Object fieldValue = entityField.get(book);
                    if(fieldValue instanceof ArrayList<?>) {
                        ArrayList<String> container = (ArrayList<String>) fieldValue;
                        container.forEach(statistic::updateFrequency);
                    } else {
                        statistic.updateFrequency(fieldValue);
                    }

                } catch (IllegalAccessException e) {
                    if(verbose) sb.append("\tWarning: Incomplete book object. Skip.\n");
                }
            }
        } catch (Exception e) {
            if(verbose) sb.append("\t" + e.getMessage() + "\n");
        }

        if(verbose) sb.append(String.format("\tUnique entries: %d\n", statistic.size()));

        if(verbose && !sb.isEmpty()) System.out.println(sb);

        return statistic;
    }
}

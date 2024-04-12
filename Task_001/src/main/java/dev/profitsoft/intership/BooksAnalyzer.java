package dev.profitsoft.intership;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

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

            // Process files via thread pool
            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            Map<Object, Integer> statistic = new HashMap<>();
            List<Future<Map<Object, Integer>>> results = new ArrayList<>();
            for(File file: listOfFiles) {
                results.add(executorService.submit(() -> processFile(file, bookField.get())));
            }

            // Collect and combine results
            for(Future<Map<Object, Integer>> result: results) {
                statistic.putAll(result.get());
            }

            // Sort by count DESC
            Map<Object, Integer> sortedMap = statistic.entrySet().stream()
                    .sorted(Map.Entry.<Object, Integer>comparingByValue().reversed())
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

            // Save results
            saveStatistic(sortedMap, "statistic_by_" + attributeName + ".xml");

            executorService.shutdown();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }

        long elapsedTime = System.nanoTime() - startTime;
        System.out.printf("Elapsed time: %d ms\n", elapsedTime / 1_000_000);
    }

    private static void saveStatistic(Map<Object, Integer> statistic, String path) {
        try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            out.println("<statistics>");
            for(Map.Entry<Object, Integer> entry : statistic.entrySet()) {
                out.println("\t<item>");
                out.println("\t\t<value>" + entry.getKey() + "</value>");
                out.println("\t\t<count>" + entry.getValue() + "</count>");
                out.println("\t</item>");
            }
            out.println("</statistics>");

            System.out.println("Statistic data saved to: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Book parseJsonObject(JsonParser parser) throws IOException {
        Book book = new Book();

        JsonToken token = parser.getCurrentToken();
        if (token != JsonToken.START_OBJECT) {
            if(verbose) {
                System.out.printf("Warning: processing entry failed at %d %d, expected json object(s). Skip\n",
                        parser.getCurrentLocation().getLineNr(),
                        parser.getCurrentLocation().getColumnNr()
                );
            }
            return book;
        }

        // Process json fields till end of json object definition
        token = parser.nextToken();
        while (token != null && token != JsonToken.END_OBJECT) {
            // "filed name" :
            if (token == JsonToken.FIELD_NAME) {
                String fieldName = parser.getCurrentName();
                // Move to the field value
                token = parser.nextToken();
                // : "field value(s)"
                if (token == JsonToken.VALUE_STRING && "title".equals(fieldName)) {
                    book.setTitle(parser.getText());
                } else if (token == JsonToken.VALUE_NUMBER_INT && "publish_year".equals(fieldName)) {
                    book.setPublishYear(parser.getNumberValue().intValue());
                } else if (token == JsonToken.START_ARRAY &&
                        ("subject".equals(fieldName) || "authors".equals(fieldName))) {
                    // "subject" / "authors":
                    // "["
                    token = parser.nextToken();
                    while (token != null && token != JsonToken.END_ARRAY) {
                        if (token == JsonToken.VALUE_STRING) {
                            String value = parser.getText();
                            if("authors".equals(fieldName))
                                book.addAuthor(value);
                            else
                                book.addCategory(value);
                        }
                        token = parser.nextToken();
                    }
                    // "]"
                }
            }
            // Next field name
            token = parser.nextToken();
        }

        return book;
    }

    private static void accumulateStatistic(Book book, Field entityField, Map<Object, Integer> statistic) {
        if(!book.isValidObject()) return;

        try {
            Object fieldValue = entityField.get(book);
            if(fieldValue instanceof ArrayList<?>) {
                ArrayList<String> container = (ArrayList<String>) fieldValue;
                for(String e : container) {
                    statistic.put(e, statistic.getOrDefault(e, 0) + 1);
                }
            } else {
                statistic.put(fieldValue, statistic.getOrDefault(fieldValue, 0) + 1);
            }

        } catch (IllegalAccessException e) {
            if(verbose) System.out.println("Warning: Incomplete book object. Skip.");
        }
    }

    private static Map<Object, Integer> processFile(File file, Field entityField) {
        Map<Object, Integer> statistic = new HashMap<>();

        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser(new File(file.getAbsolutePath()))) {
            // Get first token
            JsonToken token = parser.nextToken();

            // Expected json array
            if(token != JsonToken.START_ARRAY) {
                if(verbose) System.err.printf("Error: processing file \"%s\", expected json array\n", file.getName());
                return statistic;
            }

            // Just in case if this file contains empty array
            token = parser.nextToken();
            if(token == JsonToken.END_ARRAY) {
                if(verbose) System.err.printf("Error: processing file \"%s\" failed, unexpected end of array\n", file.getName());
                return statistic;
            }

            // Process till end of array/file
            while (token != null && token != JsonToken.END_ARRAY) {
                Book book = parseJsonObject(parser);
                accumulateStatistic(book, entityField, statistic);
                // Next object in array of objects
                token = parser.nextToken();
            }
        } catch (IOException e) {
            if(verbose) System.err.println("Error: " + e.getMessage());
        }
        return statistic;
    }
}

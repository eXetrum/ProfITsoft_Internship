package dev.profitsoft.intership;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class BooksAnalyzer {
    public static void run(String dataFolderPath, String attributeName) {
        try {
            File folder = new File(dataFolderPath);

            if(!folder.exists()) {
                System.out.printf("Folder: \"%s\" not found\n", dataFolderPath);
                return;
            }

            if(!folder.isDirectory()) {
                System.out.printf("Specified path \"%s\" is not a folder\n", dataFolderPath);
                return;
            }

            Class<?> bookClass = Book.class;
            Optional<Field> bookField = Arrays.stream(bookClass.getDeclaredFields())
                    .filter(e -> e.getName().equalsIgnoreCase(attributeName))
                    .findFirst();

            if(bookField.isEmpty()) {
                System.out.printf("Attribute \"%s\" not found\n", attributeName);
                return;
            }
            bookField.get().setAccessible(true);

            // Get directory content, json files only
            File[] listOfFiles = folder.listFiles(e -> e.isFile() && e.getName().endsWith(".json"));
            if(listOfFiles == null) {
                System.out.printf("Folder \"%s\" is empty.\n", dataFolderPath);
                return;
            }

            // Process each file one by one
            Map<Object, Integer> statistic = new HashMap<>();
            for(File file: listOfFiles) {
                System.out.printf("Processing file: %s\n", file.getName());
                statistic.putAll(processFile(file, bookField.get()));
            }

            Map<Object, Integer> sortedMap = statistic.entrySet().stream()
                    .sorted(Map.Entry.<Object, Integer>comparingByValue().reversed())
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

            sortedMap.forEach((key, value) -> System.out.println(key + ": " + value));

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static Book parseJsonObject(JsonParser parser) throws IOException {
        Book book = new Book();

        JsonToken token = parser.getCurrentToken();
        if (token != JsonToken.START_OBJECT) {
            System.out.printf("Warning: processing entry failed at %d %d, expected json object(s). Skip\n",
                    parser.getCurrentLocation().getLineNr(),
                    parser.getCurrentLocation().getColumnNr()
            );
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

        System.out.println(book);
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
            System.out.println("Warning: Incomplete book object. Skip.");
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
                System.err.printf("Error: processing file \"%s\", expected json array\n", file.getName());
                return statistic;
            }

            // Just in case if this file contains empty array
            token = parser.nextToken();
            if(token == JsonToken.END_ARRAY) {
                System.err.printf("Error: processing file \"%s\" failed, unexpected end of array\n", file.getName());
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
            System.err.println("Error: " + e.getMessage());
        }
        return statistic;
    }
}

package dev.profitsoft.intership;

import java.io.File;
import java.io.IOException;

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

            // Get directory content, json files only
            File[] listOfFiles = folder.listFiles(e -> e.isFile() && e.getName().endsWith(".json"));
            if(listOfFiles == null) {
                System.out.printf("Folder \"%s\" is empty.\n", dataFolderPath);
                return;
            }

            // Process each file one by one
            for(File file: listOfFiles) {
                System.out.printf("Processing file: %s\n", file.getName());
                processFile(file, attributeName);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

    private static void processFile(File file, String attributeName) {
        JsonFactory factory = new JsonFactory();

        try (JsonParser parser = factory.createParser(new File(file.getAbsolutePath()))) {
            // Get first token
            JsonToken token = parser.nextToken();

            // Expected json array
            if(token != JsonToken.START_ARRAY) {
                System.err.printf("Error processing file \"%s\", expected json array\n",
                        file.getName());
                return;
            }

            // Just in case if this file is empty
            token = parser.nextToken();
            if(token == JsonToken.END_ARRAY) {
                System.err.printf("Error: processing file \"%s\" failed, unexpected end of array\n",
                        file.getName());
                return;
            }

            // Process till end of array/file
            while (token != null && token != JsonToken.END_ARRAY) { // Last token must be "array end"
                if (token != JsonToken.START_OBJECT) {
                    System.out.println("Warning: processing entry failed, expected json object(s). Skip");
                    token = parser.nextToken();
                    continue;
                }

                Book book = new Book();
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
                                        book.addAuthor(new Author(value));
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

                if(book.isValidObject()) {
                    System.out.println(book);
                } else {
                    System.out.println("Warning: Incomplete book object. Skip.");
                }
                // Next object in array of objects
                token = parser.nextToken();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

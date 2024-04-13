package dev.profitsoft.intership;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.File;

public class Parser implements AutoCloseable {
    private final JsonParser jsonParser;
    private JsonToken token;

    public Parser(File file) throws Exception {

        jsonParser = new JsonFactory().createParser(file);

        token = jsonParser.nextToken();
        // Expected json array
        if(token != JsonToken.START_ARRAY) {
            throw new Exception(String.format("Error: processing file \"%s\", expected json array", file.getName()));
        }

        // Just in case if this file contains empty array
        token = jsonParser.nextToken();
    }

    @Override
    public void close() throws Exception {
        jsonParser.close();
    }

    public Book next() throws Exception {
        if(token == null || token == JsonToken.END_ARRAY) return null;

        Book book = parseJsonObject();
        token = jsonParser.nextToken();

        return book;
    }

    private Book parseJsonObject() throws Exception {
        Book book = new Book();

        token = jsonParser.getCurrentToken();
        if (token != JsonToken.START_OBJECT) {
            throw new Exception("Error: Expected json object start");
        }

        // Process json fields till end of json object definition
        token = jsonParser.nextToken();
        while (token != null && token != JsonToken.END_OBJECT) {
            // "filed name" :
            if (token == JsonToken.FIELD_NAME) {
                String fieldName = jsonParser.getCurrentName();
                // Move to the field value
                token = jsonParser.nextToken();
                // : "field value(s)"
                if (token == JsonToken.VALUE_STRING && "title".equals(fieldName)) {
                    book.setTitle(jsonParser.getText());
                } else if (token == JsonToken.VALUE_NUMBER_INT && "publish_year".equals(fieldName)) {
                    book.setPublishYear(jsonParser.getNumberValue().intValue());
                } else if (token == JsonToken.START_ARRAY &&
                        ("subject".equals(fieldName) || "authors".equals(fieldName))) {
                    // "subject" / "authors":
                    // "["
                    token = jsonParser.nextToken();
                    while (token != null && token != JsonToken.END_ARRAY) {
                        if (token == JsonToken.VALUE_STRING) {
                            String value = jsonParser.getText();
                            if("authors".equals(fieldName))
                                book.addAuthor(value);
                            else
                                book.addCategory(value);
                        }
                        token = jsonParser.nextToken();
                    }
                    // "]"
                }
            }
            // Next field name
            token = jsonParser.nextToken();
        }

        return book;
    }

}

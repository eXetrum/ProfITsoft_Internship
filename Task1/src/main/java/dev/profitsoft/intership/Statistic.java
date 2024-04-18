package dev.profitsoft.intership;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Statistic {
    private final Map<Object, Integer> data;

    public Statistic() {
        data = new HashMap<>();
    }

    public void updateFrequency(Object key) {
        data.put(key, data.getOrDefault(key, 0) + 1);
    }

    public void merge(Statistic other) {
        other.data.forEach((key, value) -> data.put(key, data.getOrDefault(key, 0) + value ));
    }

    public int size() {
        return data.size();
    }

    public void save(String filename) throws IOException {
        // Sort by count DESC
        Map<Object, Integer> sortedMap = data.entrySet().stream()
                .sorted(Map.Entry.<Object, Integer>comparingByValue().reversed())
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);

        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

            out.println("<statistics>");
            for(Map.Entry<Object, Integer> entry : sortedMap.entrySet()) {
                out.println("\t<item>");
                out.println("\t\t<value>" + entry.getKey() + "</value>");
                out.println("\t\t<count>" + entry.getValue() + "</count>");
                out.println("\t</item>");
            }
            out.println("</statistics>");

            System.out.println("Statistic data saved to: " + filename);
        }
    }

    @Override
    public String toString() {
        return data.entrySet().stream()
                .sorted(Map.Entry.<Object, Integer>comparingByValue().reversed())
                .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll)
                .entrySet()
                // now sorted entry set to string
                .stream()
                .map(e -> e.getKey() + ": " + e.getValue() + "\n")
                .collect(Collectors.joining());
    }
}

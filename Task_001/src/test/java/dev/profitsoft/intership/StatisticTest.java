package dev.profitsoft.intership;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StatisticTest {
    private Statistic globalStatisticObject;
    // Reference to the map object inside Statistic class
    private Map<Object, Integer> globalStatisticRawData;
    @BeforeEach
    public void setUp() throws Exception {
        globalStatisticObject = new Statistic();

        Class<?> statisticClass = Statistic.class;
        Field dataField = statisticClass.getDeclaredField("data");
        dataField.setAccessible(true);

        globalStatisticRawData = (Map<Object, Integer>) dataField.get(globalStatisticObject);
    }

    @Test
    public void updateFrequency() {
        globalStatisticObject.updateFrequency("test");
        globalStatisticObject.updateFrequency("test");
        globalStatisticObject.updateFrequency("test");

        assertEquals(3, globalStatisticRawData.get("test"));
        assertNull(globalStatisticRawData.get("not_in_set"));

        globalStatisticObject.updateFrequency("test");

        assertEquals(4, globalStatisticRawData.get("test"));
        assertNull(globalStatisticRawData.get("not_in_set"));
    }

    @Test
    public void merge() {
        Statistic other = new Statistic();
        other.updateFrequency("A");
        other.updateFrequency("B");
        other.updateFrequency("B");
        other.updateFrequency("C");
        other.updateFrequency("C");
        other.updateFrequency("C");

        assertEquals(0, globalStatisticRawData.size());
        assertEquals(3, other.size());

        globalStatisticObject.merge(other);

        // A=1, B=2, C=3
        assertEquals(3, globalStatisticRawData.size());

        other.updateFrequency("D");
        other.updateFrequency("D");
        // other: A=1, B=2, C=3, D=2
        // global: A=1, B=2, C=3

        assertEquals(3, globalStatisticRawData.size());
        assertEquals(4, other.size());
    }

    @Test
    void size() {
        assertEquals(0, globalStatisticObject.size());

        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("A");
        assertEquals(1, globalStatisticObject.size()); // A=3

        globalStatisticObject.updateFrequency("B");
        globalStatisticObject.updateFrequency("B");
        assertEquals(2, globalStatisticObject.size()); // A=3, B=2

        globalStatisticObject.updateFrequency("C");
        assertEquals(3, globalStatisticObject.size()); // A=3, B=2, C=1
    }

    @Test
    void save() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + System.lineSeparator());
        sb.append("<statistics>" + System.lineSeparator());
        sb.append("\t<item>" + System.lineSeparator());
        sb.append("\t\t<value>A</value>" + System.lineSeparator());
        sb.append("\t\t<count>3</count>" + System.lineSeparator());
        sb.append("\t</item>" + System.lineSeparator());
        sb.append("\t<item>" + System.lineSeparator());
        sb.append("\t\t<value>B</value>" + System.lineSeparator());
        sb.append("\t\t<count>2</count>" + System.lineSeparator());
        sb.append("\t</item>" + System.lineSeparator());
        sb.append("\t<item>" + System.lineSeparator());
        sb.append("\t\t<value>C</value>" + System.lineSeparator());
        sb.append("\t\t<count>1</count>" + System.lineSeparator());
        sb.append("\t</item>" + System.lineSeparator());
        sb.append("</statistics>" + System.lineSeparator());

        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("B");
        globalStatisticObject.updateFrequency("B");
        globalStatisticObject.updateFrequency("C");

        globalStatisticObject.save("ABC.xml");
        assertEquals(sb.toString(), Files.readString(Paths.get("ABC.xml")));
    }

    @Test
    void toStr() {
        globalStatisticObject.updateFrequency("A");
        assertEquals("A: 1\n", globalStatisticObject.toString());

        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("B");
        assertEquals("A: 2\nB: 1\n", globalStatisticObject.toString());

        globalStatisticObject.updateFrequency("A");
        globalStatisticObject.updateFrequency("B");
        globalStatisticObject.updateFrequency("C");
        assertEquals("A: 3\nB: 2\nC: 1\n", globalStatisticObject.toString());
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        Files.delete(Paths.get("ABC.xml"));
    }
}
package io.choerodon.asgard;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class SetTest {

    private Set<Integer> records = Collections.synchronizedSet(new LinkedHashSet<>(10));

    private CopyOnWriteArraySet<Integer> records3 = new CopyOnWriteArraySet<>();


    @Test
    public void name() {
        List<Integer> put = Arrays.asList(1, 2, 3, 8, 10, 20);
        records.addAll(put);
        records.add(200);
        records.add(500);
        records.add(300);
        records.add(2);
        records.add(4);
        records.add(6);


        records3.addAll(put);
        records3.add(200);
        records3.add(500);
        records3.add(300);
        records3.add(2);
        records3.add(4);
        records3.add(6);
        log.info("put {} records{} records3 {}", put, records, records3);
    }

    @Test
    public void testFile() {
        String path = "classpath://forgetPasswordPreset.html";
        String trimContentPath = path.trim();
        if (trimContentPath.startsWith("classpath://")) {
            trimContentPath = trimContentPath.substring(12, trimContentPath.length());
            System.out.println(trimContentPath);
        }
    }
}

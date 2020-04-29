package io.choerodon.asgard.preoperty.saga;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.choerodon.asgard.saga.consumer.GenerateJsonSchemaUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GenerateJsonSchemaUtilTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void generate() {
        Map<String, String> expectedResult = new HashMap<>();
        expectedResult.put("aInteger", "\"Integer\"");
        expectedResult.put("anInt", "\"int\"");
        expectedResult.put("aBoolean", "\"boolean\"");
        expectedResult.put("aString", "\"String\"");
        expectedResult.put("aVoid", "\"void\"");
        expectedResult.put("aMap", "\"Map\"");
        expectedResult.put("anEnum", "\"Enum\"");

        Arrays.asList(Demo.class.getDeclaredMethods()).forEach(method -> {
            if (expectedResult.containsKey(method.getName())) { //使用JCoco测试覆盖率的时候会添加不存在的方法
                String outputSchemaDemo = GenerateJsonSchemaUtil.generate(method.getReturnType(), OBJECT_MAPPER, false);
                Assert.assertEquals(outputSchemaDemo, expectedResult.get(method.getName()));
            }
        });
    }

    private class Demo {
        public int anInt() {
            return 0;
        }

        public Integer aInteger() {
            return 0;
        }

        public boolean aBoolean() {
            return false;
        }

        public String aString() {
            return "";
        }

        public void aVoid() {
        }

        public Map aMap() {
            return null;
        }

        public Enum anEnum() {
            return null;
        }
    }

}

package io.choerodon.asgard.preoperty.saga;

import lombok.Data;

@Data
public class Test {
    private boolean isTest;
    private String username;
    private int age;
    private double money;
    private Inner inner;

    @Data
    public static class Inner {
        private long id;
        private String name;
    }


}

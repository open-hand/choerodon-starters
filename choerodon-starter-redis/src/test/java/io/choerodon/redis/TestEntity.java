package io.choerodon.redis;

import io.choerodon.mybatis.common.query.JoinCache;
import io.choerodon.mybatis.common.query.JoinCode;
import io.choerodon.mybatis.common.query.JoinLov;

public class TestEntity {
    @JoinCache(joinKey = "test", joinColumn = "test", cacheName = "test")
    public String test1;
    @JoinLov(joinKey = "test")
    public String test2;
    @JoinCode(joinKey = "test", code = "test")
    public String test3;

    public String getTest1() {
        return test1;
    }

    public void setTest1(String test1) {
        this.test1 = test1;
    }

    public String getTest2() {
        return test2;
    }

    public void setTest2(String test2) {
        this.test2 = test2;
    }

    public String getTest3() {
        return test3;
    }

    public void setTest3(String test3) {
        this.test3 = test3;
    }
}

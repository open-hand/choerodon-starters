package io.choerodon.mybatis.code;

import io.choerodon.mybatis.constant.CommonMapperConfigConstant;

/**
 * @author qixiangyu18@163.com on 2018/8/7.
 */
public enum DbType {

    MYSQL("mysql",true,false, CommonMapperConfigConstant.IDENTITY_JDBC),
    ORACLE("oracle",false,true,CommonMapperConfigConstant.IDENTITY_SEQUENCE),
    HANA("hana",false,true,CommonMapperConfigConstant.IDENTITY_SEQUENCE),
    H2("h2",true,false,CommonMapperConfigConstant.IDENTITY_JDBC),
    SQLSERVER("sqlserver",true,false,CommonMapperConfigConstant.IDENTITY_JDBC),
    POSTGRES("postgres",false,true,CommonMapperConfigConstant.IDENTITY_SEQUENCE);

    private boolean supportAutoIncrement;

    private boolean supportSequence;

    private String identity;

    private String value;

    DbType(String value ,boolean supportAutoIncrement, boolean supportSequence,String identity){
        this.value = value;
        this.supportAutoIncrement = supportAutoIncrement;
        this.supportSequence = supportSequence;
        this.identity = identity;
    }

    public String getValue(){
        return value;
    }

    public boolean isSupportAutoIncrement() {
        return supportAutoIncrement;
    }

    public boolean isSupportSequence() {
        return supportSequence;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public static DbType getByValue(String value){
        for(DbType dbType : values()){
            if(dbType.getValue().equalsIgnoreCase(value)){
                return dbType;
            }
        }
        return null;
    }
}

package io.choerodon.mybatis.autoconfigure;

import org.springframework.core.io.Resource;
import tk.mybatis.mapper.autoconfigure.MybatisProperties;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * 用于过滤重复包名和文件名的Mapper.xml，保留第一个，实现Mapper.xml文件的覆盖
 */
public class MapperOverrideProperties extends MybatisProperties {
    @Override
    public Resource[] resolveMapperLocations() {
        Set<String> mapperFilePath = new TreeSet<>();
        //获取原来的Resources
        Resource[] result = super.resolveMapperLocations();
        for (int i = 0; i < result.length; i++){
            try {
                String[] split = result[i].getURI().toString().split("classes/");
                if (split.length != 2){
                    split = result[i].getURI().toString().split(".jar!/");
                }
                //将出现过的Resource置为null，mybatis会忽略这些null
                if (split.length == 2 && !mapperFilePath.add(split[1])){
                    result[i] = null;
                }
            } catch (IOException e) {
                //忽略无法处理的Resource
            }
        }
        return result;
    }
}

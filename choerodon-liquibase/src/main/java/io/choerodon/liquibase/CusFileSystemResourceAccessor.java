package io.choerodon.liquibase;

import liquibase.exception.UnexpectedLiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

import java.io.File;
import java.io.IOException;

/**
 * 加载系统文件资源
 *
 * @author dongfan117@gmail.com
 * @see liquibase.resource.ResourceAccessor
 */
public class CusFileSystemResourceAccessor extends FileSystemResourceAccessor {

    private File baseDirectory;

    /**
     * 没有创建根文件夹
     */
    public CusFileSystemResourceAccessor() {
        super();
    }

    /**
     * 根据相对路径创建根文件夹
     */
    public CusFileSystemResourceAccessor(String base) {
        super(base);
    }

    @Override
    protected String convertToPath(String string) {
        if (this.baseDirectory == null) {
            return string;
        } else {
            try {
                return new File(string).getCanonicalPath();
            } catch (IOException e) {
                throw new UnexpectedLiquibaseException(e);
            }
        }
    }
}

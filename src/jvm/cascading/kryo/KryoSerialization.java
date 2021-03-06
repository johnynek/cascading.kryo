package cascading.kryo;

import com.esotericsoftware.kryo.Kryo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.serializer.Deserializer;
import org.apache.hadoop.io.serializer.Serialization;
import org.apache.hadoop.io.serializer.Serializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

/** User: sritchie Date: 12/1/11 Time: 11:43 AM */
public class KryoSerialization extends Configured implements Serialization<Object> {
    Kryo kryo;
    KryoFactory factory;

    public KryoSerialization() {
        this(new Configuration());
    }

    /**
     * Constructor KryoSerialization creates a new KryoSerialization instance.
     *
     * @param conf of type Configuration
     */
    public KryoSerialization( Configuration conf ) {
        super( conf );
    }

    /**
     *
     * @param k
     * @return
     */
    public Kryo decorateKryo(Kryo k) {
        return k;
    }

    public final Kryo populatedKryo() {
        if (factory == null)
            factory = new KryoFactory(getConf());

        Kryo k = new Kryo();
        k.setInstantiatorStrategy(new StdInstantiatorStrategy());

        decorateKryo(k);
        factory.populateKryo(k);
        return k;
    }

    /**
     * Initializes Kryo instance from the JobConf on the first run. If the ACCEPT_ALL key in
     * the JobConf has been set to true, Kryo will return yes for everything; else, Kryo will only
     * return true for classes with explicitly registered serializations.
     * @param aClass
     * @return
     */
    public boolean accept(Class<?> aClass) {
        if (kryo == null)
            kryo = populatedKryo();
        try {
            return (kryo.getRegistration(aClass) != null);
        } catch (IllegalArgumentException e) {
            return factory.getAcceptAll();
        }
    }

    public Serializer<Object> getSerializer(Class<Object> aClass) {
        return new KryoSerializer(populatedKryo());
    }

    public Deserializer<Object> getDeserializer(Class<Object> aClass) {
        return new KryoDeserializer(populatedKryo(), aClass);
    }
}

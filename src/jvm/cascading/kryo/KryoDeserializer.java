package cascading.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.apache.hadoop.io.serializer.Deserializer;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/** User: sritchie Date: 12/1/11 Time: 3:15 PM */
public class KryoDeserializer implements Deserializer<Object> {

    private final Kryo kryo;
    private final Class<Object> klass;

    private DataInputStream inputStream;
    private Input input = null;

    public KryoDeserializer(Kryo kryo, Class<Object> klass) {
        this.kryo =  kryo;
        this.klass = klass;
    }

    public void open(InputStream in) throws IOException {
        if( in instanceof DataInputStream)
            this.inputStream = (DataInputStream) in;
        else
            this.inputStream = new DataInputStream( in );
    }

    public Object deserialize(Object o) throws IOException {
        int len = inputStream.readInt();
        byte[] bytes = new byte[len];
        inputStream.readFully( bytes );

        if (input == null)
            input = new Input(bytes);
        else
            input.setBuffer(bytes);

        return kryo.readObject(input, klass);
    }

    public void close() throws IOException {
        if( input != null )
            input.close();

        try {
            if( inputStream != null )
                inputStream.close();
        } finally {
            inputStream = null;
        }
    }
}

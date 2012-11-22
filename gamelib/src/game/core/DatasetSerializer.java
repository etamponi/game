package game.core;

import game.core.DataTemplate.Data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ios.IOSSerializer;

public class DatasetSerializer extends IOSSerializer<Dataset> {
	
	private static final Kryo kryo = new Kryo();
	static {
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
	}
	
	private byte[] serialize(Kryo kryo, Object object, Class<?> type) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Output output = new Output(stream);
		kryo.writeObjectOrNull(output, object, type);
		output.close();
		return stream.toByteArray();
	}
	
	private <T> T deserialize(Kryo kryo, byte[] bytes, Class<T> type) {
		Input input = new Input(bytes);
		T ret = kryo.readObjectOrNull(input, type);
		input.close();
		return ret;
	}
	
	private void writeBytes(Kryo kryo, Output output, Object object, Class<?> type) {
		byte[] bytes = serialize(kryo, object, type);
		output.writeInt(bytes.length);
		output.write(bytes);
	}
	
	private <T> T readBytes(Kryo kryo, Input input, Class<T> type) {
		int size = input.readInt();
		return deserialize(kryo, input.readBytes(size), type);
	}

	@Override
	public void write(Kryo k, Output out, Dataset object) {
		kryo.setClassLoader(k.getClassLoader());
		
		writeBytes(kryo, out, object.getTemplate(), InstanceTemplate.class);
		out.writeInt(object.size());
		for(Instance i: object) {
			writeBytes(kryo, out, new ArrayList(i.getInput()), ArrayList.class);
			writeBytes(kryo, out, new ArrayList(i.getOutput()), ArrayList.class);
			writeBytes(kryo, out, i.getPrediction() == null ? null : new ArrayList(i.getPrediction()), ArrayList.class);
			writeBytes(kryo, out, i.getPredictionEncoding(), Encoding.class);
		}
	}

	@Override
	public Dataset read(Kryo k, Input in, Class<Dataset> type) {
		kryo.setClassLoader(k.getClassLoader());
		
		InstanceTemplate template = readBytes(kryo, in, InstanceTemplate.class);
		int size = in.readInt();

		Dataset ret = new Dataset(template);
		while(size-- > 0) {
			Instance i = template.newInstance();
			
			Data input = template.inputTemplate.newData();
			input.addAll(readBytes(kryo, in, ArrayList.class));
			
			Data output = template.outputTemplate.newData();
			output.addAll(readBytes(kryo, in, ArrayList.class));
			
			Data prediction = null;
			Collection temp = readBytes(kryo, in, ArrayList.class);
			if (temp != null) {
				prediction = template.outputTemplate.newData();
				prediction.addAll(temp);
			}
			
			Encoding encoding = readBytes(kryo, in, Encoding.class);
			
			i.setInput(input);
			i.setOutput(output);
			i.setPrediction(prediction);
			i.setPredictionEncoding(encoding);
			
			ret.add(i);
		}
		
		return ret;
	}
	
}

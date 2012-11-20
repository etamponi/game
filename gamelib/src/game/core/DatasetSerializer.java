package game.core;

import game.core.DataTemplate.Data;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.objenesis.strategy.StdInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class DatasetSerializer extends Serializer<Dataset> {
	
	private static final Kryo internal = new Kryo();
	static {
		internal.setInstantiatorStrategy(new StdInstantiatorStrategy());
	}
	
	private byte[] serialize(Object object, Class<?> type) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Output output = new Output(stream);
		internal.writeObjectOrNull(output, object, type);
		output.close();
		return stream.toByteArray();
	}
	
	private <T> T deserialize(byte[] bytes, Class<T> type) {
		Input input = new Input(bytes);
		T ret = internal.readObjectOrNull(input, type);
		input.close();
		return ret;
	}
	
	private void writeBytes(Output output, Object object, Class<?> type) {
		byte[] bytes = serialize(object, type);
		output.writeInt(bytes.length);
		output.write(bytes);
	}
	
	private <T> T readBytes(Input input, Class<T> type) {
		int size = input.readInt();
		return deserialize(input.readBytes(size), type);
	}

	@Override
	public void write(Kryo kryo, Output out, Dataset object) {
		writeBytes(out, object.getTemplate(), InstanceTemplate.class);
		out.writeInt(object.size());
		for(Instance i: object) {
			writeBytes(out, new ArrayList(i.getInput()), ArrayList.class);
			writeBytes(out, new ArrayList(i.getOutput()), ArrayList.class);
			writeBytes(out, i.getPrediction() == null ? null : new ArrayList(i.getPrediction()), ArrayList.class);
			writeBytes(out, i.getPredictionEncoding(), Encoding.class);
		}
	}

	@Override
	public Dataset read(Kryo kryo, Input in, Class<Dataset> type) {
		internal.setClassLoader(kryo.getClassLoader());
		
		InstanceTemplate template = readBytes(in, InstanceTemplate.class);
		int size = in.readInt();

		Dataset ret = new Dataset(template);
		while(size-- > 0) {
			Instance i = template.newInstance();
			
			Data input = template.inputTemplate.newData();
			input.addAll(readBytes(in, ArrayList.class));
			
			Data output = template.outputTemplate.newData();
			output.addAll(readBytes(in, ArrayList.class));
			
			Data prediction = null;
			Collection temp = readBytes(in, ArrayList.class);
			if (temp != null) {
				prediction = template.outputTemplate.newData();
				prediction.addAll(temp);
			}
			
			Encoding encoding = readBytes(in, Encoding.class);
			
			i.setInput(input);
			i.setOutput(output);
			i.setPrediction(prediction);
			i.setPredictionEncoding(encoding);
			
			ret.add(i);
		}
		
		return ret;
	}
	
}

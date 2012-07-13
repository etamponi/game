package game.plugins.weka.classifiers;

import game.configuration.Configurable;
import game.core.blocks.Classifier;
import game.utils.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class WekaClassifier extends Classifier {
	
	public static class WekaClassifierConverter implements Converter {

		@Override
		public boolean canConvert(Class type) {
			try {
				Class.forName("weka.classifiers.Classifier");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
			return weka.classifiers.Classifier.class.isAssignableFrom(type);
		}

		@Override
		public void marshal(Object object, HierarchicalStreamWriter writer,
				MarshallingContext context) {
			try {
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(stream);
				oos.writeObject(object);
				oos.flush();
				oos.close();
				writer.setValue(new String(Base64Coder.encode(stream.toByteArray())));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader,
				UnmarshallingContext context) {
			Object object = null;
			
			try {
				object = new ObjectInputStream(new ByteArrayInputStream(Base64Coder.decode(reader.getValue()))).readObject();
			}  catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
			
			return object;
		}
		
	}

	static {
		Configurable.registerConverter(new WekaClassifierConverter());
	}
	
}
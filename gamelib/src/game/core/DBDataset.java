package game.core;

import game.configuration.Configurable;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DBDataset extends Configurable {
	
	private static final int COMMITCYCLE = 100;
	
	private static final XStream stream = new XStream(new DomDriver());
	
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private static final Map<String, Connection> connectionRegistry = new HashMap<>();
	
	private Connection connection = null;
	
	public String databaseName = null;
	
	public boolean readOnly = false;
	
	public List<Integer> indices = new ArrayList<>();
	
	public boolean shuffle = true;
	
	public DBDataset() {
		setPrivateOptions("databaseName", "indices", "readOnly", "shuffle");
	}
	
	public DBDataset(String datasetDirectory) {
		this(datasetDirectory, true);
	}
	
	public DBDataset(String datasetDirectory, boolean shuffle) {
		this();
		this.shuffle = shuffle;
		createDatabase(datasetDirectory);
	}
	
	private DBDataset(DBDataset base, List<Integer> indices) {
		this();
		this.indices = new ArrayList<>(indices);
		this.readOnly = true;
		this.connection = base.connection;
	}
	
	public int size() {
		return indices.size();
	}
	
	private void createDatabase(String datasetDirectory) {
		File dir = new File(datasetDirectory);
		if (!dir.exists())
			dir.mkdirs();
		try {
			String fileName = File.createTempFile("gdb", ".db", new File(datasetDirectory)).getPath();
			databaseName = fileName;
			connection = DriverManager.getConnection("jdbc:sqlite:"+fileName);
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			statement.executeUpdate("CREATE TABLE Data (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL);");
			statement.close();
			connectionRegistry.put(databaseName, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
		
		if (connection != null)
			return;
		
		if (connectionRegistry.containsKey(databaseName)) {
			connection = connectionRegistry.get(databaseName);
		} else {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:"+databaseName);
				connection.setAutoCommit(false);
				Statement statement = connection.createStatement();
				ResultSet res = statement.executeQuery("SELECT COUNT(*) AS count FROM Data;");
				res.next();
				int count = res.getInt("count");
				for(int i = 0; i < count; i++)
					indices.add(i);
				statement.close();
				connectionRegistry.put(databaseName, connection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void add(Instance instance) {
		if (readOnly)
			return; // Cannot add instances once a dataset is readOnly
		try {
			String content = stream.toXML(instance);
			PreparedStatement statement = connection.prepareStatement("INSERT INTO Data(id, content) VALUES (?, ?);");
			statement.setInt(1, indices.size());
			statement.setString(2, content);
			statement.executeUpdate();
			indices.add(indices.size());
			if (indices.size() % COMMITCYCLE == 0)
				connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setReadOnly() {
		try {
			connection.commit();
			readOnly = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public class InstanceIterator implements Iterator<Instance> {
		
		private Iterator<Integer> idIterator;
		private int currentId;

		public InstanceIterator() {
			ArrayList<Integer> temp = new ArrayList<>(indices);
			if (shuffle)
				Collections.shuffle(temp);
			idIterator = temp.iterator();
		}
		
		@Override
		public boolean hasNext() {
			return idIterator.hasNext();
		}

		@Override
		public Instance next() {
			Instance ret = null;
			try {
				Statement statement = connection.createStatement();
				currentId = idIterator.next();
				ResultSet result = statement.executeQuery("SELECT content FROM Data WHERE id = " + currentId + ";");
				while(result.next())
					ret = (Instance)stream.fromXML(result.getString("content"));
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove instances from dataset");
		}

	}
	
	public class SampleIterator implements Iterator<Sample> {

		public static final int EVERYTHING = 1;
		public static final int IN_OUT = 2;
		public static final int IN_OUT_PRED = 3;
		public static final int IN_OUT_ENC = 4;
		
		private int type;
		private Block inputEncoder;
		private Block outputEncoder;
		private InstanceIterator instanceIterator = instanceIterator();
		private List<Object> currentInputSequence;
		private List<Object> currentOutputSequence;
		private List<Object> currentPredictionSequence;
		private Encoding currentInputEncoding;
		private Encoding currentOutputEncoding;
		private Encoding currentPredictionEncoding;
		private int indexInInstance;
		
		public SampleIterator(int type) {
			if (type == IN_OUT_ENC || type == EVERYTHING)
				throw new UnsupportedOperationException("Cannot use a sample iterator with encoding if you don't specify the encoders");
			this.type = type;
			prepareForNextInstance();
		}
		
		public SampleIterator(int type, Block inputEncoder, Block outputEncoder) {
			this.type = type;
			this.inputEncoder = inputEncoder;
			this.outputEncoder = outputEncoder;
			prepareForNextInstance();
		}
		
		private void prepareForNextInstance() {
			Instance inst = instanceIterator.next();
			currentInputSequence = getSequence(inst.getInputData());
			currentOutputSequence = getSequence(inst.getOutputData());
			if (inst.getPredictionData() != null)
				currentPredictionSequence = getSequence(inst.getPredictionData());
			if (inputEncoder != null && outputEncoder != null) {
				currentInputEncoding = inputEncoder.transform(inst.getInputData());
				currentOutputEncoding = outputEncoder.transform(inst.getOutputData());
			}
			currentPredictionEncoding = inst.getPredictionEncoding();
			indexInInstance = 0;
		}
		
		private List<Object> getSequence(Object data) {
			if (data instanceof List) {
				return (List)data;
			} else {
				List<Object> ret = new ArrayList<>(1);
				ret.add(data);
				return ret;
			}
		}

		@Override
		public boolean hasNext() {
			return instanceIterator.hasNext() ||
					indexInInstance < currentInputSequence.size();
		}

		@Override
		public Sample next() {
			if (indexInInstance == currentInputSequence.size()) {
				prepareForNextInstance();
			}
			
			Sample ret = null;
			switch(type) {
			case EVERYTHING:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentInputEncoding.get(indexInInstance),
						currentOutputSequence.get(indexInInstance),
						currentOutputEncoding.get(indexInInstance),
						currentPredictionSequence.get(indexInInstance),
						currentPredictionEncoding.get(indexInInstance));
				break;
			case IN_OUT:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentOutputSequence.get(indexInInstance));
				break;
			case IN_OUT_PRED:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentOutputSequence.get(indexInInstance),
						currentPredictionSequence.get(indexInInstance));
				break;
			case IN_OUT_ENC:
				ret = new Sample(
						currentInputSequence.get(indexInInstance),
						currentInputEncoding.get(indexInInstance),
						currentOutputSequence.get(indexInInstance),
						currentOutputEncoding.get(indexInInstance));
				break;
			}
			
			indexInInstance++;
			return ret;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Cannot remove samples from dataset");
		}
		
	}

	public InstanceIterator instanceIterator() {
		return new InstanceIterator();
	}
	
	public SampleIterator sampleIterator(boolean includePrediction) {
		if (includePrediction)
			return new SampleIterator(SampleIterator.IN_OUT_PRED);
		else
			return new SampleIterator(SampleIterator.IN_OUT);
	}
	
	public SampleIterator encodedSampleIterator(Block inputEncoder, Block outputEncoder, boolean includePrediction) {
		if (includePrediction)
			return new SampleIterator(SampleIterator.EVERYTHING, inputEncoder, outputEncoder);
		else
			return new SampleIterator(SampleIterator.IN_OUT_ENC, inputEncoder, outputEncoder);
	}
	
	public static class EncodedSamples extends ArrayList<Sample> {
		private static final long serialVersionUID = 2130556043598496819L;

		public EncodedSamples() {
			
		}
		
		public EncodedSamples(int initialCapacity) {
			super(initialCapacity);
		}
		
		public EncodedSamples(Collection<Sample> other) {
			super(other);
		}
		
	}
	
	public EncodedSamples encode(Block inputEncoder, Block outputEncoder) {
		EncodedSamples ret = new EncodedSamples(size());
		
		SampleIterator it = encodedSampleIterator(inputEncoder, outputEncoder, false);
		while(it.hasNext())
			ret.add(it.next());
		
		return ret;
	}
	
	public List<DBDataset> getFolds(int folds) {
		if (!readOnly)
			return null;
		
		List<DBDataset> ret = new ArrayList<>(folds);
		List<Integer> temp = new ArrayList<>(indices);
		if (shuffle)
			Collections.shuffle(temp);
		int foldSize = indices.size() / folds;
		for(int fold = 0; fold < folds; fold++) {
			ret.add(new DBDataset(this, temp.subList(fold*foldSize, (fold+1)*foldSize)));
		}
		return ret;
	}
	
	public List<DBDataset> getComplementaryFolds(List<DBDataset> folds) {
		if (!readOnly)
			return null;
		
		List<DBDataset> ret = new ArrayList<>(folds.size());
		List<Integer> temp = new ArrayList<>(indices);
		for(DBDataset fold: folds) {
			List<Integer> complement = new ArrayList<>(temp);
			complement.removeAll(fold.indices);
			ret.add(new DBDataset(this, complement));
		}
		return ret;
	}
	
	public DBDataset getRandomSubset(double percent) {
		if (!readOnly)
			return null;
		
		assert(percent > 0 && percent <= 1);
		List<Integer> temp = new ArrayList<>(indices);
		Collections.shuffle(temp);
		return new DBDataset(this, temp.subList(0, (int)(percent*temp.size())));
	}
	
}

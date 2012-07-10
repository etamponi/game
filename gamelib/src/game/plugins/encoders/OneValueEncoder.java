package game.plugins.encoders;

public class OneValueEncoder extends LabelEncoder {
	
	public OneValueEncoder() {
		setPrivateOptions("labelMapping");
	}

	@Override
	protected void updateSingleMapping() {
		for(int i = 0; i < template.labels.size(); i++)
			labelMapping.put((String)template.labels.get(i), new double[]{i+1});
	}

}

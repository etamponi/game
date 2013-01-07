package game.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class LabeledStatisticsMatrix {

	private List<String> rowLabels = new ArrayList<>();
	private List<String> columnLabels = new ArrayList<>();
	
	private SummaryStatistics[][] matrix;
	
	public LabeledStatisticsMatrix(List<LabeledMatrix> data) {
		LabeledMatrix temp = data.get(0);
		rowLabels = new ArrayList<>(temp.getRowLabels());
		columnLabels = new ArrayList<>(temp.getColumnLabels());
		matrix = new SummaryStatistics[temp.getRowDimension()][temp.getColumnDimension()];
		
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < matrix.length; j++) {
				matrix[i][j] = new SummaryStatistics();
				for(int k = 0; k < data.size(); k++)
					matrix[i][j].addValue(data.get(k).getEntry(i, j));
			}
		}
	}
	
	public List<String> getRowLabels() {
		return rowLabels;
	}
	
	public List<String> getColumnLabels() {
		return columnLabels;
	}
	
	public SummaryStatistics[][] getMatrix() {
		return matrix;
	}

}

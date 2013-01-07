package game.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class LabeledMatrix extends Array2DRowRealMatrix {
	
	private List<String> rowLabels = new ArrayList<>();
	private List<String> columnLabels = new ArrayList<>();
	
	public LabeledMatrix(int rows, int columns) {
		super(rows, columns);
	}
	
	public LabeledMatrix(RealMatrix matrix) {
		super(matrix.getData());
	}
	
	public void setRowLabels(String... labels) {
		rowLabels.clear();
		for(String label: labels)
			rowLabels.add(label);
	}
	
	public void setColumnLabels(String... labels) {
		columnLabels.clear();
		for(String label: labels)
			columnLabels.add(label);
	}
	
	public List<String> getRowLabels() {
		return rowLabels;
	}
	
	public List<String> getColumnLabels() {
		return columnLabels;
	}

}

package game.plugins.viewers;

import game.core.LabeledStatisticsMatrix;
import game.editorsystem.PropertyEditor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class LabeledStatisticsMatrixViewer extends PropertyEditor {
	
	private static class StatisticsRow {
		private final String name;
		private final SummaryStatistics[] stats;
		
		public StatisticsRow(LabeledStatisticsMatrix matrix, int row) {
			name = matrix.getRowLabels().get(row);
			stats = new SummaryStatistics[matrix.getColumnLabels().size()];
			for(int j = 0; j < stats.length; j++)
				stats[j] = matrix.getMatrix()[row][j];
		}
	}
	
	private TableView<StatisticsRow> table = new TableView<>();
	
	public LabeledStatisticsMatrixViewer() {
		table.setEditable(false);
	}

	@Override
	public Node getView() {
		return table;
	}

	@Override
	public void updateView() {
		table.getColumns().clear();
		table.getItems().clear();
		
		LabeledStatisticsMatrix matrix = getModel().getContent();
		for(int i = 0; i < matrix.getRowLabels().size(); i++) {
			table.getItems().add(new StatisticsRow(matrix, i));
		}
		
		TableColumn<StatisticsRow, String> firstCol = new TableColumn<>();
		firstCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StatisticsRow,String>, ObservableValue<String>>() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<StatisticsRow, String> param) {
				return new SimpleStringProperty(param.getValue().name);
			}
		});
		table.getColumns().add(firstCol);
		for(int j = 0; j < matrix.getColumnLabels().size(); j++) {
			final int col_number = j;
			String title = matrix.getColumnLabels().get(j);
			TableColumn<StatisticsRow, String> column = new TableColumn<>(title);
			column.setPrefWidth(100);
			column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<StatisticsRow,String>, ObservableValue<String>>() {
				@Override
				public ObservableValue<String> call(CellDataFeatures<StatisticsRow, String> param) {
					return new SimpleStringProperty(String.format("%.3f", param.getValue().stats[col_number].getMean()));
				}
			});
			table.getColumns().add(column);
		}
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public Class getBaseEditableClass() {
		return LabeledStatisticsMatrix.class;
	}

}

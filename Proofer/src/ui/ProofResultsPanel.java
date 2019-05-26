package ui;

import geometry.proofs.FigureRelation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class ProofResultsPanel extends TableView<FormattedFigureRelation> {
	
	private ObservableList<FormattedFigureRelation> formattedData;
	private TableColumn<FormattedFigureRelation, String> stepNumCol;
	private TableColumn<FormattedFigureRelation, String> statementsCol, reasonsCol;
	
	public ProofResultsPanel(FigureRelation[] data) {
		this.formattedData = formatData(data);
		
		/*
		 * Setup columns
		 */
		
		stepNumCol = new TableColumn<FormattedFigureRelation, String>("Step");
		stepNumCol.setSortable(false);
		stepNumCol.setCellValueFactory(
				new PropertyValueFactory<FormattedFigureRelation, String>("index"));

		statementsCol = new TableColumn<FormattedFigureRelation, String>("Statements");
		statementsCol.setMinWidth(300);
		statementsCol.setCellValueFactory(
				new PropertyValueFactory<FormattedFigureRelation, String>("statement"));
		
		reasonsCol = new TableColumn<FormattedFigureRelation, String>("Reasons");
		reasonsCol.setMinWidth(300);
		reasonsCol.setCellValueFactory(new PropertyValueFactory<FormattedFigureRelation, String>("reason"));
		
		// Fill up columns
		System.out.println(getColumns().size());
		getColumns().addAll(stepNumCol, statementsCol, reasonsCol);
		setItems(formattedData);
	}
	
	private ObservableList<FormattedFigureRelation> formatData(FigureRelation[] data) {
		ObservableList<FormattedFigureRelation> formatted = FXCollections.observableArrayList();
		
		int index = 1;
		for (FigureRelation rel : data) {
			FormattedFigureRelation formattedRel = new FormattedFigureRelation(rel, index);
			if (!formatted.contains(formattedRel)) {
				formatted.add(formattedRel);
				++index;
			}
		}
		return formatted;
	}
	
}




























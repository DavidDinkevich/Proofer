package ui;

import geometry.proofs.FigureRelation;
import geometry.proofs.ProofSolver;

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
	
	/**
	 * Converts the raw traceback from a {@link ProofSolver} into a presentable user-friendly
	 * format.
	 * @param data raw taceback from a {@link ProofSolver}
	 * @return an {@link ObservableList} of the formatted traceback
	 */
	private ObservableList<FormattedFigureRelation> formatData(FigureRelation[] data) {
		ObservableList<FormattedFigureRelation> formatted = FXCollections.observableArrayList();
		
		int index = 1; // Index of statement/reason in proof
		for (FigureRelation rel : data) {
			FormattedFigureRelation formattedRel = new FormattedFigureRelation(rel, index);
			// No duplicates
			if (!formatted.contains(formattedRel)) {
				formatted.add(formattedRel);
				++index;
			}
		}
		return formatted;
	}
	
}

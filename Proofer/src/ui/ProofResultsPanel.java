package ui;

import java.util.ArrayList;
import java.util.List;

import geometry.proofs.FigureRelation;
import geometry.proofs.ProofSolver;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;


public class ProofResultsPanel extends TableView<FormattedFigureRelation> {
	
	private static final String[] BRANCH_COLORS = {
		// Pink
		"#ffe6f0", "rgb(255, 204, 224)", "#ffb3d1",
		// Purple
		"rgb(229, 204, 255)", "rgb(215, 179, 255)", "rgb(201, 153, 255)",
		// Green
		"#e6ffee", "#ccffdd", "#99ffbb",
		// Yello/orange
		"#ffe6b3", "#ffdd99", "rgb(255, 204, 102)"
	};
	
	private ObservableList<FormattedFigureRelation> formattedData;
	private List<Integer> branches;
	private boolean colorBranches;
	private TableColumn<FormattedFigureRelation, String> stepNumCol;
	private TableColumn<FormattedFigureRelation, String> statementsCol, reasonsCol;
	
	@SuppressWarnings("unchecked")
	public ProofResultsPanel(FigureRelation[] data) {
		branches = new ArrayList<>();
		colorBranches = true; // Color branches by default
		// FORMAT DATA
		this.formattedData = formatData(data);
		
		/*
		 * Setup columns
		 */
		
		stepNumCol = new TableColumn<FormattedFigureRelation, String>("Step");
		stepNumCol.setMinWidth(70);
		stepNumCol.setCellValueFactory(
				new PropertyValueFactory<FormattedFigureRelation, String>("index"));

		statementsCol = new TableColumn<FormattedFigureRelation, String>("Statements");
		statementsCol.setMinWidth(300);
		statementsCol.setCellValueFactory(
				new PropertyValueFactory<FormattedFigureRelation, String>("statement"));
		
		reasonsCol = new TableColumn<FormattedFigureRelation, String>("Reasons");
		reasonsCol.setMinWidth(300);
		reasonsCol.setCellValueFactory(new PropertyValueFactory<FormattedFigureRelation, 
				String>("reason"));
		
		// Fill up columns
		getColumns().addAll(stepNumCol, statementsCol, reasonsCol);
		setItems(formattedData);
		setColorBranches(true);
	}
	
	/**
	 * Converts the raw traceback from a {@link ProofSolver} into a presentable user-friendly
	 * format.
	 * @param data raw taceback from a {@link ProofSolver}
	 * @return an {@link ObservableList} of the formatted traceback
	 */
	private ObservableList<FormattedFigureRelation> formatData(FigureRelation[] data) {
		ObservableList<FormattedFigureRelation> formatted = FXCollections.observableArrayList();
		
		int stepNum = 1; // Step number of statement/reason in proof
		outer:
		for (FigureRelation rel : data) {
			// No duplicates
			for (FormattedFigureRelation other : formatted) {
				if (other.getFigureRelation().equals(rel)) {
					continue outer;
				}
			}
			
			// Creating a new branch (new branch if not first step and no parents)
			if (rel.getParents().size() == 0 && stepNum > 1)
				branches.add(stepNum-1);

			// Add
			FormattedFigureRelation formattedRel = new FormattedFigureRelation(rel, stepNum);
			formatted.add(formattedRel);
			++stepNum;
		}

		return formatted;
	}
	
	public void setColorBranches(boolean val) {
		colorBranches = val;
		stepNumCol.setCellFactory(column -> getCellFactory());
	}
	
	/**
	 * Custom table cell factory
	 */
	private TableCell<FormattedFigureRelation, String> getCellFactory() {
		return new TableCell<FormattedFigureRelation, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				
				if (colorBranches) {
					// COLOR BRANCHES
					// Get the current row
					TableRow<?> row = getTableRow();
					
					if (row != null && item != null) {
						// Step of this row in the proof (item is the step number)
						final int index = Integer.parseInt(item) - 1;
						// Make sure we're not operating on the last step in the proof, the
						// proof objective
						if (index == formattedData.size() - 1)
							return;
						// Find the branch that this step is in
						int branch = 0;
						for (; branch < branches.size() && index >= branches.get(branch); branch++);
						// Set the color of this row according to its branch
						if (branch < BRANCH_COLORS.length) { // Prevent IOB exception
							row.setStyle("-fx-background-color: " + BRANCH_COLORS[branch]);
						}
					}
				}
			}
		};
	}
	
}

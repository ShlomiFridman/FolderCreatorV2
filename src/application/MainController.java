package application;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController {

	@FXML
	private TextField rootField;

	@FXML
	private Button prevDayBtn;

	@FXML
	private TextField dayField;

	@FXML
	private Button nextDayBtn;

	@FXML
	private VBox foldersVBox;

	@FXML
	private VBox mainVBox;
	
	@FXML
	private Button createBtn;

	@FXML
	public Button createBtnWithEx;

	HashMap<CheckBox, File> checkBoxMap;

	File root;
	File[] subjects;
	Calendar cal;
	DateFormat format;

	public void initialize() throws IOException {
		// init root
		root = new File(".");
		rootField.setText(root.getCanonicalPath());
		// init subjects
		checkBoxMap = new HashMap<>();
		for (String subDir : Objects.requireNonNull(root.list((current, name) -> {
            return new File(current, name).isDirectory();
        }))) {
			File sub = new File(root, subDir);
			CheckBox cb = new CheckBox(sub.getName());
			checkBoxMap.put(cb, sub);
			foldersVBox.getChildren().add(cb);
		}
		// init subjects files, adjust minHeight
		updateHeight();
		// init date
		cal = Calendar.getInstance();
		format = new SimpleDateFormat("yyyy-MM-dd");
		dayField.setText(getDate());
		// set the transparent fields
		dayField.setMouseTransparent(true);
		// set the events
		prevDayBtn.setOnMouseClicked(ev -> updateDate(-1));
		nextDayBtn.setOnMouseClicked(ev -> updateDate(1));

	}

	@FXML
	void createFolders(ActionEvent event) {
		createAction(createBtn, false);
	}
	@FXML
	void createFoldersWithEx(ActionEvent event) {
		createAction(createBtnWithEx, true);
	}

	private void createAction(Button btn, boolean withEx){
		// cooldown in case of double click
		if (btn.getStyleClass().contains("clicked"))
			return;
		// iterate through subjects
		for (Entry<CheckBox, File> entry : checkBoxMap.entrySet()) {
			// if the subject's check-box is selected, call createFolder function on it
			if (entry.getKey().isSelected()) {
				createFolder(entry.getValue(), withEx);
			}
		}
		// change the btn color for 1.3sec to show the creation was successful
		btn.getStyleClass().add("clicked");
		PauseTransition pause = new PauseTransition(Duration.millis(1300));
		pause.setOnFinished(ev -> {
			btn.getStyleClass().remove("clicked");
		});
		pause.play();
	}

	private void createFolder(File subject, boolean withEx) {
		try {
			File folder = new File(subject, getDate());
			// if there isn't a folder for the date, create a new folder
			if (!folder.exists())
				folder.mkdir();
			else
				return;
			// create the notes.docx file
			new File(folder,"Notes_lec.docx").createNewFile();
			if (withEx)
				new File(folder,"Notes_ex.docx").createNewFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// update the date field after the day offset
	private void updateDate(int dayOffset) {
		cal.add(Calendar.DATE, dayOffset);
		dayField.setText(getDate());
	}

	private void updateHeight() {
		double neededHeight = 130 + checkBoxMap.size()*20;
		System.out.println(neededHeight);
		System.out.println(mainVBox.getHeight());
		if (mainVBox.getHeight() < neededHeight)
			mainVBox.setPrefHeight(neededHeight);
	}
	
	// return formated date string yyyy-MM-dd
	private String getDate() {
		return format.format(cal.getTime());
	}

}

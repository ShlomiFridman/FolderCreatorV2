package application;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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

	HashMap<CheckBox, File> checkBoxMap;

	File root;
	File[] subjects;
	Calendar cal;
	DateFormat format;

	public void initialize() throws IOException {
		// init root
		root = new File(".");
		rootField.setText(root.getCanonicalPath().toString());
		// init subjects
		checkBoxMap = new HashMap<>();
		for (String subDir : root.list((current, name) -> {
			return new File(current, name).isDirectory();
		})) {
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
		// cooldown in case of double click
		if (this.createBtn.getStyleClass().contains("clicked"))
			return;
		// iterate through subjects
		Iterator<Entry<CheckBox, File>> it = checkBoxMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<CheckBox, File> entry = it.next();
			// if the subject's check-box is selected, call createFolder function on it 
			if (entry.getKey().isSelected()) {
				createFolder(entry.getValue());
			}
		}
		// change the btn color for 1.3sec to show the creation was successful
		this.createBtn.getStyleClass().add("clicked");
		PauseTransition pause = new PauseTransition(Duration.millis(1300));
		pause.setOnFinished(ev -> {
			createBtn.getStyleClass().remove("clicked");
		});
		pause.play();
	}

	private void createFolder(File subject) {
		try {
			File folder = new File(subject, getDate());
			// if there isn't a folder for the date, create a new folder
			if (!folder.exists())
				folder.mkdir();
			else
				return;
			// create the notes.docx file
			new File(folder,"Notes.docx").createNewFile();
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
		double neededHeight = 100 + checkBoxMap.size()*20;
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

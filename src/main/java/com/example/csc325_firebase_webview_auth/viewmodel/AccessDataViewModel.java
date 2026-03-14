package com.example.csc325_firebase_webview_auth.viewmodel;



import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.*;


public class AccessDataViewModel {

	private final StringProperty userName = new SimpleStringProperty();
	private final StringProperty userMajor = new SimpleStringProperty();
	private final IntegerProperty age = new SimpleIntegerProperty();
	private final ReadOnlyBooleanWrapper writePossible = new ReadOnlyBooleanWrapper();

	public AccessDataViewModel() {
		writePossible.bind(userName.isNotEmpty().and(userMajor.isNotEmpty()));
	}

	public StringProperty userNameProperty() {
		return userName;
	}
	public StringProperty userMajorProperty() {
		return userMajor;
	}

	public IntegerProperty ageProperty() { return age; } // ADDED
	public int getAge() { return age.get(); }
	public void setAge(int age) { this.age.set(age); }

	public ReadOnlyBooleanProperty isWritePossibleProperty() {
		return writePossible.getReadOnlyProperty();
	}
}

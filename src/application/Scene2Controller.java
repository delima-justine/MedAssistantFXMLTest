package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Scene2Controller implements Initializable {
	
	@FXML private TextField emailTextField;
	@FXML private TextField passwordTextField;
	@FXML private TextField usernameTextField;
	@FXML private TextField firstNameTextField;
	@FXML private TextField lastNameTextField;
	@FXML private TextField ageTextField;
	@FXML private DatePicker birthdatePicker;
	@FXML private TextField houseNumberTextField;
	@FXML private TextField streetTextField;
	@FXML private TextField cityTextField;
	@FXML private TextField barangayTextField;
	@FXML private TextField contactTextField;
	@FXML private ComboBox<String> roleSelectionLogin; 
    @FXML private ComboBox<String> genderSelectionRegister;
	
	String[] roles = {"Patient", "Staff", "Admin", "Doctor"};
	String[] genders = {"Male", "Female", "Prefer not to say"}; 
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (roleSelectionLogin != null) {
			roleSelectionLogin.getItems().addAll(roles);
		}
		
		if (genderSelectionRegister != null) {
			genderSelectionRegister.getItems().addAll(genders);
		}
	}
	
	public void switchToScene1(ActionEvent event) throws IOException{
		Session.clear(); // clears the session / log out.
		root = FXMLLoader.load(getClass().getResource("MainDashboard.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setTitle("MedAssistant");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}
		
	public void login(ActionEvent event) throws IOException, SQLException{
		String user_email = emailTextField.getText(); 
		String user_password = passwordTextField.getText();
		String user_role = roleSelectionLogin.getValue();
		String full_name = "";
		String fxml_scene = null;
		String scene_title = null;
		
		if (user_email.isEmpty() || user_password.isEmpty()) {
			Alert alertDialog = new Alert(Alert.AlertType.ERROR);
			alertDialog.setHeaderText("Error");
			alertDialog.setContentText("Please fill all fields.");
			alertDialog.showAndWait();
			return; // return if empty.
		}
		
		// Checks if the user selects a role.
		if (user_role == null || user_role.isEmpty()) {
			Alert alertDialog = new Alert(Alert.AlertType.ERROR);
//			alertDialog.setTitle("Warning");
			alertDialog.setHeaderText("Error");
			alertDialog.setContentText("Please choose a role.");
			alertDialog.showAndWait();
			return; // return if empty.
		} else {
			// Connecting to database to select all accounts with the entered credentials
			 try (Connection conn = MedAssistantDB.getConnection()) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT * FROM UsersInformation WHERE email = '" + 
                		       user_email + 
                		       "' AND password = HASHBYTES('SHA2_256', '" + user_password + "') "
                		       + " AND role = '" + user_role + "'";
                ResultSet rs = stmt.executeQuery(sql);

                if (rs.next()) {
                	// if the user is found, it will display a successful alert.
                    new Alert(Alert.AlertType.INFORMATION, "Login successful!").showAndWait();
                    full_name = rs.getString("first_name") + " " + rs.getString("last_name");
                    Session.setFullname(full_name); // adds the session.
                    Session.setEmail(user_email);
                } else {
                	// if the user is not found, it will display an invalid alert.
                    new Alert(Alert.AlertType.ERROR, "Invalid credentials.").show();
                    return;
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
                return;
            }
			
			switch (user_role) {
				case "Patient":
					fxml_scene = "patientDashboard.fxml";
					scene_title = "Patient Dashboard";
					
					try(Connection conn = MedAssistantDB.getConnection()) {
						Statement stmt = conn.createStatement();
						String sql = "SELECT p.patient_id\r\n"
								+ "FROM patients p\r\n"
								+ "JOIN usersinformation u ON "
								+ "CONVERT(varchar(MAX), DecryptByPassPhrase('MyKey', p.contact_number)) = u.contact_number\r\n"
								+ "WHERE u.email ='" + user_email +"'; ";
						
						ResultSet rs = stmt.executeQuery(sql);
						
						while(rs.next()) {
							// set patient id if patient
							Session.setPatientId(rs.getInt("patient_id"));
						} 
					}
					
					// Dynamic change of scenes based on the role selected of user.
					FXMLLoader loader4 = new FXMLLoader(getClass().getResource(fxml_scene));
					root = loader4.load();
					
					stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setTitle(scene_title);
					stage.setScene(scene);
					stage.centerOnScreen();
					stage.show();
					break;
				case "Doctor":
					fxml_scene = "doctorDashboard.fxml";
					scene_title = "Doctor Dashboard";
					
					// Dynamic change of scenes based on the role selected of user.
					FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml_scene));
					root = loader.load();
					
					// loads the methods of other class.
					sceneController3 sceneController3 = loader.getController();
					sceneController3.setGreeting(full_name);
					
					stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setTitle(scene_title);
					stage.setScene(scene);
					stage.centerOnScreen();
					stage.show();
					break;
				case "Admin":
					fxml_scene = "adminDashboard.fxml";
					scene_title = "Admin Dashboard";
					
					// Dynamic change of scenes based on the role selected of user.
					FXMLLoader loader1 = new FXMLLoader(getClass().getResource(fxml_scene));
					root = loader1.load();
					
					stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setTitle(scene_title);
					stage.setScene(scene);
					stage.centerOnScreen();
					stage.show();
					
					break;
				case "Staff":
					fxml_scene = "staffDashboard.fxml";
					scene_title = "Staff Dashboard";
					
					// Dynamic change of scenes based on the role selected of user.
					FXMLLoader loader3 = new FXMLLoader(getClass().getResource(fxml_scene));
					root = loader3.load();
					
					stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					scene = new Scene(root);
					stage.setTitle(scene_title);
					stage.setScene(scene);
					stage.centerOnScreen();
					stage.show();
					break;
			}	
		}
	}
	 
	public boolean areFieldsEmpty(String[] fields) {
		for(String field: fields) {
			if(field.isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	// Checks if the user clicks OK or Cancel. returns true or false.
	public boolean confirm(String message) {
		return new Alert(Alert.AlertType.CONFIRMATION, message)
				.showAndWait()
				.orElse(ButtonType.CANCEL) == ButtonType.OK;
	}
	
	public void setFieldsToBlank() {
		TextField[] fields = {
				emailTextField,
				passwordTextField,
				usernameTextField,
				firstNameTextField,
				lastNameTextField,
				ageTextField,
				houseNumberTextField,
				streetTextField,
				cityTextField,
				barangayTextField,
				contactTextField
		};
		
		// Clear the fields after registration.
		for(TextField field : fields) {
			field.setText("");
		}
		
		// Clears the role selection combo box and birth date selector.
		roleSelectionLogin.setValue(null);
		birthdatePicker.setValue(null);
	}
	
	public void register(ActionEvent event) {
		int user_age; 
		String user_email = emailTextField.getText().trim(); 
		String user_password = passwordTextField.getText().trim();
		String user_role = roleSelectionLogin.getValue();
		String username = usernameTextField.getText().trim();
		String user_firstname = firstNameTextField.getText().trim();
		String user_lastname = lastNameTextField.getText().trim();
		LocalDate user_birthdate = birthdatePicker.getValue();
		String user_houseNumber = houseNumberTextField.getText().trim();
		String user_street = streetTextField.getText().trim();
		String user_city = cityTextField.getText().trim();
		String user_barangay = barangayTextField.getText().trim();
		String user_contact = contactTextField.getText().trim();
		String[] inputFields = {
				user_email,
				user_password,
				username,
				user_firstname,
				user_lastname,
				user_houseNumber,
				user_street,
				user_city,
				user_barangay,
				user_contact,
		};
		
		if (areFieldsEmpty(inputFields)) {
			Alert alertDialog = new Alert(Alert.AlertType.ERROR);
			alertDialog.setHeaderText("Error");
			alertDialog.setContentText("Please fill all the fields.");
			alertDialog.showAndWait();
			return;
		}
		
		if (user_birthdate == null) {
			Alert alertDialog = new Alert(Alert.AlertType.ERROR);
			alertDialog.setHeaderText("Error");
			alertDialog.setContentText("Please fill the birthdate.");
			alertDialog.showAndWait();
			return;
		}
		
		// Checks if the user selects a role.
		if (user_role == null || user_role.isEmpty()) {
			Alert alertDialog = new Alert(Alert.AlertType.ERROR);
			alertDialog.setHeaderText("Error");
			alertDialog.setContentText("Please choose a role.");
			alertDialog.showAndWait();
			return; // return if empty.
		} else {
			try {
				user_age = Integer.parseInt(ageTextField.getText());
			
				if (confirm("Are the information correct?")) {
					try (Connection conn = MedAssistantDB.getConnection()) {
		                Statement stmt = conn.createStatement();
		                String sql = "INSERT INTO UsersInformation "
		                		+ "(username, email, password, first_name, "
		                		+ "last_name, age, birthdate, house_number, street,"
		                		+ "barangay, city, role, contact_number) "
		                		+ "VALUES ('"
		                		+ username + "', '"
		                		+ user_email + "', "
		                		+ "HASHBYTES('SHA2_256', '" + user_password + "'), '"
		                        + user_firstname + "', '"
		                        + user_lastname + "', '"
		                        + user_age + "', '"
		                        + java.sql.Date.valueOf(user_birthdate) + "', "
		                        + user_houseNumber + ", '"
		                        + user_street + "', '"
		                        + user_barangay + "', '"
		                        + user_city + "', '"
		                        + user_role + "', '"
		                        + user_contact + "')";
		                stmt.executeUpdate(sql);
		                new Alert(Alert.AlertType.INFORMATION, "Registration Successful!").show();
		                setFieldsToBlank();
		                
		                if (user_role == "Patient") {
		                	sql = "INSERT INTO Patients(name, age, gender, contact_number) "
		                		+ "VALUES( "
		                		+ "CONVERT(varbinary(MAX), EncryptByPassPhrase('MyKey', '" + user_firstname + " " + user_lastname + "')), "
		                		+ "CONVERT(varbinary(MAX), EncryptByPassPhrase('MyKey', '" + user_age + "')), "
		                		+ "CONVERT(varbinary(MAX), EncryptByPassPhrase('MyKey', '" + genderSelectionRegister.getValue() + "')), "
		                		+ "CONVERT(varbinary(MAX), EncryptByPassPhrase('MyKey', '" + user_contact + "')) "
		                		+ ");";
		                	stmt.executeUpdate(sql);
		                } else {
		                	return;
		                }
		            } catch (Exception ex) {
		                new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).show();
	            	}
				}
			} catch(Exception e) {
				// catch the error if the user inputs an invalid number.
				new Alert(Alert.AlertType.ERROR, "Enter a valid age").show();
			}
		}
	}
}

//This class calls the main method and logs in to the APPLICATION once the user is authenticated using "Authentication" class

package code.User_Management;
import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import code.Exceptions.AuthenticationError;
import code.Exceptions.BlankFieldError;
import code.Exceptions.UserCreationError;
import code.General_Support.Configure;
import code.General_Support.Messages;
import code.General_Support.Shared_Connection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class Log_In implements Initializable {
	
	@FXML
    private Label Username_Label;

    @FXML
    private Label Password_Label;

    @FXML
    private TextField Username_Text;

    @FXML
    private PasswordField Password_Text;

    @FXML
    private Button btn_Log;

    @FXML
    private Label Data_Server_Label;

    @FXML
    private Label Port_Number_Label;

    @FXML
    private TextField Data_Server_Text;

    @FXML
    private TextField Port_Number_Text;

    @FXML
    private Button btn_Save;
    
    //Handles the Log In button press event
    @FXML
    void handleLogInButtonAction(ActionEvent event) {
    	try
    	{
    		String username=Username_Text.getText();
            String password=Password_Text.getText();
            String data_server=Data_Server_Text.getText();
            String port=Port_Number_Text.getText();
            if(username.length()==0 || password.length()==0)
            {
                throw new BlankFieldError("Please Enter Log In Credentials");
            }
            if(data_server.length()==0 || port.length()==0)
            {
                throw new BlankFieldError("Please Enter Data Server Configuration");
            }
            Shared_Connection.setServerConfig(data_server, port);
            Shared_Connection.createConnection();
            User user=new User(username,password);
            //If the User is authenticated, the Main Window is loaded//
            user.login();
            /**
        	Parent root=FXMLLoader.load(getClass().getResource("/UI/Device Manager.fxml"));
        	Scene scene=new Scene(root);
        	Stage stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        	stage.setScene(scene);
        	stage.setTitle("Main Window");
        	stage.setResizable(false);
        	stage.show();
            ***/
            System.out.println("Logged In");
    	}
    	catch(BlankFieldError e)
    	{
    		Messages.ShowErrorMessage(e.toString(), "Error");
    	}
    	catch(AuthenticationError e)
    	{
    		Username_Text.setText("");
    		Password_Text.setText("");
    		Messages.ShowErrorMessage(e.toString(), "Error");
    	}
    	catch(Exception e)
    	{
    		Username_Text.setText("");
    		Password_Text.setText("");
    		Messages.ShowErrorMessage(e.toString(), "Error");
    	}
    }
   
    //Handles the Save Configuration Button event
    @FXML
    void handleSaveButtonAction(ActionEvent event) {
    	try
        {
	        String data_server=Data_Server_Text.getText();
	        String port=Port_Number_Text.getText();
	        if(data_server.length()==0 || port.length()==0)
	        {
	            throw new BlankFieldError("Please Enter Data Server Configuration");
	        }
	        Configure.saveConfiguration(data_server, port);
	        Messages.ShowInfoMessage("Configuration Saved Successfully", "Success");
        }  
        catch (BlankFieldError ex) 
    	{
            Messages.ShowErrorMessage(ex.toString(), "Error");
        }
    }
  
    
    @FXML
    private TextField Create_Admin_User_Name_Text;
    @FXML
    private PasswordField Create_Admin_Password_Text;
    @FXML
    private PasswordField Create_Admin_Confirm_Pass_Text;
    @FXML
    private Button btn_Save_Admin;
    
    //Handles the Save Admin Button event
    @FXML
    void handleSaveAdminButtonAction(ActionEvent event) 
    {
    	try
    	{
	    	String Server_IP=Data_Server_Text.getText();
	        String Server_Port=Port_Number_Text.getText();
	        String Username=Create_Admin_User_Name_Text.getText();
	        if(Create_Admin_User_Name_Text.getText().length()==0 || Create_Admin_Password_Text.getText().length()==0 || Create_Admin_Confirm_Pass_Text.getText().length()==0)
	        {
	        	throw new BlankFieldError("All fields are mandatory");
	        }
	    	Shared_Connection.setServerConfig(Server_IP, Server_Port); 
	    	Shared_Connection.createConnection();
	    	Connection con=Shared_Connection.getSharedConnection();
	    	String query_authenticate="SELECT * FROM USER_DATA";
			PreparedStatement pst=con.prepareStatement(query_authenticate);
			ResultSet rst=pst.executeQuery();
			if(rst.next())
			{
				Create_Admin_User_Name_Text.setText("");
    			Create_Admin_Password_Text.setText("");
    			Create_Admin_Confirm_Pass_Text.setText("");
				throw new UserCreationError("1 or more existing Administrator account found.Please use one of them to create new user/admin");
			}
			if(Create_Admin_Password_Text.getText().equals(Create_Admin_Confirm_Pass_Text.getText()))
			{
    			User user=new User(Username,Create_Admin_Password_Text.getText());
    			user.create(1);
    			Shared_Connection.closeSharedConnection();
    			Create_Admin_User_Name_Text.setText("");
    			Create_Admin_Password_Text.setText("");
    			Create_Admin_Confirm_Pass_Text.setText("");
    			Messages.ShowInfoMessage("First Administrator Created Succesfully","Success");
			}
			else
			{
				throw new UserCreationError("Password not confirmed");
			}
    	}
    	catch(Exception e)
    	{
    		Messages.ShowErrorMessage(e.toString(), "Error");
    	}
    }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try{
        Data_Server_Text.setText(Configure.readIP());
        Port_Number_Text.setText(Configure.readPort());
        }
        catch(FileNotFoundException e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
            Data_Server_Text.setText("");
            Port_Number_Text.setText("");
        }
        catch(IOException e)
        {
            Messages.ShowErrorMessage(e.toString(), "Error");
            Data_Server_Text.setText("");
            Port_Number_Text.setText("");
        }
    }    

}

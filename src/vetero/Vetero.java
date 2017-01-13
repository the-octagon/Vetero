/*
 * This file is part of Vetero.
 * 
 * Vetero is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vetero is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vetero.  If not, see <http://www.gnu.org/licenses/>.
 */
package vetero;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import popup.Popup;

/**
 *
 * @author andy
 */

public class Vetero extends Application {
    //to store saved preferences and API ID
    File configFile = new File("./Vetero.config");
    
    //text areas put data in. these won't last long
    //more for testing
    TextArea topLeftTextArea = new TextArea();
    TextArea topRightTextArea = new TextArea();
    TextArea bottomTextArea = new TextArea();
    ImageView iconImage = new ImageView(new Image("http://openweathermap.org/img/w/01d.png"));
    Popup popup = new Popup("2017", "a weather information app", "Vetero");
    
    //these are housed in readInConfig()
    //location that is sent to API query
    static String city = "";
    //API key that is obtained from http://openweathermap.org/appid and placed in config file
    String appID = "";
    //
    char preferredTempUnit;
    Properties properties = new Properties();
    Weather weather = null;
    
    @Override
    public void start(Stage primaryStage) {    
        //create menu bar
            MenuBar menuBar = new MenuBar();
        //create menus
            Menu fileMenu = new Menu("File");
            Menu aboutMenu = new Menu("About");
        //create menuItems
            //file
            MenuItem fileMenuNewCity = new MenuItem("New city");
            MenuItem fileMenuManageCities = new MenuItem("Manage cities");
            //about
            MenuItem aboutMenuAbout = new MenuItem("About");
            MenuItem aboutMenuContact = new MenuItem("Contact");
            MenuItem aboutMenuLicense = new MenuItem("License");
        //add it all together
            //first the menuItems
            fileMenu.getItems().addAll(fileMenuNewCity, fileMenuManageCities);
            aboutMenu.getItems().addAll(aboutMenuAbout, aboutMenuContact, aboutMenuLicense);
            //then the menus
            menuBar.getMenus().addAll(fileMenu, aboutMenu);
        //menuItem functions
        fileMenuNewCity.setOnAction((ActionEvent t) -> {
            newCity();
        });
        aboutMenuAbout.setOnAction((ActionEvent t) -> {
            popup.aboutDialog();
        });
        aboutMenuContact.setOnAction((ActionEvent t) -> {
            popup.contactDialog();
        });
        aboutMenuLicense.setOnAction((ActionEvent t) -> {
            popup.licenseDialog();
        });
        /*fileMenuManageCities.setOnAction((ActionEvent t) -> {
            manageCities();
        });*/
        
        //set properties for stage/scene components
        topLeftTextArea.setWrapText(true);
        topRightTextArea.setWrapText(true);
        //create panes, set scene, show stage
        BorderPane root = new BorderPane();
        GridPane mainArea = new GridPane();
        //add components to mainArea subpane
        
        mainArea.add(topLeftTextArea, 1, 1);
        //mainArea.add(topRightTextArea, 2, 1);
        mainArea.add(bottomTextArea, 1, 2, 2, 2);
        mainArea.add(iconImage, 2, 1);      
        mainArea.setMargin(iconImage, new Insets(2,2,2,2));
        //add components to root pane
        root.setTop(menuBar);
        root.setCenter(mainArea);
        //commence magification
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Vetero");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //now that everything is built, to the weather functions
        //read in info from config file
        readInConfig();

        
        //create new weather object with appID and city and
        //display weather data in mainArea TextAreas
        displayWeather();
    }
    
    public void displayWeather() {
        weather = new Weather(appID, city);
        topLeftTextArea.setText(
                weather.getDescription() + "\n" + weather.getTemp(preferredTempUnit) + "\u00b0 F"
        );
        bottomTextArea.setText(weather.getLocation());
        iconImage.setImage(weather.getIconImage());
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void newCity() {
        //open new city dialog
        TextInputDialog dialog = new TextInputDialog("Gravity Falls Oregon");
        
        dialog.setTitle("Add A New City");
        dialog.setHeaderText("Enter the Name");
        dialog.setContentText("City State:");

        Optional<String> result = dialog.showAndWait();
        
        if(result.isPresent()) {
            
            city = result.toString().substring(result.toString().indexOf("[")+1, result.toString().indexOf("]"));
            properties.put("savedCity", city);
            writeOutConfig();
            displayWeather();
        } else {
            city = "";
        }
    }

    //get API ID from http://openweathermap.org/appid and place in the config file.
    public void readInConfig() {
        try {
            InputStream is = new FileInputStream(configFile);
            properties.load(is);
            appID = properties.getProperty("appID");
            city = properties.getProperty("savedCity");
            preferredTempUnit = properties.getProperty("preferredTempUnit").charAt(0);
            is.close();
        } catch (FileNotFoundException e) {
            System.err.print(e);
        } catch (IOException e) {
            System.err.print(e);
        }
    }

    public boolean testForConfigFile() {
        return configFile.exists();
    }

    public void writeOutConfig() {
        try {
            FileOutputStream os = new FileOutputStream(configFile);
            properties.store(new FileOutputStream(configFile), null);
        } catch (IOException e) {
            System.err.println("Problem writing to the file Vetero.config in writeOutConfig()");
        }
    }



//under construction
    public void manageCities() {
        Stage manageCitiesStage = new Stage ();        
        GridPane manageCitiesGridPane = new GridPane();
        Button newCityButton = new Button("New...");
        
        newCityButton.setOnAction((ActionEvent t) -> {
            newCity();
        });
        
        
        manageCitiesGridPane.add(newCityButton, 1, 1);

        Scene scene = new Scene(manageCitiesGridPane, 350, 300);
        
        manageCitiesStage.setScene(scene);
        manageCitiesStage.show();
    }


}
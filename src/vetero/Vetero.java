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
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author andy
 */

public class Vetero extends Application {
    File configFile = new File("./Vetero.config");
    
    TextArea topLeftTextArea = new TextArea();
    TextArea topRightTextArea = new TextArea();
    TextArea bottomTextArea = new TextArea();
    static String city = "";
    //obtain from http://openweathermap.org/appid and add to config file, see readInConfig()
    String appID = "";
    
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
        fileMenuManageCities.setOnAction((ActionEvent t) -> {
            manageCities();
        });
        
        //set properties for stage/scene components
        topLeftTextArea.setWrapText(true);
        topRightTextArea.setWrapText(true);
        //create panes, set scene, show stage
        BorderPane root = new BorderPane();
        GridPane mainArea = new GridPane();
        //add components to mainArea subpane
        mainArea.add(topLeftTextArea, 1, 1);
        mainArea.add(topRightTextArea, 2, 1);
        mainArea.add(bottomTextArea, 1, 2, 2, 2);
        //add components to root pane
        root.setTop(menuBar);
        root.setCenter(mainArea);
        //commence magification
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("Vetero");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            writeOutConfig();
        }); 
        
        
        //determine if config file exists and create one or read in
        if (testForConfigFile()) {
            readInConfig();
        } else {
            writeOutConfig();
        }
        
        //fetch weather data
        Weather weather = new Weather(appID, city);
        
        
        
        //display weather data
        topLeftTextArea.setText(
                weather.getDescription() + "\n" + weather.getTempF() + "\u00b0 F"
        );
        bottomTextArea.setText(weather.getLocation());
                

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
        } else {
            city = "";
        }
    }

    
    public void writeOutConfig() {
        /*try {
            FileWriter fw = new FileWriter(configFile);
            String output = "city: " + city;
            fw.write(output);
            fw.close();
            
        } catch (IOException e) {
            System.err.println("Problem writing to the file Vetero.config in writeOutConfig()");
        }*/
    }
    
    public boolean testForConfigFile() {
        return configFile.exists();
    }
    
    //get API ID from http://openweathermap.org/appid and place in the config file.
    public void readInConfig() {
        try {
            Properties prop = new Properties();
            InputStream is = new FileInputStream(configFile);
            prop.load(is);
            appID = prop.getProperty("appID");
            city = prop.getProperty("savedCity");
            is.close();            
        } catch (FileNotFoundException e) {
            System.err.print(e);
        } catch (IOException e) {
            System.err.print(e);
        }
    }
    
    
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
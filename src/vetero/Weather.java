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
 * along with Vetero.  If not, see <http://www.gnu.org/licenses/>.
 */
package vetero;

import com.sun.webkit.network.URLs;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONObject;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author andy
 */
class Weather {
    private static Weather w = new Weather();
    protected static Class wc = w.getClass();
    private String description;
    private Double humidity;
    private Image iconImage;
    private String location;
    private Double pressure;
    private Date sunrise;
    private Date sunset;
    private Double temp;
    private Double visibility;
    private String weatherReport;
    private Double windSpeed;
    private Double windDeg;
    private static DecimalFormat df = new DecimalFormat(".#");

        

    public Weather(String appID, String city) {
        try {
            fetchWeatherReport(appID, city);
            convertWeatherReport();
        } catch (NullPointerException npe) {
            System.out.println("A NullPointerException occured in Weather()");
        }
        
    } 
    public Weather() {

    }

    //mutators
    private void setDescription(String description) {
        this.description = description;
    }
    private void setHumidity(Double humidity) {
        this.humidity = humidity;
    }
    private void setIconImage(String iconCode) {
        Image iconImage = null;
        /*URL iconUrl = null;
        try {
            iconUrl = new URL("http://openweathermap.org/img/w/" + iconCode + ".png");
            System.out.println(iconUrl);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
        }
        File iconFile = new File("./temp.png");
        try {
            FileUtils.copyURLToFile(iconUrl, iconFile);
            System.out.println("copyURLtoFile succeeded");
        } catch (IOException ex) {
            Logger.getLogger(Weather.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(iconFile.getAbsolutePath());
        */
        try {
            iconImage = new Image("http://openweathermap.org/img/w/" + iconCode + ".png");
        } catch (Exception e) {
            System.out.println("exception occured");
        }
        
        this.iconImage = iconImage;
    }
    private void setLocation(String location){
        this.location = location;
    }
    private void setPressure(Double pressure) {
        this.pressure = pressure;
    }
    private void setSunrise(long sunrise) {
        this.sunrise = new Date(sunrise);
    }
    private void setSunset(long sunset) {
        this.sunset = new Date(sunset);
    }
    private void setTemp(Double temp) {
        this.temp = temp;
    }
    private void setVisibility(Double visibility) {
        this.visibility = visibility;
    }
    private void setWindDeg(Double windDeg) {
        this.windDeg = windDeg;
    }
    private void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }


    //accessors
    /**
     * 
     * @return A String that describes weather conditions, i.e. "partly cloudy", "scattered thunderstorms"
     */
    public String getDescription() {
        if (description != null) {
            return description;
        }
        return "error";
    }
    /**
     * 
     * @return A double representing humidity as a percentage.
     */
    public double getHumidity() {
        if (humidity != null) {
            return humidity;
        }
        return 0.0;
    }
    public Image getIconImage() {
        return iconImage;
    }
    /**
     * 
     * @return A String that contains the name of the location queried.
     */
    public String getLocation() {
        if (location != null) {
            return location;
        }
        return "error";
    }
    public double getPressure() {
        if (pressure != null) {
            return pressure;
        }
        return 0.0;
    }
    public Date getSunrise() {
        if (sunrise != null) {
            return sunrise;
        }
        return new Date();
    }
    public Date getSunset() {
        if (sunset != null) {
            return sunset;
        }
        return new Date();
    }
    /**
     * @param
     * @return A String that contains the temperature in Kelvin
     */
    public String getTemp() {
        if (temp != null) {
            //default is kelvin
            return df.format(temp);
        }
        return "error";
    }
    /**
     * 
     * @param tempUnit The preferred unit of measure as 'c', 'C', 'f', 'F', 'k', or 'K' Celsius, Fahrenheit, or Kelvin, respectively.
     * @return A String that contains the temperature in a given unit of measure.
     */
    public String getTemp(char tempUnit) {
        if (temp != null) {
            switch (tempUnit) {
                case 'F':
                case 'f':
                    return df.format(temp * 1.8 - 459.67) + "\u00b0 F";
                case 'C':
                case 'c':
                    return df.format(temp - 273.15) + "\u00b0 C";
                case 'K':
                case 'k':
                    return df.format(temp) + " K";
                default:
                    System.out.println("You passed an invalid temperature unit.");
                    System.out.println("An empty string will be returned.");
                    return "";
            }
        }
        return "";
    }
    public double getVisibility() {
        if (visibility != null) {
            return visibility;
        }
        return 0.0;
    }
    public double getWindDeg() {
        if (windDeg != null) {
            return windDeg;
        }
        return 0.0;
    }
    public double getWindSpeed() {
        if (windSpeed != null) {
            return windSpeed;
        }
        return 0.0;
    }

    //private methods
    private void fetchWeatherReport(String appID, String city) {
        URL u;
        InputStream is = null;
        DataInputStream dis;
        String s;

        try {
            u = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + city.replace(" ", "%20") + "&appid=" + appID);
            is = u.openStream();
            dis = new DataInputStream(new BufferedInputStream(is));
            while ((s = dis.readLine()) != null) {
                weatherReport = s;
            }
        } catch (Exception e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Vetero - Error");
            alert.setHeaderText("An error occured.");
            alert.setContentText("Are you connected to the internet?");

            alert.showAndWait();
        } finally {
            try {
                is.close();
             } catch (IOException ioe) {
                 //do nothing
             }
        }
    }
    private void convertWeatherReport() {
        JSONObject weatherObject = null;
        try  {
            weatherObject = new JSONObject(weatherReport);
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport while creating JSONObject");
            e.printStackTrace();
        }
        try {
            setDescription(weatherObject.getJSONArray("weather").getJSONObject(0).getString("description"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setDescription");
            e.printStackTrace();
        }
        try {
            setHumidity(weatherObject.getJSONObject("main").getDouble("humidity"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setHumidity");
            e.printStackTrace();
        }
        try {
            setIconImage(weatherObject.getJSONArray("weather").getJSONObject(0).getString("icon"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setIconImage");
            e.printStackTrace();
        }
        try {
            setLocation(weatherObject.getString("name"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setLocation");
            e.printStackTrace();
        }
        try {
            setPressure(weatherObject.getJSONObject("main").getDouble("pressure"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setPressure");
            e.printStackTrace();
        }
        try {
            setSunset(weatherObject.getJSONObject("sys").getLong("sunset"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setSunset");
            e.printStackTrace();
        }
        try {
            setSunrise(weatherObject.getJSONObject("sys").getLong("sunrise"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setSunrise");
            e.printStackTrace();
        }
        try {
            setTemp(weatherObject.getJSONObject("main").getDouble("temp"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setTemp");
            e.printStackTrace();
        }
        try {
            setVisibility(weatherObject.getDouble("visibility"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setVisibility");
            e.printStackTrace();
        }
        try {
            setWindDeg(weatherObject.getJSONObject("wind").getDouble("deg"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setWindDeg");
            e.printStackTrace();
        }
        try {
            setWindSpeed(weatherObject.getJSONObject("wind").getDouble("speed"));
        } catch (Exception e) {
            System.out.println("Something went wrong in convertWeatherReport under setWindSpeed");
            e.printStackTrace();
        }
    }
}
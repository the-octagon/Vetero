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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;
import org.json.JSONObject;

/**
 *
 * @author andy
 */
class Weather {
        private String description;
        private Double humidity;
        private String location;
        private Double pressure;
        private Date sunrise;
        private Date sunset;
        private Double temp;
        private Double temp_min;
        private Double temp_max;
        private Double visibility;
        private String weatherReport;
        private Double windSpeed;
        private Double windDeg;
        private static DecimalFormat df = new DecimalFormat(".#");

        public Weather(String appID, String city) {
            fetchWeatherReport(appID, city);
            convertWeatherReport();
        } 
        
        //mutators
        private void setDescription(String description) {
            this.description = description;
        }
        private void setHumidity(Double humidity) {
            this.humidity = humidity;
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
        private void setTemp_min(Double temp_min) {
            this.temp_min = temp_min;
        }
        private void setTemp_max(Double temp_max) {
            this.temp_max = temp_max;
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
        public String getDescription() {
            return description;
        }
        public double getHumidity() {
            return humidity;
        }
        public String getLocation() {
            return location;
        }
        public double getPressure() {
            return pressure;
        }
        public Date getSunrise() {
            return sunrise;
        }
        public Date getSunset() {
            return sunset;
        }
        public String getTemp() {
            return df.format(temp);
        }
        public String getTempC() {
            return df.format(temp - 273.15);
        }
        public String getTempF() {
            return df.format(temp * 1.8 - 459.67);
        }
        public double getTemp_min() {
            return temp_min;
        }
        public double getTemp_max() {
            return temp_max;
        }
        public double getVisibility() {
            return visibility;
        }
        public double getWindDeg() {
            return windDeg;
        }
        public double getWindSpeed() {
            return windSpeed;
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
                
                
            }catch (MalformedURLException mue) {
                System.out.println("Ouch - a MalformedURLException happened.");
                mue.printStackTrace();
            } catch (IOException ioe) {
                System.out.println("Oops- an IOException happened.");
                ioe.printStackTrace();
            } finally {
                try {
                    is.close();
                 } catch (IOException ioe) {
                     //do nothing
                 }
            }

        }
        private void convertWeatherReport() {
            JSONObject weatherObject = new JSONObject(weatherReport);
            
            setDescription(weatherObject.getJSONArray("weather").getJSONObject(0).getString("description"));
            setHumidity(weatherObject.getJSONObject("main").getDouble("humidity"));
            setLocation(weatherObject.getString("name"));
            setPressure(weatherObject.getJSONObject("main").getDouble("pressure"));
            setSunset(weatherObject.getJSONObject("sys").getLong("sunset"));
            setSunrise(weatherObject.getJSONObject("sys").getLong("sunrise"));
            setTemp(weatherObject.getJSONObject("main").getDouble("temp"));
            setTemp_min(weatherObject.getJSONObject("main").getDouble("temp_min"));
            setTemp_max(weatherObject.getJSONObject("main").getDouble("temp_max"));
            setVisibility(weatherObject.getDouble("visibility"));
            setWindDeg(weatherObject.getJSONObject("wind").getDouble("deg"));
            setWindSpeed(weatherObject.getJSONObject("wind").getDouble("speed"));
            
            
        }
    }
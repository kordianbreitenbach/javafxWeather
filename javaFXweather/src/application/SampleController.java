package application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
public class SampleController {
	 @FXML
	    private TextField cityName;

	  
	    @FXML
	    private Button enterCityName;
	    @FXML
	    private Button deleteButton;
	    
	
	    @FXML private TableView<WeatherData> weatherTable;
	    @FXML private TableColumn<WeatherData, String> cityColumn;
	    @FXML private TableColumn<WeatherData, Number> temperatureColumn;
	    @FXML private TableColumn<WeatherData, Integer> humidityColumn;
	    @FXML private TableColumn<WeatherData, Number> windSpeedColumn;
	    @FXML private TableColumn<WeatherData, String> conditionsColumn;
	    
	    
	    private final List<WeatherData> allWeatherData = new ArrayList<>();
	 public void initialize() {
		
		 enterCityName.setOnAction(event -> fetchWeatherData());
		 deleteButton.setOnAction(event -> deleteWeatherData());

		 cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		 temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		 humidityColumn.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		 windSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("windSpeed"));
		 conditionsColumn.setCellValueFactory(new PropertyValueFactory<>("conditions"));

		 
		 weatherTable.setItems(FXCollections.observableArrayList(allWeatherData));
	 }
	 
	 public class WeatherData {
		    private String city;
		    private double temperature;
		    private double feelsLike;
		    private int humidity;
		    private double windSpeed;
		    private String conditions;

		    public WeatherData(String city, double temperature, double feelsLike, int humidity, double windSpeed, String conditions) {
		        this.city = city;
		        this.temperature = temperature;
		        this.feelsLike = feelsLike;
		        this.humidity = humidity;
		        this.windSpeed = windSpeed;
		        this.conditions = conditions;
		    }
		   

		    @Override
		    public String toString() {
		        return String.format(
		            "Current Conditions in %s:\n" +
		            "- Temperature: %.1f°C (Feels like: %.1f°C)\n" +
		            "- Humidity: %d%%\n" +
		            "- Wind Speed: %.1f km/h\n" +
		            "- Conditions: %s\n",
		            city, temperature, feelsLike, humidity, windSpeed, conditions);
		    }
		    public String getCity() { return city; }
		    public double getTemperature() { return temperature; }
		    public int getHumidity() { return humidity; }
		    public double getWindSpeed() { return windSpeed; }
		    public String getConditions() { return conditions; }
		}
	 
	 
	 @FXML
	 private void fetchWeatherData() {
	     String city = cityName.getText().trim();
	     if (city.isEmpty()) {
	         return;
	     }

	     String apiKey = "FJZE6V2YKQYZZAGPQGUE6LYTS";
	     String urlString = String.format("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%s?unitGroup=metric&include=current&key=%s&contentType=json", city, apiKey);

	     try {
	         URL url = new URL(urlString);
	         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	         connection.setRequestMethod("GET");
	         Reader reader = new InputStreamReader(connection.getInputStream());

	         JsonObject responseJson = JsonParser.parseReader(reader).getAsJsonObject();
	         JsonObject currentConditions = responseJson.getAsJsonObject("currentConditions");

	         double temp = currentConditions.get("temp").getAsDouble();
	         double feelslike = currentConditions.get("feelslike").getAsDouble();
	         int humidity = currentConditions.get("humidity").getAsInt();
	         double windspeed = currentConditions.get("windspeed").getAsDouble();
	         String conditions = currentConditions.get("conditions").getAsString();

	         WeatherData weatherData = new WeatherData(city, temp, feelslike, humidity, windspeed, conditions);
	         allWeatherData.add(weatherData); // Add the new data to the list
	         weatherTable.setItems(FXCollections.observableArrayList(allWeatherData)); //
	         // Update UI with information from all WeatherData objects
	         javafx.application.Platform.runLater(() -> {
	             StringBuilder allDataText = new StringBuilder();
	             for (WeatherData data : allWeatherData) {
	                 allDataText.append(data.toString()).append("\n");
	             }
	        
	         });
	     } catch (Exception e) {
	         e.printStackTrace();
		   

	        	    // Tworzenie okienka dialogowego z błędem
	        	    Alert alert = new Alert(Alert.AlertType.ERROR);
	        	    alert.setTitle("Błąd");
	        	    alert.setHeaderText("Błąd podczas ładowania danych");
	        	    alert.setContentText("Nie udało się pozyskać danych z Internetu.");

	        	    // Wyświetlenie okienka
	        	    alert.showAndWait();
	     }
	 }
	 private void deleteWeatherData() {
		    String cityToRemove = cityName.getText().trim();
		    if (cityToRemove.isEmpty()) {
		        return;
		    }

		    for (int i = allWeatherData.size() - 1; i >= 0; i--) {
		        WeatherData data = allWeatherData.get(i);
		        if (data.city.equals(cityToRemove)) {
		            allWeatherData.remove(i);
		            break; // Remove only the most recent match
		        }
		    }

		    updateWeatherInfo();
		}
	 private void updateWeatherInfo() {
		    StringBuilder allDataText = new StringBuilder();
		    for (WeatherData data : allWeatherData) {
		        allDataText.append(data.toString()).append("\n");
		    }
		    
		    javafx.application.Platform.runLater(() -> {
		    	
		       
		        weatherTable.setItems(FXCollections.observableArrayList(allWeatherData)); //
		    });
		}
}

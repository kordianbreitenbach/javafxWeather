package application;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
public class SampleController {
	 @FXML
	    private TextField cityName;

	  
	    @FXML
	    private Button enterCityName;
	    @FXML
	    private Button deleteButton;
	    
	
	    @FXML private TableView<WeatherData> weatherTable;
	    @FXML private TableColumn<WeatherData, LocalDate> dayColumn;
	    @FXML private TableColumn<WeatherData, String> cityColumn;
	    @FXML private TableColumn<WeatherData, Number> temperatureColumn;
	    @FXML private TableColumn<WeatherData, Integer> humidityColumn;
	    @FXML private TableColumn<WeatherData, Number> windSpeedColumn;
	    @FXML private TableColumn<WeatherData, String> conditionsColumn;
	    
	    WeatherDataStorage storage = new WeatherDataStorage();
	    List<WeatherData> loadweather = storage.loadWeatherDataList("src/application/weatherdata.json");
	    private final List<WeatherData> allWeatherData = new ArrayList<>(loadweather);
	   
	   
	    public class WeatherDataStorage {

	        private Gson gson = new Gson();

	        public void saveWeatherDataList(List<WeatherData> list, String filePath) {
	            try (FileWriter writer = new FileWriter(filePath)) {
	                gson.toJson(list, writer);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	        public List<WeatherData> loadWeatherDataList(String filePath) {
	            try (FileReader reader = new FileReader(filePath)) {
	                return gson.fromJson(reader, new TypeToken<List<WeatherData>>(){}.getType());
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
	        }
	    }
	 public void initialize() {
		
		 enterCityName.setOnAction(event -> fetchWeatherData());
		 deleteButton.setOnAction(event -> deleteWeatherData());
		 dayColumn.setCellValueFactory(new PropertyValueFactory<>("day"));
		 cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		 temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));
		 humidityColumn.setCellValueFactory(new PropertyValueFactory<>("humidity"));
		 windSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("windSpeed"));
		 conditionsColumn.setCellValueFactory(new PropertyValueFactory<>("conditions"));

		 
		 weatherTable.setItems(FXCollections.observableArrayList(allWeatherData));
	 }
	 
	 public class WeatherData {
		    private String city;
		    private LocalDate  day;
		    private double temperature;
		    private double feelsLike;
		    private int humidity;
		    private double windSpeed;
		    private String conditions;

		    public WeatherData(String city,LocalDate  day, double temperature, double feelsLike, int humidity, double windSpeed, String conditions) {
		        this.city = city;
		        this.day=day;
		        this.temperature = temperature;
		        this.feelsLike = feelsLike;
		        this.humidity = humidity;
		        this.windSpeed = windSpeed;
		        this.conditions = conditions;
		    }
		   

		    @Override
		    public String toString() {
		        return String.format(
		            "Current Conditions in %s on %s:\n" +  // Assuming you want to include the date in the output
		            "- Temperature: %.1f°C (Feels like: %.1f°C)\n" +
		            "- Humidity: %d%%\n" +
		            "- Wind Speed: %.1f km/h\n" +
		            "- Conditions: %s\n",
		            city, day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), temperature, feelsLike, humidity, windSpeed, conditions);
		    }

		    public String getCity() { return city; }
		    public LocalDate  getDay() { return day; }
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
	     LocalDate tomorrow = LocalDate.now().plusDays(1);
	     LocalDate today=LocalDate.now();
	     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	     String dateString = tomorrow.format(formatter);
	     String urlString = String.format("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%s?unitGroup=metric&include=current&key=%s&contentType=json", city, apiKey);
	     String urlStringTommorow = String.format(
	    		    "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/%s/%s?unitGroup=metric&include=days&key=%s&contentType=json",
	    		    city, dateString, apiKey);
try {
	    	 
	         URL url = new URL(urlString);
	         URL urlTommorow = new URL(urlStringTommorow);
	         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	         HttpURLConnection connectiontommorow = (HttpURLConnection) urlTommorow.openConnection();
	         connection.setRequestMethod("GET");
	         connectiontommorow.setRequestMethod("GET");
	         Reader reader = new InputStreamReader(connection.getInputStream());
	         Reader readerTomorrow = new InputStreamReader(connectiontommorow.getInputStream());
	         JsonObject responseJson = JsonParser.parseReader(reader).getAsJsonObject();
	         JsonObject currentConditions = responseJson.getAsJsonObject("currentConditions");
	         JsonObject responseJsontomorrow = JsonParser.parseReader(readerTomorrow).getAsJsonObject();
              JsonArray daysArray = responseJsontomorrow.getAsJsonArray("days");
	        
	          
	         JsonObject currentConditionstomorrow = daysArray.get(0).getAsJsonObject();
	         
	         double temp = currentConditions.get("temp").getAsDouble();
	         double feelslike = currentConditions.get("feelslike").getAsDouble();
	         int humidity = currentConditions.get("humidity").getAsInt();
	         double windspeed = currentConditions.get("windspeed").getAsDouble();
	         String conditions = currentConditions.get("conditions").getAsString();
	         double tempt = currentConditionstomorrow.get("temp").getAsDouble();
	         double feelsliket = currentConditionstomorrow.get("feelslike").getAsDouble();
	         int humidityt = currentConditionstomorrow.get("humidity").getAsInt();
	         double windspeedt = currentConditionstomorrow.get("windspeed").getAsDouble();
	         String conditionst = currentConditionstomorrow.get("conditions").getAsString();

	         WeatherData weatherData = new WeatherData(city,today,  temp, feelslike, humidity, windspeed, conditions);
	         WeatherData weatherDatat = new WeatherData(city,tomorrow, tempt, feelsliket, humidityt, windspeedt, conditionst);
	         allWeatherData.add(weatherData);
	         allWeatherData.add(weatherDatat);
	        
	    // Add the new data to the list
	         weatherTable.setItems(FXCollections.observableArrayList(allWeatherData)); //
	         storage.saveWeatherDataList(allWeatherData, "src/application/weatherdata.json");
	         // Update UI with information from all WeatherData objects
	         javafx.application.Platform.runLater(() -> {
	             StringBuilder allDataText = new StringBuilder();
	             for (WeatherData data : allWeatherData) {
	                 allDataText.append(data.toString()).append("\n");
	                 
	             }
	              // Use setText to replace existing content
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
		             // Remove only the most recent match
		        }
		    }
		    storage.saveWeatherDataList(allWeatherData, "src/application/weatherdata.json");
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
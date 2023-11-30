import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main extends Application {
    private TextField ageField, heightField, weightField;
    private ComboBox<String> genderComboBox;
    private TextArea resultArea;
    private PieChart pieChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BMI Calculator");

        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(createInputPanel());
        pieChart = createChart();
        borderPane.setCenter(pieChart);
        resultArea = new TextArea();
        borderPane.setBottom(resultArea);

        Scene scene = new Scene(borderPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createInputPanel() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label genderLabel = new Label("Gender:");
        Label ageLabel = new Label("Age:");
        Label heightLabel = new Label("Height (cm):");
        Label weightLabel = new Label("Weight (kg):");

        genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");
        ageField = new TextField();
        heightField = new TextField();
        weightField = new TextField();

        Button calculateButton = new Button("Calculate");
        calculateButton.setOnAction(e -> calculateBMI());

        Button saveButton = new Button("Save Results");
        saveButton.setOnAction(e -> saveResults());

        gridPane.add(genderLabel, 0, 0);
        gridPane.add(genderComboBox, 1, 0);
        gridPane.add(ageLabel, 0, 1);
        gridPane.add(ageField, 1, 1);
        gridPane.add(heightLabel, 0, 2);
        gridPane.add(heightField, 1, 2);
        gridPane.add(weightLabel, 0, 3);
        gridPane.add(weightField, 1, 3);
        gridPane.add(calculateButton, 0, 4);
        gridPane.add(saveButton, 1, 4);

        return gridPane;
    }

    private PieChart createChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("BMI Category");
        return pieChart;
    }

    private void calculateBMI() {
        try {
            String gender = genderComboBox.getValue();
            int age = Integer.parseInt(ageField.getText());
            double height = Double.parseDouble(heightField.getText());
            double weight = Double.parseDouble(weightField.getText());

            // Логіка обчислення ІМТ та інших параметрів
            double bmi = calculateBMIValue(height, weight);
            String category = determineBMICategory(bmi);
            double healthyWeightLower = calculateHealthyWeightLower(height);
            double healthyWeightUpper = calculateHealthyWeightUpper(height);
            double ponderalIndex = calculatePonderalIndex(height, weight);

            // Оновлення діаграми
            updateChart(bmi);

            // Оновлення текстової області результатів
            String resultText = String.format("BMI: %.2f\nCategory: %s\nHealthy Range: %.2f - %.2f\nIdeal Weight: %.2f\nPonderal Index: %.2f",
                    bmi, category, healthyWeightLower, healthyWeightUpper, calculateIdealWeight(height), ponderalIndex);
            resultArea.setText(resultText);
        } catch (NumberFormatException e) {
            showAlert("Invalid input. Please enter valid numeric values.");
        }
    }

    private double calculateBMIValue(double height, double weight) {
        // Логіка обчислення ІМТ
        return weight / Math.pow(height / 100, 2);
    }

    private String determineBMICategory(double bmi) {
        // Логіка визначення категорії ІМТ
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal Weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private double calculateHealthyWeightLower(double height) {
        // Логіка обчислення нижньої межі здорової ваги
        return 18.5 * Math.pow(height / 100, 2);
    }

    private double calculateHealthyWeightUpper(double height) {
        // Логіка обчислення верхньої межі здорової ваги
        return 24.9 * Math.pow(height / 100, 2);
    }

    private double calculateIdealWeight(double height) {
        // Логіка обчислення ідеальної ваги
        return 22.0 * Math.pow(height / 100, 2);
    }

    private double calculatePonderalIndex(double height, double weight) {
        // Логіка обчислення індексу Пондералу
        return weight / Math.pow(height / 100, 3);
    }

    private void updateChart(double bmi) {
        // Очистіть попередні дані діаграми
        pieChart.getData().clear();

        // Додайте нові дані до діаграми
        PieChart.Data underweightData = new PieChart.Data("Underweight", (bmi < 18.5) ? 100 : 0);
        PieChart.Data normalWeightData = new PieChart.Data("Normal Weight", (18.5 <= bmi && bmi < 24.9) ? 100 : 0);
        PieChart.Data overweightData = new PieChart.Data("Overweight", (24.9 <= bmi && bmi < 29.9) ? 100 : 0);
        PieChart.Data obeseData = new PieChart.Data("Obese", (bmi >= 29.9) ? 100 : 0);

        pieChart.getData().addAll(underweightData, normalWeightData, overweightData, obeseData);
    }

    private void saveResults() {
        try {
            String fileName = "bmi_results.txt";

            // Отримати результати для збереження
            String resultText = resultArea.getText();

            // Використовуємо try-with-resources для автоматичного закриття ресурсів
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write(resultText);
            }

            showAlert("Results saved to " + fileName);
        } catch (IOException e) {
            showAlert("Error saving results to a file.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}




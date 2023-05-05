package forms;

import generators.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.NumberSample;
import testsGenerators.ParamsTest;
import testsGenerators.TestsSample;
import testsGenerators.graphictest.*;
import testsGenerators.statistictest.*;

import java.io.*;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestGenController {

    public ComboBox<String> loadComboBox;
    public ComboBox<String> genNumComboBox;
    public ComboBox<String> initNumComboBox;
    public Spinner<Integer> capacityNumSpinner;
    public Spinner<Integer> initNumberSpinner;
    public Spinner<Integer> sizeSampleSpinner;
    public Spinner<Integer> numberSamplesSpinner;
    public Tab mainTab;
    public Label loadLabel;
    public Label genLabel;
    public Label sizeLabel;
    public Label initNumLabel;
    public Label capacityLabel;
    public Label numberSamplesLabel;
    public Label initNumberLabel;
    public TextArea resTextArea;
    public Tab graphicTab;
    public Canvas graphTestCanvas;
    public StackedBarChart<String, Number> diagramBarChart;
    public Tab diagramTab;
    public Tab resTab;
    public TextField fileTextField;
    public Button chooseButton;
    public Button saveFileButton;
    public Button mainButton;
    public ListView<Integer> randListView;
    public Button testButton;
    public Label infoLabel;
    public CheckBox loadCheckBox;
    public CategoryAxis intervalCategoryAxis;
    public NumberAxis chastotaNumberAxis;
    public Tab graphicDopTab;
    public Canvas graphDopTestCanvas;
    public AnchorPane grafDopAnchorPane;
    public Label resLabel;
    public Label resProcLabel;
    public Label aLabel;
    public Spinner<Double> aSpinner;
    public StackedBarChart<String, Number> diagramBBarChart;
    public Tab diagramBTab;
    public Tab diagramSeriesTab;
    public StackedBarChart<String, Number> series01BarChart;
    public StackedBarChart<String, Number> series01BarChart1;
    public StackedBarChart<String, Number> series01BarChart2;
    public Tab diagramMonotonyTab;
    public StackedBarChart<String, Number> diagramMonotonyBarChart;
    public Tab diagramAutocorrelationTab;
    public StackedBarChart<String, Number> diagramAutocorrelationBarChart;
    public CategoryAxis autocorrelationXAxis;
    public NumberAxis autocorrelationYAxis;
    private File fileObject;
    private ChangeListener<Integer> changeL;
    private int k = 0;
    private NumberSample numberSample;
    private ChangeListener<Boolean> checkL;

    @FXML
    public void initialize() {
        loadComboBox.getItems().addAll("Программный", "Файловый");
        loadComboBox.getSelectionModel().select("Программный");
        genNumComboBox.getItems().addAll("Алгоритм_ГОСТ_ИСО_24153", "GFSR_генератор_с_3_параметрами",
                "GFSR_генератор_с_5_параметрами", "Генератор_Таусворта", "Генератор_Твистера", "LCG_генератор_V1",
                "LCG_генератор_V2", "Мультипликативный_генератор_V1", "Мультипликативный_генератор_V2", "Мультипликативный_генератор_V3", "Мультипликативный_генератор_V4", "Random_генератор", "SecureRandom_генератор",
                "SplittableRandom_генератор");
        genNumComboBox.getSelectionModel().select("LCG_генератор_V1");
        initNumComboBox.getItems().addAll("Вручную", "Генератор начального числа", "Значение по умолчанию");
        initNumComboBox.getSelectionModel().select("Вручную");
        SpinnerValueFactory<Integer> capacityNumValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 31, 31);
        capacityNumSpinner.setValueFactory(capacityNumValue);
        SpinnerValueFactory<Integer> sizeSampleValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE, 131072, 2);
        sizeSampleSpinner.setValueFactory(sizeSampleValue);
        SpinnerValueFactory<Integer> numberSamplesValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4096, 100);
        numberSamplesSpinner.setValueFactory(numberSamplesValue);
        SpinnerValueFactory<Integer> initNumberValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 19660809);
        initNumberSpinner.setValueFactory(initNumberValue);
        SpinnerValueFactory<Double> aSpinnerValue = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.001, 0.05, 0.01, 0.01);
        aSpinner.setValueFactory(aSpinnerValue);
        genLabel.setVisible(true);
        genNumComboBox.setVisible(true);
        initNumLabel.setVisible(true);
        initNumComboBox.setVisible(true);
        capacityLabel.setVisible(true);
        capacityNumSpinner.setVisible(true);
        sizeLabel.setVisible(true);
        sizeSampleSpinner.setVisible(true);
        numberSamplesLabel.setVisible(true);
        numberSamplesSpinner.setVisible(true);
        initNumberLabel.setVisible(true);
        initNumberSpinner.setVisible(true);
        fileTextField.setVisible(false);
        chooseButton.setVisible(false);
        testButton.setDisable(true);
    }

    public void chooseButtonHandler(ActionEvent actionEvent) {
        fileTextField.clear();
        Node source = (Node) actionEvent.getSource();
        Stage primaryStage = (Stage) source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        fileChooser.setTitle("Выберите файл с выборками");
        fileObject = fileChooser.showOpenDialog(primaryStage);
        try {
            fileTextField.setText(fileObject.getPath());
            Scanner sc = new Scanner(fileObject);
            k = 0;
            if (sc.hasNextInt()) {
                while (sc.hasNextInt()) {
                    k++;
                    sc.nextInt();
                }
            } else if (sc.hasNextDouble()) {
                while (sc.hasNextDouble()) {
                    k++;
                    sc.nextDouble();
                }
            }
            SpinnerValueFactory<Integer> sizeSampleValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(20, k, k, 2);
            sizeSampleSpinner.setValueFactory(sizeSampleValue);
            numberSamplesSpinner.getValueFactory().setValue(1);
            loadCheckBox.setDisable(false);
            mainButton.setDisable(false);
            numberSamplesSpinner.setDisable(false);
            sizeSampleSpinner.setDisable(false);
            testButton.setDisable(true);
            sc.close();
        } catch (Exception e) {
            loadCheckBox.setSelected(false);
            loadCheckBox.setDisable(true);
            capacityNumSpinner.setVisible(false);
            capacityLabel.setVisible(false);
            mainButton.setDisable(true);
            numberSamplesSpinner.setDisable(true);
            sizeSampleSpinner.setDisable(true);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Ошибка выбора файла!");
            alert.setContentText("Выберите файл");
            alert.showAndWait();
        }
    }

    public void loadComboBoxHandler(ActionEvent actionEvent) {
        if (loadComboBox.getSelectionModel().getSelectedItem().equals("Программный")) {
            numberSamplesSpinner.valueProperty().removeListener(changeL);
            loadCheckBox.selectedProperty().removeListener(checkL);
            SpinnerValueFactory<Integer> sizeSampleValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE, 131072, 2);
            sizeSampleSpinner.setValueFactory(sizeSampleValue);
            SpinnerValueFactory<Integer> numberSamplesValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4096, 100);
            numberSamplesSpinner.setValueFactory(numberSamplesValue);
            loadCheckBox.setVisible(true);
            loadCheckBox.setDisable(false);
            loadCheckBox.setSelected(false);
            loadCheckBox.setText("Загрузить СЧ в файл \"testSample.txt\" на рабочем столе");
            genLabel.setVisible(true);
            genNumComboBox.setVisible(true);
            initNumLabel.setVisible(true);
            initNumComboBox.setVisible(true);
            capacityLabel.setVisible(true);
            capacityNumSpinner.setVisible(true);
            sizeLabel.setVisible(true);
            sizeSampleSpinner.setVisible(true);
            numberSamplesLabel.setVisible(true);
            numberSamplesSpinner.setVisible(true);
            mainButton.setDisable(false);
            aLabel.setDisable(true);
            aSpinner.setDisable(true);
            if (initNumComboBox.getSelectionModel().getSelectedItem().equals("Вручную")) {
                initNumberLabel.setVisible(true);
                initNumberSpinner.setVisible(true);
            } else {
                initNumberLabel.setVisible(false);
                initNumberSpinner.setVisible(false);
            }
            infoLabel.setText("Сгенерированные случайные числа:");
            capacityLabel.setText("Разрядность генерируемых чисел:");
            numberSamplesSpinner.setDisable(false);
            sizeSampleSpinner.setDisable(false);
            testButton.setDisable(true);
            fileTextField.setVisible(false);
            chooseButton.setVisible(false);
            randListView.getItems().clear();
        } else if (loadComboBox.getSelectionModel().getSelectedItem().equals("Файловый")) {
            checkL = (observable, oldValue, newValue) -> {
                if (newValue) {
                    capacityLabel.setVisible(true);
                    capacityNumSpinner.setVisible(true);
                } else {
                    capacityLabel.setVisible(false);
                    capacityNumSpinner.setVisible(false);
                }
            };
            loadCheckBox.selectedProperty().addListener(checkL);
            loadCheckBox.setText("Загружаются вещественные числа от 0 до 1");
            loadCheckBox.setDisable(true);
            loadCheckBox.setSelected(false);
            numberSamplesSpinner.getValueFactory().setValue(1);
            infoLabel.setText("Загруженные случайные числа:");
            capacityLabel.setText("Разрядность загружаемых чисел:");
            genLabel.setVisible(false);
            genNumComboBox.setVisible(false);
            initNumLabel.setVisible(false);
            initNumComboBox.setVisible(false);
            capacityLabel.setVisible(false);
            capacityNumSpinner.setVisible(false);
            initNumberLabel.setVisible(false);
            initNumberSpinner.setVisible(false);
            mainButton.setDisable(true);
            sizeLabel.setVisible(true);
            sizeSampleSpinner.setVisible(true);
            numberSamplesLabel.setVisible(true);
            numberSamplesSpinner.setVisible(true);
            fileTextField.setVisible(true);
            chooseButton.setVisible(true);
            testButton.setDisable(true);
            numberSamplesSpinner.setDisable(true);
            sizeSampleSpinner.setDisable(true);
            aLabel.setDisable(true);
            aSpinner.setDisable(true);
            randListView.getItems().clear();
            changeL = (obs, oldValue, newValue) -> {
                int res;
                if ((res = newValue - 1) > 0) {
                    SpinnerValueFactory<Integer> sizeSampleValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(20, k / (res + 1), k / (res + 1), 2);
                    sizeSampleSpinner.setValueFactory(sizeSampleValue);
                } else if ((res = newValue - 1) <= 0) {
                    res = Math.abs(res);
                    SpinnerValueFactory<Integer> sizeSampleValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(20, k * (res + 1), k * (res + 1), 2);
                    sizeSampleSpinner.setValueFactory(sizeSampleValue);
                }
            };
            numberSamplesSpinner.valueProperty().addListener(changeL);
        }
    }

    public void initNumComboBoxHandler(ActionEvent actionEvent) {
        if (initNumComboBox.getSelectionModel().getSelectedItem().equals("Вручную")) {
            initNumberLabel.setVisible(true);
            initNumberSpinner.setVisible(true);
        } else {
            initNumberLabel.setVisible(false);
            initNumberSpinner.setVisible(false);
        }
    }

    public void saveFileButtonHandler(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage primaryStage = (Stage) source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
        fileChooser.setInitialFileName("resultTests");
        fileChooser.setTitle("Выберите куда сохранить результаты");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        File fileSave = fileChooser.showSaveDialog(primaryStage);
        try (PrintWriter pw = new PrintWriter(fileSave)) {
            pw.println(resLabel.getText());
            pw.println(resProcLabel.getText());
            pw.println();
            pw.println(resTextArea.getText());
            pw.flush();
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка!");
            alert.setHeaderText("Ошибка сохранения файла!");
            alert.setContentText("Попробуйте заново");
            alert.showAndWait();
        }
    }

    public void mainButtonHandler(ActionEvent actionEvent) throws IOException {
        int size = 5000000;
        randListView.getItems().clear();
        ObservableList<Integer> numsRand;
        if (loadComboBox.getSelectionModel().getSelectedItem().equals("Программный")) {
            if ((capacityNumSpinner.getValue() != null) && (sizeSampleSpinner.getValue() != null) &&
                    (numberSamplesSpinner.getValue() != null)) {
                testButton.setDisable(false);
                PrintWriter pw = null;
                if (loadCheckBox.isSelected()) {
                    pw = new PrintWriter(new FileWriter(System.getProperty("user.home") + "/Desktop/testSample.txt"));
                }
                numsRand = FXCollections.observableArrayList();
                numberSample = new NumberSample(numberSamplesSpinner.getValue(), sizeSampleSpinner.getValue());
                numberSample.setCapacity(capacityNumSpinner.getValue());
                switch (genNumComboBox.getSelectionModel().getSelectedItem()) {
                    case "Алгоритм_ГОСТ_ИСО_24153": {
                        AlgGostPISO24153 algGostPISO24153 = new AlgGostPISO24153();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    algGostPISO24153.setSeed(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    algGostPISO24153.setSeed(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                algGostPISO24153.setSeed(19660809);
                                break;
                        }
                        algGostPISO24153.setSeed2(algGostPISO24153.getSeed());//rng параметры функции начального числа
                        algGostPISO24153.setIJ(-1);//rng парамметры функциии инициализации
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, (int) Math.floor(algGostPISO24153.U() *
                                        (int) Math.pow(2, numberSample.getCapacity())));//масштабированный выход(1:nn)
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "GFSR_генератор_с_3_параметрами": {
                        GfsrGeneratorPar3 gfsrGeneratorPar3 = new GfsrGeneratorPar3();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    gfsrGeneratorPar3.initGfsr(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    gfsrGeneratorPar3.initGfsr(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                gfsrGeneratorPar3.initGfsr(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, gfsrGeneratorPar3.gfsr31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "GFSR_генератор_с_5_параметрами": {
                        GfsrGeneratorPar5 gfsrGeneratorPar5 = new GfsrGeneratorPar5();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    gfsrGeneratorPar5.initGfsr5(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    gfsrGeneratorPar5.initGfsr5(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                gfsrGeneratorPar5.initGfsr5(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, gfsrGeneratorPar5.gfsr531(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Генератор_Таусворта": {
                        TausworthGenerator tausworthGenerator = new TausworthGenerator();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    tausworthGenerator.taus88(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    tausworthGenerator.taus88(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                tausworthGenerator.taus88(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, tausworthGenerator.tausCapacity(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Генератор_Твистера": {
                        TwisterGenerator twisterGenerator = new TwisterGenerator();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    twisterGenerator.initGenrand(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    twisterGenerator.initGenrand(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                twisterGenerator.initGenrand(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, twisterGenerator.genrand31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                    infoLabel.setText("Сгенерированные случайные числа:");
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "LCG_генератор_V1": {
                        LCGeneratorV1 lcGeneratorV1 = new LCGeneratorV1();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    lcGeneratorV1.initLcong32(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    lcGeneratorV1.initLcong32(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                lcGeneratorV1.initLcong32(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, lcGeneratorV1.lcong32_31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "LCG_генератор_V2": {
                        LCGeneratorV2 lcGeneratorV2 = new LCGeneratorV2();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    lcGeneratorV2.initLcong31(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    lcGeneratorV2.initLcong31(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                lcGeneratorV2.initLcong31(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, lcGeneratorV2.lcong31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Мультипликативный_генератор_V1": {
                        MultiplicativeGeneratorV1 multiplicativeGeneratorV1 = new MultiplicativeGeneratorV1();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    multiplicativeGeneratorV1.initMul(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    multiplicativeGeneratorV1.initMul(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                multiplicativeGeneratorV1.initMul(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, multiplicativeGeneratorV1.Mul32_31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Мультипликативный_генератор_V2": {
                        MultiplicativeGeneratorV2 multiplicativeGeneratorV2 = new MultiplicativeGeneratorV2();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    multiplicativeGeneratorV2.initMul(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    multiplicativeGeneratorV2.initMul(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                multiplicativeGeneratorV2.initMul(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, multiplicativeGeneratorV2.Mul32_31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Мультипликативный_генератор_V3": {
                        MultiplicativeGeneratorV3 multiplicativeGeneratorV3 = new MultiplicativeGeneratorV3();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    multiplicativeGeneratorV3.initMul(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    multiplicativeGeneratorV3.initMul(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                multiplicativeGeneratorV3.initMul(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, multiplicativeGeneratorV3.Mul32_31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Мультипликативный_генератор_V4": {
                        MultiplicativeGeneratorV4 multiplicativeGeneratorV4 = new MultiplicativeGeneratorV4();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    multiplicativeGeneratorV4.initMul(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    multiplicativeGeneratorV4.initMul(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                multiplicativeGeneratorV4.initMul(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, multiplicativeGeneratorV4.Mul32_31(numberSample.getCapacity()));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "Random_генератор": {
                        Random r = new Random();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    r.setSeed(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    r.setSeed(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                r.setSeed(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, r.nextInt((int) Math.pow(2, numberSample.getCapacity())));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "SecureRandom_генератор": {
                        SecureRandom sr = new SecureRandom();
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    sr.setSeed(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    sr.setSeed(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                sr.setSeed(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, sr.nextInt((int) Math.pow(2, numberSample.getCapacity())));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                    case "SplittableRandom_генератор": {
                        SplittableRandom splittableRandom = null;
                        switch (initNumComboBox.getSelectionModel().getSelectedItem()) {
                            case "Вручную":
                                if (initNumberSpinner.getValue() != null) {
                                    splittableRandom = new SplittableRandom(initNumberSpinner.getValue());
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка заполнения полей!");
                                    alert.setContentText("Заполните все поля на странице ввода");
                                    alert.showAndWait();
                                }
                                break;
                            case "Генератор начального числа":
                                try {
                                    splittableRandom = new SplittableRandom(SeedGenerator.SeedGen());
                                } catch (ParseException e) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка!");
                                    alert.setHeaderText("Ошибка генерации начального числа!");
                                    alert.showAndWait();
                                }
                                break;
                            case "Значение по умолчанию":
                                splittableRandom = new SplittableRandom(19660809);
                                break;
                        }
                        for (int i = 0; i < numberSample.getCountSample(); i++) {
                            for (int j = 0; j < numberSample.getNSample(); j++) {
                                numberSample.setItemSample(i, j, splittableRandom != null ? splittableRandom.nextInt((int) Math.pow(2, numberSample.getCapacity())) : 0);
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                                if (loadCheckBox.isSelected()) {
                                    assert pw != null;
                                    pw.println(numberSample.getItemSample(i, j));
                                }
                            }
                        }
                        if (numberSample.getNSampleMas() <= size) {
                            infoLabel.setText("Сгенерированные случайные числа:");
                            randListView.setItems(numsRand);
                        } else {
                            infoLabel.setText("Выборка огромная, загружена только в массив");
                        }
                    }
                }
                if (loadCheckBox.isSelected()) {
                    assert pw != null;
                    pw.flush();
                    pw.close();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setHeaderText("Ошибка заполнения полей!");
                alert.setContentText("Заполните все поля на странице ввода");
                alert.showAndWait();
            }
        } else if (loadComboBox.getSelectionModel().getSelectedItem().equals("Файловый")) {
            numsRand = FXCollections.observableArrayList();
            numberSample = new NumberSample(numberSamplesSpinner.getValue(), sizeSampleSpinner.getValue());
            Scanner sc1;
            try {
                sc1 = new Scanner(fileObject);
                if (!loadCheckBox.isSelected() && sc1.hasNextInt()) {
                    for (int i = 0; i < numberSample.getCountSample(); i++) {
                        for (int j = 0; j < numberSample.getNSample(); j++) {
                            if (sc1.hasNextInt()) {
                                numberSample.setItemSample(i, j, sc1.nextInt());
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                            } else {
                                sc1.next();
                            }
                        }
                    }
                } else if (loadCheckBox.isSelected() || sc1.hasNextDouble()) {
                    int max = (int) Math.pow(2, capacityNumSpinner.getValue()) - 1;
                    for (int i = 0; i < numberSample.getCountSample(); i++) {
                        for (int j = 0; j < numberSample.getNSample(); j++) {
                            if (sc1.hasNextDouble()) {
                                numberSample.setItemSample(i, j, (int) (sc1.nextDouble() * max));
                                if (numberSample.getNSampleMas() <= size) {
                                    numsRand.add(numberSample.getItemSample(i, j));
                                }
                            } else {
                                sc1.next();
                            }
                        }
                    }
                }
                if (numberSample.getNSampleMas() <= size) {
                    infoLabel.setText("Сгенерированные случайные числа:");
                    randListView.setItems(numsRand);
                } else {
                    infoLabel.setText("Выборка огромная, загружена только в массив");
                }
                numberSample.setCapacity((int) Math.ceil(Math.log10(Arrays.stream(numberSample.matrToMas()).parallel().max().getAsInt()) / Math.log10(2)));
                testButton.setDisable(false);
                sc1.close();
            } catch (FileNotFoundException e) {
                testButton.setDisable(true);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка!");
                alert.setHeaderText("Ошибка чтения файла!");
                alert.setContentText("Файл не найден");
                alert.showAndWait();
            }
        }
        aLabel.setDisable(false);
        aSpinner.setDisable(false);
    }

    public void testButtonHandler(ActionEvent actionEvent) {
        diagramTab.setDisable(false);
        diagramBTab.setDisable(false);
        diagramMonotonyTab.setDisable(false);
        diagramAutocorrelationTab.setDisable(false);
        diagramSeriesTab.setDisable(false);
        graphicTab.setDisable(false);
        graphicDopTab.setDisable(false);
        resTab.setDisable(false);
        diagramBarChart.getData().clear();
        diagramBBarChart.getData().clear();
        series01BarChart.getData().clear();
        series01BarChart1.getData().clear();
        series01BarChart2.getData().clear();
        resTextArea.clear();

        ProgressForm pForm = new ProgressForm();

        ParamsTest paramsTest = new ParamsTest();
        paramsTest.setA(aSpinner.getValue());

        TestsSample testsSample = new TestsSample(numberSample, paramsTest);

        testsSample.initParams();
        List<Test> statisticTests = new ArrayList<>();
        statisticTests.add(new UniformDistributionChiSquareTest(numberSample, paramsTest));
        statisticTests.add(new MeanTest(numberSample, paramsTest, testsSample.getSummaryStatistics()));
        statisticTests.add(new CumulativeSumsTest(numberSample, paramsTest));
        statisticTests.add(new RunTest(numberSample, paramsTest));
        statisticTests.add(new FrequencyMonobitTest(numberSample, paramsTest));
        statisticTests.add(new FrequencyLongSequencesTest(numberSample, paramsTest));
        statisticTests.add(new SerialTest(numberSample, paramsTest));
        statisticTests.add(new SpectralTest(numberSample, paramsTest));
        statisticTests.add(new ApproximateEntropyTest(numberSample, paramsTest, 3));
        statisticTests.add(new LinearComplexityTest(numberSample, paramsTest, 500));
        statisticTests.add(new RankTest(numberSample, paramsTest, 32));
        statisticTests.add(new FrequencyBlockTest(numberSample, paramsTest, 20));
        statisticTests.add(new RandomExcursionsTest(numberSample, paramsTest));
        statisticTests.add(new MaurerUniversalStatisticalTest(numberSample, paramsTest));
        statisticTests.add(new LongestRunOnesInBlockTest(numberSample, paramsTest));
        statisticTests.add(new NonOverlappingTemplateMatchingTest(numberSample, paramsTest, 6, 8));
        statisticTests.add(new StackBooksTest(numberSample, paramsTest, 4));
        statisticTests.add(new PokerTest(numberSample, paramsTest));
        statisticTests.add(new KolmogorovSmirnovTest(numberSample, paramsTest));

        List<GraphicTest> graphicTests = new ArrayList<>();
        HistogramDistributionSequenceTest histogramDistributionSequenceTest
                = new HistogramDistributionSequenceTest(numberSample);
        graphicTests.add(histogramDistributionSequenceTest);
        HistogramDistributionByteSequenceTest histogramDistributionByteSequenceTest =
                new HistogramDistributionByteSequenceTest(numberSample);
        SeriesGraphicTest seriesGraphicTest = new SeriesGraphicTest(numberSample);
        MonotonyTest monotonyTest = new MonotonyTest(numberSample);
        AutocorrelationTest autocorrelationTest = new AutocorrelationTest(numberSample);

        graphicTests.add(histogramDistributionByteSequenceTest);
        graphicTests.add(new DistributionOnPlaneTest(numberSample, graphTestCanvas, graphDopTestCanvas));
        graphicTests.add(seriesGraphicTest);
        graphicTests.add(monotonyTest);
        graphicTests.add(autocorrelationTest);

        testsSample.runStatisticTest(statisticTests);
        testsSample.runGraphicTest(graphicTests);

        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<>();
        for (int i = 0; i < histogramDistributionSequenceTest.getHeights().length; i++) {
            dataSeries1.getData().add(new XYChart.Data<>(String.valueOf(i + 1), histogramDistributionSequenceTest.getParamHeights(i)));
        }
        dataSeries1.setName("Частоты");
        diagramBarChart.getData().add(dataSeries1);

        XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<>();
        histogramDistributionByteSequenceTest.getHeights().forEach((key, value) -> {
            if (key % 5 == 0) dataSeries2.getData().add(new XYChart.Data<>(String.valueOf(key), value));
        });
        dataSeries2.setName("Количество чисел");
        diagramBBarChart.getData().add(dataSeries2);

        //01
        XYChart.Series<String, Number> dataSeries3 = new XYChart.Series<>();
        seriesGraphicTest.getMapV1().forEach((key, value) ->
                dataSeries3.getData().add(new XYChart.Data<>(String.valueOf(key), value)));
        dataSeries3.setName("Количество серий");
        series01BarChart.getData().add(dataSeries3);

        XYChart.Series<String, Number> dataSeries4 = new XYChart.Series<>();
        seriesGraphicTest.getMapV2().forEach((key, value) ->
                dataSeries4.getData().add(new XYChart.Data<>(String.valueOf(key), value)));
        dataSeries4.setName("Количество серий");
        series01BarChart1.getData().add(dataSeries4);

        XYChart.Series<String, Number> dataSeries5 = new XYChart.Series<>();
        seriesGraphicTest.getMapV3().forEach((key, value) ->
                dataSeries5.getData().add(new XYChart.Data<>(String.valueOf(key), value)));
        dataSeries5.setName("Количество серий");
        series01BarChart2.getData().add(dataSeries5);

        XYChart.Series<String, Number> dataSeries6 = new XYChart.Series<>();
        int t = 100;
        monotonyTest.getMonotonySeries().entrySet().stream().limit(t).forEach(entry -> {
            dataSeries6.getData().add(new XYChart.Data<>(String.valueOf(entry.getKey()), entry.getValue()));
        });
        diagramMonotonyBarChart.getData().add(dataSeries6);

        AtomicInteger index = new AtomicInteger();
        diagramMonotonyBarChart.lookupAll(".default-color0.chart-bar")
                .forEach(node -> {
                    Color color = index.get() % 2 == 0 ? Color.RED : Color.BLUE;
                    node.setStyle("-fx-bar-fill: " + toHex(color) + ", black;");
                    index.getAndIncrement();
                });

        autocorrelationYAxis.setAutoRanging(false);
        autocorrelationYAxis.setLowerBound(-0.5);
        autocorrelationYAxis.setUpperBound(0.5);
        XYChart.Series<String, Number> dataSeries7 = new XYChart.Series<>();
        autocorrelationTest.getAutocorrelationSeries().forEach((key, value) -> dataSeries7.getData().add(new XYChart.Data<>(String.valueOf(key), value)));
        diagramAutocorrelationBarChart.getData().add(dataSeries7);
        AtomicInteger autocorrelationI = new AtomicInteger(0);
        diagramAutocorrelationBarChart.lookupAll(".default-color0.chart-bar")
                .forEach(node -> {
                    double y = dataSeries7.getData().get(autocorrelationI.get()).getYValue().doubleValue();
                    if (y < 0) {
                        node.getStyleClass().add("negative");
                    }
                    autocorrelationI.getAndIncrement();
                });

        AtomicInteger k = new AtomicInteger(1);
        statisticTests.stream().map(x -> x.result(k.getAndIncrement())).forEach(x -> {
            resTextArea.appendText("-----------------------------------------------------------------------------------------------------------------------\n");
            resTextArea.appendText(x.toString());
        });
        writeText(testsSample);
        pForm.getDialogStage().setTitle("ТЕСТИРОВАНИЕ ЗАКОНЧЕНО!");
    }

    // Метод для преобразования цвета в HEX-код
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public void writeText(TestsSample testsSample) {
        if (90 <= testsSample.getResTests() && testsSample.getResTests() <= 100) {
            resLabel.setText("Последовательность с большой вероятностью случайна");
        } else {
            resLabel.setText("Последовательность не случайна");
        }
        resProcLabel.setText("Процент пройденных тестов: " + testsSample.getResTests() + "%");
    }
}

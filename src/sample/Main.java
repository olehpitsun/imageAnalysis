package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.opencv.core.Core;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;


import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import sample.model.ObjectDetect.ObjectsD;
import sample.model.ObjectDetect.ObjectsDListWrapper;
import sample.model.RegionDetectByColor;
import sample.model.RegionDetectByColorListWrapper;
import sample.view.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.opencv.core.Core;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<RegionDetectByColor> rdbcData = FXCollections.observableArrayList();
    private ObservableList<ObjectsD> objParamData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public Main() {

    }

    public ObservableList<RegionDetectByColor> getArea() {
        return rdbcData;
    }
    public ObservableList<ObjectsD> getParamData() {
        return objParamData;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Аналіз біо зображень");

        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));

        initRootLayout();
        startProcessing();
        //showPreprocessing();
    }

    /**
     * Initializes the root layout and tries to load the last opened
     * person file.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show start page
     */
    public void startProcessing(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Start.fxml"));
            AnchorPane PreProcessing = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(PreProcessing);

            // Give the controller access to the main app.
            StartController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows function to image preprocessing
     */
    public void showPreprocessing() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/PreProcessing.fxml"));
            AnchorPane PreProcessing = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(PreProcessing);

            // Give the controller access to the main app.
            PreProcessingController controller = loader.getController();
            controller.setMainApp(this);
            //controller.setPerson(rdbcData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show image filter funtions
     */
    public void showFilters() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Filters.fxml"));
            AnchorPane PreProcessing = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(PreProcessing);

            // Give the controller access to the main app.
            FiltersController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showSegmentation(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/Segmentation.fxml"));
            AnchorPane PreProcessing = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(PreProcessing);

            // Give the controller access to the main app.
            SegmentationController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showRegionDetect(){
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("view/RegionDetect.fxml"));
            AnchorPane PreProcessing = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            rootLayout.setCenter(PreProcessing);

            // Give the controller access to the main app.
            RegionDetectController controller = loader.getController();
            controller.setMainApp(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setPersonFilePath(File file) {

        rdbcData.add(new RegionDetectByColor("Hans"));
        rdbcData.add(new RegionDetectByColor("Ruth"));
        rdbcData.add(new RegionDetectByColor("Heinz"));

        rdbcData.forEach(s -> {
            System.out.println("s = " + s);
        });
        Preferences prefs = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Update the stage title.
            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Update the stage title.
            primaryStage.setTitle("AddressApp");
        }
    }

    /**
     * Saves the current data to the specified file.
     *
     * @param file
     */
    public void savePersonDataToFile(File file, ObservableList<RegionDetectByColor> rdbcDataAreaList) {
        try {

            JAXBContext context = JAXBContext
                    .newInstance(RegionDetectByColorListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            RegionDetectByColorListWrapper wrapper = new RegionDetectByColorListWrapper();
            wrapper.setAreaList(rdbcDataAreaList);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            System.out.print(e.toString());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public void saveObjParamDataToFile(File file, ObservableList<ObjectsD> objDataAreaList) {
        try {

            JAXBContext context = JAXBContext
                    .newInstance(ObjectsDListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            ObjectsDListWrapper wrapper = new ObjectsDListWrapper();
            wrapper.setObjectsD(objDataAreaList);

            // Marshalling and saving XML to the file.
            m.marshal(wrapper, file);

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) { // catches ANY exception
            System.out.print(e.toString());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }
}
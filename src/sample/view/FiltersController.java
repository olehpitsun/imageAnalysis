package sample.view;

import sample.Main;
import sample.model.Filters.Filters;
import sample.model.Person;
import sample.model.Filters.FilterColection;
import sample.util.DateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class FiltersController {

    @FXML
    private TextField kSizeField;
    @FXML
    private TextField sigma;
    @FXML
    private TextField sigmaSpace;
    @FXML
    private TextField sigmaColor;

    @FXML
    private Button saveChangeButton;

    private Stage dialogStage;

    private boolean okClicked = false;

    @FXML
    protected ImageView originalImage;

    private Stage stage;
    // the JavaFX file chooser

    private FileChooser fileChooser;
    // support variables
    protected Mat image;

    private String filterType;

    protected List<Mat> planes;
    @FXML
    private ComboBox<FilterColection> comboBox;
    // Reference to the main application.

    protected Mat changedimage;

    protected Main mainApp;

    private ObservableList<FilterColection> comboBoxData = FXCollections.observableArrayList();
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public FiltersController() {
        comboBoxData.add(new FilterColection("1", "Гаусівський"));
        comboBoxData.add(new FilterColection("2", "Білатеральний"));
        comboBoxData.add(new FilterColection("3", "Адаnтивний біл..."));
        comboBoxData.add(new FilterColection("4", "Медіанний"));

    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        comboBox.setItems(comboBoxData);
        this.fileChooser = new FileChooser();
        this.changedimage = new Mat();
        this.image = new Mat();
        this.planes = new ArrayList<>();
        this.mainFilters();

    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleComboBoxAction() {
        FilterColection selectedFilter = comboBox.getSelectionModel().getSelectedItem();
        this.selectFilter(selectedFilter.getId());
    }

    private void selectFilter(String type){
        if(type == "1"){

            this.filterType = "1";
            this.kSizeField.setDisable(false);
            this.sigma.setDisable(false);

            this.sigmaColor.setDisable(true);
            this.sigmaSpace.setDisable(true);
        }else if(type == "2"){

            this.filterType = "2";
            this.sigma.setDisable(true);
            this.kSizeField.setDisable(false);
            this.sigmaColor.setDisable(false);
            this.sigmaSpace.setDisable(false);

        }else if(type == "3"){

            this.filterType = "3";
            this.kSizeField.setDisable(false);
            this.sigma.setDisable(true);
            this.sigmaColor.setDisable(true);
            this.sigmaSpace.setDisable(false);

        }else if(type == "4"){

            this.filterType = "4";
            this.kSizeField.setDisable(false);
            this.sigma.setDisable(true);
            this.sigmaColor.setDisable(true);
            this.sigmaSpace.setDisable(true);

        }
    }

    @FXML
    private void gaussianFilter(int kSize, double sigma ){

        Mat im = new Mat();
        im = this.optimizeImageDim(this.image);
        Mat dst = Filters.gaussianBlur(im, kSize, sigma );
        this.changedimage = dst;

        this.setCurrentImage(dst);
    }

    @FXML
    private void bilateralFilter(int kSize, double sigmaColor, double sigmaSpace){

        Mat im = new Mat();
        im = this.optimizeImageDim(this.image);
        Mat dst = Filters.bilateralFilter(im, kSize, sigmaColor, sigmaSpace);
        this.changedimage = dst;

        this.setCurrentImage(dst);
    }

    @FXML
    private void adaptiveBilateral(int kSize, Double sigmaSpace ){

        Mat im = new Mat();
        im = this.optimizeImageDim(this.image);

        int sP = Integer.valueOf(sigmaSpace.intValue());
        Mat dst = Filters.adaptiveBilateralFilter(im, kSize, sP);
        this.changedimage = dst;

        this.setCurrentImage(dst);
    }

    @FXML
    private void medianBlur(int kSize){

        Mat im = new Mat();
        im = this.optimizeImageDim(this.image);
        Mat dst = Filters.medianBlur(im,kSize );
        this.changedimage = dst;

        this.setCurrentImage(dst);
    }

    @FXML
    public void mainFilters() {

            // show the image
            this.image = sample.model.Image.getImageMat();
            this.originalImage.setImage(this.mat2Image(sample.model.Image.getImageMat()));// read the image in gray scale
            this.originalImage.setFitWidth(550.0);
            this.originalImage.setFitHeight(624.0);
            this.originalImage.setPreserveRatio(true);
    }

    @FXML
    public void saveChangeFile(){
        this.image= this.changedimage ;
        sample.model.Image.setImageMat(this.image);

    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setCurrentImage(Mat dst ){

        this.originalImage.setImage(this.mat2Image(dst));
        // set a fixed width
        this.originalImage.setFitWidth(550.0);
        this.originalImage.setFitHeight(624.0);
        // preserve image ratio
        this.originalImage.setPreserveRatio(true);
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    protected Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Highgui.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    /**
     * Optimize the image dimensions
     *
     * @param image the {@link Mat} to optimize
     * @return the image whose dimensions have been optimized
     */
    protected Mat optimizeImageDim(Mat image) {
        // init
        Mat padded = new Mat();
        // get the optimal rows size for dft
        int addPixelRows = Core.getOptimalDFTSize(image.rows());
        // get the optimal cols size for dft
        int addPixelCols = Core.getOptimalDFTSize(image.cols());
        // apply the optimal cols and rows size to the image
        Imgproc.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
                Imgproc.BORDER_CONSTANT, Scalar.all(0));

        return padded;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    public void handleOk() {
        // if (isInputValid()) {
        System.out.println(kSizeField.getText());
        System.out.print(sigma.getText());


        if(this.filterType == "1") {
            this.gaussianFilter(Integer.parseInt(kSizeField.getText()), Double.parseDouble(sigma.getText()));
        }
        if(this.filterType == "2"){
            this.bilateralFilter(Integer.parseInt(kSizeField.getText()),Double.parseDouble(sigmaSpace.getText()),Double.parseDouble(sigmaColor.getText()));
        }
        if(this.filterType == "3"){
            this.adaptiveBilateral(Integer.parseInt(kSizeField.getText()), Double.parseDouble(sigmaSpace.getText()));
        }
        if(this.filterType == "4"){
            this.medianBlur(Integer.parseInt(kSizeField.getText()));
        }

        okClicked = true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


}
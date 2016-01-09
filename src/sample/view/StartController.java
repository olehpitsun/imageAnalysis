package sample.view;

import sample.Main;
import sample.model.PreProcessing.PreProcessing;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.videoio.VideoCapture;

public class StartController {

    @FXML
    private Button PrProcButton;
    @FXML
    private Button FilterButton;
    @FXML
    private Button SegmentationButton;
    @FXML
    private Button ObjectButton;

    @FXML
    private ImageView histogram;

    private boolean okClicked = false;

    @FXML
    protected ImageView originalImage;

    private Stage stage;
    private Mat logo;

    // the JavaFX file chooser
    private FileChooser fileChooser;
    // support variables
    protected Mat image;

    protected List<Mat> planes;
    // Reference to the main application.
    protected Mat changedimage;

    protected Main mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public StartController() {

    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

        this.fileChooser = new FileChooser();
        this.changedimage = new Mat();
        this.image = new Mat();
        this.planes = new ArrayList<>();


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
    public void handlePreProc(){
        mainApp.showPreprocessing();
    }

    @FXML
    public void handleFilters (){
        mainApp.showFilters();
    }

    @FXML
    public void hanfleSegmentation(){mainApp.showSegmentation();}

    @FXML
    public void handleObjectDetect(){
        mainApp.showRegionDetect();
    }

    public void chooseFile(ActionEvent actionEvent) throws java.io.IOException {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.bmp", "*.png", "*.jpg", "*.gif"));
        File file = chooser.showOpenDialog(new Stage());
        if(file != null) {
            //this.gaussianFilterButton.setDisable(false);
            this.image = Highgui.imread(file.getAbsolutePath(), Highgui.CV_LOAD_IMAGE_COLOR);

            sample.model.Image.setImageMat(this.image);
            Mat g = sample.model.Image.getImageMat();

            // show the image
            this.setCurrentImage(g);

            this.PrProcButton.setDisable(false);
            this.FilterButton.setDisable(false);
            this.SegmentationButton.setDisable(false);
            this.ObjectButton.setDisable(false);


        }
        else
        {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
        /*alert.setContentText("You didn't select a file!");*/
            alert.showAndWait();
        }
    }



    @FXML
    private void showCurrentImg(){
        this.setCurrentImage(sample.model.Image.getImageMat());
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

    public boolean isOkClicked() {
        return okClicked;
    }
}
package sample.view;

import sample.Main;
import sample.model.PreProcessing.PreProcessing;
import sample.model.Person;
import sample.util.DateUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
import java.io.File;
import java.util.*;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
//import org.opencv.videoio.VideoCapture;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PreProcessingController {

    @FXML
    private Button contrastButton;
    @FXML
    private Button ErodeButton;
    @FXML
    private Button DilateButton;
    @FXML
    private Button BackgroundRemovalButton;
    @FXML
    private Button HistogramButton;
    @FXML
    private Button saveChangeButton;
    @FXML
    private Button CurrentImgButton;

    @FXML
    private Slider slider_preproc;

    private Stage dialogStage;

    @FXML
    private CheckBox grayscale;
    // the FXML logo checkbox
    @FXML
    private CheckBox logoCheckBox;
    // the FXML grayscale checkbox
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
    public PreProcessingController() {

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
        this.mainPreProcessing();


    }

    @FXML
    public void mainPreProcessing() {

        // show the image
        this.image = sample.model.Image.getImageMat();
        this.setCurrentImage(sample.model.Image.getImageMat());
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(Main mainApp) {

        this.mainApp = mainApp;
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

            this.contrastButton.setDisable(false);
            this.ErodeButton.setDisable(false);
            this.DilateButton.setDisable(false);
            this.HistogramButton.setDisable(false);
            this.BackgroundRemovalButton.setDisable(false);


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
    public void saveChangeFile(){
        this.image= this.changedimage ;
        sample.model.Image.setImageMat(this.image);

    }

    @FXML
    private void MainContrast(){

        this.slider_preproc.setDisable(false);
        slider_preproc.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                if(newValue.intValue() == 0){
                    Contrast(1);
                }else{
                    Contrast(newValue.intValue());
                }
            }
        });

    }

    @FXML
    private void Contrast(int sz){

        Mat im = new Mat();
        im = this.optimizeImageDim(this.image);
        Mat dst = PreProcessing.contrast(im,sz);

        this.changedimage = dst;
        // show the result of the transformation as an image
        this.setCurrentImage(dst);

    }

    @FXML
    private void MainErode() {

        this.slider_preproc.setDisable(false);
        slider_preproc.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                //Contrast(newValue.intValue());
                if(newValue.intValue() == 0){
                    Erode(1);
                }else{
                    Erode(newValue.intValue());
                }
            }
        });
    }

    @FXML
    private void Erode(int kernel){

        Mat im = this.optimizeImageDim(this.image);
        Mat dst = PreProcessing.Erode(im, kernel);
        this.changedimage = dst;

        // show the result of the transformation as an image
        this.setCurrentImage(dst);

    }


    @FXML
    private void MainDilate() {

        this.slider_preproc.setDisable(false);
        slider_preproc.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                //Contrast(newValue.intValue());
                if(newValue.intValue() == 0){
                    Dilate(1);
                }else{
                    Dilate(newValue.intValue());
                }
            }
        });
    }


    @FXML
    private void Dilate(int kernel){

        Mat src = this.optimizeImageDim(this.image);

        Mat im = this.optimizeImageDim(this.image);
        Mat dst = PreProcessing.Dilate(im,kernel);
        this.changedimage = dst;

        // show the result of the transformation as an image
        this.setCurrentImage(dst);

    }


    @FXML
    private void doBackgroundRemoval()
    {
        this.slider_preproc.setDisable(true);
        // init
        Mat frame = this.image;
        Mat hsvImg = new Mat();
        List<Mat> hsvPlanes = new ArrayList<>();
        Mat thresholdImg = new Mat();

        int thresh_type = Imgproc.THRESH_BINARY_INV;
        //if (this.inverse.isSelected())
           // thresh_type = Imgproc.THRESH_BINARY;

        // threshold the image with the average hue value
        hsvImg.create(frame.size(), CvType.CV_8U);
        Imgproc.cvtColor(frame, hsvImg, Imgproc.COLOR_BGR2HSV);
        Core.split(hsvImg, hsvPlanes);

        // get the average hue value of the image
        double threshValue = this.getHistAverage(hsvImg, hsvPlanes.get(0));

        Imgproc.threshold(hsvPlanes.get(0), thresholdImg, threshValue, 179.0, thresh_type);

        Imgproc.blur(thresholdImg, thresholdImg, new Size(3, 3));

        // dilate to fill gaps, erode to smooth edges
        Imgproc.dilate(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);
        Imgproc.erode(thresholdImg, thresholdImg, new Mat(), new Point(-1, -1), 1);

        Imgproc.threshold(thresholdImg, thresholdImg, threshValue, 179.0, Imgproc.THRESH_BINARY);

        // create the new image
        Mat foreground = new Mat(frame.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
        frame.copyTo(foreground, thresholdImg);

        this.changedimage = foreground;
        this.setCurrentImage(foreground);

    }

    /**
     * Get the average hue value of the image starting from its Hue channel
     * histogram
     *
     * @param hsvImg
     *            the current frame in HSV
     * @param hueValues
     *            the Hue component of the current frame
     * @return the average Hue value
     */
    private double getHistAverage(Mat hsvImg, Mat hueValues)
    {
        // init
        double average = 0.0;
        Mat hist_hue = new Mat();
        // 0-180: range of Hue values
        MatOfInt histSize = new MatOfInt(180);
        List<Mat> hue = new ArrayList<>();
        hue.add(hueValues);

        // compute the histogram
        Imgproc.calcHist(hue, new MatOfInt(0), new Mat(), hist_hue, histSize, new MatOfFloat(0, 179));

        // get the average Hue value of the image
        // (sum(bin(h)*h))/(image-height*image-width)
        // -----------------
        // equivalent to get the hue of each pixel in the image, add them, and
        // divide for the image size (height and width)
        for (int h = 0; h < 180; h++)
        {
            // for each bin, get its value and multiply it for the corresponding
            // hue
            average += (hist_hue.get(h, 0)[0] * h);
        }

        // return the average hue of the image
        return average = average / hsvImg.size().height / hsvImg.size().width;
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

    @FXML
    public void Histogram(){

        Mat img = this.optimizeImageDim(this.image);

        Mat equ = new Mat();
        img.copyTo(equ);
        Imgproc.blur(equ, equ, new Size(3, 3));

        Imgproc.cvtColor(equ, equ, Imgproc.COLOR_BGR2YCrCb);
        List<Mat> channels = new ArrayList<Mat>();
        Core.split(equ, channels);
        Imgproc.equalizeHist(channels.get(0), channels.get(0));
        Core.merge(channels, equ);
        Imgproc.cvtColor(equ, equ, Imgproc.COLOR_YCrCb2BGR);

        Mat gray = new Mat();
        Imgproc.cvtColor(equ, gray, Imgproc.COLOR_BGR2GRAY);
        Mat grayOrig = new Mat();
        Imgproc.cvtColor(img, grayOrig, Imgproc.COLOR_BGR2GRAY);

        this.changedimage = grayOrig;

        this.setCurrentImage(grayOrig);
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

        okClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
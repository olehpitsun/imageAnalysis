package sample.view;

import sample.Main;
import sample.model.Filters.FilterColection;
import sample.model.Filters.Filters;
import sample.model.ObjectDetect.ObjectsD;
import sample.model.RegionDetectByColor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegionDetectController {

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

    @FXML
    private Button detectByColor;

    @FXML
    private Button detectRegionButton;

    @FXML
    private Button saveObjParamValueXMLfile;

    @FXML
    private Button saveXMLfile;

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

    protected Mat changedimage;

    protected Main mainApp;

    ObservableList<RegionDetectByColor> rdbcData = FXCollections.observableArrayList();
    ObservableList<ObjectsD> objectsDs = FXCollections.observableArrayList();

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public RegionDetectController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {

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
    public void detectByColor(){

        Mat src = this.optimizeImageDim(this.image);
        Mat mHsvMat = new Mat();
        Mat mMaskMat = new Mat();
        Mat mDilatedMat = new Mat();
        Mat mRgbMat = new Mat();
        Imgproc.cvtColor(src, mHsvMat, Imgproc.COLOR_RGB2HSV, 8);


        Scalar lowerThreshold = new Scalar ( 100, 150, 0 ); // Blue color – lower hsv values
        Scalar upperThreshold = new Scalar ( 179, 255, 255 ); // Blue color – higher hsv values
        Core.inRange(mHsvMat, lowerThreshold, upperThreshold, mMaskMat);
        Imgproc.dilate(mMaskMat, mDilatedMat, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(mDilatedMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Rect rect ;
        for ( int contourIdx=0; contourIdx < contours.size(); contourIdx++ )
        {
            //if(contours.get(contourIdx).size()>100)  // Minimum size allowed for consideration
            // {
            rect = Imgproc.boundingRect(contours.get(contourIdx));

            //System.out.println(rect.height * rect.width);

            String areValueTemp = Double.toString (rect.height * rect.width);
            rdbcData.add(new RegionDetectByColor(areValueTemp));

            Imgproc.drawContours ( src, contours, contourIdx, new Scalar(0,0,255), 2);

            this.saveXMLfile.setDisable(false);
            this.saveObjParamValueXMLfile.setDisable(true);

            //Core.rectangle(src, new Point(rect.x, rect.height), new Point(rect.y, rect.width), new Scalar(0, 0, 255));

        }

        this.changedimage = src;
        this.setCurrentImage(src);
    }

    /**
     * select path to save xml file
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        mainApp.savePersonDataToFile(file, rdbcData);

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
        }
    }


    /**
     * select path to save xml file
     */
    @FXML
    private void handleObjectsParamValueSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
        mainApp.saveObjParamDataToFile(file, objectsDs);

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
        }
    }

    @FXML
    public void SimpleDetect(){

        Mat src = this.optimizeImageDim(this.image);
        Mat src_gray = new Mat();
        int thresh = 100;
        int max_thresh = 255;

        Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(src_gray, src_gray, new Size(3, 3));

        Mat canny_output = new Mat();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.Canny(src_gray, canny_output, thresh, thresh * 2, 3, false);
        Imgproc.findContours(canny_output, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Moments> mu = new ArrayList<Moments>(contours.size());
        for( int i = 0; i < contours.size(); i++ )
        {
            mu.add(i, Imgproc.moments(contours.get(i), false));
        }

        List<Point> mc = new ArrayList<Point>(contours.size());

        for( int i = 0; i < contours.size(); i++ )
        {
            mc.add(i, new Point(mu.get(i).get_m10() / mu.get(i).get_m00(), mu.get(i).get_m01() / mu.get(i).get_m00()));
        }

        Mat drawing = Mat.zeros( canny_output.size(), CvType.CV_8UC3 );
        for( int i = 0; i< contours.size(); i++ )
        {
            Imgproc.drawContours(drawing, contours, i, new Scalar(0, 0, 255), 2, 1, hierarchy, 0, new Point());
            Core.circle(drawing, mc.get(i), 4, new Scalar(0, 0, 255), -1, 8, 0);
        }

        for( int i = 0; i< contours.size(); i++ )
        {
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
            System.out.println(" Контур: " + i + " Площа: " + Imgproc.contourArea(contours.get(i)) + " Довжина: " + Imgproc.arcLength(contour2f, true) + "\n");
            //System.out.print(" Контур: " + i + " Площа: " + Imgproc.contourArea(contours.get(i)) + " Довжина: " + Imgproc.arcLength(contour2f, true) + "\n");

            objectsDs.add(new ObjectsD(i,Imgproc.contourArea(contours.get(i)), Imgproc.arcLength(contour2f, true) ));

            //Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
            Imgproc.drawContours(drawing, contours, i, new Scalar(0, 0, 255), 2, 1, hierarchy, 0, new Point());
            Core.circle(drawing, mc.get(i), 4, new Scalar(0, 0, 255), -1, 2, 0);
        }

        this.saveObjParamValueXMLfile.setDisable(false);
        this.saveXMLfile.setDisable(true);
        this.changedimage = drawing;
        this.setCurrentImage(drawing);

    }

    @FXML
    public void saveChangeFile(){
        this.image= this.changedimage ;
        sample.model.Image.setImageMat(this.image);

    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void mainFilters() {

        this.image = sample.model.Image.getImageMat();
        this.setCurrentImage(sample.model.Image.getImageMat());
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

        okClicked = true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


}
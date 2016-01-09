package sample.view;

import sample.Main;
//import ch.makery.address.model.FilterColection;
import sample.model.Person;
import sample.model.Segmentation.Segmentation;
import sample.model.Segmentation.SegmentationColection;
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
//import ch.makery.address.model.Image;

public class SegmentationController {
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private TextField kSizeField;
    @FXML
    private TextField sigma;
    @FXML
    private TextField sigmaSpace;
    @FXML
    private TextField sigmaColor;
    @FXML
    private TextField delta;

    @FXML
    private Button saveChangeButton;

    @FXML
    protected TextField LaplacianParametrField;

    @FXML
    private CheckBox inverse;

    private Stage dialogStage;
    //private Person person;
    private boolean okClicked = false;

    @FXML
    protected ImageView originalImage;

    private Stage stage;
    // the JavaFX file chooser

    private FileChooser fileChooser;
    // support variables
    protected Mat image;

    private String segType;
    public Mat r;
    protected List<Mat> planes;
    @FXML
    private ComboBox<SegmentationColection> comboBox;
    // Reference to the main application.

    protected Mat changedimage;

    protected Main mainApp;

    private ObservableList<SegmentationColection> comboBoxData = FXCollections.observableArrayList();
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public SegmentationController() {
        comboBoxData.add(new SegmentationColection("1", "Кенні"));
        comboBoxData.add(new SegmentationColection("2", "Собеля"));
        comboBoxData.add(new SegmentationColection("3", "Лапласіан"));
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
        this.mainSegmentation();

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
        SegmentationColection selectedSegMetod = comboBox.getSelectionModel().getSelectedItem();
        this.selectSegmentation(selectedSegMetod.getId());
    }

    private void selectSegmentation(String type){
        if(type == "1"){

            this.segType = "1";

            this.kSizeField.setDisable(false);
            this.sigma.setDisable(true);

            this.sigmaColor.setDisable(true);
            this.sigmaSpace.setDisable(true);
        }else if(type == "2"){
            this.segType = "2";

            this.sigma.setDisable(true);
            this.delta.setDisable(false);

            this.kSizeField.setDisable(true);
            this.sigmaColor.setDisable(true);
            this.sigmaSpace.setDisable(true);

        }else if(type == "3"){
            this.segType = "3";

            this.kSizeField.setDisable(false);
            this.delta.setDisable(false);

            this.sigma.setDisable(true);
            this.sigmaColor.setDisable(true);
            this.sigmaSpace.setDisable(true);
        }
    }

    @FXML
    private void cannyDetection(int size){

        Mat img = this.optimizeImageDim(this.image);

        Mat source = this.optimizeImageDim(this.image);
        Mat detectedEdges = Segmentation.cannyDetection(source,size);

        this.changedimage = detectedEdges;
        this.setCurrentImage(detectedEdges);
    }

    @FXML
    private void SobelDetection(int delta){

        Mat source = this.optimizeImageDim(this.image);
        Mat dst = Segmentation.Sobel(source, delta);

        this.changedimage = dst;
        this.setCurrentImage(dst);
    }

    @FXML
    private void Laplacian(int kSize, int delta){

        Mat source = this.optimizeImageDim(this.image);
        Mat dst = Segmentation.Laplacian(source,kSize,delta);

        this.changedimage = dst;
        this.setCurrentImage(dst);
    }

    @FXML
    public void mainSegmentation() {

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

    public void setLaplacianParam() {
        //this.person = person;

        LaplacianParametrField.setText("input");

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
        //System.out.println(kSizeField.getText());
        //System.out.print(sigma.getText());


        if(this.segType == "1") {
            this.cannyDetection(Integer.parseInt(kSizeField.getText()));
        }
        if(this.segType == "2"){
            this.SobelDetection(Integer.parseInt(delta.getText()));
        }
        if(this.segType == "3"){
            this.Laplacian(Integer.parseInt(kSizeField.getText()), Integer.parseInt(delta.getText()));
        }

        okClicked = true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
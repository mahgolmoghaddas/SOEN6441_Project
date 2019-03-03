package mapeditor.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mapeditor.MEMain;

import java.io.File;

/**
 * this class is to select old map from folder
 */
public class MapEditorEditController {

    @FXML
    private Button btn_mapSelect;

    @FXML
    private Button btn_returnToHomePage;

    @FXML
    private Label lab_MapPath;

    @FXML
    private TextField txf_defaultMapPath;

    @FXML
    private Text txt_OMEtitle;

    //BooleanProperty property = new SimpleBooleanProperty(false);

    BooleanBinding booleanBinding ;

    private static final String DEFAULT_PATH = "C:\\Users\\Jeffrey Wei\\Desktop\\pct\\SOEN6441_Project\\RiskGameProject\\maps\\World.map";

    public void initialize(){
        txf_defaultMapPath.setText(DEFAULT_PATH);
        detectDirectory();
    }

    /**
     * detect the directory in the text field is valid, if not then the button cannot be pressed
     */
    @FXML
    public void detectDirectory(){
        File file = new File(txf_defaultMapPath.getText());
        booleanBinding = Bindings.createBooleanBinding(()->{
            if(file.exists()&&txf_defaultMapPath.getText().endsWith(".map")){
                return true;
            }
            else{
                return false;
            }
        },txf_defaultMapPath.textProperty());
        btn_mapSelect.disableProperty().bind(booleanBinding.not());
    }

    /**
     * if the directory is valid then the button will jump to the edit page
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    public void selectPath(ActionEvent actionEvent) throws Exception{
        MEMain.OLDMAPPATH = txf_defaultMapPath.getText();
        Stage curStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Pane mapEditorEditPagePane = new FXMLLoader(getClass().getResource("../views/MapEditorEditPageView.fxml")).load();
        Scene mapEditorEditPageScene = new Scene(mapEditorEditPagePane,1200,900);

        curStage.setScene(mapEditorEditPageScene);
        curStage.show();
    }

    /**
     * return to homepage
     * @param actionEvent
     * @throws Exception
     */
    @FXML
    public void clickToReturn(ActionEvent actionEvent) throws Exception{
        Stage curStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

        Pane mapEditorHomePagePane = new FXMLLoader(getClass().getResource("../views/MapEditorHomePageView.fxml")).load();
        Scene mapEditorHomePageScene = new Scene(mapEditorHomePagePane,1200,900);

        curStage.setScene(mapEditorHomePageScene);
        curStage.show();
    }
}

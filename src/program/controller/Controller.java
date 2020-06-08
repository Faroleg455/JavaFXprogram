package program.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.RadialGradient;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import program.model.GridPicCollection;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class Controller {
    private String GreenColor = "focus-angle 0.0deg, focus-distance 0.0% , center 50.0% 50.0%, radius 90.0%, 0x1ad902ff 0.0%, 0x1ad902ff 25.0%, 0xffffffff 100.0%";
    private String RedColor = "focus-angle 0.0deg, focus-distance 0.0% , center 50.0% 50.0%, radius 90.0%, 0xff0303ff 0.0%, 0xff0303ff 25.0%, 0xffffffff 100.0%";
    private String BlackColor = "focus-angle 0.0deg, focus-distance 0.0% , center 50.0% 50.0%, radius 90.0%, 0x000000ff 0.0%, 0x000000ff 25.0%, 0xffffffff 100.0%";
    private String PlusPath = "plus.png";

    public Label sizeStatus;
    public ScrollPane scrollPane;
    //public MenuItem addMultiMenu;
    //public MenuItem clearMenu;
    public MenuItem sendMenu;
    public MenuItem saveMenu;
    //public Button addMultiButton;
    //public Button clearButton;
    public Button sendButton;
    public Button saveButton;

    public ImageView outImage;
    public GridPane GridPane;
    public Circle firstCircle;
    public Circle secondCircle;
    public VBox HeadBox;
    public VBox outBox;

    private GridPicCollection MyCollection = new GridPicCollection(3, 6); //Количество картинок для модели


    public void clearAll(ActionEvent actionEvent) {
        firstCircle.setFill(RadialGradient.valueOf(BlackColor));
        secondCircle.setFill(RadialGradient.valueOf(BlackColor));
        MyCollection.clear();
        update();
        sendMenu.setDisable(true);
        saveMenu.setDisable(true);
        sendButton.setDisable(true);
        saveButton.setDisable(true);
    }


    private ImageView getImageViewById(String id) {
        for (Node node : GridPane.getChildren()) {
            if (node.getId().equals(id))
                return (ImageView) node;
        }
        return null;
    }

    public void loadInImage(MouseEvent mouseEvent) {
        Object test = mouseEvent.getSource();
        setPicture((ImageView) test, mouseEvent);
    }

    private void setIndicators() {
        boolean size = false;
        boolean num = false;
        sendMenu.setDisable(true);
        sendButton.setDisable(true);
        if (MyCollection.sizeOfImages()) {
            firstCircle.setFill(RadialGradient.valueOf(GreenColor));
            size = true;
        } else
            firstCircle.setFill(RadialGradient.valueOf(RedColor));
        if (MyCollection.numOfImages() <= 14) {//Количество картинок для модели
            num = true;
            secondCircle.setFill(RadialGradient.valueOf(GreenColor));
        } else
            secondCircle.setFill(RadialGradient.valueOf(RedColor));
        if (size && num) {
            sendMenu.setDisable(false);
            sendButton.setDisable(false);
        }
        if (MyCollection.numOfImages() == 0) {
            firstCircle.setFill(RadialGradient.valueOf(BlackColor));
            secondCircle.setFill(RadialGradient.valueOf(BlackColor));
            sendMenu.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    private void update() {
        for (Node node : GridPane.getChildren()) {
            ((ImageView) node).setImage(MyCollection.findPic(node.getId()));
            if (((ImageView) node).getImage() == null) {
                node.setOnMouseClicked(null);
                node.setOnMouseEntered(null);
                node.setOnMouseExited(null);
                node.setOnKeyPressed(null);
            } else {
                node.setOnMouseClicked(this::loadInImage);
                node.setOnMouseEntered(this::getSizeStatus);
                node.setOnMouseExited(this::defaultStatus);
                node.setOnKeyPressed(this::deleteImage);
            }
        }
        if (!MyCollection.getPlusPicId().equals("Image_0_6")) { //Количество картинок для модели
            getImageViewById(MyCollection.getPlusPicId()).setOnMouseEntered(null);
            getImageViewById(MyCollection.getPlusPicId()).setOnMouseExited(null);
            getImageViewById(MyCollection.getPlusPicId()).setOnKeyPressed(null);
        }
        outImage.setImage(MyCollection.getOutPic());
    }

    private void setPicture(ImageView GridImage, MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter(
                "Image files (*.jpg/*.jpeg)", "*.jpg", "*jpeg");
        FileChooser.ExtensionFilter textFilter2 = new FileChooser.ExtensionFilter(
                "Image files (*.png)", "*.png");
        fileChooser.getExtensionFilters().addAll(textFilter, textFilter2);
        fileChooser.setTitle("Открыть файл..");
        File file = fileChooser.showOpenDialog(HeadBox.getScene().getWindow());
        if (file != null) {
            Image image = new Image("file:" + file.getPath());
            MyCollection.addPic(GridImage.getId(), image);
            if ((!MyCollection.getNextId(GridImage.getId()).equals("Image_0_6")) &&
                    (MyCollection.findPic(MyCollection.getNextId(GridImage.getId())) == null)) { //Количество картинок для модели
                MyCollection.addPlus(MyCollection.getNextId(GridImage.getId()));
            }
            update();
            setIndicators();
        }

    }

    public void addMultiPic(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(//
                //new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png"));
        fileChooser.setTitle("Открыть файлы..");
        List<File> files = fileChooser.showOpenMultipleDialog(HeadBox.getScene().getWindow());
        if (files != null) {
            if (files.size() != 0) {
                String currentPic = MyCollection.getPlusPicId();
                if (currentPic.equals("Image_0_6")) //Количество картинок для модели
                    return;
                for (File file : files) {
                    Image image = new Image("file:" + file.getPath());
                    MyCollection.addPic(currentPic, image);
                    if (currentPic.equals("Image_2_5")) { //Количество картинок для модели
                        update();
                        setIndicators();
                        return;
                    }
                    currentPic = MyCollection.getNextId(currentPic);
                }
                MyCollection.addPlus(currentPic);
                update();
                setIndicators();
            }
        }
    }

    public void saveImage(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(//
                //new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("Image files (*.jpg)", "*.jpg"),
                new FileChooser.ExtensionFilter("Image files (*.png)", "*.png"));
        fileChooser.setTitle("Сохранить файл..");
        fileChooser.setInitialFileName("OutImage");
        File file = fileChooser.showSaveDialog(HeadBox.getScene().getWindow());
        if (file != null) {
            if (file.getName().contains(".png"))
                ImageIO.write(SwingFXUtils.fromFXImage(outImage.getImage(), null), "png", file);
            else if (file.getName().contains(".jpg"))
                ImageIO.write(SwingFXUtils.fromFXImage(outImage.getImage(), null), "jpg", file);
        }
    }

    public void exitApp(ActionEvent actionEvent) {
        Stage stage = (Stage) HeadBox.getScene().getWindow();
        stage.close();
    }

    public void sendImages(ActionEvent actionEvent) {
        try {
            if (MyCollection.sendPictures()) {
                //System.out.println("Отправка");
                outImage.setImage(MyCollection.getOutPic());
                saveButton.setDisable(false);
                saveMenu.setDisable(false);
            } else {
                //System.out.println("Ошибка");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Нейронной сети не удалось обработать набор изображений");
                alert.setContentText("Попробуйте использовать другой набор изображений");
                alert.showAndWait();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getSizeStatus(MouseEvent mouseEvent) {
        Object test = mouseEvent.getSource();
        ((ImageView) test).requestFocus();
        sizeStatus.setText((int) ((ImageView) test).getImage().getWidth() + "x" +
                (int) ((ImageView) test).getImage().getHeight());
    }

    public void defaultStatus(MouseEvent mouseEvent) {
        HeadBox.requestFocus();
        sizeStatus.setText("Размер изображения");
    }


    public void deleteImage(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.DELETE) {
            Object test = keyEvent.getSource();
            MyCollection.shift(((ImageView) test).getId());
            update();
            setIndicators();
            sizeStatus.setText("Размер изображения");
        }
    }

    public void initResize() {
        //System.out.println("Тест");
        GridPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                for (Node node : GridPane.getChildren()) {
                    ((ImageView) node).setFitWidth((GridPane.getWidth() - 2 * GridPane.getHgap()) / 3);
                }
            }
        });
        outBox.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                if ((outBox.getWidth() < 270) && (outBox.getWidth() > 120))
                    outImage.setFitWidth(outBox.getWidth() - 20);
                if(outBox.getWidth()>=270)
                    outImage.setFitWidth(250);
                if (outBox.getWidth()<=120)
                    outImage.setFitWidth(100);
            }
        });
        HeadBox.setOnMouseEntered(null);
    }
}

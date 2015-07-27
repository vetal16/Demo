/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagerecognition;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author 吳家禎
 */
public class ImageRecognition extends Application {
    private ImageView openImageView, processedImageView;
    private BufferedImage sourceBufferedImage;
    private WritableImage writableImage, processedimage;
    private static int height, width;
    Label imageRevolution;
    private int[] RedFrequency, GreenFrequency, BlueFrequency;
    AreaChart<Number,Number> redAreaChart, greenAreaChart, blueAreaChart;
    PixelWriter pixelWriter;
    int maxRedFrequency;
    
    @Override
    public void start(Stage primaryStage) {
        
    //設定版面
    //設定工具列toolBar
        ToolBar toolBar = new ToolBar(); 
        toolBar.setId("toolBar");
        Button openFileBtn = new Button("讀檔");  //設定四種按鈕
        Button saveFileBtn = new Button("存檔");
        Button resetFileBtn = new Button("重設");

        //建立按鈕影像物件
        Image imageOpen = new Image(getClass().getResourceAsStream("icons/open.png"));
        Image imageSave = new Image(getClass().getResourceAsStream("icons/save.png"));
        Image imagereset = new Image(getClass().getResourceAsStream("icons/reset.png"));

        //設定按鈕嵌入影像
        openFileBtn.setGraphic(new ImageView(imageOpen));
        saveFileBtn.setGraphic(new ImageView(imageSave));
        resetFileBtn.setGraphic(new ImageView(imagereset));
        
        openFileBtn.setOnAction(btnOpenEventListener);
        //saveFileBtn.setOnAction(btnsaveEventListener);

        //Add the Buttons to the ToolBar.
        toolBar.setPadding(new Insets(10,10,10,10));
        toolBar.getItems().addAll(openFileBtn, saveFileBtn, resetFileBtn);
    //結束工具列
        
    //原始影像窗格設定    
        Label labelSourceFile = new Label("原始影像");   //建立原始影像的文字Title物件
        openImageView = new ImageView();               //載入原始影像
        openImageView.setImage(writableImage);
        openImageView.setFitHeight(400);
        openImageView.setFitWidth(400);
        ScrollPane scrollPaneBefore = new ScrollPane(); //將原始影像放置在ScrollPane容器內
        scrollPaneBefore.setContent(openImageView);
        
        VBox vboxLeftImage = new VBox();                     //將文字Title與ScrollPane放置在VBox之內
        vboxLeftImage.getChildren().addAll(labelSourceFile, scrollPaneBefore);      
        vboxLeftImage.setAlignment(Pos.TOP_CENTER);
        vboxLeftImage.setPadding(new Insets(5,5,5,5));
    //處理過的影像窗格設定    
        Label labelProcessedFile = new Label("處理後影像"); //建立處理後影像的文字Title物件
        processedImageView = new ImageView();               //載入處理後影像
        processedImageView.setImage(processedimage);
        processedImageView.setFitHeight(400);
        processedImageView.setFitWidth(400);
        
        ScrollPane scrollPaneAfter = new ScrollPane(); //將處理後影像放置在ScrollPane容器內
        scrollPaneAfter.setContent(processedImageView);

        VBox vboxRightImage = new VBox();
        vboxRightImage.getChildren().addAll(labelProcessedFile, scrollPaneAfter);
        vboxRightImage.setAlignment(Pos.TOP_CENTER);
        vboxRightImage.setPadding(new Insets(5,5,5,5));
  
    //將HBox劃分為二個區域
        HBox hbox2Pictures = new HBox();
        hbox2Pictures.getChildren().addAll(vboxLeftImage, vboxRightImage);
   
    //設定“空間域RGB影像處理”，位置在最左邊
        Label spaceRGBEditingTitle = new Label("空間域RGB影像處理");        //建立空間域RGB影像處理的文字Title物件
        
        TabPane tabpaneRGBEditing = new TabPane();          //建立及設定分頁的名稱及標籤文字
        Tab tabGrayScale = new Tab("色階");
        Tab tabNegatives = new Tab("負片");
        Tab tabContrast = new Tab("對比");
        Tab tabBrightness = new Tab("亮度");
        Tab tabSharpness = new Tab("銳利度");  
        tabpaneRGBEditing.getTabs().addAll(tabGrayScale, tabNegatives, tabContrast, tabBrightness, tabSharpness);
        tabpaneRGBEditing.setMinSize(400, 230);
        
    //設定色階影像處理
        Button btnGrayScale = new Button("灰階影像處理");
        btnGrayScale.setMinSize(120, 40);
        btnGrayScale.setOnAction(btnGrayScaleEventListener);
        //設定紅階影像處理    
        Button btnRedScale = new Button("紅階影像處理");
        btnRedScale.setMinSize(120, 40);
        btnRedScale.setOnAction(btnRedScaleEventListener);
        //設定綠階影像處理    
        Button btnGreenScale = new Button("綠階影像處理");
        btnGreenScale.setMinSize(120, 40);
        btnGreenScale.setOnAction(btnGreenScaleEventListener);
        //設定藍階影像處理    
        Button btnBlueScale = new Button("藍階影像處理");
        btnBlueScale.setMinSize(120, 40);
        btnBlueScale.setOnAction(btnBlueScaleEventListener);
        
        VBox btnVbox = new VBox();
        btnVbox.setSpacing(10);
        btnVbox.setId("btnVbox");
        btnVbox.getChildren().addAll(btnGrayScale, btnRedScale, btnGreenScale, btnBlueScale);
        btnVbox.setAlignment(Pos.CENTER);
        tabGrayScale.setContent(btnVbox);
    //結束色階影像處理    

    //設定負片影像處理
        Button btnNegative = new Button("負片影像處理");
        btnNegative.setMinSize(120, 40);
        VBox vboxNegative = new VBox();
        vboxNegative.setId("btnNegative");
        vboxNegative.getChildren().add(btnNegative);
        vboxNegative.setAlignment(Pos.CENTER);
        btnNegative.setOnAction(btnNegativeEventListener);
        tabNegatives.setContent(vboxNegative);
    //結束負片影像處理                
        VBox vboxSpaceRGBEditing = new VBox();
        vboxSpaceRGBEditing.setAlignment(Pos.TOP_CENTER);
        vboxSpaceRGBEditing.getChildren().addAll(spaceRGBEditingTitle, tabpaneRGBEditing);
        vboxSpaceRGBEditing.setPadding(new Insets(5,5,0,5));
    //結束空間域RGB影像處理
    
    //設定“頻率域影像處理”，位置在右邊
        Label frequencyEditingTitle = new Label("頻率域影像處理");        //建立頻率域影像處理的文字Title物件
        
        TabPane tabpaneFrequencyEditing = new TabPane();          
        Tab tabDCT = new Tab("離散餘弦轉換");
        Tab tabDWT = new Tab("離散小波轉換");
        tabpaneFrequencyEditing.getTabs().addAll(tabDCT, tabDWT);
        tabpaneFrequencyEditing.setMinSize(400, 230);
    
    //設定離散餘弦轉換影像處理
        Button btnDCT = new Button("離散餘弦轉換");
        btnDCT.setMinSize(120, 40);
        VBox vboxDCT = new VBox();
        vboxDCT.setId("vboxDCT");
        vboxDCT.getChildren().add(btnDCT);
        vboxDCT.setAlignment(Pos.CENTER);
        //btnDCT.setOnAction(btnNegativeEventListener);
        tabDCT.setContent(vboxDCT);
        
    //結束離散餘弦轉換影像處理    
        VBox vboxFrequencyEditing = new VBox();
        vboxFrequencyEditing.setAlignment(Pos.TOP_CENTER);
        vboxFrequencyEditing.getChildren().addAll(frequencyEditingTitle, tabpaneFrequencyEditing);
        vboxFrequencyEditing.setPadding(new Insets(5,10,0,0));
    //結束頻率域影像處理
    
        HBox hbox2ImageEditing = new HBox();
        hbox2ImageEditing.setSpacing(5);
        hbox2ImageEditing.getChildren().addAll(vboxSpaceRGBEditing, vboxFrequencyEditing);
        
        VBox vboxLeftFrame = new VBox();
        vboxLeftFrame.getChildren().addAll(hbox2Pictures , hbox2ImageEditing);
        
    //設定"影像基本資料"，位置在最右邊 
        Label imageProperty = new Label("影像基本資料");                  //建立影像基本資料的文字Title物件
        imageRevolution = new Label();
        imageRevolution.setPadding(new Insets(5,0,5,0));

    //畫直方圖      
        NumberAxis xAxisRed = new NumberAxis(0, 255, 64);
        xAxisRed.setSide(Side.BOTTOM);                                  //設定x座標軸的置放位置
        NumberAxis yAxisRed = new NumberAxis();
        yAxisRed.setSide(Side.LEFT);
        redAreaChart = new AreaChart<Number,Number>(xAxisRed,yAxisRed); //設定區域圖
        redAreaChart.setData(FXCollections.<XYChart.Series<Number, Number>>observableArrayList());
        redAreaChart.setTitle("Red");
        redAreaChart.setTitleSide(Side.TOP);
      
        NumberAxis xAxisGreen = new NumberAxis(0, 255, 64);
        xAxisGreen.setSide(Side.BOTTOM);                             //設定x座標軸的置放位置
        NumberAxis yAxisGreen = new NumberAxis();
        yAxisGreen.setSide(Side.LEFT);
        greenAreaChart = new AreaChart<Number,Number>(xAxisGreen,yAxisGreen); //設定區域圖
        greenAreaChart.setData(FXCollections.<XYChart.Series<Number, Number>>observableArrayList());
        greenAreaChart.setTitle("Green");
        greenAreaChart.setTitleSide(Side.TOP);
        
        NumberAxis xAxisBlue = new NumberAxis(0, 255, 64);
        xAxisBlue.setSide(Side.BOTTOM);                             //設定x座標軸的置放位置
        NumberAxis yAxisBlue = new NumberAxis();
        yAxisBlue.setSide(Side.LEFT);
        blueAreaChart = new AreaChart<Number,Number>(xAxisBlue,yAxisBlue); //設定區域圖
        blueAreaChart.setData(FXCollections.<XYChart.Series<Number, Number>>observableArrayList());
        blueAreaChart.setTitle("Blue");
        blueAreaChart.setTitleSide(Side.TOP);
        
    //結束直方圖    
        VBox vboximageProperty = new VBox();
        vboximageProperty.setId("vboximageProperty");
        vboximageProperty.setMaxSize(280, 680);
        vboximageProperty.getChildren().addAll(imageProperty, imageRevolution, redAreaChart, greenAreaChart, blueAreaChart);
        vboximageProperty.setAlignment(Pos.TOP_CENTER);
        vboximageProperty.setSpacing(1);
        vboximageProperty.setPadding(new Insets(10,2,2,2));
        
    //設定"影像基本資料"，位置在最右邊     
        HBox hboxFrame = new HBox();
        hboxFrame.getChildren().addAll(vboxLeftFrame, vboximageProperty);
        
        VBox rootVbox = new VBox();
        rootVbox.getChildren().addAll(toolBar, hboxFrame);
        
        Scene scene = new Scene(rootVbox, 1100, 800);
        scene.getStylesheets().add(this.getClass().getResource("background.css").toExternalForm());
        primaryStage.setTitle("Image Recognition 影像辨識");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    //設定FileChooser的方法
    EventHandler<ActionEvent> btnOpenEventListener = new EventHandler<ActionEvent>(){
        
        @Override
        public void handle(ActionEvent t) {
            FileChooser fileChooser = new FileChooser();
            File sourceFile = fileChooser.showOpenDialog(null);
                
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));                 
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
            );                     
            try {
                sourceBufferedImage = ImageIO.read(sourceFile);           
                   if (sourceBufferedImage != null) {
                        width = sourceBufferedImage.getWidth();
                        height = sourceBufferedImage.getHeight();
                        
                        writableImage = new WritableImage(width, height);
                        pixelWriter = writableImage.getPixelWriter();
                        for (int y = 0; y < height; y++) {
                            for (int x = 0; x < width; x++) {
                            pixelWriter.setArgb(x, y, sourceBufferedImage.getRGB(x, y));
                            }
                        }
                    }          
                imageRevolution.setText("影像解析度: 寬 x 高 = " + width +" x " + height);
                openImageView.setImage(writableImage);
                drawRGBHistogram(writableImage); 
                setXYChartData();
                redAreaChart.getStylesheets().add(getClass().getResource("redChart.css").toExternalForm());   
                greenAreaChart.getStylesheets().add(getClass().getResource("greenChart.css").toExternalForm());  
                blueAreaChart.getStylesheets().add(getClass().getResource("blueChart.css").toExternalForm());
            } 
            catch (IOException ex) {
            }
        }
    };

    public void drawRGBHistogram(Image RGBImage) {   
        RedFrequency = new int[256];
        GreenFrequency = new int[256];
        BlueFrequency = new int[256];
        
        PixelReader reader = RGBImage.getPixelReader();  
        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++) {               //注意：內迴圈先跑width,外迴圈再跑height 
                Color color = reader.getColor(x, y);

                int red = (int) (color.getRed()*255);
                int green = (int) (color.getGreen()*255);
                int blue = (int) (color.getBlue()*255);
 
                RedFrequency[red]++;
                GreenFrequency[green]++;
                BlueFrequency[blue]++;     
            }
        } 
        //maxRedFrequency = getMaxRGBValue(RedFrequency);         
    }
 
    public void setXYChartData() {  
            
            XYChart.Series<Number, Number> redSeries = new XYChart.Series<>();
            XYChart.Series<Number, Number> greenSeries = new XYChart.Series<>();
            XYChart.Series<Number, Number> blueSeries = new XYChart.Series<>();
            
            //清除原來的XYChart
            redAreaChart.getData().clear();         
            greenAreaChart.getData().clear();
            blueAreaChart.getData().clear();
            
                for (int i = 0; i < 256; i++){
                    redSeries.getData().add(new XYChart.Data<>(i,RedFrequency[i]));
                    greenSeries.getData().add(new XYChart.Data<>(i,GreenFrequency[i]));
                    blueSeries.getData().add(new XYChart.Data<>(i,BlueFrequency[i]));  
                }
            redAreaChart.getData().add(redSeries);
            greenAreaChart.getData().add(greenSeries);
            blueAreaChart.getData().add(blueSeries);
    }
    
    public static int getMaxRGBValue(int[] array){  
        int maxValue = array[0];  
        for(int i = 1;i < 256; i++){  
            if(array[i] > maxValue){  
            maxValue = array[i];  
            }  
        }  
        return maxValue;  
    }  
    
//設定紅階影像處理的方法
    EventHandler<ActionEvent> btnRedScaleEventListener = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            try {
                PixelReader pixelReader = writableImage.getPixelReader();
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        Color color = pixelReader.getColor(x, y);
                        Color redScale = new Color(color.getRed(), 0, 0, color.getOpacity());
                        pixelWriter.setColor(x, y, redScale);  
                    }
                }         
                processedImageView.setImage(processedimage);
                drawRGBHistogram(processedimage);
                setXYChartData();
                redAreaChart.getStylesheets().add(getClass().getResource("redChart.css").toExternalForm());   
                greenAreaChart.getStylesheets().add(getClass().getResource("greenChart.css").toExternalForm());  
                blueAreaChart.getStylesheets().add(getClass().getResource("blueChart.css").toExternalForm());    
            } 
            catch (Exception e) {
            }
        }
    };  
//設定綠階影像處理的方法
    EventHandler<ActionEvent> btnGreenScaleEventListener = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            try {
                PixelReader pixelReader = writableImage.getPixelReader();
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        Color color = pixelReader.getColor(x, y);
                        Color greenScale = new Color(0, color.getGreen(), 0, color.getOpacity());
                        pixelWriter.setColor(x, y, greenScale);  
                    }
                }         
                processedImageView.setImage(processedimage);
                drawRGBHistogram(processedimage);
                setXYChartData();
                redAreaChart.getStylesheets().add(getClass().getResource("redChart.css").toExternalForm());   
                greenAreaChart.getStylesheets().add(getClass().getResource("greenChart.css").toExternalForm());  
                blueAreaChart.getStylesheets().add(getClass().getResource("blueChart.css").toExternalForm());    
            } 
            catch (Exception e) {
            }
        }
    };      
    
//設定藍階影像處理的方法
    EventHandler<ActionEvent> btnBlueScaleEventListener = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            try {
                PixelReader pixelReader = writableImage.getPixelReader();
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        Color color = pixelReader.getColor(x, y);
                        Color greenScale = new Color(0, 0, color.getBlue(), color.getOpacity());
                        pixelWriter.setColor(x, y, greenScale);  
                    }
                }         
                processedImageView.setImage(processedimage);
                drawRGBHistogram(processedimage);
                setXYChartData();
                redAreaChart.getStylesheets().add(getClass().getResource("redChart.css").toExternalForm());   
                greenAreaChart.getStylesheets().add(getClass().getResource("greenChart.css").toExternalForm());  
                blueAreaChart.getStylesheets().add(getClass().getResource("blueChart.css").toExternalForm());    
            } 
            catch (Exception e) {
            }
        }
    };   
    
//設定灰階影像處理的方法
    EventHandler<ActionEvent> btnGrayScaleEventListener = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            try {
                PixelReader pixelReader = writableImage.getPixelReader();
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        Color color = pixelReader.getColor(x, y);
                        double avg = (color.getRed() + color.getGreen() + color.getBlue()) / 3.;
                        Color gray = new Color(avg, avg, avg, color.getOpacity());
                        pixelWriter.setColor(x, y, gray);  
                    }
                }         
                processedImageView.setImage(processedimage);
                drawRGBHistogram(processedimage);
                setXYChartData();
                redAreaChart.getStylesheets().add(getClass().getResource("grayChart.css").toExternalForm());   
                greenAreaChart.getStylesheets().add(getClass().getResource("grayChart.css").toExternalForm());  
                blueAreaChart.getStylesheets().add(getClass().getResource("grayChart.css").toExternalForm()); 
            } 
            catch (Exception e) {
            }
        }
    };
    
  
//設定負片影像處理的方法
    EventHandler<ActionEvent> btnNegativeEventListener = new EventHandler<ActionEvent>(){
        @Override
        public void handle(ActionEvent t) {
            try {
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        int pixel = sourceBufferedImage.getRGB(x, y);          
                        int red = (pixel >> 16) & 0xff;             
                        int green = (pixel >> 8) & 0xff;            
                        int blue = (pixel) & 0xff;        
                        int newRed = (255 - red);
                        int newGreen = (255 - green); 
                        int newBlue = (255 - blue);
                        int newColor = (newRed << 16) + (newGreen << 8) + newBlue; 
                        sourceBufferedImage.setRGB(x,y,newColor);
                        pixelWriter.setArgb(x, y, sourceBufferedImage.getRGB(x, y));
                    }
                }
                processedImageView.setImage(processedimage);
                drawRGBHistogram(processedimage);
                setXYChartData();
                redAreaChart.getStylesheets().add(getClass().getResource("redChart.css").toExternalForm());   
                greenAreaChart.getStylesheets().add(getClass().getResource("greenChart.css").toExternalForm());  
                blueAreaChart.getStylesheets().add(getClass().getResource("blueChart.css").toExternalForm());
            } 
            catch (Exception e) {
            }
        }
    };    
    
}
/*
                 PixelReader pixelReader = writableImage.getPixelReader();
                WritableImage processedimage1 = null;
                processedimage1= new WritableImage(width, height);
                pixelWriter = processedimage1.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        Color color = pixelReader.getColor(x, y);
                        int newRed = (int)(255 - color.getRed());
                        int newGreen = (int)(255 - color.getGreen());
                        int newBlue = (int)(255 - color.getBlue());
                        int newcolor = (newRed << 16) + (newGreen << 8) + newBlue;
                        pixelWriter.setColor(x, y, newcolor);
                        //pixelWriter.setArgb(x, y, sourceBufferedImage.getRGB(x, y));
                    }
                }



                BufferedImage grayScaleImage = null;
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        int pixel = sourceBufferedImage.getRGB(x, y);          
                        int red = (pixel >> 16) & 0xff;             
                        int green = (pixel >> 8) & 0xff;            
                        int blue = (pixel) & 0xff;                  
                        int grayLevel = (int) ((0.299 * red) + (0.587 * green) + (0.114 * blue));
                        int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
                        grayScaleImage.setRGB(x,y,gray);
                        pixelWriter.setArgb(x, y, grayScaleImage.getRGB(x, y));
                    }
                }    


                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        int pixel = sourceBufferedImage.getRGB(x, y);          
                        int red = (pixel >> 16) & 0xff;             
                        int green = (pixel >> 8) & 0xff;            
                        int blue = (pixel) & 0xff;        
                        int newRed = (255 - red);
                        int newGreen = (255 - green); 
                        int newBlue = (255 - blue);
                        int newColor = (newRed << 16) + (newGreen << 8) + newBlue; 
                        sourceBufferedImage.setRGB(x,y,newColor);
                        pixelWriter.setArgb(x, y, sourceBufferedImage.getRGB(x, y));
                    }
                }

                PixelReader pixelReader = writableImage.getPixelReader();
                processedimage= new WritableImage(width, height);
                pixelWriter = processedimage.getPixelWriter();
                for(int y = 0; y < height; y++){
                    for(int x = 0; x < width; x++){
                        Color color = pixelReader.getColor(x, y);
                        int newRed =  (255 - (int) color.getRed()) ;
                        int newGreen = (255 - (int)color.getGreen());
                        int newBlue = (255 - (int)color.getBlue());
                        Color negativeColor = new Color(newRed, newGreen, newBlue, color.getOpacity());
                        pixelWriter.setColor(x, y, negativeColor);  
                    }
                }
*/

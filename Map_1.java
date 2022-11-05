import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.shape.Polygon;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

//importy parsera
import java.io.File;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*; 



public class Map_1 extends Application {
  private static final int FRAME_WIDTH  = 640;
  private static final int FRAME_HEIGHT = 480;  
  
  GraphicsContext gc;
  Canvas canvas;
  
  Image image = new Image("map.jpg");
  
  //stan przycisków
  int woods_pressed = 1;
  int rocks_pressed = 1;
  
  
  //klasy
  class Las{
    double[] coord_x = new double[200];
    double[] coord_y = new double[200];
    String fill;
    int count;
  }
  Las[] lasy = new Las[5];
  int lasy_c = 0;
      
  class Skala{
    String fill;
    String stroke;
    double coord_x;
    double coord_y;
    double scale = 1.0;
  }
  
  Skala[] skaly = new Skala[20];
  int skaly_c = 0;
  //model dla skał
  static double[] model_x = new double[6];
  static double[] model_y = new double[6];
  int flag = 0;
  
  
      
  public static void main(String[] args) {
     launch(args);
  }


    
    @Override
    public void start(Stage primaryStage) {
    try{
      File inputFile = new File("points.xml");
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
    
    
      Handler_1 handler_1 = new Handler_1();

      saxParser.parse(inputFile, handler_1);     
     } catch (Exception e) {
      e.printStackTrace();
     }
     
      AnchorPane root = new AnchorPane();
      primaryStage.setTitle("Map");
    
      canvas = new Canvas(FRAME_WIDTH, FRAME_HEIGHT);
      canvas.setOnMousePressed(this::mouse);
      
      gc = canvas.getGraphicsContext2D();

      
      gc.drawImage(image, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);      
      
      double temp;
      for(int i = 0; i < lasy_c; ++i){
          for(int j = 1; j < lasy[i].count; ++j){
              lasy[i].coord_x[j] = lasy[i].coord_x[j-1] + lasy[i].coord_x[j];
              lasy[i].coord_y[j] = lasy[i].coord_y[j-1] + lasy[i].coord_y[j];
          }
      }
      
      for(int i = 0; i < 15; ++i){
          System.out.println(skaly[i].coord_x+" "+skaly[i].coord_y+" "+skaly[i].scale);
      }
      
      
      gc.setLineWidth(0.5);
      
      printWoods();
      printRocks();
      
      
          
      root.getChildren().add(canvas);    
      
      RadioButton rbtn1 = new RadioButton();
      rbtn1.setText("Woods");
      rbtn1.setSelected(true);      
      rbtn1.setOnAction(this::woods);    

      root.getChildren().add(rbtn1);
      AnchorPane.setBottomAnchor( rbtn1, 5.0d );
      AnchorPane.setLeftAnchor( rbtn1, 50.0d );      


      RadioButton rbtn2 = new RadioButton();
      rbtn2.setText("Rocks");
      rbtn2.setSelected(true);            
      rbtn2.setOnAction(this::rocks);    

      root.getChildren().add(rbtn2);
      AnchorPane.setBottomAnchor( rbtn2, 5.0d );
      AnchorPane.setLeftAnchor( rbtn2, 200.0d );      


      Scene scene = new Scene(root);
      primaryStage.setTitle("Dolina B\u0119dkowska");
      primaryStage.setScene( scene );
      primaryStage.setWidth(FRAME_WIDTH + 10);
      primaryStage.setHeight(FRAME_HEIGHT+ 80);
      primaryStage.show();
    }

    private void woods(ActionEvent e) {
     if(woods_pressed == 1){
         woods_pressed = 0;
         gc.drawImage(image, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
         if(rocks_pressed==1) printRocks();
     } else{
         woods_pressed = 1;
         printWoods();
     }
    }

   private void rocks(ActionEvent e) {
     if(rocks_pressed == 1){
         rocks_pressed = 0;
         gc.drawImage(image, 0, 0, FRAME_WIDTH, FRAME_HEIGHT);
         if(woods_pressed==1) printWoods();
     } else{
         rocks_pressed = 1;
         printRocks();
     }
    }


   
   private void mouse(MouseEvent e) {}
    
   private void printRocks(){
      for(int i = 0; i < skaly_c; ++i){
          if(skaly[i].fill == "black") gc.setFill(Color.BLACK);
          else if (skaly[i].fill == "darkgrey") gc.setFill(Color.DARKGREY);
          double[] iksy = new double[6]; //ilość punktów skały
          double[] igreki=new double[6];
          iksy[0] = skaly[i].coord_x + model_x[0]*skaly[i].scale;
          igreki[0] = skaly[i].coord_y + model_y[0]*skaly[i].scale;
          for(int j = 1; j < 6; ++j){
              iksy[j] = iksy[j-1] + model_x[j]*skaly[i].scale;
              igreki[j] = igreki[j-1] + model_y[j]*skaly[i].scale;
          }
          gc.fillPolygon(iksy, igreki, 6);
          gc.strokePolygon(iksy,igreki,6);
      }
   }
   private void printWoods(){
      gc.setFill(Color.GREEN);
      
      for(int i = 0; i < lasy_c; ++i){
          gc.fillPolygon(lasy[i].coord_x, lasy[i].coord_y, lasy[i].count);
          gc.strokePolygon(lasy[i].coord_x, lasy[i].coord_y, lasy[i].count);  
      }
   } 
   
   
   //klasa handler
   class Handler_1 extends DefaultHandler {
      int flag = 0;
      @Override
      public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
             
        //lasy
        if(flag==1){
                //System.out.println("flaga1");
                lasy[lasy_c] = new Las();
                lasy[lasy_c].fill = "green";
                
                StringTokenizer st1 = new StringTokenizer(attributes.getValue("d"), " "); 
                int i = 0;
                while (st1.hasMoreTokens()) {
                    String token = st1.nextToken();
                    if(token.equalsIgnoreCase("M") || token.equalsIgnoreCase("l")) continue;
                    else if(token.equalsIgnoreCase("z")) break;
                    else{
                        StringTokenizer st2 = new StringTokenizer(token, ","); 
                        lasy[lasy_c].coord_x[i] = Double.parseDouble(st2.nextToken());
                        lasy[lasy_c].coord_y[i] = Double.parseDouble(st2.nextToken());
                        //if(lasy_c==0) System.out.print(lasy[lasy_c].coord_x[i] + " ");
                        ++i;
                        ++(lasy[lasy_c].count);
                    }
                }
                ++lasy_c;
                //System.out.println("Lasy count: "+lasy_c);
        }
        if(qName.equalsIgnoreCase("g") && attributes.getValue("id").equalsIgnoreCase("lasy"))
            flag = 1;


        //teraz: skały
        //model skal:
        if(flag==2){
                //System.out.println("flaga2");
                StringTokenizer st1 = new StringTokenizer(attributes.getValue("d"), " "); 
                int i = 0;
                while (st1.hasMoreTokens()) {
                    String token = st1.nextToken();
                    if(token.equalsIgnoreCase("M") || token.equalsIgnoreCase("l")) continue;
                    else if(token.equalsIgnoreCase("z")) break;
                    else{
                        StringTokenizer st2 = new StringTokenizer(token, ","); 
                        model_x[i] = Double.parseDouble(st2.nextToken());
                        model_y[i] = Double.parseDouble(st2.nextToken());
                        ++i;
                    }
                }
        }
        if(qName.equalsIgnoreCase("symbol") && attributes.getValue("id").equalsIgnoreCase("skala_cz"))
            flag = 2;
            
        //pojedyncze skaly:
        if(flag==3){
            //System.out.println("flaga3");
            skaly[skaly_c] = new Skala();
            //.fill .stroke
            //w przypadku use
            if(qName.equalsIgnoreCase("use"))
            if(attributes.getValue("xlink:href").equalsIgnoreCase("#skala_cz")){
                skaly[skaly_c].stroke = "black";
                skaly[skaly_c].fill   = "black";
            } else if (attributes.getValue("xlink:href").equalsIgnoreCase("#skala_sz")){
                skaly[skaly_c].stroke = "black";
                skaly[skaly_c].fill   = "darkgrey";
            }
            //w przypadku path
            if(qName.equalsIgnoreCase("path")){
                if(attributes.getValue("fill").equalsIgnoreCase("black")){
                    skaly[skaly_c].stroke = "black";
                    skaly[skaly_c].fill   = "black";
                } else {
                    skaly[skaly_c].stroke = "black";
                    skaly[skaly_c].fill   = "darkgrey";
                }
            }
            
            //.scale i .coord_x .coord_y;
            if(attributes.getValue("transform")!=null){
                //rozdzielenie tranform na translate i scale
                StringTokenizer st1 = new StringTokenizer(attributes.getValue("transform"), " "); 
                String token = st1.nextToken();
                token = token.replaceAll("[^0-9,]", "");
                //System.out.println(token);
                //token ma teraz "coord.x,coord.y"
                StringTokenizer st2 = new StringTokenizer(token, ",");
                skaly[skaly_c].coord_x = Double.parseDouble(st2.nextToken());
                skaly[skaly_c].coord_y = Double.parseDouble(st2.nextToken());
                //jeśli w translate jest jeszcze scale:
                if(st1.hasMoreTokens()){
                    token = st1.nextToken();
                    token = token.replaceAll("[^0-9.,]", "");
                    st2 = new StringTokenizer(token, ",");
                    skaly[skaly_c].scale = Double.parseDouble(st2.nextToken());
                }
            } else if(attributes.getValue("x")!=null) {
                skaly[skaly_c].coord_x=Double.parseDouble(attributes.getValue("x"));
                skaly[skaly_c].coord_y=Double.parseDouble(attributes.getValue("y"));
            }
            
            
            //System.out.println("skala "+(skaly_c+1)+" fill: "+skaly[skaly_c].fill);
            ++skaly_c;
        }
        if(qName.equalsIgnoreCase("g") && attributes.getValue("id").equalsIgnoreCase("skaly"))
            flag = 3;
      }
       
      @Override
      public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("g") && flag==1){
          flag = 0;
          //System.out.println("flaga0");
        }
        if (qName.equalsIgnoreCase("symbol") && flag==2){
          flag = 0;
          //System.out.println("flaga0");
        }
        if (qName.equalsIgnoreCase("g") && flag==3){
          flag = 0;
          //System.out.println("flaga0");
        }
      }
    
                        
      @Override
      public void characters(char ch[], int start, int length) throws SAXException {
        //System.out.println(new String(ch, start, length));
      }
   }
}    
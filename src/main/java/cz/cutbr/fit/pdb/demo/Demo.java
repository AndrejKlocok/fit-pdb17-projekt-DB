package cz.cutbr.fit.pdb.demo;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import java.util.*;

public class Demo extends Application implements MapComponentInitializedListener{
    private GoogleMapView mapView;
    private GoogleMap map;

    private Button btnZoomIn;
    private Button btnZoomOut;
    private Label clickedPoint;
    private Marker actualPosition;

    /**
     *  Inicializacia
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {


        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        btnZoomIn = new Button("Zoom in");
        btnZoomOut = new Button("Zoom out");
        clickedPoint = new Label();
        ToolBar topBar = new ToolBar();
        BorderPane borderPane = new BorderPane();

        btnZoomIn.setOnAction(e -> {
            map.zoomProperty().set(map.getZoom() + 1);
        });
        btnZoomOut.setOnAction(e -> {
            map.zoomProperty().set(map.getZoom() - 1);
        });

        topBar.getItems().addAll(btnZoomIn, btnZoomOut,
                new Label("Click: "), clickedPoint);

        borderPane.setTop(topBar);
        borderPane.setCenter(mapView);

        Scene scene = new Scene(borderPane);
        stage.setTitle("Google Maps");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Refresh GoogleMap with zoom
     */
    private void refreshMap(){
        int zoom = map.getZoom();
        map.setZoom( zoom - 1 );
        map.setZoom( zoom );
    }
    @Override
    public void mapInitialized() {
        /*
            Brno center
         */
        LatLong Brno = new LatLong(49.191423, 16.606715);
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(Brno)
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(18);

        map = mapView.createMap(mapOptions);
        /*

         */
        Marker m = new Marker(new MarkerOptions()
                .title("Marker")
                .position(new LatLong(49.191581, 16.607092))
                .visible(true));
        /*
            Right clicked Point
         */
        map.addUIEventHandler(UIEventType.rightclick, (JSObject obj) -> {

            LatLong position = new LatLong((JSObject) obj.getMember("latLng"));
            clickedPoint.setText(position.toString());

            if(actualPosition != null){
                map.removeMarker(actualPosition);
                this.refreshMap();
            }

            MarkerOptions opts = new MarkerOptions();
            opts.title("Your Position: " + position.toString())
                    .visible(true)
                    .animation(Animation.DROP)
                    .position(position);
            actualPosition = new Marker(opts);
            map.addMarker(actualPosition);
        });

        /*
            Polygon
         */
        List<LatLong> col = new ArrayList<>();

        col.add(new LatLong(49.191381, 16.606092));
        col.add(new LatLong(49.191434, 16.606301));
        col.add(new LatLong(49.191717, 16.606153));
        col.add(new LatLong(49.191664, 16.605969));


        MVCArray mvc = new MVCArray(col.toArray());

        Polygon polygon = new Polygon( new PolygonOptions()
                .paths(mvc)
                .strokeColor("red")
                .strokeWeight(2)
                .editable(false)
                .fillColor("lightBlue")
                .fillOpacity(0.5));
        map.addMapShape(polygon);

        /*event onclick*/
        map.addUIEventHandler(polygon, UIEventType.click, (JSObject obj) -> polygon.setEditable(!polygon.getEditable())
        );
        /*
            Rectangle
         */
        LatLongBounds rb = new LatLongBounds(new LatLong(49.191438, 16.607195), new LatLong(49.191561, 16.607505));
        RectangleOptions rectangleOptions = new RectangleOptions()
                .bounds(rb)
                .strokeColor("black")
                .strokeWeight(2)
                .fillColor("yellow");
        Rectangle rectangle = new Rectangle(rectangleOptions);

        map.addUIEventHandler(rectangle, UIEventType.click, (JSObject) -> {
            rectangle.setEditable(!rectangle.getEditable());
        } );

        map.addMapShape(rectangle);

        /*
            Circle
         */
        LatLong point = new LatLong(49.191882, 16.606972);
        CircleOptions copts = new CircleOptions()
                .center(point)
                .radius(20)
                .strokeColor("green")
                .strokeWeight(2)
                .fillColor("orange")
                .fillOpacity(0.3);

        Circle c = new Circle(copts);

        map.addUIEventHandler(c, UIEventType.click, (JSObject) -> {
            c.setEditable(!c.getEditable());
        } );
        map.addMapShape(c);
    }
    public static void main(String[] args) {
        launch(args);
    }
}

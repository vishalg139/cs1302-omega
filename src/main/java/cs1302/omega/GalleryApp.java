package cs1302.omega;

import static cs1302.api.Tools.get;
import static cs1302.api.Tools.getJson;
import static cs1302.api.Tools.UTF8;
import java.io.IOException;
import cs1302.omega.MenuComp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Text;
import javafx.scene.layout.Priority;
import java.net.URL;
import javafx.application.Platform;
import java.net.URLEncoder;
import java.io.InputStreamReader;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.util.Random;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ProgressBar;


/**
 * Represents an iTunes GalleryApp.
 */
public class GalleryApp extends Application {

    private Menu menu1;
    private MenuBar menuBar;
    private MenuItem menuItem;
    private HBox hbox1 = new HBox();
    private Button play;
    private Text searchQuery;
    private Button update;
    private TextField searchField;
    private double n = .005;
    private ToolBar toolBar;
    private GridPane pictureGrid = new GridPane();
    private int numResults;
    private JsonArray results;
    private Image[][] display;
    private Image[] hidden;
    private Alert alert;
    private Timeline timer = new Timeline();
    private KeyFrame keyFr;
    private String defaultTerm = "dog";
    private ProgressBar p = new ProgressBar(0);


    /**
     * Starts the GalleryApp class and runs other methods.
     * @param stage the Stage which will be set
     */
    @Override
    public void start(Stage stage) {
        VBox pane = new VBox();

        //menuCreate();
        //pane.getChildren().addAll(menuBar);
        //breed();
        //toolBar();
        //pane.getChildren().addAll(hbox1);

        runNow(() -> {
            breed();
            Platform.runLater(() -> {
                start();
                toolBar();
                pictureGridInit();
                update.setOnAction(this:: getQuery);
                play.setOnAction(this:: playPause);
                pane.getChildren().addAll(new MenuComp(), hbox1, pictureGrid, p);
                Scene scene = new Scene(pane);
                stage.setMaxWidth(640);
                stage.setMaxHeight(480);
                stage.setTitle("Cat Dog Searcher!");
                stage.setScene(scene);
                stage.sizeToScene();
                stage.show();
            });
        });
    }

    /**
     * Creates a toolbar and puts objects in it.
     */
    private void toolBar() {
        play = new Button("Pause");
        update = new Button("Update Images");
        searchField = new TextField(defaultTerm);
        searchQuery = new Text("Search Query: ");
        toolBar = new ToolBar(play, searchQuery, searchField, update);
        HBox.setHgrow(toolBar, Priority.ALWAYS);
        hbox1.getChildren().add(toolBar);

    }

    /**
     * Sets the pause/play button to switch when the button is pressed.
     * @param e the ActionEvent entered
     */
    private void playPause(ActionEvent e) {
        if (play.getText().equals("Pause")) {
            timer.pause();
            play.setText("Play");
        } else {
            timer.play();
            play.setText("Pause");
        }
    }

    /**
     * Method to download results from a search query and populate image arrays.
     */
    private void query() {
        try {
            String sUrl =
                "https://itunes.apple.com/search?term="
                + URLEncoder.encode(searchField.getText(), "UTF-8")
                + "&limit=50&media=music&entity=album";
            URL url = new URL(sUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement je = JsonParser.parseReader(reader);
            JsonObject root = je.getAsJsonObject();

            results = rmDuplicates(root.getAsJsonArray("results"));
            numResults = results.size();
            rmNull();
            if (numResults < 21) {
                alert = new Alert(AlertType.WARNING);
                alert.setResizable(true);
                alert.setHeight(400);
                alert.setWidth(650);
                alert.setContentText("Invalid search results!"
                    + " Displaying default image query.");
                alert.show();
                runNow(() -> {
                    defaultQuery();
                    Platform.runLater(() -> pictureGridInit());
                });
            } else {
                display = new Image[4][5];
                int curr = 0;
                JsonElement artworkUrl100;
                String artworkUrl = "";
                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 5; y++) {
                        artworkUrl100 = results.get(curr)
                            .getAsJsonObject().get("artworkUrl100");
                        artworkUrl = artworkUrl100 + "";
                        display[x][y] =
                            new Image(artworkUrl.substring(1, artworkUrl.length() - 1));
                        n += 0.005;
                        p.setProgress(n);
                        curr++;
                    }
                }
                hidden = new Image[numResults - 20];
                for (int i = 0; i < hidden.length; i++) {
                    artworkUrl100 =
                       results.get(curr + i).getAsJsonObject().get("artworkUrl100");
                    artworkUrl = artworkUrl100 + "";
                    hidden[i] = new Image(artworkUrl.substring(1, artworkUrl.length() - 1));
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }


    /**
     * Query when applicaiton is first started.
     */
    public void defaultQuery() {
        try {
            String sUrl = 
                "https://itunes.apple.com/search?term="
                + URLEncoder.encode(defaultTerm, "UTF-8")
                + "&limit=50&media=music&entity=album";
            URL url = new URL(sUrl);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            JsonElement je = JsonParser.parseReader(reader);
            JsonObject root = je.getAsJsonObject();
            results = rmDuplicates(root.getAsJsonArray("results"));
            numResults = results.size();
            rmNull();
            display = new Image[4][5];
            int curr = 0;
            JsonElement artworkUrl100;
            String artworkUrl = "";
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 5; y++) {
                    artworkUrl100 = results.get(curr)
                        .getAsJsonObject().get("artworkUrl100");
                    artworkUrl = artworkUrl100 + "";
                    display[x][y] =
                        new Image(artworkUrl.substring(1, artworkUrl.length() - 1));
                    curr++;
                }
            }

            hidden = new Image[numResults - 20];
            for (int i = 0; i < hidden.length; i++) {
                artworkUrl100 = results.get(curr + i)
                    .getAsJsonObject().get("artworkUrl100");
                artworkUrl = artworkUrl100 + "";
                hidden[i] = new Image(artworkUrl.substring(1, artworkUrl.length() - 1));
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    /**
     * Randomly swaps images between the hidden and visible array.
     */
    private void imageSwapRandom() {
        runNow(() -> {
            Random r = new Random();
            int x = r.nextInt(3);
            int y = r.nextInt(4);
            Image temp = display[x][y];
            int i = r.nextInt(hidden.length - 1);
            display[x][y] = hidden[i];
            hidden[i] = temp;
            ImageView imgView = new ImageView(display[x][y]);
            imgView.setFitHeight(100);
            imgView.setFitWidth(100);
            Platform.runLater(createRunnable(x, y, imgView));//new ImageView(display[x][y])));
        });
    }

    /**
     * Creates a runnable target to add the ImageView to the picture grid.
     * @param x the row to add the ImageView
     * @param y the column to add the ImageView
     * @param imgView the ImageView to be added
     * @return the target Runnable
     */
    Runnable createRunnable(int x, int y, ImageView imgView) {
        Runnable target = () -> {
            pictureGrid.add(imgView, y, x);
        };
        return target;
    }

    /**
     * This is the event handler for the upade images button.
     * @param e the Action Event which is passed in
     */
    private void getQuery(ActionEvent e) {
        //query();
        breedChange();
        pictureGridInit();
    }

    /**
     * Initialize picture grid.
     */
    private void pictureGridInit() {
        ImageView imgView;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 5; y++) {
                imgView = new ImageView(display[x][y]);
                imgView.setFitHeight(100);
                imgView.setFitWidth(100);
                pictureGrid.add(imgView, y, x);
            }
        }
        timer.stop();
        timer.getKeyFrames().clear();
        keyFr = new KeyFrame(Duration.seconds(2), (e) -> imageSwapRandom());
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.getKeyFrames().add(keyFr);
        timer.play();

    }

    /**
     * Removes null elements from the json array.
     */
    private void rmNull() {
        JsonObject result;
        JsonElement artworkUrl100;
        for (int i = 0; i < numResults; i++) {
            result = results.get(i).getAsJsonObject();
            artworkUrl100 = result.get("artworkUrl100");
            if (artworkUrl100 == null) {
                results.remove(i);
            }
        }
        numResults = results.size();
    }

    /**
     * Remove duplicates from the Json array and creates a new array.
     * @param j the specified Json array
     * @return the new Json array
     */
    private JsonArray rmDuplicates(JsonArray j) {
        JsonArray temp = new JsonArray();
        for (int i = 0; i < j.size(); i++) {
            if (!temp.contains(j.get(i))) {
                temp.add(j.get(i));
            }
        }
        return temp;
    }

    /**
     * Creates and runs a new Daemon thread.
     * @param target the object whose run method is invoked when the thread is started
     */
    private static void runNow(Runnable target) {
        Thread thread = new Thread(target);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Shows the default cats and dogs.
     */
    public void breed() {
        String urlt = "https://api.thedogapi.com/v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=100";
        String urlc = "https://api.thecatapi.com/v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=100";
        try {
            display = new Image[4][5];
            JsonElement root = getJson(urlt);
            JsonElement rootc = getJson(urlc);
            /**
            int numFound = get(root).getAsJsonArray().size();
            System.out.printf("numFound = %d\n", numFound);
            for (int i = 0; i < 20; i++) {
                String breedName = get(root, i, "url").getAsString();
                System.out.println(breedName);
            }
            */
            int dcurr = 0;
            String artworkUrl = "";
            String linkd = "";
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 5; y++) {
                    if (dcurr % 2 != 0) {
                        linkd = get(root, dcurr, "url").getAsString();
                    } else {
                        linkd = get(rootc, dcurr, "url").getAsString();
                    }
                    display[x][y] =
                        new Image(linkd);
                    dcurr++;
                }
            }
            hidden = new Image[30];
            for (int i = 0; i < hidden.length; i++) {
                dcurr++;
                if (dcurr % 2 != 0) {
                    linkd = get(root, dcurr, "url").getAsString();
                } else {
                    linkd = get(rootc, dcurr, "url").getAsString();
                }
                hidden[i] = new Image(linkd);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }
    

    /**
     * Shows the changed cats and dogs.
     */
    public void breedChange() {
        String urlt = "https://api.thedogapi.com/v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=100";
        String urlc = "https://api.thecatapi.com/v1/images/search?size=med&mime_types=jpg&format=json&has_breeds=true&order=RANDOM&page=0&limit=100";
        try {
            display = new Image[4][5];
            JsonElement root = getJson(urlt);
            JsonElement rootc = getJson(urlc);
            /**
            int numFound = get(root).getAsJsonArray().size();
            System.out.printf("numFound = %d\n", numFound);
            for (int i = 0; i < 20; i++) {
                String breedName = get(root, i, "url").getAsString();
                System.out.println(breedName);
            }
            */
            int dcurr = 0;
            String artworkUrl = "";
            String linkd = "";
            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 5; y++) {
                    if (searchField.getText().equals("dog")) {
                        linkd = get(root, dcurr, "url").getAsString();
                    } else if (searchField.getText().equals("cat")) {
                        linkd = get(rootc, dcurr, "url").getAsString();
                    } else {
                        alert = new Alert(AlertType.WARNING);
                        alert.setResizable(true);
                        alert.setHeight(400);
                        alert.setWidth(650);
                        alert.setContentText("Must be either 'cat' or 'dog'");
                        alert.show();
                        runNow(() -> {
                            breed();
                            Platform.runLater(() -> pictureGridInit());
                        });
                    }
                    display[x][y] =
                        new Image(linkd);
                    dcurr++;
                }
            }
            hidden = new Image[40];
            for (int i = 0; i < hidden.length; i++) {
                dcurr++;
                if (searchField.getText().equals("dog")) {
                    linkd = get(root, dcurr, "url").getAsString();
                } else if (searchField.getText().equals("cat")) {
                    linkd = get(rootc, dcurr, "url").getAsString();
                }
                hidden[i] = new Image(linkd);
            }

        } catch (IOException ioe) {
            System.err.println(ioe);
        }
    }

    /**
     * This is the starting display.
     */
    public void start() {
        alert = new Alert(AlertType.INFORMATION);
        alert.setResizable(true);
        alert.setHeight(400);
        alert.setWidth(650);
        alert.setContentText("Enter 'cat' or dog' to see pictures!");
        alert.show();
    }
}

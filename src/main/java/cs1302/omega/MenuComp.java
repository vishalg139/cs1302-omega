package cs1302.omega;

import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.application.Platform;

/**
 * Custom component which represents a menu for
 * the gallery app.
 */
public class MenuComp extends HBox {
    MenuBar menuBar;
    Menu fileMenu;
    MenuItem exit;

    /**
     * Constructs the Menu bar which is contained in an HBox.
     */
    public MenuComp() {
        super();
        //Create menu bar
        menuBar = new MenuBar();
        //Create menu
        fileMenu = new Menu("File");
        //Add the menu item
        exit = new MenuItem("Exit");

        //Event handler for exit button
        exit.setOnAction(e -> Platform.exit());

        //Add menu item to the menu
        fileMenu.getItems().add(exit);
        //Add the menu to the menu bar
        menuBar.getMenus().add(fileMenu);

        HBox.setHgrow(menuBar, Priority.ALWAYS);
        this.getChildren().add(menuBar);
    }

}

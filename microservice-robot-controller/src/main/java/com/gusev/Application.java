package com.gusev;

//import com.encephalon.Hypergraph.Graph;
//import com.encephalon.Hypergraph.GraphDAG;
//import com.encephalon.Hypergraph.GraphFactor;
//import com.encephalon.Hypergraph.GraphJunctionTree;
import com.encephalon.Hypergraph.Graph;
import com.encephalon.Hypergraph.GraphDAG;
import com.encephalon.Hypergraph.GraphFactor;
import com.encephalon.Hypergraph.GraphJunctionTree;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application extends AbstractJavaFxApplicationSupport {

    @Value("${ui.title:JavaFX приложение}")//
    private String windowTitle;

    @Qualifier("mainView")
    @Autowired
    private ControllersConfiguration.ViewHolder view;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(windowTitle);
        stage.setScene(new Scene(view.getView()));
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        GraphDAG a = new GraphDAG();
        a.addNode("A");
        a.addNode("S");
        a.addNode("T");
        a.addNode("C");
        a.addNode("B");
        a.addNode("P");
        a.addNode("X");
        a.addNode("D");
        a.addConnection("A", "T");
        a.addConnection("S", "C");
        a.addConnection("S", "B");
        a.addConnection("T", "P");
        a.addConnection("C", "P");
        a.addConnection("B", "D");
        a.addConnection("P", "X");
        a.addConnection("P", "D");
        GraphFactor b = new GraphFactor(a);
        Graph c = b.buildUndirectedGraphFactory();
        c.reduceCyclic();
        GraphJunctionTree<String> cg = new GraphJunctionTree(c);
        System.out.println(cg.toString());
        stage.show();
    }

    public static void main(String[] args) {
        launchApp(Application.class, args);
    }

}

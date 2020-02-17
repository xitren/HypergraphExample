/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.fx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.gusev.controllers.AgentController;
import com.gusev.controllers.FloodController;
import com.gusev.controllers.RandomController;
import com.gusev.move_table.entity.Action;
import com.gusev.move_table.entity.Move;
import com.gusev.move_table.service.MoveService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author gusev_a
 */
public class ControlCenterController implements Initializable {
    private static String ROBOT_ID = "agent1";
    private static int PORT = 12765;

    @FXML
    public Button up;
    @FXML
    public Button left;
    @FXML
    public Button right;
    @FXML
    public Button down;
    @FXML
    public GridPane scanner_grid;
    @FXML
    public TableView<Agent> robots;
    @FXML
    public TableColumn<Agent, String> col_id;
    @FXML
    public TableColumn<Agent, Integer> col_x;
    @FXML
    public TableColumn<Agent, Integer> col_y;

    private final static Timer UPDATER = new Timer();

    public Pane scanUp = new Pane();
    public Pane scanDown = new Pane();
    public Pane scanLeft = new Pane();
    public Pane scanRight = new Pane();
    private static final Background[] WORLD_BACK = {
            new Background(
                    new BackgroundFill(
                            Color.LIGHTBLUE,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
            new Background(
                    new BackgroundFill(
                            Color.LIGHTBLUE,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
            new Background(
                    new BackgroundFill(
                            Color.BLACK,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
            new Background(
                    new BackgroundFill(
                            Color.BLACK,
                            CornerRadii.EMPTY,
                            Insets.EMPTY
                    )
            ),
    };

    @Autowired
    private MoveService moveStoreService;
    private ObservableList<Agent> agents = FXCollections.observableArrayList();
    private AgentController robot_ctrl = new FloodController();

    private Agent getAgentById(String id) {
        for (Agent ag : agents) {
            if (ag.getId().equals(id))
                return ag;
        }
        return null;
    }

    private static String sendGETAction(String action){
        try {
            URL obj = new URL(action);
            System.out.println(action);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
                return response.toString();
            } else {
                System.out.println("GET request not worked");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(
                    ControlCenterController.class.getName()
            ).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(
                    ControlCenterController.class.getName()
            ).log(Level.SEVERE, null, ex);
        }
        return "error";
    }

    private static void getAgents(ObservableList<Agent> _agents){
        try {
            URL obj = new URL("http://localhost:" + PORT + "/agents/list");
            System.out.println("http://localhost:" + PORT + "/agents/list");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
                List<Agent> ag = getAgents(response.toString());
                synchronized (_agents) {
                    _agents.clear();
                    _agents.addAll(ag);
                }
            } else {
                System.out.println("GET request not worked");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(
                    ControlCenterController.class.getName()
            ).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(
                    ControlCenterController.class.getName()
            ).log(Level.SEVERE, null, ex);
        }
    }

    public static Agent getAgent(String str){
        return new Agent(getId(str), getX(str), getY(str));
    }

    public static String getId(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<ID\\>)(\\s*.*\\s*)(?=\\<\\/ID\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return s.trim();
        }
        return "";
    }

    public static Integer getX(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<X\\>)(\\s*.*\\s*)(?=\\<\\/X\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return Integer.parseInt(s.trim());
        }
        return 0;
    }

    public static Integer getY(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<Y\\>)(\\s*.*\\s*)(?=\\<\\/Y\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return Integer.parseInt(s.trim());
        }
        return 0;
    }

    public static List<Agent> getAgents(String str) {
        List<Agent> list = new LinkedList();
        int i = 0;
        for (i = 0; i < str.length(); i++) {
            Pattern pattern = Pattern.compile(
                    "(?<=\\<ROBOT" + i + "\\>)(\\s*.*\\s*)(?=\\<\\/ROBOT" + i + "\\>)");
            java.util.regex.Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                String s = matcher.group();
                list.add(getAgent(s.trim()));
                continue;
            } else
                break;
        }
        return list;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UPDATER.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        redraw();
                    }
                }, 5000, 500);
        scanUp.setPrefWidth(30);
        scanUp.setPrefHeight(30);
        scanUp.setMinWidth(30);
        scanUp.setMinHeight(30);
        scanUp.setMaxWidth(30);
        scanUp.setMaxHeight(30);
        scanDown.setPrefWidth(30);
        scanDown.setPrefHeight(30);
        scanDown.setMinWidth(30);
        scanDown.setMinHeight(30);
        scanDown.setMaxWidth(30);
        scanDown.setMaxHeight(30);
        scanLeft.setPrefWidth(30);
        scanLeft.setPrefHeight(30);
        scanLeft.setMinWidth(30);
        scanLeft.setMinHeight(30);
        scanLeft.setMaxWidth(30);
        scanLeft.setMaxHeight(30);
        scanRight.setPrefWidth(30);
        scanRight.setPrefHeight(30);
        scanRight.setMinWidth(30);
        scanRight.setMinHeight(30);
        scanRight.setMaxWidth(30);
        scanRight.setMaxHeight(30);
        scanner_grid.add(scanUp,1,0);
        scanner_grid.add(scanDown,1,2);
        scanner_grid.add(scanLeft,0,1);
        scanner_grid.add(scanRight,2,1);
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_x.setCellValueFactory(new PropertyValueFactory<>("x"));
        col_y.setCellValueFactory(new PropertyValueFactory<>("y"));
        sendGETAction(String.format("http://localhost:" + PORT + "/agents/clear"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=2"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=3"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=4"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=5"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=6"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=7"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=8"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=9"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=10"));
        getAgents(agents);
        robots.setItems(agents);
        robots.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
            Agent tmp = robots.getSelectionModel().getSelectedItem();
            if (tmp != null)
                ROBOT_ID = tmp.getId();
            else
                ROBOT_ID = "none";
        });
    }

    private void redraw() {
        getAgents(agents);
        synchronized (agents) {
            agents.stream().forEach((e) -> {
                AgentScanner sc =  AgentScanner.getCurrentScan(e.getId(), PORT);
                if (sc == null)
                    return;
                int l = robot_ctrl.getMove(e, sc);
                Long prev = getWorldMark();
                switch (l) {
                    case 1:
                        sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/up", e.getId()));
                        break;
                    case 2:
                        sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/down", e.getId()));
                        break;
                    case 3:
                        sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/left", e.getId()));
                        break;
                    case 4:
                        sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/right", e.getId()));
                        break;
                    default:
                        break;
                }
                Long after = getWorldMark();
                moveStoreService.save(
                        new Move(e.getId(), e.getX(), e.getY(), Action.MOVE_UP,
                                sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType(),
                                prev, after)
                );
            });
        }
    }

    private void setScanned(AgentScanner sc) {
        scanUp.setBackground(WORLD_BACK[sc.getUpType()]);
        scanDown.setBackground(WORLD_BACK[sc.getDownType()]);
        scanLeft.setBackground(WORLD_BACK[sc.getLeftType()]);
        scanRight.setBackground(WORLD_BACK[sc.getRightType()]);
    }
    
    @Override
    protected void finalize(){
        sendGETAction(String.format("http://localhost:" + PORT + "/agents/delete?id=%s", ROBOT_ID));
    }

    private Long getWorldMark(){
        String rr =  sendGETAction(String.format("http://localhost:" + PORT + "/world/mark"));
        return Long.parseLong(rr);
    }

    @FXML
    public void OnUp(ActionEvent actionEvent) {
        Long prev = getWorldMark();
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/up", ROBOT_ID))
                .equals("error")) {
            Long after = getWorldMark();
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            setScanned(sc);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_UP,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType(),
                            prev, after)
            );
        }
    }

    @FXML
    public void OnLeft(ActionEvent actionEvent) {
        Long prev = getWorldMark();
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/left", ROBOT_ID))
                .equals("error")) {
            Long after = getWorldMark();
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            setScanned(sc);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_LEFT,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType(),
                            prev, after)
            );
        }
    }

    @FXML
    public void OnDelete(ActionEvent actionEvent) {
        ROBOT_ID = sendGETAction("http://localhost:" + PORT + "/agents/delete?id=" + ROBOT_ID);
        getAgents(agents);
    }

    @FXML
    public void OnRight(ActionEvent actionEvent) {
        Long prev = getWorldMark();
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/right", ROBOT_ID))
                .equals("error")) {
            Long after = getWorldMark();
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            setScanned(sc);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_RIGHT,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType(),
                            prev, after)
            );
        }
    }

    @FXML
    public void OnDown(ActionEvent actionEvent) {
        Long prev = getWorldMark();
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/down", ROBOT_ID))
                .equals("error")) {
            Long after = getWorldMark();
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            setScanned(sc);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_DOWN,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType(),
                            prev, after)
            );
        }
    }

    @FXML
    public void OnAdd(ActionEvent actionEvent) {
        ROBOT_ID = sendGETAction("http://localhost:" + PORT + "/agents/add?x=2&y=2");
        getAgents(agents);
    }
}



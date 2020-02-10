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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.gusev.move_table.entity.Action;
import com.gusev.move_table.entity.Move;
import com.gusev.move_table.service.MoveService;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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

    @Autowired
    private MoveService moveStoreService;
    private ObservableList<Agent> agents = FXCollections.observableArrayList();

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
                _agents.clear();
                _agents.addAll(ag);
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
        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        col_x.setCellValueFactory(new PropertyValueFactory<>("x"));
        col_y.setCellValueFactory(new PropertyValueFactory<>("y"));
        ROBOT_ID = sendGETAction(String.format("http://localhost:" + PORT + "/agents/add?x=2&y=2"));
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
    
    @Override
    protected void finalize(){
        sendGETAction(String.format("http://localhost:" + PORT + "/agents/delete?id=%s", ROBOT_ID));
    }

    @FXML
    public void OnUp(ActionEvent actionEvent) {
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/up", ROBOT_ID))
                .equals("error")) {
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_UP,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType())
            );
        }
    }

    @FXML
    public void OnLeft(ActionEvent actionEvent) {
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/left", ROBOT_ID))
                .equals("error")) {
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_LEFT,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType())
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
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/right", ROBOT_ID))
                .equals("error")) {
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_RIGHT,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType())
            );
        }
    }

    @FXML
    public void OnDown(ActionEvent actionEvent) {
        if (!sendGETAction(String.format("http://localhost:" + PORT + "/agents/%s/move/down", ROBOT_ID))
                .equals("error")) {
            Agent im = getAgentById(ROBOT_ID);
            AgentScanner sc =  AgentScanner.getCurrentScan(ROBOT_ID, PORT);
            moveStoreService.save(
                    new Move(im.getId(), im.getX(), im.getY(), Action.MOVE_DOWN,
                            sc.getUpType(), sc.getDownType(), sc.getLeftType(), sc.getRightType())
            );
        }
    }

    @FXML
    public void OnAdd(ActionEvent actionEvent) {
        ROBOT_ID = sendGETAction("http://localhost:" + PORT + "/agents/add?x=2&y=2");
        getAgents(agents);
    }
}



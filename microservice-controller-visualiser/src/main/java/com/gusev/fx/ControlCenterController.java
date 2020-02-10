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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

/**
 *
 * @author gusev_a
 */
public class ControlCenterController implements Initializable {
    private static String ROBOT_ID = "agent1";

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
    public TableView robots;
    @FXML
    public TableColumn col_id;
    @FXML
    public TableColumn col_x;
    @FXML
    public TableColumn col_y;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ROBOT_ID = sendGETAction(String.format("http://localhost:8080/agents/add?x=2&y=2"));
    }    
    
    @Override
    protected void finalize(){
        sendGETAction(String.format("http://localhost:8080/agents/delete?id=%s", ROBOT_ID));
    }

    @FXML
    public void OnUp(ActionEvent actionEvent) {
        sendGETAction(String.format("http://localhost:8080/agents/%s/move/up", ROBOT_ID));
    }

    @FXML
    public void OnLeft(ActionEvent actionEvent) {
        sendGETAction(String.format("http://localhost:8080/agents/%s/move/left", ROBOT_ID));
    }

    @FXML
    public void OnDelete(ActionEvent actionEvent) {
    }

    @FXML
    public void OnRight(ActionEvent actionEvent) {
        sendGETAction(String.format("http://localhost:8080/agents/%s/move/right", ROBOT_ID));
    }

    @FXML
    public void OnDown(ActionEvent actionEvent) {
        sendGETAction(String.format("http://localhost:8080/agents/%s/move/down", ROBOT_ID));
    }
}



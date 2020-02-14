/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.visual;

import com.gusev.world.WorldMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author gusev_a
 */
public class TestViewController implements Initializable {

    public WorldMap data;
    @FXML
    public Label Mark;
    @FXML
    private Pane map;
    @FXML
    private Pane tile;
    private Timer timer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            data = new WorldMap(map, new File("./map.txt"));
        } catch (IOException ex) {
            Logger.getLogger(
                    TestViewController.class.getName()
            ).log(Level.SEVERE, null, ex);
        }
        timer = new Timer("TimerTests");
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        data.step();
                        int mm = data.getMark();
                        Platform.runLater(()->{
                            Mark.setText("Current mark " + mm);
                        });
                    }
                }, 2000, 100);
    }

    @Override
    protected void finalize() {
        try {
            timer.cancel();
        } finally {
            try {
                super.finalize();
            } catch (Throwable ex) {
                Logger.getLogger(
                        TestViewController.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
    }
}
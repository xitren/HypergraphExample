/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gusev.fx;

import com.gusev.world.WorldMap;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
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
    public LineChart graph;
    @FXML
    private Pane map;
    @FXML
    private Pane tile;
    private Timer timer;

    private final XYChart.Series<Number, Number> series = new XYChart.Series();
    private final Queue<XYChart.Data> data_ser = new LinkedList();
    protected static final int VISUALIZED_RECORDS = 400;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        XYChart.Data ddd;
        for (int j = 0;j < VISUALIZED_RECORDS;j++) {
            ddd = new XYChart.Data(j, 0);
            data_ser.offer(ddd);
            series.getData().add(ddd);
        }
        graph.getData().add(series);
        graph.setCreateSymbols(false);
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
                        XYChart.Data<Number, Number> ddd = data_ser.poll();
                        Platform.runLater(()->{
                            Mark.setText("Current mark " + mm);
                            ddd.setXValue(ddd.getXValue().intValue() + VISUALIZED_RECORDS);
                            ddd.setYValue(mm);
                        });
                        data_ser.offer(ddd);
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.fx.sankeyplot;

import com.encephalon.fx.sankeyplot.tools.Helper;
import javafx.scene.paint.Color;

/**
 *
 * @author xitre
 */
public class ColorsRND {
    
    private enum Colors {
        LIGHT_BLUE(Color.web("#a6cee3")),
        ORANGE(Color.web("#fdbf6f")),
        LIGHT_RED(Color.web("#fb9a99")),
        LIGHT_GREEN(Color.web("#b2df8a")),
        YELLOW(Color.web("#ffff99")),
        PURPLE(Color.web("#cab2d6")),
        BLUE(Color.web("#1f78b4")),
        GREEN(Color.web("#33a02c"));

        private Color color;
        private Color translucentColor;

        Colors(final Color COLOR) {
            color = COLOR;
            translucentColor = Helper.getColorWithOpacity(color, 0.75);
        }

        public Color get() { return color; }
        public Color getTranslucent() { return translucentColor; }
    }
    
    public static Color GetNextColor(){
        Colors f;
        switch ((new Double(Math.random()*10)).intValue() % 10) {
            case 0:
                f = Colors.LIGHT_BLUE;
                break;
            case 1:
                f = Colors.ORANGE;
                break;
            case 2:
                f = Colors.LIGHT_RED;
                break;
            case 3:
                f = Colors.LIGHT_GREEN;
                break;
            case 4:
                f = Colors.YELLOW;
                break;
            case 5:
                f = Colors.YELLOW;
                break;
            case 6:
                f = Colors.PURPLE;
                break;
            case 7:
                f = Colors.BLUE;
                break;
            case 8:
                f = Colors.GREEN;
                break;
            case 9:
                f = Colors.PURPLE;
                break;
            default:
                f = Colors.BLUE;
                break;
        }
        return f.get();
    }
}

package com.jeremycurny.sparkjavarestapi.app;

import static spark.Spark.*;

import com.jeremycurny.sparkjavarestapi.util.Player;
import com.jeremycurny.sparkjavarestapi.util.Tile;
import com.jeremycurny.sparkjavarestapi.util.TileContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeremycurny.sparkjavarestapi.controller.impl.UserController;

import spark.Spark;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static JFrame frame;
    private static ArrayList<ArrayList<Panel>> listeLabel;

	public static void main(String[] args) {

		frame = new JFrame("FrameDemo");

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(new GridLayout(21,20));

		listeLabel = new ArrayList<ArrayList<Panel>>();

		for (int i = 0; i< 20; i++) {
			listeLabel.add(i, new ArrayList<Panel>());
			for (int j = 0; j < 20; j++) {
				listeLabel.get(i).add(j, new Panel());
                listeLabel.get(i).get(j).setSize(25,25);
				frame.add(listeLabel.get(i).get(j));
				listeLabel.get(i).get(j).setVisible(true);
				listeLabel.get(i).get(j).setBackground(Color.white);
                listeLabel.get(i).get(j).add(new Label());
                Label temp = (Label)(listeLabel.get(i).get(j).getComponent(0));
                temp.setText("x: " + i + " y: " + j);
			}
		}



		frame.setSize(1200,900);

		frame.setVisible(true);



		UserController userController = new UserController();

		port(8080);

		before((req, res) -> {
			res.header("Access-Control-Allow-Headers", "Authorization, Content-Type");
			res.header("Access-Control-Allow-Methods", "POST");
			res.header("Access-Control-Allow-Origin", "*");
		});

		post("/", (req, res) -> userController.bot(req, res));

		exception(Exception.class, (e, req, res) -> {
			String message = e.getClass().getName() + ": " + e.getMessage();
			res.type("application/json");
			res.status(500);
			res.body(message);
		});
	}

	public static void updateGui(List<List<Tile>> posMap) {
        int [] arrayColors={Color.GRAY.getRGB(),Color.magenta.getRGB(),Color.CYAN.getRGB(), Color.RED.getRGB(), Color.YELLOW.getRGB()};

        for (int i = 0; i< 20; i++) {
            for (int j = 0; j < 20; j++) {
                Color color = new Color(arrayColors[posMap.get(i).get(j).Content]);
                listeLabel.get(i).get(j).setBackground(color);
                Label temp = (Label)listeLabel.get(i).get(j).getComponent(0);
                if (posMap.get(i).get(j).Content == 0)
                    temp.setText("Empty");
                else if (posMap.get(i).get(j).Content == 1)
                    temp.setText("Wall");
                else if (posMap.get(i).get(j).Content == 2)
                    temp.setText("House");
                else if (posMap.get(i).get(j).Content == 3)
                    temp.setText("Lava");
                else if (posMap.get(i).get(j).Content == 4)
                    temp.setText("Resource");
                else if (posMap.get(i).get(j).Content == 5)
                    temp.setText("Shop");
                else if (posMap.get(i).get(j).Content == 6)
                    temp.setText("Player");
            }
        }
    }

    public static void updatePlayer(Player player) {
        Label temp = (Label)listeLabel.get(player.Position.x - 15).get(player.Position.y - 17).getComponent(0);
        temp.setText("HERE");
        listeLabel.get(player.Position.x - 15).get(player.Position.y - 17).setBackground(Color.green);

    }
}

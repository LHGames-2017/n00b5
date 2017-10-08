package com.jeremycurny.sparkjavarestapi.controller.impl;

import com.jeremycurny.sparkjavarestapi.app.App;
import com.jeremycurny.sparkjavarestapi.controller.RestController;
import com.jeremycurny.sparkjavarestapi.util.*;

import java.awt.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.jeremycurny.sparkjavarestapi.util.Point;
import spark.Request;
import spark.Response;

import javax.xml.soap.Node;

class NodePosition {
    int x = 0;
    int y = 0;
    int distY = 99;
    int distX = 99;

    NodePosition(int x, int y, int distX, int distY) {
        this.x = x;
        this.y = y;
        this.distX = distX;
        this.distY = distY;
    }
}


public class UserController extends RestController {

    // Listes de points/score ou on peut tenter dupgrade

    public static int currentLevel = 0;
    public static int[] arrayUpgrade = {15000, 30000, 80000, 130000, 230000, 330000, 580000, 830000, 1330000, 1830000};

    @Override
    public Object bot(Request req, Response res) {
        String s = URLDecoder.decode(req.body()).substring(4);
        GameInfo gameInfo = new GameInfo();
        gameInfo.fromJson(s);
        String action;

        // If lastPos == currentPos then try to attack wall that blocks us
        Point lastPos = null;

        // Boolean whether to attack or move
        boolean attack = false;

        // Dont get stuck at home indefinitely trying to upgrade yourself
        boolean lastTurnTriedUpgrade = false;

        Tile tempTileTarget = null;


        try {

            // Gui VRAIMENT beau
            App.updateGui(gameInfo.map);

        } catch (Exception e) {
            System.out.println("ERROR");
            e.printStackTrace();
            System.out.println(e.getCause());
            System.out.println(e.getStackTrace());

        }


        // Nice little infos
        System.out.println("Player Level (Guesstimate): " + currentLevel);
        System.out.println("Player Score: " + gameInfo.player.Score);
        System.out.println("Player Health: " + gameInfo.player.Health);


        //Try and find a target, else go find a node near your house
        Tile targetTile = null;

        try {
            targetTile = findNearestNode(gameInfo.map, gameInfo.player);

        } catch (Exception e) {
            e.printStackTrace();
            Double tempdouble1 = Math.floor(gameInfo.player.HouseLocation.x + Math.random() * 30 - 15);
            Double tempdouble2 = Math.floor(gameInfo.player.HouseLocation.y + Math.random() * 30 - 15);

            if (targetTile == null) {
                targetTile = new Tile(tempdouble1.intValue(), tempdouble2.intValue(), 0);
                tempTileTarget = targetTile;
            }
        }


        int targetX, targetY;
        targetX = gameInfo.player.Position.x;
        targetY = gameInfo.player.Position.y;

        // Handle the upgrade... Using the most efficient gathering update path
        if (gameInfo.player.Position.x == gameInfo.player.HouseLocation.x && gameInfo.player.Position.y == gameInfo.player.HouseLocation.y &&
                gameInfo.player.Score > arrayUpgrade[currentLevel] && !lastTurnTriedUpgrade) {
            lastTurnTriedUpgrade = true;
            currentLevel++;
            if (currentLevel % 2 == 1)
                action = AiHelper.CreateUpgradeAction(UpgradeType.CarryingCapacity);
            else
                action = AiHelper.CreateUpgradeAction(UpgradeType.CollectingSpeed);

            System.out.println("TRYING TO LEVEL TO LEVEL " + currentLevel);
        }

        // Gather resources when within 1 tile of the resource
        else if (gameInfo.player.CarriedResources < gameInfo.player.CarryingCapacity &&
                (targetTile.X == gameInfo.player.Position.x - 1 && targetTile.Y == gameInfo.player.Position.y ||
                        targetTile.X == gameInfo.player.Position.x + 1 && targetTile.Y == gameInfo.player.Position.y ||
                        targetTile.X == gameInfo.player.Position.x && targetTile.Y == gameInfo.player.Position.y + 1 ||
                        targetTile.X == gameInfo.player.Position.x && targetTile.Y == gameInfo.player.Position.y - 1)) {
            System.out.println("GATHERING");
            System.out.println("Player Carried Ressources: " + gameInfo.player.CarriedResources);
            System.out.println("Player Carry capacity: " + gameInfo.player.CarryingCapacity);


            lastTurnTriedUpgrade = false;
            action = AiHelper.CreateCollectAction(new Point(targetTile.X, targetTile.Y));
        }

        // If you can still carry more resources, go near a node to gather it
        else if (gameInfo.player.CarriedResources < gameInfo.player.CarryingCapacity) {

            if (lastPos != null) {
                if (lastPos.x == gameInfo.player.Position.x && lastPos.y == gameInfo.player.Position.y) {
                    attack = true;
                } else {
                    attack = false;
                }
            }


            if (targetTile.X > gameInfo.player.Position.x)
                targetX++;
            else if (targetTile.Y > gameInfo.player.Position.y)
                targetY++;
            else if (targetTile.X < gameInfo.player.Position.x)
                targetX--;
            else if (targetTile.Y < gameInfo.player.Position.y)
                targetY--;

            gameInfo.player.Position.x = targetX;
            gameInfo.player.Position.y = targetY;


            lastTurnTriedUpgrade = false;
            if (!attack) {
                action = AiHelper.CreateMoveAction(gameInfo.player.Position);
            } else {
                System.out.println("Trynna attack a wall");
                action = AiHelper.CreateAttackAction(gameInfo.player.Position);
            }
        }


        // Go back home with resources
        else {

            if (lastPos != null) {
                if (lastPos.x == gameInfo.player.Position.x && lastPos.y == gameInfo.player.Position.y) {
                    attack = true;
                } else {
                    attack = false;
                }
            }

            lastPos = new Point(gameInfo.player.Position.x, gameInfo.player.Position.y);


            if (gameInfo.player.HouseLocation.x > gameInfo.player.Position.x)
                targetX++;
            else if (gameInfo.player.HouseLocation.y > gameInfo.player.Position.y)
                targetY++;
            else if (gameInfo.player.HouseLocation.x < gameInfo.player.Position.x)
                targetX--;
            else if (gameInfo.player.HouseLocation.y < gameInfo.player.Position.y)
                targetY--;


            gameInfo.player.Position.x = targetX;
            gameInfo.player.Position.y = targetY;


            if (!attack) {
                action = AiHelper.CreateMoveAction(gameInfo.player.Position);
            } else {
                System.out.println("Trynna attack a wall");
                action = AiHelper.CreateAttackAction(gameInfo.player.Position);
            }

            lastTurnTriedUpgrade = false;

        }


        return super.resJson(req, res, 200, action);
    }

    // Function to find nearest Resource node
    private Tile findNearestNode(List<List<Tile>> map, Player player) {

        ArrayList<Tile> arrayPos = new ArrayList<Tile>();
        NodePosition nodeClosest = null;

        int distX = -1;
        int distY = -1;

        for (int i = 0; i < map.size(); i++) {
            for (int j = 0; j < map.get(i).size(); j++) {
                if (map.get(i).get(j).Content == 4) {
                    arrayPos.add(map.get(i).get(j));
                }
            }
        }


        for (int i = 0; i < arrayPos.size(); i++) {
            if (nodeClosest == null) {
                nodeClosest = new NodePosition(arrayPos.get(i).X, arrayPos.get(i).Y, Math.abs(player.Position.x - arrayPos.get(i).X), Math.abs(player.Position.y - arrayPos.get(i).Y));
            } else if (nodeClosest.distX + nodeClosest.distY > Math.abs(player.Position.x - arrayPos.get(i).X) + Math.abs(player.Position.y - arrayPos.get(i).Y)) {
                nodeClosest = nodeClosest = new NodePosition(arrayPos.get(i).X, arrayPos.get(i).Y, Math.abs(player.Position.x - arrayPos.get(i).X), Math.abs(player.Position.y - arrayPos.get(i).Y));

            }

        }

        Tile tile = new Tile(nodeClosest.x, nodeClosest.y, 4);
        return tile;
    }
}

package com.jeremycurny.sparkjavarestapi.controller.impl;

import com.jeremycurny.sparkjavarestapi.app.App;
import com.jeremycurny.sparkjavarestapi.controller.RestController;
import com.jeremycurny.sparkjavarestapi.util.AiHelper;

import java.awt.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.jeremycurny.sparkjavarestapi.util.GameInfo;
import com.jeremycurny.sparkjavarestapi.util.Player;
import com.jeremycurny.sparkjavarestapi.util.Point;
import com.jeremycurny.sparkjavarestapi.util.Tile;
import spark.Request;
import spark.Response;

import javax.xml.soap.Node;

class NodePosition{
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

	@Override
	public Object bot(Request req, Response res) {
		String s = URLDecoder.decode(req.body()).substring(4);
		GameInfo gameInfo = new GameInfo();
		gameInfo.fromJson(s);
        String action;
        Point lastPos = null;
        boolean attack = false;

        //
//        System.out.println("print");

		try {
			App.updateGui(gameInfo.map);

        }
        catch(Exception e) {
		    System.out.println("ERROR");
            e.printStackTrace();
            System.out.println(e.getCause());
            System.out.println(e.getStackTrace());


        }

        System.out.println("Player Score: " + gameInfo.player.Score);
        System.out.println("Player Health: " + gameInfo.player.Health);

//        Find a target because fuck you tahts why

        Tile targetTile = null;

        try {
            targetTile = findNearestNode(gameInfo.map, gameInfo.player);
        }
        catch(Exception e){
            e.printStackTrace();
        }
//

//		System.out.println("X: " + targetTile.X + "    - Y: " + targetTile.Y);
        int targetX, targetY;
        targetX = gameInfo.player.Position.x;
        targetY = gameInfo.player.Position.y;

        if (gameInfo.player.CarriedResources < gameInfo.player.CarryingCapacity && (targetTile.X == gameInfo.player.Position.x-1 && targetTile.Y == gameInfo.player.Position.y || targetTile.X == gameInfo.player.Position.x+1 && targetTile.Y == gameInfo.player.Position.y || targetTile.X == gameInfo.player.Position.x && targetTile.Y == gameInfo.player.Position.y+1 || targetTile.X == gameInfo.player.Position.x && targetTile.Y == gameInfo.player.Position.y-1)) {
            System.out.println("GATHERING");
            System.out.println("Player Carried Ressources: " + gameInfo.player.CarriedResources);
            System.out.println("Player Carry capacity: " + gameInfo.player.CarryingCapacity);



            action = AiHelper.CreateCollectAction(new Point(targetTile.X, targetTile.Y));
        }

        else if (gameInfo.player.CarriedResources < gameInfo.player.CarryingCapacity) {

            if (targetTile.X > gameInfo.player.Position.x)
                targetX++;
            else if (targetTile.Y > gameInfo.player.Position.y)
                targetY++;
            else if (targetTile.X < gameInfo.player.Position.x)
                targetX--;
            else if (targetTile.Y < gameInfo.player.Position.y)
                targetY--;

            //        if (Math.floor(Math.random()*2) == 1)
//            if (Math.floor(Math.random()*2) == 1)targetX++;
//            else targetX--;
//
//        else
//            if (Math.floor(Math.random()*2) == 1)targetY++;
//            else targetY--;

            gameInfo.player.Position.x = targetX;
            gameInfo.player.Position.y = targetY;



            // AI IMPLEMENTATION HERE.



            action = AiHelper.CreateMoveAction(gameInfo.player.Position);
        }

        else {

            if (lastPos != null) {
                if (lastPos.x == gameInfo.player.Position.x && lastPos.y == gameInfo.player.Position.y) {
                    attack = true;
                }
                else {
                    attack = false;
                }
            }


            if (gameInfo.player.HouseLocation.x > gameInfo.player.Position.x)
                targetX++;
            else if (gameInfo.player.HouseLocation.y > gameInfo.player.Position.y)
                targetY++;
            else if (gameInfo.player.HouseLocation.x < gameInfo.player.Position.x)
                targetX--;
            else if (gameInfo.player.HouseLocation.y < gameInfo.player.Position.y)
                targetY--;


            //        if (Math.floor(Math.random()*2) == 1)
//            if (Math.floor(Math.random()*2) == 1)targetX++;
//            else targetX--;
//
//        else
//            if (Math.floor(Math.random()*2) == 1)targetY++;
//            else targetY--;

            gameInfo.player.Position.x = targetX;
            gameInfo.player.Position.y = targetY;


            // AI IMPLEMENTATION HERE.

            lastPos = new Point(gameInfo.player.Position.x, gameInfo.player.Position.y);

            if (!attack)
                action = AiHelper.CreateMoveAction(gameInfo.player.Position);
            else 
                action = AiHelper.CreateAttackAction(gameInfo.player.Position);

        }



	    return super.resJson(req, res, 200, action);
	}


    private Tile findNearestNode(List<List<Tile>> map, Player player) {

	    ArrayList<Tile> arrayPos = new ArrayList<Tile>();
	    NodePosition nodeClosest = null;

        int distX = -1;
        int distY = -1;

        for (int i = 0; i<map.size(); i++) {
            for (int j = 0; j<map.get(i).size(); j++) {
                if (map.get(i).get(j).Content == 4) {
                    arrayPos.add(map.get(i).get(j));
                }
            }
        }


        for (int i = 0; i<arrayPos.size(); i++) {
            if (nodeClosest == null) {
                nodeClosest = new NodePosition(arrayPos.get(i).X, arrayPos.get(i).Y, Math.abs(player.Position.x - arrayPos.get(i).X), Math.abs(player.Position.y - arrayPos.get(i).Y));
            }

            else if (nodeClosest.distX + nodeClosest.distY > Math.abs(player.Position.x - arrayPos.get(i).X)+ Math.abs(player.Position.y - arrayPos.get(i).Y)) {
                nodeClosest = nodeClosest = new NodePosition(arrayPos.get(i).X, arrayPos.get(i).Y, Math.abs(player.Position.x - arrayPos.get(i).X), Math.abs(player.Position.y - arrayPos.get(i).Y));

            }

        }

        Tile tile = new Tile(nodeClosest.x, nodeClosest.y, 4);
        return tile;
    }
}

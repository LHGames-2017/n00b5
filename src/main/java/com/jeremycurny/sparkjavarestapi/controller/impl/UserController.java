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

class NodePosition{
    int x = -1;
    int y = -1;
}

public class UserController extends RestController {

	@Override
	public Object bot(Request req, Response res) {
		String s = URLDecoder.decode(req.body()).substring(4);
		GameInfo gameInfo = new GameInfo();
		gameInfo.fromJson(s);
        String action;
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



            action = AiHelper.CreateMoveAction(gameInfo.player.Position);
        }



	    return super.resJson(req, res, 200, action);
	}


    private Tile findNearestNode(List<List<Tile>> map, Player player) {
	    NodePosition nearestNode = new NodePosition();
	    Tile tile = null;

        int distX = -1;
        int distY = -1;

        for (int i = 0; i<map.size(); i++) {
            for (int j = 0; j<map.get(i).size(); j++) {
                if (map.get(i).get(j).Content == 4) {
                    tile = map.get(i).get(j);
                    break;
                }
            }
        }

        return tile;
    }
}

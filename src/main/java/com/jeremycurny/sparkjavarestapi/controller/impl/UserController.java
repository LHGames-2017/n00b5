package com.jeremycurny.sparkjavarestapi.controller.impl;

import com.jeremycurny.sparkjavarestapi.app.App;
import com.jeremycurny.sparkjavarestapi.controller.RestController;
import com.jeremycurny.sparkjavarestapi.util.AiHelper;

import java.awt.*;
import java.net.URLDecoder;
import java.util.ArrayList;

import com.jeremycurny.sparkjavarestapi.util.GameInfo;
import spark.Request;
import spark.Response;

public class UserController extends RestController {

	@Override
	public Object bot(Request req, Response res) {
		String s = URLDecoder.decode(req.body()).substring(4);
		GameInfo gameInfo = new GameInfo();
		System.out.println("yolo");
		gameInfo.fromJson(s);


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

        int targetX, targetY;
        targetX = gameInfo.player.Position.x;
        targetY = gameInfo.player.Position.y;

        if (Math.floor(Math.random()*2) == 1)
            if (Math.floor(Math.random()*2) == 1)targetX++;
            else targetX--;

        else
            if (Math.floor(Math.random()*2) == 1)targetY++;
            else targetY--;

        gameInfo.player.Position.x = targetX;
        gameInfo.player.Position.y = targetY;


		// AI IMPLEMENTATION HERE.



		String action = AiHelper.CreateMoveAction(gameInfo.player.Position);
	    return super.resJson(req, res, 200, action);
	}
}

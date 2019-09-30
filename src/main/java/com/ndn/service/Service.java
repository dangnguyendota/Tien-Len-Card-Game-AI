package com.ndn.service;

import com.google.gson.Gson;
import com.ndn.algorithm.BotListener;
import com.ndn.algorithm.MctsPlayerConfiguration;
import com.ndn.algorithm.MonteCarloTreeSearchPlayer;
import com.ndn.base.Game;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;

public class Service {
    private Gson gson = new Gson();

    private void handle(Game game, final MctsPlayerConfiguration configuration, final BotListener listener){
        MonteCarloTreeSearchPlayer.getInstance().start(game, configuration, listener);
    }

    public void run() throws Exception{
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        HttpHandler handler = httpExchange -> {
            try {
                InputStream inputStream = httpExchange.getRequestBody();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                ReceiveData receiveData = gson.fromJson(reader, ReceiveData.class);
                handle(receiveData.game(), receiveData.botConf(), o -> {
                    try {
                        SendData sendData = new SendData(o);
                        String response = gson.toJson(sendData);
                        httpExchange.getResponseHeaders().set("Content-Type", "application/json");
                        httpExchange.sendResponseHeaders(200, response.length());
                        httpExchange.getResponseBody().write(response.getBytes());
                        httpExchange.getResponseBody().close();
                    } catch (Exception e){
                        e.printStackTrace();
                        httpExchange.close();
                    }
                });
            } catch (Exception e){
                httpExchange.close();
            }
        };
        server.createContext("/tienlen", handler);
        server.setExecutor(null);
        server.start();
    }
}

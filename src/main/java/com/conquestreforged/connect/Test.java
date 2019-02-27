package com.conquestreforged.connect;

import com.conquestreforged.connect.http.Requests;
import com.conquestreforged.connect.http.Response;
import com.conquestreforged.connect.server.ServerInfo;

import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception {
        Response<List<ServerInfo>> response = Requests.getJson(ServerInfo.DATA_URL)
                .then(ServerInfo::unmarshalServers)
                .then(ServerInfo::filterServers)
                .send();

        System.out.println("working:");
        while (!response.done()) {
            System.out.print(".");
            Thread.sleep(500);
        }

        System.out.println("\nservers:");
        for (ServerInfo server : response.get()) {
            System.out.println(server);
        }
    }
}

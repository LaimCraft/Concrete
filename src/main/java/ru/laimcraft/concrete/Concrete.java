package ru.laimcraft.concrete;

import ru.laimcraft.concrete.network.MinecraftServer;

public class Concrete {

    MinecraftServer minecraftServer = new MinecraftServer();

    public Concrete() {
        minecraftServer.start();
    }
}

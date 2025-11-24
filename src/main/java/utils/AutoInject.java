package utils;

import network.Network;
import service.Command;
import service.Commands;
import service.netCommands.netCommand;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class AutoInject {


    public void register(Commands commands, Network network) {

        Class<?> clazz = commands.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Command.class)) {
                try {

                    Class<?> type = field.getType();

                    Constructor<?> constructor = type.getConstructor(Network.class);
                    netCommand instance = (netCommand) constructor.newInstance(network);

                    field.setAccessible(true);
                    field.set(commands, instance);

                    commands.getCommands().add(instance);


                } catch (Exception e) {
                    throw new RuntimeException("Failed to inject command " + e.getMessage());
                }

            }
        }
    }
}

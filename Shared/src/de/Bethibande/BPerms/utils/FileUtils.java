package de.Bethibande.BPerms.utils;

import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    public static List<String> read(File f) {
        List<String> content = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String s;
            while((s = reader.readLine()) != null) {
                content.add(s);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void write(File f, String[] content) {
        try {
            PrintWriter w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(f)));
            for(String s : content) {
                w.println(s);
            }
            w.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory() && dir.exists()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(children[i]);
                if (!success) {
                    return false;
                }
            }
        } else if(!dir.exists()) {
            return true;
        }
        return dir.delete();
    }

    public static Object loadJson(File f) {
        String s = "";
        for(String str : read(f)) {
            s = s + str.replaceAll("\t", "");
        }
        try {
            Gson g = new Gson();
            JsonObject jo = g.fromJson(s, JsonObject.class);
            String clazz = jo.get("className").getAsString().substring(6);
            jo.remove("className");
            return g.fromJson(s, Class.forName(clazz));
        } catch(ClassNotFoundException e) {
            System.err.println("Couldn't load json from File: '" + f.getPath() + "'! \n");
        }
        return null;
    }

    public static void saveJson(File f, Object obj) {
        Gson g = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jo = g.toJsonTree(obj).getAsJsonObject();
        jo.addProperty("className", obj.getClass() + "");
        String[] c = { g.toJson(jo) };
        write(f, c);
    }

    public static boolean createFile(File f) {
        try {
            f.createNewFile();
            return true;
        } catch(IOException e) {
            return false;
        }
    }

}

package org.mimja.entityPortalFix;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class MonsterBucket {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public HashMap<UUID, Date> monsters = new HashMap<>();

    File targetFile;
    public MonsterBucket(Path path) {
        targetFile = new File(path.toUri());
    }

    public void save() {
        createStore();

        try (Writer writer = new FileWriter(targetFile)) {
            writer.write(gson.toJson(monsters));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        createStore();

        try (Reader reader = new FileReader(targetFile)) {
            TypeToken<HashMap<UUID, Date>> mapType = new TypeToken<>(){};
            monsters = gson.fromJson(reader,  mapType);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(monsters == null) monsters = new HashMap<>();
    }

    private void createStore() {
        if(!targetFile.exists()) {
            try {
                boolean createdFolder = targetFile.getParentFile().mkdirs();
                if(!createdFolder) EntityPortalFix.log.warning("failed to create folders with file missing");

                boolean createdFile = targetFile.createNewFile();
                if(!createdFile) throw new IOException("Could not create file");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
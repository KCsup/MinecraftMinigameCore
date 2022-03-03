package org.kcsup.gramersgamecore.arena.sign;

import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersgamecore.Main;
import org.kcsup.gramersgamecore.arena.Arena;
import org.kcsup.gramersgamecore.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignManager {

    private Main main;
    private File signData;

    public SignManager(Main main) {
        this.main = main;

        filesCheck();
    }

    /* Sign Data File Structure
    {
        "signs": Object[] **Array to put signs in
    }
     */
    private void filesCheck() {
        String signDataPath = main.getDataFolder() + "/signs.json";
        signData = new File(signDataPath);
        if(!signData.exists()) {
            try {
                signData.createNewFile();

                JSONObject file = new JSONObject();
                file.put("signs", new JSONArray());

                FileWriter fileWriter = new FileWriter(signData);
                fileWriter.write(file.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<ArenaSign> getSigns() {
        if(signData == null) return null;

        try {
            List<ArenaSign> signs = new ArrayList<>();
            
            JSONObject file = Util.getJsonFile(signData);
            JSONArray jSigns = file.getJSONArray("signs");

            for(Object s : jSigns) {
                ArenaSign sign = jsonToSign(new JSONObject(s));
                if(sign != null) signs.add(sign);
            }

            if(!signs.isEmpty()) return signs;
            else return null;
        } catch(IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArenaSign getSign(Arena arena) {
        List<ArenaSign> signs = getSigns();
        if(arena == null || signs == null) return null;

        for(ArenaSign sign : getSigns()) {
            if(sign.getArena().getId() == arena.getId()) return sign;
        }

        return null;
    }

    /* Sign Json Structure
    {
        "location": Object **The location of the sign
        "arenaId": int **The id of the arena for this sign
        "lines": String[] **
    }
     */
    private ArenaSign jsonToSign(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        try {
            Location location = Util.jsonToLocation(jsonObject.getJSONObject("location"));
            int arenaId = jsonObject.getInt("arenaId");
            Arena arena = main.getArenaManager().getArena(arenaId);
            if(arena == null || location == null) return null;

            return new ArenaSign(location, arena, (String[]) jsonObject.get("lines"));
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private JSONObject signToJson(ArenaSign sign) {
        if(sign == null) return null;
        
        try {
            Location location = sign.getLocation();
            Arena arena = sign.getArena();
            if(arena == null || location == null) return null;
            
            JSONObject jsonSign = new JSONObject();
            jsonSign.put("location", Util.locationToJson(location));
            jsonSign.put("arenaId", arena.getId());
            jsonSign.put("lines", sign.getLines());
            
            return jsonSign;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
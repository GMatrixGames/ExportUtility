package com.gmatrixgames.util;

import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Cosmetic {
    static Boolean verbose = false;

    public static void generateNewCosmetics() throws IOException, JSONException {
        if (verbose) System.out.println("No benbot or rarity choice, setting defaults");
        generateNewCosmetics(false);
    }

    public static void generateNewCosmetics(Boolean benbot) throws IOException, JSONException {
        if (verbose) System.out.println("No rarity choice, setting default");
        generateNewCosmetics(benbot, true);
    }

    public static void generateNewCosmetics(Boolean benbot, Boolean shouldGenRarity) throws IOException, JSONException {
        URL url;
        if (benbot) {
            url = new URL("https://benbotfn.tk/api/v1/newCosmetics");
        } else {
            url = new URL("https://fortnite-api.com/v2/cosmetics/br/new");
        }
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 2.0; Windows NT 5.0; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)");

        if (con.getResponseCode() == 200) {
            JSONArray array;
            URL small = null;
            URL icon = null;
            URL featured = null;
            String backendType = null;
            String rarity = null;
            String id = null;
            String source = null;
            if (benbot) {
                array = new JSONObject(con.getInputStream()).getJSONArray("items");
                for (JSONObject obj : (Iterable<JSONObject>) array) {
                    backendType = obj.getString("backendValue");
                    id = obj.getString("id");

                    try {
                        if (backendType.equalsIgnoreCase("AthenaEmoji")) {
                            small = new URL(obj.getJSONObject("icons").getString("icon")); // Benbot doesn't have small icons, they're merged into "icon"
                        }
                    } catch (JSONException e) {
                        small = null; // In case it doesn't exist
                    }

                    try {
                        icon = new URL(obj.getJSONObject("icons").getString("icon")); // Same equiv to small
                    } catch (JSONException e) {
                        icon = null;
                    }

                    try {
                        featured = new URL(obj.getJSONObject("icons").getString("featured"));
                    } catch (JSONException e) {
                        featured = null;
                    }

                    try {
                        rarity = obj.getJSONObject("icons").getString("series").replace("https://benbotfn.tk/cdn/series/", "").replace(".png", "").toLowerCase();
                    } catch (JSONException e) {
                        rarity = obj.getString("rarity").toLowerCase();
                    }

                    try {
                        for (String s : (Iterable<String>) obj.getJSONArray("gameplayTags")) {
                            if (s.contains("Cosmetics.Source.")) {
                                source = s.replace("Cosmetics.Source.", "");
                            }
                        }
                    } catch (JSONException e) {
                        source = "";
                    }
                }
            } else {
                array = new JSONObject(con.getInputStream()).getJSONObject("data").getJSONArray("items");
                for (JSONObject obj : (Iterable<JSONObject>) array) {
                    backendType = obj.getJSONObject("type").getString("backendValue");
                    id = obj.getString("id");

                    try {
                        small = new URL(obj.getJSONObject("images").getString("smallIcon"));
                    } catch (JSONException e) {
                        small = null;
                    }

                    try {
                        icon = new URL(obj.getJSONObject("images").getString("icon"));
                    } catch (JSONException e) {
                        icon = null;
                    }

                    try {
                        featured = new URL(obj.getJSONObject("images").getString("featured"));
                    } catch (JSONException e) {
                        featured = null;
                    }

                    try {
                        rarity = obj.getJSONObject("series").getString("backendValue").toLowerCase();
                    } catch (JSONException e) {
                        rarity = obj.getJSONObject("rarity").getString("backendValue").replace("EFortRarity::", "").toLowerCase();
                    }

                    try {
                        for (String s : (Iterable<String>) obj.getJSONArray("gameplayTags")) {
                            if (s.contains("Cosmetics.Source.")) {
                                source = s.replace("Cosmetics.Source.", "");
                            }
                        }
                    } catch (JSONException e) {
                        source = "";
                    }
                }
            }

//            try {
//                if (small != null) {
//                    Export.downloadImage(small, backendType, id + "_small.png", true);
//                }
//
//                if (icon != null) {
//                    Export.downloadImage(icon, backendType, id + "_icon.png", true);
//                }
//
//                if (featured != null) {
//                    Export.downloadImage(featured, backendType, id + "_featured.png", true);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (shouldGenRarity) {
//                if (featured != null && (id.contains("Glider") || id.contains("Pickaxe"))) {
//                    Export.rarityImages(backendType, id + "_featured.png", rarity, obj.getString("name"), obj.getString("description"), source, obj.getJSONObject("type").getString("value"), true);
//                } else if (icon == null) {
//                    Export.rarityImages(backendType, id + "_smallicon.png", rarity, obj.getString("name"), obj.getString("description"), source, obj.getJSONObject("type").getString("value"), true);
//                } else {
//                    Export.rarityImages(backendType, id + "_icon.png", rarity, obj.getString("name"), obj.getString("description"), source, obj.getJSONObject("type").getString("value"), true);
//                }
//            }
        }
    }
}
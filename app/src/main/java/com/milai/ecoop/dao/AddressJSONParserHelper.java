package com.milai.ecoop.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.milai.ecoop.bean.zonebean.City;

public class AddressJSONParserHelper {
	public ArrayList<String> province_list_code = new ArrayList<String>();
	public ArrayList<String> city_list_code = new ArrayList<String>();

	public List<City> getJSONParserResult(String JSONString, String key) {
		List<City> list = new ArrayList<City>();
		JsonObject result = new JsonParser().parse(JSONString)
				.getAsJsonObject().getAsJsonObject(key);

		Iterator iterator = result.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
					.next();
			City City = new City();

			City.setCity_name(entry.getValue().getAsString());
			City.setId(entry.getKey());
			province_list_code.add(entry.getKey());
			list.add(City);
		}
		System.out.println(province_list_code.size());
		return list;
	}

	public HashMap<String, List<City>> getJSONParserResultArray(
			String JSONString, String key) {
		HashMap<String, List<City>> hashMap = new HashMap<String, List<City>>();
		JsonObject result = new JsonParser().parse(JSONString)
				.getAsJsonObject().getAsJsonObject(key);

		Iterator iterator = result.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonElement> entry = (Entry<String, JsonElement>) iterator
					.next();
			List<City> list = new ArrayList<City>();
			JsonArray array = entry.getValue().getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				City City = new City();
				City.setCity_name(array.get(i).getAsJsonArray().get(0)
						.getAsString());
				City.setId(array.get(i).getAsJsonArray().get(1)
						.getAsString());
				city_list_code.add(array.get(i).getAsJsonArray().get(1)
						.getAsString());
				list.add(City);
			}
			hashMap.put(entry.getKey(), list);
		}
		return hashMap;
	}
}

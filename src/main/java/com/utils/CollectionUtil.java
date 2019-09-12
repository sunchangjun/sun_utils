package com.utils;



import java.util.*;

public class CollectionUtil {

    public static <T> List<T>  listRemoveDuplicates (List<T> list){
        Set<T> set= new HashSet<T>(list);
        return new ArrayList<T>(set);
    }


    public static <T> List<T> getRandomList(List<T> paramList, int count) {
        if (paramList.size() < count) {
            return paramList;
        }
        Random random = new Random();
        List<Integer> tempList = new ArrayList<Integer>();
        List<T> newList = new ArrayList<T>();
        int temp = 0;
        for (int i = 0; i < count; i++) {
            temp = random.nextInt(paramList.size());// 将产生的随机数作为被抽list的索引
            if (!tempList.contains(temp)) {
                tempList.add(temp);
                newList.add(paramList.get(temp));
            } else {
                i--;
            }
        }
        return newList;
    }


    public static  Map mapSortByValueDesc(Map<String, List<String>>  map){
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<String>> sortedMap = new LinkedHashMap<String, List<String>>();
        List<Map.Entry<String,List<String>>> entryList = new ArrayList<Map.Entry<String, List<String>>>(map.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, List<String>>>() {
            @Override
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                int o1Size=o1.getValue().size();
                int o2Size=o2.getValue().size();
                return   o2Size-o1Size;
            }
        });
        for (Map.Entry<String, List<String>> linkMap :entryList) {
            sortedMap.put(linkMap.getKey(),linkMap.getValue());
        }
        return sortedMap;
    }

    public static  Map mapSortByLongValueDesc(Map<String, Long>  map){
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
        List<Map.Entry<String, Long>> entryList = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
        Collections.sort(entryList, new Comparator<Map.Entry<String, Long>>() {
            @Override
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                Long o1Size=o1.getValue();
                Long o2Size=o2.getValue();
                return    o2Size.intValue()-  o1Size.intValue();
            }
        });
        for (Map.Entry<String, Long> linkMap :entryList) {
            sortedMap.put(linkMap.getKey(),linkMap.getValue());
        }
        return sortedMap;
    }

}

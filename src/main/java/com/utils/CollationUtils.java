package com.utils;

import java.util.*;

public class CollationUtils {
    /*利用Set对List去重(T泛型)*/
    public static <T> List<T> listRemoveDuplicates (List<T> list){
        Set<T> set= new HashSet<T>(list);
        return new ArrayList<T>(set);
    }

    /*从list中随机获取一部分元素(泛型)*/
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

}

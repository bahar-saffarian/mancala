package com.bahar.mancala.util;

import java.util.List;

public class ListUtil {
    public static  <T> T getNextOfLoopList(List<T> list, int nextIndex) {
        return list.get((nextIndex% list.size()));
    }
}

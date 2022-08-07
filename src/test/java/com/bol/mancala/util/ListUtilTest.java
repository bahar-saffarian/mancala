package com.bol.mancala.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ListUtilTest {

    @Test
    void getNextElementOfLoopListTest() {
        //Given
        List<String> list = List.of("A","B","C");
        int currentIndex = 0;

        //When
        String secondOfLoopList = ListUtil.getNextOfLoopList(list, currentIndex + 1);
        String thirdOfLoopList = ListUtil.getNextOfLoopList(list, currentIndex + 2);

        //Then
        assertThat(secondOfLoopList).isEqualTo("B");
        assertThat(thirdOfLoopList).isEqualTo("C");
    }

    @Test
    void getNextElementOfTheEndOfLoopListTest() {
        //Given
        List<String> list = List.of("A","B","C");
        int lastIndexOfList = list.size()-1;

        //When
        String firstOfLoopList = ListUtil.getNextOfLoopList(list, lastIndexOfList + 1);

        //Then
        assertThat(firstOfLoopList).isEqualTo("A");
    }
}

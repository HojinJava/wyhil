package com.hnix.sd.core.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 간단한 정렬 유틸리티 클래스
 * QueryDSL을 사용하지 않는 환경에서 정렬 정보를 처리합니다.
 */
public class QueryDslSortUtil {

    /**
     * 정렬 정보를 처리하는 메서드 (더미 구현)
     * 실제 정렬은 MyBatis XML이나 서비스 레이어에서 처리합니다.
     */
    public static List<String> sorting(List<SortOrder> orders) {
        List<String> sortStrings = new ArrayList<>();
        for (SortOrder order : orders) {
            String sortStr = order.getProperty() + (order.isDescending() ? " DESC" : " ASC");
            sortStrings.add(sortStr);
        }
        return sortStrings;
    }

    public static class SortOrder {
        private final String property;
        private final boolean descending;

        public SortOrder(String property, boolean descending) {
            this.property = property;
            this.descending = descending;
        }

        public String getProperty() {
            return property;
        }

        public boolean isDescending() {
            return descending;
        }
    }

}

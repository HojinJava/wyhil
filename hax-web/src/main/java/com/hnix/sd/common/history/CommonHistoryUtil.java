package com.hnix.sd.common.history;

import java.time.LocalDateTime;

public class CommonHistoryUtil {

  public static final String COMMON_HISTORY_TYPE_CREATE = "COMMON_HISTORY_TYPE_CREATE";
  public static final String COMMON_HISTORY_TYPE_UPDATE = "COMMON_HISTORY_TYPE_UPDATE";
  public static final String COMMON_HISTORY_TYPE_DELETE = "COMMON_HISTORY_TYPE_DELETE";
  public static final String COMMON_HISTORY_TYPE_CERT = "COMMON_HISTORY_TYPE_CERT";
  public static final String COMMON_HISTORY_TYPE_CONSENT = "COMMON_HISTORY_TYPE_CONSENT";
  public static final String COMMON_HISTORY_TYPE_RECREATE = "COMMON_HISTORY_TYPE_RECREATE";
  public static final String COMMON_HISTORY_TYPE_CONSENT_EX = "COMMON_HISTORY_TYPE_CONSENT_EX";
  public static final String COMMON_HISTORY_TYPE_CONSENT_RE = "COMMON_HISTORY_TYPE_CONSENT_RE";

  public static String getCommonHistory(String msg, String title, String oldValue, String newValue) {
    if (!msg.isEmpty()) msg += ", ";
    if(oldValue == null || oldValue.isEmpty()) {
      msg += title + " : " + newValue + " 로 변경";
    } else if (newValue == null || newValue.isEmpty()) {
      msg += title + " : " + oldValue + " > '''' 변경";
    } else {
      msg += title + " : " + oldValue + " > " + newValue + " 변경";
    }
    return msg;
  }

  public static boolean isOtherValue(String oldValue, String newValue) {
    boolean returnVal = false;

    if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
      returnVal = false;
    } else {
      if (oldValue == null && newValue == null) {
        returnVal = false;
      } else {
        returnVal = true;
      }
    }

    return returnVal;
  }

  public static String getCommonHistory(String msg, String title, LocalDateTime oldValue, LocalDateTime newValue) {
    if (!msg.isEmpty()) msg += ", ";
    msg += title + " : " + oldValue + " > " + newValue + " 변경";
    return msg;
  }

  public static boolean isOtherValue(LocalDateTime oldValue, LocalDateTime newValue) {
    boolean returnVal = false;

    if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
      returnVal = false;
    } else {
      if (oldValue == null && newValue == null) {
        returnVal = false;
      } else {
        returnVal = true;
      }
    }

    return returnVal;
  }

  public static boolean isOtherValue(Character oldValue, Character newValue) {
    boolean returnVal = false;

    if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
      returnVal = false;
    } else {
      if (oldValue == null && newValue == null) {
        returnVal = false;
      } else {
        returnVal = true;
      }
    }

    return returnVal;
  }
}
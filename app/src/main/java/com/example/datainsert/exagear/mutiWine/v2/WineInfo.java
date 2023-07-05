package com.example.datainsert.exagear.mutiWine.v2;



public interface WineInfo {
     /**
      * tag名，用于显示在视图上的该wine的名字，应该唯一
      */
      String getTagName();

    /**
     * 获取更多信息 用于显示在“可下载”页面下的subtext中
     */
     String getDescription();

}

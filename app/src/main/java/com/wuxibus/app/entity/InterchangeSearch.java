package com.wuxibus.app.entity;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/10/13.
 */
public class InterchangeSearch {
    /**
     * 起始地址
     */
    public static PoiInfo sourceInfo=null;
    /**
     * 目的地址
     */
    public static PoiInfo destinationInfo=null;

    /**
     * 家，公司信息列表（2个元素）
     */

    public static PoiInfo homePoiInfo;

    public static PoiInfo companyInfo;

    public static boolean isAroundStop;//是否是从附近站台点击过来

   // public static List<PoiInfo> homeCompanyInfoList = null;
}

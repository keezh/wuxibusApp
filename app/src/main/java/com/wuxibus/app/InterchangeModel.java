package com.wuxibus.app;

import com.wuxibus.app.entity.InterchangeScheme;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhongkee on 15/11/1.
 */
public class InterchangeModel {

    private static InterchangeModel instance;

    public List<InterchangeScheme> schemeList;
    public List<InterchangeScheme> sortList;

    private InterchangeModel(){

    }

    public static InterchangeModel getInstance(){
        if(instance == null){
            instance = new InterchangeModel();
        }
        return instance;
    }
}

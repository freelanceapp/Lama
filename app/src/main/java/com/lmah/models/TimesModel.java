package com.lmah.models;

import java.io.Serializable;
import java.util.List;

public class TimesModel implements Serializable {
    private List<String> dates;

    public List<String> getDates() {
        return dates;
    }
}

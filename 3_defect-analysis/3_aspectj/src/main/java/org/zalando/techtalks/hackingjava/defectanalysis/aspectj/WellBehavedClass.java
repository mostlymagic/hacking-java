package org.zalando.techtalks.hackingjava.defectanalysis.aspectj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.WellBehaved;

/**
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since   21.01.2016
 */
public class WellBehavedClass implements WellBehaved {
    private Map<String, String> map = new HashMap<>();
    private List<String> list = new ArrayList<>();

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(final Map<String, String> map) {
        this.map = map;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(final List<String> list) {
        this.list = list;
    }
}

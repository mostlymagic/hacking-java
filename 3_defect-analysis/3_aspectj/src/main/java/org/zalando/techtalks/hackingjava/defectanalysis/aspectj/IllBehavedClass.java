package org.zalando.techtalks.hackingjava.defectanalysis.aspectj;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.zalando.techtalks.hackingjava.defectanalysis.baseline.marker.IllBehaved;

/**
 * @author Sean Patrick Floyd (sean.floyd@zalando.de)
 * @since 21.01.2016
 */
public class IllBehavedClass implements IllBehaved {
    private Map<String, String> map = new Hashtable<>();
    private List<String> list = new Vector<>();

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

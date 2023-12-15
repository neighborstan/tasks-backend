package tech.saas.tasks.core.models;

import java.util.HashMap;
import java.util.Map;

public class PolymorphMap<K,V> extends HashMap<K,V> implements TaskEntity, TaskPayload {
    public PolymorphMap(Map<K,V> map){
        super(map);
    }
}

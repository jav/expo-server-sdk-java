package io.github.jav.exposerversdk;

import java.util.List;
import java.util.Map;

public class DefaultExpoPushMessage extends ExpoPushMessage<Map<String,String>> {

    // super ugly https://stackoverflow.com/questions/22152982/passing-parameterized-class-instance-to-the-constructor
    private static Class<Map<String,String>> _mapClazz = (Class<Map<String,String>>)(Class<?>)Map.class;
    
    @SuppressWarnings("unchecked")
    public DefaultExpoPushMessage() {
        super(_mapClazz);
    }
    

    public DefaultExpoPushMessage(List<String> _to, DefaultExpoPushMessage _message) {
        super (_to, _message);
    }

    public DefaultExpoPushMessage(List<String> _to) {
        super (_to, _mapClazz);
    }

    public DefaultExpoPushMessage(String _to) {    
        super (_to, _mapClazz);
    }
    
    @Override
    public DefaultExpoPushMessage toChunk(List<String> partialTo) {
        return new DefaultExpoPushMessage(partialTo, this);
    }

}

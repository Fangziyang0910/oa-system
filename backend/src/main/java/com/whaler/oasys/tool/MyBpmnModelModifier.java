package com.whaler.oasys.tool;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.GraphicInfo;
import org.springframework.stereotype.Component;

@Component
public class MyBpmnModelModifier {
    public void setBpmnModel(BpmnModel bpmnModel){
        Double scale = 2.0d;
        Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
        for (String key : locationMap.keySet()) {
            locationMap.get(key).setX(locationMap.get(key).getX()*scale);
            locationMap.get(key).setY(locationMap.get(key).getY()*scale);
            locationMap.get(key).setWidth(locationMap.get(key).getWidth()*scale);
            locationMap.get(key).setHeight(locationMap.get(key).getHeight()*scale);   
        }
        Map<String, GraphicInfo> labelLocationMap = bpmnModel.getLabelLocationMap();
        for (String key : labelLocationMap.keySet()) {
            locationMap.get(key).setX(locationMap.get(key).getX()*scale);
            locationMap.get(key).setY(locationMap.get(key).getY()*scale);
            locationMap.get(key).setWidth(locationMap.get(key).getWidth()*scale);
            locationMap.get(key).setHeight(locationMap.get(key).getHeight()*scale);   
        }
        Map<String, List<GraphicInfo>>flowLocationMap = bpmnModel.getFlowLocationMap();
        for (String key : flowLocationMap.keySet()) {
            List<GraphicInfo> graphicInfos = flowLocationMap.get(key);
            for (GraphicInfo graphicInfo : graphicInfos) {
                graphicInfo.setX(graphicInfo.getX()*scale);
                graphicInfo.setY(graphicInfo.getY()*scale);
                graphicInfo.setWidth(graphicInfo.getWidth()*scale);
                graphicInfo.setHeight(graphicInfo.getHeight()*scale);
            }
        }
    }
}

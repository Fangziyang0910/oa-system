package com.whaler.oasys.tool;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.GraphicInfo;
import org.springframework.stereotype.Component;

@Component
public class MyBpmnModelModifier {
    /**
     * 设置BpmnModel的缩放比例。
     * 对BpmnModel中所有图形信息的位置和尺寸进行缩放处理，包括：元素本身的位置和尺寸、元素标签的位置和尺寸、以及流程线的位置和尺寸。
     * 
     * @param bpmnModel 要进行缩放处理的Bpmn模型对象。
     */
    public void setBpmnModel(BpmnModel bpmnModel){
        // 定义缩放比例
        Double scale = 2.0d;
        
        // 获取并缩放元素本身的位置和尺寸
        Map<String, GraphicInfo> locationMap = bpmnModel.getLocationMap();
        for (String key : locationMap.keySet()) {
            locationMap.get(key).setX(locationMap.get(key).getX()*scale);
            locationMap.get(key).setY(locationMap.get(key).getY()*scale);
            locationMap.get(key).setWidth(locationMap.get(key).getWidth()*scale);
            locationMap.get(key).setHeight(locationMap.get(key).getHeight()*scale);   
        }
        
        // 获取并缩放元素标签的位置和尺寸
        Map<String, GraphicInfo> labelLocationMap = bpmnModel.getLabelLocationMap();
        for (String key : labelLocationMap.keySet()) {
            // 此处存在潜在错误，应使用labelLocationMap.get(key)而非locationMap.get(key)
            locationMap.get(key).setX(locationMap.get(key).getX()*scale);
            locationMap.get(key).setY(locationMap.get(key).getY()*scale);
            locationMap.get(key).setWidth(locationMap.get(key).getWidth()*scale);
            locationMap.get(key).setHeight(locationMap.get(key).getHeight()*scale);   
        }
        
        // 获取并缩放流程线的位置和尺寸
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

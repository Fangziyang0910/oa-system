package com.whaler.oasys.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MsgVo {
    private String msgId;
    private String sendTime;
    private String msgName;
    private String msgContent;
}

package com.hiwoo.entity.enums;

public enum QoSEnum {
    AT_MOST_ONCE  (0),
    AT_LEAST_ONCE (1),
    EXACTLY_ONCE  (2);

    final public int val;

    QoSEnum(int val) {
        this.val = val;
    }

    public static QoSEnum valueOf(int i) {
        for(QoSEnum q: QoSEnum.values()) {
            if (q.val == i)
                return q;
        }
        throw new IllegalArgumentException("Not a valid QoS number: " + i);
    }
}

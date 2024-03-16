package com.example.datainsert.exagear.controlsV2;

public interface XZoomHandler {
    public void start(float x1, float y1, float x2, float y2);

    public void update(float x1, float y1, float x2, float y2);

    public void stop();
    static XZoomHandler EMPTY = new XZoomHandler() {
        @Override
        public void start(float x1, float y1, float x2, float y2) {

        }

        @Override
        public void update(float x1, float y1, float x2, float y2) {

        }

        @Override
        public void stop() {

        }
    };
}



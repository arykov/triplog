package com.ryaltech.orm;

import org.springframework.beans.BeanWrapperImpl;

public class IntrospectionBlockingExperiment {

    public static class DataObject {

        private String prop0;
        private String prop1;
        private String prop2;
        private String prop3;
        private String prop4;
        private String prop5;
        private String prop6;
        private String prop7;
        private String prop8;
        private String prop9;
        private String prop10;

        public String getProp0() {
            return prop0;
        }
        public String getProp1() {
            return prop1;
        }
        public String getProp10() {
            return prop10;
        }
        public String getProp2() {
            return prop2;
        }
        public String getProp3() {
            return prop3;
        }
        public String getProp4() {
            return prop4;
        }
        public String getProp5() {
            return prop5;
        }
        public String getProp6() {
            return prop6;
        }
        public String getProp7() {
            return prop7;
        }
        public String getProp8() {
            return prop8;
        }
        public String getProp9() {
            return prop9;
        }
        public void setProp0(String prop0) {
            this.prop0 = prop0;
        }
        public void setProp1(String prop1) {
            this.prop1 = prop1;
        }
        public void setProp10(String prop10) {
            this.prop10 = prop10;
        }
        public void setProp2(String prop2) {
            this.prop2 = prop2;
        }
        public void setProp3(String prop3) {
            this.prop3 = prop3;
        }
        public void setProp4(String prop4) {
            this.prop4 = prop4;
        }
        public void setProp5(String prop5) {
            this.prop5 = prop5;
        }
        public void setProp6(String prop6) {
            this.prop6 = prop6;
        }
        public void setProp7(String prop7) {
            this.prop7 = prop7;
        }
        public void setProp8(String prop8) {
            this.prop8 = prop8;
        }
        public void setProp9(String prop9) {
            this.prop9 = prop9;
        }
    }


    private static class Populator implements Runnable {

        private String name;
        private int instances;

        public Populator(String name, int instances) {
            this.name = name;
            this.instances = instances;
        }

        @Override
        public void run() {

            long start = System.currentTimeMillis();

            for (int i = 0; i < instances; i++) {
                DataObject dataObject = new DataObject();
                BeanWrapperImpl wrapper = new BeanWrapperImpl(dataObject);
                for (int j = 0; j < 10; j++) {
                    wrapper.setPropertyValue("prop" + j, "foobar");
                }
            }

            long elapsed = System.currentTimeMillis() - start;

            System.out.println(
                    String.format("Test '%s' did %d populations in %d ms (%d microseconds per)",
                            name,
                            instances,
                            elapsed,
                            elapsed * 1000 / instances));
        }


    }

    public static void main(String[] args) {

        new Populator("warmup", 1).run();
        new Populator("single thread", 100000).run();

        new Thread(new Populator("thread 1", 100000)).start();
        new Thread(new Populator("thread 2", 100000)).start();
//        new Thread(new Populator("thread 3", 100000)).start();
//        new Thread(new Populator("thread 4", 100000)).start();
//        new Thread(new Populator("thread 5", 100000)).start();
    }
}

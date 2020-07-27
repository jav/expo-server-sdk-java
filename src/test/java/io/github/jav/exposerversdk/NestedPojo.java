package io.github.jav.exposerversdk;

import java.util.List;

public class NestedPojo {
    int id;
    InnerPojo i;
    public NestedPojo() {
        
    }
    public NestedPojo(int id, int a, String b, List<String> c) {
        this.id = id;
        this.i = new InnerPojo(a,b,c);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public InnerPojo getI() {
        return i;
    }

    public void setI(InnerPojo i) {
        this.i = i;
    }
    


    public static class InnerPojo {
        private int a;
        private String b;
        private List<String> c;
        public InnerPojo() {
            
        }
        public InnerPojo(int a, String b, List<String> c) {
            this.a = a;
            this.b = b;
            this.c = c;
            
        }
        
        public int getA() {
            return a;
        }
        public void setA(int a) {
            this.a = a;
        }
        public String getB() {
            return b;
        }
        public void setB(String b) {
            this.b = b;
        }
        public List<String> getC() {
            return c;
        }
        public void setC(List<String> c) {
            this.c = c;
        }
        
    }
}

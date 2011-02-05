package IC.lir;

import java.util.LinkedHashMap;
import java.util.Map;

import IC.AST.Field;
import IC.AST.Method;
public class ClassLayout {
        private String name; //class name
        private int methodOffset;
        private int fieldOffset;
        
        private Map<String,Integer> methodToOffset;
        private Map<String,Integer> fieldToOffset;
       
        public ClassLayout(String name, ClassLayout parent) { 
            super();
            this.methodToOffset = new LinkedHashMap<String,Integer>(parent.getMethodToOffset());
            this.fieldToOffset = new LinkedHashMap<String, Integer>(parent.getFieldToOffset());
            methodOffset = parent.getMethodOffset();
            fieldOffset = parent.getFieldOffset();
            this.name = name;
        }
        
        public ClassLayout(String name){
            super();
            this.methodToOffset = new LinkedHashMap<String,Integer>();
            this.fieldToOffset = new LinkedHashMap<String, Integer>();
            methodOffset = 0;
            fieldOffset = 1;
            this.name = name;
        }        
        
        public int getFieldOffset(String field) { 
            if (!fieldToOffset.containsKey(field)) { 
                return -1;
            }
            return fieldToOffset.get(field);
        }
        
        public int getMethodOffset(String method) {
            String methodSuffix = method.substring(method.indexOf("_", 1));
            for (String keyMethod : methodToOffset.keySet()) { 
                String keySuffix = keyMethod.substring(method.indexOf("_", 1));
                if (keySuffix.compareTo(methodSuffix) == 0) { 
                    return methodToOffset.get(keyMethod);
                }
            }
            return -1;
        }

        public void addMethodOffset(String method) {
            String methodName = method.substring(method.indexOf("_",1)+1); //without CLASS_
            String keyName;
            for (String key : methodToOffset.keySet()) { 
                keyName = key.substring(method.indexOf("_",1)+1);
                if (methodName.compareTo(keyName) == 0) { 
                    int offset = methodToOffset.get(key);
                    methodToOffset.remove(key);
                    methodToOffset.put(method, offset);
                    return;
                }
            }
            
            methodToOffset.put(method,methodOffset++);
            
        }
        
        public void addFieldOffset(String field) { 
            fieldToOffset.put(field,fieldOffset++);
        }

        public int getMethodOffset() {
            return methodOffset;
        }

        public void setMethodOffset(int methodOffset) {
            this.methodOffset = methodOffset;
        }

        public int getFieldOffset() {
            return fieldOffset;
        }

        public void setFieldOffset(int fieldOffset) {
            this.fieldOffset = fieldOffset;
        }

        public Map<String, Integer> getMethodToOffset() {
            return methodToOffset;
        }

        public void setMethodToOffset(Map<String, Integer> methodToOffset) {
            this.methodToOffset = methodToOffset;
        }

        public Map<String, Integer> getFieldToOffset() {
            return fieldToOffset;
        }

        public void setFieldToOffset(Map<String, Integer> fieldToOffset) {
            this.fieldToOffset = fieldToOffset;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
        public int getAllocationSize() { 
            return (fieldToOffset.size() + 1) * 4;
        }
        
        public String toStringFieldOffsets() { 
            StringBuffer result = new StringBuffer("#" + "Class " +  this.name + ":\n");
            result.append("#" + "Field offsets:\n");
            for (String field : fieldToOffset.keySet()) { 
                result.append("#" + "\t" + field + " - " + fieldToOffset.get(field) + "\n");
            }            
            return result.toString();
        }
        public String toString() { 
            StringBuffer result = new StringBuffer(this.name + ":\n");
            result.append("Fields:\n");
            for (String field : fieldToOffset.keySet()) { 
                result.append("\t" + field + " - " + fieldToOffset.get(field) + "\n");
            }
            result.append("\n");
            result.append("Methods:\n");
            for (String method : methodToOffset.keySet()) { 
                result.append("\t" + method + " - " + methodToOffset.get(method)+ "\n");
            }
            
            return result.toString();
            
        }

}

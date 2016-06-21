package netty.db.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 使用json-lib构造和解析Json数据
 * 
 * @author Alexia
 * @date 2013/5/23
 *
 */
public class JsonTest {

    /**
     * 构造Json数据
     * 
     * @return
     */
    public static String BuildJson() {

        // JSON格式数据解析对象
        JSONObject jo = new JSONObject();

        // 下面构造两个map、一个list和一个Employee对象
        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("name", "Alexia");
        map1.put("sex", "female");
        map1.put("age", "23");

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put("name", "Edward");
        map2.put("sex", "male");
        map2.put("age", "24");

        List<Map> list = new ArrayList<Map>();
        list.add(map1);
        list.add(map2);

        Employee employee = new Employee();
        employee.setName("wjl");
        employee.setSex("female");
        employee.setAge(24);

        // 将Map转换为JSONArray数据
        JSONArray ja1 = JSONArray.fromObject(map1);
        JSONObject ob = JSONObject.fromObject(map1);
        // 将List转换为JSONArray数据
        JSONArray ja2 = JSONArray.fromObject(list);
        // 将Bean转换为JSONArray数据
        JSONArray ja3 = JSONArray.fromObject(employee);

        System.out.println("JSONArray对象数据格式：");
        System.out.println(ja1.toString());
        System.out.println(ob.toString());
        System.out.println(ja2.toString());
        System.out.println(ja3.toString());

        // 构造Json数据，包括一个map和一个Employee对象
        jo.put("map", ja1);
        jo.put("employee", ja2);
        System.out.println("\n最终构造的JSON数据格式：");
        System.out.println(jo.toString());

        return jo.toString();

    }

    /**
     * 解析Json数据
     * 
     * @param jsonString Json数据字符串
     */
    public static JSONObject ParseJson(String jsonString) {

        // 以employee为例解析，map类似
        JSONObject jb = JSONObject.fromObject(jsonString);
        JSONArray ja = jb.getJSONArray("employee");

        List<Employee> empList = new ArrayList<Employee>();

        // 循环添加Employee对象（可能有多个）
        for (int i = 0; i < ja.size(); i++) {
            Employee employee = new Employee();

            employee.setName(ja.getJSONObject(i).getString("name"));
            employee.setSex(ja.getJSONObject(i).getString("sex"));
            employee.setAge(ja.getJSONObject(i).getInt("age"));

            empList.add(employee);
        }

        System.out.println("\n将Json数据转换为Employee对象：");
        for (int i = 0; i < empList.size(); i++) {
            Employee emp = empList.get(i);
            System.out.println("name: " + emp.getName() + " sex: "
                    + emp.getSex() + " age: " + emp.getAge());
        }
        return jb;
    }
    
    
    /**
     * 将json键值对分别解析到数组中
     * 
     * @param jsonject    需要解析的json对象
     * @param type        决定返回值的内容：键或值
     * @return type="key"：返回json对象中"键"的字符串， type="key""value":返回json对象中"值"的字符串
     */
   private static String[] analyzeJsonToArray(JSONObject jsonject, String type) {

	   String string = jsonject.toString();
	   string = string.replace("}", "");
	   string = string.replace("{", "");
	   string = string.replace("\"", "");
	   String[] strings = string.split(",");

	   if (type.equals("key")) {
		   String[] stringsNum = new String[strings.length];
		   for (int i = 0; i < strings.length; i++) {
			   stringsNum[i] = strings[i].split(":")[0];
			   }
		   return stringsNum;
	   } else if (type.equals("value")) {
		   String[] stringsName = new String[strings.length];
		   for (int i = 0; i < strings.length; i++) {
			   stringsName[i] = strings[i].split(":")[1];
		   }
		   return stringsName;
	   } else 
		   return null;
	}


   

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    	String str = BuildJson();
        JSONObject ob = ParseJson(str);
        String[] str1 =analyzeJsonToArray(ob, "key");
    }

}
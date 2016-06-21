package netty.db.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * ʹ��json-lib����ͽ���Json����
 * 
 * @author Alexia
 * @date 2013/5/23
 *
 */
public class JsonTest {

    /**
     * ����Json����
     * 
     * @return
     */
    public static String BuildJson() {

        // JSON��ʽ���ݽ�������
        JSONObject jo = new JSONObject();

        // ���湹������map��һ��list��һ��Employee����
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

        // ��Mapת��ΪJSONArray����
        JSONArray ja1 = JSONArray.fromObject(map1);
        JSONObject ob = JSONObject.fromObject(map1);
        // ��Listת��ΪJSONArray����
        JSONArray ja2 = JSONArray.fromObject(list);
        // ��Beanת��ΪJSONArray����
        JSONArray ja3 = JSONArray.fromObject(employee);

        System.out.println("JSONArray�������ݸ�ʽ��");
        System.out.println(ja1.toString());
        System.out.println(ob.toString());
        System.out.println(ja2.toString());
        System.out.println(ja3.toString());

        // ����Json���ݣ�����һ��map��һ��Employee����
        jo.put("map", ja1);
        jo.put("employee", ja2);
        System.out.println("\n���չ����JSON���ݸ�ʽ��");
        System.out.println(jo.toString());

        return jo.toString();

    }

    /**
     * ����Json����
     * 
     * @param jsonString Json�����ַ���
     */
    public static JSONObject ParseJson(String jsonString) {

        // ��employeeΪ��������map����
        JSONObject jb = JSONObject.fromObject(jsonString);
        JSONArray ja = jb.getJSONArray("employee");

        List<Employee> empList = new ArrayList<Employee>();

        // ѭ�����Employee���󣨿����ж����
        for (int i = 0; i < ja.size(); i++) {
            Employee employee = new Employee();

            employee.setName(ja.getJSONObject(i).getString("name"));
            employee.setSex(ja.getJSONObject(i).getString("sex"));
            employee.setAge(ja.getJSONObject(i).getInt("age"));

            empList.add(employee);
        }

        System.out.println("\n��Json����ת��ΪEmployee����");
        for (int i = 0; i < empList.size(); i++) {
            Employee emp = empList.get(i);
            System.out.println("name: " + emp.getName() + " sex: "
                    + emp.getSex() + " age: " + emp.getAge());
        }
        return jb;
    }
    
    
    /**
     * ��json��ֵ�Էֱ������������
     * 
     * @param jsonject    ��Ҫ������json����
     * @param type        ��������ֵ�����ݣ�����ֵ
     * @return type="key"������json������"��"���ַ����� type="key""value":����json������"ֵ"���ַ���
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